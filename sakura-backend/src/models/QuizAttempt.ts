import mongoose, { Document, Schema } from 'mongoose';

export interface IQuizAttempt extends Document {
    userId: mongoose.Types.ObjectId;
    quizSetId: mongoose.Types.ObjectId;
    topic: string;
    level: string;
    score: number;
    correctCount: number;
    totalQuestions: number;
    earnedPoints: number;
    totalPoints: number;
    timeSpent: number;
    passed: boolean;
    answers: {
        questionId: string;
        userAnswer: string;
        isCorrect: boolean;
    }[];
    completedAt: Date;
}

const quizAttemptSchema = new Schema<IQuizAttempt>(
    {
        userId: { type: Schema.Types.ObjectId, ref: 'User', required: true },
        quizSetId: { type: Schema.Types.ObjectId, ref: 'QuizSet', required: true },
        topic: { type: String, required: true },
        level: { type: String, required: true },
        score: { type: Number, required: true },
        correctCount: { type: Number, required: true },
        totalQuestions: { type: Number, required: true },
        earnedPoints: { type: Number, required: true },
        totalPoints: { type: Number, required: true },
        timeSpent: { type: Number, default: 0 },
        passed: { type: Boolean, required: true },
        answers: [
            {
                questionId: String,
                userAnswer: String,
                isCorrect: Boolean,
            },
        ],
        completedAt: { type: Date, default: Date.now },
    },
    { timestamps: true }
);

quizAttemptSchema.index({ userId: 1, quizSetId: 1, completedAt: -1 });
quizAttemptSchema.index({ userId: 1, topic: 1, level: 1 });

export default mongoose.model<IQuizAttempt>('QuizAttempt', quizAttemptSchema);
