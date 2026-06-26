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
    <link rel="stylesheet" href="/css/homepage.css" />
    <title>
        <c:choose>
            <c:when test="${not empty q}">Kết quả: "${q}"</c:when>
            <c:when test="${not empty selectedCategory}">${selectedCategory.name}</c:when>
            <c:otherwise>Tất cả sách</c:otherwise>
        </c:choose>
        — Booktify
    </title>
    <style>
        /* ── Shop layout ── */
        .shop-wrap {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1.5rem 1.5rem 3.5rem;
            display: grid;
            grid-template-columns: 220px 1fr;
            gap: 1.5rem;
            align-items: start;
        }

        /* ── Sidebar ── */
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
        .sidebar-list { padding: .5rem 0; }
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
        .sidebar-item:hover { background: rgba(0,107,94,.06); color: var(--primary); }
        .sidebar-item.active {
            background: rgba(0,107,94,.08);
            color: var(--primary);
            font-weight: 700;
        }
        .sidebar-item .dot {
            width: 7px; height: 7px;
            border-radius: 50%;
            background: var(--border);
            flex-shrink: 0;
            transition: background .12s;
        }
        .sidebar-item.active .dot,
        .sidebar-item:hover .dot { background: var(--primary); }

        /* ── Main area ── */
        .shop-main {}

        /* ── Breadcrumb ── */
        .shop-breadcrumb {
            display: flex;
            align-items: center;
            gap: .45rem;
            font-size: .8rem;
            color: var(--text-muted);
            margin-bottom: 1rem;
        }
        .shop-breadcrumb a { color: var(--text-muted); text-decoration: none; transition: color .12s; }
        .shop-breadcrumb a:hover { color: var(--primary); }
        .shop-breadcrumb i { font-size: .6rem; opacity: .4; }

        /* ── Toolbar ── */
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
        .shop-toolbar__right { display: flex; align-items: center; gap: .6rem; flex-wrap: wrap; }

        /* Sort select */
        .sort-label {
            font-size: .8rem;
            color: var(--text-muted);
            font-weight: 600;
            white-space: nowrap;
        }
        .sort-select {
            padding: .4rem .75rem;
            border: 1px solid var(--border);
            border-radius: var(--radius);
            font-size: .85rem;
            font-family: inherit;
            color: var(--text);
            background: #fff;
            cursor: pointer;
            outline: none;
            transition: border-color .15s;
        }
        .sort-select:focus { border-color: var(--primary); }

        /* ── Product grid ── */
        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
            gap: 1rem;
        }

        /* ── Product card (extends .book-card from homepage.css) ── */
        .product-card {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: var(--radius);
            overflow: hidden;
            display: flex;
            flex-direction: column;
            transition: transform .22s, box-shadow .22s;
            cursor: pointer;
            text-decoration: none;
            color: inherit;
        }
        .product-card:hover { transform: translateY(-5px); box-shadow: var(--shadow-lg); }
        .product-card__thumb {
            position: relative;
            height: 210px;
            background: linear-gradient(135deg, #ECEFF1, #CFD8DC);
            overflow: hidden;
            flex-shrink: 0;
        }
        .product-card__thumb img { width:100%;height:100%;object-fit:cover;transition:transform .32s; }
        .product-card:hover .product-card__thumb img { transform:scale(1.06); }
        .product-card__no-img {
            width:100%;height:100%;display:flex;flex-direction:column;
            align-items:center;justify-content:center;gap:.35rem;color:#90A4AE;
        }
        .product-card__no-img i { font-size:3rem; }
        .product-card__no-img span { font-size:.65rem;font-weight:700;text-transform:uppercase;letter-spacing:.1em; }
        .product-card__cat-badge {
            position:absolute;top:.45rem;left:.45rem;
            background:var(--primary);color:#fff;
            font-size:.62rem;font-weight:800;
            padding:.18rem .5rem;border-radius:4px;
        }
        .product-card__body { padding:.65rem .8rem .85rem;flex:1;display:flex;flex-direction:column; }
        .product-card__cat { font-size:.67rem;font-weight:700;text-transform:uppercase;letter-spacing:.07em;color:var(--primary-light);margin-bottom:.2rem; }
        .product-card__title {
            font-size:.88rem;font-weight:700;line-height:1.35;color:var(--text);
            flex:1;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;
            margin-bottom:.2rem;
        }
        .product-card__author { font-size:.73rem;color:var(--text-muted);margin-bottom:.6rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis; }
        .product-card__footer { display:flex;align-items:center;justify-content:space-between;gap:.5rem;margin-top:auto; }
        .product-card__price { font-size:1.05rem;font-weight:800;color:var(--accent-warm,#F57C00);white-space:nowrap; }
        .product-card__link {
            display:inline-flex;align-items:center;justify-content:center;
            padding:.35rem .7rem;border-radius:6px;
            background:var(--primary);color:#fff;font-size:.72rem;font-weight:700;
            text-decoration:none;transition:background .15s,transform .15s;white-space:nowrap;cursor:pointer;
        }
        .product-card__link:hover { background:var(--primary-light);transform:scale(1.04); }

        /* ── Search result keyword highlight ── */
        .search-result-bar {
            background: rgba(0,107,94,.07);
            border: 1px solid rgba(0,107,94,.18);
            border-radius: var(--radius);
            padding: .6rem 1rem;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: .75rem;
            flex-wrap: wrap;
        }
        .search-result-bar span { font-size: .88rem; color: var(--primary); font-weight: 600; }
        .search-result-bar a { font-size: .8rem; color: var(--text-muted); text-decoration: none; }
        .search-result-bar a:hover { color: var(--accent-red,#E53935); }

        /* ── Empty state ── */
        .empty-state {
            grid-column: 1/-1;
            text-align: center;
            padding: 4rem 1.5rem;
            color: var(--text-muted);
        }
        .empty-state i { font-size: 3.5rem; display: block; margin-bottom: 1rem; opacity: .25; }
        .empty-state p { font-size: 1.05rem; margin: 0 0 1.25rem; }
        .empty-state a {
            display: inline-flex; align-items: center; gap: .45rem;
            padding: .7rem 1.5rem; border-radius: var(--radius);
            background: var(--primary); color: #fff; font-weight: 700; text-decoration: none;
            transition: background .15s;
        }
        .empty-state a:hover { background: var(--primary-light); }

        @media(max-width:768px){
            .shop-wrap { grid-template-columns: 1fr; }
            .shop-sidebar { position: static; display: flex; flex-wrap: wrap; }
            .sidebar-head { width: 100%; }
            .sidebar-list { display: flex; flex-wrap: wrap; padding: .25rem; width: 100%; }
            .sidebar-item { padding: .4rem .7rem; border-radius: var(--radius); }
            .product-grid { grid-template-columns: repeat(auto-fill, minmax(145px,1fr)); gap: .75rem; }
        }
    </style>
</head>
<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <div class="shop-wrap">

        <%-- ── Sidebar ── --%>
        <aside class="shop-sidebar">
            <div class="sidebar-head"><i class="fa-solid fa-bars"></i> Danh mục sách</div>
            <div class="sidebar-list">
                <a href="/books" class="sidebar-item ${empty selectedCategoryId && empty q ? 'active' : ''}">
                    <span class="dot"></span> Tất cả sách
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
        </aside>

        <%-- ── Main ── --%>
        <main class="shop-main">

            <%-- Breadcrumb --%>
            <nav class="shop-breadcrumb">
                <a href="/">Trang chủ</a>
                <i class="fa-solid fa-chevron-right"></i>
                <a href="/books">Sách</a>
                <c:if test="${not empty selectedCategory}">
                    <i class="fa-solid fa-chevron-right"></i>
                    <span>${selectedCategory.name}</span>
                </c:if>
                <c:if test="${not empty q}">
                    <i class="fa-solid fa-chevron-right"></i>
                    <span>Tìm: "${q}"</span>
                </c:if>
            </nav>

            <%-- Search result bar --%>
            <c:if test="${not empty q}">
                <div class="search-result-bar">
                    <span><i class="fa-solid fa-magnifying-glass"></i>
                        Kết quả tìm kiếm cho: "<strong>${q}</strong>"
                        — ${empty books ? 0 : books.size()} sách
                    </span>
                    <a href="/books"><i class="fa-solid fa-xmark"></i> Xoá tìm kiếm</a>
                </div>
            </c:if>

            <%-- Toolbar --%>
            <div class="shop-toolbar">
                <div class="shop-toolbar__left">
                    <h2>
                        <c:choose>
                            <c:when test="${not empty selectedCategory}">${selectedCategory.name}</c:when>
                            <c:when test="${not empty q}">Kết quả tìm kiếm</c:when>
                            <c:otherwise>Tất cả sách</c:otherwise>
                        </c:choose>
                    </h2>
                    <p>${empty books ? 0 : books.size()} sản phẩm</p>
                </div>
                <div class="shop-toolbar__right">
                    <span class="sort-label">Sắp xếp:</span>
                    <form method="get" action="/books" id="sortForm" style="display:contents;">
                        <c:if test="${not empty selectedCategoryId}">
                            <input type="hidden" name="categoryId" value="${selectedCategoryId}" />
                        </c:if>
                        <c:if test="${not empty q}">
                            <input type="hidden" name="q" value="${q}" />
                        </c:if>
                        <select name="sort" class="sort-select" onchange="document.getElementById('sortForm').submit();">
                            <option value="default"     ${sort == 'default'    ? 'selected' : ''}>Mặc định</option>
                            <option value="price_asc"   ${sort == 'price_asc'  ? 'selected' : ''}>Giá tăng dần</option>
                            <option value="price_desc"  ${sort == 'price_desc' ? 'selected' : ''}>Giá giảm dần</option>
                        </select>
                    </form>
                </div>
            </div>

            <%-- Product grid --%>
            <div class="product-grid">
                <c:choose>
                    <c:when test="${empty books}">
                        <div class="empty-state">
                            <i class="fa-solid fa-box-open"></i>
                            <p>Không tìm thấy sách nào.</p>
                            <a href="/books"><i class="fa-solid fa-arrow-left"></i> Xem tất cả sách</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${books}" var="book">
                            <a href="/books/${book.id}" class="product-card">
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
                                    <div class="product-card__author">${book.author}</div>
                                    <div class="product-card__footer">
                                        <span class="product-card__price">${book.priceFormatted} &#8363;</span>
                                        <span class="product-card__link">Chi tiết</span>
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
