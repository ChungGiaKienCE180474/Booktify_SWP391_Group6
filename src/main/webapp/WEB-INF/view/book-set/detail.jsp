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
    <link rel="stylesheet" href="/css/homepage.css" />
    <link rel="stylesheet" href="/css/book.css" />
    <title>${bookSet.name} — Booktify</title>
</head>

<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <nav class="breadcrumb">
        <a href="/">Home</a>
        <i class="fa-solid fa-chevron-right"></i>
        <a href="/book-sets">Book sets</a>
        <c:if test="${not empty bookSet.gradeLevel}">
            <i class="fa-solid fa-chevron-right"></i>
            <c:url var="tagFilterUrl" value="/book-sets">
                <c:param name="tag" value="${bookSet.gradeLevel}" />
            </c:url>
            <a href="${tagFilterUrl}">${tagLabel}</a>
        </c:if>
        <i class="fa-solid fa-chevron-right"></i>
        <span>${bookSet.name}</span>
    </nav>

    <c:if test="${not empty successMessage}">
        <div style="max-width:1200px;margin:0 auto;padding:0 1.5rem;">
            <div style="background:#dcfce7;color:#166534;padding:.75rem 1rem;border-radius:8px;margin-bottom:.5rem;">
                ${successMessage}
            </div>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div style="max-width:1200px;margin:0 auto;padding:0 1.5rem;">
            <div style="background:#fee2e2;color:#991b1b;padding:.75rem 1rem;border-radius:8px;margin-bottom:.5rem;">
                ${errorMessage}
            </div>
        </div>
    </c:if>

    <div class="detail-wrap">
        <aside class="detail-cover">
            <div class="detail-cover__frame">
                <c:choose>
                    <c:when test="${not empty bookSet.imageUrl}">
                        <img src="${bookSet.imageUrl}" alt="${bookSet.name}" />
                    </c:when>
                    <c:otherwise>
                        <div class="detail-cover__placeholder">
                            <i class="fa-solid fa-layer-group"></i>
                            <span>No cover</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <a href="/book-sets" class="detail-cover__back">
                <i class="fa-solid fa-arrow-left"></i> Back to list
            </a>
        </aside>

        <div class="detail-info">
            <c:choose>
                <c:when test="${not empty bookSet.gradeLevel}">
                    <c:url var="tagLink" value="/book-sets">
                        <c:param name="tag" value="${bookSet.gradeLevel}" />
                    </c:url>
                    <a href="${tagLink}" class="detail-info__cat-link">
                        <i class="fa-solid fa-tag"></i> ${tagLabel}
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="/book-sets" class="detail-info__cat-link">
                        <i class="fa-solid fa-layer-group"></i> Book set
                    </a>
                </c:otherwise>
            </c:choose>

            <h1 class="detail-info__title"><c:out value="${bookSet.name}"/></h1>
            <p class="detail-info__author">
                Includes <strong>${bookSet.items.size()} books</strong>
            </p>

            <div class="detail-price-row">
                <c:choose>
                    <c:when test="${discounted}">
                        <div class="detail-price-stack">
                            <div class="detail-discount-line">
                                <span class="detail-old-price">
                                    ${retailTotalFormatted} &#8363;
                                </span>
                                <span class="detail-discount-badge">Set price</span>
                            </div>
                            <div class="detail-price detail-price--sale">
                                ${setPriceFormatted} &#8363;
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="detail-price">
                            ${setPriceFormatted} &#8363;
                        </div>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${availableSets > 0}">
                        <span class="detail-stock detail-stock--in">
                            <i class="fa-solid fa-circle-check"></i> In stock
                            (${availableSets})
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="detail-stock detail-stock--out">
                            <i class="fa-solid fa-circle-xmark"></i> Out of stock
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="detail-actions">
                <c:choose>
                    <c:when test="${bookSet.active and availableSets > 0}">
                        <c:choose>
                            <c:when test="${not empty sessionScope.username}">
                                <form action="/cart/add-set" method="post" class="detail-cart-form">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <input type="hidden" name="bookSetId" value="${bookSet.id}" />
                                    <input type="hidden" name="redirect" value="/book-sets/${bookSet.id}" />
                                    <div class="detail-qty-group">
                                        <label class="detail-qty-label" for="cartQty">Quantity</label>
                                        <input type="number" id="cartQty" name="quantity"
                                               value="1" min="1" max="${availableSets}"
                                               step="1" class="detail-qty-input" required />
                                    </div>
                                    <button type="submit" class="btn-detail-primary">
                                        <i class="fa-solid fa-cart-shopping"></i> Add to cart
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <a href="/login" class="btn-detail-primary">
                                    <i class="fa-solid fa-right-to-bracket"></i> Log in to buy
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <button type="button" class="btn-detail-primary" disabled
                                style="opacity:.6;cursor:not-allowed;">
                            <i class="fa-solid fa-cart-shopping"></i> Cannot add to cart
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="detail-meta">
                <span class="meta-chip">
                    <i class="fa-solid fa-books"></i> ${bookSet.items.size()} books
                </span>
                <span class="meta-chip">
                    <i class="fa-solid fa-boxes-stacked"></i> Stock: ${availableSets}
                </span>
                <c:if test="${not empty bookSet.gradeLevel}">
                    <span class="meta-chip">
                        <i class="fa-solid fa-tags"></i> ${tagLabel}
                    </span>
                </c:if>
            </div>

            <c:if test="${not empty bookSet.items}">
                <div class="detail-desc-label">Included in this set</div>
                <ul class="set-include-list">
                    <c:forEach items="${bookSet.items}" var="item">
                        <li>
                            <a href="/books/${item.book.id}" class="set-include-row">
                                <span class="set-include-thumb">
                                    <c:choose>
                                        <c:when test="${not empty item.book.imageUrl}">
                                            <img src="${item.book.imageUrl}" alt="" />
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fa-solid fa-book"></i>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                                <span class="set-include-main">
                                    <span class="set-include-title"><c:out value="${item.book.title}"/></span>
                                    <span class="set-include-meta">
                                        Qty ${item.quantity}
                                        <c:if test="${not empty item.book.author}">
                                            · <c:out value="${item.book.author.authorName}"/>
                                        </c:if>
                                    </span>
                                </span>
                                <span class="set-include-price">${item.book.priceFormatted} &#8363;</span>
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <div class="detail-desc-label">About this set</div>
            <c:choose>
                <c:when test="${not empty bookSet.description}">
                    <p class="detail-desc"><c:out value="${bookSet.description}"/></p>
                </c:when>
                <c:otherwise>
                    <p class="detail-desc" style="color:var(--text-muted);font-style:italic;">
                        No description available for this book set.
                    </p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
</body>

</html>
