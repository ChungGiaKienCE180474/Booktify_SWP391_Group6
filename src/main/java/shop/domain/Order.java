package shop.domain;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Entity đơn hàng — bảng {@code orders}.
 * <p>
 * Một Order gồm thông tin giao hàng, thanh toán, trạng thái và danh sách
 * {@link OrderItem}. Được tạo bởi {@link shop.service.OrderService#createOrderFromCart}.
 * Trạng thái tuân theo state machine {@link OrderStatus}.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mã đơn hiển thị cho user, dạng ORD-yyyyMMdd-xxxxxx, unique trên DB. */
    @Column(name = "order_code", nullable = false, unique = true, length = 32)
    private String orderCode;

    /** Customer đặt đơn — FK users.id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Tên người nhận hàng (snapshot từ CheckoutForm, có thể khác fullName user). */
    @Column(name = "recipient_name", nullable = false, length = 150)
    private String recipientName;

    /** SĐT người nhận — dùng liên hệ giao hàng. */
    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    /** Địa chỉ giao hàng đầy đủ. */
    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    /** Phương thức thanh toán — hiện chỉ lưu {@code COD}. */
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    /** Phương thức vận chuyển — hiện mặc định {@code STANDARD}. */
    @Column(name = "shipping_method", nullable = false, length = 30)
    private String shippingMethod;

    /** Trạng thái đơn — giá trị enum {@link OrderStatus#name()}. */
    @Column(nullable = false, length = 30)
    private String status;

    /** Trạng thái thanh toán — giá trị enum {@link PaymentStatus#name()}. */
    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus = PaymentStatus.UNPAID.name();

    /** Thời điểm xác nhận đã thu tiền (admin/VNPay); null khi chưa thanh toán. */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /** Mã voucher đã áp dụng (nullable nếu không dùng voucher). */
    @Column(name = "voucher_code", length = 50)
    private String voucherCode;

    /** Tổng tiền hàng sau khuyến mãi từng sách, trước voucher. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    /** Số tiền giảm từ voucher — lưu riêng để hiển thị trên hóa đơn. */
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /** Phí vận chuyển — hiện luôn 0. */
    @Column(name = "shipping_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    /** Tổng thanh toán = subtotal - discount_amount (+ shipping_fee). */
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /** Ghi chú tùy chọn từ customer khi checkout. */
    @Column(length = 500)
    private String note;

    /** Thời điểm tạo đơn — set tự động bởi {@link #onCreate()}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Thời điểm cập nhật gần nhất — set bởi {@link #onUpdate()}. */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Các dòng sản phẩm trong đơn — cascade ALL: lưu/xóa cùng Order. */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /** JPA callback: gán createdAt và updatedAt khi INSERT lần đầu. */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** JPA callback: cập nhật updatedAt mỗi lần UPDATE (vd: đổi status). */
    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getCreatedAtFormatted() {
        return createdAt == null ? ""
                : createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getTotalAmountFormatted() {
        return formatMoney(totalAmount);
    }

    public String getSubtotalFormatted() {
        return formatMoney(subtotal);
    }

    public String getDiscountAmountFormatted() {
        return formatMoney(discountAmount);
    }

    public String getShippingFeeFormatted() {
        return formatMoney(shippingFee);
    }

    /** Nhãn trạng thái — OrderDTO.statusLabel */
    public String getStatusLabel() {
        try {
            return OrderStatus.valueOf(status).getLabel();
        } catch (Exception ex) {
            return status;
        }
    }

    public String getPaymentMethodLabel() {
        try {
            return PaymentMethod.valueOf(paymentMethod).getLabel();
        } catch (Exception ex) {
            return paymentMethod;
        }
    }

    /** Nhãn trạng thái thanh toán — OrderDTO.paymentStatusLabel */
    public String getPaymentStatusLabel() {
        return PaymentStatus.fromValue(paymentStatus).getLabel();
    }

    public String getShippingMethodLabel() {
        try {
            return ShippingMethod.valueOf(shippingMethod).getLabel();
        } catch (Exception ex) {
            return shippingMethod;
        }
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return NumberFormat.getIntegerInstance(Locale.GERMANY).format(amount.longValue());
    }
}
