package shop.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Genre entity (table "genres"). Books and genres are many-to-many through
 * book_genres (see Book.genres) — a genre doesn't need to share the category
 * of the books it's attached to.
 */
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Genre name is required")
    @Size(max = 120, message = "Genre name must be at most 120 characters")
    @Column(nullable = false, length = 120)
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters")
    @Column(length = 500)
    private String description;

    // Only used to group/filter genres in the admin screen — unrelated to the
    // category of any book this genre gets attached to.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Hidden but recoverable. Different from "deleted" below.
    @Column(nullable = false)
    private boolean active = true;

    // Soft-deleted for good — not meant to be restored through the normal UI.
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedAtString() {
        return updatedAt == null ? "" : updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getCreatedAtString() {
        return createdAt == null ? "" : createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Book.genres is a Set<Genre>, so equality must be id-based rather than
    // reference-based — otherwise the same genre loaded in different
    // sessions would be treated as duplicates.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre other = (Genre) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
