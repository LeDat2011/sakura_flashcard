import { Router } from 'express';
import { body } from 'express-validator';
import {
    getDueVocabulary,
    updateVocabularyProgress,
    getVocabularyStats,
    getQuizHistory,
    getUserStats,
} from '../controllers/userProgress.controller';
import { authenticate } from '../middlewares/auth.middleware';
import { validate } from '../middlewares/validate.middleware';

const router = Router();

const progressValidation = [
    body('vocabularyId').notEmpty().withMessage('Vocabulary ID required'),
    body('quality').isInt({ min: 0, max: 5 }).withMessage('Quality must be 0-5'),
];

// All routes require authentication
router.use(authenticate);

router.get('/vocabulary/due', getDueVocabulary);
router.post('/vocabulary/review', validate(progressValidation), updateVocabularyProgress);
router.get('/vocabulary/stats', getVocabularyStats);
router.get('/quiz/history', getQuizHistory);
router.get('/stats', getUserStats);

export default router;
