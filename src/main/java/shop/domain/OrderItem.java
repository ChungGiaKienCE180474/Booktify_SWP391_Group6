package shop.domain;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Một dòng sản phẩm trong đơn hàng — bảng {@code order_items}.
 * <p>
 * Mỗi OrderItem thuộc về một {@link Order} và đại diện cho sách HOẶC văn phòng phẩm
 * (một trong hai FK book / vppItem được set, còn lại null).
 * Giá và tên được snapshot tại thời điểm đặt hàng.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Đơn hàng cha — FK orders.id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** FK sách — null nếu dòng này là VPP. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    /** FK văn phòng phẩm — null nếu dòng này là sách. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vpp_item_id")
    private VppItem vppItem;

    /** Tên sản phẩm tại thời điểm mua (snapshot, không đổi khi sách đổi tên). */
    @Column(name = "book_title", nullable = false, length = 200)
    private String bookTitle;

    /** Giá đơn vị sau KM (sách) hoặc giá gốc (VPP) tại thời điểm mua. */
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    /** Thành tiền dòng = unit_price × quantity. */
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public VppItem getVppItem() {
        return vppItem;
    }

    public void setVppItem(VppItem vppItem) {
        this.vppItem = vppItem;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    /** Format giá đơn vị VND kiểu 1.234.567 — dùng khi map sang OrderItemDTO. */
    public String getUnitPriceFormatted() {
        return formatMoney(unitPrice);
    }

    public String getLineTotalFormatted() {
        return formatMoney(lineTotal);
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return NumberFormat.getIntegerInstance(Locale.GERMANY).format(amount.longValue());
    }
}
