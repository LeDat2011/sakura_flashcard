# Hướng Dẫn Sử Dụng Ứng Dụng Japanese Flashcard

## Tổng Quan Ứng Dụng

**Japanese Flashcard** là một ứng dụng Android học tiếng Nhật thông minh với các tính năng:
- 📚 Học từ vựng qua flashcard với thuật toán spaced repetition
- 🔤 Học 3 hệ thống chữ viết: Hiragana, Katakana, Kanji
- 📝 Làm quiz theo cấp độ JLPT (N5-N1)
- 🎮 Mini-games học tập tương tác
- 📊 Theo dõi tiến độ học tập chi tiết
- 🔄 Đồng bộ dữ liệu online/offline

---

## 🛠️ Yêu Cầu Hệ Thống

### Môi Trường Phát Triển
- **Android Studio**: Phiên bản mới nhất (Hedgehog 2023.1.1+)
- **JDK**: Java 17 hoặc cao hơn
- **Android SDK**: API Level 24+ (Android 7.0)
- **Kotlin**: 1.9.0+
- **Gradle**: 8.0+

### Thiết Bị Chạy Ứng Dụng
- **Android**: 7.0 (API 24) trở lên
- **RAM**: Tối thiểu 2GB
- **Dung lượng**: 100MB trống

---

## 📦 Cài Đặt Môi Trường

### 1. Cài Đặt Android Studio
```bash
# Tải từ: https://developer.android.com/studio
# Cài đặt theo hướng dẫn của Google
```

### 2. Cài Đặt JDK 17
```bash
# Windows (sử dụng Chocolatey)
choco install openjdk17

# macOS (sử dụng Homebrew)
brew install openjdk@17

# Ubuntu/Debian
sudo apt install openjdk-17-jdk
```

### 3. Thiết Lập Biến Môi Trường
```bash
# Windows
set JAVA_HOME=C:\Program Files\OpenJDK\openjdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

# macOS/Linux
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

---

## 🚀 Cách Chạy Ứng Dụng

### Bước 1: Clone Project
```bash
git clone <repository-url>
cd sakura_flashcard
```

### Bước 2: Cấu Hình API Keys
Tạo file `local.properties` trong thư mục gốc:
```properties
# API Keys (tùy chọn - cho tính năng online)
GEMINI_API_KEY=your_gemini_api_key_here
MONGODB_CONNECTION_STRING=your_mongodb_connection_string

# Android SDK Path (tự động tạo bởi Android Studio)
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

### Bước 3: Mở Project trong Android Studio
1. Mở Android Studio
2. Chọn "Open an Existing Project"
3. Chọn thư mục `sakura_flashcard`
4. Đợi Gradle sync hoàn thành

### Bước 4: Thiết Lập Emulator hoặc Device
#### Sử dụng Emulator:
1. Mở AVD Manager trong Android Studio
2. Tạo Virtual Device mới
3. Chọn API Level 24+ (Android 7.0+)
4. Khởi động emulator

#### Sử dụng Thiết Bị Thật:
1. Bật "Developer Options" trên điện thoại
2. Bật "USB Debugging"
3. Kết nối điện thoại qua USB
4. Cho phép debugging khi có popup

### Bước 5: Build và Chạy
```bash
# Trong terminal của Android Studio hoặc cmd/terminal
./gradlew assembleDebug

# Hoặc nhấn nút "Run" (▶️) trong Android Studio
```

---

## 🗄️ Hệ Thống Database

### Cấu Trúc Database

#### 1. Local Database (Room/SQLite)
Ứng dụng sử dụng **Room Database** để lưu trữ dữ liệu offline:

```kotlin
// Các bảng chính:
- users              // Thông tin người dùng
- flashcards         // Thẻ từ vựng
- characters         // Ký tự Nhật Bản
- quiz_results       // Kết quả quiz
- game_results       // Kết quả mini-games
- spaced_repetition  // Dữ liệu thuật toán học lặp
- sync_operations    // Thao tác đồng bộ
```

#### 2. Remote Database (MongoDB - Tùy chọn)
Cho tính năng đồng bộ đám mây:
```javascript
// Collections:
- users              // Hồ sơ người dùng
- user_progress      // Tiến độ học tập
- flashcard_sets     // Bộ flashcard
- learning_analytics // Phân tích học tập
```

### Khởi Tạo Database

#### Database được tự động khởi tạo khi:
1. Lần đầu mở ứng dụng
2. Dữ liệu mẫu được tạo tự động
3. Không cần setup thủ công

#### Kiểm tra Database:
```bash
# Sử dụng Android Studio Database Inspector
# View > Tool Windows > Database Inspector
# Chọn device và app để xem database
```

---

## 🔐 Hệ Thống Đăng Nhập

### Cách Đăng Ký Tài Khoản
1. Mở ứng dụng lần đầu
2. Chọn "Đăng ký" (Register)
3. Nhập thông tin:
   - **Username**: 3-20 ký tự, chỉ chữ, số và _
   - **Email**: Định dạng email hợp lệ
   - **Password**: Tối thiểu 8 ký tự
4. Nhấn "Tạo tài khoản"

### Cách Đăng Nhập
1. Nhập email và password
2. Nhấn "Đăng nhập"
3. Hệ thống sẽ lưu phiên đăng nhập

