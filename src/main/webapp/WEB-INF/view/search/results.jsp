<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>Search: "${q}" — Booktify</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="${ctx}/css/header.css" />
    <link rel="stylesheet" href="${ctx}/css/footer.css" />
    <link rel="stylesheet" href="${ctx}/css/homepage.css" />
    <link rel="stylesheet" href="${ctx}/css/book.css" />
    <link rel="stylesheet" href="${ctx}/css/author.css" />

    <style>
        .search-wrap {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1.5rem 1.5rem 3.5rem;
        }

        .search-breadcrumb {
            display: flex;
            align-items: center;
            gap: .45rem;
            font-size: .8rem;
            color: var(--text-muted);
            margin-bottom: 1rem;
        }

        .search-breadcrumb a {
            color: var(--text-muted);
            text-decoration: none;
        }

        .search-breadcrumb a:hover {
            color: var(--primary);
        }

        .search-breadcrumb i {
            font-size: .6rem;
            opacity: .4;
        }

        .search-summary {
            background: rgba(0, 107, 94, .07);
            border: 1px solid rgba(0, 107, 94, .18);
            border-radius: var(--radius);
            padding: .7rem 1.1rem;
            margin-bottom: 1.5rem;
            font-size: .9rem;
            color: var(--primary);
            font-weight: 600;
        }

        .search-section {
            margin-bottom: 2.25rem;
        }

        .search-section__head {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: .9rem;
        }

        .search-section__head h2 {
            margin: 0;
            font-size: 1.05rem;
            font-weight: 800;
            color: var(--primary);
            display: flex;
            align-items: center;
            gap: .5rem;
        }

        .search-section__head a {
            font-size: .78rem;
            color: var(--text-muted);
            text-decoration: none;
            font-weight: 600;
        }

        .search-section__head a:hover {
            color: var(--primary);
        }

        .search-empty-state {
            text-align: center;
            padding: 3.5rem 1.5rem;
            color: var(--text-muted);
            background: #fff;
            border: 1px solid var(--border);
            border-radius: var(--radius);
        }

        .search-empty-state i {
            font-size: 3rem;
            display: block;
            margin-bottom: 1rem;
            opacity: .25;
        }

        .search-empty-state a {
            display: inline-flex;
            align-items: center;
            gap: .45rem;
            margin-top: 1rem;
            padding: .6rem 1.3rem;
            border-radius: var(--radius);
            background: var(--primary);
            color: #fff;
            font-weight: 700;
            text-decoration: none;
        }

        .search-author-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            gap: 1rem;
        }
    </style>
</head>

