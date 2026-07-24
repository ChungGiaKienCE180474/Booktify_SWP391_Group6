<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8"/>
    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"/>

    <link
        rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"/>

    <link rel="stylesheet" href="/css/header.css"/>
    <link rel="stylesheet" href="/css/footer.css"/>
    <link rel="stylesheet" href="/css/cart.css"/>
    <link rel="stylesheet" href="/css/voucher.css"/>

    <title>Cart — Booktify</title>
</head>

<body class="home-page">

<jsp:include page="/WEB-INF/view/layout/header.jsp"/>

<main class="main-content">

    <div class="cart-wrap">

        <div class="cart-head">
            <h1>
                <i class="fa-solid fa-cart-shopping"></i>
                Your cart
            </h1>

            <p>Manage your books before checkout</p>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="cart-alert cart-alert--success">
                <c:out value="${successMessage}"/>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="cart-alert cart-alert--error">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <c:if test="${not empty warningMessage}">
            <div class="cart-alert cart-alert--warn">
                <c:out value="${warningMessage}"/>
            </div>
        </c:if>

        <c:choose>

            <%-- =============================== --%>
            <%-- EMPTY CART                      --%>
            <%-- =============================== --%>

            <c:when test="${empty cart.items}">

                <div class="cart-empty">

                    <i class="fa-solid fa-cart-arrow-down"></i>

                    <h2>Your cart is empty</h2>

                    <p style="color:var(--cart-muted);">
                        Explore books and add them to your cart!
                    </p>

                    <a
                        href="/books"
                        class="cart-btn cart-btn--primary"
                        style="margin-top:1rem;">

                        <i class="fa-solid fa-book"></i>
                        Browse books
                    </a>

                </div>

            </c:when>

            <%-- =============================== --%>
            <%-- CART WITH PRODUCTS              --%>
            <%-- =============================== --%>

            <c:otherwise>

                <div class="cart-layout">

                    <%-- =============================== --%>
                    <%-- LEFT: CART TABLE                --%>
                    <%-- =============================== --%>

                    <div class="cart-table-wrap">

                        <table class="cart-table">

                            <colgroup>
                                <col class="col-product"/>
                                <col class="col-price"/>
                                <col class="col-qty"/>
                                <col class="col-promotion"/>
                                <col class="col-subtotal"/>
                                <col class="col-remove"/>
                            </colgroup>

                            <thead>
                            <tr>
                                <th class="col-product">
                                    Product
                                </th>

                                <th class="col-price">
                                    Unit price
                                </th>

                                <th class="col-qty">
                                    Quantity
                                </th>

                                <th class="col-promotion">
                                    Promotion
                                </th>

                                <th class="col-subtotal">
                                    Subtotal
                                </th>

                                <th
                                    class="col-remove"
                                    aria-label="Remove">
                                </th>
                            </tr>
                            </thead>

                            <tbody>

                            <c:forEach
                                items="${cart.items}"
                                var="item">

                                <tr>

                                    <%-- =============================== --%>
                                    <%-- PRODUCT                         --%>
                                    <%-- =============================== --%>

                                    <td class="col-product">

                                        <div class="cart-book">

                                            <div class="cart-book__thumb">

                                                <c:choose>

                                                    <c:when test="${not empty item.bookImageUrl}">

                                                        <img
                                                            src="${item.bookImageUrl}"
                                                            alt="${item.bookTitle}"/>

                                                    </c:when>

                                                    <c:otherwise>

                                                        <i
                                                            class="fa-solid fa-book"
                                                            style="color:#94a3b8;">
                                                        </i>

                                                    </c:otherwise>

                                                </c:choose>

                                            </div>

                                            <div class="cart-book__info">

                                                <a
                                                    href="/books/${item.bookId}"
                                                    class="cart-book__title">

                                                    <c:out value="${item.bookTitle}"/>
                                                </a>

                                                <div class="cart-book__author">
                                                    <c:out value="${item.bookAuthor}"/>
                                                </div>

                                                <c:if test="${!item.bookActive}">
                                                    <small style="color:#dc2626;">
                                                        Discontinued
                                                    </small>
                                                </c:if>

                                            </div>

                                        </div>

                                    </td>

                                    <%-- =============================== --%>
                                    <%-- UNIT PRICE                      --%>
                                    <%-- =============================== --%>

                                    <td class="col-price">

                                        <c:choose>

                                            <c:when test="${
                                                selectedPromotionMap[item.bookId] != null
                                                && selectedPromotionMap[item.bookId] != 'NONE'
                                            }">

                                                <div class="cart-promotion-price">

                                                    <span class="cart-old-price">
                                                        ${item.originalPriceFormatted}
                                                        &#8363;
                                                    </span>

                                                    <span class="cart-sale-price">
                                                        ${selectedUnitPriceMap[item.id]}
                                                        &#8363;
                                                    </span>

                                                </div>

                                            </c:when>

                                            <c:otherwise>

                                                <span class="cart-price">
                                                    ${item.bookPriceFormatted}
                                                    &#8363;
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <%-- =============================== --%>
                                    <%-- QUANTITY                        --%>
                                    <%-- =============================== --%>

                                    <td class="col-qty">

                                        <form
                                            class="cart-qty-form"
                                            method="post"
                                            action="/cart/update">

                                            <input
                                                type="hidden"
                                                name="${_csrf.parameterName}"
                                                value="${_csrf.token}"/>

                                            <input
                                                type="hidden"
                                                name="itemId"
                                                value="${item.id}"/>

                                            <input
                                                type="number"
                                                name="quantity"
                                                value="${item.quantity}"
                                                min="1"
                                                max="${item.bookStockQuantity}"
                                                step="1"
                                                required/>

                                            <button
                                                type="submit"
                                                class="cart-btn cart-btn--outline cart-btn--icon"
                                                title="Update quantity"
                                                aria-label="Update quantity">

                                                <i class="fa-solid fa-check"></i>

                                            </button>

                                        </form>

                                    </td>

                                    <%-- =============================== --%>
                                    <%-- PROMOTION                       --%>
                                    <%-- =============================== --%>

                                    <td class="col-promotion">

                                        <c:set
                                            var="percentagePromotion"
                                            value="${percentagePromotionMap[item.bookId]}"/>

                                        <c:set
                                            var="fixedPromotion"
                                            value="${fixedPromotionMap[item.bookId]}"/>

                                        <c:choose>

                                            <c:when test="${
                                                not empty percentagePromotion
                                                or not empty fixedPromotion
                                            }">

                                                <form
                                                    method="post"
                                                    action="/cart/promotion"
                                                    class="cart-promotion-form">

                                                    <input
                                                        type="hidden"
                                                        name="${_csrf.parameterName}"
                                                        value="${_csrf.token}"/>

                                                    <input
                                                        type="hidden"
                                                        name="bookId"
                                                        value="${item.bookId}"/>

                                                    <select
                                                        name="selection"
                                                        class="cart-promotion-select"
                                                        aria-label="Select promotion for ${item.bookTitle}"
                                                        onchange="this.form.submit();">

                                                        <option
                                                            value="NONE"
                                                            ${
                                                                empty selectedPromotionMap[item.bookId]
                                                                || selectedPromotionMap[item.bookId] == 'NONE'
                                                                ? 'selected'
                                                                : ''
                                                            }>

                                                            No promotion
                                                        </option>

                                                        <c:if test="${not empty percentagePromotion}">

                                                            <option
                                                                value="PERCENTAGE"
                                                                ${
                                                                    selectedPromotionMap[item.bookId] == 'PERCENTAGE'
                                                                    ? 'selected'
                                                                    : ''
                                                                }>

                                                                Save
                                                                ${percentagePromotion.discountValue}%
                                                            </option>

                                                        </c:if>

                                                        <c:if test="${not empty fixedPromotion}">

                                                            <option
                                                                value="FIXED"
                                                                ${
                                                                    selectedPromotionMap[item.bookId] == 'FIXED'
                                                                    ? 'selected'
                                                                    : ''
                                                                }>

                                                                Save

                                                                <fmt:formatNumber
                                                                    value="${fixedPromotion.discountValue}"
                                                                    type="number"
                                                                    groupingUsed="true"/>

                                                                &#8363;
                                                            </option>

                                                        </c:if>

                                                    </select>

                                                </form>

                                                <small class="promotion-saving">
                                                    Applied to this product only
                                                </small>

                                            </c:when>

                                            <c:otherwise>

                                                <span class="no-promotion">
                                                    No promotion
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <%-- =============================== --%>
                                    <%-- SUBTOTAL                        --%>
                                    <%-- =============================== --%>

                                    <td class="col-subtotal">

                                        <c:choose>

                                            <c:when test="${
                                                selectedPromotionMap[item.bookId] != null
                                                && selectedPromotionMap[item.bookId] != 'NONE'
                                            }">

                                                <div class="cart-promotion-price">

                                                    <span class="cart-old-price">
                                                        ${item.originalSubtotalFormatted}
                                                        &#8363;
                                                    </span>

                                                    <span class="cart-sale-price">
                                                        ${selectedSubtotalMap[item.id]}
                                                        &#8363;
                                                    </span>

                                                </div>

                                            </c:when>

                                            <c:otherwise>

                                                <span class="cart-price">
                                                    ${item.subtotalFormatted}
                                                    &#8363;
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <%-- =============================== --%>
                                    <%-- REMOVE                          --%>
                                    <%-- =============================== --%>

                                    <td class="col-remove">

                                        <form
                                            method="post"
                                            action="/cart/remove">

                                            <input
                                                type="hidden"
                                                name="${_csrf.parameterName}"
                                                value="${_csrf.token}"/>

                                            <input
                                                type="hidden"
                                                name="itemId"
                                                value="${item.id}"/>

                                            <button
                                                type="submit"
                                                class="cart-btn cart-btn--ghost cart-btn--remove"
                                                title="Remove product"
                                                aria-label="Remove product">

                                                <i class="fa-solid fa-trash"></i>

                                            </button>

                                        </form>

                                    </td>

                                </tr>

                            </c:forEach>

                            </tbody>

                        </table>

                    </div>

                    <%-- =============================== --%>
                    <%-- RIGHT: CART SUMMARY             --%>
                    <%-- =============================== --%>

                    <aside class="cart-summary">

                        <%-- =============================== --%>
                        <%-- AVAILABLE VOUCHERS              --%>
                        <%-- =============================== --%>

                        <div class="voucher-box">

                            <h2>
                                <i class="fa-solid fa-ticket"></i>
                                Available vouchers
                            </h2>

                            <c:choose>

                                <c:when test="${empty activeVouchers}">

                                    <p class="voucher-empty">
                                        No vouchers available.
                                    </p>

                                </c:when>

                                <c:otherwise>

                                    <div class="voucher-list-scroll">

                                        <c:forEach
                                            items="${activeVouchers}"
                                            var="voucher">

                                            <div class="voucher-card">

                                                <div class="voucher-description">
                                                    <strong>
                                                        🎟
                                                        <c:out value="${voucher.voucherName}"/>
                                                    </strong>
                                                </div>

                                                <div class="voucher-info">
                                                    <span>Code:</span>

                                                    <b>
                                                        <c:out value="${voucher.voucherCode}"/>
                                                    </b>
                                                </div>

                                                <div class="voucher-info">

                                                    Orders from:

                                                    <b>
                                                        <fmt:formatNumber
                                                            value="${voucher.minOrderAmount}"
                                                            type="number"
                                                            groupingUsed="true"/>

                                                        &#8363;
                                                    </b>

                                                </div>

                                                <div class="voucher-info">

                                                    Expiry:

                                                    <b>
                                                        <c:out value="${voucher.endDateString}"/>
                                                    </b>

                                                </div>

                                                <div class="voucher-actions">

                                                    <button
                                                        type="button"
                                                        class="copy-voucher-btn"
                                                        onclick="copyVoucher(
                                                            '${voucher.voucherCode}'
                                                        )">

                                                        <i class="fa-solid fa-copy"></i>
                                                        Copy
                                                    </button>

                                                    <button
                                                        type="button"
                                                        class="detail-voucher-btn"
                                                        onclick="showVoucherDetail(
                                                            '${voucher.voucherName}',
                                                            '${voucher.minOrderAmount}',
                                                            '${voucher.voucherCode}',
                                                            '${voucher.startDateString}',
                                                            '${voucher.endDateString}',
                                                            '${voucher.description}'
                                                        )">

                                                        <i class="fa-solid fa-circle-info"></i>
                                                        View details
                                                    </button>

                                                </div>

                                            </div>

                                        </c:forEach>

                                    </div>

                                </c:otherwise>

                            </c:choose>

                        </div>

                        <%-- =============================== --%>
                        <%-- ORDER SUMMARY                   --%>
                        <%-- =============================== --%>

                        <h2>Order summary</h2>

                        <div class="cart-summary-row">

                            <span>Items</span>

                            <span>
                                ${cartItemCount}
                            </span>

                        </div>

                        <div class="cart-summary-row">

                            <span>Original subtotal</span>

                            <span>
                                ${originalSubtotalFormatted}
                                &#8363;
                            </span>

                        </div>

                        <div class="cart-summary-row cart-summary-discount">

                            <span>Promotion discount</span>

                            <span>
                                -${promotionDiscountFormatted}
                                &#8363;
                            </span>

                        </div>

                        <div class="cart-summary-row total">

                            <span>Total</span>

                            <span>
                                ${promotionSubtotalFormatted}
                                &#8363;
                            </span>

                        </div>

                        <div class="cart-summary-actions">

                            <a
                                href="/books"
                                class="cart-btn cart-btn--outline">

                                <i class="fa-solid fa-arrow-left"></i>
                                Continue shopping
                            </a>

                            <form
                                method="post"
                                action="/cart/clear"
                                onsubmit="return confirm(
                                    'Are you sure you want to clear your entire cart?'
                                );">

                                <input
                                    type="hidden"
                                    name="${_csrf.parameterName}"
                                    value="${_csrf.token}"/>

                                <button
                                    type="submit"
                                    class="cart-btn cart-btn--ghost"
                                    style="width:100%;">

                                    <i class="fa-solid fa-trash-can"></i>
                                    Clear entire cart
                                </button>

                            </form>

                            <form
                                method="post"
                                action="/cart/validate">

                                <input
                                    type="hidden"
                                    name="${_csrf.parameterName}"
                                    value="${_csrf.token}"/>

                                <button
                                    type="submit"
                                    class="cart-btn cart-btn--primary"
                                    style="width:100%;">

                                    <i class="fa-solid fa-credit-card"></i>
                                    Proceed to checkout
                                </button>

                            </form>

                        </div>

                    </aside>

                </div>

            </c:otherwise>

        </c:choose>

    </div>

