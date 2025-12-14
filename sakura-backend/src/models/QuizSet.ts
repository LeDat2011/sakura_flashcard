import mongoose, { Document, Schema } from 'mongoose';

export interface IQuizOption {
    optionId: string;
    text: string;
    isCorrect: boolean;
}

export interface IQuestion {
    questionNumber: number;
    questionType: string;
    questionText: string;
    options: IQuizOption[];
    correctAnswer: string;
    explanation?: string;
    points: number;
    difficulty: number;
}

export interface IQuizSet extends Document {
    topic: string;
    level: string;
    setNumber: number;
    title: string;
    description?: string;
    questions: IQuestion[];
    settings: {
        totalQuestions: number;
        passingScore: number;
        shuffleQuestions: boolean;
        shuffleOptions: boolean;
        showExplanation: boolean;
        allowRetry: boolean;
    };
    statistics: {
        totalAttempts: number;
        averageScore: number;
        passRate: number;
    };
    isActive: boolean;
    isPublished: boolean;
}

const quizSetSchema = new Schema<IQuizSet>(
    {
        topic: { type: String, required: true },
        level: { type: String, required: true },
        setNumber: { type: Number, required: true },
        title: { type: String, required: true },
        description: String,
        questions: [
            {
                questionNumber: { type: Number, required: true },
                questionType: { type: String, required: true },
                questionText: { type: String, required: true },
                options: [
                    {
                        optionId: { type: String, required: true },
                        text: { type: String, required: true },
                        isCorrect: { type: Boolean, default: false }
                    }
                ],
                correctAnswer: { type: String, required: true },
                explanation: String,
                points: { type: Number, default: 10 },
                difficulty: { type: Number, default: 1 }
            }
        ],
        settings: {
            totalQuestions: { type: Number, default: 10 },
            passingScore: { type: Number, default: 70 },
            shuffleQuestions: { type: Boolean, default: true },
            shuffleOptions: { type: Boolean, default: true },
            showExplanation: { type: Boolean, default: true },
            allowRetry: { type: Boolean, default: true }
        },
        statistics: {
            totalAttempts: { type: Number, default: 0 },
            averageScore: { type: Number, default: 0 },
            passRate: { type: Number, default: 0 }
        },
        isActive: { type: Boolean, default: true },
        isPublished: { type: Boolean, default: false }
    },
    { timestamps: true }
);

quizSetSchema.index({ topic: 1, level: 1, setNumber: 1 }, { unique: true });
quizSetSchema.index({ topic: 1, level: 1 });

export default mongoose.model<IQuizSet>('QuizSet', quizSetSchema, 'quiz_sets');
