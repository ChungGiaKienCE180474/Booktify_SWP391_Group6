package shop.domain.dto;

public class StockSummaryDTO {

    /*
     * Tổng số sản phẩm đang được quản lý trong bảng stock.
     *
     * Bao gồm:
     * - Book
     * - VPP
     */
    private long totalProducts;

    /*
     * Tổng số lượng hàng đang có trong kho.
     *
     * Ví dụ:
     * Book A = 10
     * Book B = 20
     * VPP C = 5
     *
     * totalStockUnits = 35
     */
    private long totalStockUnits;

    /*
     * Số sản phẩm còn hàng nhưng sắp hết.
     *
     * Quy tắc hiện tại:
     * quantity > 0
     * quantity <= LOW_STOCK_THRESHOLD
     */
    private long lowStockProducts;

    /*
     * Số sản phẩm đã hết hàng.
     *
     * quantity = 0
     */
    private long outOfStockProducts;

    public StockSummaryDTO() {
    }

    public StockSummaryDTO(
            long totalProducts,
            long totalStockUnits,
            long lowStockProducts,
            long outOfStockProducts
    ) {
        this.totalProducts = totalProducts;
        this.totalStockUnits = totalStockUnits;
        this.lowStockProducts = lowStockProducts;
        this.outOfStockProducts = outOfStockProducts;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(
            long totalProducts
    ) {
        this.totalProducts = totalProducts;
    }

    public long getTotalStockUnits() {
        return totalStockUnits;
    }

    public void setTotalStockUnits(
            long totalStockUnits
    ) {
        this.totalStockUnits = totalStockUnits;
    }

    public long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(
            long lowStockProducts
    ) {
        this.lowStockProducts = lowStockProducts;
    }

    public long getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public void setOutOfStockProducts(
            long outOfStockProducts
    ) {
        this.outOfStockProducts = outOfStockProducts;
    }
}