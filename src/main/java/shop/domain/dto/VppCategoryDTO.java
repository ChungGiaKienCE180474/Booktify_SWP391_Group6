package shop.domain.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VppCategoryDTO {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;

    @NotBlank(message = "Category name is required.")
    @Size(max = 120, message = "Category name must be less than 120 characters.")
    private String name;

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

    public VppCategoryDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public VppCategoryDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VppCategoryDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public VppCategoryDTO setActive(boolean active) {
        this.active = active;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public VppCategoryDTO setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public VppCategoryDTO setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}