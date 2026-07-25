package shop.domain.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardDTO {

    /*
     * Ngày được chọn theo định dạng yyyy-MM-dd.
     *
     * Ví dụ:
     * 2026-07-25
     */
    private String selectedDate;

    /*
     * Nhãn ngày hiển thị trên Dashboard.
     *
     * Ví dụ:
     * July 25, 2026
     */
    private String selectedDateLabel;

    /*
     * Ngày liền trước, dùng để hiển thị nội dung so sánh.
     *
     * Ví dụ:
     * July 24, 2026
     */
    private String previousDateLabel;

    private MetricDTO revenue;
    private MetricDTO totalOrders;
    private MetricDTO averageOrderValue;
    private MetricDTO returningCustomers;
    private MetricDTO shippingOrders;

    private long orderStatusTotal;
    private String orderStatusGradient;

    /*
     * Dữ liệu biểu đồ theo từng giờ trong ngày.
     */
    private List<SalesPointDTO> salesPoints =
            new ArrayList<>();

    /*
     * Số lượng đơn theo từng trạng thái.
     */
    private List<OrderStatusDTO> orderStatuses =
            new ArrayList<>();

    /*
     * Các đơn được tạo trong ngày đã chọn.
     */
    private List<RecentOrderDTO> recentOrders =
            new ArrayList<>();

    /*
     * Các sản phẩm bán chạy trong ngày đã chọn.
     */
    private List<TopProductDTO> topProducts =
            new ArrayList<>();

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(
            String selectedDate
    ) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedDateLabel() {
        return selectedDateLabel;
    }

    public void setSelectedDateLabel(
            String selectedDateLabel
    ) {
        this.selectedDateLabel =
                selectedDateLabel;
    }

    public String getPreviousDateLabel() {
        return previousDateLabel;
    }

    public void setPreviousDateLabel(
            String previousDateLabel
    ) {
        this.previousDateLabel =
                previousDateLabel;
    }

    public MetricDTO getRevenue() {
        return revenue;
    }

    public void setRevenue(
            MetricDTO revenue
    ) {
        this.revenue = revenue;
    }

    public MetricDTO getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(
            MetricDTO totalOrders
    ) {
        this.totalOrders = totalOrders;
    }

    public MetricDTO getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(
            MetricDTO averageOrderValue
    ) {
        this.averageOrderValue =
                averageOrderValue;
    }

    public MetricDTO getReturningCustomers() {
        return returningCustomers;
    }

    public void setReturningCustomers(
            MetricDTO returningCustomers
    ) {
        this.returningCustomers =
                returningCustomers;
    }

    public MetricDTO getShippingOrders() {
        return shippingOrders;
    }

    public void setShippingOrders(
            MetricDTO shippingOrders
    ) {
        this.shippingOrders =
                shippingOrders;
    }

    public long getOrderStatusTotal() {
        return orderStatusTotal;
    }

    public void setOrderStatusTotal(
            long orderStatusTotal
    ) {
        this.orderStatusTotal =
                orderStatusTotal;
    }

    public String getOrderStatusGradient() {
        return orderStatusGradient;
    }

    public void setOrderStatusGradient(
            String orderStatusGradient
    ) {
        this.orderStatusGradient =
                orderStatusGradient;
    }

    public List<SalesPointDTO> getSalesPoints() {
        return salesPoints;
    }

    public void setSalesPoints(
            List<SalesPointDTO> salesPoints
    ) {
        this.salesPoints =
                salesPoints == null
                        ? new ArrayList<>()
                        : salesPoints;
    }

    public List<OrderStatusDTO> getOrderStatuses() {
        return orderStatuses;
    }

    public void setOrderStatuses(
            List<OrderStatusDTO> orderStatuses
    ) {
        this.orderStatuses =
                orderStatuses == null
                        ? new ArrayList<>()
                        : orderStatuses;
    }

    public List<RecentOrderDTO> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(
            List<RecentOrderDTO> recentOrders
    ) {
        this.recentOrders =
                recentOrders == null
                        ? new ArrayList<>()
                        : recentOrders;
    }

    public List<TopProductDTO> getTopProducts() {
        return topProducts;
    }

    public void setTopProducts(
            List<TopProductDTO> topProducts
    ) {
        this.topProducts =
                topProducts == null
                        ? new ArrayList<>()
                        : topProducts;
    }

    /*
     * ============================================================
     * DASHBOARD METRIC
     * ============================================================
     */

    public static class MetricDTO {

        /*
         * Giá trị hiển thị.
         *
         * Ví dụ:
         * 125,000 ₫
         * 12
         */
        private String value;

        /*
         * Mức thay đổi so với ngày trước.
         *
         * Ví dụ:
         * +12.5%
         * -5.0%
         * New
         */
        private String changeText;

        /*
         * Giá trị:
         * up
         * down
         * neutral
         */
        private String trendCssClass;

        /*
         * Font Awesome class:
         * fa-arrow-up
         * fa-arrow-down
         * fa-minus
         */
        private String trendIconClass;

        public MetricDTO() {
        }

        public MetricDTO(
                String value,
                String changeText,
                String trendCssClass,
                String trendIconClass
        ) {
            this.value = value;
            this.changeText = changeText;
            this.trendCssClass =
                    trendCssClass;
            this.trendIconClass =
                    trendIconClass;
        }

        public String getValue() {
            return value;
        }

        public void setValue(
                String value
        ) {
            this.value = value;
        }

        public String getChangeText() {
            return changeText;
        }

        public void setChangeText(
                String changeText
        ) {
            this.changeText =
                    changeText;
        }

        public String getTrendCssClass() {
            return trendCssClass;
        }

        public void setTrendCssClass(
                String trendCssClass
        ) {
            this.trendCssClass =
                    trendCssClass;
        }

        public String getTrendIconClass() {
            return trendIconClass;
        }

        public void setTrendIconClass(
                String trendIconClass
        ) {
            this.trendIconClass =
                    trendIconClass;
        }
    }

    /*
     * ============================================================
     * HOURLY SALES CHART POINT
     * ============================================================
     */

    public static class SalesPointDTO {

        /*
         * Ví dụ:
         * 00:00
         * 01:00
         * 02:00
         */
        private String label;

        /*
         * Doanh thu từ các đơn DELIVERED được tạo trong giờ đó.
         */
        private long revenue;

        /*
         * Tổng số đơn được tạo trong giờ đó.
         */
        private long orderCount;

        public SalesPointDTO() {
        }

        public SalesPointDTO(
                String label,
                long revenue,
                long orderCount
        ) {
            this.label = label;
            this.revenue = revenue;
            this.orderCount = orderCount;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(
                String label
        ) {
            this.label = label;
        }

        public long getRevenue() {
            return revenue;
        }

        public void setRevenue(
                long revenue
        ) {
            this.revenue = revenue;
        }

        public long getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(
                long orderCount
        ) {
            this.orderCount =
                    orderCount;
        }
    }

    /*
     * ============================================================
     * ORDER STATUS
     * ============================================================
     */

    public static class OrderStatusDTO {

        private String status;
        private String label;
        private long count;
        private double percentage;
        private String percentageFormatted;
        private String color;

        public String getStatus() {
            return status;
        }

        public void setStatus(
                String status
        ) {
            this.status = status;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(
                String label
        ) {
            this.label = label;
        }

        public long getCount() {
            return count;
        }

        public void setCount(
                long count
        ) {
            this.count = count;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(
                double percentage
        ) {
            this.percentage =
                    percentage;
        }

        public String getPercentageFormatted() {
            return percentageFormatted;
        }

        public void setPercentageFormatted(
                String percentageFormatted
        ) {
            this.percentageFormatted =
                    percentageFormatted;
        }

        public String getColor() {
            return color;
        }

        public void setColor(
                String color
        ) {
            this.color = color;
        }
    }

    /*
     * ============================================================
     * RECENT ORDER
     * ============================================================
     */

    public static class RecentOrderDTO {

        private Long id;
        private String orderCode;
        private String customerName;
        private String totalFormatted;
        private String status;
        private String statusLabel;
        private String statusCssClass;
        private String createdAtFormatted;

        public Long getId() {
            return id;
        }

        public void setId(
                Long id
        ) {
            this.id = id;
        }

        public String getOrderCode() {
            return orderCode;
        }

        public void setOrderCode(
                String orderCode
        ) {
            this.orderCode = orderCode;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(
                String customerName
        ) {
            this.customerName =
                    customerName;
        }

        public String getTotalFormatted() {
            return totalFormatted;
        }

        public void setTotalFormatted(
                String totalFormatted
        ) {
            this.totalFormatted =
                    totalFormatted;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(
                String status
        ) {
            this.status = status;
        }

        public String getStatusLabel() {
            return statusLabel;
        }

        public void setStatusLabel(
                String statusLabel
        ) {
            this.statusLabel =
                    statusLabel;
        }

        public String getStatusCssClass() {
            return statusCssClass;
        }

        public void setStatusCssClass(
                String statusCssClass
        ) {
            this.statusCssClass =
                    statusCssClass;
        }

        public String getCreatedAtFormatted() {
            return createdAtFormatted;
        }

        public void setCreatedAtFormatted(
                String createdAtFormatted
        ) {
            this.createdAtFormatted =
                    createdAtFormatted;
        }
    }

    /*
     * ============================================================
     * TOP-SELLING PRODUCT
     * ============================================================
     */

    public static class TopProductDTO {

        private String productName;
        private String productTypeLabel;
        private long unitsSold;
        private String revenueFormatted;
        private String imageUrl;

        public String getProductName() {
            return productName;
        }

        public void setProductName(
                String productName
        ) {
            this.productName =
                    productName;
        }

        public String getProductTypeLabel() {
            return productTypeLabel;
        }

        public void setProductTypeLabel(
                String productTypeLabel
        ) {
            this.productTypeLabel =
                    productTypeLabel;
        }

        public long getUnitsSold() {
            return unitsSold;
        }

        public void setUnitsSold(
                long unitsSold
        ) {
            this.unitsSold =
                    unitsSold;
        }

        public String getRevenueFormatted() {
            return revenueFormatted;
        }

        public void setRevenueFormatted(
                String revenueFormatted
        ) {
            this.revenueFormatted =
                    revenueFormatted;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(
                String imageUrl
        ) {
            this.imageUrl = imageUrl;
        }
    }
}