package shop.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Promotion name is required.")
    @Size(
            max = 200,
            message = "Promotion name must not exceed 200 characters."
    )
    @Column(
            name = "name",
            nullable = false,
            length = 200
    )
    private String name;

    @Size(
            max = 1000,
            message = "Promotion description must not exceed 1000 characters."
    )
    @Column(
            name = "description",
            length = 1000
    )
    private String description;

    @NotNull(message = "Discount value is required.")
    @DecimalMin(
            value = "0.01",
            message = "Discount value must be greater than 0."
    )
    @Column(
            name = "discount_value",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal discountValue;

    @Column(
            name = "is_percentage",
            nullable = false
    )
    private boolean isPercentage = true;

    @NotNull(message = "Start date is required.")
    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDateTime startDate;

    @NotNull(message = "End date is required.")
    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDateTime endDate;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "promotion_categories",
            joinColumns =
            @JoinColumn(name = "promotion_id"),
            inverseJoinColumns =
            @JoinColumn(name = "category_id")
    )
    private List<Category> applicableCategories =
            new ArrayList<>();

    @AssertTrue(
            message = "Percentage discount must not exceed 100%."
    )
    public boolean isPercentageValueValid() {
        if (!isPercentage || discountValue == null) {
            return true;
        }

        return discountValue.compareTo(
                BigDecimal.valueOf(100)
        ) <= 0;
    }

    @AssertTrue(
            message = "End date must be after start date."
    )
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true;
        }

        return endDate.isAfter(startDate);
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now =
                LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null
                ? null
                : name.trim()
                .replaceAll("\\s+", " ");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description == null
                        || description.isBlank()
                        ? null
                        : description.trim();
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(
            BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public void setPercentage(
            boolean percentage) {
        isPercentage = percentage;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Category>
    getApplicableCategories() {
        return applicableCategories;
    }

    public void setApplicableCategories(
            List<Category>
                    applicableCategories) {

        this.applicableCategories =
                applicableCategories == null
                        ? new ArrayList<>()
                        : applicableCategories;
    }
    public String getStartDateFormatted() {
        if (startDate == null) {
            return "";
        }

        return startDate.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );
    }

    public String getEndDateFormatted() {
        if (endDate == null) {
            return "";
        }

        return endDate.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );
    }
}