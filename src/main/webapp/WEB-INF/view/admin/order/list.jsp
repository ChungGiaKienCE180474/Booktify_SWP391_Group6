<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <title>Orders — Booktify Admin</title>
    <style>
        .order-status-badge {
            display: inline-flex;
            align-items: center;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: .72rem;
            font-weight: 700;
        }
        .order-status-badge--PENDING { background: #FEF3C7; color: #92400E; border: 1px solid #FDE68A; }
        .order-status-badge--CONFIRMED { background: #DBEAFE; color: #1D4ED8; border: 1px solid #BFDBFE; }
        .order-status-badge--SHIPPING { background: #E0E7FF; color: #4338CA; border: 1px solid #C7D2FE; }
        .order-status-badge--DELIVERED { background: #D1FAE5; color: #047857; border: 1px solid #A7F3D0; }
        .order-status-badge--CANCELLED { background: #FEE2E2; color: #B91C1C; border: 1px solid #FECACA; }
    </style>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />
    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />
        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-receipt"></i> Order Management</p>
                    <h2>Orders</h2>
                    <p>View and manage customer orders</p>
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="admin-alert admin-alert--success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert--error">${errorMessage}</div>
            </c:if>

            <div class="admin-card">
                <c:choose>
                    <c:when test="${empty orders}">
                        <div class="admin-empty">
                            <i class="fa-solid fa-box-open"></i>
                            <p>No orders yet.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="admin-table-wrap">
                            <table class="admin-table">
                                <thead>
                                    <tr>
                                        <th>Order Code</th>
                                        <th>Customer</th>
                                        <th>Recipient</th>
                                        <th>Total</th>
                                        <th>Status</th>
                                        <th>Date</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${orders}" var="order">
                                        <tr>
                                            <td><strong>${order.orderCode}</strong></td>
                                            <td>
                                                <div>${order.customerName}</div>
                                                <div style="font-size:.75rem;color:#6B7280;">${order.customerEmail}</div>
                                            </td>
                                            <td>${order.recipientName}</td>
                                            <td>${order.totalAmountFormatted} &#8363;</td>
                                            <td>
                                                <span class="order-status-badge order-status-badge--${order.status}">
                                                    ${order.statusLabel}
                                                </span>
                                            </td>
                                            <td>${order.createdAtFormatted}</td>
                                            <td>
                                                <a href="/admin/orders/${order.id}" class="icon-link" title="View">
                                                    <i class="fa-solid fa-eye"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </main>
</body>
</html>
