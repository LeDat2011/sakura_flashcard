import { Request, Response } from 'express';
import Vocabulary from '../models/Vocabulary';
import { successResponse, errorResponse, paginatedResponse } from '../utils/response';

export const getVocabularies = async (req: Request, res: Response) => {
    try {
        const { topic, level, page = 1, limit = 20 } = req.query;

        const query: any = { isActive: true };
        if (topic) query.topic = topic;
        if (level) query.level = level;

        const pageNum = parseInt(page as string);
        const limitNum = parseInt(limit as string);
        const skip = (pageNum - 1) * limitNum;

        const [vocabularies, total] = await Promise.all([
            Vocabulary.find(query).sort({ order: 1 }).skip(skip).limit(limitNum),
            Vocabulary.countDocuments(query),
        ]);

        return paginatedResponse(res, vocabularies, pageNum, limitNum, total);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const getVocabularyById = async (req: Request, res: Response) => {
    try {
        const vocabulary = await Vocabulary.findById(req.params.id);
        if (!vocabulary) {
            return errorResponse(res, 'Vocabulary not found', 404);
        }
        return successResponse(res, vocabulary);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const getTopics = async (req: Request, res: Response) => {
    try {
        const { level } = req.query;

        const match: any = { isActive: true };
        if (level) match.level = level;

        const topics = await Vocabulary.aggregate([
            { $match: match },
            {
                $group: {
                    _id: { topic: '$topic', level: '$level' },
                    count: { $sum: 1 },
                },
            },
            {
                $project: {
                    _id: 0,
                    topic: '$_id.topic',
                    level: '$_id.level',
                    count: 1,
                },
            },
            { $sort: { level: 1, topic: 1 } },
        ]);

        return successResponse(res, topics);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

export const searchVocabulary = async (req: Request, res: Response) => {
    try {
        const { q, level } = req.query;

        if (!q) {
            return errorResponse(res, 'Search query required', 400);
        }

        const query: any = {
            isActive: true,
            $or: [
                { 'word.japanese': { $regex: q, $options: 'i' } },
                { 'word.reading': { $regex: q, $options: 'i' } },
                { 'word.meaning': { $regex: q, $options: 'i' } },
            ],
        };

        if (level) query.level = level;

        const vocabularies = await Vocabulary.find(query).limit(50);

        return successResponse(res, vocabularies);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};
