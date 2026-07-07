package shop.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CheckoutForm {

    @NotBlank(message = "Vui lòng nhập tên người nhận.")
    @Size(max = 150, message = "Tên người nhận tối đa 150 ký tự.")
    private String recipientName;

    @NotBlank(message = "Vui lòng nhập số điện thoại.")
    @Pattern(regexp = "^(0[35789])[0-9]{8}$", message = "Số điện thoại không đúng định dạng (10 chữ số, bắt đầu bằng 0).")
    private String recipientPhone;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống.")
    @Size(max = 500, message = "Địa chỉ tối đa 500 ký tự.")
    private String shippingAddress;

    @Size(max = 50, message = "Mã voucher tối đa 50 ký tự.")
    private String voucherCode;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự.")
    private String note;

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

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
