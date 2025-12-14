import { Router } from 'express';
import { body } from 'express-validator';
import {
    getQuizSets,
    getQuizSetById,
    getQuizTopics,
    getQuizQuestion,
    getQuizQuestions,
    getQuizAnswer,
    submitQuiz
} from '../controllers/quiz.controller';
import { authenticate } from '../middlewares/auth.middleware';
import { validate } from '../middlewares/validate.middleware';

const router = Router();

const submitValidation = [
    body('quizSetId').notEmpty().withMessage('Quiz set ID required'),
    body('answers').isArray().withMessage('Answers must be an array')
];

// Public routes
router.get('/topics', getQuizTopics);
router.get('/question', getQuizQuestion);
router.get('/questions', getQuizQuestions);
router.get('/answer', getQuizAnswer);
router.get('/', getQuizSets);
router.get('/:id', getQuizSetById);

// Protected routes
router.post('/submit', authenticate, validate(submitValidation), submitQuiz);

export default router;
