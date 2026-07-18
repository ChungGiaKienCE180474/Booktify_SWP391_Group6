package shop.controller.admin;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shop.domain.Category;
import shop.domain.Promotion;
import shop.service.CategoryService;
import shop.service.PromotionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/promotions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromotionController {

    private final PromotionService promotionService;
    private final CategoryService categoryService;

    public AdminPromotionController(
            PromotionService promotionService,
            CategoryService categoryService) {

        this.promotionService = promotionService;
        this.categoryService = categoryService;
    }

    /*
     * Hiển thị danh sách promotion.
     * list.jsp dùng modal tạo và chỉnh sửa promotion.
     */
    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "promotions",
                promotionService.getAllPromotions()
        );

        model.addAttribute(
                "categories",
                getActiveCategories()
        );

        if (!model.containsAttribute("promotion")) {
            model.addAttribute(
                    "promotion",
                    new Promotion()
            );
        }

        if (!model.containsAttribute("selectedCategoryIds")) {
            model.addAttribute(
                    "selectedCategoryIds",
                    new ArrayList<Long>()
            );
        }

        model.addAttribute(
                "formMode",
                "create"
        );

        return "admin/promotion/list";
    }

    /*
     * Hiển thị trang form tạo promotion riêng.
     */
    @GetMapping("/create")
    public String createForm(Model model) {

        model.addAttribute(
                "promotion",
                new Promotion()
        );

        model.addAttribute(
                "categories",
                getActiveCategories()
        );

        model.addAttribute(
                "selectedCategoryIds",
                new ArrayList<Long>()
        );

        model.addAttribute(
                "formMode",
                "create"
        );

        return "admin/promotion/form";
    }

    /*
     * Tạo promotion mới theo thể loại.
     */
    @PostMapping
    public String create(
            @ModelAttribute("promotion")
            @Valid Promotion promotion,

            BindingResult bindingResult,

            @RequestParam(
                    value = "categoryIds",
                    required = false
            )
            List<Long> categoryIds,

            Model model,
            RedirectAttributes redirectAttributes) {

        List<Category> selectedCategories =
                getSelectedCategories(categoryIds);

        validatePromotion(
                promotion,
                null,
                categoryIds,
                selectedCategories,
                bindingResult
        );

        if (bindingResult.hasErrors()) {

            populateListPage(
                    model,
                    categoryIds,
                    "create"
            );

            model.addAttribute(
                    "errorMessage",
                    getFirstErrorMessage(bindingResult)
            );

            return "admin/promotion/list";
        }

        /*
         * Xóa dữ liệu category cũ nếu có,
         * sau đó gán các category được chọn.
         */
        promotion.getApplicableCategories().clear();
        promotion.getApplicableCategories().addAll(
                selectedCategories
        );

        promotionService.savePromotion(
                promotion
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Promotion created successfully for "
                        + selectedCategories.size()
                        + " selected category(s)."
        );

        return "redirect:/admin/promotions";
    }

    /*
     * Hiển thị trang chỉnh sửa promotion.
     */
    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Promotion promotion =
                promotionService
                        .getPromotionById(id)
                        .orElse(null);

        if (promotion == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Promotion not found."
            );

            return "redirect:/admin/promotions";
        }

        List<Long> selectedCategoryIds;

        if (promotion.getApplicableCategories() == null) {

            selectedCategoryIds =
                    new ArrayList<>();

        } else {

            selectedCategoryIds =
                    promotion
                            .getApplicableCategories()
                            .stream()
                            .filter(category ->
                                    category != null
                                            && category.getId() != null
                            )
                            .map(Category::getId)
                            .toList();
        }

        model.addAttribute(
                "promotion",
                promotion
        );

        model.addAttribute(
                "categories",
                getActiveCategories()
        );

        model.addAttribute(
                "selectedCategoryIds",
                selectedCategoryIds
        );

        model.addAttribute(
                "formMode",
                "edit"
        );

        model.addAttribute(
                "editId",
                id
        );

        return "admin/promotion/form";
    }

    /*
     * Cập nhật promotion.
     */
    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,

            @ModelAttribute("promotion")
            @Valid Promotion promotion,

            BindingResult bindingResult,

            @RequestParam(
                    value = "categoryIds",
                    required = false
            )
            List<Long> categoryIds,

            Model model,
            RedirectAttributes redirectAttributes) {

        Promotion existing =
                promotionService
                        .getPromotionById(id)
                        .orElse(null);

        if (existing == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Promotion not found."
            );

            return "redirect:/admin/promotions";
        }

        List<Category> selectedCategories =
                getSelectedCategories(categoryIds);

        validatePromotion(
                promotion,
                existing,
                categoryIds,
                selectedCategories,
                bindingResult
        );

        if (bindingResult.hasErrors()) {

            populateListPage(
                    model,
                    categoryIds,
                    "edit"
            );

            model.addAttribute(
                    "editId",
                    id
            );

            model.addAttribute(
                    "errorMessage",
                    getFirstErrorMessage(bindingResult)
            );

            return "admin/promotion/list";
        }

        existing.setName(
                promotion.getName()
        );

        existing.setDescription(
                promotion.getDescription()
        );

        existing.setDiscountValue(
                promotion.getDiscountValue()
        );

        existing.setPercentage(
                promotion.isPercentage()
        );

        existing.setStartDate(
                promotion.getStartDate()
        );

        existing.setEndDate(
                promotion.getEndDate()
        );

        existing.setActive(
                promotion.isActive()
        );

        /*
         * Xóa danh sách category cũ
         * và gán lại danh sách category mới.
         */
        existing.getApplicableCategories().clear();
        existing.getApplicableCategories().addAll(
                selectedCategories
        );

        promotionService.savePromotion(
                existing
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Promotion updated successfully for "
                        + selectedCategories.size()
                        + " selected category(s)."
        );

        return "redirect:/admin/promotions";
    }

    /*
     * Xóa mềm promotion.
     */
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            Promotion promotion =
                    promotionService
                            .getPromotionById(id)
                            .orElse(null);

            if (promotion == null) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Promotion not found."
                );

                return "redirect:/admin/promotions";
            }

            promotionService.softDeletePromotion(
                    id
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Promotion deleted successfully."
            );

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "An error occurred while deleting the promotion."
            );
        }

        return "redirect:/admin/promotions";
    }

    /*
     * Kiểm tra toàn bộ dữ liệu promotion.
     */
    private void validatePromotion(
            Promotion promotion,
            Promotion existingPromotion,
            List<Long> categoryIds,
            List<Category> selectedCategories,
            BindingResult bindingResult) {

        validatePromotionName(
                promotion,
                existingPromotion,
                bindingResult
        );

        validatePromotionDates(
                promotion,
                bindingResult
        );

        validateDiscountValue(
                promotion,
                bindingResult
        );

        validateSelectedCategories(
                categoryIds,
                selectedCategories,
                bindingResult
        );

        validateCategoryConflicts(
                promotion,
                existingPromotion,
                selectedCategories,
                bindingResult
        );
    }

    /*
     * Kiểm tra tên promotion bị trùng.
     */
    private void validatePromotionName(
            Promotion promotion,
            Promotion existingPromotion,
            BindingResult bindingResult) {

        if (promotion.getName() == null
                || promotion.getName().isBlank()) {

            return;
        }

        String promotionName =
                promotion.getName().trim();

        promotion.setName(
                promotionName
        );

        boolean duplicateName;

        if (existingPromotion == null) {

            duplicateName =
                    promotionService
                            .existsByNameIgnoreCase(
                                    promotionName
                            );

        } else {

            boolean nameChanged =
                    existingPromotion.getName() == null
                            || !existingPromotion
                            .getName()
                            .equalsIgnoreCase(
                                    promotionName
                            );

            duplicateName =
                    nameChanged
                            && promotionService
                            .existsByNameIgnoreCase(
                                    promotionName
                            );
        }

        if (duplicateName) {

            bindingResult.rejectValue(
                    "name",
                    "promotion.name.exists",
                    "Promotion name already exists."
            );
        }
    }

    /*
     * Kiểm tra ngày bắt đầu và ngày kết thúc.
     */
    private void validatePromotionDates(
            Promotion promotion,
            BindingResult bindingResult) {

        if (promotion.getStartDate() == null
                || promotion.getEndDate() == null) {

            return;
        }

        if (!promotion
                .getEndDate()
                .isAfter(
                        promotion.getStartDate()
                )) {

            bindingResult.rejectValue(
                    "endDate",
                    "promotion.endDate.invalid",
                    "End date must be after start date."
            );
        }
    }

    /*
     * Kiểm tra giá trị giảm.
     */
    private void validateDiscountValue(
            Promotion promotion,
            BindingResult bindingResult) {

        BigDecimal discountValue =
                promotion.getDiscountValue();

        if (discountValue == null) {
            return;
        }

        if (discountValue.compareTo(BigDecimal.ZERO) < 0) {

            bindingResult.rejectValue(
                    "discountValue",
                    "promotion.discountValue.negative",
                    "Discount value cannot be negative."
            );

            return;
        }

        if (promotion.isPercentage()
                && discountValue.compareTo(
                BigDecimal.valueOf(100)
        ) > 0) {

            bindingResult.rejectValue(
                    "discountValue",
                    "promotion.discountValue.invalid",
                    "Percentage discount cannot exceed 100%."
            );
        }
    }

    /*
     * Kiểm tra category đã chọn có hợp lệ không.
     */
    private void validateSelectedCategories(
            List<Long> categoryIds,
            List<Category> selectedCategories,
            BindingResult bindingResult) {

        if (categoryIds == null
                || categoryIds.isEmpty()) {

            bindingResult.reject(
                    "promotion.categories.required",
                    "Please select at least one category."
            );

            return;
        }

        Set<Long> uniqueCategoryIds =
                new LinkedHashSet<>(
                        categoryIds
                );

        uniqueCategoryIds.remove(null);

        if (uniqueCategoryIds.isEmpty()) {

            bindingResult.reject(
                    "promotion.categories.required",
                    "Please select at least one category."
            );

            return;
        }

        if (selectedCategories.size()
                != uniqueCategoryIds.size()) {

            bindingResult.reject(
                    "promotion.categories.invalid",
                    "One or more selected categories are invalid or inactive."
            );
        }
    }

    /*
     * Không cho một category chạy nhiều promotion
     * trong cùng khoảng thời gian.
     */
    private void validateCategoryConflicts(
            Promotion promotion,
            Promotion existingPromotion,
            List<Category> selectedCategories,
            BindingResult bindingResult) {

        if (promotion.getStartDate() == null
                || promotion.getEndDate() == null
                || selectedCategories == null
                || selectedCategories.isEmpty()) {

            return;
        }

        /*
         * Không kiểm tra conflict khi ngày đã sai.
         */
        if (!promotion
                .getEndDate()
                .isAfter(
                        promotion.getStartDate()
                )) {

            return;
        }

        Long excludePromotionId =
                existingPromotion != null
                        ? existingPromotion.getId()
                        : null;

        for (Category category : selectedCategories) {

            boolean hasConflict =
                    promotionService.hasCategoryConflict(
                            category.getId(),
                            promotion.getStartDate(),
                            promotion.getEndDate(),
                            excludePromotionId
                    );

            if (hasConflict) {

                bindingResult.reject(
                        "promotion.category.conflict",
                        "Category \""
                                + category.getName()
                                + "\" already has another promotion "
                                + "during the selected period."
                );

                /*
                 * Chỉ hiển thị lỗi conflict đầu tiên.
                 */
                break;
            }
        }
    }

    /*
     * Lấy danh sách category đang active.
     */
    private List<Category> getActiveCategories() {

        return categoryService
                .getAllCategories()
                .stream()
                .filter(Category::isActive)
                .toList();
    }

    /*
     * Lấy danh sách Category từ categoryIds.
     * Chỉ nhận category đang active.
     */
    private List<Category> getSelectedCategories(
            List<Long> categoryIds) {

        List<Category> selectedCategories =
                new ArrayList<>();

        if (categoryIds == null
                || categoryIds.isEmpty()) {

            return selectedCategories;
        }

        Set<Long> uniqueCategoryIds =
                new LinkedHashSet<>(
                        categoryIds
                );

        uniqueCategoryIds.remove(null);

        for (Long categoryId : uniqueCategoryIds) {

            categoryService
                    .getCategoryById(categoryId)
                    .filter(Category::isActive)
                    .ifPresent(
                            selectedCategories::add
                    );
        }

        return selectedCategories;
    }

    /*
     * Nạp lại dữ liệu cho list.jsp khi có lỗi.
     */
    private void populateListPage(
            Model model,
            List<Long> selectedCategoryIds,
            String formMode) {

        model.addAttribute(
                "promotions",
                promotionService.getAllPromotions()
        );

        model.addAttribute(
                "categories",
                getActiveCategories()
        );

        model.addAttribute(
                "selectedCategoryIds",
                selectedCategoryIds != null
                        ? selectedCategoryIds
                        : new ArrayList<Long>()
        );

        model.addAttribute(
                "formMode",
                formMode
        );
    }

    /*
     * Lấy thông báo lỗi đầu tiên.
     */
    private String getFirstErrorMessage(
            BindingResult bindingResult) {

        if (bindingResult.getAllErrors().isEmpty()) {

            return "Invalid promotion data.";
        }

        String message =
                bindingResult
                        .getAllErrors()
                        .get(0)
                        .getDefaultMessage();

        return message != null
                ? message
                : "Invalid promotion data.";
    }
}