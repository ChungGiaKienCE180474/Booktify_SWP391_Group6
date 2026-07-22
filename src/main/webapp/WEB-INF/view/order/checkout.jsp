<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
                <link rel="stylesheet" href="/css/header.css" />
                <link rel="stylesheet" href="/css/footer.css" />
                <link rel="stylesheet" href="/css/cart.css" />
                <link rel="stylesheet" href="/css/order.css" />
                <link rel="stylesheet" href="/css/voucher.css" />

                <style>
                    .checkout-item-prices {
                        display: flex;
                        align-items: flex-end;
                        flex-direction: column;
                        gap: 3px;
                        text-align: right;
                    }

                    .checkout-old-price {
                        color: #94a3b8;
                        font-size: .75rem;
                        font-weight: 700;
                        text-decoration: line-through;
                    }

                    .checkout-discount-badge {
                        display: inline-flex;
                        margin-top: 3px;
                        padding: 2px 7px;
                        border-radius: 999px;
                        background: #fee2e2;
                        color: #dc2626;
                        font-size: .68rem;
                        font-weight: 800;
                    }

                    .checkout-sale-price {
                        color: #dc2626;
                        font-weight: 800;
                    }
                </style>

                <title>Checkout — Booktify</title>
            </head>

            <body class="home-page">
                <jsp:include page="/WEB-INF/view/layout/header.jsp" />

                <main class="main-content">
                    <div class="cart-wrap order-wrap">
                        <div class="cart-head">
                            <h1><i class="fa-solid fa-receipt"></i> Order checkout</h1>
                            <p>Fill in delivery information and confirm your order</p>
                        </div>

                        <c:if test="${not empty successMessage}">
                            <div class="cart-alert cart-alert--success">${successMessage}</div>
                        </c:if>
                        <c:if test="${not empty errorMessage}">
                            <div class="cart-alert cart-alert--error">${errorMessage}</div>
                        </c:if>
                        <c:if test="${not empty warningMessage}">
                            <div class="cart-alert cart-alert--warn">${warningMessage}</div>
                        </c:if>

                        <div class="order-layout">
                            <div class="order-form-panel">
                                <form:form modelAttribute="checkoutForm" action="/orders/checkout" method="post"
                                    cssClass="order-form">
                                    <h2>Delivery information</h2>

                                    <div class="order-field">
                                        <label>Recipient <span class="required">*</span></label>
                                        <form:input path="recipientName" cssClass="order-input"
                                            placeholder="Recipient full name" />
                                        <form:errors path="recipientName" cssClass="order-error" />
                                    </div>

                                    <div class="order-field">
                                        <label>Phone number <span class="required">*</span></label>
                                        <form:input path="recipientPhone" cssClass="order-input"
                                            placeholder="Example: 0912345678" />
                                        <form:errors path="recipientPhone" cssClass="order-error" />
                                    </div>

                                    <div class="order-field">
                                        <label>Shipping address <span class="required">*</span></label>
                                        <form:textarea path="shippingAddress" cssClass="order-textarea" rows="3"
                                            placeholder="House number, street, ward, district, province/city" />
                                        <form:errors path="shippingAddress" cssClass="order-error" />
                                    </div>

                                    <div class="order-field order-field--info">
                                        <label>Payment</label>
                                        <p class="order-static-value">
                                            <i class="fa-solid fa-money-bill-wave"></i> ${paymentLabel}
                                        </p>
                                    </div>

                                    <div class="order-field">
                                        <label>
                                            Voucher code
                                            <span class="order-optional">(optional)</span>
                                        </label>

                                        <div style="display:flex; gap:10px;">

                                            <form:input path="voucherCode" cssClass="order-input"
                                                placeholder="Enter voucher code" />

                                            <button type="submit" formaction="/orders/apply-voucher" formmethod="post"
                                                class="apply-voucher-btn" name="action" value="apply">
                                                Apply
                                            </button>
                                            </div>

                                        <form:errors path="voucherCode" cssClass="order-error" />

                                    </div>
                                    <div class="order-field">
                                        <label>Note</label>
                                        <form:textarea path="note" cssClass="order-textarea" rows="2"
                                            placeholder="Note for your order (optional)" />
                                    </div>

                                    <form:errors cssClass="order-error" element="div" />

                                    <div class="order-actions">
                                        <a href="/cart" class="cart-btn cart-btn--outline">
                                            <i class="fa-solid fa-arrow-left"></i> Back to cart
                                        </a>
                                        <button type="submit" class="cart-btn cart-btn--primary">
                                            <i class="fa-solid fa-check"></i> Confirm order
                                        </button>
                                    </div>
                                </form:form>
                            </div>

                            <aside class="cart-summary order-summary">
                                <h2>Your order</h2>
                                <c:forEach items="${cart.items}" var="item">
                                    <div class="order-summary-item">
                                        <div>
                                            <strong>${item.bookTitle}</strong>
                                            <div class="order-summary-meta">
                                                x${item.quantity}

                                                <c:if test="${item.promotionApplied}">
                                                    <span class="checkout-discount-badge">
                                                        ${item.promotionLabel}
                                                    </span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <c:choose>
                                            <c:when test="${item.promotionApplied}">
                                                <div class="checkout-item-prices">
                                                    <span class="checkout-old-price">
                                                        ${item.originalSubtotalFormatted} &#8363;
                                                    </span>
                                                    <span class="checkout-sale-price">
                                                        ${item.subtotalFormatted} &#8363;
                                                    </span>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span>${item.subtotalFormatted} &#8363;</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:forEach>
                                <div class="cart-summary-row">
                                    <span>Subtotal</span>
                                    <span>${cart.totalAmountFormatted} &#8363;</span>
                                </div>

                                <div class="cart-summary-row">
                                    <span>Voucher</span>
                                    <span style="color:#dc2626;font-weight:700;">
                                        -
                                        <c:choose>
                                            <c:when test="${not empty discountAmountFormatted}">
                                                ${discountAmountFormatted}
                                            </c:when>
                                            <c:otherwise>
                                                0
                                            </c:otherwise>
                                        </c:choose>
                                        &#8363;
                                    </span>
                                </div>

                                <div class="cart-summary-row total">
                                    <span>Total</span>
                                    <span>${checkoutTotalFormatted} &#8363;</span>
                                </div>
                            </aside>
                        </div>
                    </div>
                </main>

                <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
            </body>

            </html>