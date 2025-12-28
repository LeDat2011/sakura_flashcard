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
import { OAuth2Client } from 'google-auth-library';
import nodemailer from 'nodemailer';
import crypto from 'crypto';

const googleClient = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

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

        // Import security functions
        const { recordFailedLogin, clearLoginAttempts } = await import('../middlewares/security.middleware');

        // Find user
        const user = await User.findOne({ email: email.toLowerCase() });
        if (!user) {
            // Record failed attempt even for non-existent users (prevent enumeration)
            recordFailedLogin(email);
            return errorResponse(res, 'Invalid credentials', 401);
        }

        // Check password
        const isMatch = await user.comparePassword(password);
        if (!isMatch) {
            const { isLocked, attemptsLeft } = recordFailedLogin(email);

            if (isLocked) {
                return errorResponse(res, 'Tài khoản tạm khóa do đăng nhập thất bại quá nhiều lần. Vui lòng thử lại sau 15 phút.', 429);
            }

            return errorResponse(res, `Sai mật khẩu. Còn ${attemptsLeft} lần thử.`, 401);
        }

        // Check if active
        if (!user.isActive) {
            return errorResponse(res, 'Account is deactivated', 403);
        }

        // Clear failed login attempts on successful login
        clearLoginAttempts(email);

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

