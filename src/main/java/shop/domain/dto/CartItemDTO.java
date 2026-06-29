package shop.domain.dto;

public class CartItemDTO {

    private Long id;
    private int quantity;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookImageUrl;
    private String bookPriceFormatted;
    private int bookStockQuantity;
    private boolean bookActive;
    private String subtotalFormatted;

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

    public void setBookPriceFormatted(String bookPriceFormatted) {
        this.bookPriceFormatted = bookPriceFormatted;
    }

    public int getBookStockQuantity() {
        return bookStockQuantity;
    }

    public void setBookStockQuantity(int bookStockQuantity) {
        this.bookStockQuantity = bookStockQuantity;
    }

    public boolean isBookActive() {
        return bookActive;
    }

    public void setBookActive(boolean bookActive) {
        this.bookActive = bookActive;
    }

    public String getSubtotalFormatted() {
        return subtotalFormatted;
    }

    public void setSubtotalFormatted(String subtotalFormatted) {
        this.subtotalFormatted = subtotalFormatted;
    }
}
