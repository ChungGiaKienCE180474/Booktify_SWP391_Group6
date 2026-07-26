<%-- Storefront book listing: category/genre/keyword filters. --%>
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
                        <c:when test="${not empty selectedCategory}">${selectedCategory.name}</c:when>
                        <c:otherwise>All books</c:otherwise>
                    </c:choose>
                    — Booktify
                </title>

            </head>

            <body class="home-page">
                <jsp:include page="/WEB-INF/view/layout/header.jsp" />

                <div class="shop-wrap">

                    <%-- ── Sidebar ── --%>
                        <aside class="shop-sidebar">
                            <div class="sidebar-head"><i class="fa-solid fa-bars"></i> Book categories</div>
                            <div class="sidebar-list">
                                <a href="/books"
                                    class="sidebar-item ${empty selectedCategoryId && empty q ? 'active' : ''}">
                                    <span class="dot"></span> All books
                                </a>
                                <c:forEach items="${categories}" var="cat">
                                    <c:if test="${cat.active}">
                                        <a href="/books?categoryId=${cat.id}"
                                            class="sidebar-item ${selectedCategoryId == cat.id ? 'active' : ''}">
                                            <span class="dot"></span> ${cat.name}
                                        </a>
                                    </c:if>
                                </c:forEach>
                            </div>

                            <%-- Genre filter: independent from Category, matches books with any selected genre --%>
                                <c:if test="${not empty genres}">
                                    <div class="sidebar-head" style="border-top:1px solid rgba(255,255,255,.15);">
                                        <i class="fa-solid fa-tags"></i> Genre
                                    </div>
                                    <form method="get" action="/books"
                                        style="width:100%;box-sizing:border-box;padding:.75rem 1rem;display:flex;flex-direction:column;gap:.5rem;">
                                        <c:if test="${not empty selectedCategoryId}">
                                            <input type="hidden" name="categoryId" value="${selectedCategoryId}" />
                                        </c:if>
                                        <c:if test="${not empty q}">
                                            <input type="hidden" name="q" value="${q}" />
                                        </c:if>
                                        <c:forEach items="${genres}" var="g">
                                            <label
                                                style="display:flex;align-items:center;gap:.5rem;font-size:.83rem;color:var(--text-muted);cursor:pointer;">
                                                <input type="checkbox" name="genreIds" value="${g.id}" <c:if
                                                    test="${selectedGenreIds.contains(g.id)}">checked
                                </c:if>
                                style="accent-color:var(--primary);" />
                                <c:out value="${g.name}" />
                                </label>
                                </c:forEach>
                                <button type="submit"
                                    style="align-self:flex-start;margin-top:.4rem;padding:.4rem .9rem;border:none;border-radius:6px;background:var(--primary);color:#fff;font-size:.78rem;font-weight:700;cursor:pointer;">
                                    <i class="fa-solid fa-filter"></i> Apply
                                </button>
                                <c:if test="${not empty selectedGenreIds}">
                                    <a href="/books<c:if test='${not empty selectedCategoryId}'>?categoryId=${selectedCategoryId}</c:if>"
                                        style="font-size:.76rem;color:var(--text-muted);text-decoration:underline;">Clear
                                        genre filter</a>
                                </c:if>
                                </form>
                                </c:if>
                        </aside>

                        <%-- ── Main ── --%>
                            <main class="shop-main">

                                <%-- Breadcrumb --%>
                                    <nav class="shop-breadcrumb">
                                        <a href="/">Home</a>
                                        <i class="fa-solid fa-chevron-right"></i>
                                        <a href="/books">Books</a>
                                        <c:if test="${not empty selectedCategory}">
                                            <i class="fa-solid fa-chevron-right"></i>
                                            <span>${selectedCategory.name}</span>
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

                                    <%-- Search result bar --%>
                                        <c:if test="${not empty q}">
                                            <div class="search-result-bar">
                                                <span><i class="fa-solid fa-magnifying-glass"></i>
                                                    Search results for: "<strong>${q}</strong>"
                                                    — ${empty books ? 0 : books.size()} book(s)
                                                </span>
                                                <a href="/books"><i class="fa-solid fa-xmark"></i> Clear search</a>
                                            </div>
                                        </c:if>

                                        <%-- Toolbar --%>
                                            <div class="shop-toolbar">
                                                <div class="shop-toolbar__left">
                                                    <h2>
                                                        <c:choose>
                                                            <c:when test="${not empty selectedCategory}">
                                                                ${selectedCategory.name}</c:when>
                                                            <c:when test="${not empty q}">Search results</c:when>
                                                            <c:otherwise>All books</c:otherwise>
                                                        </c:choose>
                                                    </h2>
                                                    <p>${empty books ? 0 : books.size()} product(s)</p>
                                                </div>
                                            </div>

                                            <%-- Product grid --%>
                                                <div class="product-grid">
                                                    <c:choose>
                                                        <c:when test="${empty books}">
                                                            <div class="empty-state">
                                                                <i class="fa-solid fa-box-open"></i>
                                                                <p>No books found.</p>
                                                                <a href="/books"><i class="fa-solid fa-arrow-left"></i>
                                                                    View all books</a>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:forEach items="${books}" var="book">
                                                                <a href="/books/${book.id}" class="product-card">
                                                                    <div class="product-card__thumb">
                                                                        <c:choose>
                                                                            <c:when test="${not empty book.imageUrl}">
                                                                                <img src="${book.imageUrl}"
                                                                                    alt="${book.title}" />
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <div class="product-card__no-img">
                                                                                    <i class="fa-solid fa-book"></i>
                                                                                    <span>No cover</span>
                                                                                </div>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                        <c:if test="${not empty book.category}">
                                                                            <span
                                                                                class="product-card__cat-badge">${book.category.name}</span>
                                                                        </c:if>
                                                                    </div>
                                                                    <div class="product-card__body">
                                                                        <div class="product-card__title">${book.title}
                                                                        </div>
                                                                        <div class="product-card__author">
                                                                            <c:out value="${book.author.authorName}" default="—" />
                                                                        </div>
                                                                        <div class="product-card__footer">
                                                                            <div class="product-card__price-box">
                                                                                <c:choose>
                                                                                    <c:when
                                                                                        test="${not empty bestPromotionMap[book.id]}">
                                                                                        <div
                                                                                            class="product-card__discount-line">
                                                                                            <span
                                                                                                class="product-card__old-price">
                                                                                                ${book.priceFormatted}
                                                                                                &#8363;
                                                                                            </span>
                                                                                            <span
                                                                                                class="product-card__discount-badge">
                                                                                                ${discountLabelMap[book.id]}
                                                                                            </span>
                                                                                        </div>
                                                                                        <span
                                                                                            class="product-card__price product-card__price--sale">
                                                                                            ${discountedPriceFormattedMap[book.id]}
                                                                                            &#8363;
                                                                                        </span>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <span
                                                                                            class="product-card__price">
                                                                                            ${book.priceFormatted}
                                                                                            &#8363;
                                                                                        </span>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </div>
                                                                            <span
                                                                                class="product-card__link">Details</span>
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