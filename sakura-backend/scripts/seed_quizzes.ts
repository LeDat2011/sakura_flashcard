import mongoose from 'mongoose';
import dotenv from 'dotenv';
import QuizSet from '../src/models/QuizSet';

dotenv.config();

const topics = [
    'ANIME', 'BODY_PARTS', 'FOOD', 'DAILY_LIFE', 'ANIMALS',
    'SCHOOL', 'TRAVEL', 'WEATHER', 'FAMILY', 'TECHNOLOGY',
    'CLOTHES', 'COLORS', 'NUMBERS', 'COMMON_EXPRESSIONS'
];

const levels = ['N5', 'N4', 'N3', 'N2', 'N1'];

// Helper to get random item from array
const getRandom = <T>(arr: T[]): T => arr[Math.floor(Math.random() * arr.length)];

// Helper to generate a question based on type and level
const createQuestion = (topic: string, level: string, index: number) => {
    // Mix of types: mostly multiple_choice, some true_false and fill_blank
    const types = ['multiple_choice', 'multiple_choice', 'multiple_choice', 'true_false', 'fill_blank'];
    const type = getRandom(types);
    const questionNum = index + 1;

    // Base structure
    const baseQuestion = {
        questionId: `${topic}-${level}-${questionNum}-${Date.now()}`, // Required by schema
        questionNumber: questionNum, // User example field (might not be in schema strict, but good for ordering)
        type: type, // Schema uses 'type', User example used 'questionType'. Schema is the truth.
        // Wait, looking at User's JSON: "questionType": "MULTIPLE_CHOICE". 
        // Looking at Schema I read earlier: "type": { enum: ['multiple_choice', ...] }
        // I will use Schema's 'type' field but map the values correctly.

        question: {
            text: `Question ${questionNum} about ${topic} (${level}) - ${type}?`,
        },
        points: 10,
        difficulty: level === 'N5' ? 1 : level === 'N4' ? 2 : level === 'N3' ? 3 : level === 'N2' ? 4 : 5,
        explanation: `Explanation for ${topic} question ${questionNum}. This is why the answer is correct.`
    };

    if (type === 'multiple_choice') {
        return {
            ...baseQuestion,
            type: 'multiple_choice',
            options: [
                { id: 'A', text: `${topic} Answer A`, isCorrect: true },
                { id: 'B', text: `${topic} Answer B`, isCorrect: false },
                { id: 'C', text: `${topic} Answer C`, isCorrect: false },
                { id: 'D', text: `${topic} Answer D`, isCorrect: false },
            ],
            // Schema doesn't have explicit 'correctAnswer' field at top level, it uses isCorrect in options.
            // User example had "correctAnswer": "A". Schema relies on options.
        };
    } else if (type === 'true_false') {
        return {
            ...baseQuestion,
            type: 'true_false',
            question: { text: `Is ${topic} typically associated with Japan?` },
            options: [
                { id: 'TRUE', text: 'True', isCorrect: true },
                { id: 'FALSE', text: 'False', isCorrect: false }
            ]
        };
    } else if (type === 'fill_blank') {
        return {
            ...baseQuestion,
            type: 'fill_blank',
            question: { text: `Complete the word: Ka_aoke (${topic}).` },
            options: [
                { id: '1', text: 'r', isCorrect: true } // Simplified for fill_blank storage
            ]
        };
    }
    return baseQuestion;
};

const seedQuizzes = async () => {
    try {
        await mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/sakura_flashcard');
        console.log('Connected to MongoDB');

        // Clear existing quizzes to ensure fresh data structure
        await QuizSet.deleteMany({});
        console.log('Cleared existing quizzes');

        for (const topic of topics) {
            for (const level of levels) {
                // Determine question count based on level
                // N5, N4 -> 10 questions
                // N3, N2, N1 -> 5 questions
                const questionCount = (level === 'N5' || level === 'N4') ? 10 : 5;

                // Check existing
                const existing = await QuizSet.findOne({ topic: topic, level: level, setNumber: 1 });
                if (existing) {
                    console.log(`Quiz ${topic} ${level} exists. Skipping.`);
                    continue;
                }

                const questions = [];
                for (let i = 0; i < questionCount; i++) {
                    questions.push(createQuestion(topic, level, i));
                }

                const quizSet = new QuizSet({
                    topic: topic,
                    level: level,
                    setNumber: 1,
                    title: `${topic} - ${level}`,
                    description: `Practice ${topic} vocabulary and grammar at ${level} level.`,
                    questions: questions,
                    settings: {
                        timeLimit: 600,
                        passingScore: 70,
                        shuffleQuestions: true,
                        shuffleOptions: true
                    },
                    isActive: true,
                    isPublished: true
                });

                await quizSet.save();
                console.log(`Created ${topic} ${level} with ${questionCount} questions.`);
            }
        }

        console.log('Seed completed successfully');
        process.exit(0);
    } catch (error) {
        console.error('Seed failed:', error);
        process.exit(1);
    }
};

seedQuizzes();
