# Sakura Flashcard - Database Schema

## MongoDB Atlas Connection
```
URI: mongodb+srv://sakura_admin:***@sakura-flashcard.idbgmve.mongodb.net/sakura_flashcard_db
Database: sakura_flashcard_db
```

---

## Collections Overview

| Collection | Mô tả | Số lượng (hiện tại) |
|------------|-------|---------------------|
| `users` | Thông tin người dùng | - |
| `vocabularies` | Từ vựng tiếng Nhật | 299 |
| `characters` | Hiragana & Katakana | 92 |
| `quiz_sets` | Bộ câu hỏi quiz | 17 |
| `quiz_attempts` | Lịch sử làm quiz | - |
| `user_vocabulary_progress` | Tiến độ học từ vựng | - |
| `achievement_definitions` | Định nghĩa thành tựu | 11 |

---

## 1. Users Collection

```javascript
{
  _id: ObjectId,
  email: String,                    // unique, required
  username: String,                 // unique, required
  password: String,                 // hashed, required
  profile: {
    displayName: String,            // required
    avatar: String,                 // optional, URL
    currentLevel: String            // default: "N5"
  },
  auth: {
    refreshTokens: [{
      token: String,
      expiresAt: Date,
      device: String
    }],
    emailVerified: Boolean,         // default: false
    emailVerificationToken: String,
    passwordResetToken: String,
    passwordResetExpires: Date
  },
  stats: {
    totalXP: Number,                // default: 0
    currentStreak: Number,          // default: 0
    longestStreak: Number,          // default: 0
    lastStudyDate: Date
  },
  role: String,                     // "user" | "admin", default: "user"
  isActive: Boolean,                // default: true
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ email: 1 }                        // unique
{ username: 1 }                     // unique
```

---

## 2. Vocabularies Collection

```javascript
{
  _id: ObjectId,
  word: {
    japanese: String,               // required, e.g. "日本語"
    hiragana: String,               // optional, e.g. "にほんご"
    romaji: String,                 // required, e.g. "nihongo"
    vietnamese: String,             // required, e.g. "Tiếng Nhật"
    wordType: String                // "noun", "verb", "adjective", etc.
  },
  details: {
    explanation: String,
    exampleSentences: [{
      japanese: String,
      reading: String,
      meaning: String
    }],
    synonyms: [String],
    antonyms: [String],
    memoryTip: String
  },
  media: {
    audioUrl: String,
    imageUrl: String
  },
  topic: String,                    // "DAILY_LIFE", "FOOD", "FAMILY", etc.
  level: String,                    // "N5", "N4", "N3", "N2", "N1"
  difficulty: Number,               // 1-5
  order: Number,
  tags: [String],
  isActive: Boolean,                // default: true
  createdBy: ObjectId,              // ref: User
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ topic: 1, level: 1, order: 1 }
```

---

## 3. Characters Collection

```javascript
{
  _id: ObjectId,
  character: String,                // e.g. "あ", "ア"
  type: String,                     // "hiragana" | "katakana"
  romaji: String,                   // e.g. "a"
  strokeCount: Number,
  strokeOrder: [String],            // URLs to stroke images
  audioUrl: String,
  examples: [{
    word: String,
    reading: String,
    meaning: String
  }],
  order: Number,
  createdAt: Date
}

// Indexes
{ type: 1, order: 1 }
{ character: 1 }
```

---

## 4. Quiz Sets Collection

