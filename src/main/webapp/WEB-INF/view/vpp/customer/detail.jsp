<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />
                <title>
                    <c:out value="${item.name}" /> - Booktify
                </title>

                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <link rel="stylesheet" href="/css/rating.css" />
                <style>
                    body {
                        margin: 0;
                        background: #F5F5FA;
                        color: #111827;
                    }

                    .vpp-detail-page {
                        max-width: 1400px;
                        margin: 0 auto;
                        padding: 28px 60px 50px;
                    }

                    .vpp-alert {
                        margin-bottom: 18px;
                        padding: 14px 18px;
                        border-radius: 10px;
                        font-weight: 800;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }

                    .vpp-alert-success {
                        background: #ECFDF5;
                        color: #047857;
                        border: 1px solid #A7F3D0;
                    }

                    .vpp-alert-error {
                        background: #FEF2F2;
                        color: #DC2626;
                        border: 1px solid #FECACA;
                    }

                    .vpp-breadcrumb {
                        color: #8A8A8A;
                        margin-bottom: 20px;
                        font-size: .96rem;
                        display: flex;
                        align-items: center;
                        gap: 9px;
                    }

                    .vpp-breadcrumb a {
                        color: #8A8A8A;
                        text-decoration: none;
                    }

                    .vpp-breadcrumb a:hover {
                        color: #007967;
                    }

                    .vpp-detail-layout {
                        display: grid;
                        grid-template-columns: 360px minmax(0, 1fr);
                        gap: 42px;
                        align-items: start;
                    }

                    .vpp-left-panel {
                        display: flex;
                        flex-direction: column;
                        gap: 16px;
                    }

                    .vpp-image-card {
                        background: #fff;
                        border-radius: 8px;
                        box-shadow: 0 10px 26px rgba(15, 23, 42, .08);
                        overflow: hidden;
                        border: 1px solid #E5E7EB;
                    }

                    .vpp-image-box {
                        width: 100%;
                        height: 430px;
                        background: #F4F7F8;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }

                    .vpp-image-box img {
                        width: 100%;
                        height: 100%;
                        object-fit: contain;
                        padding: 18px;
                    }

                    .vpp-no-image {
                        color: #8AA0AA;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        gap: 12px;
                        font-weight: 900;
                        letter-spacing: .08em;
                    }

                    .vpp-no-image i {
                        font-size: 4rem;
                    }

                    .vpp-back-link {
                        color: #6B7280;
                        text-decoration: none;
                        font-size: 1rem;
                        font-weight: 700;
                        display: inline-flex;
                        align-items: center;
                        gap: 8px;
                    }

                    .vpp-back-link:hover {
                        color: #007967;
                    }

                    .vpp-info-panel {
                        min-width: 0;
                    }

                    .vpp-category-badge {
                        display: inline-flex;
                        align-items: center;
                        gap: 8px;
                        background: #007967;
                        color: #fff;
                        padding: 8px 15px;
                        border-radius: 4px;
                        font-size: .85rem;
                        font-weight: 900;
                        letter-spacing: .08em;
                        text-transform: uppercase;
                        margin-bottom: 18px;
                    }

                    .vpp-title {
                        margin: 0 0 18px;
                        font-size: 2.2rem;
                        line-height: 1.2;
                        font-weight: 900;
                        color: #111827;
                    }

                    .vpp-supplier {
                        color: #6B7280;
                        font-size: 1.05rem;
                        margin-bottom: 22px;
                    }

                    .vpp-supplier strong {
                        color: #111827;
                        font-weight: 900;
                    }

                    .vpp-price-stock-box {
                        background: #F7F7FB;
                        border: 1px solid #E5E7EB;
                        border-radius: 10px;
                        padding: 24px 28px;
                        display: flex;
                        align-items: center;
                        gap: 24px;
                        margin-bottom: 22px;
                    }

                    .vpp-price {
                        color: #FF7A00;
                        font-size: 2.1rem;
                        font-weight: 900;
                        white-space: nowrap;
                    }

                    .vpp-stock-pill {
                        display: inline-flex;
                        align-items: center;
                        gap: 8px;
                        background: #EAF6F3;
                        color: #007967;
                        border-radius: 999px;
                        padding: 10px 18px;
                        font-size: 1rem;
                        font-weight: 900;
                    }

                    .vpp-stock-pill.out {
                        background: #FEF2F2;
                        color: #DC2626;
                    }

                    .vpp-purchase-row {
                        display: grid;
                        grid-template-columns: auto 110px minmax(260px, 1fr) 190px;
                        gap: 14px;
                        align-items: center;
                        margin-bottom: 22px;
                    }

                    .vpp-qty-label {
                        color: #6B7280;
                        font-weight: 700;
                        white-space: nowrap;
                    }

                    .vpp-quantity-input {
                        width: 100%;
                        height: 54px;
                        border: 1px solid #E5E7EB;
                        border-radius: 10px;
                        padding: 0 16px;
                        font-size: 1.05rem;
                        outline: none;
                        background: #fff;
                    }

                    .vpp-quantity-input:focus {
                        border-color: #007967;
                        box-shadow: 0 0 0 3px rgba(0, 121, 103, .12);
                    }

                    .vpp-add-cart-btn {
                        width: 100%;
                        height: 56px;
                        border: none;
                        border-radius: 10px;
                        background: #007967;
                        color: #fff;
                        font-size: 1.05rem;
                        font-weight: 900;
                        cursor: pointer;
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                        gap: 10px;
                    }

                    .vpp-add-cart-btn:hover {
                        background: #005F52;
                    }

                    .vpp-add-cart-btn:disabled {
                        background: #D1D5DB;
                        cursor: not-allowed;
                    }

                    .vpp-view-cart-btn {
                        height: 56px;
                        border: 2px solid #007967;
                        border-radius: 10px;
                        background: #fff;
                        color: #007967;
                        font-size: 1.05rem;
                        font-weight: 900;
                        text-decoration: none;
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                        gap: 10px;
                    }

                    .vpp-view-cart-btn:hover {
                        background: #EAF6F3;
                        color: #007967;
                    }

                    .vpp-meta-list {
                        display: flex;
                        align-items: center;
                        gap: 10px;
                        flex-wrap: wrap;
                        margin-bottom: 16px;
                    }

                    .vpp-meta-item {
                        background: #fff;
                        border: 1px solid #E5E7EB;
                        border-radius: 8px;
                        padding: 9px 14px;
                        color: #6B7280;
                        font-weight: 800;
                        display: inline-flex;
                        align-items: center;
                        gap: 8px;
                    }

                    .vpp-meta-item i {
                        color: #007967;
                    }

                    .vpp-description-section {
                        border-top: 1px solid #E5E7EB;
                        padding-top: 18px;
                        margin-top: 12px;
                    }

                    .vpp-section-title {
                        margin: 0 0 8px;
                        font-size: 1rem;
                        font-weight: 900;
                        letter-spacing: .12em;
                        text-transform: uppercase;
                        color: #374151;
                    }

                    .vpp-description {
                        color: #374151;
                        font-size: 1.05rem;
                        line-height: 1.55;
                        margin: 0;
                        padding: 0;
                        min-height: 0;
                    }

                    .vpp-related-section {
                        margin-top: 24px;
                    }

                    .vpp-related-header {
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        border-bottom: 4px solid #007967;
                        padding-bottom: 14px;
                        margin-bottom: 24px;
                    }

                    .vpp-related-title {
                        display: flex;
                        align-items: center;
                        gap: 14px;
                        color: #007967;
                        font-size: 1.45rem;
                        font-weight: 900;
                        letter-spacing: .04em;
                        text-transform: uppercase;
                    }

                    .vpp-related-icon {
                        width: 42px;
                        height: 42px;
                        border-radius: 9px;
                        background: #007967;
                        color: #fff;
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                    }

                    .vpp-related-more {
                        color: #007967;
                        font-weight: 900;
                        text-decoration: none;
                        display: inline-flex;
                        align-items: center;
                        gap: 8px;
                    }

                    .vpp-related-more:hover {
                        color: #005F52;
                    }

                    .vpp-related-grid {
                        display: grid;
                        grid-template-columns: repeat(4, minmax(0, 1fr));
                        gap: 22px;
                    }

                    .vpp-related-card {
                        background: #fff;
                        border: 1px solid #E5E7EB;
                        border-radius: 10px;
                        overflow: hidden;
                        box-shadow: 0 8px 20px rgba(15, 23, 42, .04);
                        text-decoration: none;
                        color: inherit;
                        transition: .18s ease;
                    }

                    .vpp-related-card:hover {
                        transform: translateY(-4px);
                        box-shadow: 0 14px 28px rgba(15, 23, 42, .10);
                    }

                    .vpp-related-image-box {
                        height: 230px;
                        background: #F4F7F8;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        border-bottom: 1px solid #E5E7EB;
                    }

                    .vpp-related-image-box img {
                        width: 100%;
                        height: 100%;
                        object-fit: contain;
                        padding: 16px;
                    }

                    .vpp-related-no-image {
                        color: #8AA0AA;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        gap: 10px;
                        font-weight: 900;
                    }

                    .vpp-related-no-image i {
                        font-size: 3rem;
                    }

                    .vpp-related-body {
                        padding: 18px;
                    }

                    .vpp-related-name {
                        color: #111827;
                        font-size: 1.05rem;
                        font-weight: 900;
                        line-height: 1.4;
                        margin-bottom: 8px;
                    }

                    .vpp-related-desc {
                        color: #6B7280;
                        font-size: .95rem;
                        line-height: 1.45;
                        min-height: 42px;
                        margin-bottom: 14px;
                    }

                    .vpp-related-price {
                        color: #FF7A00;
                        font-size: 1.2rem;
                        font-weight: 900;
                    }

                    @media (max-width: 1200px) {
                        .vpp-related-grid {
                            grid-template-columns: repeat(3, minmax(0, 1fr));
                        }
                    }

                    @media (max-width: 1100px) {
                        .vpp-detail-page {
                            padding: 28px 30px 50px;
                        }

                        .vpp-detail-layout {
                            grid-template-columns: 1fr;
                        }

                        .vpp-image-box {
                            height: 430px;
                        }

                        .vpp-purchase-row {
                            grid-template-columns: auto 110px 1fr;
                        }

                        .vpp-view-cart-btn {
                            grid-column: 3 / 4;
                        }
                    }

                    @media (max-width: 900px) {
                        .vpp-related-grid {
                            grid-template-columns: repeat(2, minmax(0, 1fr));
                        }
                    }

                    @media (max-width: 700px) {
                        .vpp-detail-page {
                            padding: 20px 14px 40px;
                        }

                        .vpp-title {
                            font-size: 1.75rem;
                        }

                        .vpp-price-stock-box {
                            flex-direction: column;
                            align-items: flex-start;
                        }

                        .vpp-purchase-row {
                            grid-template-columns: 1fr;
                        }

                        .vpp-view-cart-btn {
                            grid-column: auto;
                        }
                    }

                    @media (max-width: 600px) {
                        .vpp-related-grid {
                            grid-template-columns: 1fr;
                        }

                        .vpp-related-header {
                            align-items: flex-start;
                            flex-direction: column;
                            gap: 12px;
                        }
                    }
                </style>
            </head>

            <body>

                <jsp:include page="/WEB-INF/view/layout/header.jsp" />

                <main class="vpp-detail-page">

                    <div class="vpp-breadcrumb">
                        <a href="${pageContext.request.contextPath}/">Home</a>
                        <span>›</span>
                        <a href="${pageContext.request.contextPath}/customer/vpp">Stationery</a>
                        <span>›</span>
                        <span>
                            <c:out value="${item.name}" />
                        </span>
                    </div>

                    <section class="vpp-detail-layout">

                        <div class="vpp-left-panel">

                            <div class="vpp-image-card">
                                <div class="vpp-image-box">
                                    <c:choose>
                                        <c:when test="${not empty item.imagePath}">
                                            <img src="${pageContext.request.contextPath}${item.imagePath}?v=${item.updatedAt}"
                                                alt="${item.name}">
                                        </c:when>

                                        <c:otherwise>
                                            <div class="vpp-no-image">
                                                <i class="fa-solid fa-box"></i>
                                                <span>NO IMAGE</span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <a href="${pageContext.request.contextPath}/customer/vpp" class="vpp-back-link">
                                <i class="fa-solid fa-arrow-left"></i>
                                Back to list
                            </a>

                        </div>

                        <div class="vpp-info-panel">

                            <div class="vpp-category-badge">
                                <i class="fa-solid fa-tag"></i>
                                <c:out value="${item.categoryName}" />
                            </div>

                            <h1 class="vpp-title">
                                <c:out value="${item.name}" />
                            </h1>

                            <div class="vpp-supplier">
                                Supplier:
                                <strong>
                                    <c:choose>
                                        <c:when test="${not empty item.supplier}">
                                            <c:out value="${item.supplier}" />
                                        </c:when>
                                        <c:otherwise>Updating</c:otherwise>
                                    </c:choose>
                                </strong>
                            </div>

                            <div class="vpp-price-stock-box">

                                <div class="vpp-price">
                                    <fmt:formatNumber value="${item.price}" type="number" groupingUsed="true" />
                                    VND
                                </div>

                                <c:choose>
                                    <c:when test="${item.inStock}">
                                        <div class="vpp-stock-pill">
                                            <i class="fa-solid fa-circle-check"></i>
                                            In Stock: (${item.stockQuantity})
                                        </div>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="vpp-stock-pill out">
                                            <i class="fa-solid fa-circle-xmark"></i>
                                            Out of stock
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                            </div>

                            <c:choose>
                                <c:when test="${item.inStock}">
                                    <c:choose>
                                        <c:when test="${not empty sessionScope.username}">
                                            <form method="post"
                                                action="${pageContext.request.contextPath}/cart/add-vpp">
                                                <input type="hidden" name="${_csrf.parameterName}"
                                                    value="${_csrf.token}" />
                                                <input type="hidden" name="vppItemId" value="${item.id}" />
                                                <input type="hidden" name="redirect" value="/customer/vpp/${item.id}" />
                                                <div class="vpp-purchase-row">
                                                    <label for="quantity" class="vpp-qty-label">
                                                        Quantity
                                                    </label>
                                                    <input id="quantity" type="number" name="quantity"
                                                        class="vpp-quantity-input" min="1" max="${item.stockQuantity}"
                                                        value="1" required />

                                                    <button type="submit" class="vpp-add-cart-btn">
                                                        <i class="fa-solid fa-cart-shopping"></i>
                                                        Add to cart
                                                    </button>


                                                </div>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="vpp-purchase-row">
                                                <a href="${pageContext.request.contextPath}/login"
                                                    class="vpp-add-cart-btn" style="text-decoration:none;">
                                                    <i class="fa-solid fa-right-to-bracket"></i>
                                                    Đăng nhập để mua
                                                </a>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                            </c:choose>

                            <div class="vpp-meta-list">
                                <div class="vpp-meta-item">
                                    <i class="fa-solid fa-layer-group"></i>
                                    <c:out value="${item.categoryName}" />
                                </div>
                                <div class="vpp-meta-item">
                                    <i class="fa-solid fa-boxes-stacked"></i>
                                    Stock:
                                    <c:out value="${item.stockQuantity}" />
                                </div>
                                <c:if test="${not empty item.supplier}">
                                    <div class="vpp-meta-item">
                                        <i class="fa-solid fa-truck-field"></i>
                                        <c:out value="${item.supplier}" />
                                    </div>
                                </c:if>
                            </div>
                            <div class="vpp-description-section">
                                <h2 class="vpp-section-title">
                                    ABOUT THIS PRODUCT
                                </h2>
                                <div class="vpp-description">
                                    <c:choose>
                                        <c:when test="${not empty item.description}">
                                            <c:out value="${item.description}" />
                                        </c:when>
                                        <c:otherwise>
                                            No description available.
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>



                        </div>



                    </section>

                    <%-- REVIEWS --%>
                        <section class="rating-section vpp-rating-section">
                            <h2 class="rating-title">Product reviews</h2>

                            <%-- Form Creation & State Section --%>
                                <div class="rating-create">
                                    <c:choose>
                                        <%-- TH 1: Được phép viết đánh giá mới --%>
                                            <c:when test="${canReviewVpp}">
                                                <form id="vppCreateRatingForm"
                                                    action="${pageContext.request.contextPath}/ratings/vpp/create"
                                                    method="post">
                                                    <input type="hidden" name="${_csrf.parameterName}"
                                                        value="${_csrf.token}" />
                                                    <input type="hidden" name="vppItemId" value="${item.id}" />

                                                    <label>Rating</label>

                                                    <div class="rating-stars-select">
                                                        <input id="star5" type="radio" name="ratingValue" value="5"
                                                            required>
                                                        <label for="star5">★</label>

                                                        <input id="star4" type="radio" name="ratingValue" value="4">
                                                        <label for="star4">★</label>

                                                        <input id="star3" type="radio" name="ratingValue" value="3">
                                                        <label for="star3">★</label>

                                                        <input id="star2" type="radio" name="ratingValue" value="2">
                                                        <label for="star2">★</label>

                                                        <input id="star1" type="radio" name="ratingValue" value="1">
                                                        <label for="star1">★</label>
                                                    </div>

                                                    <textarea name="reviewText" rows="5" maxlength="1000"
                                                        required></textarea>

                                                    <button type="submit" class="vpp-add-cart-btn">Submit
                                                        review</button>
                                                </form>
                                            </c:when>

                                            <%-- TH 2: Đã đánh giá trước đó -> Hiển thị form cập nhật --%>
                                                <c:when test="${not empty myRating}">
                                                    <div class="vpp-alert">
                                                        You have already reviewed this book.
                                                    </div>

                                                    <form id="vppRatingForm"
                                                        action="${pageContext.request.contextPath}/ratings/vpp/update"
                                                        method="post" style="display:none;">
                                                        <input type="hidden" name="${_csrf.parameterName}"
                                                            value="${_csrf.token}" />
                                                        <input type="hidden" name="vppItemId" value="${item.id}" />

                                                        <div class="rating-stars-select">
                                                            <input id="update-star5" type="radio" name="ratingValue"
                                                                value="5">
                                                            <label for="update-star5">★</label>

                                                            <input id="update-star4" type="radio" name="ratingValue"
                                                                value="4">
                                                            <label for="update-star4">★</label>

                                                            <input id="update-star3" type="radio" name="ratingValue"
                                                                value="3">
                                                            <label for="update-star3">★</label>

                                                            <input id="update-star2" type="radio" name="ratingValue"
                                                                value="2">
                                                            <label for="update-star2">★</label>

                                                            <input id="update-star1" type="radio" name="ratingValue"
                                                                value="1">
                                                            <label for="update-star1">★</label>
                                                        </div>

                                                        <textarea name="reviewText">${myRating.review}</textarea>

                                                        <button type="submit" class="vpp-add-cart-btn">Update
                                                            review</button>
                                                    </form>
                                                </c:when>

                                                <%-- TH 3: Đã đăng nhập nhưng chưa mua hàng --%>
                                                    <c:when test="${not empty currentUser}">
                                                        <div class="vpp-alert vpp-alert-error">
                                                            You need to purchase the product before you can leave a
                                                            review.
                                                        </div>
                                                    </c:when>

                                                    <%-- TH 4: Chưa đăng nhập --%>
                                                        <c:otherwise>
                                                            <div class="vpp-alert">
                                                                Please login to leave a review.
                                                            </div>
                                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <%-- Review List Section --%>
                                    <section class="rating-list-section">
                                        <c:choose>
                                            <%-- TH 1: Có danh sách đánh giá --%>
                                                <c:when test="${not empty vppRatings}">
                                                    <c:forEach items="${vppRatings}" var="rating">
                                                        <div class="rating-item">

                                                            <div class="rating-header">
                                                                <div class="rating-user">
                                                                    ${rating.customer.fullName}
                                                                </div>

                                                                <div class="rating-actions">
                                                                    <c:if
                                                                        test="${not empty currentUser && currentUser.id == rating.customer.id}">
                                                                        <button type="button" class="rating-edit-btn"
                                                                            onclick="editVppRating('${rating.ratingValue}', '${fn:escapeXml(rating.review)}')">
                                                                            <i class="fa-solid fa-pen"></i>
                                                                        </button>

                                                                        <form
                                                                            action="${pageContext.request.contextPath}/ratings/vpp/delete"
                                                                            method="post" style="display:inline;">
                                                                            <input type="hidden"
                                                                                name="${_csrf.parameterName}"
                                                                                value="${_csrf.token}" />
                                                                            <input type="hidden" name="vppItemId"
                                                                                value="${item.id}" />

                                                                            <button type="submit"
                                                                                class="rating-delete-btn"
                                                                                onclick="return confirm('Delete this review?')">
                                                                                <i class="fa-solid fa-trash-can"></i>
                                                                            </button>
                                                                        </form>
                                                                    </c:if>
                                                                </div>
                                                            </div>

                                                            <div class="rating-stars">
                                                                <c:forEach begin="1" end="5" var="i">
                                                                    <c:choose>
                                                                        <c:when test="${i <= rating.ratingValue}">
                                                                            <i class="fa-solid fa-star star-filled"></i>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <i
                                                                                class="fa-regular fa-star star-empty"></i>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>
                                                            </div>

                                                            <div class="rating-date">
                                                                ${rating.createdAtFormatted}
                                                            </div>

                                                            <div class="rating-content">
                                                                ${rating.review}
                                                            </div>

                                                        </div>
                                                    </c:forEach>
                                                </c:when>

                                                <%-- TH 2: Chưa có đánh giá nào --%>
                                                    <c:otherwise>
                                                        <div class="no-rating">
                                                            No reviews yet for this product.
                                                        </div>
                                                    </c:otherwise>
                                        </c:choose>
                                    </section>
                        </section>

                        <c:if test="${not empty relatedItems}">

                            <section class="vpp-related-section">

                                <div class="vpp-related-header">

                                    <div class="vpp-related-title">
                                        <span class="vpp-related-icon">
                                            <i class="fa-solid fa-wand-magic-sparkles"></i>
                                        </span>
                                        Có thể bạn quan tâm
                                    </div>

                                    <a href="${pageContext.request.contextPath}/customer/vpp" class="vpp-related-more">
                                        Xem thêm
                                        <i class="fa-solid fa-chevron-right"></i>
                                    </a>

                                </div>

                                <div class="vpp-related-grid">

                                    <c:forEach items="${relatedItems}" var="related">

                                        <a href="${pageContext.request.contextPath}/customer/vpp/${related.id}"
                                            class="vpp-related-card">

                                            <div class="vpp-related-image-box">
                                                <c:choose>
                                                    <c:when test="${not empty related.imagePath}">
                                                        <img src="${pageContext.request.contextPath}${related.imagePath}"
                                                            alt="${related.name}">
                                                    </c:when>

                                                    <c:otherwise>
                                                        <div class="vpp-related-no-image">
                                                            <i class="fa-solid fa-box"></i>
                                                            <span>NO IMAGE</span>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="vpp-related-body">

                                                <div class="vpp-related-name">
                                                    <c:out value="${related.name}" />
                                                </div>

                                                <div class="vpp-related-desc">
                                                    <c:if test="${not empty related.description}">
                                                        <c:out value="${related.description}" />
                                                    </c:if>
                                                </div>

                                                <div class="vpp-related-price">
                                                    <fmt:formatNumber value="${related.price}" type="number"
                                                        groupingUsed="true" />
                                                    đ
                                                </div>

                                            </div>

                                        </a>

                                    </c:forEach>

                                </div>

                            </section>

                        </c:if>

                </main>

                <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

                <script>
                    function editVppRating(value, review) {

                        const form = document.getElementById("vppRatingForm");

                        if (!form) return;


                        form.style.display = "block";


                        // chọn số sao cũ
                        const star = form.querySelector(
                            'input[name="ratingValue"][value="' + value + '"]'
                        );

                        if (star) {
                            star.checked = true;
                        }


                        // load nội dung review cũ
                        const textarea = form.querySelector(
                            'textarea[name="reviewText"]'
                        );

                        if (textarea) {
                            textarea.value = review;
                        }


                        form.scrollIntoView({
                            behavior: "smooth"
                        });
                    }
                </script>
            </body>

            </html>