<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <title>Order ${order.orderCode} — Booktify Admin</title>
    <style>
        .order-detail-grid {
            display: grid;
            grid-template-columns: 1.4fr 1fr;
            gap: 20px;
            align-items: start;
        }
        .order-panel {
            background: #fff;
            border: 1px solid #E5E7EB;
            border-radius: 18px;
            padding: 20px;
        }
        .order-panel h3 {
            margin: 0 0 14px;
            font-size: 1rem;
        }
        .order-info-row {
            display: flex;
            justify-content: space-between;
            gap: 12px;
            padding: 8px 0;
            border-bottom: 1px solid #F3F4F6;
            font-size: .9rem;
        }
        .order-info-row:last-child { border-bottom: none; }
        .order-info-row dt { color: #6B7280; margin: 0; }
        .order-info-row dd { margin: 0; font-weight: 600; text-align: right; }
        .order-status-badge {
            display: inline-flex;
            padding: 6px 12px;
            border-radius: 999px;
            font-size: .78rem;
            font-weight: 700;
        }
        .order-status-badge--PENDING { background: #FEF3C7; color: #92400E; }
        .order-status-badge--CONFIRMED { background: #DBEAFE; color: #1D4ED8; }
        .order-status-badge--SHIPPING { background: #E0E7FF; color: #4338CA; }
        .order-status-badge--DELIVERED { background: #D1FAE5; color: #047857; }
        .order-status-badge--CANCELLED { background: #FEE2E2; color: #B91C1C; }
        .status-form {
            display: flex;
            gap: 10px;
            margin-top: 16px;
            flex-wrap: wrap;
        }
        .status-form select {
            flex: 1;
            min-width: 160px;
            padding: 10px 12px;
            border: 1px solid #D1D5DB;
            border-radius: 10px;
        }
        @media (max-width: 900px) {
            .order-detail-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />
    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />
        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-receipt"></i> Order Detail</p>
                    <h2>${order.orderCode}</h2>
                    <p>${order.createdAtFormatted}</p>
                </div>
                <a href="/admin/orders" class="admin-button">
                    <i class="fa-solid fa-arrow-left"></i> Back to Orders
                </a>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="admin-alert admin-alert--success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert--error">${errorMessage}</div>
            </c:if>

            <div class="order-detail-grid">
                <div class="order-panel">
                    <h3><i class="fa-solid fa-box"></i> Items</h3>
                    <div class="admin-table-wrap">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>Book</th>
                                    <th>Unit Price</th>
                                    <th>Qty</th>
                                    <th>Line Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${order.items}" var="item">
                                    <tr>
                                        <td>${item.bookTitle}</td>
                                        <td>${item.unitPriceFormatted} &#8363;</td>
                                        <td>${item.quantity}</td>
                                        <td>${item.lineTotalFormatted} &#8363;</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div>
                    <div class="order-panel" style="margin-bottom:20px;">
                        <h3><i class="fa-solid fa-user"></i> Customer</h3>
                        <dl>
                            <div class="order-info-row"><dt>Name</dt><dd>${order.customerName}</dd></div>
                            <div class="order-info-row"><dt>Email</dt><dd>${order.customerEmail}</dd></div>
                        </dl>
                    </div>

                    <div class="order-panel" style="margin-bottom:20px;">
                        <h3><i class="fa-solid fa-truck"></i> Shipping</h3>
                        <dl>
                            <div class="order-info-row"><dt>Recipient</dt><dd>${order.recipientName}</dd></div>
                            <div class="order-info-row"><dt>Phone</dt><dd>${order.recipientPhone}</dd></div>
                            <div class="order-info-row"><dt>Address</dt><dd>${order.shippingAddress}</dd></div>
                            <div class="order-info-row"><dt>Payment</dt><dd>${order.paymentMethodLabel}</dd></div>
                        </dl>
                    </div>

                    <div class="order-panel">
                        <h3><i class="fa-solid fa-circle-info"></i> Summary</h3>
                        <dl>
                            <div class="order-info-row"><dt>Subtotal</dt><dd>${order.subtotalFormatted} &#8363;</dd></div>
                            <div class="order-info-row"><dt>Discount</dt><dd>-${order.discountAmountFormatted} &#8363;</dd></div>
                            <div class="order-info-row"><dt>Shipping fee</dt><dd>${order.shippingFeeFormatted} &#8363;</dd></div>
                            <div class="order-info-row"><dt><strong>Total</strong></dt><dd><strong>${order.totalAmountFormatted} &#8363;</strong></dd></div>
                        </dl>

                        <div style="margin-top:14px;">
                            <span class="order-status-badge order-status-badge--${order.status}">${order.statusLabel}</span>
                        </div>

                        <form class="status-form" method="post" action="/admin/orders/${order.id}/status">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <c:choose>
                                <c:when test="${empty allowedNextStatuses}">
                                    <p style="margin:0;color:#6B7280;font-size:.88rem;">
                                        This order is in a final status and cannot be changed.
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <select name="status" required>
                                        <c:forEach items="${allowedNextStatuses}" var="st">
                                            <option value="${st}">${st.label}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit" class="admin-button admin-button--primary">
                                        <i class="fa-solid fa-rotate"></i> Update Status
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    </main>
</body>
</html>
