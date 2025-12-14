# MongoDB Database Schema - Sakura Flashcard

> Version: 1.0 | Database: MongoDB | Updated: 2024-12-14

---

## Summary - 14 Collections

| # | Collection | Purpose |
|---|------------|---------|
| 1 | users | User, auth, progress |
| 2 | vocabularies | Flashcard hệ thống |
| 3 | user_vocabulary_progress | SM-2 cho vocabulary |
| 4 | quiz_sets | Bộ câu hỏi (10 câu/bộ) |
| 5 | quiz_attempts | Lịch sử làm quiz |
| 6 | user_quiz_progress | Tiến độ quiz tổng |
| 7 | custom_decks | Bộ flashcard user tạo |
| 8 | custom_flashcards | Card trong bộ custom |
| 9 | deck_study_sessions | Phiên học (optional) |
| 10 | characters | Hiragana/Katakana/Kanji |
| 11 | user_character_progress | SM-2 cho characters |
| 12 | leaderboards | Bảng xếp hạng |
| 13 | user_achievements | Thành tích user |
| 14 | achievement_definitions | Định nghĩa thành tích |

---

## 1. users

{
  _id: ObjectId,
  username: String,                    // unique, 3-20 chars
  email: String,                       // unique, lowercase
  passwordHash: String,                // bcrypt 12 rounds

  profile: {
    displayName: String,
    avatar: String,
    age: Number,
    currentLevel: String,              // N5-N1
    targetLevel: String,
    dailyStudyGoalMinutes: Number,
    isOnboardingCompleted: Boolean
  },

  learningProgress: {
    flashcardsLearned: Number,
    quizzesCompleted: Number,
    currentStreak: Number,
    longestStreak: Number,
    totalStudyTimeMinutes: Number,
    lastStudyDate: Date,
    levelProgress: { N5, N4, N3, N2, N1: Number } // 0.0-1.0
  },

  auth: {
    refreshTokens: [{
      token: String,
      deviceInfo: String,
      ipAddress: String,
      createdAt: Date,
      expiresAt: Date,
      lastUsedAt: Date
    }],
    passwordChangedAt: Date,
    failedLoginAttempts: Number,       // max 5 -> lock
    lockUntil: Date,
    emailVerified: Boolean,
    emailVerificationToken: String,
    emailVerificationExpires: Date,
    passwordResetToken: String,
    passwordResetExpires: Date
  },

  role: String,                        // "user", "admin", "moderator"
  isActive: Boolean,
  createdAt: Date,
  updatedAt: Date,
  lastLoginAt: Date
}

// Indexes
{ email: 1 }                           // unique
{ username: 1 }                        // unique
{ "auth.refreshTokens.token": 1 }

---

## 2. vocabularies

{
  _id: ObjectId,
  topic: String,                       // ANIME, FOOD, DAILY_LIFE...
  level: String,                       // N5, N4, N3, N2, N1

  word: {
    japanese: String,                  // 食べる
    hiragana: String,                  // たべる
    romaji: String,                    // taberu
    vietnamese: String,                // Ăn
    wordType: String                   // noun, verb, adjective...
  },

  details: {
    explanation: String,
    exampleSentences: [{
      japanese: String,
      hiragana: String,
      romaji: String,
      vietnamese: String
    }],
    synonyms: [String],
    antonyms: [String],
    memoryTip: String
  },

  media: {
    audioUrl: String,
    imageUrl: String
  },

  difficulty: Number,                  // 1-5
  order: Number,
  tags: [String],
  isActive: Boolean,
  createdBy: ObjectId,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ topic: 1, level: 1, order: 1 }

---

## 3. user_vocabulary_progress (SM-2)

