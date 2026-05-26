package shop.domain.dto;

import java.io.Serializable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

// @RegisterChecked
public class RegisterDTO {
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID
    @Size(min = 3, message = "FirstName phải có tối thiểu 3 ký tự")
    private String firstName;

    @Size(min = 3, message = "LastName phải có tối thiểu 3 ký tự")
    private String lastName;

    @Email(message = "Email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
    @Size(min = 3, message = "Password phải có tối thiểu 3 ký tự")
    private String password;

    private String confirmPassword;

    @AssertTrue(message = "Password và ConfirmPassword phải trùng khớp")
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
