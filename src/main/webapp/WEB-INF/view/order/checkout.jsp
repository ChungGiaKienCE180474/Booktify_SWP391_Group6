<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/cart.css" />
    <link rel="stylesheet" href="/css/order.css" />
    <title>Đặt hàng — Booktify</title>
</head>
<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">
        <div class="cart-wrap order-wrap">
            <div class="cart-head">
                <h1><i class="fa-solid fa-receipt"></i> Thanh toán đơn hàng</h1>
                <p>Điền thông tin nhận hàng và xác nhận đặt hàng</p>
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
                    <form:form modelAttribute="checkoutForm" action="/orders/checkout" method="post" cssClass="order-form">
                        <h2>Thông tin nhận hàng</h2>

                        <div class="order-field">
                            <label>Người nhận <span class="required">*</span></label>
                            <form:input path="recipientName" cssClass="order-input" placeholder="Họ và tên người nhận" />
                            <form:errors path="recipientName" cssClass="order-error" />
                        </div>

                        <div class="order-field">
                            <label>Số điện thoại <span class="required">*</span></label>
                            <form:input path="recipientPhone" cssClass="order-input" placeholder="VD: 0912345678" />
                            <form:errors path="recipientPhone" cssClass="order-error" />
                        </div>

                        <div class="order-field">
                            <label>Địa chỉ giao hàng <span class="required">*</span></label>
                            <form:textarea path="shippingAddress" cssClass="order-textarea" rows="3"
                                           placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành phố" />
                            <form:errors path="shippingAddress" cssClass="order-error" />
                        </div>

                        <div class="order-field order-field--info">
                            <label>Thanh toán</label>
                            <p class="order-static-value">
                                <i class="fa-solid fa-money-bill-wave"></i> ${paymentLabel}
                            </p>
                            <p class="order-static-hint">Phí vận chuyển: ${shippingFeeFormatted} &#8363;</p>
                        </div>

                        <div class="order-field">
                            <label>Mã voucher <span class="order-optional">(tùy chọn)</span></label>
                            <form:input path="voucherCode" cssClass="order-input" placeholder="Chưa hỗ trợ — để trống" />
                            <form:errors path="voucherCode" cssClass="order-error" />
                        </div>

                        <div class="order-field">
                            <label>Ghi chú</label>
                            <form:textarea path="note" cssClass="order-textarea" rows="2" placeholder="Ghi chú cho đơn hàng (tùy chọn)" />
                        </div>

                        <form:errors cssClass="order-error" element="div" />

                        <div class="order-actions">
                            <a href="/cart" class="cart-btn cart-btn--outline">
                                <i class="fa-solid fa-arrow-left"></i> Quay lại giỏ hàng
                            </a>
                            <button type="submit" class="cart-btn cart-btn--primary">
                                <i class="fa-solid fa-check"></i> Xác nhận đặt hàng
                            </button>
                        </div>
                    </form:form>
                </div>

                <aside class="cart-summary order-summary">
                    <h2>Đơn hàng của bạn</h2>
                    <c:forEach items="${cart.items}" var="item">
                        <div class="order-summary-item">
                            <div>
                                <strong>${item.bookTitle}</strong>
                                <div class="order-summary-meta">x${item.quantity}</div>
                            </div>
                            <span>${item.subtotalFormatted} &#8363;</span>
                        </div>
                    </c:forEach>
                    <div class="cart-summary-row">
                        <span>Tạm tính</span>
                        <span>${cart.totalAmountFormatted} &#8363;</span>
                    </div>
                    <div class="cart-summary-row">
                        <span>Phí vận chuyển</span>
                        <span>${shippingFeeFormatted} &#8363;</span>
                    </div>
                    <div class="cart-summary-row total">
                        <span>Tổng thanh toán</span>
                        <span>${checkoutTotalFormatted} &#8363;</span>
                    </div>
                </aside>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
</body>
</html>
