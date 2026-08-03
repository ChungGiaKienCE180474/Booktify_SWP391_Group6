package shop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import shop.domain.VppCategory;
import shop.domain.VppItem;
import shop.domain.dto.VppItemDTO;
import shop.repository.VppItemRepository;

@Service
@Transactional(readOnly = true)
public class VppItemService {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS =
            Set.of(
                    "jpg",
                    "jpeg",
                    "png",
                    "gif",
                    "webp",
                    "jfif",
                    "avif",
                    "bmp"
            );

    private final VppItemRepository itemRepository;
    private final VppCategoryService categoryService;
    private final StockService stockService;

    public VppItemService(
            VppItemRepository itemRepository,
            VppCategoryService categoryService,
            StockService stockService
    ) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
        this.stockService = stockService;
    }

    /*
     * ============================================================
     * ADMIN SEARCH
     * ============================================================
     */

    public Page<VppItemDTO> searchForAdmin(
            String keyword,
            Long categoryId,
            String status,
            boolean showDeleted,
            Pageable pageable
    ) {
        return itemRepository
                .searchForAdmin(
                        clean(keyword),
                        categoryId,
                        clean(status),
                        showDeleted,
                        pageable
                )
                .map(this::toDto);
    }

    /*
     * ============================================================
     * CUSTOMER SEARCH
     * ============================================================
     */

    public Page<VppItemDTO> searchForCustomer(
            String keyword,
            Long categoryId,
            boolean inStockOnly,
            String sort,
            Pageable pageable
    ) {
        Page<VppItemDTO> page =
                itemRepository
                        .searchForCustomer(
                                clean(keyword),
                                categoryId,
                                inStockOnly,
                                pageable
                        )
                        .map(this::toDto);

        return sortCustomerItems(
                page,
                sort
        );
    }

    /*
     * ============================================================
     * FIND PRODUCT
     * ============================================================
     */

    public VppItemDTO getById(
            Long id
    ) {
        return toDto(
                getEntityById(id)
        );
    }

    public VppItemDTO getCustomerVisibleById(
            Long id
    ) {
        VppItem item =
                getEntityById(id);

        boolean visible =
                !item.isDeleted()
                        && item.isActive()
                        && item.getCategory() != null
                        && item.getCategory().isActive();

        if (!visible) {
            throw new IllegalArgumentException(
                    "VPP product is not available."
            );
        }

        return toDto(item);
    }

    public VppItem getEntityById(
            Long id
    ) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "VPP product ID is required."
            );
        }

        return itemRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "VPP product not found."
                        )
                );
    }

    /*
     * ============================================================
     * CREATE PRODUCT
     * ============================================================
     */

    @Transactional
    public VppItemDTO create(
            VppItemDTO dto
    ) {
        return create(
                dto,
                null
        );
    }

    @Transactional
    public VppItemDTO create(
            VppItemDTO dto,
            MultipartFile imageFile
    ) {
        validateDto(dto);

        String name =
                clean(dto.getName());

        if (itemRepository
                .existsByNameIgnoreCase(name)) {

            throw new IllegalArgumentException(
                    "Product name already exists."
            );
        }

        VppItem item =
                new VppItem();

        /*
         * Khi tạo mới, stockQuantity trong DTO là số lượng ban đầu.
         */
        applyDtoToEntity(
                item,
                dto
        );

        applyImageToEntity(
                item,
                imageFile
        );

        item.setDeleted(false);

        VppItem savedItem =
                itemRepository.save(item);

        /*
         * Sau khi VPP có ID, tạo một dòng trong bảng stock:
         *
         * stock.vpp_item_id = savedItem.id
         * stock.book_id = NULL
         * stock.quantity = số lượng ban đầu
         */
        stockService.recordInitialVppStock(
                savedItem
        );

        return toDto(savedItem);
    }

    /*
     * ============================================================
     * UPDATE PRODUCT
     * ============================================================
     */

    @Transactional
    public VppItemDTO update(
            Long id,
            VppItemDTO dto
    ) {
        return update(
                id,
                dto,
                null
        );
    }

    @Transactional
    public VppItemDTO update(
            Long id,
            VppItemDTO dto,
            MultipartFile imageFile
    ) {
        validateDto(dto);

        VppItem item =
                getEntityById(id);

        String name =
                clean(dto.getName());

        if (itemRepository
                .existsByNameIgnoreCaseAndIdNot(
                        name,
                        id
                )) {

            throw new IllegalArgumentException(
                    "Product name already exists."
            );
        }

        /*
         * Nếu đây là VPP cũ chưa có dòng trong bảng stock,
         * hệ thống sẽ tạo dòng stock từ số lượng cũ trước.
         */
        stockService.recordInitialVppStock(
                item
        );

        /*
         * Số lượng thật được lấy từ bảng stock.
         *
         * Không dùng số lượng người dùng gửi từ form Edit.
         */
        int currentStock =
                stockService.getVppQuantity(id);

        /*
         * Cập nhật tên, danh mục, giá, nhà cung cấp và trạng thái.
         *
         * applyDtoToEntity() tạm thời gán stockQuantity từ form,
         * nên ngay sau đó phải đặt lại bằng currentStock.
         */
        applyDtoToEntity(
                item,
                dto
        );

        /*
         * Không cho trang Edit VPP thay đổi số lượng.
         *
         * Việc tăng hoặc giảm kho chỉ được thực hiện tại:
         *
         * /admin/stock
         */
        item.setStockQuantity(
                currentStock
        );

        applyImageToEntity(
                item,
                imageFile
        );

        VppItem savedItem =
                itemRepository.save(item);

        return toDto(savedItem);
    }

    /*
     * ============================================================
     * HIDE PRODUCT
     * ============================================================
     */

    @Transactional
    public void hide(
            Long id
    ) {
        VppItem item =
                getEntityById(id);

        item.setStatus(
                VppItem.STATUS_INACTIVE
        );

        itemRepository.save(item);
    }

    /*
     * ============================================================
     * RESTORE PRODUCT
     * ============================================================
     */

    @Transactional
    public void restore(
            Long id
    ) {
        VppItem item =
                getEntityById(id);

        item.setStatus(
                VppItem.STATUS_ACTIVE
        );

        item.setDeleted(false);

        VppItem savedItem =
                itemRepository.save(item);

        /*
         * Hỗ trợ VPP cũ chưa có dòng trong bảng stock.
         */
        stockService.recordInitialVppStock(
                savedItem
        );
    }

    /*
     * ============================================================
     * SOFT DELETE
     * ============================================================
     */

    @Transactional
    public void softDelete(
            Long id
    ) {
        VppItem item =
                getEntityById(id);

        item.setDeleted(true);

        item.setStatus(
                VppItem.STATUS_INACTIVE
        );

        /*
         * Không xóa dòng stock.
         *
         * Khi sản phẩm được restore, lượng tồn kho cũ vẫn còn.
         */
        itemRepository.save(item);
    }

    /*
     * ============================================================
     * APPLY DTO TO ENTITY
     * ============================================================
     */

    private void applyDtoToEntity(
            VppItem item,
            VppItemDTO dto
    ) {
        String categoryName =
                clean(dto.getCategoryName());

        if (categoryName == null
                || categoryName.isBlank()) {

            throw new IllegalArgumentException(
                    "Category is required."
            );
        }

        VppCategory category =
                categoryService
                        .findOrCreateByName(
                                categoryName
                        );

        item.setName(
                clean(dto.getName())
        );

        item.setCategory(
                category
        );

        item.setDescription(
                clean(dto.getDescription())
        );

        item.setPrice(
                dto.getPrice() == null
                        ? BigDecimal.ZERO
                        : dto.getPrice()
        );

        /*
         * Giá trị này chỉ được dùng làm tồn kho ban đầu khi Create.
         *
         * Khi Update, phương thức update() sẽ đặt lại bằng
         * stock.quantity ngay sau khi gọi applyDtoToEntity().
         */
        item.setStockQuantity(
                dto.getStockQuantity() == null
                        ? 0
                        : dto.getStockQuantity()
        );

        item.setSupplier(
                clean(dto.getSupplier())
        );

        String status =
                clean(dto.getStatus());

        if (status == null
                || status.isBlank()) {

            item.setStatus(
                    VppItem.STATUS_ACTIVE
            );

        } else if (
                VppItem.STATUS_ACTIVE
                        .equalsIgnoreCase(status)
                        || VppItem.STATUS_INACTIVE
                        .equalsIgnoreCase(status)
        ) {

            item.setStatus(
                    status.toUpperCase()
            );

        } else {
            throw new IllegalArgumentException(
                    "Status must be ACTIVE or INACTIVE."
            );
        }
    }

    /*
     * ============================================================
     * IMAGE HANDLING
     * ============================================================
     */

    private void applyImageToEntity(
            VppItem item,
            MultipartFile imageFile
    ) {
        if (imageFile == null
                || imageFile.isEmpty()) {

            return;
        }

        String originalFilename =
                imageFile.getOriginalFilename();

        if (originalFilename == null
                || originalFilename.isBlank()) {

            throw new IllegalArgumentException(
                    "Image file name is invalid."
            );
        }

        String extension =
                getExtension(
                        originalFilename
                );

        if (!ALLOWED_IMAGE_EXTENSIONS
                .contains(extension)) {

            throw new IllegalArgumentException(
                    "Only JPG, JPEG, PNG, GIF, WEBP, "
                            + "JFIF, AVIF, and BMP images are allowed."
            );
        }

        String contentType =
                imageFile.getContentType();

        if (contentType == null
                || !contentType
                .toLowerCase()
                .startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed."
            );
        }

        try {
            item.setImageData(
                    imageFile.getBytes()
            );

            item.setImageContentType(
                    contentType
            );

            item.setImageFileName(
                    originalFilename
            );

            /*
             * Ảnh mới được lưu trực tiếp trong database,
             * nên xóa đường dẫn ảnh cũ.
             */
            item.setImagePath(null);

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not save VPP image to database.",
                    exception
            );
        }
    }

    /*
     * ============================================================
     * ENTITY TO DTO
     * ============================================================
     */

    private VppItemDTO toDto(
            VppItem item
    ) {
        VppItemDTO dto =
                new VppItemDTO();

        dto.setId(
                item.getId()
        );

        dto.setName(
                item.getName()
        );

        dto.setDescription(
                item.getDescription()
        );

        dto.setPrice(
                item.getPrice()
        );

        /*
         * Hai cột stock_quantity cũ đang được StockService
         * đồng bộ tạm thời với stock.quantity.
         *
         * Vì vậy DTO và các query cũ vẫn hoạt động trong
         * giai đoạn chuyển đổi.
         */
        dto.setStockQuantity(
                item.getStockQuantity()
        );

        dto.setSupplier(
                item.getSupplier()
        );

        dto.setStatus(
                item.getStatus()
        );

        dto.setDeleted(
                item.isDeleted()
        );

        dto.setCreatedAt(
                item.getCreatedAt()
        );

        dto.setUpdatedAt(
                item.getUpdatedAt()
        );

        boolean hasImage =
                item.hasImageData();

        dto.setHasImage(
                hasImage
        );

        /*
         * Endpoint ảnh VPP đúng:
         *
         * /uploads/vpp/{id}/image
         */
        if (hasImage
                && item.getId() != null) {

            dto.setImagePath(
                    "/uploads/vpp/"
                            + item.getId()
                            + "/image"
            );

        } else {
            dto.setImagePath(
                    item.getImagePath()
            );
        }

        if (item.getCategory() != null) {
            dto.setCategoryId(
                    item.getCategory()
                            .getId()
            );

            dto.setCategoryName(
                    item.getCategory()
                            .getName()
            );
        }

        return dto;
    }

    /*
     * ============================================================
     * CUSTOMER SORT
     * ============================================================
     */

    private Page<VppItemDTO> sortCustomerItems(
            Page<VppItemDTO> page,
            String sort
    ) {
        List<VppItemDTO> content =
                new ArrayList<>(
                        page.getContent()
                );

        String cleanSort =
                clean(sort);

        if ("price_asc"
                .equalsIgnoreCase(cleanSort)) {

            content.sort(
                    Comparator.comparing(
                            VppItemDTO::getPrice,
                            Comparator.nullsLast(
                                    BigDecimal::compareTo
                            )
                    )
            );

        } else if ("price_desc"
                .equalsIgnoreCase(cleanSort)) {

            content.sort(
                    Comparator.comparing(
                            VppItemDTO::getPrice,
                            Comparator.nullsLast(
                                    BigDecimal::compareTo
                            )
                    ).reversed()
            );

        } else if ("name_asc"
                .equalsIgnoreCase(cleanSort)) {

            content.sort(
                    Comparator.comparing(
                            product ->
                                    product.getName() == null
                                            ? ""
                                            : product.getName(),
                            String.CASE_INSENSITIVE_ORDER
                    )
            );

        } else {
            content.sort(
                    Comparator.comparing(
                            VppItemDTO::getId,
                            Comparator.nullsLast(
                                    Long::compareTo
                            )
                    ).reversed()
            );
        }

        return new PageImpl<>(
                content,
                page.getPageable(),
                page.getTotalElements()
        );
    }

    /*
     * ============================================================
     * VALIDATION
     * ============================================================
     */

    private void validateDto(
            VppItemDTO dto
    ) {
        if (dto == null) {
            throw new IllegalArgumentException(
                    "VPP product data is required."
            );
        }

        String name =
                clean(dto.getName());

        if (name == null
                || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Product name is required."
            );
        }

        if (dto.getPrice() != null
                && dto.getPrice()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Price must be greater than or equal to 0."
            );
        }

        if (dto.getStockQuantity() != null
                && dto.getStockQuantity() < 0) {

            throw new IllegalArgumentException(
                    "Stock quantity must be greater than or equal to 0."
            );
        }
    }

    private String getExtension(
            String filename
    ) {
        int dotIndex =
                filename.lastIndexOf(".");

        if (dotIndex < 0
                || dotIndex
                == filename.length() - 1) {

            throw new IllegalArgumentException(
                    "Image file must have an extension."
            );
        }

        return filename
                .substring(dotIndex + 1)
                .toLowerCase();
    }

    private String clean(
            String value
    ) {
        return value == null
                ? null
                : value.trim();
    }
}