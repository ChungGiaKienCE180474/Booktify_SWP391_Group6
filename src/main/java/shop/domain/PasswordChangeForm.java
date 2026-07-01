package shop.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PasswordChangeForm {

    private String currentPassword;

    @NotEmpty(message = "New password is required")
    @Size(min = 3, message = "Mật khẩu mới phải có tối thiểu 3 ký tự")
    private String newPassword;

    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
