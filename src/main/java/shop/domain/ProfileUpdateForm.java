package shop.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileUpdateForm {

    @NotBlank(message = "Full name must not be empty")
    @Size(min = 3, message = "Full name must be at least 3 characters")
    private String fullName;

    @Size(max = 20, message = "Phone number must be at most 20 characters")
    private String phone;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
