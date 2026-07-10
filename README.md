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
```
Booktify_SWP391_Group6
├─ .mvn
│  └─ wrapper
│     └─ maven-wrapper.properties
├─ error.txt
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ README.md
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ shop
   │  │     ├─ config
   │  │     │  ├─ CategoryDataInitializer.java
   │  │     │  ├─ CustomSuccessHandler.java
   │  │     │  ├─ GlobalModelAdvice.java
   │  │     │  ├─ RoleDataInitializer.java
   │  │     │  ├─ SecurityConfiguration.java
   │  │     │  ├─ TestDataInitializer.java
   │  │     │  ├─ UserDataInitializer.java
   │  │     │  └─ WebMvcConfig.java
   │  │     ├─ controller
   │  │     │  ├─ admin
   │  │     │  │  ├─ AdminAuthorController.java
   │  │     │  │  ├─ AdminBookController.java
   │  │     │  │  ├─ AdminDashboardController.java
   │  │     │  │  ├─ CategoryController.java
   │  │     │  │  ├─ CustomerController.java
   │  │     │  │  └─ StaffController.java
   │  │     │  ├─ ChangePassController.java
   │  │     │  ├─ client
   │  │     │  │  ├─ CartController.java
   │  │     │  │  ├─ ClientBookController.java
   │  │     │  │  ├─ HomePageController.java
   │  │     │  │  ├─ ProfileController.java
   │  │     │  │  └─ RegisterController.java
   │  │     │  ├─ ForgotPasswordController.java
   │  │     │  ├─ LoginController.java
   │  │     │  ├─ StationeryCategoryController.java
   │  │     │  └─ StationeryController.java
   │  │     ├─ domain
   │  │     │  ├─ Author.java
   │  │     │  ├─ Book.java
   │  │     │  ├─ Cart.java
   │  │     │  ├─ CartItem.java
   │  │     │  ├─ Category.java
   │  │     │  ├─ dto
   │  │     │  │  ├─ AuthorDTO.java
   │  │     │  │  ├─ CustomerDTO.java
   │  │     │  │  ├─ RegisterDTO.java
   │  │     │  │  ├─ StaffDTO.java
   │  │     │  │  ├─ StationeryCategoryDTO.java
   │  │     │  │  └─ StationeryItemDTO.java
   │  │     │  ├─ OTPForm.java
   │  │     │  ├─ PasswordChangeForm.java
   │  │     │  ├─ ProfileUpdateForm.java
   │  │     │  ├─ ResetPasswordForm.java
   │  │     │  ├─ Role.java
   │  │     │  ├─ RoleName.java
   │  │     │  ├─ StationeryCategory.java
   │  │     │  ├─ StationeryItem.java
   │  │     │  ├─ StationeryLog.java
   │  │     │  └─ User.java
   │  │     ├─ repository
   │  │     │  ├─ AuthorRepository.java
   │  │     │  ├─ BookRepository.java
   │  │     │  ├─ CartItemRepository.java
   │  │     │  ├─ CartRepository.java
   │  │     │  ├─ CategoryRepository.java
   │  │     │  ├─ RoleRepository.java
   │  │     │  ├─ StationeryCategoryRepository.java
   │  │     │  ├─ StationeryLogRepository.java
   │  │     │  ├─ StationeryRepository.java
   │  │     │  └─ UserRepository.java
   │  │     ├─ service
   │  │     │  ├─ AuthorService.java
   │  │     │  ├─ BookService.java
   │  │     │  ├─ CartService.java
   │  │     │  ├─ CategoryService.java
   │  │     │  ├─ EmailService.java
   │  │     │  ├─ StationeryCategoryService.java
   │  │     │  ├─ StationeryService.java
   │  │     │  ├─ UserService.java
   │  │     │  └─ validator
   │  │     │     └─ CustomUserDetailsService.java
   │  │     └─ ShopApplication.java
   │  ├─ resources
   │  │  ├─ application.properties
   │  │  └─ schema.sql
   │  └─ webapp
   │     ├─ resources
   │     │  ├─ client
   │     │  │  ├─ css
   │     │  │  │  ├─ bootstrap.min.css
   │     │  │  │  └─ style.css
   │     │  │  ├─ img
   │     │  │  │  ├─ FB.png
   │     │  │  │  ├─ hero-img-1.jpg
   │     │  │  │  ├─ hero-img-2.jpg
   │     │  │  │  ├─ honkai.png
   │     │  │  │  ├─ ITR.jpg
   │     │  │  │  ├─ logo.jpg
   │     │  │  │  ├─ pela.jpg
   │     │  │  │  └─ YTB.png
   │     │  │  ├─ js
   │     │  │  │  └─ main.js
   │     │  │  └─ lib
   │     │  │     ├─ easing
   │     │  │     │  ├─ easing.js
   │     │  │     │  └─ easing.min.js
   │     │  │     ├─ lightbox
   │     │  │     │  ├─ css
   │     │  │     │  │  └─ lightbox.min.css
   │     │  │     │  ├─ images
   │     │  │     │  │  ├─ close.png
   │     │  │     │  │  ├─ loading.gif
   │     │  │     │  │  ├─ next.png
   │     │  │     │  │  └─ prev.png
   │     │  │     │  └─ js
   │     │  │     │     └─ lightbox.min.js
   │     │  │     ├─ owlcarousel
   │     │  │     │  ├─ assets
   │     │  │     │  │  ├─ ajax-loader.gif
   │     │  │     │  │  ├─ owl.carousel.css
   │     │  │     │  │  ├─ owl.carousel.min.css
   │     │  │     │  │  ├─ owl.theme.default.css
   │     │  │     │  │  ├─ owl.theme.default.min.css
   │     │  │     │  │  ├─ owl.theme.green.css
   │     │  │     │  │  ├─ owl.theme.green.min.css
   │     │  │     │  │  └─ owl.video.play.png
   │     │  │     │  ├─ LICENSE
   │     │  │     │  ├─ owl.carousel.js
   │     │  │     │  └─ owl.carousel.min.js
   │     │  │     └─ waypoints
   │     │  │        ├─ links.php
   │     │  │        └─ waypoints.min.js
   │     │  ├─ css
   │     │  │  ├─ admin-dashboard.css
   │     │  │  ├─ auth-theme.css
   │     │  │  ├─ cart.css
   │     │  │  ├─ ewstyle.css
   │     │  │  ├─ footer.css
   │     │  │  ├─ header.css
   │     │  │  ├─ homepage.css
   │     │  │  ├─ profile.css
   │     │  │  ├─ register.css
   │     │  │  ├─ stationery.css
   │     │  │  ├─ style-oh.css
   │     │  │  ├─ style.css
   │     │  │  └─ styles.css
   │     │  ├─ images
   │     │  │  ├─ logo.jpg
   │     │  │  └─ stationery
   │     │  │     ├─ 2444d2e2-0f7f-419e-b880-f5d0469e5aa4_WIN_20250915_08_46_51_Pro.jpg
   │     │  │     ├─ 6c0fc399-ca28-4b55-bc5d-6558b5e06524_z7637192459742_e074a8ef097930a4ba03a9f227c7942f.jpg
   │     │  │     ├─ 99372904-4bca-4324-9749-8f38d18f9715_butbithienlongtl2529-3581.jpg
   │     │  │     └─ d771fb59-92b3-4200-8efe-7fb40f3ea12f_giay_double_a_a4_4c5d99f9035c4abdb9ae92447ae7ddeb.jpg
   │     │  └─ js
   │     │     └─ scripts.js
   │     └─ WEB-INF
   │        └─ view
   │           ├─ admin
   │           │  ├─ author
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ book
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ category
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ customer
   │           │  │  ├─ detail.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ dashboard
   │           │  │  └─ index.jsp
   │           │  └─ staff
   │           │     ├─ deleted.jsp
   │           │     ├─ detail.jsp
   │           │     └─ list.jsp
   │           ├─ authentication
   │           │  ├─ changepass.jsp
   │           │  ├─ deny.jsp
   │           │  ├─ enterOTP.jsp
   │           │  ├─ enterRegisterOTP.jsp
   │           │  ├─ forgotpassword.jsp
   │           │  ├─ login.jsp
   │           │  ├─ register.jsp
   │           │  └─ resetPassword.jsp
   │           ├─ book
   │           │  ├─ detail.jsp
   │           │  └─ list.jsp
   │           ├─ cart
   │           │  └─ index.jsp
   │           ├─ homepage
   │           │  └─ index.jsp
   │           ├─ layout
   │           │  ├─ admin
   │           │  │  ├─ header.jsp
   │           │  │  └─ sidebar.jsp
   │           │  ├─ footer.jsp
   │           │  └─ header.jsp
   │           ├─ profile
   │           │  └─ index.jsp
   │           └─ stationery
   │              ├─ category
   │              │  ├─ create.jsp
   │              │  ├─ edit.jsp
   │              │  └─ list.jsp
   │              ├─ create.jsp
   │              ├─ detail.jsp
   │              ├─ edit.jsp
   │              └─ list.jsp
   └─ test
      └─ java
         └─ shop
            └─ ShopApplicationTests.java

```
```
Booktify_SWP391_Group6
├─ .mvn
│  └─ wrapper
│     └─ maven-wrapper.properties
├─ error.txt
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ README.md
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ shop
   │  │     ├─ config
   │  │     │  ├─ CategoryDataInitializer.java
   │  │     │  ├─ CustomSuccessHandler.java
   │  │     │  ├─ GlobalModelAdvice.java
   │  │     │  ├─ RoleDataInitializer.java
   │  │     │  ├─ SecurityConfiguration.java
   │  │     │  ├─ TestDataInitializer.java
   │  │     │  ├─ UserDataInitializer.java
   │  │     │  └─ WebMvcConfig.java
   │  │     ├─ controller
   │  │     │  ├─ admin
   │  │     │  │  ├─ AdminAuthorController.java
   │  │     │  │  ├─ AdminBookController.java
   │  │     │  │  ├─ AdminDashboardController.java
   │  │     │  │  ├─ AdminGenreController.java
   │  │     │  │  ├─ CategoryController.java
   │  │     │  │  ├─ CustomerController.java
   │  │     │  │  └─ StaffController.java
   │  │     │  ├─ ChangePassController.java
   │  │     │  ├─ client
   │  │     │  │  ├─ AuthorController.java
   │  │     │  │  ├─ CartController.java
   │  │     │  │  ├─ ClientBookController.java
   │  │     │  │  ├─ HomePageController.java
   │  │     │  │  ├─ ProfileController.java
   │  │     │  │  └─ RegisterController.java
   │  │     │  ├─ ForgotPasswordController.java
   │  │     │  ├─ LoginController.java
   │  │     │  ├─ StationeryCategoryController.java
   │  │     │  └─ StationeryController.java
   │  │     ├─ domain
   │  │     │  ├─ Author.java
   │  │     │  ├─ Book.java
   │  │     │  ├─ Cart.java
   │  │     │  ├─ CartItem.java
   │  │     │  ├─ Category.java
   │  │     │  ├─ dto
   │  │     │  │  ├─ AuthorDTO.java
   │  │     │  │  ├─ CartDTO.java
   │  │     │  │  ├─ CartItemDTO.java
   │  │     │  │  ├─ CustomerDTO.java
   │  │     │  │  ├─ ProfileDTO.java
   │  │     │  │  ├─ RegisterDTO.java
   │  │     │  │  ├─ StaffDTO.java
   │  │     │  │  ├─ StationeryCategoryDTO.java
   │  │     │  │  └─ StationeryItemDTO.java
   │  │     │  ├─ Genre.java
   │  │     │  ├─ OTPForm.java
   │  │     │  ├─ PasswordChangeForm.java
   │  │     │  ├─ ProfileUpdateForm.java
   │  │     │  ├─ ResetPasswordForm.java
   │  │     │  ├─ Role.java
   │  │     │  ├─ RoleName.java
   │  │     │  ├─ StationeryCategory.java
   │  │     │  ├─ StationeryItem.java
   │  │     │  ├─ StationeryLog.java
   │  │     │  └─ User.java
   │  │     ├─ repository
   │  │     │  ├─ AuthorRepository.java
   │  │     │  ├─ BookRepository.java
   │  │     │  ├─ CartItemRepository.java
   │  │     │  ├─ CartRepository.java
   │  │     │  ├─ CategoryRepository.java
   │  │     │  ├─ GenreRepository.java
   │  │     │  ├─ RoleRepository.java
   │  │     │  ├─ StationeryCategoryRepository.java
   │  │     │  ├─ StationeryLogRepository.java
   │  │     │  ├─ StationeryRepository.java
   │  │     │  └─ UserRepository.java
   │  │     ├─ service
   │  │     │  ├─ AuthorService.java
   │  │     │  ├─ BookService.java
   │  │     │  ├─ CartService.java
   │  │     │  ├─ CategoryService.java
   │  │     │  ├─ EmailService.java
   │  │     │  ├─ FileStorageService.java
   │  │     │  ├─ GenreService.java
   │  │     │  ├─ StationeryCategoryService.java
   │  │     │  ├─ StationeryService.java
   │  │     │  ├─ UserService.java
   │  │     │  └─ validator
   │  │     │     └─ CustomUserDetailsService.java
   │  │     └─ ShopApplication.java
   │  ├─ resources
   │  │  ├─ application.properties
   │  │  └─ schema.sql
   │  └─ webapp
   │     ├─ resources
   │     │  ├─ client
   │     │  │  ├─ css
   │     │  │  │  ├─ bootstrap.min.css
   │     │  │  │  └─ style.css
   │     │  │  ├─ img
   │     │  │  │  ├─ FB.png
   │     │  │  │  ├─ hero-img-1.jpg
   │     │  │  │  ├─ hero-img-2.jpg
   │     │  │  │  ├─ honkai.png
   │     │  │  │  ├─ ITR.jpg
   │     │  │  │  ├─ logo.jpg
   │     │  │  │  ├─ pela.jpg
   │     │  │  │  └─ YTB.png
   │     │  │  ├─ js
   │     │  │  │  └─ main.js
   │     │  │  └─ lib
   │     │  │     ├─ easing
   │     │  │     │  ├─ easing.js
   │     │  │     │  └─ easing.min.js
   │     │  │     ├─ lightbox
   │     │  │     │  ├─ css
   │     │  │     │  │  └─ lightbox.min.css
   │     │  │     │  ├─ images
   │     │  │     │  │  ├─ close.png
   │     │  │     │  │  ├─ loading.gif
   │     │  │     │  │  ├─ next.png
   │     │  │     │  │  └─ prev.png
   │     │  │     │  └─ js
   │     │  │     │     └─ lightbox.min.js
   │     │  │     ├─ owlcarousel
   │     │  │     │  ├─ assets
   │     │  │     │  │  ├─ ajax-loader.gif
   │     │  │     │  │  ├─ owl.carousel.css
   │     │  │     │  │  ├─ owl.carousel.min.css
   │     │  │     │  │  ├─ owl.theme.default.css
   │     │  │     │  │  ├─ owl.theme.default.min.css
   │     │  │     │  │  ├─ owl.theme.green.css
   │     │  │     │  │  ├─ owl.theme.green.min.css
   │     │  │     │  │  └─ owl.video.play.png
   │     │  │     │  ├─ LICENSE
   │     │  │     │  ├─ owl.carousel.js
   │     │  │     │  └─ owl.carousel.min.js
   │     │  │     └─ waypoints
   │     │  │        ├─ links.php
   │     │  │        └─ waypoints.min.js
   │     │  ├─ css
   │     │  │  ├─ admin-dashboard.css
   │     │  │  ├─ auth-theme.css
   │     │  │  ├─ auth.css
   │     │  │  ├─ cart.css
   │     │  │  ├─ ewstyle.css
   │     │  │  ├─ footer.css
   │     │  │  ├─ header.css
   │     │  │  ├─ homepage.css
   │     │  │  ├─ profile.css
   │     │  │  ├─ register.css
   │     │  │  ├─ stationery.css
   │     │  │  ├─ style-oh.css
   │     │  │  ├─ style.css
   │     │  │  └─ styles.css
   │     │  ├─ images
   │     │  │  ├─ logo.jpg
   │     │  │  └─ stationery
   │     │  │     ├─ 2444d2e2-0f7f-419e-b880-f5d0469e5aa4_WIN_20250915_08_46_51_Pro.jpg
   │     │  │     ├─ 6c0fc399-ca28-4b55-bc5d-6558b5e06524_z7637192459742_e074a8ef097930a4ba03a9f227c7942f.jpg
   │     │  │     ├─ 99372904-4bca-4324-9749-8f38d18f9715_butbithienlongtl2529-3581.jpg
   │     │  │     └─ d771fb59-92b3-4200-8efe-7fb40f3ea12f_giay_double_a_a4_4c5d99f9035c4abdb9ae92447ae7ddeb.jpg
   │     │  └─ js
   │     │     └─ scripts.js
   │     └─ WEB-INF
   │        └─ view
   │           ├─ admin
   │           │  ├─ author
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ book
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ category
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ customer
   │           │  │  ├─ detail.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ dashboard
   │           │  │  └─ index.jsp
   │           │  ├─ genre
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  └─ staff
   │           │     ├─ deleted.jsp
   │           │     ├─ detail.jsp
   │           │     └─ list.jsp
   │           ├─ authentication
   │           │  ├─ changepass.jsp
   │           │  ├─ deny.jsp
   │           │  ├─ enterOTP.jsp
   │           │  ├─ enterRegisterOTP.jsp
   │           │  ├─ forgotpassword.jsp
   │           │  ├─ login.jsp
   │           │  ├─ register.jsp
   │           │  └─ resetPassword.jsp
   │           ├─ author
   │           │  ├─ detail.jsp
   │           │  └─ list.jsp
   │           ├─ book
   │           │  ├─ detail.jsp
   │           │  └─ list.jsp
   │           ├─ cart
   │           │  └─ index.jsp
   │           ├─ homepage
   │           │  └─ index.jsp
   │           ├─ layout
   │           │  ├─ admin
   │           │  │  ├─ header.jsp
   │           │  │  └─ sidebar.jsp
   │           │  ├─ footer.jsp
   │           │  └─ header.jsp
   │           ├─ profile
   │           │  └─ index.jsp
   │           └─ stationery
   │              ├─ category
   │              │  ├─ create.jsp
   │              │  ├─ edit.jsp
   │              │  └─ list.jsp
   │              ├─ create.jsp
   │              ├─ detail.jsp
   │              ├─ edit.jsp
   │              └─ list.jsp
   └─ test
      └─ java
         └─ shop
            └─ ShopApplicationTests.java

```
```
Booktify_SWP391_Group6
├─ .mvn
│  └─ wrapper
│     └─ maven-wrapper.properties
├─ error.txt
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ README.md
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ shop
   │  │     ├─ config
   │  │     │  ├─ CategoryDataInitializer.java
   │  │     │  ├─ CustomSuccessHandler.java
   │  │     │  ├─ GlobalModelAdvice.java
   │  │     │  ├─ RoleDataInitializer.java
   │  │     │  ├─ SecurityConfiguration.java
   │  │     │  ├─ TestDataInitializer.java
   │  │     │  ├─ UserDataInitializer.java
   │  │     │  └─ WebMvcConfig.java
   │  │     ├─ controller
   │  │     │  ├─ admin
   │  │     │  │  ├─ AdminAuthorController.java
   │  │     │  │  ├─ AdminBookController.java
   │  │     │  │  ├─ AdminDashboardController.java
   │  │     │  │  ├─ AdminGenreController.java
   │  │     │  │  ├─ CategoryController.java
   │  │     │  │  ├─ CustomerController.java
   │  │     │  │  └─ StaffController.java
   │  │     │  ├─ ChangePassController.java
   │  │     │  ├─ client
   │  │     │  │  ├─ AuthorController.java
   │  │     │  │  ├─ CartController.java
   │  │     │  │  ├─ ClientBookController.java
   │  │     │  │  ├─ HomePageController.java
   │  │     │  │  ├─ ProfileController.java
   │  │     │  │  └─ RegisterController.java
   │  │     │  ├─ ForgotPasswordController.java
   │  │     │  ├─ LoginController.java
   │  │     │  ├─ StationeryCategoryController.java
   │  │     │  └─ StationeryController.java
   │  │     ├─ domain
   │  │     │  ├─ Author.java
   │  │     │  ├─ AuthProvider.java
   │  │     │  ├─ Book.java
   │  │     │  ├─ Cart.java
   │  │     │  ├─ CartItem.java
   │  │     │  ├─ Category.java
   │  │     │  ├─ dto
   │  │     │  │  ├─ AuthorDTO.java
   │  │     │  │  ├─ CartDTO.java
   │  │     │  │  ├─ CartItemDTO.java
   │  │     │  │  ├─ CustomerDTO.java
   │  │     │  │  ├─ ProfileDTO.java
   │  │     │  │  ├─ RegisterDTO.java
   │  │     │  │  ├─ StaffDTO.java
   │  │     │  │  ├─ StationeryCategoryDTO.java
   │  │     │  │  └─ StationeryItemDTO.java
   │  │     │  ├─ Genre.java
   │  │     │  ├─ OTPForm.java
   │  │     │  ├─ PasswordChangeForm.java
   │  │     │  ├─ ProfileUpdateForm.java
   │  │     │  ├─ ResetPasswordForm.java
   │  │     │  ├─ Role.java
   │  │     │  ├─ RoleName.java
   │  │     │  ├─ StationeryCategory.java
   │  │     │  ├─ StationeryItem.java
   │  │     │  ├─ StationeryLog.java
   │  │     │  └─ User.java
   │  │     ├─ repository
   │  │     │  ├─ AuthorRepository.java
   │  │     │  ├─ BookRepository.java
   │  │     │  ├─ CartItemRepository.java
   │  │     │  ├─ CartRepository.java
   │  │     │  ├─ CategoryRepository.java
   │  │     │  ├─ GenreRepository.java
   │  │     │  ├─ RoleRepository.java
   │  │     │  ├─ StationeryCategoryRepository.java
   │  │     │  ├─ StationeryLogRepository.java
   │  │     │  ├─ StationeryRepository.java
   │  │     │  └─ UserRepository.java
   │  │     ├─ service
   │  │     │  ├─ AuthorService.java
   │  │     │  ├─ BookService.java
   │  │     │  ├─ CartService.java
   │  │     │  ├─ CategoryService.java
   │  │     │  ├─ EmailService.java
   │  │     │  ├─ FileStorageService.java
   │  │     │  ├─ GenreService.java
   │  │     │  ├─ StationeryCategoryService.java
   │  │     │  ├─ StationeryService.java
   │  │     │  ├─ UserService.java
   │  │     │  └─ validator
   │  │     │     ├─ CustomOAuth2UserService.java
   │  │     │     └─ CustomUserDetailsService.java
   │  │     └─ ShopApplication.java
   │  ├─ resources
   │  │  ├─ application.properties
   │  │  └─ schema.sql
   │  └─ webapp
   │     ├─ resources
   │     │  ├─ client
   │     │  │  ├─ css
   │     │  │  │  ├─ bootstrap.min.css
   │     │  │  │  └─ style.css
   │     │  │  ├─ img
   │     │  │  │  ├─ FB.png
   │     │  │  │  ├─ hero-img-1.jpg
   │     │  │  │  ├─ hero-img-2.jpg
   │     │  │  │  ├─ honkai.png
   │     │  │  │  ├─ ITR.jpg
   │     │  │  │  ├─ logo.jpg
   │     │  │  │  ├─ pela.jpg
   │     │  │  │  └─ YTB.png
   │     │  │  ├─ js
   │     │  │  │  └─ main.js
   │     │  │  └─ lib
   │     │  │     ├─ easing
   │     │  │     │  ├─ easing.js
   │     │  │     │  └─ easing.min.js
   │     │  │     ├─ lightbox
   │     │  │     │  ├─ css
   │     │  │     │  │  └─ lightbox.min.css
   │     │  │     │  ├─ images
   │     │  │     │  │  ├─ close.png
   │     │  │     │  │  ├─ loading.gif
   │     │  │     │  │  ├─ next.png
   │     │  │     │  │  └─ prev.png
   │     │  │     │  └─ js
   │     │  │     │     └─ lightbox.min.js
   │     │  │     ├─ owlcarousel
   │     │  │     │  ├─ assets
   │     │  │     │  │  ├─ ajax-loader.gif
   │     │  │     │  │  ├─ owl.carousel.css
   │     │  │     │  │  ├─ owl.carousel.min.css
   │     │  │     │  │  ├─ owl.theme.default.css
   │     │  │     │  │  ├─ owl.theme.default.min.css
   │     │  │     │  │  ├─ owl.theme.green.css
   │     │  │     │  │  ├─ owl.theme.green.min.css
   │     │  │     │  │  └─ owl.video.play.png
   │     │  │     │  ├─ LICENSE
   │     │  │     │  ├─ owl.carousel.js
   │     │  │     │  └─ owl.carousel.min.js
   │     │  │     └─ waypoints
   │     │  │        ├─ links.php
   │     │  │        └─ waypoints.min.js
   │     │  ├─ css
   │     │  │  ├─ admin-dashboard.css
   │     │  │  ├─ auth-theme.css
   │     │  │  ├─ auth.css
   │     │  │  ├─ cart.css
   │     │  │  ├─ ewstyle.css
   │     │  │  ├─ footer.css
   │     │  │  ├─ header.css
   │     │  │  ├─ homepage.css
   │     │  │  ├─ profile.css
   │     │  │  ├─ register.css
   │     │  │  ├─ stationery.css
   │     │  │  ├─ style-oh.css
   │     │  │  ├─ style.css
   │     │  │  └─ styles.css
   │     │  ├─ images
   │     │  │  ├─ logo.jpg
   │     │  │  └─ stationery
   │     │  │     ├─ 2444d2e2-0f7f-419e-b880-f5d0469e5aa4_WIN_20250915_08_46_51_Pro.jpg
   │     │  │     ├─ 6c0fc399-ca28-4b55-bc5d-6558b5e06524_z7637192459742_e074a8ef097930a4ba03a9f227c7942f.jpg
   │     │  │     ├─ 99372904-4bca-4324-9749-8f38d18f9715_butbithienlongtl2529-3581.jpg
   │     │  │     └─ d771fb59-92b3-4200-8efe-7fb40f3ea12f_giay_double_a_a4_4c5d99f9035c4abdb9ae92447ae7ddeb.jpg
   │     │  └─ js
   │     │     └─ scripts.js
   │     └─ WEB-INF
   │        └─ view
   │           ├─ admin
   │           │  ├─ author
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ book
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ category
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ customer
   │           │  │  ├─ detail.jsp
   │           │  │  └─ list.jsp
   │           │  ├─ dashboard
   │           │  │  └─ index.jsp
   │           │  ├─ genre
   │           │  │  ├─ form.jsp
   │           │  │  └─ list.jsp
   │           │  └─ staff
   │           │     ├─ deleted.jsp
   │           │     ├─ detail.jsp
   │           │     └─ list.jsp
   │           ├─ authentication
   │           │  ├─ changepass.jsp
   │           │  ├─ deny.jsp
   │           │  ├─ enterOTP.jsp
   │           │  ├─ enterRegisterOTP.jsp
   │           │  ├─ forgotpassword.jsp
   │           │  ├─ login.jsp
   │           │  ├─ register.jsp
   │           │  └─ resetPassword.jsp
   │           ├─ author
   │           │  ├─ detail.jsp
   │           │  └─ list.jsp
   │           ├─ book
   │           │  ├─ detail.jsp
   │           │  └─ list.jsp
   │           ├─ cart
   │           │  └─ index.jsp
   │           ├─ homepage
   │           │  └─ index.jsp
   │           ├─ layout
   │           │  ├─ admin
   │           │  │  ├─ header.jsp
   │           │  │  └─ sidebar.jsp
   │           │  ├─ footer.jsp
   │           │  └─ header.jsp
   │           ├─ profile
   │           │  └─ index.jsp
   │           └─ stationery
   │              ├─ category
   │              │  ├─ create.jsp
   │              │  ├─ edit.jsp
   │              │  └─ list.jsp
   │              ├─ create.jsp
   │              ├─ detail.jsp
   │              ├─ edit.jsp
   │              └─ list.jsp
   └─ test
      └─ java
         └─ shop
            └─ ShopApplicationTests.java

```