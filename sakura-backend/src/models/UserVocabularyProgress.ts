import mongoose, { Document, Schema } from 'mongoose';

export interface IUserVocabularyProgress extends Document {
    userId: mongoose.Types.ObjectId;
    vocabularyId: mongoose.Types.ObjectId;
    topic: string;
    level: string;
    status: 'new' | 'learning' | 'reviewing' | 'mastered';
    sm2: {
        repetitions: number;
        easeFactor: number;
        interval: number;
        nextReviewAt: Date;
        lastReviewedAt?: Date;
    };
    stats: {
        correctCount: number;
        incorrectCount: number;
        totalReviews: number;
    };
    notes: {
        userNote?: string;
        isFlagged: boolean;
    };
}

const userVocabularyProgressSchema = new Schema<IUserVocabularyProgress>(
    {
        userId: { type: Schema.Types.ObjectId, ref: 'User', required: true },
        vocabularyId: { type: Schema.Types.ObjectId, ref: 'Vocabulary', required: true },
        topic: { type: String, required: true },
        level: { type: String, required: true },
        status: {
            type: String,
            enum: ['new', 'learning', 'reviewing', 'mastered'],
            default: 'new',
        },
        sm2: {
            repetitions: { type: Number, default: 0 },
            easeFactor: { type: Number, default: 2.5 },
            interval: { type: Number, default: 1 },
            nextReviewAt: { type: Date, default: Date.now },
            lastReviewedAt: Date,
        },
        stats: {
            correctCount: { type: Number, default: 0 },
            incorrectCount: { type: Number, default: 0 },
            totalReviews: { type: Number, default: 0 },
        },
        notes: {
            userNote: String,
            isFlagged: { type: Boolean, default: false },
        },
    },
    { timestamps: true }
);

userVocabularyProgressSchema.index({ userId: 1, vocabularyId: 1 }, { unique: true });
userVocabularyProgressSchema.index({ userId: 1, status: 1 });
userVocabularyProgressSchema.index({ userId: 1, 'sm2.nextReviewAt': 1 });

export default mongoose.model<IUserVocabularyProgress>('UserVocabularyProgress', userVocabularyProgressSchema);
