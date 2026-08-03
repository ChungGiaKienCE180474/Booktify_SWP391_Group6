package shop.domain;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form dữ liệu checkout — bind từ trang order/checkout.jsp.
 * <p>
 * Submit qua POST /orders/checkout ({@link shop.controller.client.OrderController#placeOrder}).
 * Bean Validation chạy với @Valid trước khi OrderService.createOrderFromCart().
 */
public class CheckoutForm {

    /** Tên người nhận hàng — @NotBlank khi submit checkout */
    @NotBlank(message = "Please enter the recipient's name.")
    @Size(
            max = 150,
            message = "Recipient name must be at most 150 characters."
    )
    private String recipientName;

    /** SĐT VN 10 số, bắt đầu 03/05/07/08/09 */
    @NotBlank(message = "Please enter a phone number.")
    @Pattern(
            regexp = "^(0[35789])[0-9]{8}$",
            message = "Invalid phone number format (10 digits, starting with 0)."
    )
    private String recipientPhone;

    /** Địa chỉ giao hàng đầy đủ */
    @NotBlank(message = "Shipping address must not be empty.")
    @Size(
            max = 500,
            message = "Address must be at most 500 characters."
    )
    private String shippingAddress;

    /** Mã giảm giá tùy chọn — validate trong OrderService.validateCheckoutForm */
    @Size(
            max = 50,
            message = "Voucher code must be at most 50 characters."
    )
    private String voucherCode;

    /** Ghi chú giao hàng tùy chọn */
    @Size(
            max = 500,
            message = "Note must be at most 500 characters."
    )
    private String note;

    /** Phương thức thanh toán — COD hoặc VNPAY */
    private String paymentMethod = PaymentMethod.COD.name();

    /** Lưu tên/SĐT/địa chỉ vừa nhập làm mặc định cho tài khoản (mặc định bật). */
    private boolean saveAddress = true;

    /*
     * Lưu promotion được chọn cho từng sách.
     *
     * Key   : bookId
     * Value : NONE, PERCENTAGE hoặc FIXED
     *
     * Ví dụ:
     * {
     *     1: "PERCENTAGE",
     *     2: "FIXED",
     *     3: "NONE"
     * }
     */
    private Map<Long, String> bookPromotionSelections =
            new HashMap<>();

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(
            String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(
            String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(
            String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public boolean isSaveAddress() {
        return saveAddress;
    }

    public void setSaveAddress(boolean saveAddress) {
        this.saveAddress = saveAddress;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(
            String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(
            String note) {
        this.note = note;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod resolvePaymentMethod() {
        if (PaymentMethod.isValid(paymentMethod)) {
            return PaymentMethod.valueOf(paymentMethod);
        }
        return PaymentMethod.COD;
    }

    public Map<Long, String>
    getBookPromotionSelections() {

        return bookPromotionSelections;
    }

    public void setBookPromotionSelections(
            Map<Long, String>
                    bookPromotionSelections) {

        this.bookPromotionSelections =
                bookPromotionSelections == null
                        ? new HashMap<>()
                        : bookPromotionSelections;
    }

    /*
     * Lấy loại promotion khách đã chọn
     * cho một sách cụ thể.
     */
    public PromotionSelection
    resolvePromotionSelection(
            Long bookId) {

        if (bookId == null
                || bookPromotionSelections == null) {

            return PromotionSelection.NONE;
        }

        String selectedValue =
                bookPromotionSelections.get(bookId);

        return PromotionSelection.fromValue(
                selectedValue
        );
    }
}



