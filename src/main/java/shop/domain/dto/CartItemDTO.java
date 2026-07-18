package shop.domain.dto;

public class CartItemDTO {

    private Long id;
    private int quantity;

    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookImageUrl;

    private String bookPriceFormatted;
    private String originalPriceFormatted;
    private String effectivePriceFormatted;

    private String originalSubtotalFormatted;
    private String subtotalFormatted;

    private String promotionLabel;
    private boolean promotionApplied;

    private int bookStockQuantity;
    private boolean bookActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getBookPriceFormatted() {
        return bookPriceFormatted;
    }

    public void setBookPriceFormatted(
            String bookPriceFormatted) {
        this.bookPriceFormatted = bookPriceFormatted;
    }

    public String getOriginalPriceFormatted() {
        return originalPriceFormatted;
    }

    public void setOriginalPriceFormatted(
            String originalPriceFormatted) {
        this.originalPriceFormatted =
                originalPriceFormatted;
    }

    public String getEffectivePriceFormatted() {
        return effectivePriceFormatted;
    }

    public void setEffectivePriceFormatted(
            String effectivePriceFormatted) {
        this.effectivePriceFormatted =
                effectivePriceFormatted;
    }

    public String getOriginalSubtotalFormatted() {
        return originalSubtotalFormatted;
    }

    public void setOriginalSubtotalFormatted(
            String originalSubtotalFormatted) {
        this.originalSubtotalFormatted =
                originalSubtotalFormatted;
    }

    public String getSubtotalFormatted() {
        return subtotalFormatted;
    }

    public void setSubtotalFormatted(
            String subtotalFormatted) {
        this.subtotalFormatted = subtotalFormatted;
    }

    public String getPromotionLabel() {
        return promotionLabel;
    }

    public void setPromotionLabel(
            String promotionLabel) {
        this.promotionLabel = promotionLabel;
    }

    public boolean isPromotionApplied() {
        return promotionApplied;
    }

    public void setPromotionApplied(
            boolean promotionApplied) {
        this.promotionApplied = promotionApplied;
    }

    public int getBookStockQuantity() {
        return bookStockQuantity;
    }

    public void setBookStockQuantity(
            int bookStockQuantity) {
        this.bookStockQuantity = bookStockQuantity;
    }

    public boolean isBookActive() {
        return bookActive;
    }

    public void setBookActive(boolean bookActive) {
        this.bookActive = bookActive;
    }
}