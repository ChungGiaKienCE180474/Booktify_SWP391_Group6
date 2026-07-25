<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx"
       value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8" />

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0" />

    <title>Dashboard — Booktify Admin</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <link rel="stylesheet"
          href="${ctx}/css/admin-dashboard.css?v=7" />

    <link rel="stylesheet"
          href="${ctx}/css/admin-dashboard-overview.css?v=2" />

</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

    <section class="admin-content dashboard-overview-page">

        <%-- =====================================================
             PAGE HEADER
             ===================================================== --%>

        <div class="dashboard-heading">

            <div>

                <p class="admin-kicker">

                    <i class="fa-solid fa-chart-line"></i>

                    Dashboard

                </p>

                <h2>Dashboard Overview</h2>

                <p>
                    Daily revenue, orders, customer activity,
                    delivery progress, and product performance.
                </p>

            </div>

            <form method="get"
                  action="${ctx}/admin"
                  class="dashboard-date-form">

                <label for="dashboardDate">

                    <i class="fa-regular fa-calendar"></i>

                    <span>Statistics Date</span>

                </label>

                <input id="dashboardDate"
                       type="date"
                       name="date"
                       value="${dashboard.selectedDate}"
                       aria-label="Select dashboard statistics date"
                       onchange="this.form.submit()" />

            </form>

        </div>

        <div class="dashboard-selected-date">

            <i class="fa-regular fa-calendar-check"></i>

            <span>
                Statistics for
                <strong>
                    <c:out value="${dashboard.selectedDateLabel}" />
                </strong>
            </span>

        </div>

        <%-- =====================================================
             KPI CARDS
             ===================================================== --%>

        <div class="dashboard-kpi-grid">

            <%-- REVENUE --%>

            <article class="dashboard-kpi-card">

                <div class="
                    dashboard-kpi-icon
                    dashboard-kpi-icon--revenue">

                    <i class="fa-solid fa-coins"></i>

                </div>

                <div class="dashboard-kpi-content">

                    <span>Revenue</span>

                    <strong>
                        <c:out value="${dashboard.revenue.value}" />
                    </strong>

                    <div class="dashboard-kpi-comparison">

                        <b class="
                            dashboard-trend
                            dashboard-trend--${dashboard.revenue.trendCssClass}">

                            <i class="
                                fa-solid
                                ${dashboard.revenue.trendIconClass}">
                            </i>

                            <c:out value="${dashboard.revenue.changeText}" />

                        </b>

                        <small>
                            vs
                            <c:out value="${dashboard.previousDateLabel}" />
                        </small>

                    </div>

                </div>

            </article>

            <%-- TOTAL ORDERS --%>

            <article class="dashboard-kpi-card">

                <div class="
                    dashboard-kpi-icon
                    dashboard-kpi-icon--orders">

                    <i class="fa-solid fa-bag-shopping"></i>

                </div>

                <div class="dashboard-kpi-content">

                    <span>Total Orders</span>

                    <strong>
                        <c:out value="${dashboard.totalOrders.value}" />
                    </strong>

                    <div class="dashboard-kpi-comparison">

                        <b class="
                            dashboard-trend
                            dashboard-trend--${dashboard.totalOrders.trendCssClass}">

                            <i class="
                                fa-solid
                                ${dashboard.totalOrders.trendIconClass}">
                            </i>

                            <c:out value="${dashboard.totalOrders.changeText}" />

                        </b>

                        <small>
                            vs
                            <c:out value="${dashboard.previousDateLabel}" />
                        </small>

                    </div>

                </div>

            </article>

            <%-- AVERAGE ORDER VALUE --%>

            <article class="dashboard-kpi-card">

                <div class="
                    dashboard-kpi-icon
                    dashboard-kpi-icon--average">

                    <i class="fa-regular fa-credit-card"></i>

                </div>

                <div class="dashboard-kpi-content">

                    <span>Average Order Value</span>

                    <strong>
                        <c:out value="${dashboard.averageOrderValue.value}" />
                    </strong>

                    <div class="dashboard-kpi-comparison">

                        <b class="
                            dashboard-trend
                            dashboard-trend--${dashboard.averageOrderValue.trendCssClass}">

                            <i class="
                                fa-solid
                                ${dashboard.averageOrderValue.trendIconClass}">
                            </i>

                            <c:out value="${dashboard.averageOrderValue.changeText}" />

                        </b>

                        <small>
                            vs
                            <c:out value="${dashboard.previousDateLabel}" />
                        </small>

                    </div>

                </div>

            </article>

            <%-- RETURNING CUSTOMERS --%>

            <article class="dashboard-kpi-card">

                <div class="
                    dashboard-kpi-icon
                    dashboard-kpi-icon--customers">

                    <i class="fa-solid fa-users"></i>

                </div>

                <div class="dashboard-kpi-content">

                    <span>Returning Customers</span>

                    <strong>
                        <c:out value="${dashboard.returningCustomers.value}" />
                    </strong>

                    <div class="dashboard-kpi-comparison">

                        <b class="
                            dashboard-trend
                            dashboard-trend--${dashboard.returningCustomers.trendCssClass}">

                            <i class="
                                fa-solid
                                ${dashboard.returningCustomers.trendIconClass}">
                            </i>

                            <c:out value="${dashboard.returningCustomers.changeText}" />

                        </b>

                        <small>
                            vs
                            <c:out value="${dashboard.previousDateLabel}" />
                        </small>

                    </div>

                </div>

            </article>

            <%-- SHIPPING ORDERS --%>

            <article class="dashboard-kpi-card">

                <div class="
                    dashboard-kpi-icon
                    dashboard-kpi-icon--shipping">

                    <i class="fa-solid fa-truck-fast"></i>

                </div>

                <div class="dashboard-kpi-content">

                    <span>Shipping Orders</span>

                    <strong>
                        <c:out value="${dashboard.shippingOrders.value}" />
                    </strong>

                    <div class="dashboard-kpi-comparison">

                        <b class="
                            dashboard-trend
                            dashboard-trend--${dashboard.shippingOrders.trendCssClass}">

                            <i class="
                                fa-solid
                                ${dashboard.shippingOrders.trendIconClass}">
                            </i>

                            <c:out value="${dashboard.shippingOrders.changeText}" />

                        </b>

                        <small>
                            vs
                            <c:out value="${dashboard.previousDateLabel}" />
                        </small>

                    </div>

                </div>

            </article>

        </div>

        <%-- =====================================================
             SALES CHART AND ORDER STATUS
             ===================================================== --%>

        <div class="dashboard-main-grid">

            <%-- SALES OVERVIEW --%>

            <article class="
                dashboard-panel
                dashboard-sales-panel">

                <div class="dashboard-panel-header">

                    <div>

                        <h3>Sales Overview</h3>

                        <p>
                            Hourly activity on
                            <c:out value="${dashboard.selectedDateLabel}" />
                        </p>

                    </div>

                    <div class="dashboard-chart-legend">

                        <span>

                            <i class="
                                dashboard-legend-dot
                                dashboard-legend-dot--revenue">
                            </i>

                            Revenue (₫)

                        </span>

                        <span>

                            <i class="
                                dashboard-legend-dot
                                dashboard-legend-dot--orders">
                            </i>

                            Orders

                        </span>

                    </div>

                </div>

                <div class="dashboard-chart-wrap">

                    <canvas id="salesOverviewChart"
                            aria-label="Hourly revenue and order chart">
                    </canvas>

                </div>

            </article>

            <%-- ORDER STATUS --%>

            <article class="
                dashboard-panel
                dashboard-status-panel">

                <div class="dashboard-panel-header">

                    <div>

                        <h3>Order Status</h3>

                        <p>
                            Orders created on
                            <c:out value="${dashboard.selectedDateLabel}" />
                        </p>

                    </div>

                </div>

                <div class="dashboard-status-content">

                    <div class="dashboard-donut"
                         style="background: <c:out value='${dashboard.orderStatusGradient}' />;">

                        <div class="dashboard-donut-center">

                            <strong>
                                <c:out value="${dashboard.orderStatusTotal}" />
                            </strong>

                            <span>Total Orders</span>

                        </div>

                    </div>

                    <div class="dashboard-status-list">

                        <c:forEach
                            items="${dashboard.orderStatuses}"
                            var="statusItem">

                            <div class="dashboard-status-row">

                                <div class="dashboard-status-name">

                                    <i style="background: ${statusItem.color};">
                                    </i>

                                    <span>
                                        <c:out value="${statusItem.label}" />
                                    </span>

                                </div>

                                <strong>

                                    <c:out value="${statusItem.count}" />

                                    <small>
                                        (
                                        <c:out value="${statusItem.percentageFormatted}" />
                                        )
                                    </small>

                                </strong>

                            </div>

                        </c:forEach>

                    </div>

                </div>

                <a href="${ctx}/admin/orders"
                   class="dashboard-panel-link">

                    View all orders

                    <i class="fa-solid fa-arrow-right"></i>

                </a>

            </article>

        </div>

        <%-- =====================================================
             RECENT ORDERS
             ===================================================== --%>

        <article class="
            dashboard-panel
            dashboard-full-width-panel">

            <div class="dashboard-panel-header">

                <div>

                    <h3>Recent Orders</h3>

                    <p>
                        Latest orders created on
                        <c:out value="${dashboard.selectedDateLabel}" />
                    </p>

                </div>

                <a href="${ctx}/admin/orders"
                   class="dashboard-small-button">

                    View All Orders

                </a>

            </div>

            <div class="dashboard-table-wrap">

                <table class="dashboard-table">

                    <thead>

                    <tr>
                        <th>Order Code</th>
                        <th>Customer</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th>Created At</th>
                        <th>Action</th>
                    </tr>

                    </thead>

                    <tbody>

                    <c:choose>

                        <c:when test="${empty dashboard.recentOrders}">

                            <tr>

                                <td colspan="6"
                                    class="dashboard-empty-cell">

                                    No orders were created on this date.

                                </td>

                            </tr>

                        </c:when>

                        <c:otherwise>

                            <c:forEach
                                items="${dashboard.recentOrders}"
                                var="order">

                                <tr>

                                    <td>

                                        <a href="${ctx}/admin/orders/${order.id}"
                                           class="dashboard-order-link">

                                            <i class="fa-solid fa-cube"></i>

                                            <c:out value="${order.orderCode}" />

                                        </a>

                                    </td>

                                    <td>

                                        <c:out value="${order.customerName}" />

                                    </td>

                                    <td class="dashboard-money">

                                        <c:out value="${order.totalFormatted}" />

                                    </td>

                                    <td>

                                        <span class="
                                            dashboard-order-status
                                            dashboard-order-status--${order.statusCssClass}">

                                            <c:out value="${order.statusLabel}" />

                                        </span>

                                    </td>

                                    <td class="dashboard-muted-cell">

                                        <c:out value="${order.createdAtFormatted}" />

                                    </td>

                                    <td>

                                        <a href="${ctx}/admin/orders/${order.id}"
                                           class="dashboard-row-action">

                                            <i class="fa-regular fa-eye"></i>

                                            View

                                        </a>

                                    </td>

                                </tr>

                            </c:forEach>

                        </c:otherwise>

                    </c:choose>

                    </tbody>

                </table>

            </div>

        </article>

        <%-- =====================================================
             TOP SELLING PRODUCTS
             ===================================================== --%>

        <article class="
            dashboard-panel
            dashboard-full-width-panel">

            <div class="dashboard-panel-header">

                <div>

                    <h3>Top Selling Products</h3>

                    <p>
                        Products from delivered orders created on
                        <c:out value="${dashboard.selectedDateLabel}" />
                    </p>

                </div>

                <a href="${ctx}/admin/orders"
                   class="dashboard-small-button">

                    View All Orders

                </a>

            </div>

            <div class="dashboard-table-wrap">

                <table class="
                    dashboard-table
                    dashboard-top-products-table">

                    <thead>

                    <tr>
                        <th>#</th>
                        <th>Product</th>
                        <th>Type</th>
                        <th>Units Sold</th>
                        <th>Revenue</th>
                    </tr>

                    </thead>

                    <tbody>

                    <c:choose>

                        <c:when test="${empty dashboard.topProducts}">

                            <tr>

                                <td colspan="5"
                                    class="dashboard-empty-cell">

                                    No delivered-product sales
                                    were recorded on this date.

                                </td>

                            </tr>

                        </c:when>

                        <c:otherwise>

                            <c:forEach
                                items="${dashboard.topProducts}"
                                var="product"
                                varStatus="loop">

                                <tr>

                                    <td class="dashboard-rank">

                                        <c:out value="${loop.index + 1}" />

                                    </td>

                                    <td>

                                        <div class="dashboard-product-cell">

                                            <c:choose>

                                                <c:when test="${not empty product.imageUrl}">

                                                    <img
                                                        src="${ctx}${product.imageUrl}"
                                                        alt="Product image"
                                                        loading="lazy"
                                                        onerror="
                                                            this.style.display='none';
                                                            this.nextElementSibling.style.display='grid';
                                                        " />

                                                    <span
                                                        class="dashboard-product-placeholder"
                                                        style="display: none;">

                                                        <i class="fa-solid fa-box"></i>

                                                    </span>

                                                </c:when>

                                                <c:otherwise>

                                                    <span class="dashboard-product-placeholder">

                                                        <i class="fa-solid fa-box"></i>

                                                    </span>

                                                </c:otherwise>

                                            </c:choose>

                                            <strong>
                                                <c:out value="${product.productName}" />
                                            </strong>

                                        </div>

                                    </td>

                                    <td>

                                        <span class="dashboard-product-type">

                                            <c:out value="${product.productTypeLabel}" />

                                        </span>

                                    </td>

                                    <td class="dashboard-stock-number">

                                        <c:out value="${product.unitsSold}" />

                                    </td>

                                    <td class="dashboard-money">

                                        <c:out value="${product.revenueFormatted}" />

                                    </td>

                                </tr>

                            </c:forEach>

                        </c:otherwise>

                    </c:choose>

                    </tbody>

                </table>

            </div>

        </article>

    </section>

