package shop.domain.dto;

/**
 * DTO một dòng sản phẩm trong đơn — map từ {@link shop.domain.OrderItem}.
 * <p>
 * bookId dùng cho cả sách và VPP (VPP dùng vppItem.id).
 * bookImageUrl: URL ảnh sách hoặc /uploads/vpp/{id}/image cho VPP.
 */
public class OrderItemDTO {

    /** ID sách hoặc VPP — dùng link chi tiết sản phẩm trên view */
    private Long bookId;
    /** Tên snapshot tại thời điểm đặt hàng */
    private String bookTitle;
    private String bookImageUrl;
    private int quantity;
    /** Giá đơn vị và thành tiền dòng — đã format VND */
    private String unitPriceFormatted;
    private String lineTotalFormatted;
    private Long bookSetId;
    private String bookSetName;

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

    public Long getBookSetId() {
        return bookSetId;
    }

    public void setBookSetId(Long bookSetId) {
        this.bookSetId = bookSetId;
    }

    public String getBookSetName() {
        return bookSetName;
    }

    public void setBookSetName(String bookSetName) {
        this.bookSetName = bookSetName;
    }
}
