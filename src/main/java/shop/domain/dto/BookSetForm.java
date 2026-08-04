package shop.domain.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookSetForm {

    private Long id;

    @NotBlank(message = "Set name is required.")
    @Size(max = 200)
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "Set price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Set price must be greater than 0.")
    private BigDecimal setPrice;

    /**
     * Free-text tag / series label (e.g. "Manga", "One Piece", "Grade 1", "Comics").
     * Stored in grade_level column for backward compatibility.
     */
    @Size(max = 100)
    private String gradeLevel;

    private boolean active = true;

    /** Components of the set — at least 2 different books. */
    private List<BookSetItemForm> items = new ArrayList<>();

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
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getSetPrice() {
        return setPrice;
    }

    public void setSetPrice(BigDecimal setPrice) {
        this.setPrice = setPrice;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<BookSetItemForm> getItems() {
        return items;
    }

    public void setItems(List<BookSetItemForm> items) {
        this.items = items;
    }
}
