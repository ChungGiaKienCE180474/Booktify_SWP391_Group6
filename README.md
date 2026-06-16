# Hướng dẫn chạy project bằng terminal

Project này là một ứng dụng Spring Boot dùng Maven Wrapper, nên bạn có thể chạy trực tiếp bằng terminal trên Windows mà không cần cài Maven riêng.

## Yêu cầu trước khi chạy

- Đã cài `Java 17`
- Đã cài và bật `PostgreSQL`
- Đã tạo database tên `booktify`
- Tài khoản PostgreSQL khớp với cấu hình trong `src/main/resources/application.properties`

Hiện tại cấu hình đang dùng:

- `spring.datasource.url=jdbc:postgresql://localhost:5432/booktify`
- `spring.datasource.username=postgres`
- `spring.datasource.password=1234`
- `server.port=8081`

## Cách chạy project

Mở PowerShell hoặc Command Prompt, sau đó chuyển vào thư mục gốc của project:

```powershell
cd "C:\Users\Nhat_Anh\OneDrive\Documents\SWP391"
```

### Cách 1: Chạy trực tiếp bằng Maven Wrapper

```powershell
.\mvnw.cmd spring-boot:run
```

### Cách 2: Build trước rồi chạy file jar

```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target\shop-0.0.1-SNAPSHOT.jar
```

## Kiểm tra sau khi chạy

Nếu ứng dụng khởi động thành công, mở trình duyệt và truy cập:

```text
http://localhost:8081
```

## Admin dashboard

Dashboard admin nằm ở:

```text
http://localhost:8081/admin
```

Tài khoản seed sẵn để đăng nhập admin:

- Email: `admin@booktify.local`
- Password: `Admin@123`

Ngoài ra có thêm 2 tài khoản seed để test phân quyền:

- Staff: `staff@booktify.local` / `Staff@123`
- Customer: `customer@booktify.local` / `Customer@123`

## Nếu bị lỗi kết nối database

Kiểm tra các điểm sau:

- PostgreSQL đang chạy
- Database `booktify` đã tồn tại
- Username và password trong `application.properties` đúng
- Cổng `5432` không bị thay đổi

## Ghi chú

- Project dùng JSP nên build cần chạy đầy đủ dependencies của Maven.
- Nếu muốn đổi port, sửa `server.port` trong `src/main/resources/application.properties`.