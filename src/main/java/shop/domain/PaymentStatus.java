package shop.domain;

/**
 * Trạng thái thanh toán của đơn — lưu dạng String trong cột {@code orders.payment_status}.
 * <p>
 * Luồng COD: UNPAID → (khách bấm "Tôi đã thanh toán") → AWAITING_CONFIRMATION
 *            → (admin bấm "Hoàn thành") → PAID.
 * <p>
 * Luồng VNPay: UNPAID → (thanh toán online thành công) → PAID (bỏ qua bước chờ xác nhận).
 */
public enum PaymentStatus {
    /** Chưa thanh toán / chưa thu tiền. */
    UNPAID("Unpaid"),
    /** Khách đã báo đã thanh toán — chờ admin xác nhận. */
    AWAITING_CONFIRMATION("Awaiting confirmation"),
    /** Đã thu tiền / đã thanh toán — trạng thái cuối. */
    PAID("Paid");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    /** Nhãn hiển thị trên JSP. */
    public String getLabel() {
        return label;
    }

    /** Parse String từ DB; mặc định UNPAID nếu null/không hợp lệ (dữ liệu cũ). */
    public static PaymentStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            return UNPAID;
        }
        try {
            return PaymentStatus.valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            return UNPAID;
        }
    }
}
