<%-- Storefront book-set listing — same UI pattern as book/list.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/homepage.css" />
    <link rel="stylesheet" href="/css/book.css" />

    <title>
        <c:choose>
            <c:when test="${not empty q}">Results: "${q}"</c:when>
            <c:when test="${not empty tag}">${tagLabel}</c:when>
            <c:otherwise>All book sets</c:otherwise>
        </c:choose>
        — Booktify
    </title>
</head>

<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <div class="shop-wrap">

        <aside class="shop-sidebar">
            <div class="sidebar-head"><i class="fa-solid fa-bars"></i> Tags / Series</div>
            <div class="sidebar-list">
                <a href="/book-sets"
                    class="sidebar-item ${empty tag && empty q ? 'active' : ''}">
                    <span class="dot"></span> All sets
                </a>
                <c:forEach items="${setTagLabels}" var="entry">
                    <c:url var="tagUrl" value="/book-sets">
                        <c:param name="tag" value="${entry.key}" />
                    </c:url>
                    <a href="${tagUrl}"
                        class="sidebar-item ${tag == entry.key ? 'active' : ''}">
                        <span class="dot"></span> <c:out value="${entry.value}" />
                    </a>
                </c:forEach>
            </div>
        </aside>

        <main class="shop-main">

            <nav class="shop-breadcrumb">
                <a href="/">Home</a>
                <i class="fa-solid fa-chevron-right"></i>
                <a href="/book-sets">Book sets</a>
                <c:if test="${not empty tag}">
                    <i class="fa-solid fa-chevron-right"></i>
                    <span><c:out value="${tagLabel}" /></span>
                </c:if>
                <c:if test="${not empty q}">
                    <i class="fa-solid fa-chevron-right"></i>
                    <span>Search: "${q}"</span>
                </c:if>
            </nav>

            <c:if test="${not empty errorMessage}">
                <div style="margin:0 0 18px;padding:14px 18px;border-radius:10px;font-weight:700;
                            background:#FEF2F2;color:#DC2626;border:1px solid #FECACA;
                            display:flex;align-items:center;gap:10px;">
                    <i class="fa-solid fa-circle-exclamation"></i>
                    <c:out value="${errorMessage}" />
                </div>
            </c:if>

            <c:if test="${not empty q}">
                <div class="search-result-bar">
                    <span><i class="fa-solid fa-magnifying-glass"></i>
                        Search results for: "<strong>${q}</strong>"
                        — ${empty bookSets ? 0 : bookSets.size()} set(s)
                    </span>
                    <a href="/book-sets"><i class="fa-solid fa-xmark"></i> Clear search</a>
                </div>
            </c:if>

            <div class="shop-toolbar">
                <div class="shop-toolbar__left">
                    <h2>
                        <c:choose>
                            <c:when test="${not empty tag}"><c:out value="${tagLabel}" /></c:when>
                            <c:when test="${not empty q}">Search results</c:when>
                            <c:otherwise>All book sets</c:otherwise>
                        </c:choose>
                    </h2>
                    <p>${empty bookSets ? 0 : bookSets.size()} product(s)</p>
                </div>
            </div>

            <div class="product-grid">
                <c:choose>
                    <c:when test="${empty bookSets}">
                        <div class="empty-state">
                            <i class="fa-solid fa-box-open"></i>
                            <p>No book sets found.</p>
                            <a href="/book-sets"><i class="fa-solid fa-arrow-left"></i>
                                View all sets</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${bookSets}" var="set">
                            <a href="/book-sets/${set.id}" class="product-card">
                                <div class="product-card__thumb">
                                    <c:choose>
                                        <c:when test="${not empty set.imageUrl}">
                                            <img src="${set.imageUrl}"
                                                alt="<c:out value='${set.name}'/>" />
                                        </c:when>
                                        <c:otherwise>
                                            <div class="product-card__no-img">
                                                <i class="fa-solid fa-layer-group"></i>
                                                <span>No cover</span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:if test="${not empty tagLabelMap[set.id] && tagLabelMap[set.id] != 'General'}">
                                        <span class="product-card__cat-badge">
                                            <c:out value="${tagLabelMap[set.id]}" />
                                        </span>
                                    </c:if>
                                    <c:if test="${availableMap[set.id] <= 0}">
                                        <span style="position:absolute;top:8px;right:8px;background:#dc2626;color:#fff;font-size:.7rem;font-weight:700;padding:.2rem .55rem;border-radius:999px;">
                                            Out of stock
                                        </span>
                                    </c:if>
                                </div>
                                <div class="product-card__body">
                                    <div class="product-card__title">
                                        <c:out value="${set.name}" />
                                    </div>
                                    <c:choose>
                                        <c:when test="${availableMap[set.id] <= 0}">
                                            <div class="product-card__author" style="color:#dc2626;font-weight:600;">
                                                Out of stock
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="product-card__author">
                                                ${set.items.size()} books in set
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="product-card__footer">
                                        <div class="product-card__price-box">
                                            <c:choose>
                                                <c:when test="${discountedMap[set.id]}">
                                                    <div class="product-card__discount-line">
                                                        <span class="product-card__old-price">
                                                            ${retailMap[set.id]} &#8363;
                                                        </span>
                                                    </div>
                                                    <span class="product-card__price product-card__price--sale">
                                                        ${setPriceMap[set.id]} &#8363;
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="product-card__price">
                                                        ${setPriceMap[set.id]} &#8363;
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <span class="product-card__link">Details</span>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
</body>

</html>
