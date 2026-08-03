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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_book", columnNames = { "cart_id", "book_id" }),
        @UniqueConstraint(name = "uk_cart_vpp", columnNames = { "cart_id", "vpp_item_id" }),
        @UniqueConstraint(name = "uk_cart_book_set", columnNames = { "cart_id", "book_set_id" })
})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "vpp_item_id")
    private VppItem vppItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_set_id")
    private BookSet bookSet;

    @Column(nullable = false)
    private int quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public VppItem getVppItem() {
        return vppItem;
    }

    public void setVppItem(VppItem vppItem) {
        this.vppItem = vppItem;
    }

    public BookSet getBookSet() {
        return bookSet;
    }

    public void setBookSet(BookSet bookSet) {
        this.bookSet = bookSet;
    }

    public boolean isBookSetItem() {
        return bookSet != null;
    }

    public String getSubtotalFormatted() {
        BigDecimal unitPrice = null;
        if (bookSet != null) {
            unitPrice = bookSet.getSetPrice();
        } else if (book != null) {
            unitPrice = book.getPrice();
        } else if (vppItem != null) {
            unitPrice = vppItem.getPrice();
        }
        if (unitPrice == null) {
            return "0";
        }
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return NumberFormat.getIntegerInstance(Locale.GERMANY).format(subtotal.longValue());
    }
}
