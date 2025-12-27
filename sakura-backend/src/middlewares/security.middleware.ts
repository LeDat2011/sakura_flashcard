import { Request, Response, NextFunction } from 'express';
import rateLimit from 'express-rate-limit';
import mongoSanitize from 'express-mongo-sanitize';
import hpp from 'hpp';

/**
 * Security Middleware Collection
 * Protects against: Brute-force, NoSQL Injection, XSS, HPP
 */

// ==================== RATE LIMITING ====================

/**
 * General API rate limiter
 * Limits: 100 requests per 15 minutes per IP
 */
export const generalLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100,
    message: {
        success: false,
        message: 'Quá nhiều request. Vui lòng thử lại sau 15 phút.'
    },
    standardHeaders: true,
    legacyHeaders: false,
});

/**
 * Strict limiter for authentication endpoints
 * Limits: 5 attempts per 15 minutes per IP
 * Protects against brute-force password attacks
 */
export const authLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 5,
    message: {
        success: false,
        message: 'Quá nhiều lần đăng nhập thất bại. Vui lòng thử lại sau 15 phút.'
    },
    standardHeaders: true,
    legacyHeaders: false,
    skipSuccessfulRequests: true, // Only count failed attempts
});

/**
 * OTP rate limiter
 * Limits: 3 OTP requests per 5 minutes per IP
 */
export const otpLimiter = rateLimit({
    windowMs: 5 * 60 * 1000, // 5 minutes
    max: 3,
    message: {
        success: false,
        message: 'Quá nhiều yêu cầu OTP. Vui lòng thử lại sau 5 phút.'
    },
    standardHeaders: true,
    legacyHeaders: false,
});

/**
 * Password reset rate limiter
 * Limits: 3 requests per hour per IP
 */
export const passwordResetLimiter = rateLimit({
    windowMs: 60 * 60 * 1000, // 1 hour
    max: 3,
    message: {
        success: false,
        message: 'Quá nhiều yêu cầu đặt lại mật khẩu. Vui lòng thử lại sau 1 giờ.'
    },
    standardHeaders: true,
    legacyHeaders: false,
});

// ==================== INPUT SANITIZATION ====================

/**
 * Sanitize user input to prevent NoSQL Injection
 * Removes $ and . from user input
 */
export const sanitizeInput = mongoSanitize({
    replaceWith: '_',
    onSanitize: ({ key, req }: { key: string; req: Request }) => {
        console.warn(`[SECURITY] Potential NoSQL injection detected in field: ${key}`);
    }
});

/**
 * Custom XSS sanitizer middleware
 * Escapes HTML entities in string fields
 */
