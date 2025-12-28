3.3. Thiết kế các chức năng bảo mật

Trong kỷ nguyên chuyển đổi số, an toàn thông tin không còn là một tính năng bổ sung mà là xương sống của mọi hệ thống phần mềm. Đối với ứng dụng Sakura Flashcard, việc bảo vệ dữ liệu học tập và thông tin cá nhân của người dùng được đặt lên hàng đầu. Hệ thống được thiết kế theo mô hình phòng thủ đa tầng (Defense in Depth), kết hợp chặt chẽ giữa bảo mật hạ tầng, bảo mật máy chủ và bảo mật ứng dụng di động.

3.3.1. Bảo mật phía Backend (Node.js)

Lớp phía máy chủ đóng vai trò là "thành trì" cuối cùng, chịu trách nhiệm xác thực, kiểm soát quyền truy cập và đảm bảo tính toàn vẹn của dữ liệu trước khi lưu trữ vào cơ sở dữ liệu.

3.3.1.1. Hệ thống giới hạn truy cập (Rate Limiting)
Mục tiêu: Ngăn chặn các cuộc tấn công từ chối dịch vụ (DoS) và các nỗ lực dò tìm mật khẩu tự động.

Hệ thống triển khai các chính sách giới hạn tại lớp Middleware để kiểm soát lưu lượng từ mỗi IP. Đoạn mã triển khai thực tế (tại src/middlewares/security.middleware.ts):

export const authLimiter = rateLimit({
    windowMs: 15 * 60 * 1000,
    max: 5,
    message: {
        success: false,
        message: 'Quá nhiều lần đăng nhập thất bại. Vui lòng thử lại sau 15 phút.'
    },
    standardHeaders: true,
    legacyHeaders: false,
    skipSuccessfulRequests: true
});

3.3.1.2. Chống tấn công NoSQL Injection
Mục tiêu: Ngăn chặn việc thao túng các toán tử truy vấn của MongoDB.

Sử dụng thư viện express-mongo-sanitize để quét và loại bỏ các ký tự điều khiển nguy hiểm (như $, .). Đoạn mã triển khai thực tế:

export const sanitizeInput = mongoSanitize({
    replaceWith: '_',
    onSanitize: ({ key, req }) => {
        console.warn(`[SECURITY] Potential NoSQL injection detected in field: ${key}`);
    }
});

3.3.1.3. Chống tấn công Cross-Site Scripting (XSS)
Mục tiêu: Ngăn chặn việc chèn mã độc Javascript vào các nội dung hiển thị cho người dùng khác.

Cơ chế xssSanitizer được xây dựng để duyệt qua dữ liệu và thực hiện escape các ký tự đặc biệt. Đoạn mã triển khai thực tế:

const sanitizeString = (str: string): string => {
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#x27;')
        .replace(/\//g, '&#x2F;');
};

3.3.2. Bảo mật phía Client (Mobile App Security)

Ứng dụng di động được trang bị các công nghệ bảo vệ chủ động để ngăn chặn phân tích và tấn công trực tiếp.

3.3.2.1. Làm rối mã nguồn (Obfuscation)
Mục tiêu: Ngăn chặn việc dịch ngược mã nguồn và bảo vệ logic nghiệp vụ.

Sử dụng ProGuard/R8 để đổi tên class và loại bỏ Log gỡ lỗi. Cấu hình tại app/proguard-rules.pro:

-repackageclasses 'sakura'
-allowaccessmodification
-overloadaggressively
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
}

3.3.2.2. Kiểm soát môi trường thực thi (Anti-Tamper)
Mục tiêu: Đảm bảo ứng dụng chỉ chạy trên môi trường an toàn.

Ứng dụng tích hợp bộ kiểm tra an ninh chủ động (tại util/AntiTamper.kt). Hàm kiểm tra thiết bị Root:

fun isRooted(): Boolean {
    val suPaths = arrayOf(
        "/system/app/Superuser.apk", "/sbin/su",
        "/system/bin/su", "/system/xbin/su",
        "/data/local/xbin/su", "/su/bin/su"
    )
    for (path in suPaths) {
        if (File(path).exists()) return true
    }
    return false
}

3.3.2.3. Bảo vệ màn hình nhạy cảm (Secure Screen)
Khi người dùng nhập mật khẩu, ứng dụng ngăn chặn chụp ảnh và quay phim màn hình. Đoạn mã triển khai tại util/SecurityUtils.kt:

@Composable
fun SecureScreen(content: @Composable () -> Unit) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    content()
}

3.3.3. Bảo mật truyền tải và Lưu trữ

3.3.3.1. Mã hóa mật khẩu với Bcrypt
Mật khẩu được băm với thuật toán Bcrypt (Cost factor 12) trước khi lưu. Đoạn mã tại model/User.ts:

userSchema.pre('save', async function (next) {
    if (!this.isModified('password')) return next();
    const salt = await bcrypt.genSalt(12);
    this.password = await bcrypt.hash(this.password, salt);
    next();
});

3.3.3.2. Quản lý phiên làm việc với JWT
Sử dụng JSON Web Token để xác thực các yêu cầu API. Đoạn mã xác thực tại AuthMiddleware:

const token = authHeader.split(' ')[1];
const decoded = jwt.verify(token, process.env.ACCESS_TOKEN_SECRET);
req.user = decoded;

3.3.4. Bảng tổng hợp các giải pháp bảo mật đã triển khai

STT | Tính năng bảo mật | Giải pháp kỹ thuật | Lớp bảo vệ
---|---|---|---
1 | Chống Brute-force | express-rate-limit | Máy chủ (Backend)
2 | Chống NoSQL Injection | express-mongo-sanitize | Máy chủ (Backend)
3 | Chống Nghe lén | HTTPS / TLS 1.3 | Đường truyền (Network)
4 | Chống Dịch ngược | ProGuard / R8 | Ứng dụng (Mobile)
5 | Chống Root/Debug | AntiTamper Runtime Check | Ứng dụng (Mobile)
6 | Chống Chụp màn hình | FLAG_SECURE | Ứng dụng (Mobile)
7 | Bảo vệ Mật khẩu | Bcrypt (Cost factor 12) | Cơ sở dữ liệu

3.3.5. Kết luận cho chương Bảo mật

    Tổng kết lại, kiến trúc bảo mật của Sakura Flashcard được xây dựng dựa trên nguyên lý "Phòng thủ đa tầng" (Defense in Depth), đảm bảo an toàn từ cấp độ đường truyền, máy chủ đến mã nguồn ứng dụng di động. Việc kết hợp nhiều giải pháp kỹ thuật như xác thực JWT, mã hóa Bcrypt, cơ chế chống giả mạo (Anti-Tamper) và giới hạn truy cập (Rate Limiting) không chỉ giúp bảo vệ toàn diện dữ liệu người dùng mà còn tối ưu hóa hiệu năng hệ thống. Sự đầu tư bài bản vào lớp bảo mật này không chỉ ngăn chặn các rủi ro từ tấn công bên ngoài mà còn tạo ra một nền tảng tin cậy, giúp người dùng hoàn toàn yên tâm trong quá trình học tập và phát triển bản thân cùng ứng dụng.
