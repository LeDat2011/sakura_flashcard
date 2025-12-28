# BÁO CÁO ĐỒ ÁN
# ỨNG DỤNG HỌC TIẾNG NHẬT - SAKURA FLASHCARD

---

## MỤC LỤC

1. [PHẦN 1: TỔNG QUAN](#phần-1-tổng-quan)
   - 1.1 Lời mở đầu
   - 1.2 Giới thiệu đề tài
   - 1.3 Công nghệ sử dụng

2. [PHẦN 2: PHÂN TÍCH VÀ THIẾT KẾ](#phần-2-phân-tích-và-thiết-kế)
   - 2.1 Phân tích yêu cầu
   - 2.2 Use Case Diagram
   - 2.3 Thiết kế cơ sở dữ liệu
   - 2.4 Thiết kế API

3. [PHẦN 3: TRIỂN KHAI](#phần-3-triển-khai)
   - 3.1 Kiến trúc hệ thống
   - 3.2 Các chức năng chính
   - 3.3 Bảo mật

4. [PHẦN 4: KẾT QUẢ VÀ ĐÁNH GIÁ](#phần-4-kết-quả-và-đánh-giá)
   - 4.1 Kết quả đạt được
   - 4.2 Đánh giá
   - 4.3 Hướng phát triển
   - 4.4 Kết luận

5. [TÀI LIỆU THAM KHẢO](#tài-liệu-tham-khảo)

---

# PHẦN 1: TỔNG QUAN

## 1.1 Lời Mở Đầu

### 1.1.1 Lý do chọn đề tài

Trong bối cảnh toàn cầu hóa ngày càng sâu rộng, việc học ngoại ngữ trở thành nhu cầu thiết yếu của nhiều người. Tiếng Nhật là một trong những ngôn ngữ phổ biến được lựa chọn bởi:

- **Nhu cầu việc làm**: Nhiều doanh nghiệp Nhật Bản đầu tư vào Việt Nam, tạo ra nhu cầu lớn về nhân lực biết tiếng Nhật
- **Du học**: Nhật Bản là điểm đến du học hấp dẫn với nhiều học bổng chính phủ
- **Văn hóa**: Sự phổ biến của anime, manga, và văn hóa Nhật Bản thu hút giới trẻ

Tuy nhiên, việc học tiếng Nhật gặp nhiều thách thức:
- Hệ thống chữ viết phức tạp (Hiragana, Katakana, Kanji)
- Khối lượng từ vựng lớn cần ghi nhớ
- Thiếu môi trường thực hành

Phương pháp **Flashcard** đã được chứng minh là hiệu quả trong việc ghi nhớ từ vựng thông qua nguyên lý **Spaced Repetition** (lặp lại ngắt quãng). Kết hợp với công nghệ di động, chúng tôi phát triển **Sakura Flashcard** - ứng dụng học tiếng Nhật thông minh, giúp người dùng học tập mọi lúc, mọi nơi.

### 1.1.2 Mục tiêu của đề tài

**Mục tiêu chính:**
- Xây dựng ứng dụng di động hỗ trợ học tiếng Nhật theo chuẩn JLPT (N5 - N1)
- Áp dụng phương pháp Flashcard và Spaced Repetition
- Cung cấp hệ thống kiểm tra, đánh giá tiến độ học tập

**Mục tiêu cụ thể:**
1. Phát triển ứng dụng Android với giao diện thân thiện, dễ sử dụng
2. Xây dựng backend API phục vụ đồng bộ dữ liệu và quản lý người dùng
3. Thiết kế cơ sở dữ liệu lưu trữ từ vựng theo cấp độ JLPT
4. Tích hợp chức năng quiz và theo dõi tiến độ học tập
5. Đảm bảo bảo mật dữ liệu người dùng

---

## 1.2 Giới Thiệu Đề Tài

### 1.2.1 Tên ứng dụng
**Sakura Flashcard** (桜フラッシュカード)

### 1.2.2 Ý nghĩa tên gọi
- **Sakura (桜)**: Hoa anh đào - biểu tượng của Nhật Bản, tượng trưng cho sự đẹp đẽ và nỗ lực
- **Flashcard**: Thẻ ghi nhớ - phương pháp học tập hiệu quả

### 1.2.3 Đối tượng người dùng
- Học sinh, sinh viên chuẩn bị thi JLPT
- Người đi làm muốn học tiếng Nhật để phát triển sự nghiệp
- Người yêu thích văn hóa Nhật Bản
- Người chuẩn bị du học, du lịch Nhật Bản

### 1.2.4 Phạm vi đề tài

**Bao gồm:**
- Ứng dụng Android cho người học
- Backend API server
- Admin Dashboard quản trị
- Cơ sở dữ liệu từ vựng và quiz theo JLPT

**Không bao gồm:**
- Ứng dụng iOS
- Tính năng nhận diện giọng nói
- Tính năng chat với AI

---

## 1.3 Công Nghệ Sử Dụng

### 1.3.1 Ứng dụng Android (Mobile App)

| Công nghệ | Mô tả | Phiên bản |
|-----------|-------|-----------|
| **Kotlin** | Ngôn ngữ lập trình chính | 1.9.x |
| **Jetpack Compose** | UI toolkit hiện đại | BOM 2024.x |
| **Hilt** | Dependency Injection | 2.48+ |
| **Room** | Local Database ORM | 2.6.x |
| **Retrofit** | HTTP Client | 2.9.x |
| **Coroutines** | Asynchronous programming | 1.7.x |
| **Navigation Compose** | Điều hướng màn hình | 2.7.x |

**Kiến trúc:** Clean Architecture + MVVM

### 1.3.2 Backend Server

| Công nghệ | Mô tả | Phiên bản |
|-----------|-------|-----------|
| **Node.js** | Runtime environment | 18.x LTS |
| **Express.js** | Web framework | 4.x |
| **TypeScript** | Strongly typed JavaScript | 5.x |
| **MongoDB** | NoSQL Database | Atlas |
| **Mongoose** | ODM for MongoDB | 8.x |
| **JWT** | Authentication | jsonwebtoken 9.x |

### 1.3.3 Bảo mật

| Thành phần | Công nghệ |
|------------|-----------|
| Password Hashing | bcryptjs |
| Token Authentication | JWT (Access + Refresh) |
| API Protection | Rate Limiting, Helmet |
| Input Validation | express-validator |
| XSS Prevention | xss-clean |
| NoSQL Injection | express-mongo-sanitize |
| Database Encryption | SQLCipher (Android) |
| Code Obfuscation | ProGuard/R8 |

### 1.3.4 Công cụ phát triển

| Công cụ | Mục đích |
|---------|----------|
| Android Studio | Phát triển ứng dụng Android |
| Visual Studio Code | Phát triển Backend |
| MongoDB Compass | Quản lý database |
| Postman | Test API |
| Git/GitHub | Version control |

---

# PHẦN 2: PHÂN TÍCH VÀ THIẾT KẾ

## 2.1 Phân Tích Yêu Cầu

### 2.1.1 Yêu cầu chức năng (Functional Requirements)

#### A. Chức năng người dùng (User)

| STT | Mã | Chức năng | Mô tả |
|-----|-----|-----------|-------|
| 1 | FR01 | Đăng ký tài khoản | Đăng ký bằng email hoặc Google |
| 2 | FR02 | Đăng nhập | Đăng nhập với email/password hoặc Google |
| 3 | FR03 | Xác thực OTP | Xác thực email qua mã OTP |
| 4 | FR04 | Quên mật khẩu | Đặt lại mật khẩu qua email |
| 5 | FR05 | Học Flashcard | Xem thẻ từ vựng với nghĩa và ví dụ |
| 6 | FR06 | Học Hiragana/Katakana | Bảng chữ cái với cách viết và phát âm |
| 7 | FR07 | Làm Quiz | Kiểm tra kiến thức với nhiều dạng câu hỏi |
| 8 | FR08 | Xem tiến độ | Theo dõi tiến độ học tập, điểm số |
| 9 | FR09 | Chỉnh sửa profile | Cập nhật thông tin cá nhân |
| 10 | FR10 | Đăng xuất | Đăng xuất khỏi ứng dụng |

#### B. Chức năng quản trị (Admin)

| STT | Mã | Chức năng | Mô tả |
|-----|-----|-----------|-------|
| 1 | FR11 | Xem thống kê | Dashboard tổng quan hệ thống |
| 2 | FR12 | Quản lý người dùng | CRUD, khóa/mở khóa tài khoản |
| 3 | FR13 | Quản lý từ vựng | Thêm/sửa/xóa từ vựng |
| 4 | FR14 | Quản lý Quiz | Xem và quản lý bộ đề quiz |

### 2.1.2 Yêu cầu phi chức năng (Non-functional Requirements)

| STT | Mã | Yêu cầu | Mô tả |
|-----|-----|---------|-------|
| 1 | NFR01 | Hiệu năng | Thời gian phản hồi < 2 giây |
| 2 | NFR02 | Bảo mật | Mã hóa mật khẩu, JWT authentication |
| 3 | NFR03 | Khả dụng | Uptime > 99% |
| 4 | NFR04 | Khả mở rộng | Hỗ trợ nhiều người dùng đồng thời |
| 5 | NFR05 | Tương thích | Android 12+ (API 31+) |
| 6 | NFR06 | Giao diện | Thân thiện, dễ sử dụng |
| 7 | NFR07 | Đa ngôn ngữ | Hỗ trợ Tiếng Việt |

---

## 2.2 Use Case Diagram

### 2.2.1 Actors (Tác nhân)

| Actor | Mô tả |
|-------|-------|
| **User** | Người dùng học tiếng Nhật |
| **Admin** | Quản trị viên hệ thống |
| **System** | Hệ thống tự động (gửi email, xử lý) |

### 2.2.2 Use Case Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        SAKURA FLASHCARD                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────┐                                          ┌──────────┐ │
│  │ User │                                          │  Admin   │ │
│  └──┬───┘                                          └────┬─────┘ │
│     │                                                   │       │
│     ├──► (Đăng ký/Đăng nhập)                           │       │
│     │         │                                         │       │
│     │         └──► (Xác thực OTP) ◄── [System]         │       │
│     │                                                   │       │
│     ├──► (Học Flashcard)                               │       │
│     │         ├── Học từ vựng                          │       │
│     │         └── Học Hiragana/Katakana                │       │
│     │                                                   │       │
│     ├──► (Làm Quiz)                                    │       │
│     │         ├── Chọn chủ đề                          │       │
│     │         └── Xem kết quả                          │       │
│     │                                                   │       │
│     ├──► (Xem tiến độ)                                 │       │
│     │                                                   │       │
│     └──► (Quản lý Profile)                             │       │
│                                                         │       │
│                              ┌──────────────────────────┤       │
│                              │                          │       │
│                              ├──► (Xem Dashboard)      │       │
│                              ├──► (Quản lý User)       │       │
│                              ├──► (Quản lý Vocabulary) │       │
│                              └──► (Quản lý Quiz)       │       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2.3 Đặc tả Use Case chính

#### UC01: Đăng ký tài khoản

| Thành phần | Mô tả |
|------------|-------|
| **Use Case ID** | UC01 |
| **Tên** | Đăng ký tài khoản |
| **Actor** | User |
| **Mô tả** | Người dùng tạo tài khoản mới |
| **Tiền điều kiện** | Chưa có tài khoản |
| **Luồng chính** | 1. User nhập email, password, username<br>2. System validate thông tin<br>3. System gửi OTP qua email<br>4. User nhập OTP xác thực<br>5. System tạo tài khoản |
| **Luồng thay thế** | 1a. Đăng ký bằng Google Account |
| **Hậu điều kiện** | Tài khoản được tạo thành công |

#### UC02: Học Flashcard

| Thành phần | Mô tả |
|------------|-------|
| **Use Case ID** | UC02 |
| **Tên** | Học Flashcard |
| **Actor** | User |
| **Mô tả** | Người dùng học từ vựng qua flashcard |
| **Tiền điều kiện** | Đã đăng nhập |
| **Luồng chính** | 1. User chọn cấp độ JLPT<br>2. User chọn chủ đề<br>3. System hiển thị flashcard<br>4. User lật thẻ xem nghĩa<br>5. User đánh giá mức độ nhớ |
| **Hậu điều kiện** | Tiến độ học được cập nhật |

---

## 2.3 Thiết Kế Cơ Sở Dữ Liệu

### 2.3.1 Mô hình dữ liệu (MongoDB Collections)

#### A. Collection: users

```javascript
{
  _id: ObjectId,
  email: String,           // unique, required
  password: String,        // hashed
  username: String,        // unique
  displayName: String,
  avatar: String,          // URL
  role: String,            // 'user' | 'admin'
  level: Number,           // 1-100
  currentLevel: String,    // 'N5' | 'N4' | 'N3' | 'N2' | 'N1'
  totalPoints: Number,
  isActive: Boolean,
  isLocked: Boolean,
  isVerified: Boolean,
  googleId: String,        // for Google OAuth
  refreshToken: String,
  createdAt: Date,
  updatedAt: Date
}
```

#### B. Collection: vocabularies

```javascript
{
  _id: ObjectId,
  word: {
    japanese: String,      // Kanji/Hiragana
    reading: String,       // Furigana
    meaning: String        // Vietnamese meaning
  },
  details: {
    partOfSpeech: String,  // noun, verb, adj...
    examples: [{
      japanese: String,
      meaning: String
    }],
    notes: String
  },
  topic: String,           // daily_life, travel, business...
  level: String,           // N5, N4, N3, N2, N1
  order: Number,
  isActive: Boolean,
  createdAt: Date,
  updatedAt: Date
}
```

#### C. Collection: quiz_sets

```javascript
{
  _id: ObjectId,
  topic: String,
  level: String,
  questions: [{
    question: String,
    questionType: String,  // multiple_choice, true_false, fill_blank
    options: [String],
    correctAnswer: String,
    explanation: String,
    points: Number
  }],
  totalQuestions: Number,
  totalPoints: Number,
  isActive: Boolean,
  createdAt: Date,
  updatedAt: Date
}
```

#### D. Collection: user_progress

```javascript
{
  _id: ObjectId,
  userId: ObjectId,        // ref: users
  quizSetId: ObjectId,     // ref: quiz_sets
  score: Number,
  totalPoints: Number,
  correctAnswers: Number,
  totalQuestions: Number,
  completedAt: Date
}
```

### 2.3.2 Entity Relationship Diagram (ERD)

```
┌─────────────────┐       ┌─────────────────┐
│     USERS       │       │  VOCABULARIES   │
├─────────────────┤       ├─────────────────┤
│ _id (PK)        │       │ _id (PK)        │
│ email           │       │ word            │
│ password        │       │ details         │
│ username        │       │ topic           │
│ displayName     │       │ level           │
│ level           │       │ order           │
│ totalPoints     │       │ isActive        │
│ ...             │       └─────────────────┘
└────────┬────────┘
         │
         │ 1:N
         ▼
┌─────────────────┐       ┌─────────────────┐
│ USER_PROGRESS   │       │   QUIZ_SETS     │
├─────────────────┤       ├─────────────────┤
│ _id (PK)        │       │ _id (PK)        │
│ userId (FK)     │◄──────│ topic           │
│ quizSetId (FK)  │───────►│ level           │
│ score           │       │ questions[]     │
│ completedAt     │       │ totalPoints     │
└─────────────────┘       └─────────────────┘
```

---

## 2.4 Thiết Kế API

### 2.4.1 Tổng quan API

**Base URL:** `http://localhost:3000/api`

**Authentication:** Bearer Token (JWT)

### 2.4.2 API Endpoints

#### A. Authentication APIs

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| POST | `/auth/register` | Đăng ký tài khoản | No |
| POST | `/auth/verify-otp` | Xác thực OTP | No |
| POST | `/auth/login` | Đăng nhập | No |
| POST | `/auth/google` | Đăng nhập Google | No |
| POST | `/auth/refresh-token` | Làm mới token | No |
| POST | `/auth/forgot-password` | Quên mật khẩu | No |
| POST | `/auth/reset-password` | Đặt lại mật khẩu | No |
| GET | `/auth/profile` | Lấy thông tin profile | Yes |
| PUT | `/auth/profile` | Cập nhật profile | Yes |
| POST | `/auth/logout` | Đăng xuất | Yes |

#### B. Vocabulary APIs

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/vocabularies` | Lấy danh sách từ vựng | Yes |
| GET | `/vocabularies/:id` | Lấy chi tiết từ vựng | Yes |
| GET | `/vocabularies/topics` | Lấy danh sách chủ đề | Yes |
| GET | `/vocabularies/levels` | Lấy danh sách level | Yes |

#### C. Quiz APIs

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/quizzes` | Lấy danh sách quiz | Yes |
| GET | `/quizzes/:id` | Lấy chi tiết quiz | Yes |
| POST | `/quizzes/:id/submit` | Nộp bài quiz | Yes |
| GET | `/quizzes/history` | Lịch sử làm quiz | Yes |

#### D. Admin APIs

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/admin/stats` | Thống kê tổng quan | Admin |
| GET | `/admin/users` | Danh sách users | Admin |
| PUT | `/admin/users/:id` | Cập nhật user | Admin |
| DELETE | `/admin/users/:id` | Xóa user | Admin |
| POST | `/admin/vocabularies` | Thêm từ vựng | Admin |
| PUT | `/admin/vocabularies/:id` | Sửa từ vựng | Admin |
| DELETE | `/admin/vocabularies/:id` | Xóa từ vựng | Admin |

### 2.4.3 Response Format

```javascript
// Success Response
{
  "success": true,
  "data": { ... },
  "message": "Success message"
}

// Error Response
{
  "success": false,
  "message": "Error message",
  "errors": [ ... ]  // Optional validation errors
}
```

---

# PHẦN 3: TRIỂN KHAI

## 3.1 Kiến Trúc Hệ Thống

### 3.1.1 Kiến trúc tổng quan

```
┌─────────────────────────────────────────────────────────────┐
│                      CLIENT LAYER                            │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐         ┌──────────────────────────┐  │
│  │   Android App    │         │    Admin Dashboard       │  │
│  │  (Kotlin/Compose)│         │     (HTML/JS/CSS)        │  │
│  └────────┬─────────┘         └────────────┬─────────────┘  │
│           │                                │                 │
│           └────────────┬───────────────────┘                 │
│                        │                                     │
│                        ▼                                     │
│           ┌────────────────────────┐                        │
│           │      REST API          │                        │
│           │   (HTTPS/JSON)         │                        │
│           └────────────┬───────────┘                        │
└────────────────────────┼────────────────────────────────────┘
                         │
┌────────────────────────┼────────────────────────────────────┐
│                        ▼                                     │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                 BACKEND SERVER                       │    │
│  │              (Node.js + Express)                     │    │
│  ├─────────────────────────────────────────────────────┤    │
│  │  ┌─────────┐  ┌──────────┐  ┌──────────────────┐   │    │
│  │  │ Routes  │──│Controllers│──│    Services      │   │    │
│  │  └─────────┘  └──────────┘  └────────┬─────────┘   │    │
│  │                                       │             │    │
│  │  ┌─────────────────────────────────────────────┐   │    │
│  │  │              MIDDLEWARES                     │   │    │
│  │  │  Auth │ Rate Limit │ Validation │ Security  │   │    │
│  │  └─────────────────────────────────────────────┘   │    │
│  └─────────────────────────┬───────────────────────────┘    │
│                            │                                 │
│                            ▼                                 │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                  DATABASE LAYER                      │    │
│  │               (MongoDB Atlas)                        │    │
│  │  ┌─────────┐ ┌────────────┐ ┌──────────────────┐   │    │
│  │  │  Users  │ │Vocabularies│ │    QuizSets      │   │    │
│  │  └─────────┘ └────────────┘ └──────────────────┘   │    │
│  └─────────────────────────────────────────────────────┘    │
│                        SERVER LAYER                          │
└─────────────────────────────────────────────────────────────┘
```

### 3.1.2 Kiến trúc Android App (Clean Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    UI (Compose)                      │    │
│  │   Screens │ Components │ Theme │ Navigation         │    │
│  └───────────────────────┬─────────────────────────────┘    │
│                          ▼                                   │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                   ViewModels                         │    │
│  │   LoginVM │ HomeVM │ LearnVM │ QuizVM │ ProfileVM   │    │
│  └───────────────────────┬─────────────────────────────┘    │
├──────────────────────────┼──────────────────────────────────┤
│                          ▼                                   │
│                      DOMAIN LAYER                            │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                   Use Cases                          │    │
│  │   Login │ Register │ GetVocabs │ SubmitQuiz │ ...   │    │
│  └───────────────────────┬─────────────────────────────┘    │
│                          ▼                                   │
│  ┌─────────────────────────────────────────────────────┐    │
│  │               Repository Interfaces                  │    │
│  └───────────────────────┬─────────────────────────────┘    │
├──────────────────────────┼──────────────────────────────────┤
│                          ▼                                   │
│                       DATA LAYER                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Repository Implementations              │    │
│  └────────────┬────────────────────────┬───────────────┘    │
│               ▼                        ▼                     │
│  ┌────────────────────┐    ┌────────────────────────┐       │
│  │   Remote (API)     │    │    Local (Room DB)     │       │
│  │   Retrofit/OkHttp  │    │    SQLCipher           │       │
│  └────────────────────┘    └────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### 3.1.3 Cấu trúc thư mục

#### Android App
```
app/src/main/java/com/example/sakura_flashcard/
├── data/
│   ├── api/                 # Retrofit interfaces
│   ├── local/               # Room database
│   ├── model/               # Data models
│   └── repository/          # Repository implementations
├── di/                      # Hilt modules
├── ui/
│   ├── components/          # Reusable UI components
│   ├── navigation/          # Navigation setup
│   ├── screens/             # Screen composables
│   └── theme/               # Material theme
├── util/                    # Utilities
└── SakuraApplication.kt     # Application class
```

#### Backend
```
sakura-backend/src/
├── controllers/             # Request handlers
├── middlewares/             # Express middlewares
├── models/                  # Mongoose schemas
├── routes/                  # API routes
├── utils/                   # Utilities
└── app.ts                   # Entry point
```

---

## 3.2 Các Chức Năng Chính

### 3.2.1 Đăng ký và Đăng nhập

#### Luồng đăng ký với OTP

```
┌────────┐         ┌────────┐         ┌────────┐         ┌────────┐
│  User  │         │  App   │         │ Server │         │ Email  │
└───┬────┘         └───┬────┘         └───┬────┘         └───┬────┘
    │                  │                   │                  │
    │  Nhập thông tin  │                   │                  │
    │─────────────────►│                   │                  │
    │                  │   POST /register  │                  │
    │                  │──────────────────►│                  │
    │                  │                   │   Send OTP       │
    │                  │                   │─────────────────►│
    │                  │   201 Created     │                  │
    │                  │◄──────────────────│                  │
    │  Hiển thị OTP    │                   │                  │
    │◄─────────────────│                   │                  │
    │                  │                   │                  │
    │  Nhập OTP        │                   │                  │
    │─────────────────►│                   │                  │
    │                  │  POST /verify-otp │                  │
    │                  │──────────────────►│                  │
    │                  │   200 Success     │                  │
    │                  │◄──────────────────│                  │
    │  Đăng ký thành   │                   │                  │
    │◄─────────────────│                   │                  │
    │  công            │                   │                  │
```

#### Code xử lý đăng ký (Backend)

```typescript
export const register = async (req: Request, res: Response) => {
    const { email, password, username } = req.body;

    // Check existing user
    const existingUser = await User.findOne({ 
        $or: [{ email }, { username }] 
    });
    if (existingUser) {
        return errorResponse(res, 'Email hoặc username đã tồn tại', 400);
    }

    // Hash password
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    // Generate OTP
    const otp = generateOTP();
    const otpExpires = new Date(Date.now() + 10 * 60 * 1000); // 10 minutes

    // Create user
    const user = new User({
        email,
        password: hashedPassword,
        username,
        otp: { code: otp, expiresAt: otpExpires }
    });
    await user.save();

    // Send OTP email
    await sendOTPEmail(email, otp);

    return successResponse(res, null, 'Đăng ký thành công. Vui lòng kiểm tra email');
};
```

### 3.2.2 Học Flashcard

Ứng dụng cung cấp 3 loại thẻ học:
- **Hiragana**: Bảng chữ cái cơ bản (46 ký tự)
- **Katakana**: Bảng chữ cái cho từ ngoại lai (46 ký tự)
- **Kanji**: Chữ Hán theo cấp độ JLPT

#### Giao diện Flashcard

```
┌─────────────────────────────────────┐
│           FLASHCARD                  │
├─────────────────────────────────────┤
│                                      │
│              学生                    │
│           (がくせい)                 │
│                                      │
│         ──────────────               │
│                                      │
│          Học sinh                    │
│          Student                     │
│                                      │
│  ┌─────────────────────────────┐    │
│  │  Ví dụ: 私は学生です。       │    │
│  │  (Watashi wa gakusei desu)  │    │
│  │  Tôi là học sinh.           │    │
│  └─────────────────────────────┘    │
│                                      │
│      [<< Trước]    [Tiếp >>]        │
└─────────────────────────────────────┘
```

### 3.2.3 Quiz

Hệ thống quiz gồm nhiều dạng câu hỏi:
- **Multiple Choice**: Chọn 1 đáp án đúng
- **True/False**: Đúng hoặc sai
- **Fill in the blank**: Điền vào chỗ trống

#### Tính điểm theo level JLPT

| Level | Điểm/câu | Độ khó |
|-------|----------|--------|
| N5 | 10 điểm | Cơ bản |
| N4 | 10 điểm | Sơ cấp |
| N3 | 15 điểm | Trung cấp |
| N2 | 20 điểm | Cao cấp |
| N1 | 25 điểm | Nâng cao |

---

## 3.3 Bảo Mật

### 3.3.1 Tổng quan bảo mật

```
┌─────────────────────────────────────────────────────────────┐
│                    SECURITY LAYERS                           │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │  TRANSPORT LAYER                                    │     │
│  │  • HTTPS (TLS 1.2/1.3)                             │     │
│  │  • Certificate validation                           │     │
│  └────────────────────────────────────────────────────┘     │
│                          ▼                                   │
│  ┌────────────────────────────────────────────────────┐     │
│  │  API LAYER                                          │     │
│  │  • Rate Limiting (100 req/15min)                   │     │
│  │  • JWT Authentication                               │     │
│  │  • Request Validation                               │     │
│  └────────────────────────────────────────────────────┘     │
│                          ▼                                   │
│  ┌────────────────────────────────────────────────────┐     │
│  │  APPLICATION LAYER                                  │     │
│  │  • Input Sanitization (XSS, NoSQL Injection)       │     │
│  │  • Password Hashing (bcrypt)                        │     │
│  │  • Role-based Access Control                        │     │
│  └────────────────────────────────────────────────────┘     │
│                          ▼                                   │
│  ┌────────────────────────────────────────────────────┐     │
│  │  DATA LAYER                                         │     │
│  │  • Database Encryption (SQLCipher)                 │     │
│  │  • Secure Token Storage                             │     │
│  └────────────────────────────────────────────────────┘     │
│                          ▼                                   │
│  ┌────────────────────────────────────────────────────┐     │
│  │  CODE LAYER                                         │     │
│  │  • ProGuard/R8 Obfuscation                         │     │
│  │  • Anti-Debug Protection                            │     │
│  │  • Root Detection                                   │     │
│  └────────────────────────────────────────────────────┘     │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 3.3.2 JWT Authentication

#### Token Flow

```
┌────────┐                              ┌────────┐
│ Client │                              │ Server │
└───┬────┘                              └───┬────┘
    │                                       │
    │  1. Login (email, password)           │
    │──────────────────────────────────────►│
    │                                       │
    │  2. Access Token + Refresh Token      │
    │◄──────────────────────────────────────│
    │                                       │
    │  3. API Request + Access Token        │
    │──────────────────────────────────────►│
    │                                       │
    │  4. Response Data                     │
    │◄──────────────────────────────────────│
    │                                       │
    │  5. Access Token Expired              │
    │──────────────────────────────────────►│
    │                                       │
    │  6. 401 Unauthorized                  │
    │◄──────────────────────────────────────│
    │                                       │
    │  7. Refresh Token Request             │
    │──────────────────────────────────────►│
    │                                       │
    │  8. New Access Token                  │
    │◄──────────────────────────────────────│
```

#### Token Configuration

| Token | Thời hạn | Mục đích |
|-------|----------|----------|
| Access Token | 15 phút | Xác thực API request |
| Refresh Token | 7 ngày | Lấy access token mới |

### 3.3.3 Rate Limiting

| Endpoint | Giới hạn | Thời gian |
|----------|----------|-----------|
| General API | 100 requests | 15 phút |
| Login | 5 attempts | 15 phút |
| OTP | 3 requests | 5 phút |
| Password Reset | 3 requests | 1 giờ |

### 3.3.4 Anti-Tampering (Android)

```kotlin
object AntiTamper {
    fun isCompromised(context: Context): Boolean {
        return isDebugged() || 
               isRooted() || 
               isEmulator() ||
               isDebuggable(context)
    }

    fun isDebugged(): Boolean {
        return Debug.isDebuggerConnected()
    }

    fun isRooted(): Boolean {
        val suPaths = arrayOf("/system/bin/su", "/system/xbin/su", ...)
        return suPaths.any { File(it).exists() }
    }
}
```

---

# PHẦN 4: KẾT QUẢ VÀ ĐÁNH GIÁ

## 4.1 Kết Quả Đạt Được

### 4.1.1 Các chức năng hoàn thành

| STT | Chức năng | Trạng thái |
|-----|-----------|------------|
| 1 | Đăng ký/Đăng nhập | ✅ Hoàn thành |
| 2 | Xác thực OTP | ✅ Hoàn thành |
| 3 | Đăng nhập Google | ✅ Hoàn thành |
| 4 | Học Hiragana/Katakana | ✅ Hoàn thành |
| 5 | Học từ vựng Flashcard | ✅ Hoàn thành |
| 6 | Quiz JLPT | ✅ Hoàn thành |
| 7 | Theo dõi tiến độ | ✅ Hoàn thành |
| 8 | Profile management | ✅ Hoàn thành |
| 9 | Admin Dashboard | ✅ Hoàn thành |
| 10 | Bảo mật | ✅ Hoàn thành |

### 4.1.2 Thống kê dữ liệu

| Loại dữ liệu | Số lượng |
|--------------|----------|
| Từ vựng | 1000+ từ |
| Quiz sets | 70 bộ (14 chủ đề x 5 level) |
| Chủ đề từ vựng | 14 chủ đề |
| Cấp độ JLPT | 5 (N5-N1) |

---

## 4.2 Đánh Giá

### 4.2.1 Ưu điểm

1. **Giao diện hiện đại**: Sử dụng Jetpack Compose với Material Design 3
2. **Bảo mật cao**: Đầy đủ các cơ chế bảo vệ từ transport đến code level
3. **Dữ liệu phong phú**: Từ vựng theo chuẩn JLPT, đa dạng chủ đề
4. **Kiến trúc tốt**: Clean Architecture, dễ bảo trì và mở rộng
5. **Đa nền tảng**: Android app + Web admin dashboard

### 4.2.2 Hạn chế

1. Chưa có ứng dụng iOS
2. Chưa có tính năng nhận diện giọng nói
3. Chưa có tính năng học offline hoàn chỉnh
4. Chưa có gamification (huy hiệu, bảng xếp hạng)

---

## 4.3 Hướng Phát Triển

### Ngắn hạn (3-6 tháng)
- [ ] Thêm tính năng học offline
- [ ] Thêm notification nhắc học
- [ ] Cải thiện UI/UX

### Trung hạn (6-12 tháng)
- [ ] Phát triển iOS app
- [ ] Thêm Speech Recognition
- [ ] Thêm gamification

### Dài hạn (1-2 năm)
- [ ] AI-powered learning path
- [ ] Social features (nhóm học)
- [ ] Premium subscription

---

## 4.4 Kết Luận

Đồ án **Sakura Flashcard** đã hoàn thành các mục tiêu đề ra:

✅ Xây dựng thành công ứng dụng học tiếng Nhật trên Android

✅ Phát triển backend API với đầy đủ chức năng

✅ Thiết kế cơ sở dữ liệu từ vựng theo chuẩn JLPT

✅ Tích hợp hệ thống quiz và theo dõi tiến độ

✅ Đảm bảo bảo mật với nhiều lớp bảo vệ

Ứng dụng có thể đáp ứng nhu cầu học tiếng Nhật cơ bản cho người dùng, với giao diện thân thiện và nội dung phong phú. Nhóm sẽ tiếp tục phát triển thêm các tính năng mới trong tương lai.

---

# TÀI LIỆU THAM KHẢO

1. Android Developers Documentation. (2024). Jetpack Compose. https://developer.android.com/jetpack/compose

2. MongoDB Documentation. (2024). MongoDB Manual. https://www.mongodb.com/docs/manual/

3. Express.js Documentation. (2024). Express.js Guide. https://expressjs.com/

4. JLPT Official Website. (2024). Japanese-Language Proficiency Test. https://www.jlpt.jp/

5. OWASP. (2024). OWASP Top 10. https://owasp.org/Top10/

6. Material Design. (2024). Material Design 3. https://m3.material.io/

---

*Báo cáo được hoàn thành bởi nhóm phát triển Sakura Flashcard*
