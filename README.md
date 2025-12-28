# Sakura Flashcard 🌸

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-43853D?style=for-the-badge&logo=node.js&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)

Ứng dụng học tiếng Nhật toàn diện hỗ trợ ôn luyện từ vựng JLPT (N5-N1), Hiragana/Katakana qua Flashcards, Quiz và Game tương tác.

## ✨ Tính Năng Chính

### 📚 Học Từ Vựng (Flashcards)
- Hệ thống từ vựng phân theo cấp độ JLPT (N5 - N1)
- Chủ đề đa dạng: Đời sống, Công việc, Du lịch, Anime, v.v.
- **Spaced Repetition Algorithm (SM-2)** - tối ưu hóa việc ghi nhớ
- Phát âm chuẩn Nhật Bản (Text-to-Speech)
- **Hiển thị nét viết (Stroke Order)** cho Hiragana/Katakana

### 📝 Luyện Tập (Quiz Mode)
- Hàng trăm bộ câu hỏi trắc nghiệm theo chủ đề và trình độ
- Tính thời gian thực, chấm điểm và giải thích chi tiết
- Xem lại lịch sử làm bài và các câu trả lời sai

### 🎮 Mini Games
- **Memory Match:** Lật thẻ ghép cặp từ vựng
- **Sentence Order Puzzle:** Sắp xếp câu tiếng Nhật
- **Quick Answer Challenge:** Trả lời nhanh trong 5 giây

### 🔐 Bảo Mật
- **Xác thực Biometric** (Vân tay/Face ID)
- Đăng nhập qua Google OAuth 2.0
- JWT Access/Refresh Token
- Mã hóa dữ liệu nhạy cảm (AES-256)
- Quên mật khẩu qua Email OTP

### 📊 Thống Kê & Theo Dõi
- Biểu đồ tiến độ học tập hàng ngày/tuần
- Hệ thống streak và XP
- Đồng bộ hóa dữ liệu đám mây

### �️ Admin Dashboard (Web)
- Quản lý người dùng (khóa/mở khóa tài khoản)
- Quản lý nội dung từ vựng và bộ quiz
- Thống kê hệ thống

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

### Android Client
- **Ngôn ngữ:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt (Dagger)
- **Networking:** Retrofit + OkHttp
- **Asynchronous:** Coroutines + Flow
- **Navigation:** Jetpack Navigation Compose

### Backend Server
- **Runtime:** Node.js
- **Framework:** Express.js
- **Language:** TypeScript
- **Database:** MongoDB (Mongoose ORM)
- **Authentication:** JWT (Access & Refresh Tokens)

## 🚀 Cài Đặt & Chạy Dự Án (Getting Started)

### Yêu cầu tiên quyết (Prerequisites)
- Android Studio Ladybug (hoặc mới hơn)
- JDK 17+
- Node.js v18+
- Tài khoản MongoDB Atlas (hoặc MongoDB local)

### 1. Backend Setup
1. Truy cập thư mục backend:
   ```bash
   cd sakura-backend
   ```
2. Cài đặt dependencies:
   ```bash
   npm install
   ```
3. Tạo file `.env` từ `.env.example`:
   ```env
   PORT=3000
   MONGODB_URI=mongodb+srv://<username>:<password>@cluster.mongodb.net/sakura_db
   JWT_SECRET=your_super_secret_key_123
   JWT_REFRESH_SECRET=your_super_refresh_secret_456
   ```
4. Chạy server:
   ```bash
   npm run dev
   ```
   Server sẽ khởi động tại `http://localhost:3000`

### 2. Android App Setup
1. Mở thư mục gốc `sakura_flashcard` bằng Android Studio.
2. Mở file `app/src/main/java/com/example/sakura_flashcard/di/NetworkModule.kt`.
   - Nếu chạy trên Emulator: Sử dụng `http://10.0.2.2:3000/api/`
   - Nếu chạy trên thiết bị thật: Thay bằng địa chỉ IP LAN của máy tính (VD: `http://192.168.1.100:3000/api/`)
3. Sync Project với Gradle Files.
4. Nhấn **Run** (▶) để build và cài đặt lên thiết bị.

## 📂 Cấu Trúc Dự Án (Project Structure)

```
sakura_flashcard/
├── app/                        # Android Client Code
│   ├── src/main/java/com/example/sakura_flashcard/
│   │   ├── data/               # Data Layer (API, Models, Repositories)
│   │   ├── di/                 # Dependency Injection Modules
│   │   ├── navigation/         # Navigation Graph
│   │   ├── ui/                 # Presentation Layer (Active Screens, ViewModels)
│   │   │   ├── components/     # Reusable UI Components
│   │   │   ├── screens/        # Feature Screens
│   │   │   └── theme/          # App Theme & Colors
│   │   └── MainActivity.kt     # Entry Point
│   └── ...
├── sakura-backend/             # Backend API Code
│   ├── src/
│   │   ├── controllers/        # Request Handlers
│   │   ├── models/             # Mongoose Schemas
│   │   ├── routes/             # API Routes
│   │   ├── middlewares/        # Auth & Validation Middlewares
│   │   └── scripts/            # Seed Data Scripts
│   └── ...
└── README.md                   # Project Documentation
```

## 🤝 Đóng Góp (Contributing)
Mọi đóng góp đều được hoan nghênh! Vui lòng tạo Pull Request hoặc mở Issue để báo lỗi/đề xuất tính năng.

## 📜 License
Dự án này được phân phối dưới giấy phép [MIT](LICENSE).

---
**Developed by [LeDat2011](https://github.com/LeDat2011)** 🚀
