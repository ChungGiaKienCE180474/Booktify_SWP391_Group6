package shop.domain;

public enum StockProductType {
    BOOK("Book"),
    VPP("Stationery");

    private final String label;

    StockProductType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StockProductType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Product type is required.");
        }

        try {
            return StockProductType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Product type must be BOOK or VPP."
            );
        }
    }
}