<body class="home-page">

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <div class="search-wrap">

        <nav class="search-breadcrumb">
            <a href="${ctx}/">Home</a>
            <i class="fa-solid fa-chevron-right"></i>
            <span>Search: "<c:out value="${q}" />"</span>
        </nav>

        <c:choose>
            <c:when test="${empty q}">
                <div class="search-empty-state">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    <p>Type something in the search bar to find books, stationery, or authors.</p>
                </div>
            </c:when>

            <c:when test="${totalResults == 0}">
                <div class="search-empty-state">
                    <i class="fa-solid fa-box-open"></i>
                    <p>No results found for "<strong><c:out value="${q}" /></strong>".</p>
                    <a href="${ctx}/"><i class="fa-solid fa-arrow-left"></i> Back to home</a>
                </div>
            </c:when>

            <c:otherwise>
                <div class="search-summary">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    ${totalResults} result(s) for "<c:out value="${q}" />"
                </div>

                <%-- ── Books ── --%>
                <c:if test="${not empty books}">
                    <section class="search-section">
                        <div class="search-section__head">
                            <h2><i class="fa-solid fa-book"></i> Books (${books.size()})</h2>
                            <c:url var="booksSearchUrl" value="/books">
                                <c:param name="q" value="${q}" />
                            </c:url>
                            <a href="${booksSearchUrl}">View all in Books <i class="fa-solid fa-chevron-right"></i></a>
                        </div>

                        <div class="product-grid">
                            <c:forEach items="${books}" var="book">
                                <a href="${ctx}/books/${book.id}" class="product-card">
                                    <div class="product-card__thumb">
                                        <c:choose>
                                            <c:when test="${not empty book.imageUrl}">
                                                <img src="${book.imageUrl}" alt="${book.title}" />
                                            </c:when>
                                            <c:otherwise>
                                                <div class="product-card__no-img">
                                                    <i class="fa-solid fa-book"></i>
                                                    <span>No cover</span>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${not empty book.category}">
                                            <span class="product-card__cat-badge">${book.category.name}</span>
                                        </c:if>
                                    </div>
                                    <div class="product-card__body">
                                        <div class="product-card__title">${book.title}</div>
                                        <div class="product-card__author">
                                            <c:out value="${book.author.authorName}" default="—" />
                                        </div>
                                        <div class="product-card__footer">
                                            <div class="product-card__price-box">
                                                <c:choose>
                                                    <c:when test="${not empty bestPromotionMap[book.id]}">
                                                        <div class="product-card__discount-line">
                                                            <span class="product-card__old-price">
                                                                ${book.priceFormatted} &#8363;
                                                            </span>
                                                            <span class="product-card__discount-badge">
                                                                ${discountLabelMap[book.id]}
                                                            </span>
                                                        </div>
                                                        <span class="product-card__price product-card__price--sale">
                                                            ${discountedPriceFormattedMap[book.id]} &#8363;
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="product-card__price">
                                                            ${book.priceFormatted} &#8363;
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <span class="product-card__link">Details</span>
                                        </div>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </section>
                </c:if>

                <%-- ── Stationery (VPP) ── --%>
                <c:if test="${not empty vppItems}">
                    <section class="search-section">
                        <div class="search-section__head">
                            <h2><i class="fa-solid fa-pen-ruler"></i> Stationery (${vppItems.size()})</h2>
                            <c:url var="vppSearchUrl" value="/customer/vpp">
                                <c:param name="q" value="${q}" />
                            </c:url>
                            <a href="${vppSearchUrl}">View all in Stationery <i class="fa-solid fa-chevron-right"></i></a>
                        </div>

                        <div class="product-grid">
                            <c:forEach items="${vppItems}" var="item">
                                <a href="${ctx}/customer/vpp/${item.id}" class="product-card">
                                    <div class="product-card__thumb">
                                        <c:choose>
                                            <c:when test="${item.hasImage}">
                                                <img src="${ctx}/uploads/vpp/${item.id}/image?v=${item.updatedAt}"
                                                    alt="${item.name}" />
                                            </c:when>
                                            <c:when test="${not empty item.imagePath}">
                                                <img src="${ctx}${item.imagePath}" alt="${item.name}" />
                                            </c:when>
                                            <c:otherwise>
                                                <div class="product-card__no-img">
                                                    <i class="fa-solid fa-pen-ruler"></i>
                                                    <span>No image</span>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${not empty item.categoryName}">
                                            <span class="product-card__cat-badge">
                                                <c:out value="${item.categoryName}" />
                                            </span>
                                        </c:if>
                                    </div>
                                    <div class="product-card__body">
                                        <div class="product-card__title">
                                            <c:out value="${item.name}" />
                                        </div>
                                        <div class="product-card__author">
                                            <c:choose>
                                                <c:when test="${not empty item.description}">
                                                    <c:out value="${item.description}" />
                                                </c:when>
                                                <c:otherwise>Stationery products at Booktify.</c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="product-card__footer">
                                            <span class="product-card__price">
                                                <fmt:formatNumber value="${item.price}" type="number" groupingUsed="true" />
                                                &#8363;
                                            </span>
                                            <span class="product-card__link">Details</span>
                                        </div>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </section>
                </c:if>

                <%-- ── Authors ── --%>
                <c:if test="${not empty authors}">
                    <section class="search-section">
                        <div class="search-section__head">
                            <h2><i class="fa-solid fa-user-pen"></i> Authors (${authors.size()})</h2>
                            <a href="${ctx}/authors">View all Authors <i class="fa-solid fa-chevron-right"></i></a>
                        </div>

                        <div class="search-author-grid">
                            <c:forEach items="${authors}" var="a">
                                <a href="${ctx}/authors/${a.authorId}" class="author-card">
                                    <div class="author-img">
                                        <c:choose>
                                            <c:when test="${not empty a.profileImage}">
                                                <img src="${a.profileImage}" alt="${a.authorName}" />
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fa-solid fa-user"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="author-body">
                                        <div class="author-name">${a.authorName}</div>
                                        <div class="author-nationality">${a.nationality}</div>
                                        <span class="author-btn">Details</span>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </section>
                </c:if>
            </c:otherwise>
        </c:choose>

    </div>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

</body>

</html>
