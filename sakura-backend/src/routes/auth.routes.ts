import { Router } from 'express';
import { body } from 'express-validator';
import { register, login, refreshToken, logout, getProfile } from '../controllers/auth.controller';
import { authenticate } from '../middlewares/auth.middleware';
import { validate } from '../middlewares/validate.middleware';

const router = Router();

// Validation rules
const registerValidation = [
    body('email').isEmail().withMessage('Valid email required'),
    body('username').isLength({ min: 3 }).withMessage('Username min 3 characters'),
    body('password').isLength({ min: 6 }).withMessage('Password min 6 characters'),
];

const loginValidation = [
    body('email').isEmail().withMessage('Valid email required'),
    body('password').notEmpty().withMessage('Password required'),
];

// Routes
router.post('/register', validate(registerValidation), register);
router.post('/login', validate(loginValidation), login);
router.post('/refresh-token', refreshToken);
router.post('/logout', authenticate, logout);
router.get('/profile', authenticate, getProfile);

export default router;
