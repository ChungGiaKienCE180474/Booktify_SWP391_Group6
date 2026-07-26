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
            <c:if test="${not empty errorMessage}">
                <div class="cart-alert cart-alert--error">${errorMessage}</div>
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
                                            <div class="cart-book__info">
                                                <a href="/books/${item.bookId}" class="cart-book__title">${item.bookTitle}</a>
                                            </div>
                                        </div>
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
                    <div class="cart-summary-row total"><span>Total</span><span>${order.totalAmountFormatted} &#8363;</span></div>

                    <c:if test="${not empty order.note}">
                        <p class="order-note"><strong>Note:</strong> ${order.note}</p>
                    </c:if>

                    <a href="/orders" class="cart-btn cart-btn--outline" style="width:100%;margin-top:1rem;">
                        <i class="fa-solid fa-arrow-left"></i> Back to list
                    </a>

                    <c:if test="${canCancelOrder}">
                        <button type="button"
                                id="openCancelModalBtn"
                                class="cart-btn order-btn--cancel"
                                style="width:100%;margin-top:.75rem;">
                            <i class="fa-solid fa-ban"></i> Cancel order
                        </button>
                    </c:if>
                </aside>
            </div>
        </div>
    </main>

    <c:if test="${canCancelOrder}">
        <div id="cancelOrderModal" class="order-modal" aria-hidden="true">
            <div class="order-modal__backdrop" data-close-modal></div>
            <div class="order-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="cancelOrderTitle">
                <div class="order-modal__header">
                    <h2 id="cancelOrderTitle"><i class="fa-solid fa-triangle-exclamation"></i> Cancel order</h2>
                    <button type="button" class="order-modal__close" data-close-modal aria-label="Close">
                        <i class="fa-solid fa-xmark"></i>
                    </button>
                </div>
                <div class="order-modal__body">
                    <p>Are you sure you want to cancel order <strong>${order.orderCode}</strong>?</p>
                    <p class="order-modal__hint">This action cannot be undone. Product stock will be restored.</p>
                </div>
                <div class="order-modal__actions">
                    <button type="button" class="cart-btn cart-btn--outline" data-close-modal>
                        Keep order
                    </button>
                    <form action="/orders/${order.id}/cancel" method="post" style="margin:0;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <button type="submit" class="cart-btn order-btn--cancel">
                            <i class="fa-solid fa-ban"></i> Yes, cancel order
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            (function () {
                var modal = document.getElementById('cancelOrderModal');
                var openBtn = document.getElementById('openCancelModalBtn');
                if (!modal || !openBtn) {
                    return;
                }

                function openModal() {
                    modal.classList.add('order-modal--open');
                    modal.setAttribute('aria-hidden', 'false');
                }

                function closeModal() {
                    modal.classList.remove('order-modal--open');
                    modal.setAttribute('aria-hidden', 'true');
                }

                openBtn.addEventListener('click', openModal);

                modal.querySelectorAll('[data-close-modal]').forEach(function (el) {
                    el.addEventListener('click', closeModal);
                });

                document.addEventListener('keydown', function (event) {
                    if (event.key === 'Escape' && modal.classList.contains('order-modal--open')) {
                        closeModal();
                    }
                });
            })();
        </script>
    </c:if>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
</body>
</html>
