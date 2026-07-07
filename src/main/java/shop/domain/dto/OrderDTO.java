package shop.domain.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private Long id;
    private String orderCode;
    private String status;
    private String statusLabel;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String paymentMethodLabel;
    private String shippingMethodLabel;
    private String voucherCode;
    private String subtotalFormatted;
    private String discountAmountFormatted;
    private String shippingFeeFormatted;
    private String totalAmountFormatted;
    private String createdAtFormatted;
    private String customerEmail;
    private String customerName;
    private String note;
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

    public String getPaymentMethodLabel() {
        return paymentMethodLabel;
    }

    public void setPaymentMethodLabel(String paymentMethodLabel) {
        this.paymentMethodLabel = paymentMethodLabel;
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
