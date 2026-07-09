package shop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "vouchers")
public class Voucher {

    // =====================
    // FIELD
    // =====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "voucher_name", length = 50)
    private String voucherName;

    @Column(name = "voucher_code", nullable = false, unique = true, length = 50)
    private String voucherCode;

    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "max_order_amount", precision = 10, scale = 2)
    private BigDecimal maxOrderAmount;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 20)
    private String status;

    @Column(length = 200)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =====================
    // CONSTRUCTOR
    // =====================

    public Voucher() {
    }

    // =====================
    // AUTO TIMESTAMP
    // =====================

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();
    }

    // =====================
    // GETTER SETTER
    // =====================

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public BigDecimal getMaxOrderAmount() {
        return maxOrderAmount;
    }

    public void setMaxOrderAmount(BigDecimal maxOrderAmount) {
        this.maxOrderAmount = maxOrderAmount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getUpdatedAtString() {

        if (updatedAt == null) {
            return "";
        }

        return updatedAt.format(
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm"));
    }

}