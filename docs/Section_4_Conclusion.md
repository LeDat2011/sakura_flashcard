PHẦN 4: KẾT QUẢ VÀ ĐÁNH GIÁ

4.1. Kết quả đạt được

Hệ thống Sakura Flashcard đã hoàn thành 100% các mục tiêu đề ra ban đầu, cung cấp một giải pháp học tập toàn diện cho người học tiếng Nhật. Các kết quả cụ thể bao gồm:

4.1.1. Về mặt chức năng ứng dụng di động
- Hệ thống xác thực an toàn: Triển khai thành công tính năng đăng ký, đăng nhập qua Email và Google OAuth. Đặc biệt, quy trình xác thực OTP qua email hoạt động ổn định, đảm bảo tính chính chủ của tài khoản.
- Học tập đa năng: Cung cấp đầy đủ các module học bảng chữ cái (Hiragana/Katakana), tra cứu và học từ vựng qua Flashcard sống động, cùng hệ thống Quiz đa dạng (trắc nghiệm, đúng/sai).
- Theo dõi tiến độ: Người dùng có thể xem lại lịch sử học tập, thống kê điểm số và theo dõi sự tiến bộ theo từng cấp độ JLPT (từ N5 đến N1).

4.1.2. Về mặt quản trị và dữ liệu
- Admin Dashboard: Xây dựng giao diện Web cho phép người quản trị theo dõi thống kê người dùng, quản lý kho dữ liệu từ vựng và quiz một cách hiệu trực quan.
- Kho dữ liệu phong phú: Hệ thống được nạp sẵn hơn 1000 từ vựng và 70 bộ đề Quiz, được phân loại khoa học theo 14 chủ đề thông dụng và 5 cấp độ JLPT.

4.1.3. Về mặt kỹ thuật và kiến trúc
- Kết hợp hoàn hảo giữa Kotlin Jetpack Compose (Modern UI) và Backend Node.js/Express, mang lại trải nghiệm mượt mà và khả năng phản hồi nhanh (dưới 1 giây cho phần lớn yêu cầu).
- Kiến trúc Clean Architecture giúp mã nguồn dễ bảo trì và mở rộng trong tương lai.

4.2. Đánh giá hệ thống

4.2.1. Ưu điểm
- Trải nghiệm người dùng: Giao diện Material Design 3 hiện đại, thân thiện, dễ tiếp cận với người mới bắt đầu học tiếng Nhật.
- Tính an ninh: Hệ thống được bảo vệ vững chắc qua 5 tầng bảo mật (Transport, API, Application, Data, Code), giúp ngăn chặn hầu hết các cuộc tấn công thông thường.
- Hiệu năng: Ứng dụng chạy mượt mà trên đa dạng thiết bị Android, dữ liệu được đồng bộ hóa tức thời giữa thiết bị di động và máy chủ.

4.2.2. Hạn chế
- Nền tảng: Hiện tại ứng dụng mới chỉ hỗ trợ hệ điều hành Android, chưa có phiên bản dành cho iOS.
- Chế độ offline: Việc học tập vẫn phụ thuộc nhiều vào kết nối internet để đồng bộ dữ liệu.
- Tính tương tác: Chưa tích hợp các tính năng cộng đồng như bảng xếp hạng (Leaderboard) hay thi đấu trực tuyến giữa người dùng.

4.3. Hướng phát triển trong tương lai

Để nâng cao giá trị cho người dùng, hệ thống sẽ được phát triển theo các định hướng sau:
- Nâng cấp tính năng học tập: Tích hợp công nghệ AI để gợi ý lộ trình học tập cá nhân hóa dựa trên kết quả Quiz; bổ sung tính năng nhận diện giọng nói (Speech-to-text) để luyện phát âm.
- Mở rộng nền tảng: Phát triển phiên bản web-app và ứng dụng iOS sử dụng Flutter hoặc React Native.
- Tính năng cộng đồng: Xây dựng hệ thống bảng xếp hạng, huy hiệu học tập (Gamification) để tăng tính gắn kết và động lực cho người học.
- Chế độ Offline: Cải thiện cơ chế lưu trữ nội bộ (Local Caching) để cho phép học tập ngay cả khi không có kết nối mạng.

KẾT LUẬN

Đồ án Sakura Flashcard không chỉ là một bài tập kỹ thuật mà còn là lời giải cho nhu cầu học tập ngôn ngữ thực tế trong kỷ nguyên số. Thông qua quy trình phân tích, thiết kế và triển khai bài bản, nhóm phát triển đã xây dựng được một hệ thống ổn định, bảo mật và giàu tính năng. 

Mặc dù vẫn còn những hạn chế nhất định về mặt nền tảng và tích hợp AI, nhưng với nền tảng kiến trúc vững chắc đã thiết lập, Sakura Flashcard hoàn toàn có tiềm năng trở thành một công cụ hỗ trợ đắc lực cho hàng ngàn người Việt trên con đường chinh phục tiếng Nhật. Việc hoàn thành đồ án này cũng là minh chứng cho khả năng làm chủ các công nghệ hiện đại từ di động đến máy chủ của nhóm thực hiện.
