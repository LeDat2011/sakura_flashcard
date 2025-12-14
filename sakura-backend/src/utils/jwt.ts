import jwt from 'jsonwebtoken';
import dotenv from 'dotenv';

dotenv.config();

const JWT_SECRET: jwt.Secret = process.env.JWT_SECRET || 'default_secret';
const JWT_REFRESH_SECRET: jwt.Secret = process.env.JWT_REFRESH_SECRET || 'default_refresh_secret';
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '15m';
const JWT_REFRESH_EXPIRES_IN = process.env.JWT_REFRESH_EXPIRES_IN || '7d';

export interface TokenPayload {
    userId: string;
    email: string;
    role: string;
}

export const generateAccessToken = (payload: TokenPayload): string => {
    return jwt.sign(
        { userId: payload.userId, email: payload.email, role: payload.role },
        JWT_SECRET,
        { expiresIn: '15m' }
    );
};

export const generateRefreshToken = (payload: TokenPayload): string => {
    return jwt.sign(
        { userId: payload.userId, email: payload.email, role: payload.role },
        JWT_REFRESH_SECRET,
        { expiresIn: '7d' }
    );
};

export const verifyAccessToken = (token: string): TokenPayload => {
    return jwt.verify(token, JWT_SECRET) as TokenPayload;
};

export const verifyRefreshToken = (token: string): TokenPayload => {
    return jwt.verify(token, JWT_REFRESH_SECRET) as TokenPayload;
};

export const getRefreshTokenExpiry = (): Date => {
    const days = 7;
    return new Date(Date.now() + days * 24 * 60 * 60 * 1000);
};
