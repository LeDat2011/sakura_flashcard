import mongoose from 'mongoose';
import dotenv from 'dotenv';
import fs from 'fs';
import path from 'path';
import QuizSet from '../src/models/QuizSet';

dotenv.config();

// Helper to transform EJSON (MongoDB export format) to standard JS objects for Mongoose
const transformData = (data: any): any => {
    if (data === null || data === undefined) return data;

    // Safety check: if it's already an ObjectId or Date, return as is to prevent recursive object loop issues
    if (data instanceof mongoose.Types.ObjectId || data instanceof Date) {
        return data;
    }

    if (Array.isArray(data)) {
        return data.map(item => transformData(item));
    } else if (typeof data === 'object') {
        // Handle specific EJSON keys immediately
        if (data.$oid) return new mongoose.Types.ObjectId(data.$oid);
        if (data.$date) return new Date(data.$date);

        // Handle _id field specially if it contains $oid in the root object (legacy behavior support, though the recursion below handles it too)
        // We will just recurse.

        const transformed: any = {};
        for (const key in data) {
            // Recurse on values. 
            // If data[key] is { "$oid": "..." }, recursion transforms it to ObjectId.
            // If data[key] is { "$date": "..." }, recursion transforms it to Date.
            transformed[key] = transformData(data[key]);
        }
        return transformed;
    }
    return data;
};

const seedQuizData = async () => {
    try {
        await mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/sakura_flashcard');
        console.log('Connected to MongoDB');

        const jsonPath = path.join(__dirname, '../../quiz_data.json');
        console.log(`Reading data from: ${jsonPath}`);

        if (!fs.existsSync(jsonPath)) {
            console.error('quiz_data.json not found at expected path!');
            process.exit(1);
        }

        const fileContent = fs.readFileSync(jsonPath, 'utf-8');
        const rawData = JSON.parse(fileContent);

        // Transform EJSON data
        const transformedData = transformData(rawData);

        const quizzes = Array.isArray(transformedData) ? transformedData : [transformedData];

        console.log(`Found ${quizzes.length} quiz sets to seed.`);

        // Clear existing quizzes
        await QuizSet.deleteMany({});
        console.log('Cleared existing quizzes collection.');

        // Insert new quizzes
        const result = await QuizSet.insertMany(quizzes);

        console.log(`Successfully seeded ${result.length} quiz sets.`);
        process.exit(0);
    } catch (error: any) {
        console.error('Seed failed:', JSON.stringify(error, null, 2));
        if (error.errors) {
            console.error('Validation Errors Details:', JSON.stringify(error.errors, null, 2));
        }
        process.exit(1);
    }
};

seedQuizData();