{
  _id: ObjectId,
  userId: ObjectId,
  vocabularyId: ObjectId,
  topic: String,                       // denormalized
  level: String,

  status: String,                      // new, learning, reviewing, mastered

  sm2: {
    repetitionCount: Number,
    easeFactor: Number,                // default 2.5, min 1.3
    interval: Number,                  // days
    nextReviewAt: Date,
    lastReviewedAt: Date
  },

  stats: {
    totalReviews: Number,
    againCount: Number,                // Quên rồi
    hardCount: Number,                 // Hơi khó
    goodCount: Number,                 // Nhớ rồi
    currentStreak: Number,
    longestStreak: Number
  },

  notes: {
    personalNote: String,
    isFlagged: Boolean
  },

  firstLearnedAt: Date,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ userId: 1, vocabularyId: 1 }         // unique
{ userId: 1, "sm2.nextReviewAt": 1 }   // cards cần ôn

---

## 4. quiz_sets

{
  _id: ObjectId,
  topic: String,
  level: String,
  setNumber: Number,                   // Bộ 1, 2, 3...
  title: String,
  description: String,

  questions: [{                        // 10 câu/bộ
    _id: ObjectId,
    questionNumber: Number,
    questionType: String,              // MULTIPLE_CHOICE, FILL_IN_BLANK, TRUE_FALSE
    questionText: String,
    questionAudio: String,
    questionImage: String,

    options: [{
      optionId: String,                // A, B, C, D
      text: String,
      isCorrect: Boolean
    }],

    correctAnswer: String,
    acceptedAnswers: [String],
    explanation: String,
    points: Number,                    // default 10
    difficulty: Number
  }],

  settings: {
    totalQuestions: Number,            // 10
    passingScore: Number,              // 70%
    shuffleQuestions: Boolean,
    shuffleOptions: Boolean,
    showExplanation: Boolean,
    allowRetry: Boolean
  },

  statistics: {
    totalAttempts: Number,
    averageScore: Number,
    passRate: Number
  },

  isActive: Boolean,
  isPublished: Boolean,
  createdBy: ObjectId,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ topic: 1, level: 1, setNumber: 1 }   // unique

---

## 5. quiz_attempts

{
  _id: ObjectId,
  userId: ObjectId,
  quizSetId: ObjectId,
  topic: String,
  level: String,
  setNumber: Number,

  score: Number,
  totalPoints: Number,
  percentage: Number,
  correctCount: Number,
  totalQuestions: Number,
  isPassed: Boolean,

  answers: [{
    questionId: ObjectId,
    questionNumber: Number,
    userAnswer: String,
    correctAnswer: String,
    isCorrect: Boolean,
    points: Number,
    timeSpentSeconds: Number
  }],

  startedAt: Date,
  completedAt: Date,
  totalTimeSeconds: Number,
  attemptNumber: Number,
  createdAt: Date
}

// Indexes
{ userId: 1, quizSetId: 1, completedAt: -1 }

---

## 6. user_quiz_progress

{
  _id: ObjectId,
  userId: ObjectId,

  progress: [{
    topic: String,
    level: String,
    completedSets: [Number],
    currentSet: Number,
    totalSetsAvailable: Number,
    bestScores: [{
      setNumber: Number,
      score: Number,
      percentage: Number,
      achievedAt: Date
    }],
    totalAttempts: Number,
    averageScore: Number
  }],

  overallStats: {
    totalQuizzesTaken: Number,
    totalQuestionsAnswered: Number,
    correctAnswers: Number,
    accuracy: Number
  },

  updatedAt: Date
}

// Indexes
{ userId: 1 }                          // unique

---

## 7. custom_decks

{
  _id: ObjectId,
  userId: ObjectId,
  name: String,
  description: String,
  coverImage: String,
  color: String,                       // #FF6B6B
  icon: String,
  topic: String,
  level: String,
  tags: [String],

  statistics: {
    cardCount: Number,
    learnedCount: Number,
    masteredCount: Number,
    lastStudiedAt: Date
  },

  settings: {
    isPublic: Boolean,
    allowCopy: Boolean,
    shuffleCards: Boolean
  },

  sharing: {
    copyCount: Number,
    likeCount: Number,
    viewCount: Number
  },

  isFavorite: Boolean,
  isArchived: Boolean,
  order: Number,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ userId: 1, isArchived: 1, order: 1 }

