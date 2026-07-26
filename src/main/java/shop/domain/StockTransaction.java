package shop.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_transactions")
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "product_type",
            nullable = false,
            length = 20
    )
    private StockProductType productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vpp_item_id")
    private VppItem vppItem;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "movement_type",
            nullable = false,
            length = 30
    )
    private StockMovementType movementType;

    @Column(name = "quantity_change", nullable = false)
    private int quantityChange;

    @Column(name = "stock_before", nullable = false)
    private int stockBefore;

    @Column(name = "stock_after", nullable = false)
    private int stockAfter;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_code", length = 100)
    private String referenceCode;

    @Column(length = 200)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_user_id")
    private User performedBy;

    @Column(
            name = "performed_by_name",
            nullable = false,
            length = 150
    )
    private String performedByName;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (performedByName == null || performedByName.isBlank()) {
            performedByName = "System";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StockProductType getProductType() {
        return productType;
    }

    public void setProductType(
            StockProductType productType
    ) {
        this.productType = productType;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public VppItem getVppItem() {
        return vppItem;
    }

    public void setVppItem(VppItem vppItem) {
        this.vppItem = vppItem;
    }

    public StockMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(
            StockMovementType movementType
    ) {
        this.movementType = movementType;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    public int getStockBefore() {
        return stockBefore;
    }

    public void setStockBefore(int stockBefore) {
        this.stockBefore = stockBefore;
    }

    public int getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(int stockAfter) {
        this.stockAfter = stockAfter;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public String getPerformedByName() {
        return performedByName;
    }

    public void setPerformedByName(
            String performedByName
    ) {
        this.performedByName = performedByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }
}