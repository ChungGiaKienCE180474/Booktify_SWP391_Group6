package shop.domain.dto;

public class CustomerDTO {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Boolean status;
    private String customerCode;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String email, String fullName,
                       String phone, String address, Boolean status, String customerCode) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.customerCode = customerCode;
    }
    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getInitial() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "U";
        }
        return fullName.trim().substring(0, 1).toUpperCase();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}