### Quên Mật Khẩu
- Hiện tại: Chỉ hỗ trợ tạo tài khoản mới
- Tương lai: Sẽ có tính năng reset password qua email

---

## 📱 Cách Sử Dụng Ứng Dụng

### 1. Màn Hình Chính (Home)
- **Flashcard Carousel**: Xem flashcard được đề xuất
- **Tiến độ học tập**: Thống kê ngày hôm nay
- **Truy cập nhanh**: Đến các tính năng chính

### 2. Học Ký Tự (Learn)
- **3 Tab**: Hiragana, Katakana, Kanji
- **Grid Layout**: Hiển thị ký tự theo lưới
- **Chi tiết ký tự**: Nhấn vào ký tự để xem:
  - Cách viết (stroke order)
  - Phát âm
  - Ví dụ sử dụng
  - Animation viết ký tự

### 3. Quiz
- **Chọn chủ đề**: Anime, Food, Daily Life, v.v.
- **Chọn cấp độ**: N5 (dễ) đến N1 (khó)
- **3 loại câu hỏi**:
  - Multiple Choice (Trắc nghiệm)
  - Fill in the Blank (Điền từ)
  - True/False (Đúng/Sai)
- **Kết quả**: Điểm số, thời gian, phân tích

### 4. Mini Games
- **Sentence Order Puzzle**: Sắp xếp từ thành câu
- **Quick Answer Challenge**: Trả lời nhanh trong 5 giây
- **Memory Match Game**: Ghép thẻ từ vựng

### 5. Hồ Sơ (Profile)
- **Thống kê học tập**: Flashcard đã học, quiz hoàn thành
- **Tiến độ JLPT**: Theo từng cấp độ
- **Cài đặt**: Theme, ngôn ngữ, thông báo

---

## 🔧 Troubleshooting

### Lỗi Thường Gặp

#### 1. Build Failed
```bash
# Giải pháp:
./gradlew clean
./gradlew build

# Hoặc trong Android Studio:
Build > Clean Project
Build > Rebuild Project
```

#### 2. Emulator Không Khởi Động
```bash
# Kiểm tra:
- Bật Virtualization trong BIOS
- Cài đặt Intel HAXM (Windows)
- Tăng RAM cho emulator (4GB+)
```

#### 3. App Crash khi Mở
```bash
# Kiểm tra Logcat:
View > Tool Windows > Logcat
# Tìm lỗi màu đỏ và báo cáo
```

#### 4. Database Lỗi
```bash
# Reset database:
Settings > Apps > Japanese Flashcard > Storage > Clear Data
# Hoặc uninstall và cài lại app
```

### Logs và Debug

#### Xem Logs:
```bash
# Android Studio Logcat
adb logcat | grep "SakuraFlashcard"

# Hoặc filter trong Logcat window
```

#### Debug Mode:
```kotlin
// Trong app/build.gradle
android {
    buildTypes {
        debug {
            debuggable true
            minifyEnabled false
        }
    }
}
```

---

## 🧪 Chạy Tests

### Unit Tests
```bash
./gradlew test
```

### Integration Tests
```bash
./gradlew connectedAndroidTest
```

### Property-Based Tests
```bash
# Tests sử dụng Kotest framework
./gradlew testDebugUnitTest --tests "*PropertyTest*"
```

---

## 📊 Tính Năng Nâng Cao

### 1. Spaced Repetition Algorithm
- Dựa trên thuật toán SuperMemo SM-2
- Tự động điều chỉnh khoảng thời gian ôn tập
- Theo dõi độ khó của từng flashcard

### 2. Offline Mode
- Hoạt động hoàn toàn offline
- Đồng bộ khi có internet
- Lưu trữ local với Room Database

### 3. Analytics
- Theo dõi thời gian học
- Phân tích điểm mạnh/yếu
- Đề xuất nội dung học tiếp theo

### 4. Accessibility
- Hỗ trợ screen reader
- High contrast mode
- Keyboard navigation
- Font size adjustment

---

## 🔄 Cập Nhật và Bảo Trì

### Cập Nhật Dependencies
```bash
# Kiểm tra updates
./gradlew dependencyUpdates

# Update Gradle Wrapper
./gradlew wrapper --gradle-version=8.5
```

### Backup Dữ Liệu
```bash
# Export database (cần root)
adb shell su -c "cp /data/data/com.example.sakura_flashcard/databases/flashcard_db /sdcard/"
adb pull /sdcard/flashcard_db ./backup/
```

---

## 📞 Hỗ Trợ

### Báo Lỗi
1. Mô tả chi tiết lỗi
2. Đính kèm screenshot
3. Cung cấp device info
4. Logcat nếu có thể

### Đóng Góp
1. Fork repository
2. Tạo feature branch
3. Commit changes
4. Tạo Pull Request

---

## 📝 Ghi Chú Quan Trọng

### Hiệu Năng
- App được tối ưu cho thiết bị RAM 2GB+
- Sử dụng lazy loading cho danh sách lớn
- Image caching để giảm tải network

### Bảo Mật
- Mật khẩu được hash với bcrypt
- Token authentication cho API
- Dữ liệu local được mã hóa

### Tương Thích
- Hỗ trợ Android 7.0+ (API 24+)
- Tối ưu cho màn hình 5-7 inch
- Hỗ trợ cả portrait và landscape

---

**Chúc bạn học tiếng Nhật vui vẻ với Sakura Flashcard! 🌸**