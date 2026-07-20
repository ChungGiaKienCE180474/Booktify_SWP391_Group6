<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>Stationery — Booktify</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="${ctx}/css/header.css" />
    <link rel="stylesheet" href="${ctx}/css/footer.css" />
    <link rel="stylesheet" href="${ctx}/css/homepage.css" />

    <style>
        .shop-wrap {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1.5rem 1.5rem 3.5rem;
            display: grid;
            grid-template-columns: 220px 1fr;
            gap: 1.5rem;
            align-items: start;
        }

        .shop-sidebar {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: var(--shadow-sm);
            position: sticky;
            top: 112px;
        }

        .sidebar-head {
            background: var(--primary);
            color: #fff;
            padding: .75rem 1rem;
            font-size: .8rem;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: .08em;
            display: flex;
            align-items: center;
            gap: .5rem;
        }

        .sidebar-list {
            padding: .5rem 0;
        }

        .sidebar-item {
            display: flex;
            align-items: center;
            gap: .55rem;
            padding: .55rem 1rem;
            text-decoration: none;
            color: var(--text-muted);
            font-size: .875rem;
            font-weight: 500;
            transition: background .12s, color .12s;
            cursor: pointer;
        }

        .sidebar-item:hover {
            background: rgba(0, 107, 94, .06);
            color: var(--primary);
        }

        .sidebar-item.active {
            background: rgba(0, 107, 94, .08);
            color: var(--primary);
            font-weight: 700;
        }

        .sidebar-item .dot {
            width: 7px;
            height: 7px;
            border-radius: 50%;
            background: var(--border);
            flex-shrink: 0;
            transition: background .12s;
        }

        .sidebar-item.active .dot,
        .sidebar-item:hover .dot {
            background: var(--primary);
        }

        .shop-breadcrumb {
            display: flex;
            align-items: center;
            gap: .45rem;
            font-size: .8rem;
            color: var(--text-muted);
            margin-bottom: 1rem;
        }

        .shop-breadcrumb a {
            color: var(--text-muted);
            text-decoration: none;
            transition: color .12s;
        }

        .shop-breadcrumb a:hover {
            color: var(--primary);
        }

        .shop-breadcrumb i {
            font-size: .6rem;
            opacity: .4;
        }

        .search-result-bar {
            background: rgba(0, 107, 94, .07);
            border: 1px solid rgba(0, 107, 94, .18);
            border-radius: var(--radius);
            padding: .6rem 1rem;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: .75rem;
            flex-wrap: wrap;
        }

        .search-result-bar span {
            font-size: .88rem;
            color: var(--primary);
            font-weight: 600;
        }

        .search-result-bar a {
            font-size: .8rem;
            color: var(--text-muted);
            text-decoration: none;
        }

        .search-result-bar a:hover {
            color: var(--accent-red, #E53935);
        }

        .shop-toolbar {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: .75rem 1rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: .75rem;
            flex-wrap: wrap;
            margin-bottom: 1.25rem;
            box-shadow: var(--shadow-sm);
        }

        .shop-toolbar__left h2 {
            margin: 0;
            font-size: 1.1rem;
            font-weight: 800;
            color: var(--primary);
        }

        .shop-toolbar__left p {
            margin: .1rem 0 0;
            font-size: .78rem;
            color: var(--text-muted);
        }

        .shop-toolbar__right {
            display: flex;
            align-items: center;
            gap: .6rem;
            flex-wrap: wrap;
        }

        .shop-sort-form {
            display: flex;
            align-items: center;
            gap: .5rem;
        }

        .shop-sort-form label {
            font-size: .78rem;
            color: var(--text-muted);
            font-weight: 700;
        }

        .shop-sort-select {
            height: 34px;
            border: 1px solid var(--border);
            border-radius: 6px;
            padding: 0 .7rem;
            background: #fff;
            color: var(--text);
            font-size: .78rem;
            font-weight: 600;
            outline: none;
            cursor: pointer;
        }

        .shop-sort-select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(0, 107, 94, .1);
        }

        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
            gap: 1rem;
        }

        .product-card {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: var(--radius);
            overflow: hidden;
            display: flex;
            flex-direction: column;
            height: 345px;
            transition: transform .22s, box-shadow .22s;
            cursor: pointer;
            text-decoration: none;
            color: inherit;
        }

        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: var(--shadow-lg);
        }

        .product-card__thumb {
            position: relative;
            height: 180px;
            background: linear-gradient(135deg, #ECEFF1, #CFD8DC);
            overflow: hidden;
            flex-shrink: 0;
        }

        .product-card__thumb img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform .32s;
        }

        .product-card:hover .product-card__thumb img {
            transform: scale(1.06);
        }

        .product-card__no-img {
            width: 100%;
            height: 100%;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: .35rem;
            color: #90A4AE;
        }

        .product-card__no-img i {
            font-size: 3rem;
        }

        .product-card__no-img span {
            font-size: .65rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: .1em;
        }

        .product-card__cat-badge {
            position: absolute;
            top: .45rem;
            left: .45rem;
            background: var(--primary);
            color: #fff;
            font-size: .6rem;
            font-weight: 800;
            padding: .16rem .45rem;
            border-radius: 4px;
            max-width: calc(100% - .9rem);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .product-card__body {
            padding: .6rem .75rem .7rem;
            flex: 1;
            min-width: 0;
            min-height: 0;
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }

        .product-card__title {
            font-size: .84rem;
            font-weight: 700;
            line-height: 1.3;
            color: var(--text);
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            margin-bottom: .2rem;
            min-height: calc(1.3em * 2);
        }

        .product-card__author {
            font-size: .7rem;
            color: var(--text-muted);
            margin-bottom: .5rem;
            line-height: 1.35;
            min-height: calc(1.35em * 2);
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .product-card__footer {
            display: flex;
            flex-direction: column;
            gap: .4rem;
            margin-top: auto;
        }

        .product-card__price {
            font-size: .88rem;
            font-weight: 800;
            color: var(--accent-warm, #F57C00);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            width: 100%;
        }

        .product-card__link {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            height: 30px;
            box-sizing: border-box;
            border-radius: 6px;
            background: var(--primary);
            color: #fff;
            font-size: .68rem;
            font-weight: 700;
            text-decoration: none;
            transition: background .15s, transform .15s;
            white-space: nowrap;
            cursor: pointer;
        }

        .product-card__link:hover {
            background: var(--primary-light);
            transform: scale(1.02);
        }

        .empty-state {
            grid-column: 1/-1;
            text-align: center;
            padding: 4rem 1.5rem;
            color: var(--text-muted);
        }

        .empty-state i {
            font-size: 3.5rem;
            display: block;
            margin-bottom: 1rem;
            opacity: .25;
        }

        .empty-state p {
            font-size: 1.05rem;
            margin: 0 0 1.25rem;
        }

        .empty-state a {
            display: inline-flex;
            align-items: center;
            gap: .45rem;
            padding: .7rem 1.5rem;
            border-radius: var(--radius);
            background: var(--primary);
            color: #fff;
            font-weight: 700;
            text-decoration: none;
            transition: background .15s;
        }

        .empty-state a:hover {
            background: var(--primary-light);
        }

        .vpp-pagination {
            margin-top: 1.5rem;
            display: flex;
            justify-content: center;
            gap: .45rem;
            flex-wrap: wrap;
        }

        .vpp-page-btn {
            min-width: 34px;
            height: 34px;
            padding: 0 .65rem;
            border-radius: 6px;
            border: 1px solid var(--border);
            background: #fff;
            color: var(--text-muted);
            text-decoration: none;
            font-size: .78rem;
            font-weight: 700;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .vpp-page-btn.active,
        .vpp-page-btn:hover {
            background: var(--primary);
            color: #fff;
            border-color: var(--primary);
        }

        @media(max-width:768px) {
            .shop-wrap {
                grid-template-columns: 1fr;
            }

            .shop-sidebar {
                position: static;
                display: flex;
                flex-wrap: wrap;
            }

            .sidebar-head {
                width: 100%;
            }

            .sidebar-list {
                display: flex;
                flex-wrap: wrap;
                padding: .25rem;
                width: 100%;
            }

            .sidebar-item {
                padding: .4rem .7rem;
                border-radius: var(--radius);
            }

            .product-grid {
                grid-template-columns: repeat(auto-fill, minmax(145px, 1fr));
                gap: .75rem;
            }

            .product-card {
                height: 320px;
            }

            .product-card__thumb {
                height: 155px;
            }
        }
    </style>
</head>

<body class="home-page">

<jsp:include page="/WEB-INF/view/layout/header.jsp" />

<div class="shop-wrap">

    <aside class="shop-sidebar">
        <div class="sidebar-head">
            <i class="fa-solid fa-bars"></i>
            VPP categories
        </div>

        <div class="sidebar-list">

            <c:url var="allVppUrl" value="/customer/vpp">
                <c:if test="${not empty q}">
                    <c:param name="q" value="${q}" />
                </c:if>
                <c:param name="stock" value="${stock}" />
                <c:param name="sort" value="${sort}" />
                <c:param name="size" value="${size}" />
            </c:url>

            <a href="${allVppUrl}"
               class="sidebar-item ${empty selectedCategoryId ? 'active' : ''}">
                <span class="dot"></span>
                All products
            </a>

            <c:forEach items="${vppCategories}" var="category">
                <c:url var="categoryUrl" value="/customer/vpp">
                    <c:if test="${not empty q}">
                        <c:param name="q" value="${q}" />
                    </c:if>
                    <c:param name="categoryId" value="${category.id}" />
                    <c:param name="stock" value="${stock}" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="size" value="${size}" />
                </c:url>

                <a href="${categoryUrl}"
                   class="sidebar-item ${selectedCategoryId == category.id ? 'active' : ''}">
                    <span class="dot"></span>
                    <c:out value="${category.name}" />
                </a>
            </c:forEach>

        </div>
    </aside>

    <main class="shop-main">

        <nav class="shop-breadcrumb">
            <a href="${ctx}/">Home</a>
            <i class="fa-solid fa-chevron-right"></i>
            <a href="${ctx}/customer/vpp">Stationery</a>

            <c:if test="${not empty q}">
                <i class="fa-solid fa-chevron-right"></i>
                <span>Search: "<c:out value="${q}" />"</span>
            </c:if>
        </nav>

        <c:if test="${not empty q}">
            <div class="search-result-bar">
                <span>
                    <i class="fa-solid fa-magnifying-glass"></i>
                    Search results for:
                    "<strong><c:out value="${q}" /></strong>"
                    — ${totalItems} product(s)
                </span>

                <a href="${ctx}/customer/vpp">
                    <i class="fa-solid fa-xmark"></i>
                    Clear search
                </a>
            </div>
        </c:if>

        <div class="shop-toolbar">
            <div class="shop-toolbar__left">
                <h2>Stationery</h2>
                <p>${totalItems} product(s)</p>
            </div>

            <div class="shop-toolbar__right">
                <form method="get"
                      action="${ctx}/customer/vpp"
                      class="shop-sort-form">

                    <c:if test="${not empty q}">
                        <input type="hidden" name="q" value="${q}" />
                    </c:if>

                    <c:if test="${not empty selectedCategoryId}">
                        <input type="hidden" name="categoryId" value="${selectedCategoryId}" />
                    </c:if>

                    <input type="hidden" name="stock" value="${stock}" />
                    <input type="hidden" name="size" value="${size}" />

                    <label for="sort">Sort:</label>

                    <select id="sort"
                            name="sort"
                            class="shop-sort-select"
                            onchange="this.form.submit()">

                        <option value="newest" ${sort == 'newest' ? 'selected' : ''}>
                            Newest
                        </option>

                        <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>
                            Price: Low to High
                        </option>

                        <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>
                            Price: High to Low
                        </option>

                        <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>
                            Name: A to Z
                        </option>
                    </select>
                </form>
            </div>
        </div>

        <div class="product-grid">
            <c:choose>
                <c:when test="${empty items}">
                    <div class="empty-state">
                        <i class="fa-solid fa-box-open"></i>
                        <p>No stationery products found.</p>

                        <a href="${ctx}/customer/vpp">
                            <i class="fa-solid fa-arrow-left"></i>
                            View all products
                        </a>
                    </div>
                </c:when>

                <c:otherwise>
                    <c:forEach items="${items}" var="item">
                        <a href="${ctx}/customer/vpp/${item.id}" class="product-card">

                            <div class="product-card__thumb">
                                <c:choose>
                                    <c:when test="${item.hasImage}">
                                        <img src="${ctx}/uploads/vpp/${item.id}/image"
                                             alt="${item.name}" />
                                    </c:when>

                                    <c:when test="${not empty item.imagePath}">
                                        <img src="${ctx}${item.imagePath}"
                                             alt="${item.name}" />
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
                                        <c:otherwise>
                                            Stationery products at Booktify.
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="product-card__footer">
                                    <span class="product-card__price">
                                        <fmt:formatNumber value="${item.price}"
                                                          type="number"
                                                          groupingUsed="true" />
                                        đ
                                    </span>

                                    <span class="product-card__link">
                                        Details
                                    </span>
                                </div>
                            </div>

                        </a>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${totalPages > 1}">
            <div class="vpp-pagination">

                <c:if test="${currentPage > 0}">
                    <c:url var="prevPageUrl" value="/customer/vpp">
                        <c:param name="page" value="${currentPage - 1}" />

                        <c:if test="${not empty q}">
                            <c:param name="q" value="${q}" />
                        </c:if>

                        <c:if test="${not empty selectedCategoryId}">
                            <c:param name="categoryId" value="${selectedCategoryId}" />
                        </c:if>

                        <c:param name="stock" value="${stock}" />
                        <c:param name="sort" value="${sort}" />
                        <c:param name="size" value="${size}" />
                    </c:url>

                    <a class="vpp-page-btn" href="${prevPageUrl}">
                        Previous
                    </a>
                </c:if>

                <c:forEach begin="0" end="${totalPages - 1}" var="i">
                    <c:url var="pageUrl" value="/customer/vpp">
                        <c:param name="page" value="${i}" />

                        <c:if test="${not empty q}">
                            <c:param name="q" value="${q}" />
                        </c:if>

                        <c:if test="${not empty selectedCategoryId}">
                            <c:param name="categoryId" value="${selectedCategoryId}" />
                        </c:if>

                        <c:param name="stock" value="${stock}" />
                        <c:param name="sort" value="${sort}" />
                        <c:param name="size" value="${size}" />
                    </c:url>

                    <a class="vpp-page-btn ${i == currentPage ? 'active' : ''}"
                       href="${pageUrl}">
                        ${i + 1}
                    </a>
                </c:forEach>

                <c:if test="${currentPage + 1 < totalPages}">
                    <c:url var="nextPageUrl" value="/customer/vpp">
                        <c:param name="page" value="${currentPage + 1}" />

                        <c:if test="${not empty q}">
                            <c:param name="q" value="${q}" />
                        </c:if>

                        <c:if test="${not empty selectedCategoryId}">
                            <c:param name="categoryId" value="${selectedCategoryId}" />
                        </c:if>

                        <c:param name="stock" value="${stock}" />
                        <c:param name="sort" value="${sort}" />
                        <c:param name="size" value="${size}" />
                    </c:url>

                    <a class="vpp-page-btn" href="${nextPageUrl}">
                        Next
                    </a>
                </c:if>

            </div>
        </c:if>

    </main>
</div>

<jsp:include page="/WEB-INF/view/layout/footer.jsp" />

</body>
</html>