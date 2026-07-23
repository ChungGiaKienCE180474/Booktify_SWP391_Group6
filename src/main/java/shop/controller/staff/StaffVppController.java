package shop.controller.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.domain.dto.VppItemDTO;
import shop.service.VppCategoryService;
import shop.service.VppItemService;

@Controller
@RequestMapping("/staff/vpp")
@PreAuthorize("hasRole('STAFF')")
public class StaffVppController {

    private static final int PAGE_SIZE = 10;

    private final VppItemService vppItemService;
    private final VppCategoryService vppCategoryService;

    public StaffVppController(
            VppItemService vppItemService,
            VppCategoryService vppCategoryService
    ) {
        this.vppItemService = vppItemService;
        this.vppCategoryService = vppCategoryService;
    }

    @GetMapping
    public String list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        if (page < 0) {
            page = 0;
        }

        Page<VppItemDTO> itemPage = vppItemService.searchForAdmin(
                q,
                categoryId,
                status,
                false,
                PageRequest.of(page, PAGE_SIZE)
        );

        long totalItems = itemPage.getTotalElements();

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("categories", vppCategoryService.getActiveCategories());

        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("status", status);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("fromItem", totalItems == 0 ? 0 : page * PAGE_SIZE + 1);
        model.addAttribute("toItem", Math.min((page + 1) * PAGE_SIZE, (int) totalItems));

        return "layout/staff/vpp/list";
    }
}