```javascript
{
  _id: ObjectId,
  topic: String,                    // "DAILY_LIFE", "FOOD", etc.
  level: String,                    // "N5", "N4", "N3"
  setNumber: Number,
  title: String,
  description: String,
  questions: [{
    _id: ObjectId,
    questionNumber: Number,
    questionType: String,           // "MULTIPLE_CHOICE", "TRUE_FALSE", "FILL_BLANK"
    questionText: String,
    options: [{
      optionId: String,             // "A", "B", "C", "D"
      text: String,
      isCorrect: Boolean
    }],
    correctAnswer: String,          // "A", "B", "C", or "D"
    explanation: String,
    points: Number,                 // default: 10
    difficulty: Number              // 1-5
  }],
  settings: {
    timeLimit: Number,              // seconds, default: 600
    passingScore: Number,           // percentage, default: 70
    shuffleQuestions: Boolean,      // default: true
    shuffleOptions: Boolean         // default: true
  },
  statistics: {
    totalAttempts: Number,
    averageScore: Number,
    passRate: Number
  },
  isActive: Boolean,                // default: true
  isPublished: Boolean,             // default: true
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ topic: 1, level: 1, setNumber: 1 }  // unique
```

---

## 5. Quiz Attempts Collection

```javascript
{
  _id: ObjectId,
  userId: ObjectId,                 // ref: User
  quizSetId: ObjectId,              // ref: QuizSet
  topic: String,
  level: String,
  score: Number,                    // percentage
  correctCount: Number,
  totalQuestions: Number,
  earnedPoints: Number,
  totalPoints: Number,
  timeSpent: Number,                // seconds
  passed: Boolean,
  answers: [{
    questionId: String,
    userAnswer: String,
    isCorrect: Boolean
  }],
  completedAt: Date,
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ userId: 1, quizSetId: 1, completedAt: -1 }
{ userId: 1, topic: 1, level: 1 }
```

---

## 6. User Vocabulary Progress Collection

```javascript
{
  _id: ObjectId,
  userId: ObjectId,                 // ref: User
  vocabularyId: ObjectId,           // ref: Vocabulary
  topic: String,
  level: String,
  status: String,                   // "new", "learning", "reviewing", "mastered"
  sm2: {
    repetitions: Number,            // default: 0
    easeFactor: Number,             // default: 2.5
    interval: Number,               // days, default: 1
    nextReviewAt: Date,
    lastReviewedAt: Date
  },
  stats: {
    correctCount: Number,           // default: 0
    incorrectCount: Number,         // default: 0
    totalReviews: Number            // default: 0
  },
  notes: {
    userNote: String,
    isFlagged: Boolean              // default: false
  },
  createdAt: Date,
  updatedAt: Date
}

// Indexes
{ userId: 1, vocabularyId: 1 }      // unique
{ userId: 1, status: 1 }
{ userId: 1, "sm2.nextReviewAt": 1 }
```

---

## 7. Achievement Definitions Collection

```javascript
{
  _id: ObjectId,
  code: String,                     // unique, e.g. "FIRST_WORD"
  name: String,
  nameVi: String,                   // Vietnamese name
  description: String,
  descriptionVi: String,            // Vietnamese description
  icon: String,
  category: String,                 // "learning", "streak", "quiz", etc.
  requirement: {
    type: String,                   // "count", "streak", "score"
    target: Number,
    metric: String                  // "words_learned", "days_streak", etc.
  },
  xpReward: Number,
  isActive: Boolean,
  createdAt: Date
}

// Indexes
{ code: 1 }                         // unique
{ category: 1 }
```

---

## Topics List

```javascript
const TOPICS = [
  "DAILY_LIFE",
  "FOOD",
  "FAMILY",
  "SCHOOL",
  "TRAVEL",
  "WEATHER",
  "ANIMALS",
  "NUMBERS",
  "COLORS",
  "COMMON_EXPRESSIONS",
  "TECHNOLOGY",
  "ANIME"
];
```

## JLPT Levels

```javascript
const LEVELS = ["N5", "N4", "N3", "N2", "N1"];
// N5: Beginner
// N4: Elementary
// N3: Intermediate
// N2: Upper Intermediate
// N1: Advanced
```

---

