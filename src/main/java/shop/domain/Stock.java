package shop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Một dòng stock có thể thuộc Book.
     *
     * Nếu dòng này thuộc Book:
     * book_id có giá trị
     * vpp_item_id là NULL
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "book_id",
            unique = true
    )
    private Book book;

    /*
     * Một dòng stock có thể thuộc VPP.
     *
     * Nếu dòng này thuộc VPP:
     * vpp_item_id có giá trị
     * book_id là NULL
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vpp_item_id",
            unique = true
    )
    private VppItem vppItem;

    /*
     * Số lượng tồn kho hiện tại.
     */
    @Column(
            name = "quantity",
            nullable = false
    )
    private int quantity = 0;

    public Stock() {
    }

    /*
     * Tạo stock cho Book.
     */
    public static Stock forBook(
            Book book,
            int quantity
    ) {
        if (book == null) {
            throw new IllegalArgumentException(
                    "Book is required."
            );
        }

        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must be greater than or equal to 0."
            );
        }

        Stock stock = new Stock();

        stock.setBook(book);
        stock.setVppItem(null);
        stock.setQuantity(quantity);

        return stock;
    }

    /*
     * Tạo stock cho VPP.
     */
    public static Stock forVppItem(
            VppItem vppItem,
            int quantity
    ) {
        if (vppItem == null) {
            throw new IllegalArgumentException(
                    "Stationery product is required."
            );
        }

        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must be greater than or equal to 0."
            );
        }

        Stock stock = new Stock();

        stock.setBook(null);
        stock.setVppItem(vppItem);
        stock.setQuantity(quantity);

        return stock;
    }

    /*
     * Kiểm tra dòng stock thuộc Book hay không.
     */
    public boolean isBookStock() {
        return book != null;
    }

    /*
     * Kiểm tra dòng stock thuộc VPP hay không.
     */
    public boolean isVppStock() {
        return vppItem != null;
    }

    /*
     * Lấy loại sản phẩm.
     */
    public StockProductType getProductType() {
        if (book != null) {
            return StockProductType.BOOK;
        }

        if (vppItem != null) {
            return StockProductType.VPP;
        }

        return null;
    }

    /*
     * Lấy ID sản phẩm.
     */
    public Long getProductId() {
        if (book != null) {
            return book.getId();
        }

        if (vppItem != null) {
            return vppItem.getId();
        }

        return null;
    }

    /*
     * Lấy tên sản phẩm.
     */
    public String getProductName() {
        if (book != null) {
            return book.getTitle();
        }

        if (vppItem != null) {
            return vppItem.getName();
        }

        return "";
    }

    /*
     * Tăng số lượng tồn kho.
     */
    public void increase(
            int amount
    ) {
        requirePositiveAmount(amount);

        this.quantity = Math.addExact(
                this.quantity,
                amount
        );
    }

    /*
     * Giảm số lượng tồn kho.
     */
    public void decrease(
            int amount
    ) {
        requirePositiveAmount(amount);

        if (amount > this.quantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available quantity: "
                            + this.quantity
                            + "."
            );
        }

        this.quantity -= amount;
    }

    /*
     * Điều chỉnh trực tiếp về số lượng thực tế.
     */
    public void adjust(
            int actualQuantity
    ) {
        if (actualQuantity < 0) {
            throw new IllegalArgumentException(
                    "Actual stock must be greater than or equal to 0."
            );
        }

        this.quantity = actualQuantity;
    }

    private void requirePositiveAmount(
            int amount
    ) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0."
            );
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(
            Book book
    ) {
        this.book = book;
    }

    public VppItem getVppItem() {
        return vppItem;
    }

    public void setVppItem(
            VppItem vppItem
    ) {
        this.vppItem = vppItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(
            int quantity
    ) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must be greater than or equal to 0."
            );
        }

        this.quantity = quantity;
    }
}