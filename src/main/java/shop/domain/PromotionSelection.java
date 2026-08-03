package shop.domain;

public enum PromotionSelection {
    NONE,
    PERCENTAGE,
    FIXED;

    public static PromotionSelection fromValue(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }

        try {
            return PromotionSelection.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return NONE;
        }
    }
}
