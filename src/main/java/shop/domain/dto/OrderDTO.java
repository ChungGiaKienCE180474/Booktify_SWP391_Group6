package shop.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO truyền dữ liệu đơn hàng sang JSP — không chứa entity JPA.
 * <p>
 * OrderService map từ {@link shop.domain.Order} qua {@code toSummaryDTO} (danh sách)
 * hoặc {@code toDetailDTO} (trang chi tiết, kèm {@link OrderItemDTO}).
 * Các trường *Formatted đã format tiền VND để hiển thị trực tiếp trên view.
 */
public class OrderDTO {

    private Long id;
    /** Mã đơn ORD-yyyyMMdd-xxxxxx */
    private String orderCode;
    /** Giá trị enum OrderStatus.name() — vd: PENDING */
    private String status;
    /** Nhãn hiển thị — vd: Pending confirmation */
    private String statusLabel;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentMethodLabel;
    /** Giá trị enum PaymentStatus.name() — vd: UNPAID */
    private String paymentStatus;
    /** Nhãn hiển thị — vd: Awaiting confirmation */
    private String paymentStatusLabel;
    /** Khách được phép bấm "Tôi đã thanh toán" (COD, đang giao/đã giao, chưa thanh toán). */
    private boolean canCustomerConfirmPayment;
    /** Admin được phép bấm "Hoàn thành" (COD, khách đã báo thanh toán). */
    private boolean canAdminCompletePayment;
    private String shippingMethodLabel;
    private String voucherCode;
    /** Tổng hàng sau KM từng sách (đã format) */
    private String subtotalFormatted;
    /** Giảm giá voucher (đã format) */
    private String discountAmountFormatted;
    private String shippingFeeFormatted;
    /** Tổng thanh toán cuối (đã format) */
    private String totalAmountFormatted;
    private String createdAtFormatted;
    /** Chỉ có trên view Admin/Staff */
    private String customerEmail;
    private String customerName;
    private String note;
    /** Danh sách sản phẩm — chỉ có trong toDetailDTO */
    private List<OrderItemDTO> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethodLabel() {
        return paymentMethodLabel;
    }

    public void setPaymentMethodLabel(String paymentMethodLabel) {
        this.paymentMethodLabel = paymentMethodLabel;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatusLabel() {
        return paymentStatusLabel;
    }

    public void setPaymentStatusLabel(String paymentStatusLabel) {
        this.paymentStatusLabel = paymentStatusLabel;
    }

    public boolean isCanCustomerConfirmPayment() {
        return canCustomerConfirmPayment;
    }

    public void setCanCustomerConfirmPayment(boolean canCustomerConfirmPayment) {
        this.canCustomerConfirmPayment = canCustomerConfirmPayment;
    }

    public boolean isCanAdminCompletePayment() {
        return canAdminCompletePayment;
    }

    public void setCanAdminCompletePayment(boolean canAdminCompletePayment) {
        this.canAdminCompletePayment = canAdminCompletePayment;
    }

    public String getShippingMethodLabel() {
        return shippingMethodLabel;
    }

    public void setShippingMethodLabel(String shippingMethodLabel) {
        this.shippingMethodLabel = shippingMethodLabel;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getSubtotalFormatted() {
        return subtotalFormatted;
    }

    public void setSubtotalFormatted(String subtotalFormatted) {
        this.subtotalFormatted = subtotalFormatted;
    }

    public String getDiscountAmountFormatted() {
        return discountAmountFormatted;
    }

    public void setDiscountAmountFormatted(String discountAmountFormatted) {
        this.discountAmountFormatted = discountAmountFormatted;
    }

    public String getShippingFeeFormatted() {
        return shippingFeeFormatted;
    }

    public void setShippingFeeFormatted(String shippingFeeFormatted) {
        this.shippingFeeFormatted = shippingFeeFormatted;
    }

    public String getTotalAmountFormatted() {
        return totalAmountFormatted;
    }

    public void setTotalAmountFormatted(String totalAmountFormatted) {
        this.totalAmountFormatted = totalAmountFormatted;
    }

    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }

    public void setCreatedAtFormatted(String createdAtFormatted) {
        this.createdAtFormatted = createdAtFormatted;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
