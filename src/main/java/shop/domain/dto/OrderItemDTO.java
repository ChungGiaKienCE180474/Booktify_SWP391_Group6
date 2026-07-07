package shop.domain.dto;

public class OrderItemDTO {

    private Long bookId;
    private String bookTitle;
    private String bookImageUrl;
    private int quantity;
    private String unitPriceFormatted;
    private String lineTotalFormatted;

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

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnitPriceFormatted() {
        return unitPriceFormatted;
    }

    public void setUnitPriceFormatted(String unitPriceFormatted) {
        this.unitPriceFormatted = unitPriceFormatted;
    }

    public String getLineTotalFormatted() {
        return lineTotalFormatted;
    }

    public void setLineTotalFormatted(String lineTotalFormatted) {
        this.lineTotalFormatted = lineTotalFormatted;
    }
}
