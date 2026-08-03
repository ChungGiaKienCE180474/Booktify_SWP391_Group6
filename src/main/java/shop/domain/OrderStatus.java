package shop.domain;

import java.util.EnumSet;
import java.util.Set;

/**
 * Trạng thái vòng đời đơn hàng — lưu dạng String trong bảng {@code orders.status}.
 * <p>
 * Luồng chính: PENDING → CONFIRMED → SHIPPING → DELIVERED.
 * Có thể chuyển sang CANCELLED từ PENDING, CONFIRMED hoặc SHIPPING.
 * <p>
 * Customer chỉ được hủy khi {@link #canBeCancelled()} (status = PENDING).
 * Admin/Staff cập nhật status qua {@link shop.service.OrderService#updateOrderStatus}.
 */
public enum OrderStatus {
    /** Đơn mới tạo sau checkout — chờ shop xác nhận. */
    PENDING("Pending confirmation"),
    /** Shop đã xác nhận, chuẩn bị giao. */
    CONFIRMED("Confirmed"),
    /** Đang vận chuyển. */
    SHIPPING("Shipping"),
    /** Đã giao thành công — trạng thái cuối, không chuyển tiếp. */
    DELIVERED("Delivered"),
    /** Đã hủy — trạng thái cuối; kho được hoàn lại khi chuyển sang đây. */
    CANCELLED("Cancelled");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    /** Nhãn hiển thị trên JSP (tiếng Anh). */
    public String getLabel() {
        return label;
    }

    /** DELIVERED và CANCELLED không thể chuyển sang trạng thái khác. */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Kiểm tra chuyển trạng thái hợp lệ — dùng trong updateOrderStatus().
     * Cùng trạng thái (this == target) luôn được phép (no-op).
     */
    public boolean canTransitionTo(OrderStatus target) {
        if (target == null) {
            return false;
        }
        if (this == target) {
            return true;
        }
        if (isTerminal()) {
            return false;
        }
        return switch (this) {
            case PENDING -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED -> target == SHIPPING || target == CANCELLED;
            case SHIPPING -> target == DELIVERED || target == CANCELLED;
            default -> false;
        };
    }

    /** Các trạng thái tiếp theo hợp lệ — dùng cho dropdown Admin/Staff. */
    public Set<OrderStatus> allowedTransitions() {
        if (isTerminal()) {
            return EnumSet.noneOf(OrderStatus.class);
        }
        return switch (this) {
            case PENDING -> EnumSet.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> EnumSet.of(SHIPPING, CANCELLED);
            case SHIPPING -> EnumSet.of(DELIVERED, CANCELLED);
            default -> EnumSet.noneOf(OrderStatus.class);
        };
    }

    /** Customer chỉ hủy được đơn PENDING — OrderController.canCancelOrder(). */
    public boolean canBeCancelled() {
        return this == PENDING;
    }

    /** Parse String từ DB/form; ném IllegalArgumentException nếu không hợp lệ. */
    public static OrderStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Invalid order status.");
        }
        try {
            return OrderStatus.valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid order status.");
        }
    }
}
