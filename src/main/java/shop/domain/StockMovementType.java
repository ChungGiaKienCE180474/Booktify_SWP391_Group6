package shop.domain;

public enum StockMovementType {
    INITIAL_STOCK("Initial Stock", "initial"),
    STOCK_IN("Stock In", "in"),
    STOCK_OUT("Stock Out", "out"),
    SALE("Sale", "sale"),
    ORDER_CANCELLED("Order Cancelled", "cancelled"),
    ADJUSTMENT("Adjustment", "adjustment");

    private final String label;
    private final String cssClass;

    StockMovementType(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static StockMovementType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return StockMovementType.valueOf(
                    value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid stock movement type."
            );
        }
    }
}