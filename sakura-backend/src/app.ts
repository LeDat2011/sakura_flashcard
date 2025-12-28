import express, { Application, Request, Response, NextFunction } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import dotenv from 'dotenv';
import connectDB from './config/database';

// Security middleware
import {
    generalLimiter,
    sanitizeInput,
    xssSanitizer,
    hppProtection,
    securityHeaders,
    enforceHttps
} from './middlewares/security.middleware';

// Import routes
import authRoutes from './routes/auth.routes';
import vocabularyRoutes from './routes/vocabulary.routes';
import quizRoutes from './routes/quiz.routes';
import userProgressRoutes from './routes/userProgress.routes';
import adminRoutes from './routes/admin.routes';

dotenv.config();

const app: Application = express();
const PORT = process.env.PORT || 3000;

// ==================== SECURITY MIDDLEWARE ====================

// Enforce HTTPS in production (must be first)
app.use(enforceHttps);

// Set security HTTP headers
app.use(helmet());

// Additional security headers (includes HSTS)
app.use(securityHeaders);

// Enable CORS with specific options
app.use(cors({
    origin: process.env.ALLOWED_ORIGINS?.split(',') || '*',
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'],
    allowedHeaders: ['Content-Type', 'Authorization'],
    credentials: true,
    maxAge: 86400 // 24 hours
}));

// Rate limiting - apply to all requests
app.use(generalLimiter);

// Body parsers
app.use(express.json({ limit: '10kb' })); // Limit body size to prevent DoS
app.use(express.urlencoded({ extended: true, limit: '10kb' }));

// Data sanitization against NoSQL injection
app.use(sanitizeInput);

// Data sanitization against XSS
app.use(xssSanitizer);

// Prevent HTTP Parameter Pollution
app.use(hppProtection);

// ==================== STATIC FILES (Admin Dashboard) ====================

app.use('/admin', express.static('web'));

// ==================== ROUTES ====================

app.use('/api/auth', authRoutes);
app.use('/api/vocabularies', vocabularyRoutes);
app.use('/api/quiz', quizRoutes);
app.use('/api/user', userProgressRoutes);
app.use('/api/admin', adminRoutes);

// Health check
app.get('/health', (req: Request, res: Response) => {
    res.json({ status: 'OK', timestamp: new Date().toISOString() });
});


// Error handling middleware
app.use((err: any, req: Request, res: Response, next: NextFunction) => {
    console.error(err.stack);

    // Don't leak error details in production
    const message = process.env.NODE_ENV === 'production'
        ? 'Internal Server Error'
        : err.message;

    res.status(err.status || 500).json({
        success: false,
        message,
    });
});

// 404 handler
app.use((req: Request, res: Response) => {
    res.status(404).json({ success: false, message: 'Route not found' });
});

// Start server
const startServer = async () => {
    await connectDB();
    const HOST = '0.0.0.0'; // Accept connections from any IP
    app.listen(Number(PORT), HOST, () => {
        console.log(`🚀 Server running on http://${HOST}:${PORT}`);
        console.log(`🛡️ Security middleware enabled: Rate limiting, XSS, NoSQL injection protection`);
    });
};

startServer();