## API Endpoints Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Đăng ký tài khoản |
| POST | `/api/auth/login` | Đăng nhập |
| POST | `/api/auth/refresh-token` | Làm mới token |
| POST | `/api/auth/logout` | Đăng xuất |
| GET | `/api/auth/profile` | Lấy thông tin user |
| GET | `/api/vocabularies` | Lấy danh sách từ vựng |
| GET | `/api/vocabularies/topics` | Lấy danh sách topics |
| GET | `/api/vocabularies/search?q=` | Tìm kiếm từ vựng |
| GET | `/api/vocabularies/:id` | Lấy chi tiết từ vựng |
| GET | `/api/quiz` | Lấy danh sách quiz sets |
| GET | `/api/quiz/topics` | Lấy danh sách quiz topics |
| GET | `/api/quiz/:id` | Lấy chi tiết quiz set |
| POST | `/api/quiz/submit` | Nộp bài quiz |
| GET | `/api/user/vocabulary/due` | Lấy từ cần ôn tập |
| POST | `/api/user/vocabulary/review` | Cập nhật tiến độ |
| GET | `/api/user/vocabulary/stats` | Thống kê từ vựng |
| GET | `/api/user/quiz/history` | Lịch sử làm quiz |
| GET | `/api/user/stats` | Thống kê tổng hợp |

---

## Data Flow Diagrams

### 1. Authentication Flow

```mermaid
sequenceDiagram
    participant App as Android App
    participant API as Backend API
    participant DB as MongoDB

    Note over App,DB: ĐĂNG KÝ
    App->>API: POST /auth/register
    API->>DB: Create User
    DB-->>API: User created
    API->>API: Generate JWT tokens
    API-->>App: { user, accessToken, refreshToken }
    App->>App: Save tokens to SharedPreferences

    Note over App,DB: ĐĂNG NHẬP
    App->>API: POST /auth/login
    API->>DB: Find user by email
    DB-->>API: User data
    API->>API: Verify password
    API->>API: Generate JWT tokens
    API->>DB: Save refresh token
    API-->>App: { user, accessToken, refreshToken }

    Note over App,DB: REFRESH TOKEN
    App->>API: POST /auth/refresh-token
    API->>DB: Verify refresh token exists
    API->>API: Generate new tokens
    API->>DB: Update refresh token
    API-->>App: { accessToken, refreshToken }
```

### 2. Vocabulary Learning Flow

```mermaid
sequenceDiagram
    participant App as Android App
    participant API as Backend API
    participant DB as MongoDB

    Note over App,DB: TẢI TỪ VỰNG
    App->>API: GET /vocabularies?topic=FOOD&level=N5
    API->>DB: Query vocabularies
    DB-->>API: List of vocabularies
    API-->>App: { data, pagination }

    Note over App,DB: HỌC TỪ MỚI
    App->>App: User studies flashcard
    App->>API: POST /user/vocabulary/review
    Note right of App: { vocabularyId, quality: 0-5 }
    API->>DB: Find/Create UserVocabularyProgress
    API->>API: Calculate SM2 algorithm
    API->>DB: Update progress & next review date
    API->>DB: Update user XP
    API-->>App: { progress, xpEarned }

    Note over App,DB: ÔN TẬP SPACED REPETITION
    App->>API: GET /user/vocabulary/due
    API->>DB: Find items where nextReviewAt <= now
    DB-->>API: Due vocabulary list
    API-->>App: Vocabularies to review
```

### 3. Quiz Flow

```mermaid
sequenceDiagram
    participant App as Android App
    participant API as Backend API
    participant DB as MongoDB

    Note over App,DB: CHỌN QUIZ
    App->>API: GET /quiz?topic=FOOD&level=N5
    API->>DB: Query quiz sets
    DB-->>API: Quiz sets list
    API-->>App: { quizSets, pagination }

    Note over App,DB: BẮT ĐẦU LÀM QUIZ
    App->>API: GET /quiz/:id
    API->>DB: Get quiz with questions
    API->>API: Shuffle questions/options
    API->>API: Remove correct answers
    API-->>App: Quiz without answers

    Note over App,DB: NỘP BÀI
    App->>API: POST /quiz/submit
    Note right of App: { quizSetId, answers[], timeSpent }
    API->>DB: Get quiz with correct answers
    API->>API: Calculate score
    API->>DB: Save QuizAttempt
    API-->>App: { score, results, explanations }
```

