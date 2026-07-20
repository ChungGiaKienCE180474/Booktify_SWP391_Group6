package shop.domain.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SupplierDTO {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;

    @NotBlank(message = "Supplier name is required.")
    @Size(max = 150, message = "Supplier name must be less than 150 characters.")
    private String supplierName;

    @Size(max = 150, message = "Contact person must be less than 150 characters.")
    private String contactPerson;

    @Email(message = "Invalid email format.")
    @Size(max = 150, message = "Email must be less than 150 characters.")
    private String email;

    @Size(max = 30, message = "Phone must be less than 30 characters.")
    private String phone;

    @Size(max = 255, message = "Address must be less than 255 characters.")
    private String address;

    @Size(max = 1000, message = "Description must be less than 1000 characters.")
    private String description;

    private boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public String getCreatedAtFormatted() {
        return createdAt == null ? "—" : createdAt.format(DATE_TIME_FORMATTER);
    }

    public String getUpdatedAtFormatted() {
        return updatedAt == null ? "—" : updatedAt.format(DATE_TIME_FORMATTER);
    }

    public String getStatusText() {
        return active ? "Active" : "Hidden";
    }

    public Long getId() {
        return id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}