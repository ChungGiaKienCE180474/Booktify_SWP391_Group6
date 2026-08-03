package shop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.domain.StationeryCategory;
import shop.domain.dto.StationeryCategoryDTO;
import shop.repository.StationeryCategoryRepository;
import shop.repository.StationeryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ cho danh mục văn phòng phẩm.
 *
 * Khác với StationeryItem, danh mục dùng xóa thật (deleteById) chứ không dùng soft delete.
 * Tuy nhiên, việc xóa bị chặn nếu còn sản phẩm nào đang tham chiếu tới danh mục đó,
 * tránh tình trạng mặt hàng bị mất category.
 */
@Service
public class StationeryCategoryService {

    private final StationeryCategoryRepository categoryRepository;
    // Cần StationeryRepository để đếm sản phẩm đang dùng danh mục trước khi cho xóa
    private final StationeryRepository stationeryRepository;

    public StationeryCategoryService(StationeryCategoryRepository categoryRepository,
                                     StationeryRepository stationeryRepository) {
        this.categoryRepository = categoryRepository;
        this.stationeryRepository = stationeryRepository;
    }

    // ── Mapping methods ──────────────────────────────────────────────────────

    /**
     * Chuyển StationeryCategory (entity) → StationeryCategoryDTO (để trả về View).
     * Sao chép cả createdAt để hiển thị trong category/list.jsp.
     */
    public StationeryCategoryDTO categoryToDTO(StationeryCategory cat) {
        StationeryCategoryDTO dto = new StationeryCategoryDTO();
        dto.setId(cat.getId());
        dto.setName(cat.getName());
        dto.setDescription(cat.getDescription());
        dto.setCreatedAt(cat.getCreatedAt());
        return dto;
    }

    /**
     * Chuyển StationeryCategoryDTO (dữ liệu form) → StationeryCategory (entity để lưu DB).
     * Không sao chép id và createdAt vì chúng do DB/JPA tự sinh.
     */
    public StationeryCategory categoryDTOtoCategory(StationeryCategoryDTO dto) {
        StationeryCategory cat = new StationeryCategory();
        cat.setName(dto.getName());
        cat.setDescription(dto.getDescription());
        return cat;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /** Lấy toàn bộ danh mục, mỗi entity được map sang DTO trước khi trả về. */
    public List<StationeryCategoryDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::categoryToDTO)
                .collect(Collectors.toList());
    }

    /** Tìm danh mục theo id, trả về Optional.empty() nếu không tồn tại. */
    public Optional<StationeryCategoryDTO> getById(Long id) {
        return categoryRepository.findById(id).map(this::categoryToDTO);
    }

    /**
     * Tạo mới danh mục từ DTO.
     * Kiểm tra tên trùng trước khi lưu (tên danh mục phải duy nhất toàn hệ thống).
     */
    @Transactional
    public StationeryCategoryDTO create(StationeryCategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException(
                "Category '" + dto.getName() + "' already exists.");
        }
        StationeryCategory saved = categoryRepository.save(categoryDTOtoCategory(dto));
        return categoryToDTO(saved);
    }

    /**
     * Cập nhật tên và mô tả danh mục.
     * Áp dụng pattern "fetch → modify → save" để @Column(updatable=false) trên createdAt
     * không bị ghi đè (nếu tạo entity mới và save, JPA sẽ set createdAt = null).
     */
    @Transactional
    public StationeryCategoryDTO update(Long id, StationeryCategoryDTO dto) {
        StationeryCategory existing = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        // Kiểm tra tên trùng với danh mục KHÁC (loại trừ chính danh mục đang sửa)
        if (categoryRepository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new IllegalArgumentException(
                "Category '" + dto.getName() + "' already exists.");
        }

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return categoryToDTO(categoryRepository.save(existing));
    }

    /**
     * Xóa thật danh mục khỏi DB.
     * Bị chặn nếu còn bất kỳ sản phẩm nào (chưa bị soft-delete) đang dùng danh mục này,
     * tránh để sản phẩm tồn tại nhưng không có danh mục hợp lệ.
     */
    @Transactional
    public void delete(Long id) {
        StationeryCategory existing = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        // Đếm mặt hàng chưa bị xóa mềm đang thuộc danh mục này
        long itemCount = stationeryRepository.countByCategoryAndDeletedFalse(existing.getName());
        if (itemCount > 0) {
            throw new IllegalArgumentException(
                "Cannot delete category '" + existing.getName() +
                "' because " + itemCount + " item(s) are still using it.");
        }

        categoryRepository.deleteById(id);
    }

    // ── Helper: kiểm tra trùng tên (gọi từ Controller trước khi validate) ───

    /** Trả về true nếu tên đã tồn tại trong bảng danh mục. */
    public boolean isDuplicateName(String name) {
        return categoryRepository.existsByName(name);
    }

    /** Trả về true nếu tên trùng với danh mục KHÁC khi cập nhật. */
    public boolean isDuplicateNameForUpdate(String name, Long id) {
        return categoryRepository.existsByNameAndIdNot(name, id);
    }
}
