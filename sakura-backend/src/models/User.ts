import mongoose, { Document, Schema } from 'mongoose';
import bcrypt from 'bcryptjs';

export interface IUser extends Document {
    email: string;
    username: string;
    password: string;
    profile: {
        displayName: string;
        avatar?: string;
        currentLevel: string;
    };
    auth: {
        refreshTokens: { token: string; expiresAt: Date; device?: string }[];
        emailVerified: boolean;
        emailVerificationToken?: string;
        passwordResetToken?: string;
        passwordResetExpires?: Date;
        otpInfo?: {
            code: string;
            expiresAt: Date;
        };
    };
    googleId?: string;
    stats: {
        totalXP: number;
        currentStreak: number;
        longestStreak: number;
        lastStudyDate?: Date;
    };
    role: 'user' | 'admin';
    isActive: boolean;
    createdAt: Date;
    updatedAt: Date;
    comparePassword(candidatePassword: string): Promise<boolean>;
}

const userSchema = new Schema<IUser>(
    {
        email: { type: String, required: true, unique: true, lowercase: true },
        username: { type: String, required: true, unique: true },
        password: { type: String, required: false, minlength: 6 },
        googleId: { type: String, sparse: true, unique: true },
        profile: {
            displayName: { type: String, required: true },
            avatar: String,
            currentLevel: { type: String, default: 'N5' },
        },
        auth: {
            refreshTokens: [
                {
                    token: String,
                    expiresAt: Date,
                    device: String,
                },
            ],
            emailVerified: { type: Boolean, default: false },
            emailVerificationToken: String,
            passwordResetToken: String,
            passwordResetExpires: Date,
            otpInfo: {
                code: String,
                expiresAt: Date
            }
        },
        stats: {
            totalXP: { type: Number, default: 0 },
            currentStreak: { type: Number, default: 0 },
            longestStreak: { type: Number, default: 0 },
            lastStudyDate: Date,
        },
        role: { type: String, enum: ['user', 'admin'], default: 'user' },
        isActive: { type: Boolean, default: true },
    },
    { timestamps: true }
);

// Hash password before saving
userSchema.pre('save', async function (next) {
    if (!this.isModified('password')) return next();
    const salt = await bcrypt.genSalt(12);
    this.password = await bcrypt.hash(this.password, salt);
    next();
});

// Compare password method
userSchema.methods.comparePassword = async function (candidatePassword: string): Promise<boolean> {
    return bcrypt.compare(candidatePassword, this.password);
};

export default mongoose.model<IUser>('User', userSchema);
