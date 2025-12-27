import { Router } from 'express';
import { body } from 'express-validator';
import { register, login, refreshToken, logout, getProfile, updateProfile, googleLogin, sendOTP, verifyOTP } from '../controllers/auth.controller';
import { authenticate } from '../middlewares/auth.middleware';
import { validate } from '../middlewares/validate.middleware';
import { authLimiter, otpLimiter, accountLockout } from '../middlewares/security.middleware';

const router = Router();

// Validation rules
const registerValidation = [
    body('email').isEmail().normalizeEmail().withMessage('Valid email required'),
    body('username')
        .isLength({ min: 3, max: 30 })
        .matches(/^[a-zA-Z0-9_]+$/)
        .withMessage('Username: 3-30 characters, letters, numbers, underscores only'),
    body('password')
        .isLength({ min: 8 })
        .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/)
        .withMessage('Password: min 8 chars, 1 uppercase, 1 lowercase, 1 number'),
];

const loginValidation = [
    body('email').isEmail().normalizeEmail().withMessage('Valid email required'),
    body('password').notEmpty().withMessage('Password required'),
];

const otpValidation = [
    body('email').isEmail().normalizeEmail().withMessage('Valid email required'),
];

const otpVerifyValidation = [
    body('email').isEmail().normalizeEmail().withMessage('Valid email required'),
    body('otp')
        .isLength({ min: 6, max: 6 })
        .isNumeric()
        .withMessage('OTP must be 6 digits'),
];

// Routes with security middleware
router.post('/register', authLimiter, validate(registerValidation), register);
router.post('/login', authLimiter, accountLockout, validate(loginValidation), login);
router.post('/google', authLimiter, googleLogin);
router.post('/otp/send', otpLimiter, validate(otpValidation), sendOTP);
router.post('/otp/verify', authLimiter, validate(otpVerifyValidation), verifyOTP);
router.post('/refresh-token', authLimiter, refreshToken);
router.post('/logout', authenticate, logout);
router.get('/profile', authenticate, getProfile);
router.put('/profile', authenticate, updateProfile);

export default router;

