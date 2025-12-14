import { Router } from 'express';
import {
    getVocabularies,
    getVocabularyById,
    getTopics,
    searchVocabulary,
} from '../controllers/vocabulary.controller';

const router = Router();

router.get('/', getVocabularies);
router.get('/topics', getTopics);
router.get('/search', searchVocabulary);
router.get('/:id', getVocabularyById);

export default router;
