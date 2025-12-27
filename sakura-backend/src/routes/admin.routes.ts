import { Router } from 'express';
import {
    getStats,
    getAllUsers,
    getUserById,
    updateUser,
    deleteUser,
    toggleUserLock,
    getAllVocabularies,
    createVocabulary,
    updateVocabulary,
    deleteVocabulary,
    getAllQuizSets,
    getQuizSetById
} from '../controllers/admin.controller';

const router = Router();

// Statistics
router.get('/stats', getStats);

// User Management
router.get('/users', getAllUsers);
router.get('/users/:id', getUserById);
router.put('/users/:id', updateUser);
router.delete('/users/:id', deleteUser);
router.post('/users/:id/toggle-lock', toggleUserLock);

// Vocabulary Management
router.get('/vocabularies', getAllVocabularies);
router.post('/vocabularies', createVocabulary);
router.put('/vocabularies/:id', updateVocabulary);
router.delete('/vocabularies/:id', deleteVocabulary);

// Quiz Sets
router.get('/quiz-sets', getAllQuizSets);
router.get('/quiz-sets/:id', getQuizSetById);

export default router;