</main>

<jsp:include page="/WEB-INF/view/layout/footer.jsp"/>

<%-- =============================== --%>
<%-- VOUCHER DETAIL MODAL            --%>
<%-- =============================== --%>

<div
    id="voucherModal"
    class="voucher-modal">

    <div class="voucher-modal-content">

        <button
            type="button"
            class="voucher-close"
            onclick="closeVoucherDetail()">

            <i class="fa-solid fa-xmark"></i>
        </button>

        <h2>
            <i class="fa-solid fa-ticket"></i>
            <span id="modalVoucherName"></span>
        </h2>

        <div class="voucher-detail-item">
            <b>Minimum order:</b>
            <span id="modalMinOrder"></span>
        </div>

        <div class="voucher-detail-item">
            <b>Discount code:</b>
            <span id="modalCode"></span>
        </div>

        <div class="voucher-detail-item">
            <b>Valid period:</b>
            <span id="modalDate"></span>
        </div>

        <div class="voucher-detail-item">
            <b>Details:</b>
            <p id="modalDescription"></p>
        </div>

    </div>

</div>

<script>
    function copyVoucher(code) {
        navigator.clipboard
            .writeText(code)
            .then(function () {
                alert(
                    "Voucher code copied: " + code
                );
            });
    }

    function showVoucherDetail(
        name,
        minOrder,
        code,
        startDate,
        endDate,
        description
    ) {
        document.getElementById(
            "modalVoucherName"
        ).innerText = name;

        document.getElementById(
            "modalMinOrder"
        ).innerText =
            Number(minOrder)
                .toLocaleString("vi-VN")
            + " ₫";

        document.getElementById(
            "modalCode"
        ).innerText = code;

        document.getElementById(
            "modalDate"
        ).innerText =
            startDate + " - " + endDate;

        document.getElementById(
            "modalDescription"
        ).innerText =
            description || "No description";

        document.getElementById(
            "voucherModal"
        ).classList.add(
            "active"
        );
    }

    function closeVoucherDetail() {
        document.getElementById(
            "voucherModal"
        ).classList.remove(
            "active"
        );
    }
</script>

</body>
</html>