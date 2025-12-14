import { Request, Response } from 'express';
import QuizSet from '../models/QuizSet';
import { successResponse, errorResponse, paginatedResponse } from '../utils/response';
import { AuthRequest } from '../middlewares/auth.middleware';

// Lấy danh sách topics
export const getQuizTopics = async (req: Request, res: Response) => {
    try {
        const { level } = req.query;

        const match: any = { isActive: true, isPublished: true };
        if (level) match.level = level;

        const topics = await QuizSet.aggregate([
            { $match: match },
            {
                $group: {
                    _id: { topic: '$topic', level: '$level' },
                    quizCount: { $sum: 1 },
                    totalQuestions: { $sum: { $size: '$questions' } }
                }
            },
            {
                $project: {
                    _id: 0,
                    topic: '$_id.topic',
                    level: '$_id.level',
                    quizCount: 1,
                    totalQuestions: 1
                }
            },
            { $sort: { topic: 1, level: 1 } }
        ]);

        return successResponse(res, topics);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Lấy danh sách quiz sets
export const getQuizSets = async (req: Request, res: Response) => {
    try {
        const { topic, level, page = 1, limit = 20 } = req.query;

        const query: any = { isActive: true, isPublished: true };
        if (topic) query.topic = topic;
        if (level) query.level = level;

        const pageNum = parseInt(page as string);
        const limitNum = parseInt(limit as string);
        const skip = (pageNum - 1) * limitNum;

        const [quizSets, total] = await Promise.all([
            QuizSet.find(query).sort({ setNumber: 1 }).skip(skip).limit(limitNum),
            QuizSet.countDocuments(query)
        ]);

        return paginatedResponse(res, quizSets, pageNum, limitNum, total);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Lấy quiz set theo ID
export const getQuizSetById = async (req: Request, res: Response) => {
    try {
        const quizSet = await QuizSet.findOne({
            _id: req.params.id,
            isActive: true,
            isPublished: true
        });

        if (!quizSet) {
            return errorResponse(res, 'Quiz set not found', 404);
        }

        return successResponse(res, quizSet);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Lấy 1 câu hỏi cụ thể
export const getQuizQuestion = async (req: Request, res: Response) => {
    try {
        const { topic, level, questionNumber = 1 } = req.query;

        if (!topic || !level) {
            return errorResponse(res, 'Topic và level là bắt buộc', 400);
        }

        const qNum = parseInt(questionNumber as string);

        const quizSet = await QuizSet.findOne(
            {
                topic: topic,
                level: level,
                'questions.questionNumber': qNum,
                isActive: true,
                isPublished: true
            },
            {
                topic: 1,
                level: 1,
                setNumber: 1,
                title: 1,
                description: 1,
                settings: 1,
                'questions.$': 1
            }
        );

        if (!quizSet) {
            return errorResponse(res, 'Không tìm thấy câu hỏi', 404);
        }

        return successResponse(res, quizSet);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Lấy tất cả câu hỏi của 1 quiz (theo topic + level)
export const getQuizQuestions = async (req: Request, res: Response) => {
    try {
        const { topic, level } = req.query;

        if (!topic || !level) {
            return errorResponse(res, 'Topic và level là bắt buộc', 400);
        }

        const quizSet = await QuizSet.findOne({
            topic: topic,
            level: level,
            isActive: true,
            isPublished: true
        });

        if (!quizSet) {
            return errorResponse(res, 'Không tìm thấy quiz set', 404);
        }

        return successResponse(res, quizSet);
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Lấy đáp án của 1 câu hỏi
export const getQuizAnswer = async (req: Request, res: Response) => {
    try {
        const { topic, level, questionNumber } = req.query;

        if (!topic || !level || !questionNumber) {
            return errorResponse(res, 'Topic, level và questionNumber là bắt buộc', 400);
        }

        const qNum = parseInt(questionNumber as string);

        const quizSet = await QuizSet.findOne(
            {
                topic: topic,
                level: level,
                'questions.questionNumber': qNum,
                isActive: true,
                isPublished: true
            },
            {
                'questions.$': 1
            }
        );

        if (!quizSet || !quizSet.questions[0]) {
            return errorResponse(res, 'Không tìm thấy câu hỏi', 404);
        }

        const question = quizSet.questions[0];
        const correctOption = question.options.find(o => o.optionId === question.correctAnswer);

        return successResponse(res, {
            questionNumber: question.questionNumber,
            correctAnswer: question.correctAnswer,
            correctOptionText: correctOption?.text || '',
            explanation: question.explanation
        });
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};

// Submit quiz
export const submitQuiz = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.userId;
        if (!userId) {
            return errorResponse(res, 'Unauthorized', 401);
        }

        const { quizSetId, answers, timeSpent } = req.body;

        const quizSet = await QuizSet.findById(quizSetId);
        if (!quizSet) {
            return errorResponse(res, 'Quiz set not found', 404);
        }

        let correctCount = 0;
        let earnedPoints = 0;
        let totalPoints = 0;

        const results = quizSet.questions.map((question) => {
            const userAnswer = answers.find(
                (a: any) => a.questionNumber === question.questionNumber
            );

            const correctOption = question.options.find(o => o.optionId === question.correctAnswer);
            const isCorrect = userAnswer?.answer === question.correctAnswer;

            if (isCorrect) {
                correctCount++;
                earnedPoints += question.points;
            }
            totalPoints += question.points;

            return {
                questionNumber: question.questionNumber,
                questionText: question.questionText,
                userAnswer: userAnswer?.answer || '',
                correctAnswer: question.correctAnswer,
                correctOptionText: correctOption?.text || '',
                isCorrect,
                explanation: question.explanation,
                points: isCorrect ? question.points : 0
            };
        });

        const score = Math.round((correctCount / quizSet.questions.length) * 100);
        const passed = score >= quizSet.settings.passingScore;

        // Cập nhật statistics
        await QuizSet.findByIdAndUpdate(quizSetId, {
            $inc: { 'statistics.totalAttempts': 1 }
        });

        return successResponse(res, {
            quizSetId,
            topic: quizSet.topic,
            level: quizSet.level,
            results,
            summary: {
                score,
                correctCount,
                totalQuestions: quizSet.questions.length,
                earnedPoints,
                totalPoints,
                timeSpent,
                passed,
                passingScore: quizSet.settings.passingScore
            }
        });
    } catch (error: any) {
        return errorResponse(res, error.message, 500);
    }
};
