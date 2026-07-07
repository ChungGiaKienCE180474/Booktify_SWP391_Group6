<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title>Lịch sử đơn hàng — Booktify</title>
</head>
<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">
        <div class="cart-wrap order-wrap">
            <div class="cart-head">
                <h1><i class="fa-solid fa-box"></i> Đơn hàng của tôi</h1>
                <p>Theo dõi trạng thái các đơn hàng đã đặt</p>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="cart-alert cart-alert--success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="cart-alert cart-alert--error">${errorMessage}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty orders}">
                    <div class="cart-empty">
                        <i class="fa-solid fa-box-open"></i>
                        <h2>Chưa có đơn hàng</h2>
                        <p style="color:var(--cart-muted);">Hãy mua sách và đặt hàng để xem lịch sử tại đây.</p>
                        <a href="/books" class="cart-btn cart-btn--primary" style="margin-top:1rem;">
                            <i class="fa-solid fa-book"></i> Khám phá sách
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="order-list">
                        <c:forEach items="${orders}" var="order">
                            <a href="/orders/${order.id}" class="order-card">
                                <div class="order-card__top">
                                    <strong>${order.orderCode}</strong>
                                    <span class="order-status order-status--${order.status}">${order.statusLabel}</span>
                                </div>
                                <div class="order-card__meta">
                                    <span><i class="fa-regular fa-clock"></i> ${order.createdAtFormatted}</span>
                                    <span><i class="fa-solid fa-user"></i> ${order.recipientName}</span>
                                </div>
                                <div class="order-card__total">
                                    Tổng tiền: <strong>${order.totalAmountFormatted} &#8363;</strong>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
</body>
</html>
