package shop.domain;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be at most 500 characters")
    @Column(nullable = false, length = 500)
    private String title;

    // Optional — a book can be saved without an author selected.
    @Size(max = 150, message = "Author must be at most 150 characters")
    @Column(length = 150)
    private String author;

    @Size(max = 20, message = "ISBN must be at most 20 characters")
    @Column(length = 20, unique = true)
    private String isbn;

    @Size(max = 10000, message = "Description must be at most 10000 characters")
    @Column(length = 10000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be >= 0")
    @Digits(integer = 10, fraction = 0, message = "Price must have at most 10 digits before the decimal point")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity must be >= 0")
    @Column(nullable = false)
    private int stockQuantity = 0;

    @Size(max = 500, message = "Image URL must be at most 500 characters")
    @Column(length = 500)
    private String imageUrl;

    // Soft delete flag. Books stay in the DB but disappear from the storefront
    // and the "active" admin filter once this is false.
    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Every book belongs to exactly one category; required on create/edit.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Genres are secondary tags, independent from category — a book can carry
    // several, and a genre doesn't need to match the book's category.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new LinkedHashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAtString() {
        return createdAt == null ? "" : createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getUpdatedAtString() {
        return updatedAt == null ? "" : updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Display-only formatting ("299.000") — don't use this for calculations.
    public String getPriceFormatted() {
        if (price == null)
            return "0";
        NumberFormat nf = NumberFormat.getIntegerInstance(Locale.GERMANY);
        return nf.format(price.longValue());
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres != null ? genres : new LinkedHashSet<>();
    }

    // Comma-separated genre names for list/detail views. Filters out inactive
    // or soft-deleted genres so a hidden genre never leaks into the UI.
    public String getGenreNames() {
        if (genres == null || genres.isEmpty()) return "";
        return genres.stream()
                .filter(Genre::isActive)
                .filter(g -> !g.isDeleted())
                .map(Genre::getName)
                .sorted()
                .collect(java.util.stream.Collectors.joining(", "));
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