---

## 8. custom_flashcards

{
  _id: ObjectId,
  deckId: ObjectId,
  userId: ObjectId,

  front: {
    japanese: String,
    hiragana: String,
    romaji: String,
    audioUrl: String,
    imageUrl: String
  },

  back: {
    vietnamese: String,
    alternativeMeanings: [String],
    wordType: String,
    explanation: String,
    exampleSentence: {
      japanese: String,
      vietnamese: String
    }
  },

  notes: {
    personalNote: String,
    memoryTip: String,
    difficulty: String                 // easy, medium, hard
  },

  learning: {
    status: String,                    // new, learning, reviewing, mastered
    repetitionCount: Number,
    easeFactor: Number,
    interval: Number,
    nextReviewAt: Date,
    lastReviewedAt: Date,
    totalReviews: Number,
    againCount: Number,
    hardCount: Number,
    goodCount: Number
  },

  order: Number,
  isFlagged: Boolean,
  tags: [String],
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ deckId: 1, order: 1 }
{ userId: 1, "learning.nextReviewAt": 1 }

---

## 9. deck_study_sessions

{
  _id: ObjectId,
  userId: ObjectId,
  deckId: ObjectId,
  sessionType: String,                 // learn_new, review, cram

  cardsStudied: Number,
  correctCount: Number,
  incorrectCount: Number,

  cardResults: [{
    cardId: ObjectId,
    result: String,                    // again, hard, good
    responseTime: Number,
    wasCorrect: Boolean
  }],

  startedAt: Date,
  completedAt: Date,
  durationSeconds: Number,
  createdAt: Date
}

// Indexes
{ userId: 1, completedAt: -1 }
{ deckId: 1, completedAt: -1 }

---

## 10. characters

{
  _id: ObjectId,
  character: String,                   // あ, ア, 日
  script: String,                      // HIRAGANA, KATAKANA, KANJI

  readings: {
    romaji: String,                    // a, ka, shi
    onyomi: [String],                  // Kanji only
    kunyomi: [String],
    onyomiRomaji: [String],
    kunyomiRomaji: [String]
  },

  meanings: {
    vietnamese: [String],
    english: [String]
  },

  writing: {
    strokeCount: Number,
    strokeOrder: String,               // URL gif/video
    strokeImages: [String],
    animationUrl: String,
    practiceSheetUrl: String
  },

  examples: [{
    word: String,
    hiragana: String,
    romaji: String,
    meaning: String
  }],

  category: {
    row: String,                       // a, ka, sa... (Hiragana/Katakana)
    type: String,                      // basic, dakuten, combo
    jlptLevel: String,                 // Kanji only
    radical: String,
    radicalMeaning: String
  },

  memory: {
    mnemonic: String,
    mnemonicImage: String,
    similarCharacters: [String],
    commonMistakes: String
  },

  audio: {
    pronunciationUrl: String,
    exampleAudioUrls: [String]
  },

  order: Number,
  difficulty: Number,
  isActive: Boolean,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ script: 1, order: 1 }
{ character: 1 }                       // unique

---

## 11. user_character_progress

{
  _id: ObjectId,
  userId: ObjectId,
  characterId: ObjectId,

  status: String,                      // not_started, learning, learned, mastered

  sm2: {
    repetitionCount: Number,
    easeFactor: Number,
    interval: Number,
    nextReviewAt: Date,
    lastReviewedAt: Date
  },

  stats: {
    totalReviews: Number,
    againCount: Number,
    hardCount: Number,
    goodCount: Number,
    writingPracticeCount: Number
  },

  personalNote: String,
  isFlagged: Boolean,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ userId: 1, characterId: 1 }          // unique
{ userId: 1, "sm2.nextReviewAt": 1 }

---

## 12. leaderboards

