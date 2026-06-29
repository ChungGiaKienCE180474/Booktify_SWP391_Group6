<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/cart.css" />
    <title>Giỏ hàng — Booktify</title>
</head>
<body class="home-page">

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">
        <div class="cart-wrap">
            <div class="cart-head">
                <h1><i class="fa-solid fa-cart-shopping"></i> Giỏ hàng của bạn</h1>
                <p>Quản lý sách trước khi đặt hàng</p>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="cart-alert cart-alert--success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="cart-alert cart-alert--error">${errorMessage}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty cart.items}">
                    <div class="cart-empty">
                        <i class="fa-solid fa-cart-arrow-down"></i>
                        <h2>Giỏ hàng trống</h2>
                        <p style="color:var(--cart-muted);">Hãy khám phá sách và thêm vào giỏ hàng nhé!</p>
                        <a href="/books" class="cart-btn cart-btn--primary" style="margin-top:1rem;">
                            <i class="fa-solid fa-book"></i> Xem sách
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="cart-layout">
                        <div class="cart-table-wrap">
                            <table class="cart-table">
                                <thead>
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th>Đơn giá</th>
                                        <th>Số lượng</th>
                                        <th>Thành tiền</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${cart.items}" var="item">
                                        <tr>
                                            <td>
                                                <div class="cart-book">
                                                    <div class="cart-book__thumb">
                                                        <c:choose>
                                                            <c:when test="${not empty item.bookImageUrl}">
                                                                <img src="${item.bookImageUrl}" alt="${item.bookTitle}" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="fa-solid fa-book" style="color:#94a3b8;"></i>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div>
                                                        <a href="/books/${item.bookId}" class="cart-book__title">${item.bookTitle}</a>
                                                        <div class="cart-book__author">${item.bookAuthor}</div>
                                                        <c:if test="${!item.bookActive}">
                                                            <small style="color:#dc2626;">Ngừng kinh doanh</small>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <span class="cart-price">
                                                    ${item.bookPriceFormatted} &#8363;
                                                </span>
                                            </td>
                                            <td>
                                                <form class="cart-qty-form" method="post" action="/cart/update">
                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                    <input type="hidden" name="itemId" value="${item.id}" />
                                                    <input type="number" name="quantity" value="${item.quantity}"
                                                           min="1" max="${item.bookStockQuantity}" required />
                                                    <button type="submit" class="cart-btn cart-btn--outline">Cập nhật</button>
                                                </form>
                                            </td>
                                            <td>
                                                <span class="cart-price">
                                                    ${item.subtotalFormatted} &#8363;
                                                </span>
                                            </td>
                                            <td>
                                                <form method="post" action="/cart/remove">
                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                    <input type="hidden" name="itemId" value="${item.id}" />
                                                    <button type="submit" class="cart-btn cart-btn--ghost" title="Xóa">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <aside class="cart-summary">
                            <h2>Tóm tắt đơn hàng</h2>
                            <div class="cart-summary-row">
                                <span>Số sản phẩm</span>
                                <span>${cartItemCount}</span>
                            </div>
                            <div class="cart-summary-row total">
                                <span>Tổng tiền</span>
                                <span>
                                    ${cart.totalAmountFormatted} &#8363;
                                </span>
                            </div>
                            <div class="cart-summary-actions">
                                <a href="/books" class="cart-btn cart-btn--outline">
                                    <i class="fa-solid fa-arrow-left"></i> Tiếp tục mua sách
                                </a>
                                <form method="post" action="/cart/validate">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <button type="submit" class="cart-btn cart-btn--primary" style="width:100%;">
                                        <i class="fa-solid fa-credit-card"></i> Kiểm tra &amp; thanh toán
                                    </button>
                                </form>
                            </div>
                        </aside>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

</body>
</html>
