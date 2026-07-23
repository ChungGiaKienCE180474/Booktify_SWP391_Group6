package shop.domain.dto;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class StaffCreateDTO
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(
            message = "Full name is required."
    )
    @Size(
            min = 3,
            max = 150,
            message = "Full name must be between 3 and 150 characters."
    )
    private String fullName;

    @NotBlank(
            message = "Email is required."
    )
    @Email(
            message = "Please enter a valid email address."
    )
    @Size(
            max = 255,
            message = "Email must not exceed 255 characters."
    )
    private String email;

    @NotBlank(
            message = "Password is required."
    )
    @Size(
            min = 8,
            max = 72,
            message = "Password must be between 8 and 72 characters."
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain an uppercase letter, a lowercase letter, and a number."
    )
    private String password;

    @NotBlank(
            message = "Password confirmation is required."
    )
    private String confirmPassword;

    @NotBlank(
            message = "Phone number is required."
    )
    @Pattern(
            regexp = "^(0[35789])[0-9]{8}$",
            message = "Please enter a valid Vietnamese phone number."
    )
    private String phone;

    @NotBlank(
            message = "Address is required."
    )
    @Size(
            max = 500,
            message = "Address must not exceed 500 characters."
    )
    private String address;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(
            String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(
            String confirmPassword) {
        this.confirmPassword =
                confirmPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(
            String address) {
        this.address = address;
    }
}