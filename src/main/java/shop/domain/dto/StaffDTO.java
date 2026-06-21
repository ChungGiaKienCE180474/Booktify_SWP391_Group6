package shop.domain.dto;

public class StaffDTO {

    private Long id;
    private String staffCode;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Boolean status;
    private Boolean deleted;
    private String staffRole;

    public StaffDTO() {
    }

    public StaffDTO(Long id, String staffCode, String email, String fullName,
                    String phone, String address, Boolean status,
                    Boolean deleted, String staffRole) {
        this.id = id;
        this.staffCode = staffCode;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.deleted = deleted;
        this.staffRole = staffRole;
    }

    public String getInitial() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "S";
        }
        return fullName.trim().substring(0, 1).toUpperCase();
    }

    public Long getId() { return id; }
    public String getStaffCode() { return staffCode; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public Boolean getStatus() { return status; }
    public Boolean getDeleted() { return deleted; }
    public String getStaffRole() { return staffRole; }

    public void setId(Long id) { this.id = id; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setStatus(Boolean status) { this.status = status; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public void setStaffRole(String staffRole) { this.staffRole = staffRole; }
}