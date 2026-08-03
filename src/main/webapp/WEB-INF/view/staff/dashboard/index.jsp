<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />

    <title>Dashboard — Booktify Staff</title>

    <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
</head>

<body class="admin-shell">

    <jsp:include page="/WEB-INF/view/layout/staff/sidebar.jsp" />

    <main class="admin-main">

        <jsp:include page="/WEB-INF/view/layout/staff/header.jsp" />

        <section class="admin-content">

            <div class="admin-hero">
                <div>
                    <p class="admin-kicker">Welcome Back</p>

                    <h2>Dashboard Overview</h2>

                    <p>
                        Monitor orders, reviews, vouchers, stationery, suppliers, and
                        customer contact requests from one place.
                    </p>
                </div>
            </div>

            <section class="admin-cards">

                <a href="${ctx}/staff/orders?status=PENDING" class="admin-card" style="text-decoration:none;">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-hourglass-half"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Pending Orders</span>
                        <strong><c:out value="${pendingOrders}" default="0" /></strong>
                    </div>
                </a>

                <a href="${ctx}/staff/orders" class="admin-card" style="text-decoration:none;">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-receipt"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Total Orders</span>
                        <strong><c:out value="${totalOrders}" default="0" /></strong>
                    </div>
                </a>

                <a href="${ctx}/staff/reviews" class="admin-card" style="text-decoration:none;">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-star"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Products With Reviews</span>
                        <strong><c:out value="${reviewedProducts}" default="0" /></strong>
                    </div>
                </a>

                <a href="${ctx}/staff/vouchers?status=ACTIVE" class="admin-card" style="text-decoration:none;">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-ticket"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Active Vouchers</span>
                        <strong><c:out value="${activeVouchers}" default="0" /></strong>
                    </div>
                </a>

                <a href="${ctx}/staff/contact" class="admin-card" style="text-decoration:none;">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-headset"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Open Contact Requests</span>
                        <strong><c:out value="${openContacts}" default="0" /></strong>
                    </div>
                </a>

                <a href="${ctx}/staff/suppliers" class="admin-card" style="text-decoration:none;">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-truck"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Suppliers</span>
                        <strong><c:out value="${totalSuppliers}" default="0" /></strong>
                    </div>
                </a>

                <a href="${ctx}/staff/contact" class="admin-card" style="text-decoration:none;">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-inbox"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Total Contacts</span>
                        <strong><c:out value="${totalContacts}" default="0" /></strong>
                    </div>
                </a>

            </section>

            <article class="admin-panel" style="margin-top:24px;padding:0;overflow:hidden;">

                <div style="padding:18px 22px;border-bottom:1px solid var(--border);display:flex;align-items:center;justify-content:space-between;">
                    <h3 style="margin:0;"><i class="fa-solid fa-clock-rotate-left"></i> Recent Orders</h3>
                    <a href="${ctx}/staff/orders" class="admin-button admin-button--ghost">
                        View All Orders
                    </a>
                </div>

                <div class="admin-table-wrap" style="border:none;border-radius:0;box-shadow:none;">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Order Code</th>
                                <th>Customer</th>
                                <th class="col-money">Total</th>
                                <th>Status</th>
                                <th>Date</th>
                                <th style="width:72px;text-align:center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty recentOrders}">
                                    <tr>
                                        <td colspan="6" style="text-align:center;padding:42px 20px;color:#9CA3AF;">
                                            No orders yet.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${recentOrders}" var="order">
                                        <tr>
                                            <td><strong>${order.orderCode}</strong></td>
                                            <td>${order.customerName}</td>
                                            <td class="col-money"><strong>${order.totalAmountFormatted} &#8363;</strong></td>
                                            <td>
                                                <span class="order-status-badge order-status-badge--${order.status}">
                                                    ${order.statusLabel}
                                                </span>
                                            </td>
                                            <td>${order.createdAtFormatted}</td>
                                            <td class="admin-table__actions" style="text-align:center;">
                                                <a href="${ctx}/staff/orders/${order.id}" class="icon-link" title="View details">
                                                    <i class="fa-solid fa-eye"></i>
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

        </section>

    </main>

</body>

</html>
