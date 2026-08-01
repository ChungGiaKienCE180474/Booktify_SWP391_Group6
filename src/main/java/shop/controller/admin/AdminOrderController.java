// Package controller admin — quản lý đơn hàng
package shop.controller.admin;

import java.util.List; // Danh sách OrderDTO cho phân trang

import org.springframework.security.access.prepost.PreAuthorize; // Kiểm tra role trước khi vào method
import org.springframework.stereotype.Controller; // Đánh dấu class là Spring MVC Controller
import org.springframework.ui.Model; // Truyền dữ liệu sang JSP
import org.springframework.web.bind.annotation.GetMapping; // Map HTTP GET
import org.springframework.web.bind.annotation.PathVariable; // Lấy {id} từ URL
import org.springframework.web.bind.annotation.PostMapping; // Map HTTP POST
import org.springframework.web.bind.annotation.RequestMapping; // Prefix URL cho class
import org.springframework.web.bind.annotation.RequestParam; // Query param / form param
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Flash message sau redirect
import shop.domain.OrderStatus; // Enum trạng thái đơn + allowedTransitions()
import shop.domain.dto.OrderDTO; // DTO hiển thị trên view (không dùng entity trực tiếp)
import shop.service.OrderService; // Logic nghiệp vụ đơn hàng

/**
 * Quản lý đơn hàng phía Admin — prefix {@code /admin/orders}.
 * Chỉ role ADMIN truy cập được. Logic nghiệp vụ ủy thác cho OrderService.
 */
@Controller // Spring scan và đăng ký bean Controller
@RequestMapping("/admin/orders") // Mọi URL trong class bắt đầu bằng /admin/orders
@PreAuthorize("hasRole('ADMIN')") // Chặn nếu user không có ROLE_ADMIN
public class AdminOrderController {

    private static final int PAGE_SIZE = 10; // Số đơn hiển thị mỗi trang

    private final OrderService orderService; // Inject service xử lý DB + business

    // Constructor injection — Spring tự truyền OrderService
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService; // Gán vào field final
    }

    /**
     * GET /admin/orders — danh sách đơn hàng có tìm kiếm, lọc status, sắp xếp.
     */
    @GetMapping // Map GET /admin/orders (không cần path thêm vì đã có @RequestMapping class)
    public String listOrders(
            @RequestParam(required = false) String keyword, // Từ khóa tìm kiếm (optional)
            @RequestParam(required = false, defaultValue = "all") String status, // Lọc status, mặc định all
            @RequestParam(required = false, defaultValue = "default") String sort, // Kiểu sắp xếp
            @RequestParam(defaultValue = "0") int page, // Trang hiện tại (0-based)
            Model model) { // Model chứa data cho JSP

        // Gọi service: load all đơn + filter keyword/status + sort
        List<OrderDTO> all = orderService.searchOrders(keyword, status, sort);
        int totalItems = all.size(); // Tổng số đơn sau filter
        // Tính tổng số trang: làm tròn lên totalItems / PAGE_SIZE
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (totalPages > 0) { // Có ít nhất 1 trang
            // Đảm bảo page nằm trong [0, totalPages-1] — tránh IndexOutOfBounds
            page = Math.max(0, Math.min(page, totalPages - 1));
        } else { // Không có đơn nào
            page = 0; // Reset về trang 0
        }
        int from = page * PAGE_SIZE; // Chỉ số bắt đầu subList (vd page=1 -> from=10)
        int to = Math.min(from + PAGE_SIZE, totalItems); // Chỉ số kết thúc (exclusive)

        // Danh sách 10 đơn của trang hiện tại; List.of() nếu rỗng
        model.addAttribute("orders", totalItems == 0 ? List.of() : all.subList(from, to));
        model.addAttribute("keyword", keyword); // Giữ giá trị ô search trên form
        model.addAttribute("status", status); // Giữ dropdown status đã chọn
        model.addAttribute("sort", sort); // Giữ dropdown sort đã chọn
        model.addAttribute("currentPage", page); // Trang đang xem — dùng cho nút Prev/Next
        model.addAttribute("totalPages", totalPages); // Tổng số trang
        model.addAttribute("totalItems", totalItems); // Tổng số đơn (sau filter)
        model.addAttribute("fromItem", totalItems == 0 ? 0 : from + 1); // STT đơn đầu trang (1-based)
        model.addAttribute("toItem", to); // STT đơn cuối trang
        model.addAttribute("orderStatuses", OrderStatus.values()); // Mảng enum cho dropdown lọc
        return "admin/order/list"; // Resolve JSP: WEB-INF/view/admin/order/list.jsp
    }

    /**
     * GET /admin/orders/{id} — chi tiết một đơn hàng.
     */
    @GetMapping("/{id}") // id là primary key bảng orders
    public String orderDetail(
            @PathVariable Long id, // Spring bind {id} từ URL vào biến
            Model model,
            RedirectAttributes redirectAttributes) {

        // Optional: có đơn thì map, không có thì orElseGet
        return orderService.getOrderById(id)
                .map(order -> { // Lambda khi tìm thấy đơn
                    model.addAttribute("order", order); // OrderDTO đầy đủ items, customer...
                    model.addAttribute("orderStatuses", OrderStatus.values()); // Hiển thị label status
                    model.addAttribute(
                            "allowedNextStatuses",
                            orderService.getAllowedNextStatuses(
                                    order.getStatus(),
                                    order.getPaymentMethod()
                            )
                    );
                    return "admin/order/detail"; // View chi tiết
                })
                .orElseGet(() -> { // Lambda khi không tìm thấy đơn
                    redirectAttributes.addFlashAttribute("errorMessage", "Order not found."); // Flash 1 lần
                    return "redirect:/admin/orders"; // Quay về danh sách
                });
    }

    /**
     * POST /admin/orders/{id}/status — admin đổi trạng thái đơn.
     */
    @PostMapping("/{id}/status") // Form submit status mới
    public String updateStatus(
            @PathVariable Long id, // ID đơn cần cập nhật
            @RequestParam String status, // Trạng thái mới: CONFIRMED, SHIPPING, CANCELLED...
            RedirectAttributes redirectAttributes) {

        try {
            // Service: validate canTransitionTo, hoàn stock nếu CANCELLED, save DB
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
        } catch (IllegalArgumentException ex) {
            // Lỗi nghiệp vụ: chuyển status không hợp lệ, đơn không tồn tại...
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/orders/" + id; // PRG — quay lại trang detail cùng đơn
    }
}
