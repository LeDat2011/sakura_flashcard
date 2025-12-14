import { Request, Response } from 'express';
import User from '../models/User';
import {
    generateAccessToken,
    generateRefreshToken,
    verifyRefreshToken,
    getRefreshTokenExpiry,
} from '../utils/jwt';
import { successResponse, errorResponse } from '../utils/response';
import { AuthRequest } from '../middlewares/auth.middleware';

export const register = async (req: Request, res: Response) => {
    try {
        const { email, username, password, displayName } = req.body;

        // Check existing user
        const existingUser = await User.findOne({ $or: [{ email }, { username }] });
        if (existingUser) {
            return errorResponse(res, 'Email or username already exists', 400);
        }

        // Create user
        const user = await User.create({
            email,
            username,
            password,
            profile: { displayName: displayName || username },
        });

        // Generate tokens
        const payload = { userId: user._id.toString(), email: user.email, role: user.role };
        const accessToken = generateAccessToken(payload);
        const refreshToken = generateRefreshToken(payload);

        // Save refresh token
        user.auth.refreshTokens.push({
            token: refreshToken,
            expiresAt: getRefreshTokenExpiry(),
        });
        await user.save();

        return successResponse(
            res,
            {
                user: {
                    id: user._id,
                    email: user.email,
                    username: user.username,
                    profile: user.profile,
                },
                accessToken,
                refreshToken,
            },
            'Registration successful',
            201
        );
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const login = async (req: Request, res: Response) => {
    try {
        const { email, password } = req.body;

        // Find user
        const user = await User.findOne({ email });
        if (!user) {
            return errorResponse(res, 'Invalid credentials', 401);
        }

        // Check password
        const isMatch = await user.comparePassword(password);
        if (!isMatch) {
            return errorResponse(res, 'Invalid credentials', 401);
        }

        // Check if active
        if (!user.isActive) {
            return errorResponse(res, 'Account is deactivated', 403);
        }

        // Generate tokens
        const payload = { userId: user._id.toString(), email: user.email, role: user.role };
        const accessToken = generateAccessToken(payload);
        const refreshToken = generateRefreshToken(payload);

        // Save refresh token
        user.auth.refreshTokens.push({
            token: refreshToken,
            expiresAt: getRefreshTokenExpiry(),
        });
        await user.save();

        return successResponse(res, {
            user: {
                id: user._id,
                email: user.email,
                username: user.username,
                profile: user.profile,
                stats: user.stats,
            },
            accessToken,
            refreshToken,
        }, 'Login successful');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const refreshToken = async (req: Request, res: Response) => {
    try {
        const { refreshToken: token } = req.body;

        if (!token) {
            return errorResponse(res, 'Refresh token required', 400);
        }

        // Verify token
        const decoded = verifyRefreshToken(token);

        // Find user and check token
        const user = await User.findById(decoded.userId);
        if (!user) {
            return errorResponse(res, 'User not found', 404);
        }

        const tokenExists = user.auth.refreshTokens.find((t) => t.token === token);
        if (!tokenExists) {
            return errorResponse(res, 'Invalid refresh token', 401);
        }

        // Remove old token
        user.auth.refreshTokens = user.auth.refreshTokens.filter((t) => t.token !== token);

        // Generate new tokens
        const payload = { userId: user._id.toString(), email: user.email, role: user.role };
        const accessToken = generateAccessToken(payload);
        const newRefreshToken = generateRefreshToken(payload);

        // Save new refresh token
        user.auth.refreshTokens.push({
            token: newRefreshToken,
            expiresAt: getRefreshTokenExpiry(),
        });
        await user.save();

        return successResponse(res, { accessToken, refreshToken: newRefreshToken }, 'Token refreshed');
    } catch (error: any) {
        return errorResponse(res, 'Invalid refresh token', 401);
    }
};

export const logout = async (req: AuthRequest, res: Response) => {
    try {
        const { refreshToken: token } = req.body;
        const userId = req.user?.userId;

        if (userId && token) {
            await User.findByIdAndUpdate(userId, {
                $pull: { 'auth.refreshTokens': { token } },
            });
        }

        return successResponse(res, null, 'Logged out successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const getProfile = async (req: AuthRequest, res: Response) => {
    try {
        const user = await User.findById(req.user?.userId).select('-password -auth');
        if (!user) {
            return errorResponse(res, 'User not found', 404);
        }
        return successResponse(res, user);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const updateProfile = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.userId;
        const { displayName, username, avatar, currentLevel } = req.body;

        const user = await User.findById(userId);
        if (!user) {
            return errorResponse(res, 'User not found', 404);
        }

        // Check if username is taken by another user
        if (username && username !== user.username) {
            const existingUser = await User.findOne({ username });
            if (existingUser) {
                return errorResponse(res, 'Username already exists', 400);
            }
            user.username = username;
        }

        // Update profile fields
        if (displayName) user.profile.displayName = displayName;
        if (avatar) user.profile.avatar = avatar;
        if (currentLevel) user.profile.currentLevel = currentLevel;

        await user.save();

        return successResponse(res, {
            id: user._id,
            email: user.email,
            username: user.username,
            profile: user.profile,
            stats: user.stats,
            role: user.role,
            isActive: user.isActive
        }, 'Profile updated successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};
