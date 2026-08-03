package shop.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shop.domain.Cart;

public class CartRefreshResult {

    private final Cart cart;
    private final List<String> warnings;

    public CartRefreshResult(Cart cart, List<String> warnings) {
        this.cart = cart;
        this.warnings = warnings != null ? warnings : Collections.emptyList();
    }

    public static CartRefreshResult empty() {
        return new CartRefreshResult(null, List.of());
    }

    public Cart getCart() {
        return cart;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public static Builder builder(Cart cart) {
        return new Builder(cart);
    }

    public static class Builder {
        private final Cart cart;
        private final List<String> warnings = new ArrayList<>();

        private Builder(Cart cart) {
            this.cart = cart;
        }

        public Builder warning(String message) {
            if (message != null && !message.isBlank()) {
                warnings.add(message);
            }
            return this;
        }

        public CartRefreshResult build() {
            return new CartRefreshResult(cart, warnings);
        }
    }
}
