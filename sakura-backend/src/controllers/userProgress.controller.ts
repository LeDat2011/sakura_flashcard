import { Response } from 'express';
import UserVocabularyProgress from '../models/UserVocabularyProgress';
import QuizAttempt from '../models/QuizAttempt';
import Vocabulary from '../models/Vocabulary';
import User from '../models/User';
import { successResponse, errorResponse, paginatedResponse } from '../utils/response';
import { AuthRequest } from '../middlewares/auth.middleware';
import mongoose from 'mongoose';

// SM2 Algorithm
const calculateSM2 = (quality: number, sm2: any) => {
    let { repetitions, easeFactor, interval } = sm2;

    if (quality >= 3) {
        if (repetitions === 0) {
            interval = 1;
        } else if (repetitions === 1) {
            interval = 6;
        } else {
            interval = Math.round(interval * easeFactor);
        }
        repetitions += 1;
        easeFactor = Math.max(1.3, easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)));
    } else {
        repetitions = 0;
        interval = 1;
    }

    const nextReviewAt = new Date();
    nextReviewAt.setDate(nextReviewAt.getDate() + interval);

    return { repetitions, easeFactor, interval, nextReviewAt, lastReviewedAt: new Date() };
};

// Get due vocabulary for review
export const getDueVocabulary = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.userId;
        const { limit = 20 } = req.query;

        const dueItems = await UserVocabularyProgress.find({
            userId: new mongoose.Types.ObjectId(userId),
            'sm2.nextReviewAt': { $lte: new Date() },
            status: { $ne: 'mastered' },
        })
            .populate('vocabularyId')
            .sort({ 'sm2.nextReviewAt': 1 })
            .limit(parseInt(limit as string));

        return successResponse(res, dueItems);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Update vocabulary progress after review
export const updateVocabularyProgress = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.userId;
        const { vocabularyId, quality } = req.body; // quality: 0-5 (SM2)

        if (quality < 0 || quality > 5) {
            return errorResponse(res, 'Quality must be between 0 and 5', 400);
        }

        // Get vocabulary info
        const vocabulary = await Vocabulary.findById(vocabularyId);
        if (!vocabulary) {
            return errorResponse(res, 'Vocabulary not found', 404);
        }

        // Find or create progress
        let progress = await UserVocabularyProgress.findOne({
            userId: new mongoose.Types.ObjectId(userId),
            vocabularyId: new mongoose.Types.ObjectId(vocabularyId),
        });

        if (!progress) {
            progress = new UserVocabularyProgress({
                userId: new mongoose.Types.ObjectId(userId),
                vocabularyId: new mongoose.Types.ObjectId(vocabularyId),
                topic: vocabulary.topic,
                level: vocabulary.level,
            });
        }

        // Update SM2
        const newSm2 = calculateSM2(quality, progress.sm2);
        progress.sm2 = newSm2;

        // Update stats
        progress.stats.totalReviews += 1;
        if (quality >= 3) {
            progress.stats.correctCount += 1;
        } else {
            progress.stats.incorrectCount += 1;
        }

        // Update status
        if (progress.sm2.repetitions >= 5 && progress.sm2.easeFactor >= 2.5) {
            progress.status = 'mastered';
        } else if (progress.sm2.repetitions >= 2) {
            progress.status = 'reviewing';
        } else if (progress.stats.totalReviews > 0) {
            progress.status = 'learning';
        }

        await progress.save();

        // Update user XP
        const xpEarned = quality >= 3 ? 10 : 2;
        await User.findByIdAndUpdate(userId, {
            $inc: { 'stats.totalXP': xpEarned },
            'stats.lastStudyDate': new Date(),
        });

        return successResponse(res, { progress, xpEarned });
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Get user vocabulary stats
export const getVocabularyStats = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.userId;
        const { topic, level } = req.query;

        const match: any = { userId: new mongoose.Types.ObjectId(userId) };
        if (topic) match.topic = topic;
        if (level) match.level = level;

        const stats = await UserVocabularyProgress.aggregate([
            { $match: match },
            {
                $group: {
                    _id: '$status',
                    count: { $sum: 1 },
                },
            },
        ]);

        const summary = {
            new: 0,
            learning: 0,
            reviewing: 0,
            mastered: 0,
            total: 0,
        };

        stats.forEach((s) => {
            summary[s._id as keyof typeof summary] = s.count;
            summary.total += s.count;
        });

        return successResponse(res, summary);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Get quiz history
export const getQuizHistory = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.userId;
        const { page = 1, limit = 20 } = req.query;

        const pageNum = parseInt(page as string);
        const limitNum = parseInt(limit as string);
        const skip = (pageNum - 1) * limitNum;

        const [attempts, total] = await Promise.all([
            QuizAttempt.find({ userId: new mongoose.Types.ObjectId(userId) })
                .populate('quizSetId', 'title topic level')
                .sort({ completedAt: -1 })
                .skip(skip)
                .limit(limitNum),
            QuizAttempt.countDocuments({ userId: new mongoose.Types.ObjectId(userId) }),
        ]);

        return paginatedResponse(res, attempts, pageNum, limitNum, total);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Get overall user stats
export const getUserStats = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.userId;

        const [user, vocabStats, quizStats] = await Promise.all([
            User.findById(userId).select('stats profile'),
            UserVocabularyProgress.aggregate([
                { $match: { userId: new mongoose.Types.ObjectId(userId) } },
                {
                    $group: {
                        _id: null,
                        total: { $sum: 1 },
                        mastered: { $sum: { $cond: [{ $eq: ['$status', 'mastered'] }, 1, 0] } },
                        totalReviews: { $sum: '$stats.totalReviews' },
                    },
                },
            ]),
            QuizAttempt.aggregate([
                { $match: { userId: new mongoose.Types.ObjectId(userId) } },
                {
                    $group: {
                        _id: null,
                        totalAttempts: { $sum: 1 },
                        avgScore: { $avg: '$score' },
                        totalPassed: { $sum: { $cond: ['$passed', 1, 0] } },
                    },
                },
            ]),
        ]);

        return successResponse(res, {
            user: user?.stats,
            vocabulary: vocabStats[0] || { total: 0, mastered: 0, totalReviews: 0 },
            quiz: quizStats[0] || { totalAttempts: 0, avgScore: 0, totalPassed: 0 },
        });
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};
