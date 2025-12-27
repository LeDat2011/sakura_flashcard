import { Request, Response } from 'express';
import User from '../models/User';
import Vocabulary from '../models/Vocabulary';
import QuizSet from '../models/QuizSet';
import { successResponse, errorResponse } from '../utils/response';

/**
 * Admin Controller
 * Handles all admin dashboard operations
 */

// ==================== STATISTICS ====================

export const getStats = async (req: Request, res: Response) => {
    try {
        const [
            totalUsers,
            activeUsers,
            totalVocabularies,
            totalQuizSets,
            recentUsers
        ] = await Promise.all([
            User.countDocuments(),
            User.countDocuments({ isActive: true }),
            Vocabulary.countDocuments(),
            QuizSet.countDocuments(),
            User.find().sort({ createdAt: -1 }).limit(5).select('username email createdAt')
        ]);

        return successResponse(res, {
            users: {
                total: totalUsers,
                active: activeUsers,
                inactive: totalUsers - activeUsers
            },
            content: {
                vocabularies: totalVocabularies,
                quizSets: totalQuizSets
            },
            recentUsers
        });
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// ==================== USER MANAGEMENT ====================

export const getAllUsers = async (req: Request, res: Response) => {
    try {
        const page = parseInt(req.query.page as string) || 1;
        const limit = parseInt(req.query.limit as string) || 20;
        const search = req.query.search as string || '';

        const query: any = {};
        if (search) {
            query.$or = [
                { username: { $regex: search, $options: 'i' } },
                { email: { $regex: search, $options: 'i' } }
            ];
        }

        const [users, total] = await Promise.all([
            User.find(query)
                .select('-password -auth.refreshTokens')
                .sort({ createdAt: -1 })
                .skip((page - 1) * limit)
                .limit(limit),
            User.countDocuments(query)
        ]);

        return successResponse(res, {
            users,
            pagination: {
                page,
                limit,
                total,
                pages: Math.ceil(total / limit)
            }
        });
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const getUserById = async (req: Request, res: Response) => {
    try {
        const user = await User.findById(req.params.id).select('-password -auth.refreshTokens');
        if (!user) return errorResponse(res, 'User not found', 404);
        return successResponse(res, user);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const updateUser = async (req: Request, res: Response) => {
    try {
        const { username, email, role, isActive, profile } = req.body;
        const user = await User.findById(req.params.id);

        if (!user) return errorResponse(res, 'User not found', 404);

        if (username) user.username = username;
        if (email) user.email = email;
        if (role) user.role = role;
        if (typeof isActive === 'boolean') user.isActive = isActive;
        if (profile) {
            if (profile.displayName) user.profile.displayName = profile.displayName;
            if (profile.currentLevel) user.profile.currentLevel = profile.currentLevel;
        }

        await user.save();
        return successResponse(res, user, 'User updated successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const deleteUser = async (req: Request, res: Response) => {
    try {
        const user = await User.findByIdAndDelete(req.params.id);
        if (!user) return errorResponse(res, 'User not found', 404);
        return successResponse(res, null, 'User deleted successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const toggleUserLock = async (req: Request, res: Response) => {
    try {
        const user = await User.findById(req.params.id);
        if (!user) return errorResponse(res, 'User not found', 404);

        user.isActive = !user.isActive;
        await user.save();

        return successResponse(res, {
            isActive: user.isActive
        }, `User ${user.isActive ? 'unlocked' : 'locked'} successfully`);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// ==================== CONTENT MANAGEMENT ====================

export const getAllVocabularies = async (req: Request, res: Response) => {
    try {
        const page = parseInt(req.query.page as string) || 1;
        const limit = parseInt(req.query.limit as string) || 50;
        const level = req.query.level as string;
        const topic = req.query.topic as string;

        const query: any = {};
        if (level) query.level = level;
        if (topic) query.topic = topic;

        const [vocabularies, total] = await Promise.all([
            Vocabulary.find(query)
                .sort({ topic: 1, level: 1, order: 1 })
                .skip((page - 1) * limit)
                .limit(limit),
            Vocabulary.countDocuments(query)
        ]);

        return successResponse(res, {
            vocabularies,
            pagination: { page, limit, total, pages: Math.ceil(total / limit) }
        });
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const createVocabulary = async (req: Request, res: Response) => {
    try {
        const { word, details, topic, level, order } = req.body;

        if (!word?.japanese || !word?.reading || !word?.meaning) {
            return errorResponse(res, 'Word (japanese, reading, meaning) is required', 400);
        }
        if (!topic || !level) {
            return errorResponse(res, 'Topic and level are required', 400);
        }

        const vocabulary = new Vocabulary({
            word,
            details: details || { partOfSpeech: 'noun', examples: [] },
            topic,
            level,
            order: order || 0
        });

        await vocabulary.save();
        return successResponse(res, vocabulary, 'Vocabulary created successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const updateVocabulary = async (req: Request, res: Response) => {
    try {
        const { word, details, topic, level, order, isActive } = req.body;
        const vocabulary = await Vocabulary.findById(req.params.id);

        if (!vocabulary) return errorResponse(res, 'Vocabulary not found', 404);

        if (word) {
            if (word.japanese) vocabulary.word.japanese = word.japanese;
            if (word.reading) vocabulary.word.reading = word.reading;
            if (word.meaning) vocabulary.word.meaning = word.meaning;
        }
        if (details) {
            if (details.partOfSpeech) vocabulary.details.partOfSpeech = details.partOfSpeech;
            if (details.examples) vocabulary.details.examples = details.examples;
            if (details.notes !== undefined) vocabulary.details.notes = details.notes;
        }
        if (topic) vocabulary.topic = topic;
        if (level) vocabulary.level = level;
        if (typeof order === 'number') vocabulary.order = order;
        if (typeof isActive === 'boolean') vocabulary.isActive = isActive;

        await vocabulary.save();
        return successResponse(res, vocabulary, 'Vocabulary updated successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const deleteVocabulary = async (req: Request, res: Response) => {
    try {
        const vocabulary = await Vocabulary.findByIdAndDelete(req.params.id);
        if (!vocabulary) return errorResponse(res, 'Vocabulary not found', 404);
        return successResponse(res, null, 'Vocabulary deleted successfully');
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const getAllQuizSets = async (req: Request, res: Response) => {
    try {
        const quizSets = await QuizSet.find().sort({ level: 1, topic: 1 });
        return successResponse(res, quizSets);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const getQuizSetById = async (req: Request, res: Response) => {
    try {
        const quizSet = await QuizSet.findById(req.params.id);
        if (!quizSet) return errorResponse(res, 'Quiz set not found', 404);
        return successResponse(res, quizSet);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};
