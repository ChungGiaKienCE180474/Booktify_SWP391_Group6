package shop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.domain.StationeryItem;
import shop.domain.StationeryLog;
import shop.domain.User;
import shop.domain.dto.StationeryItemDTO;
import shop.repository.StationeryLogRepository;
import shop.repository.StationeryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ cho mặt hàng văn phòng phẩm.
 *
 * Tầng trung gian giữa Controller và Repository:
 *   Controller → Service (DTO) → Repository (Entity) → DB
 *
 * Hai phương thức mapping là cầu nối giữa DTO và Entity:
 *   itemDTOtoItem : DTO  → Entity  (dùng khi lưu DB)
 *   itemToItemDTO : Entity → DTO  (dùng khi trả về View)
 */
@Service
public class StationeryService {

    private final StationeryRepository stationeryRepository;
    private final StationeryLogRepository stationeryLogRepository;

    public StationeryService(StationeryRepository stationeryRepository,
                             StationeryLogRepository stationeryLogRepository) {
        this.stationeryRepository = stationeryRepository;
        this.stationeryLogRepository = stationeryLogRepository;
    }

    // ── Mapping methods ──────────────────────────────────────────────────────

    /**
     * Chuyển StationeryItem (entity từ DB) sang StationeryItemDTO (để trả về View).
     * Sao chép toàn bộ trường, kể cả createdAt/updatedAt dùng cho trang detail.
     */
    public StationeryItemDTO itemToItemDTO(StationeryItem item) {
        StationeryItemDTO dto = new StationeryItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setCategory(item.getCategory());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setStockQuantity(item.getStockQuantity());
        dto.setSupplier(item.getSupplier());
        dto.setImagePath(item.getImagePath());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }

    /**
     * Chuyển StationeryItemDTO (dữ liệu từ form) sang StationeryItem (entity để lưu DB).
     * Không sao chép id/createdAt/updatedAt vì các trường này do DB/JPA tự quản lý.
     */
    public StationeryItem itemDTOtoItem(StationeryItemDTO dto) {
        StationeryItem item = new StationeryItem();
        item.setName(dto.getName());
        item.setCategory(dto.getCategory());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setStockQuantity(dto.getStockQuantity());
        item.setSupplier(dto.getSupplier());
        // imagePath có thể null nếu người dùng không chọn ảnh → không set để tránh ghi đè null
        if (dto.getImagePath() != null) item.setImagePath(dto.getImagePath());
        return item;
    }

    // ── UC-17.1: View List Of Stationery ────────────────────────────────────

