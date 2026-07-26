package shop.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service tập trung toàn bộ logic upload/delete file ảnh.
 *
 * Cách hoạt động:
 *   - File lưu trên disk tại:  {app.upload.dir}/{folder}/{uuid}.{ext}
 *   - URL trả về dạng:         /uploads/{folder}/{uuid}.{ext}
 *   - WebMvcConfig map URL /uploads/** → file:{app.upload.dir}/
 *
 * Ưu điểm tách ra Service riêng:
 *   - Controller nào cần upload ảnh đều dùng chung (Book, Staff, User...)
 *   - Sau này đổi sang lưu S3/cloud chỉ cần sửa ở đây, Controller không đổi
 */
@Service
public class FileStorageService {

    /** Đọc từ application.properties: app.upload.dir=${user.home}/booktify-uploads */
    @Value("${app.upload.dir}")
    private String uploadDir;

    /** Các đuôi file ảnh được chấp nhận */
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "webp", "gif");

    /**
     * Lưu file ảnh upload vào thư mục con chỉ định.
     *
     * @param file   File upload từ form (MultipartFile)
     * @param folder Thư mục con, ví dụ "book-images", "avatars", "staff-images"
     * @return URL path để lưu vào DB, ví dụ "/uploads/book-images/uuid.jpg"
     * @throws IOException          Nếu không ghi được file
     * @throws IllegalArgumentException Nếu định dạng file không được phép
     */
    public String save(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (ext == null || !ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new IllegalArgumentException(
                "File type not allowed. Accepted: jpg, jpeg, png, webp, gif");
        }

        // Tên file ngẫu nhiên — tránh trùng và tránh path traversal attack
        String filename = UUID.randomUUID() + "." + ext.toLowerCase();

        // Tạo thư mục nếu chưa tồn tại
        Path dir = Paths.get(uploadDir, folder);
        Files.createDirectories(dir);

        // Ghi file vào disk
        Files.copy(file.getInputStream(), dir.resolve(filename));

        // Trả URL path để JSP dùng: <img src="${book.imageUrl}"/>
        return "/uploads/" + folder + "/" + filename;
    }

    /**
     * Xóa file ảnh cũ khỏi disk (khi admin upload ảnh mới hoặc xóa entity).
     * Không throw exception — xóa thất bại chỉ log warning, không crash request.
     *
     * @param imageUrl URL đang lưu trong DB, ví dụ "/uploads/book-images/uuid.jpg"
     * @param folder   Thư mục con tương ứng, ví dụ "book-images"
     */
    public void delete(String imageUrl, String folder) {
        if (!StringUtils.hasText(imageUrl)) return;
        try {
            // Lấy tên file từ URL: "/uploads/book-images/uuid.jpg" → "uuid.jpg"
            String filename = Paths.get(imageUrl).getFileName().toString();
            Path filePath = Paths.get(uploadDir, folder, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("[WARN] FileStorageService: could not delete file '"
                    + imageUrl + "': " + e.getMessage());
        }
    }

    /**
     * Tiện ích: thay thế ảnh cũ bằng ảnh mới trong một bước.
     * Nếu lưu ảnh mới thành công thì xóa ảnh cũ, nếu lỗi thì giữ nguyên ảnh cũ.
     *
     * @param newFile     File mới upload
     * @param oldImageUrl URL ảnh cũ đang lưu trong DB (có thể null)
     * @param folder      Thư mục con
     * @return URL của ảnh mới
     * @throws IOException Nếu không lưu được ảnh mới
     */
    public String replace(MultipartFile newFile, String oldImageUrl, String folder)
            throws IOException {
        // Lưu ảnh mới trước — nếu lỗi thì exception, ảnh cũ vẫn còn nguyên
        String newUrl = save(newFile, folder);
        // Lưu thành công → xóa ảnh cũ
        delete(oldImageUrl, folder);
        return newUrl;
    }
}
