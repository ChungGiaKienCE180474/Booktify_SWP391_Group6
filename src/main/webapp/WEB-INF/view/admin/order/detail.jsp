<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=5" />
    <title>Order ${order.orderCode} — Booktify Admin</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />
    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />
        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-receipt"></i> Order Management</p>
                    <h2>Order Details</h2>
                    <p>Review order items, customer information, and update status.</p>
                </div>
                <a href="/admin/orders" class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-arrow-left"></i> Back to Orders
                </a>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="admin-alert admin-alert--success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert--error">${errorMessage}</div>
            </c:if>

            <div class="admin-order-card">
                <div class="admin-order-header">
                    <div class="admin-order-header__main">
                        <h3>${order.orderCode}</h3>
                        <p class="admin-order-header__meta">
                            <i class="fa-regular fa-clock"></i> Placed on ${order.createdAtFormatted}
                        </p>
                    </div>
                    <span class="order-status-badge order-status-badge--${order.status}">
                        ${order.statusLabel}
                    </span>
                </div>

                <div class="admin-order-body">
                    <div class="admin-order-layout">
                        <div>
                            <div class="admin-panel" style="padding:0;overflow:hidden;">
                                <div style="padding:18px 22px;border-bottom:1px solid var(--border);">
                                    <h3 style="margin:0;"><i class="fa-solid fa-box"></i> Order Items</h3>
                                </div>
                                <div class="admin-table-wrap" style="border:none;border-radius:0;box-shadow:none;">
                                    <table class="admin-table">
                                        <thead>
                                            <tr>
                                                <th>Product</th>
                                                <th class="col-money">Unit Price</th>
                                                <th style="width:80px;text-align:center;">Qty</th>
                                                <th class="col-money">Line Total</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${order.items}" var="item">
                                                <tr>
                                                    <td>
                                                        <div class="admin-order-product">
                                                            <div class="admin-order-product__thumb">
                                                                <c:choose>
                                                                    <c:when test="${not empty item.bookImageUrl}">
                                                                        <img src="${item.bookImageUrl}" alt="${item.bookTitle}" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <i class="fa-solid fa-book"></i>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                            <strong>${item.bookTitle}</strong>
                                                        </div>
                                                    </td>
                                                    <td class="col-money">${item.unitPriceFormatted} &#8363;</td>
                                                    <td style="text-align:center;">${item.quantity}</td>
                                                    <td class="col-money"><strong>${item.lineTotalFormatted} &#8363;</strong></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="admin-order-side">
                            <div class="admin-panel">
                                <h3><i class="fa-solid fa-user"></i> Customer</h3>
                                <dl class="admin-kv-list">
                                    <div class="admin-kv-row">
                                        <dt>Name</dt>
                                        <dd>${order.customerName}</dd>
                                    </div>
                                    <div class="admin-kv-row">
                                        <dt>Email</dt>
                                        <dd>${order.customerEmail}</dd>
                                    </div>
                                </dl>
                            </div>

                            <div class="admin-panel">
                                <h3><i class="fa-solid fa-truck"></i> Shipping</h3>
                                <dl class="admin-kv-list">
                                    <div class="admin-kv-row">
                                        <dt>Recipient</dt>
                                        <dd>${order.recipientName}</dd>
                                    </div>
                                    <div class="admin-kv-row">
                                        <dt>Phone</dt>
                                        <dd>${order.recipientPhone}</dd>
                                    </div>
                                    <div class="admin-kv-row">
                                        <dt>Address</dt>
                                        <dd>${order.shippingAddress}</dd>
                                    </div>
                                    <div class="admin-kv-row">
                                        <dt>Payment</dt>
                                        <dd>${order.paymentMethodLabel}</dd>
                                    </div>
                                </dl>
                            </div>

                            <div class="admin-panel">
                                <h3><i class="fa-solid fa-circle-info"></i> Payment Summary</h3>
                                <dl class="admin-kv-list">
                                    <div class="admin-kv-row">
                                        <dt>Subtotal</dt>
                                        <dd>${order.subtotalFormatted} &#8363;</dd>
                                    </div>
                                    <div class="admin-kv-row">
                                        <dt>Discount</dt>
                                        <dd>-${order.discountAmountFormatted} &#8363;</dd>
                                    </div>
                                    <c:if test="${not empty order.voucherCode}">
                                        <div class="admin-kv-row">
                                            <dt>Voucher</dt>
                                            <dd>${order.voucherCode}</dd>
                                        </div>
                                    </c:if>
                                    <div class="admin-kv-row admin-kv-row--total">
                                        <dt><strong>Total</strong></dt>
                                        <dd><strong>${order.totalAmountFormatted} &#8363;</strong></dd>
                                    </div>
                                </dl>

                                <c:if test="${not empty order.note}">
                                    <div style="margin-top:14px;padding-top:14px;border-top:1px solid var(--border);">
                                        <div style="font-size:.72rem;font-weight:700;text-transform:uppercase;letter-spacing:.08em;color:var(--text-muted);margin-bottom:6px;">
                                            Note
                                        </div>
                                        <p class="admin-order-status-note">${order.note}</p>
                                    </div>
                                </c:if>
                            </div>

                            <div class="admin-panel">
                                <h3><i class="fa-solid fa-rotate"></i> Update Status</h3>
                                <form class="admin-order-status-form" method="post" action="/admin/orders/${order.id}/status">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <c:choose>
                                        <c:when test="${empty allowedNextStatuses}">
                                            <p class="admin-order-status-note">
                                                This order is in a final status and cannot be changed.
                                            </p>
                                        </c:when>
                                        <c:otherwise>
                                            <select name="status" class="admin-input" required>
                                                <c:forEach items="${allowedNextStatuses}" var="st">
                                                    <option value="${st}">${st.label}</option>
                                                </c:forEach>
                                            </select>
                                            <button type="submit" class="admin-button">
                                                <i class="fa-solid fa-check"></i> Update
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                            </div>

                            <div class="admin-panel">
                                <h3><i class="fa-solid fa-money-bill-wave"></i> Payment</h3>
                                <p class="admin-order-status-note" style="margin-bottom:.5rem;">
                                    Status:
                                    <span class="order-status-badge order-status-badge--${order.paymentStatus}">
                                        ${order.paymentStatusLabel}
                                    </span>
                                </p>
                                <%-- Cảnh báo: đơn đã giao nhưng chưa được đánh dấu thanh toán --%>
                                <c:if test="${order.status == 'DELIVERED' and order.paymentStatus == 'UNPAID'}">
                                    <div style="margin:.25rem 0 .5rem;padding:.6rem .8rem;background:#fef2f2;border:1px solid #fecaca;border-radius:8px;color:#b91c1c;font-size:.85rem;line-height:1.5;">
                                        <i class="fa-solid fa-triangle-exclamation"></i>
                                        <strong>Warning:</strong> this order is <strong>Delivered</strong> but still unpaid
                                    </div>
                                </c:if>
                                <c:if test="${order.canAdminCompletePayment}">
                                    <p class="admin-order-status-note" style="margin-bottom:.5rem;">
                                        The customer has confirmed payment. Complete the order to mark it
                                        paid and delivered.
                                    </p>
                                    <form method="post" action="/admin/orders/${order.id}/complete-payment">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                        <button type="submit" class="admin-button">
                                            <i class="fa-solid fa-check"></i> Complete (paid + delivered)
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>
</body>
</html>
