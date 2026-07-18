package shop.controller.admin;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.OrderStatus;
import shop.domain.dto.OrderDTO;
import shop.service.OrderService;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private static final int PAGE_SIZE = 10;

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false, defaultValue = "default") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        List<OrderDTO> all = orderService.searchOrders(keyword, status, sort);
        int totalItems = all.size();
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (totalPages > 0) {
            page = Math.max(0, Math.min(page, totalPages - 1));
        } else {
            page = 0;
        }
        int from = page * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, totalItems);

        model.addAttribute("orders", totalItems == 0 ? List.of() : all.subList(from, to));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("fromItem", totalItems == 0 ? 0 : from + 1);
        model.addAttribute("toItem", to);
        model.addAttribute("orderStatuses", OrderStatus.values());
        return "admin/order/list";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return orderService.getOrderById(id)
                .map(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("orderStatuses", OrderStatus.values());
                    return "admin/order/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
                    return "redirect:/admin/orders";
                });
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}