### 4. Data Relationships

```mermaid
erDiagram
    USERS ||--o{ USER_VOCABULARY_PROGRESS : has
    USERS ||--o{ QUIZ_ATTEMPTS : makes
    VOCABULARIES ||--o{ USER_VOCABULARY_PROGRESS : tracked_by
    QUIZ_SETS ||--o{ QUIZ_ATTEMPTS : attempted_in

    USERS {
        ObjectId _id PK
        string email UK
        string username UK
        string password
        object profile
        object stats
    }

    VOCABULARIES {
        ObjectId _id PK
        object word
        string topic
        string level
        int difficulty
    }

    QUIZ_SETS {
        ObjectId _id PK
        string topic
        string level
        array questions
        object settings
    }

    USER_VOCABULARY_PROGRESS {
        ObjectId _id PK
        ObjectId userId FK
        ObjectId vocabularyId FK
        string status
        object sm2
    }

    QUIZ_ATTEMPTS {
        ObjectId _id PK
        ObjectId userId FK
        ObjectId quizSetId FK
        int score
        boolean passed
    }
```

### 5. SM2 Spaced Repetition Algorithm

```mermaid
flowchart TD
    A[User answers flashcard] --> B{Quality >= 3?}
    B -->|Yes - Correct| C[Increase repetitions]
    B -->|No - Wrong| D[Reset repetitions to 0]
    
    C --> E{repetitions = 0?}
    E -->|Yes| F[interval = 1 day]
    E -->|No| G{repetitions = 1?}
    G -->|Yes| H[interval = 6 days]
    G -->|No| I[interval = interval × easeFactor]
    
    D --> J[interval = 1 day]
    
    F --> K[Update easeFactor]
    H --> K
    I --> K
    J --> K
    
    K --> L[Calculate nextReviewAt]
    L --> M[Save to UserVocabularyProgress]
    M --> N[Award XP to user]
```

### 6. User Learning Journey

```mermaid
flowchart LR
    subgraph Onboarding
        A[Register] --> B[Set JLPT Level]
        B --> C[Choose Topics]
    end
    
    subgraph Daily Learning
        D[Load Due Vocabulary] --> E[Study Flashcards]
        E --> F[Rate Knowledge 0-5]
        F --> G[Update SM2 Progress]
        G --> H[Earn XP]
    end
    
    subgraph Quiz Practice
        I[Select Quiz] --> J[Answer Questions]
        J --> K[Submit Quiz]
        K --> L[View Results]
        L --> M[Review Explanations]
    end
    
    subgraph Progress Tracking
        N[View Stats] --> O[Check Streak]
        O --> P[View Achievements]
        P --> Q[Track Mastery]
    end
    
    C --> D
    H --> I
    M --> N
```

---

## Status Values

### Vocabulary Progress Status
| Status | Meaning | Condition |
|--------|---------|-----------|
| `new` | Chưa học | totalReviews = 0 |
| `learning` | Đang học | totalReviews > 0, repetitions < 2 |
| `reviewing` | Đang ôn tập | repetitions >= 2 |
| `mastered` | Đã thuộc | repetitions >= 5, easeFactor >= 2.5 |

### SM2 Quality Rating
| Quality | Meaning | Effect |
|---------|---------|--------|
| 0 | Không nhớ gì | Reset, interval = 1 |
| 1 | Sai hoàn toàn | Reset, interval = 1 |
| 2 | Sai nhưng nhớ được hint | Reset, interval = 1 |
| 3 | Đúng nhưng khó | Continue, reduce EF |
| 4 | Đúng sau khi nghĩ | Continue, stable EF |
| 5 | Đúng ngay lập tức | Continue, increase EF |
