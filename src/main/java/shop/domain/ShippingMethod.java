package shop.domain;

import java.math.BigDecimal;

public enum ShippingMethod {
    STANDARD("Giao tiêu chuẩn", new BigDecimal("30000")),
    EXPRESS("Giao nhanh", new BigDecimal("50000")),
    PICKUP("Nhận tại cửa hàng", BigDecimal.ZERO);

    private final String label;
    private final BigDecimal fee;

    ShippingMethod(String label, BigDecimal fee) {
        this.label = label;
        this.fee = fee;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            ShippingMethod.valueOf(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