export const googleLogin = async (req: Request, res: Response) => {
    try {
        const { idToken } = req.body;
        if (!idToken) return errorResponse(res, 'ID Token is required', 400);

        const ticket = await googleClient.verifyIdToken({
            idToken,
            audience: process.env.GOOGLE_CLIENT_ID
        });

        const payload = ticket.getPayload();
        if (!payload || !payload.email) return errorResponse(res, 'Invalid Google Token', 400);

        const { email, name, sub: googleId, picture } = payload;

        let user = await User.findOne({ $or: [{ googleId }, { email }] });

        if (!user) {
            // Create user for Google Login
            user = await User.create({
                email,
                username: email.split('@')[0] + Math.random().toString(36).substring(7),
                googleId,
                profile: {
                    displayName: name || email.split('@')[0],
                    avatar: picture
                },
                auth: { emailVerified: true }
            });
        } else if (!user.googleId) {
            // Link existing account to Google
            user.googleId = googleId;
            if (!user.profile.avatar && picture) user.profile.avatar = picture;
            await user.save();
        }

        const tokenPayload = { userId: user._id.toString(), email: user.email, role: user.role };
        const accessToken = generateAccessToken(tokenPayload);
        const refreshToken = generateRefreshToken(tokenPayload);

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
        }, 'Google Login successful');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const sendOTP = async (req: Request, res: Response) => {
    try {
        const { email } = req.body;
        const user = await User.findOne({ email });
        if (!user) return errorResponse(res, 'User not found', 404);

        const otp = Math.floor(100000 + Math.random() * 900000).toString();
        user.auth.otpInfo = {
            code: otp,
            expiresAt: new Date(Date.now() + 5 * 60 * 1000) // 5 minutes
        };
        await user.save();

        // Send Email (Mocking for now, need valid SMTP credentials in .env)
        const transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
                user: process.env.EMAIL_USER,
                pass: process.env.EMAIL_PASS
            }
        });

        const mailOptions = {
            from: `"Sakura Flashcard" <${process.env.EMAIL_USER}>`,
            to: email,
            subject: '🌸 Sakura Flashcard - Mã xác thực OTP',
            html: `
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <!-- Header -->
                        <div style="background: linear-gradient(135deg, #FF69B4 0%, #FFB6C1 100%); border-radius: 16px 16px 0 0; padding: 30px; text-align: center;">
                            <h1 style="color: white; margin: 0; font-size: 28px;">🌸 Sakura Flashcard</h1>
                            <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px;">Học tiếng Nhật mỗi ngày</p>
                        </div>
                        
                        <!-- Content -->
                        <div style="background-color: white; padding: 40px 30px; border-radius: 0 0 16px 16px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                            <h2 style="color: #333; margin: 0 0 20px 0; font-size: 22px; text-align: center;">Xác thực tài khoản</h2>
                            
                            <p style="color: #666; font-size: 16px; line-height: 1.6; text-align: center; margin-bottom: 30px;">
                                Xin chào! Đây là mã xác thực OTP của bạn:
                            </p>
                            
                            <!-- OTP Box -->
                            <div style="background: linear-gradient(135deg, #FFF0F5 0%, #FFE4E1 100%); border: 2px dashed #FF69B4; border-radius: 12px; padding: 25px; text-align: center; margin: 20px 0;">
                                <p style="font-size: 40px; font-weight: bold; color: #FF1493; letter-spacing: 8px; margin: 0;">
                                    ${otp}
                                </p>
                            </div>
                            
                            <p style="color: #888; font-size: 14px; text-align: center; margin-top: 25px;">
                                ⏰ Mã này có hiệu lực trong <strong style="color: #FF69B4;">5 phút</strong>
                            </p>
                            
                            <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                            
                            <p style="color: #999; font-size: 13px; text-align: center; line-height: 1.6;">
                                Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.<br>
                                Không chia sẻ mã OTP với bất kỳ ai.
                            </p>
                        </div>
                        
                        <!-- Footer -->
                        <div style="text-align: center; padding: 20px; color: #999; font-size: 12px;">
                            <p style="margin: 0;">© 2024 Sakura Flashcard. All rights reserved.</p>
                            <p style="margin: 5px 0 0 0;">Học tiếng Nhật thông minh 📚</p>
                        </div>
                    </div>
                </body>
                </html>
            `
        };

        // In development, just log the OTP if no credentials provided
        if (!process.env.EMAIL_USER || !process.env.EMAIL_PASS) {
            console.log(`[DEV] OTP for ${email}: ${otp}`);
        } else {
            await transporter.sendMail(mailOptions);
        }

        return successResponse(res, null, 'OTP sent successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const verifyOTP = async (req: Request, res: Response) => {
    try {
        const { email, otp } = req.body;
        const user = await User.findOne({ email });

        if (!user || !user.auth.otpInfo) return errorResponse(res, 'OTP not requested', 400);
        if (user.auth.otpInfo.code !== otp) return errorResponse(res, 'Invalid OTP', 400);
        if (user.auth.otpInfo.expiresAt < new Date()) return errorResponse(res, 'OTP expired', 400);

        // Clear OTP
        user.auth.otpInfo = undefined;
        await user.save();

        const tokenPayload = { userId: user._id.toString(), email: user.email, role: user.role };
        const accessToken = generateAccessToken(tokenPayload);
        const refreshToken = generateRefreshToken(tokenPayload);

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
        }, 'OTP verified successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const forgotPassword = async (req: Request, res: Response) => {
    try {
        const { email } = req.body;
        const user = await User.findOne({ email: email.toLowerCase() });

        if (!user) {
            // Don't reveal if email exists or not (security)
            return successResponse(res, null, 'Nếu email tồn tại, bạn sẽ nhận được hướng dẫn đặt lại mật khẩu.');
        }

        // Generate reset token (6 digit OTP for simplicity)
        const resetToken = Math.floor(100000 + Math.random() * 900000).toString();
        user.auth.passwordResetToken = resetToken;
        user.auth.passwordResetExpires = new Date(Date.now() + 15 * 60 * 1000); // 15 minutes
        await user.save();

        // Send Email
        const transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
                user: process.env.EMAIL_USER,
                pass: process.env.EMAIL_PASS
            }
        });

        const mailOptions = {
            from: `"Sakura Flashcard" <${process.env.EMAIL_USER}>`,
            to: email,
            subject: '🔐 Sakura Flashcard - Đặt lại mật khẩu',
            html: `
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <!-- Header -->
                        <div style="background: linear-gradient(135deg, #FF69B4 0%, #FFB6C1 100%); border-radius: 16px 16px 0 0; padding: 30px; text-align: center;">
                            <h1 style="color: white; margin: 0; font-size: 28px;">🌸 Sakura Flashcard</h1>
                            <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px;">Học tiếng Nhật mỗi ngày</p>
                        </div>
                        
                        <!-- Content -->
                        <div style="background-color: white; padding: 40px 30px; border-radius: 0 0 16px 16px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                            <h2 style="color: #333; margin: 0 0 20px 0; font-size: 22px; text-align: center;">🔐 Đặt lại mật khẩu</h2>
                            
                            <p style="color: #666; font-size: 16px; line-height: 1.6; text-align: center; margin-bottom: 30px;">
                                Bạn đã yêu cầu đặt lại mật khẩu. Đây là mã xác nhận của bạn:
                            </p>
                            
                            <!-- Reset Code Box -->
                            <div style="background: linear-gradient(135deg, #FFF0F5 0%, #FFE4E1 100%); border: 2px dashed #FF69B4; border-radius: 12px; padding: 25px; text-align: center; margin: 20px 0;">
                                <p style="font-size: 40px; font-weight: bold; color: #FF1493; letter-spacing: 8px; margin: 0;">
                                    ${resetToken}
                                </p>
                            </div>
                            
                            <p style="color: #888; font-size: 14px; text-align: center; margin-top: 25px;">
                                ⏰ Mã này có hiệu lực trong <strong style="color: #FF69B4;">15 phút</strong>
                            </p>
                            
                            <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                            
                            <p style="color: #999; font-size: 13px; text-align: center; line-height: 1.6;">
                                Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.<br>
                                Tài khoản của bạn vẫn an toàn.
                            </p>
                        </div>
                        
                        <!-- Footer -->
                        <div style="text-align: center; padding: 20px; color: #999; font-size: 12px;">
                            <p style="margin: 0;">© 2024 Sakura Flashcard. All rights reserved.</p>
                            <p style="margin: 5px 0 0 0;">Học tiếng Nhật thông minh 📚</p>
                        </div>
                    </div>
                </body>
                </html>
            `
        };

        if (!process.env.EMAIL_USER || !process.env.EMAIL_PASS) {
            console.log(`[DEV] Password Reset Token for ${email}: ${resetToken}`);
        } else {
            await transporter.sendMail(mailOptions);
        }

        return successResponse(res, null, 'Nếu email tồn tại, bạn sẽ nhận được hướng dẫn đặt lại mật khẩu.');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const resetPassword = async (req: Request, res: Response) => {
    try {
        const { email, token, newPassword } = req.body;

        const user = await User.findOne({
            email: email.toLowerCase(),
            'auth.passwordResetToken': token,
            'auth.passwordResetExpires': { $gt: new Date() }
        });

        if (!user) {
            return errorResponse(res, 'Mã xác nhận không hợp lệ hoặc đã hết hạn', 400);
        }

        // Update password
        user.password = newPassword;
        user.auth.passwordResetToken = undefined;
        user.auth.passwordResetExpires = undefined;
        await user.save();

        return successResponse(res, null, 'Đặt lại mật khẩu thành công! Vui lòng đăng nhập.');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};