</main>

<%-- =============================================================
     SALES OVERVIEW CHART
     ============================================================= --%>

<script>
(function () {

    'use strict';

    const labels = [

        <c:forEach
            items="${dashboard.salesPoints}"
            var="point"
            varStatus="status">

            "<c:out value="${point.label}" />"${status.last ? '' : ','}

        </c:forEach>

    ];

    const revenueValues = [

        <c:forEach
            items="${dashboard.salesPoints}"
            var="point"
            varStatus="status">

            ${point.revenue}${status.last ? '' : ','}

        </c:forEach>

    ];

    const orderValues = [

        <c:forEach
            items="${dashboard.salesPoints}"
            var="point"
            varStatus="status">

            ${point.orderCount}${status.last ? '' : ','}

        </c:forEach>

    ];

    const canvas =
        document.getElementById(
            'salesOverviewChart'
        );

    if (!canvas || labels.length === 0) {
        return;
    }

    const context =
        canvas.getContext('2d');

    if (!context) {
        return;
    }

    let resizeTimer;

    function formatCompact(value) {

        const numericValue =
            Number(value) || 0;

        if (numericValue >= 1000000000) {

            return (
                numericValue / 1000000000
            ).toFixed(1) + 'B';

        }

        if (numericValue >= 1000000) {

            return (
                numericValue / 1000000
            ).toFixed(1) + 'M';

        }

        if (numericValue >= 1000) {

            return (
                numericValue / 1000
            ).toFixed(0) + 'K';

        }

        return String(
            Math.round(numericValue)
        );
    }

    function drawLine(
        values,
        maxValue,
        xForIndex,
        yForValue,
        strokeColor,
        fillColor
    ) {

        if (!Array.isArray(values)
                || values.length === 0) {

            return;
        }

        context.beginPath();

        values.forEach(
            function (value, index) {

                const x =
                    xForIndex(index);

                const y =
                    yForValue(
                        Number(value) || 0,
                        maxValue
                    );

                if (index === 0) {

                    context.moveTo(x, y);

                } else {

                    context.lineTo(x, y);
                }
            }
        );

        if (fillColor) {

            const lastX =
                xForIndex(
                    values.length - 1
                );

            const firstX =
                xForIndex(0);

            const baseline =
                yForValue(
                    0,
                    maxValue
                );

            context.lineTo(
                lastX,
                baseline
            );

            context.lineTo(
                firstX,
                baseline
            );

            context.closePath();

            context.fillStyle =
                fillColor;

            context.fill();

            context.beginPath();

            values.forEach(
                function (value, index) {

                    const x =
                        xForIndex(index);

                    const y =
                        yForValue(
                            Number(value) || 0,
                            maxValue
                        );

                    if (index === 0) {

                        context.moveTo(x, y);

                    } else {

                        context.lineTo(x, y);
                    }
                }
            );
        }

        context.strokeStyle =
            strokeColor;

        context.lineWidth =
            2.2;

        context.lineJoin =
            'round';

        context.lineCap =
            'round';

        context.stroke();

        values.forEach(
            function (value, index) {

                const x =
                    xForIndex(index);

                const y =
                    yForValue(
                        Number(value) || 0,
                        maxValue
                    );

                context.beginPath();

                context.arc(
                    x,
                    y,
                    2.7,
                    0,
                    Math.PI * 2
                );

                context.fillStyle =
                    strokeColor;

                context.fill();

                context.lineWidth =
                    1.5;

                context.strokeStyle =
                    '#FFFFFF';

                context.stroke();
            }
        );
    }

    function drawChart() {

        const bounds =
            canvas.getBoundingClientRect();

        const width =
            Math.max(
                bounds.width,
                320
            );

        const height =
            Math.max(
                bounds.height,
                280
            );

        const pixelRatio =
            window.devicePixelRatio || 1;

        canvas.width =
            Math.round(
                width * pixelRatio
            );

        canvas.height =
            Math.round(
                height * pixelRatio
            );

        context.setTransform(
            pixelRatio,
            0,
            0,
            pixelRatio,
            0,
            0
        );

        context.clearRect(
            0,
            0,
            width,
            height
        );

        const padding = {
            top: 18,
            right: 46,
            bottom: 38,
            left: 58
        };

        const chartWidth =
            width
            - padding.left
            - padding.right;

        const chartHeight =
            height
            - padding.top
            - padding.bottom;

        const revenueMax =
            Math.max(
                ...revenueValues.map(
                    function (value) {
                        return Number(value) || 0;
                    }
                ),
                1
            );

        const orderMax =
            Math.max(
                ...orderValues.map(
                    function (value) {
                        return Number(value) || 0;
                    }
                ),
                1
            );

        const roundedRevenueMax =
            Math.ceil(
                revenueMax / 5
            ) * 5 || 1;

        const roundedOrderMax =
            Math.ceil(
                orderMax / 5
            ) * 5 || 1;

        function xForIndex(index) {

            if (labels.length === 1) {

                return padding.left
                    + chartWidth / 2;
            }

            return padding.left
                + index
                * chartWidth
                / (labels.length - 1);
        }

        function yForValue(
            value,
            maxValue
        ) {

            return padding.top
                + chartHeight
                - value
                / maxValue
                * chartHeight;
        }

        context.font =
            '11px Inter, Segoe UI, sans-serif';

        context.textBaseline =
            'middle';

        for (
            let lineIndex = 0;
            lineIndex <= 4;
            lineIndex++
        ) {

            const ratio =
                lineIndex / 4;

            const y =
                padding.top
                + ratio
                * chartHeight;

            context.beginPath();

            context.moveTo(
                padding.left,
                y
            );

            context.lineTo(
                width - padding.right,
                y
            );

            context.strokeStyle =
                '#E8EDF3';

            context.lineWidth =
                1;

            context.stroke();

            const leftValue =
                roundedRevenueMax
                * (1 - ratio);

            const rightValue =
                roundedOrderMax
                * (1 - ratio);

            context.fillStyle =
                '#8A94A6';

            context.textAlign =
                'right';

            context.fillText(
                formatCompact(leftValue),
                padding.left - 9,
                y
            );

            context.textAlign =
                'left';

            context.fillText(
                String(
                    Math.round(rightValue)
                ),
                width
                - padding.right
                + 9,
                y
            );
        }

        /*
         * 24 hourly points are generated by the service.
         * Display labels every four hours and always display
         * the final 23:00 label.
         */
        context.fillStyle =
            '#8A94A6';

        context.textAlign =
            'center';

        context.textBaseline =
            'top';

        labels.forEach(
            function (label, index) {

                const shouldDisplay =
                    index % 4 === 0
                    || index === labels.length - 1;

                if (!shouldDisplay) {
                    return;
                }

                context.fillText(
                    label,
                    xForIndex(index),
                    height
                    - padding.bottom
                    + 11
                );
            }
        );

        const revenueGradient =
            context.createLinearGradient(
                0,
                padding.top,
                0,
                padding.top
                + chartHeight
            );

        revenueGradient.addColorStop(
            0,
            'rgba(5, 150, 105, 0.22)'
        );

        revenueGradient.addColorStop(
            1,
            'rgba(5, 150, 105, 0.02)'
        );

        drawLine(
            revenueValues,
            roundedRevenueMax,
            xForIndex,
            yForValue,
            '#059669',
            revenueGradient
        );

        drawLine(
            orderValues,
            roundedOrderMax,
            xForIndex,
            yForValue,
            '#3B82F6',
            null
        );
    }

    drawChart();

    window.addEventListener(
        'resize',
        function () {

            clearTimeout(
                resizeTimer
            );

            resizeTimer =
                setTimeout(
                    drawChart,
                    120
                );
        }
    );

})();
</script>

</body>
</html>