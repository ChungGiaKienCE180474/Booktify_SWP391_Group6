package shop.domain.dto;

public class PromotionPreviewDTO {
    private String promotionSelection;
    private String originalSubtotalFormatted;
    private String promotionDiscountFormatted;
    private String voucherDiscountFormatted;
    private String finalTotalFormatted;
    private String message;

    public PromotionPreviewDTO() {}

    public PromotionPreviewDTO(String promotionSelection, String originalSubtotalFormatted,
                               String promotionDiscountFormatted, String voucherDiscountFormatted,
                               String finalTotalFormatted, String message) {
        this.promotionSelection = promotionSelection;
        this.originalSubtotalFormatted = originalSubtotalFormatted;
        this.promotionDiscountFormatted = promotionDiscountFormatted;
        this.voucherDiscountFormatted = voucherDiscountFormatted;
        this.finalTotalFormatted = finalTotalFormatted;
        this.message = message;
    }

    public String getPromotionSelection() { return promotionSelection; }
    public void setPromotionSelection(String promotionSelection) { this.promotionSelection = promotionSelection; }
    public String getOriginalSubtotalFormatted() { return originalSubtotalFormatted; }
    public void setOriginalSubtotalFormatted(String originalSubtotalFormatted) { this.originalSubtotalFormatted = originalSubtotalFormatted; }
    public String getPromotionDiscountFormatted() { return promotionDiscountFormatted; }
    public void setPromotionDiscountFormatted(String promotionDiscountFormatted) { this.promotionDiscountFormatted = promotionDiscountFormatted; }
    public String getVoucherDiscountFormatted() { return voucherDiscountFormatted; }
    public void setVoucherDiscountFormatted(String voucherDiscountFormatted) { this.voucherDiscountFormatted = voucherDiscountFormatted; }
    public String getFinalTotalFormatted() { return finalTotalFormatted; }
    public void setFinalTotalFormatted(String finalTotalFormatted) { this.finalTotalFormatted = finalTotalFormatted; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
