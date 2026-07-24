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
     * Displays the promotion list.
     * list.jsp contains the create and edit promotion modal.
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
     * Displays the separate promotion creation form.
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
     * Creates a new category-based promotion.
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
         * Clears any existing category data and assigns
         * the categories selected by the administrator.
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
     * Displays the promotion edit form.
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
     * Updates an existing promotion.
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
         * Replaces the previous category list
         * with the newly selected categories.
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
     * Soft-deletes a promotion.
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
     * Validates all promotion data.
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
     * Validates that the promotion name is unique.
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
     * Validates the promotion start and end dates.
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
     * Validates the promotion discount value.
     */
    private void validateDiscountValue(
            Promotion promotion,
            BindingResult bindingResult) {

        BigDecimal discountValue =
                promotion.getDiscountValue();

        if (discountValue == null) {
            return;
        }

        if (discountValue.compareTo(BigDecimal.ZERO) <= 0) {

            bindingResult.rejectValue(
                    "discountValue",
                    "promotion.discountValue.minimum",
                    "Discount value must be greater than zero."
            );

            return;
        }

        if (promotion.isPercentage()
                && discountValue.compareTo(
                new BigDecimal("100")
        ) > 0) {

            bindingResult.rejectValue(
                    "discountValue",
                    "promotion.discountValue.maximum",
                    "Percentage discount must not exceed 100%."
            );
        }
    }

    /*
     * Validates the selected categories.
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
     * Prevents overlapping promotions of the same type
     * for the same category.
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
         * Skips conflict validation when the date range is invalid.
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
                            promotion.isPercentage(),
                            promotion.getStartDate(),
                            promotion.getEndDate(),
                            excludePromotionId
                    );

            if (hasConflict) {

                String promotionType =
                        promotion.isPercentage()
                                ? "percentage"
                                : "fixed amount";

                bindingResult.reject(
                        "promotion.category.conflict",
                        "Category \""
                                + category.getName()
                                + "\" already has an overlapping "
                                + promotionType
                                + " promotion during the selected period."
                );

                /*
                 * Displays only the first conflict error.
                 */
                break;
            }
        }
    }

    /*
     * Returns all active categories.
     */
    private List<Category> getActiveCategories() {

        return categoryService
                .getAllCategories()
                .stream()
                .filter(Category::isActive)
                .toList();
    }

    /*
     * Resolves category IDs into active Category entities.
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
     * Repopulates list.jsp data after validation errors.
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
     * Returns the first validation error message.
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