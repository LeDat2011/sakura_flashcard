import mongoose, { Document, Schema } from 'mongoose';

export interface IVocabulary extends Document {
    word: {
        japanese: string;
        reading: string;
        meaning: string;
    };
    details: {
        partOfSpeech: string;
        examples: { japanese: string; reading: string; meaning: string }[];
        notes?: string;
    };
    topic: string;
    level: string;
    order: number;
    isActive: boolean;
}

const vocabularySchema = new Schema<IVocabulary>(
    {
        word: {
            japanese: { type: String, required: true },
            reading: { type: String, required: true },
            meaning: { type: String, required: true },
        },
        details: {
            partOfSpeech: { type: String, required: true },
            examples: [
                {
                    japanese: String,
                    reading: String,
                    meaning: String,
                },
            ],
            notes: String,
        },
        topic: { type: String, required: true },
        level: { type: String, required: true },
        order: { type: Number, default: 0 },
        isActive: { type: Boolean, default: true },
    },
    { timestamps: true }
);

vocabularySchema.index({ topic: 1, level: 1, order: 1 });

export default mongoose.model<IVocabulary>('Vocabulary', vocabularySchema);
