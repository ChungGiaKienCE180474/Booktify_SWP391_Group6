package shop.domain;

public enum OrderStatus {
    PENDING("Pending confirmation"),
    CONFIRMED("Confirmed"),
    SHIPPING("Shipping"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
