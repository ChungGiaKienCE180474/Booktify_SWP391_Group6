package shop.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartDTO {

    private Long id;
    private List<CartItemDTO> items = new ArrayList<>();
    private String totalAmountFormatted;

    public static CartDTO empty() {
        CartDTO dto = new CartDTO();
        dto.setItems(Collections.emptyList());
        dto.setTotalAmountFormatted("0");
        return dto;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public String getTotalAmountFormatted() {
        return totalAmountFormatted;
    }

    public void setTotalAmountFormatted(String totalAmountFormatted) {
        this.totalAmountFormatted = totalAmountFormatted;
    }
}
