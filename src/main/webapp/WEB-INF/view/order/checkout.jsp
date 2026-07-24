<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib
    prefix="c"
    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib
    prefix="form"
    uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0" />

    <link
        rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <link
        rel="stylesheet"
        href="/css/header.css" />

    <link
        rel="stylesheet"
        href="/css/footer.css" />

    <link
        rel="stylesheet"
        href="/css/cart.css" />

    <link
        rel="stylesheet"
        href="/css/order.css?v=6" />

    <link
        rel="stylesheet"
        href="/css/voucher.css" />

    <link
        rel="stylesheet"
        href="/css/checkout-promotion.css?v=6" />

    <title>Checkout — Booktify</title>
</head>

<body class="home-page">

<jsp:include
    page="/WEB-INF/view/layout/header.jsp" />

<main class="main-content">

    <div class="cart-wrap order-wrap">

        <%-- PAGE HEADER --%>

        <div class="cart-head">

            <h1>
                <i class="fa-solid fa-receipt"></i>
                Order checkout
            </h1>

            <p>
                Fill in delivery information and
                confirm your order
            </p>

        </div>

        <%-- MESSAGES --%>

        <c:if test="${not empty successMessage}">
            <div class="cart-alert cart-alert--success">
                <c:out value="${successMessage}" />
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="cart-alert cart-alert--error">
                <c:out value="${errorMessage}" />
            </div>
        </c:if>

        <c:if test="${not empty warningMessage}">
            <div class="cart-alert cart-alert--warn">
                <c:out value="${warningMessage}" />
            </div>
        </c:if>

        <%-- CHECKOUT FORM --%>

        <form:form
            id="checkoutForm"
            modelAttribute="checkoutForm"
            action="/orders/checkout"
            method="post"
            cssClass="order-form">

            <div class="order-layout">

                <%-- ================================= --%>
                <%-- LEFT: DELIVERY INFORMATION        --%>
                <%-- ================================= --%>

                <section class="order-form-panel">

                    <h2>Delivery information</h2>

                    <%-- Recipient --%>

                    <div class="order-field">

                        <label for="recipientName">
                            Recipient
                            <span class="required">*</span>
                        </label>

                        <form:input
                            id="recipientName"
                            path="recipientName"
                            cssClass="order-input"
                            placeholder="Recipient full name" />

                        <form:errors
                            path="recipientName"
                            cssClass="order-error" />

                    </div>

                    <%-- Phone number --%>

                    <div class="order-field">

                        <label for="recipientPhone">
                            Phone number
                            <span class="required">*</span>
                        </label>

                        <form:input
                            id="recipientPhone"
                            path="recipientPhone"
                            cssClass="order-input"
                            placeholder="Example: 0912345678" />

                        <form:errors
                            path="recipientPhone"
                            cssClass="order-error" />

                    </div>

                    <%-- Shipping address --%>

                    <div class="order-field">

                        <label for="shippingAddress">
                            Shipping address
                            <span class="required">*</span>
                        </label>

                        <form:textarea
                            id="shippingAddress"
                            path="shippingAddress"
                            cssClass="order-textarea"
                            rows="3"
                            placeholder="House number, street, ward, district, province/city" />

                        <form:errors
                            path="shippingAddress"
                            cssClass="order-error" />

                    </div>

                    <%-- Payment --%>

                    <div class="order-field order-field--info">

                        <label>Payment</label>

                        <p class="order-static-value">
                            <i class="fa-solid fa-money-bill-wave"></i>
                            ${paymentLabel}
                        </p>

                    </div>

                    <%-- Voucher --%>

                    <div class="order-field">

                        <label for="voucherCode">
                            Voucher code

                            <span class="order-optional">
                                (optional)
                            </span>
                        </label>

                        <div class="voucher-input-row">

                            <form:input
                                id="voucherCode"
                                path="voucherCode"
                                cssClass="order-input"
                                placeholder="Enter voucher code" />

                            <button
                                type="submit"
                                formaction="/orders/apply-voucher"
                                formmethod="post"
                                class="apply-voucher-btn"
                                name="action"
                                value="apply">

                                Apply
                            </button>

                        </div>

                        <form:errors
                            path="voucherCode"
                            cssClass="order-error" />

                    </div>

                    <%-- Note --%>

                    <div class="order-field">

                        <label for="note">
                            Note
                        </label>

                        <form:textarea
                            id="note"
                            path="note"
                            cssClass="order-textarea"
                            rows="2"
                            placeholder="Note for your order (optional)" />

                        <form:errors
                            path="note"
                            cssClass="order-error" />

                    </div>

                    <form:errors
                        cssClass="order-error"
                        element="div" />

                    <%-- Actions --%>

                    <div class="order-actions">

                        <a
                            href="/cart"
                            class="cart-btn cart-btn--outline">

                            <i class="fa-solid fa-arrow-left"></i>
                            Back to cart
                        </a>

                        <button
                            type="submit"
                            class="cart-btn cart-btn--primary">

                            <i class="fa-solid fa-check"></i>
                            Confirm order
                        </button>

                    </div>

                </section>

                <%-- ================================= --%>
                <%-- RIGHT: ORDER SUMMARY              --%>
                <%-- ================================= --%>

                <aside class="cart-summary order-summary compact-order-summary">

                    <%-- Summary header --%>

                    <div class="compact-summary-header">

                        <div>

                            <h2>Your order</h2>

                            <span>
                                ${cart.items.size()} products
                            </span>

                        </div>

                        <i class="fa-solid fa-bag-shopping"></i>

                    </div>

                    <%-- Totals --%>

                    <div class="compact-total-box">

                        <div class="compact-total-row">

                            <span>
                                Original subtotal
                            </span>

                            <strong id="originalSubtotalDisplay">
                                ${originalSubtotalFormatted}
                                &#8363;
                            </strong>

                        </div>

                        <div class="compact-total-row compact-discount-row">

                            <span>
                                Promotion discount
                            </span>

                            <strong id="promotionDiscountDisplay">
                                -${promotionDiscountFormatted}
                                &#8363;
                            </strong>

                        </div>

                        <div class="compact-total-row compact-discount-row">

                            <span>Voucher</span>

                            <strong id="voucherDiscountDisplay">
                                -${discountAmountFormatted}
                                &#8363;
                            </strong>

                        </div>

                        <div class="compact-total-row compact-final-total">

                            <span>Total</span>

                            <strong id="checkoutTotalDisplay">
                                ${checkoutTotalFormatted}
                                &#8363;
                            </strong>

                        </div>

                    </div>

                </aside>

            </div>

        </form:form>

    </div>

</main>

<jsp:include
    page="/WEB-INF/view/layout/footer.jsp" />

</body>
</html>






:root {
    --cart-primary: #006B5E;
    --cart-text: #1a1a2e;
    --cart-muted: #64748b;
    --cart-border: #e2e8f0;
    --cart-white: #ffffff;
    --cart-radius: 12px;
    --cart-shadow: 0 4px 24px rgba(0, 107, 94, 0.08);
}

