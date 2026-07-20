package shop.controller.client;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.dto.VppCategoryDTO;
import shop.domain.dto.VppItemDTO;
import shop.service.VppCategoryService;
import shop.service.VppItemService;

@Controller
@RequestMapping("/customer/vpp")
public class CustomerVppController {

    private final VppItemService vppItemService;
    private final VppCategoryService vppCategoryService;

    public CustomerVppController(
            VppItemService vppItemService,
            VppCategoryService vppCategoryService
    ) {
        this.vppItemService = vppItemService;
        this.vppCategoryService = vppCategoryService;
    }

    @GetMapping
    public String list(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "stock", required = false) String stock,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            Model model
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 12 : Math.min(size, 48);

        boolean inStockOnly =
                "in_stock".equalsIgnoreCase(stock)
                        || "true".equalsIgnoreCase(stock);

        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<VppItemDTO> itemPage = vppItemService.searchForCustomer(
                q,
                categoryId,
                inStockOnly,
                sort,
                pageable
        );

        List<VppCategoryDTO> categories = vppCategoryService.getActiveCategories();

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("vppCategories", categories);

        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("stock", inStockOnly ? "in_stock" : "");
        model.addAttribute("sort", sort);
        model.addAttribute("size", safeSize);

        model.addAttribute("currentPage", itemPage.getNumber());
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());

        return "vpp/customer/list";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            VppItemDTO item = vppItemService.getCustomerVisibleById(id);

            Page<VppItemDTO> relatedPage = vppItemService.searchForCustomer(
                    null,
                    item.getCategoryId(),
                    false,
                    "newest",
                    PageRequest.of(0, 8)
            );

            List<VppItemDTO> relatedItems = relatedPage.getContent()
                    .stream()
                    .filter(related -> related.getId() != null && !related.getId().equals(item.getId()))
                    .limit(4)
                    .toList();

            model.addAttribute("item", item);
            model.addAttribute("relatedItems", relatedItems);

            return "vpp/customer/detail";

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/customer/vpp";
        }
    }
}