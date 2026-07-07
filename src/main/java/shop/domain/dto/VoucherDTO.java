package shop.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VoucherDTO {

    // =====================
    // FIELD
    // =====================

    @NotBlank(message = "Voucher code is required.")
    @Size(max = 50, message = "Voucher code must not exceed 50 characters.")
    private String voucherCode;

    @NotNull(message = "Discount value is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount value must be greater than 0.")
    @DecimalMax(value = "100.0", message = "Discount value must be between 0.1 and 100.")
    private BigDecimal discountValue;

    @DecimalMin(value = "0", inclusive = true, message = "Minimum order amount cannot be negative.")
    private BigDecimal minOrderAmount;

    @DecimalMin(value = "0", inclusive = true, message = "Maximum order amount cannot be negative.")
    private BigDecimal maxOrderAmount;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be greater than 0.")
    private Integer quantity;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    @Size(max = 200, message = "Description must not exceed 200 characters.")
    private String description;

    // =====================
    // CONSTRUCTOR
    // =====================

    public VoucherDTO() {
    }

    // =====================
    // GETTER SETTER
    // =====================

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}