{
  _id: ObjectId,
  type: String,                        // QUIZ, STREAK, XP, VOCABULARY
  scope: {
    period: String,                    // DAILY, WEEKLY, MONTHLY, ALL_TIME
    topic: String,
    level: String
  },
  periodStart: Date,
  periodEnd: Date,

  rankings: [{
    rank: Number,
    userId: ObjectId,
    username: String,
    displayName: String,
    avatar: String,
    score: Number,
    details: {
      quizzesCompleted: Number,
      currentStreak: Number,
      wordsLearned: Number
    },
    previousRank: Number,
    rankChange: Number
  }],

  maxRankings: Number,
  lastCalculatedAt: Date,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ type: 1, "scope.period": 1, periodStart: -1 }
{ "rankings.userId": 1 }

---

## 13. user_achievements

{
  _id: ObjectId,
  userId: ObjectId,

  achievements: [{
    achievementId: String,
    name: String,
    description: String,
    icon: String,
    category: String,                  // QUIZ, STREAK, VOCABULARY
    rarity: String,                    // COMMON, RARE, EPIC, LEGENDARY
    isUnlocked: Boolean,
    unlockedAt: Date,
    progress: Number,                  // 0-100%
    currentValue: Number,
    targetValue: Number,
    xpReward: Number,
    badgeUrl: String
  }],

  stats: {
    totalUnlocked: Number,
    totalAchievements: Number,
    totalXPFromAchievements: Number
  },

  updatedAt: Date
}

// Indexes
{ userId: 1 }                          // unique

---

## 14. achievement_definitions

{
  _id: ObjectId,
  achievementId: String,               // unique
  name: String,
  description: String,
  icon: String,
  badgeUrl: String,
  category: String,
  rarity: String,

  condition: {
    type: String,                      // STREAK, COUNT, SCORE
    field: String,
    operator: String,                  // >=, ==, >
    value: Number
  },

  rewards: {
    xp: Number,
    badge: Boolean,
    title: String
  },

  order: Number,
  isActive: Boolean,
  isHidden: Boolean,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ achievementId: 1 }                   // unique
{ category: 1, order: 1 }
{ isActive: 1 }

---

## SM-2 Algorithm (3 Levels)

| Button | Label | Quality | Action |
|--------|-------|---------|--------|
| X | Quên rồi | 1 | Reset: interval=1, n=0 |
| ? | Hơi khó | 3 | interval*1.2, EF-0.15 |
| V | Nhớ rồi | 5 | interval*EF, EF+0.1 |

function updateSM2(card, quality) {
  let { repetitionCount, easeFactor, interval } = card.sm2;

  if (quality === 1) {
    repetitionCount = 0;
    interval = 1;
  } else if (quality === 3) {
    repetitionCount++;
    interval = Math.ceil(interval * 1.2);
    easeFactor = Math.max(1.3, easeFactor - 0.15);
  } else if (quality === 5) {
    repetitionCount++;
    if (repetitionCount === 1) interval = 1;
    else if (repetitionCount === 2) interval = 6;
    else interval = Math.ceil(interval * easeFactor);
    easeFactor += 0.1;
  }

  easeFactor = Math.min(3.0, Math.max(1.3, easeFactor));
  
  let status = repetitionCount >= 5 && interval >= 21 
    ? "mastered" 
    : repetitionCount >= 2 ? "reviewing" : "learning";

  return { sm2: { repetitionCount, easeFactor, interval, nextReviewAt, lastReviewedAt }, status };
}

---

## Enums

JLPTLevel = ["N5", "N4", "N3", "N2", "N1"]
VocabularyTopic = ["ANIME", "FOOD", "DAILY_LIFE", "ANIMALS", "SCHOOL", "TRAVEL", "WEATHER", "FAMILY", "TECHNOLOGY", "CLOTHES", "COLORS", "NUMBERS", "COMMON_EXPRESSIONS"]
CharacterScript = ["HIRAGANA", "KATAKANA", "KANJI"]
QuestionType = ["MULTIPLE_CHOICE", "FILL_IN_BLANK", "TRUE_FALSE"]
LearningStatus = ["new", "learning", "reviewing", "mastered"]
UserRole = ["user", "admin", "moderator"]