    /**
     * Tìm kiếm mặt hàng theo từ khóa và danh mục.
     * Chuẩn hóa tham số: chuỗi rỗng/chỉ khoảng trắng → null để query bỏ qua điều kiện đó.
     */
    public List<StationeryItemDTO> search(String keyword, String category) {
        String kw  = (keyword  == null || keyword.isBlank())  ? null : keyword.trim();
        String cat = (category == null || category.isBlank()) ? null : category.trim();
        // Mỗi entity được map sang DTO trước khi trả về Controller
        return stationeryRepository.search(kw, cat).stream()
                .map(this::itemToItemDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return stationeryRepository.findAllCategories();
    }

    // ── UC-17.2: View Stationery Details ────────────────────────────────────

    /**
     * Lấy mặt hàng theo id. Trả về Optional.empty() nếu không tồn tại hoặc đã soft-delete.
     * Controller dùng Optional.isEmpty() để redirect về danh sách khi không tìm thấy.
     */
    public Optional<StationeryItemDTO> getById(Long id) {
        return stationeryRepository.findByIdAndDeletedFalse(id)
                .map(this::itemToItemDTO);
    }

    // ── UC-17.3: Create Stationery ──────────────────────────────────────────

    /**
     * Tạo mới mặt hàng từ DTO.
     * Kiểm tra trùng tên trước khi lưu (chỉ tính các bản ghi chưa bị xóa mềm).
     * @Transactional đảm bảo cả save item và save log thành công cùng lúc hoặc rollback cùng lúc.
     */
    @Transactional
    public StationeryItemDTO create(StationeryItemDTO dto, User staff) {
        // Kiểm tra trùng tên trong các mặt hàng chưa bị xóa
        if (stationeryRepository.existsByNameAndDeletedFalse(dto.getName())) {
            throw new IllegalArgumentException(
                "Item with name '" + dto.getName() + "' already exists.");
        }
        StationeryItem item = itemDTOtoItem(dto);
        item.setStatus("ACTIVE");
        item.setDeleted(false);
        StationeryItem saved = stationeryRepository.save(item);
        // Ghi log CREATE sau khi lưu thành công
        saveLog(saved, staff, "CREATE", null, toLogString(saved));
        return itemToItemDTO(saved);
    }

    // ── UC-17.4: Update Stationery ──────────────────────────────────────────

    /**
     * Cập nhật mặt hàng theo id.
     * Áp dụng pattern "fetch → modify → save" thay vì tạo entity mới,
     * để @PreUpdate tự kích hoạt cập nhật updatedAt và giữ nguyên các trường không sửa.
     */
    @Transactional
    public StationeryItemDTO update(Long id, StationeryItemDTO dto, User staff) {
        // Lấy entity gốc từ DB, ném lỗi nếu không tìm thấy hoặc đã bị xóa
        StationeryItem existing = stationeryRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Stationery item not found with ID: " + id));

        // Kiểm tra tên trùng với bản ghi KHÁC (loại trừ chính id đang sửa)
        if (stationeryRepository.existsByNameAndIdNotAndDeletedFalse(dto.getName(), id)) {
            throw new IllegalArgumentException(
                "Item with name '" + dto.getName() + "' already exists.");
        }

        // Lưu giá trị cũ trước khi cập nhật (dùng cho audit log)
        String oldValue = toLogString(existing);

        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStockQuantity(dto.getStockQuantity());
        existing.setSupplier(dto.getSupplier());
        // Chỉ cập nhật ảnh nếu người dùng upload ảnh mới; null = giữ nguyên ảnh cũ
        if (dto.getImagePath() != null) existing.setImagePath(dto.getImagePath());

        StationeryItem saved = stationeryRepository.save(existing);
        saveLog(saved, staff, "UPDATE", oldValue, toLogString(saved));
        return itemToItemDTO(saved);
    }

    // ── UC-17.5: Soft Delete Stationery ─────────────────────────────────────

    /**
     * Xóa mềm mặt hàng: đặt deleted=true thay vì DELETE khỏi DB.
     * Bản ghi vẫn tồn tại để bảo toàn lịch sử đơn hàng đã có trước đó.
     * Sau khi xóa, tất cả query dùng findByIdAndDeletedFalse sẽ không trả về bản ghi này.
     */
    @Transactional
    public void delete(Long id, User staff) {
        // Kiểm tra tồn tại trước; nếu đã bị xóa rồi thì ném lỗi
        stationeryRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Stationery item not found with ID: " + id));

        // Dùng findById (không lọc deleted) để lấy entity và đánh dấu xóa
        stationeryRepository.findById(id).ifPresent(item -> {
            item.setDeleted(true);
            stationeryRepository.save(item);
        });
    }

    // ── Helper: kiểm tra trùng tên (gọi từ Controller trước khi validate) ───

    /** Trả về true nếu tên đã tồn tại trong các bản ghi chưa bị xóa. */
    public boolean isDuplicateName(String name) {
        return stationeryRepository.existsByNameAndDeletedFalse(name);
    }

    /** Trả về true nếu tên trùng với bản ghi KHÁC khi cập nhật. */
    public boolean isDuplicateNameForUpdate(String name, Long id) {
        return stationeryRepository.existsByNameAndIdNotAndDeletedFalse(name, id);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /** Lưu log thao tác (CREATE / UPDATE) vào bảng stationery_logs. */
    private void saveLog(StationeryItem item, User staff, String action,
                         String oldValue, String newValue) {
        StationeryLog log = new StationeryLog();
        log.setStationeryItem(item);
        log.setStaff(staff);
        log.setAction(action);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        stationeryLogRepository.save(log);
    }

    /** Tạo chuỗi JSON đơn giản mô tả trạng thái mặt hàng tại thời điểm ghi log. */
    private String toLogString(StationeryItem item) {
        return String.format(
            "{name:'%s', category:'%s', price:%s, stockQuantity:%d, supplier:'%s'}",
            item.getName(), item.getCategory(), item.getPrice(),
            item.getStockQuantity(), item.getSupplier());
    }
}
