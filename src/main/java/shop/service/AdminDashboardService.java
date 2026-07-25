package shop.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Order;
import shop.domain.OrderItem;
import shop.domain.OrderStatus;
import shop.domain.VppItem;
import shop.domain.dto.AdminDashboardDTO;
import shop.domain.dto.AdminDashboardDTO.MetricDTO;
import shop.domain.dto.AdminDashboardDTO.OrderStatusDTO;
import shop.domain.dto.AdminDashboardDTO.RecentOrderDTO;
import shop.domain.dto.AdminDashboardDTO.SalesPointDTO;
import shop.domain.dto.AdminDashboardDTO.TopProductDTO;
import shop.repository.OrderRepository;

@Service
@Transactional(readOnly = true)
public class AdminDashboardService {

    private static final int RECENT_ORDER_LIMIT = 8;
    private static final int TOP_PRODUCT_LIMIT = 5;

    private static final DateTimeFormatter DATE_LABEL_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "MMMM d, yyyy",
                    Locale.ENGLISH
            );

    private static final DateTimeFormatter ORDER_DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd-MM-yyyy HH:mm"
            );

    private static final Map<OrderStatus, String> STATUS_COLORS =
            createStatusColors();

    private final OrderRepository orderRepository;

    public AdminDashboardService(
            OrderRepository orderRepository
    ) {
        this.orderRepository = orderRepository;
    }

    /*
     * ============================================================
     * BUILD DASHBOARD FOR ONE SPECIFIC DATE
     * ============================================================
     */

    public AdminDashboardDTO getDashboard(
            LocalDate requestedDate
    ) {
        LocalDate selectedDate =
                requestedDate == null
                        ? LocalDate.now()
                        : requestedDate;

        LocalDate previousDate =
                selectedDate.minusDays(1);

        LocalDateTime selectedStart =
                selectedDate.atStartOfDay();

        LocalDateTime selectedEnd =
                selectedDate
                        .plusDays(1)
                        .atStartOfDay();

        LocalDateTime previousStart =
                previousDate.atStartOfDay();

        LocalDateTime previousEnd =
                selectedStart;

        List<Order> allOrders =
                loadDistinctOrders();

        List<Order> selectedOrders =
                filterOrders(
                        allOrders,
                        selectedStart,
                        selectedEnd
                );

        List<Order> previousOrders =
                filterOrders(
                        allOrders,
                        previousStart,
                        previousEnd
                );

        /*
         * Revenue only includes DELIVERED orders created
         * on the selected date.
         */
        BigDecimal selectedRevenue =
                calculateDeliveredRevenue(
                        selectedOrders
                );

        BigDecimal previousRevenue =
                calculateDeliveredRevenue(
                        previousOrders
                );

        long selectedDeliveredOrders =
                countByStatus(
                        selectedOrders,
                        OrderStatus.DELIVERED
                );

        long previousDeliveredOrders =
                countByStatus(
                        previousOrders,
                        OrderStatus.DELIVERED
                );

        BigDecimal selectedAverageOrderValue =
                calculateAverageOrderValue(
                        selectedRevenue,
                        selectedDeliveredOrders
                );

        BigDecimal previousAverageOrderValue =
                calculateAverageOrderValue(
                        previousRevenue,
                        previousDeliveredOrders
                );

        long selectedReturningCustomers =
                countReturningCustomers(
                        allOrders,
                        selectedOrders,
                        selectedStart
                );

        long previousReturningCustomers =
                countReturningCustomers(
                        allOrders,
                        previousOrders,
                        previousStart
                );

        long selectedShippingOrders =
                countByStatus(
                        selectedOrders,
                        OrderStatus.SHIPPING
                );

        long previousShippingOrders =
                countByStatus(
                        previousOrders,
                        OrderStatus.SHIPPING
                );

        AdminDashboardDTO dashboard =
                new AdminDashboardDTO();

        dashboard.setSelectedDate(
                selectedDate.toString()
        );

        dashboard.setSelectedDateLabel(
                selectedDate.format(
                        DATE_LABEL_FORMATTER
                )
        );

        dashboard.setPreviousDateLabel(
                previousDate.format(
                        DATE_LABEL_FORMATTER
                )
        );

        dashboard.setRevenue(
                createMetric(
                        formatMoney(
                                selectedRevenue
                        ),
                        selectedRevenue,
                        previousRevenue
                )
        );

        dashboard.setTotalOrders(
                createMetric(
                        formatNumber(
                                selectedOrders.size()
                        ),
                        BigDecimal.valueOf(
                                selectedOrders.size()
                        ),
                        BigDecimal.valueOf(
                                previousOrders.size()
                        )
                )
        );

        dashboard.setAverageOrderValue(
                createMetric(
                        formatMoney(
                                selectedAverageOrderValue
                        ),
                        selectedAverageOrderValue,
                        previousAverageOrderValue
                )
        );

        dashboard.setReturningCustomers(
                createMetric(
                        formatNumber(
                                selectedReturningCustomers
                        ),
                        BigDecimal.valueOf(
                                selectedReturningCustomers
                        ),
                        BigDecimal.valueOf(
                                previousReturningCustomers
                        )
                )
        );

        dashboard.setShippingOrders(
                createMetric(
                        formatNumber(
                                selectedShippingOrders
                        ),
                        BigDecimal.valueOf(
                                selectedShippingOrders
                        ),
                        BigDecimal.valueOf(
                                previousShippingOrders
                        )
                )
        );

        List<OrderStatusDTO> orderStatuses =
                buildOrderStatuses(
                        selectedOrders
                );

        dashboard.setOrderStatusTotal(
                selectedOrders.size()
        );

        dashboard.setOrderStatuses(
                orderStatuses
        );

        dashboard.setOrderStatusGradient(
                buildOrderStatusGradient(
                        orderStatuses
                )
        );

        /*
         * Creates 24 points:
         * 00:00 through 23:00.
         */
        dashboard.setSalesPoints(
                buildHourlySalesPoints(
                        selectedOrders
                )
        );

        /*
         * Recent orders only include orders created
         * on the selected date.
         */
        dashboard.setRecentOrders(
                buildRecentOrders(
                        selectedOrders,
                        RECENT_ORDER_LIMIT
                )
        );

        /*
         * Top products only include delivered orders
         * created on the selected date.
         */
        dashboard.setTopProducts(
                buildTopProducts(
                        selectedOrders,
                        TOP_PRODUCT_LIMIT
                )
        );

        return dashboard;
    }

    /*
     * ============================================================
     * LOAD ORDERS
     * ============================================================
     *
     * The fetch-join repository query can return one Order more
     * than once when it contains several OrderItems.
     *
     * This method removes duplicate Orders by ID.
     */

    private List<Order> loadDistinctOrders() {
        Map<Long, Order> uniqueOrders =
                new LinkedHashMap<>();

        for (Order order :
                orderRepository
                        .findAllWithUserAndItemsOrderByCreatedAtDesc()) {

            if (order != null
                    && order.getId() != null) {

                uniqueOrders.putIfAbsent(
                        order.getId(),
                        order
                );
            }
        }

        return uniqueOrders
                .values()
                .stream()
                .sorted(
                        Comparator.comparing(
                                Order::getCreatedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )
                .toList();
    }

    /*
     * ============================================================
     * FILTER ORDERS BY DATE RANGE
     * ============================================================
     */

    private List<Order> filterOrders(
            List<Order> orders,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return orders
                .stream()
                .filter(order ->
                        order.getCreatedAt() != null
                )
                .filter(order ->
                        !order.getCreatedAt()
                                .isBefore(start)
                )
                .filter(order ->
                        order.getCreatedAt()
                                .isBefore(end)
                )
                .toList();
    }

    /*
     * ============================================================
     * REVENUE
     * ============================================================
     */

    private BigDecimal calculateDeliveredRevenue(
            List<Order> orders
    ) {
        return orders
                .stream()
                .filter(order ->
                        hasStatus(
                                order,
                                OrderStatus.DELIVERED
                        )
                )
                .map(
                        Order::getTotalAmount
                )
                .filter(amount ->
                        amount != null
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    /*
     * ============================================================
     * AVERAGE ORDER VALUE
     * ============================================================
     *
     * Average Order Value =
     *
     * Delivered Revenue / Delivered Order Count
     */

    private BigDecimal calculateAverageOrderValue(
            BigDecimal revenue,
            long deliveredOrderCount
    ) {
        if (deliveredOrderCount <= 0) {
            return BigDecimal.ZERO;
        }

        return revenue.divide(
                BigDecimal.valueOf(
                        deliveredOrderCount
                ),
                0,
                RoundingMode.HALF_UP
        );
    }

    /*
     * ============================================================
     * COUNT ORDER STATUS
     * ============================================================
     */

    private long countByStatus(
            List<Order> orders,
            OrderStatus status
    ) {
        return orders
                .stream()
                .filter(order ->
                        hasStatus(
                                order,
                                status
                        )
                )
                .count();
    }

    /*
     * ============================================================
     * RETURNING CUSTOMERS
     * ============================================================
     *
     * A returning customer:
     *
     * 1. Has a non-cancelled order on the selected date.
     * 2. Already had at least one non-cancelled order before
     *    the selected date.
     *
     * Multiple orders from the same customer on one date are
     * counted as one returning customer.
     */

    private long countReturningCustomers(
            List<Order> allOrders,
            List<Order> selectedOrders,
            LocalDateTime selectedStart
    ) {
        Set<Long> previousCustomerIds =
                new HashSet<>();

        for (Order order : allOrders) {

            if (order.getUser() == null
                    || order.getCreatedAt() == null
                    || !order.getCreatedAt()
                    .isBefore(selectedStart)
                    || hasStatus(
                    order,
                    OrderStatus.CANCELLED
            )) {
                continue;
            }

            previousCustomerIds.add(
                    order.getUser()
                            .getId()
            );
        }

        return selectedOrders
                .stream()
                .filter(order ->
                        order.getUser() != null
                )
                .filter(order ->
                        !hasStatus(
                                order,
                                OrderStatus.CANCELLED
                        )
                )
                .map(order ->
                        order.getUser()
                                .getId()
                )
                .distinct()
                .filter(
                        previousCustomerIds::contains
                )
                .count();
    }

    /*
     * ============================================================
     * HOURLY SALES CHART
     * ============================================================
     *
     * Creates exactly 24 chart points:
     *
     * 00:00
     * 01:00
     * ...
     * 23:00
     *
     * Revenue:
     * DELIVERED orders only.
     *
     * Order count:
     * All orders created during the hour.
     */

    private List<SalesPointDTO> buildHourlySalesPoints(
            List<Order> selectedOrders
    ) {
        Map<Integer, HourlySalesAccumulator> hourlyData =
                new LinkedHashMap<>();

        for (int hour = 0;
             hour < 24;
             hour++) {

            hourlyData.put(
                    hour,
                    new HourlySalesAccumulator()
            );
        }

        for (Order order : selectedOrders) {

            if (order.getCreatedAt() == null) {
                continue;
            }

            int hour =
                    order.getCreatedAt()
                            .getHour();

            HourlySalesAccumulator accumulator =
                    hourlyData.get(hour);

            accumulator.orderCount++;

            if (hasStatus(
                    order,
                    OrderStatus.DELIVERED
            )
                    && order.getTotalAmount() != null) {

                accumulator.revenue =
                        accumulator.revenue.add(
                                order.getTotalAmount()
                        );
            }
        }

        List<SalesPointDTO> points =
                new ArrayList<>();

        for (Map.Entry<Integer, HourlySalesAccumulator> entry :
                hourlyData.entrySet()) {

            String hourLabel =
                    String.format(
                            Locale.US,
                            "%02d:00",
                            entry.getKey()
                    );

            points.add(
                    new SalesPointDTO(
                            hourLabel,
                            entry.getValue()
                                    .revenue
                                    .longValue(),
                            entry.getValue()
                                    .orderCount
                    )
            );
        }

        return points;
    }

    /*
     * ============================================================
     * ORDER STATUS
     * ============================================================
     */

    private List<OrderStatusDTO> buildOrderStatuses(
            List<Order> orders
    ) {
        Map<OrderStatus, Long> counts =
                new EnumMap<>(
                        OrderStatus.class
                );

        for (OrderStatus status :
                OrderStatus.values()) {

            counts.put(
                    status,
                    0L
            );
        }

        for (Order order : orders) {

            OrderStatus status =
                    parseStatus(
                            order.getStatus()
                    );

            if (status != null) {

                counts.computeIfPresent(
                        status,
                        (key, value) ->
                                value + 1
                );
            }
        }

        long total =
                orders.size();

        List<OrderStatusDTO> result =
                new ArrayList<>();

        for (OrderStatus status :
                OrderStatus.values()) {

            long count =
                    counts.getOrDefault(
                            status,
                            0L
                    );

            double percentage =
                    total == 0
                            ? 0.0
                            : count * 100.0 / total;

            OrderStatusDTO dto =
                    new OrderStatusDTO();

            dto.setStatus(
                    status.name()
            );

            dto.setLabel(
                    shortStatusLabel(
                            status
                    )
            );

            dto.setCount(
                    count
            );

            dto.setPercentage(
                    percentage
            );

            dto.setPercentageFormatted(
                    String.format(
                            Locale.US,
                            "%.1f%%",
                            percentage
                    )
            );

            dto.setColor(
                    STATUS_COLORS.get(status)
            );

            result.add(dto);
        }

        return result;
    }

    /*
     * ============================================================
     * ORDER STATUS DONUT GRADIENT
     * ============================================================
     */

    private String buildOrderStatusGradient(
            List<OrderStatusDTO> statuses
    ) {
        double totalPercentage =
                statuses
                        .stream()
                        .mapToDouble(
                                OrderStatusDTO::getPercentage
                        )
                        .sum();

        if (totalPercentage <= 0.0) {
            return "conic-gradient(#E5E7EB 0% 100%)";
        }

        StringBuilder gradient =
                new StringBuilder(
                        "conic-gradient("
                );

        double cursor =
                0.0;

        boolean hasSegment =
                false;

        for (OrderStatusDTO status : statuses) {

            if (status.getPercentage() <= 0.0) {
                continue;
            }

            double end =
                    Math.min(
                            cursor
                                    + status.getPercentage(),
                            100.0
                    );

            if (hasSegment) {
                gradient.append(", ");
            }

            gradient
                    .append(
                            status.getColor()
                    )
                    .append(' ')
                    .append(
                            formatPercentage(cursor)
                    )
                    .append("% ")
                    .append(
                            formatPercentage(end)
                    )
                    .append('%');

            cursor =
                    end;

            hasSegment =
                    true;
        }

        if (!hasSegment) {
            return "conic-gradient(#E5E7EB 0% 100%)";
        }

        gradient.append(')');

        return gradient.toString();
    }

    /*
     * ============================================================
     * RECENT ORDERS
     * ============================================================
     */

    private List<RecentOrderDTO> buildRecentOrders(
            List<Order> selectedOrders,
            int limit
    ) {
        return selectedOrders
                .stream()
                .sorted(
                        Comparator.comparing(
                                Order::getCreatedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )
                .limit(
                        Math.max(
                                limit,
                                0
                        )
                )
                .map(
                        this::toRecentOrderDTO
                )
                .toList();
    }

    private RecentOrderDTO toRecentOrderDTO(
            Order order
    ) {
        RecentOrderDTO dto =
                new RecentOrderDTO();

        OrderStatus status =
                parseStatus(
                        order.getStatus()
                );

        dto.setId(
                order.getId()
        );

        dto.setOrderCode(
                order.getOrderCode()
        );

        dto.setCustomerName(
                order.getUser() == null
                        || !StringUtils.hasText(
                        order.getUser()
                                .getFullName()
                )
                        ? safeText(
                        order.getRecipientName(),
                        "Guest"
                )
                        : order.getUser()
                                .getFullName()
        );

        dto.setTotalFormatted(
                formatMoney(
                        order.getTotalAmount()
                )
        );

        dto.setStatus(
                status == null
                        ? order.getStatus()
                        : status.name()
        );

        dto.setStatusLabel(
                status == null
                        ? safeText(
                        order.getStatus(),
                        "Unknown"
                )
                        : shortStatusLabel(
                        status
                )
        );

        dto.setStatusCssClass(
                status == null
                        ? "neutral"
                        : status.name()
                                .toLowerCase(
                                        Locale.ROOT
                                )
        );

        dto.setCreatedAtFormatted(
                order.getCreatedAt() == null
                        ? "—"
                        : order.getCreatedAt()
                                .format(
                                        ORDER_DATE_FORMATTER
                                )
        );

        return dto;
    }

    /*
     * ============================================================
     * TOP-SELLING PRODUCTS
     * ============================================================
     *
     * Only OrderItems belonging to DELIVERED orders are counted.
     */

    private List<TopProductDTO> buildTopProducts(
            List<Order> selectedOrders,
            int limit
    ) {
        Map<String, ProductSalesAccumulator> productSales =
                new LinkedHashMap<>();

        selectedOrders
                .stream()
                .filter(order ->
                        hasStatus(
                                order,
                                OrderStatus.DELIVERED
                        )
                )
                .flatMap(order ->
                        order.getItems()
                                .stream()
                )
                .forEach(item ->
                        accumulateProductSale(
                                productSales,
                                item
                        )
                );

        return productSales
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparingLong(
                                        ProductSalesAccumulator
                                                ::getUnitsSold
                                )
                                .reversed()
                                .thenComparing(
                                        ProductSalesAccumulator
                                                ::getRevenue,
                                        Comparator.reverseOrder()
                                )
                                .thenComparing(
                                        ProductSalesAccumulator
                                                ::getProductName,
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .limit(
                        Math.max(
                                limit,
                                0
                        )
                )
                .map(
                        this::toTopProductDTO
                )
                .toList();
    }

    private void accumulateProductSale(
            Map<String, ProductSalesAccumulator> productSales,
            OrderItem item
    ) {
        if (item == null) {
            return;
        }

        String key;
        String productName;
        String productTypeLabel;
        String imageUrl;

        /*
         * BOOK
         */
        if (item.getBook() != null) {

            key =
                    "BOOK:"
                            + item.getBook()
                            .getId();

            productName =
                    item.getBookTitle();

            productTypeLabel =
                    "Book";

            imageUrl =
                    item.getBook()
                            .getImageUrl();

        /*
         * VPP
         */
        } else if (item.getVppItem() != null) {

            VppItem vppItem =
                    item.getVppItem();

            key =
                    "VPP:"
                            + vppItem.getId();

            productName =
                    item.getBookTitle();

            productTypeLabel =
                    "Stationery";

            imageUrl =
                    vppItem.hasImageData()
                            ? "/uploads/vpp/"
                            + vppItem.getId()
                            + "/image"
                            : vppItem.getImagePath();

        /*
         * UNKNOWN PRODUCT TYPE
         */
        } else {

            key =
                    "ITEM:"
                            + item.getId();

            productName =
                    item.getBookTitle();

            productTypeLabel =
                    "Product";

            imageUrl =
                    null;
        }

        ProductSalesAccumulator accumulator =
                productSales.computeIfAbsent(
                        key,
                        ignored ->
                                new ProductSalesAccumulator(
                                        safeText(
                                                productName,
                                                "Unknown product"
                                        ),
                                        productTypeLabel,
                                        imageUrl
                                )
                );

        accumulator.unitsSold +=
                Math.max(
                        item.getQuantity(),
                        0
                );

        if (item.getLineTotal() != null) {

            accumulator.revenue =
                    accumulator.revenue.add(
                            item.getLineTotal()
                    );
        }
    }

    private TopProductDTO toTopProductDTO(
            ProductSalesAccumulator source
    ) {
        TopProductDTO dto =
                new TopProductDTO();

        dto.setProductName(
                source.productName
        );

        dto.setProductTypeLabel(
                source.productTypeLabel
        );

        dto.setUnitsSold(
                source.unitsSold
        );

        dto.setRevenueFormatted(
                formatMoney(
                        source.revenue
                )
        );

        dto.setImageUrl(
                source.imageUrl
        );

        return dto;
    }

    /*
     * ============================================================
     * METRIC COMPARISON
     * ============================================================
     */

    private MetricDTO createMetric(
            String value,
            BigDecimal current,
            BigDecimal previous
    ) {
        BigDecimal safeCurrent =
                current == null
                        ? BigDecimal.ZERO
                        : current;

        BigDecimal safePrevious =
                previous == null
                        ? BigDecimal.ZERO
                        : previous;

        /*
         * Previous date = 0.
         */
        if (safePrevious.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            /*
             * Current and previous are both 0.
             */
            if (safeCurrent.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

                return new MetricDTO(
                        value,
                        "0.0%",
                        "neutral",
                        "fa-minus"
                );
            }

            /*
             * Previous = 0, current > 0.
             */
            return new MetricDTO(
                    value,
                    "New",
                    "up",
                    "fa-arrow-up"
            );
        }

        BigDecimal percentageChange =
                safeCurrent
                        .subtract(
                                safePrevious
                        )
                        .divide(
                                safePrevious.abs(),
                                4,
                                RoundingMode.HALF_UP
                        )
                        .multiply(
                                BigDecimal.valueOf(100)
                        );

        int comparison =
                percentageChange.compareTo(
                        BigDecimal.ZERO
                );

        String cssClass =
                comparison > 0
                        ? "up"
                        : comparison < 0
                        ? "down"
                        : "neutral";

        String iconClass =
                comparison > 0
                        ? "fa-arrow-up"
                        : comparison < 0
                        ? "fa-arrow-down"
                        : "fa-minus";

        String prefix =
                comparison > 0
                        ? "+"
                        : "";

        return new MetricDTO(
                value,
                prefix
                        + percentageChange
                        .setScale(
                                1,
                                RoundingMode.HALF_UP
                        )
                        .toPlainString()
                        + "%",
                cssClass,
                iconClass
        );
    }

    /*
     * ============================================================
     * STATUS HELPERS
     * ============================================================
     */

    private boolean hasStatus(
            Order order,
            OrderStatus expectedStatus
    ) {
        return order != null
                && expectedStatus != null
                && expectedStatus.name()
                .equalsIgnoreCase(
                        order.getStatus()
                );
    }

    private OrderStatus parseStatus(
            String status
    ) {
        if (!StringUtils.hasText(status)) {
            return null;
        }

        try {
            return OrderStatus.valueOf(
                    status.trim()
                            .toUpperCase(
                                    Locale.ROOT
                            )
            );

        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private String shortStatusLabel(
            OrderStatus status
    ) {
        return switch (status) {

            case PENDING ->
                    "Pending";

            case CONFIRMED ->
                    "Confirmed";

            case SHIPPING ->
                    "Shipping";

            case DELIVERED ->
                    "Delivered";

            case CANCELLED ->
                    "Cancelled";
        };
    }

    /*
     * ============================================================
     * FORMAT HELPERS
     * ============================================================
     */

    private String safeText(
            String value,
            String fallback
    ) {
        return StringUtils.hasText(value)
                ? value.trim()
                : fallback;
    }

    private String formatMoney(
            BigDecimal amount
    ) {
        BigDecimal safeAmount =
                amount == null
                        ? BigDecimal.ZERO
                        : amount;

        return NumberFormat
                .getIntegerInstance(
                        Locale.GERMANY
                )
                .format(
                        safeAmount.longValue()
                )
                + " ₫";
    }

    private String formatNumber(
            long value
    ) {
        return NumberFormat
                .getIntegerInstance(
                        Locale.US
                )
                .format(value);
    }

    private String formatPercentage(
            double value
    ) {
        return String.format(
                Locale.US,
                "%.4f",
                Math.max(
                        0.0,
                        Math.min(
                                value,
                                100.0
                        )
                )
        );
    }

    /*
     * ============================================================
     * ORDER STATUS COLORS
     * ============================================================
     */

    private static Map<OrderStatus, String>
    createStatusColors() {

        Map<OrderStatus, String> colors =
                new EnumMap<>(
                        OrderStatus.class
                );

        colors.put(
                OrderStatus.PENDING,
                "#F59E0B"
        );

        colors.put(
                OrderStatus.CONFIRMED,
                "#3B82F6"
        );

        colors.put(
                OrderStatus.SHIPPING,
                "#8B5CF6"
        );

        colors.put(
                OrderStatus.DELIVERED,
                "#22C55E"
        );

        colors.put(
                OrderStatus.CANCELLED,
                "#EF4444"
        );

        return colors;
    }

    /*
     * ============================================================
     * INTERNAL ACCUMULATORS
     * ============================================================
     */

    private static class HourlySalesAccumulator {

        private BigDecimal revenue =
                BigDecimal.ZERO;

        private long orderCount;
    }

    private static class ProductSalesAccumulator {

        private final String productName;
        private final String productTypeLabel;
        private final String imageUrl;

        private long unitsSold;

        private BigDecimal revenue =
                BigDecimal.ZERO;

        private ProductSalesAccumulator(
                String productName,
                String productTypeLabel,
                String imageUrl
        ) {
            this.productName =
                    productName;

            this.productTypeLabel =
                    productTypeLabel;

            this.imageUrl =
                    imageUrl;
        }

        public String getProductName() {
            return productName;
        }

        public long getUnitsSold() {
            return unitsSold;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }
    }
}