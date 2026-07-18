<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/cart.css" />
    <link rel="stylesheet" href="/css/order.css" />
    <title>Order details ${order.orderCode} — Booktify</title>
</head>
<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">
        <div class="cart-wrap order-wrap">
            <div class="cart-head order-detail-head">
                <div>
                    <h1><i class="fa-solid fa-receipt"></i> ${order.orderCode}</h1>
                    <p>Placed on ${order.createdAtFormatted}</p>
                </div>
                <span class="order-status order-status--${order.status}">${order.statusLabel}</span>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="cart-alert cart-alert--success">${successMessage}</div>
            </c:if>

            <div class="order-detail-grid">
                <section class="order-panel">
                    <h2>Products</h2>
                    <table class="cart-table">
                        <thead>
                            <tr>
                                <th>Book</th>
                                <th>Unit price</th>
                                <th>SL</th>
                                <th>Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${order.items}" var="item">
                                <tr>
                                    <td>
                                        <a href="/books/${item.bookId}" class="cart-book__title">${item.bookTitle}</a>
                                    </td>
                                    <td>${item.unitPriceFormatted} &#8363;</td>
                                    <td>${item.quantity}</td>
                                    <td>${item.lineTotalFormatted} &#8363;</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </section>

                <aside class="cart-summary order-summary">
                    <h2>Shipping information</h2>
                    <dl class="order-info-list">
                        <div><dt>Recipient</dt><dd>${order.recipientName}</dd></div>
                        <div><dt>Phone</dt><dd>${order.recipientPhone}</dd></div>
                        <div><dt>Address</dt><dd>${order.shippingAddress}</dd></div>
                        <div><dt>Payment</dt><dd>${order.paymentMethodLabel}</dd></div>
                    </dl>

                    <h2>Payment summary</h2>
                    <div class="cart-summary-row"><span>Subtotal</span><span>${order.subtotalFormatted} &#8363;</span></div>
                    <div class="cart-summary-row"><span>Discount</span><span>-${order.discountAmountFormatted} &#8363;</span></div>
                    <div class="cart-summary-row"><span>Shipping fee</span><span>${order.shippingFeeFormatted} &#8363;</span></div>
                    <div class="cart-summary-row total"><span>Total</span><span>${order.totalAmountFormatted} &#8363;</span></div>

                    <c:if test="${not empty order.note}">
                        <p class="order-note"><strong>Note:</strong> ${order.note}</p>
                    </c:if>

                    <a href="/orders" class="cart-btn cart-btn--outline" style="width:100%;margin-top:1rem;">
                        <i class="fa-solid fa-arrow-left"></i> Back to list
                    </a>
                </aside>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
</body>
</html>