export const xssSanitizer = (req: Request, res: Response, next: NextFunction) => {
    const sanitizeString = (str: string): string => {
        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#x27;')
            .replace(/\//g, '&#x2F;');
    };

    const sanitizeObject = (obj: any): any => {
        if (typeof obj === 'string') {
            return sanitizeString(obj);
        }
        if (Array.isArray(obj)) {
            return obj.map(sanitizeObject);
        }
        if (obj && typeof obj === 'object') {
            const sanitized: any = {};
            for (const [key, value] of Object.entries(obj)) {
                sanitized[key] = sanitizeObject(value);
            }
            return sanitized;
        }
        return obj;
    };

    if (req.body) {
        req.body = sanitizeObject(req.body);
    }
    if (req.query) {
        req.query = sanitizeObject(req.query);
    }
    if (req.params) {
        req.params = sanitizeObject(req.params);
    }

    next();
};

/**
 * HTTP Parameter Pollution protection
 * Prevents duplicate query parameters being exploited
 */
export const hppProtection = hpp({
    whitelist: ['sort', 'filter', 'level', 'topic'] // Allow these to be arrays
});

// ==================== ACCOUNT LOCKOUT ====================

// In-memory store for failed login attempts (use Redis in production)
const loginAttempts = new Map<string, { count: number; lockUntil: number }>();

const MAX_LOGIN_ATTEMPTS = 5;
const LOCK_TIME = 15 * 60 * 1000; // 15 minutes

/**
 * Account lockout middleware
 * Tracks failed login attempts and locks accounts temporarily
 */
export const accountLockout = (req: Request, res: Response, next: NextFunction) => {
    const email = req.body.email?.toLowerCase();
    if (!email) return next();

    const attempts = loginAttempts.get(email);
    const now = Date.now();

    // Check if account is locked
    if (attempts && attempts.lockUntil > now) {
        const remainingMinutes = Math.ceil((attempts.lockUntil - now) / 60000);
        return res.status(429).json({
            success: false,
            message: `Tài khoản tạm khóa do đăng nhập thất bại quá nhiều lần. Vui lòng thử lại sau ${remainingMinutes} phút.`
        });
    }

    // Reset if lock has expired
    if (attempts && attempts.lockUntil <= now) {
        loginAttempts.delete(email);
    }

    next();
};

/**
 * Record a failed login attempt
 */
export const recordFailedLogin = (email: string): { isLocked: boolean; attemptsLeft: number } => {
    const normalizedEmail = email.toLowerCase();
    const attempts = loginAttempts.get(normalizedEmail) || { count: 0, lockUntil: 0 };

    attempts.count++;

    if (attempts.count >= MAX_LOGIN_ATTEMPTS) {
        attempts.lockUntil = Date.now() + LOCK_TIME;
        loginAttempts.set(normalizedEmail, attempts);
        return { isLocked: true, attemptsLeft: 0 };
    }

    loginAttempts.set(normalizedEmail, attempts);
    return { isLocked: false, attemptsLeft: MAX_LOGIN_ATTEMPTS - attempts.count };
};

/**
 * Clear login attempts on successful login
 */
export const clearLoginAttempts = (email: string): void => {
    loginAttempts.delete(email.toLowerCase());
};

// ==================== SECURITY HEADERS ====================

/**
 * Additional security headers middleware
 */
export const securityHeaders = (req: Request, res: Response, next: NextFunction) => {
    // Prevent MIME type sniffing
    res.setHeader('X-Content-Type-Options', 'nosniff');

    // Prevent clickjacking
    res.setHeader('X-Frame-Options', 'DENY');

    // XSS filter
    res.setHeader('X-XSS-Protection', '1; mode=block');

    // Referrer policy
    res.setHeader('Referrer-Policy', 'strict-origin-when-cross-origin');

    // Permissions policy
    res.setHeader('Permissions-Policy', 'geolocation=(), microphone=(), camera=()');

    // HSTS - Force HTTPS for 1 year, include subdomains
    // Only set in production to avoid issues during development
    if (process.env.NODE_ENV === 'production') {
        res.setHeader('Strict-Transport-Security', 'max-age=31536000; includeSubDomains; preload');
    }

    next();
};

// ==================== HTTPS ENFORCEMENT ====================

/**
 * Middleware to enforce HTTPS in production
 * Redirects HTTP requests to HTTPS
 */
export const enforceHttps = (req: Request, res: Response, next: NextFunction) => {
    // Skip in development
    if (process.env.NODE_ENV !== 'production') {
        return next();
    }

    // Check if request is already HTTPS
    // x-forwarded-proto is set by reverse proxies like nginx, AWS ELB
    const isHttps = req.secure || req.headers['x-forwarded-proto'] === 'https';

    if (!isHttps) {
        // Redirect to HTTPS
        return res.redirect(301, `https://${req.headers.host}${req.url}`);
    }

    next();
};

// ==================== TLS CONFIGURATION INFO ====================

/**
 * TLS Configuration Guidelines for Production:
 * 
 * 1. Use TLS 1.2 or higher (TLS 1.3 preferred)
 * 2. Use strong cipher suites (ECDHE, AES-GCM)
 * 3. Enable OCSP Stapling
 * 4. Use 2048-bit or higher RSA keys, or 256-bit ECDSA
 * 
 * Example nginx configuration:
 * 
 * ssl_protocols TLSv1.2 TLSv1.3;
 * ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
 * ssl_prefer_server_ciphers on;
 * ssl_session_cache shared:SSL:10m;
 * ssl_stapling on;
 * ssl_stapling_verify on;
 */

