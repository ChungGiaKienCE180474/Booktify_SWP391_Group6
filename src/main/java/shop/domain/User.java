package shop.domain;

import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /*
     * Email dùng chung cho Customer, Staff và Admin.
     * unique = true ngăn email trùng tại database.
     */
    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    @Size(
            max = 255,
            message = "Email must not exceed 255 characters."
    )
    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 255
    )
    private String email;

    /*
     * Đây là mật khẩu đã được BCrypt encode.
     * Không kiểm tra cấu trúc mật khẩu gốc tại Entity.
     */
    @NotBlank(message = "Password is required.")
    @Size(
            max = 255,
            message = "Encoded password must not exceed 255 characters."
    )
    @Column(
            name = "password",
            nullable = false,
            length = 255
    )
    private String password;

    @NotBlank(message = "Full name is required.")
    @Size(
            min = 3,
            max = 150,
            message = "Full name must be between 3 and 150 characters."
    )
    @Column(
            name = "full_name",
            nullable = false,
            length = 150
    )
    private String fullName;

    @Size(
            max = 500,
            message = "Address must not exceed 500 characters."
    )
    @Column(
            name = "address",
            length = 500
    )
    private String address;

    /*
     * Cho phép null hoặc chuỗi trống.
     * Nếu có dữ liệu thì phải là số điện thoại Việt Nam.
     */
    @Pattern(
            regexp = "^$|^(0[35789])[0-9]{8}$",
            message = "Please enter a valid Vietnamese phone number."
    )
    @Column(
            name = "phone",
            length = 20
    )
    private String phone;

    @Size(
            max = 1000,
            message = "Avatar URL must not exceed 1000 characters."
    )
    @Column(
            name = "avatar",
            length = 1000
    )
    private String avatar;

    @Column(
            name = "status",
            nullable = false
    )
    private boolean status = true;

    @Column(
            name = "deleted",
            nullable = false
    )
    private boolean deleted = false;

    @Size(
            max = 50,
            message = "Staff role must not exceed 50 characters."
    )
    @Column(
            name = "staff_role",
            length = 50
    )
    private String staffRole;

    /*
     * EAGER là cần thiết vì Spring Security phải đọc role
     * sau khi User được lấy khỏi database.
     */
    @NotNull(message = "Account role is required.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "role_id",
            nullable = false
    )
    private Role role;

    @NotBlank(message = "Authentication provider is required.")
    @Size(
            max = 20,
            message = "Authentication provider must not exceed 20 characters."
    )
    @Column(
            name = "auth_provider",
            nullable = false,
            length = 20
    )
    private String authProvider = AuthProvider.LOCAL.name();

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Rating> ratings = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null
                ? null
                : email.trim().toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName == null
                ? null
                : fullName
                .trim()
                .replaceAll("\\s+", " ");
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.isBlank()) {
            this.address = null;
            return;
        }

        this.address = address
                .trim()
                .replaceAll("\\s+", " ");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            this.phone = null;
            return;
        }

        this.phone = phone.trim();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if (avatar == null || avatar.isBlank()) {
            this.avatar = null;
            return;
        }

        this.avatar = avatar.trim();
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(String staffRole) {
        if (staffRole == null || staffRole.isBlank()) {
            this.staffRole = null;
            return;
        }

        this.staffRole = staffRole.trim();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        if (authProvider == null || authProvider.isBlank()) {
            this.authProvider = AuthProvider.LOCAL.name();
            return;
        }

        this.authProvider = authProvider
                .trim()
                .toUpperCase();
    }

    public boolean isGoogleAccount() {
        return AuthProvider.GOOGLE.name().equals(authProvider);
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings == null
                ? new ArrayList<>()
                : ratings;
    }
}