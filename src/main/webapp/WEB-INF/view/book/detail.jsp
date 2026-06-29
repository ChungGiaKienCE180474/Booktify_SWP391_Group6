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
    <title>${book.title} — Booktify</title>
    <style>
        /* ── Breadcrumb ── */
        .breadcrumb {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1rem 1.5rem .25rem;
            display: flex;
            align-items: center;
            gap: .45rem;
            font-size: .8rem;
            color: var(--text-muted);
            flex-wrap: wrap;
        }
        .breadcrumb a { color: var(--text-muted); text-decoration: none; transition: color .12s; }
        .breadcrumb a:hover { color: var(--primary); }
        .breadcrumb i { font-size: .6rem; opacity: .4; }

        /* ── Detail layout ── */
        .detail-wrap {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1.25rem 1.5rem 2.5rem;
            display: grid;
            grid-template-columns: 280px 1fr;
            gap: 2.5rem;
            align-items: start;
        }

        /* ── Left: cover ── */
        .detail-cover {
            position: sticky;
            top: 112px;
        }
        .detail-cover__frame {
            width: 100%;
            aspect-ratio: 2/3;
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: 0 16px 48px rgba(0,0,0,.18);
            background: linear-gradient(135deg, #ECEFF1, #CFD8DC);
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .detail-cover__frame img { width: 100%; height: 100%; object-fit: cover; display: block; }
        .detail-cover__placeholder {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: .65rem;
            color: #90A4AE;
        }
        .detail-cover__placeholder i { font-size: 4.5rem; }
        .detail-cover__placeholder span { font-size: .7rem; font-weight: 700; text-transform: uppercase; letter-spacing: .1em; }
        .detail-cover__back {
            display: inline-flex;
            align-items: center;
            gap: .4rem;
            margin-top: 1rem;
            font-size: .83rem;
            font-weight: 600;
            color: var(--text-muted);
            text-decoration: none;
            transition: color .12s;
        }
        .detail-cover__back:hover { color: var(--primary); }

        /* ── Right: info ── */
        .detail-info__cat-link {
            display: inline-flex;
            align-items: center;
            gap: .35rem;
            font-size: .72rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: .08em;
            color: #fff;
            background: var(--primary);
            padding: .25rem .7rem;
            border-radius: 4px;
            text-decoration: none;
            margin-bottom: .85rem;
            transition: background .12s;
        }
        .detail-info__cat-link:hover { background: var(--primary-light); }
        .detail-info__title {
            font-size: 1.85rem;
            font-weight: 800;
            line-height: 1.2;
            color: var(--text);
            margin: 0 0 .35rem;
        }
        .detail-info__author {
            font-size: .95rem;
            color: var(--text-muted);
            margin-bottom: 1.25rem;
        }
        .detail-info__author strong { color: var(--text); font-weight: 700; }

        /* Price row */
        .detail-price-row {
            display: flex;
            align-items: center;
            gap: 1.25rem;
            margin-bottom: 1.5rem;
            padding: 1rem 1.25rem;
            background: #F5F7FA;
            border-radius: var(--radius);
            border: 1px solid var(--border);
            flex-wrap: wrap;
        }
        .detail-price {
            font-size: 2rem;
            font-weight: 800;
            color: var(--accent-warm, #F57C00);
        }
        .detail-stock {
            display: inline-flex;
            align-items: center;
            gap: .4rem;
            font-size: .82rem;
            font-weight: 700;
            padding: .35rem .85rem;
            border-radius: 999px;
        }
        .detail-stock--in  { background: rgba(27,136,44,.1); color: #1b882c; }
        .detail-stock--out { background: rgba(198,40,40,.08); color: #C62828; }

        /* Action buttons */
        .detail-actions {
            display: flex;
            gap: .75rem;
            margin-bottom: 1.75rem;
            flex-wrap: wrap;
            align-items: flex-end;
        }
        .detail-cart-form {
            display: flex;
            flex-wrap: wrap;
            gap: .75rem;
            align-items: flex-end;
            flex: 1;
        }
        .detail-qty-label {
            font-size: .8rem;
            font-weight: 600;
            color: var(--text-muted);
            display: block;
            margin-bottom: .25rem;
        }
        .detail-qty-input {
            width: 80px;
            padding: .65rem .75rem;
            border: 1px solid var(--border);
            border-radius: var(--radius);
        }
        .btn-detail-primary {
            flex: 1;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: .5rem;
            padding: .85rem 1.5rem;
            background: var(--primary);
            color: #fff;
            border: none;
            border-radius: var(--radius);
            font-size: .95rem;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            transition: background .15s, transform .15s, box-shadow .15s;
            min-width: 160px;
        }
        .btn-detail-primary:hover {
            background: var(--primary-light);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0,107,94,.3);
        }
        .btn-detail-secondary {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: .5rem;
            padding: .85rem 1.5rem;
            background: transparent;
            color: var(--primary);
            border: 2px solid var(--primary);
            border-radius: var(--radius);
            font-size: .95rem;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            transition: background .15s, transform .15s;
        }
        .btn-detail-secondary:hover { background: rgba(0,107,94,.07); transform: translateY(-1px); }

        /* Meta chips */
        .detail-meta {
            display: flex;
            flex-wrap: wrap;
            gap: .6rem;
            padding-bottom: 1.25rem;
            border-bottom: 1px solid var(--border);
            margin-bottom: 1.25rem;
        }
        .meta-chip {
            display: inline-flex;
            align-items: center;
            gap: .35rem;
            font-size: .78rem;
            font-weight: 600;
            padding: .28rem .7rem;
            background: #fff;
            border: 1px solid var(--border);
            border-radius: 6px;
            color: var(--text-muted);
        }
        .meta-chip i { font-size: .7rem; color: var(--primary); }

        /* Description */
        .detail-desc-label {
            font-size: .72rem;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: .1em;
            color: var(--text-muted);
            margin-bottom: .5rem;
        }
        .detail-desc {
            font-size: .93rem;
            line-height: 1.8;
            color: var(--text);
        }

        /* ── Suggested ── */
        .suggested-wrap {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 1.5rem 3.5rem;
        }
        .suggested-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(165px, 1fr));
            gap: 1rem;
        }
        .s-card {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: var(--radius);
            overflow: hidden;
            display: flex;
            flex-direction: column;
            cursor: pointer;
            text-decoration: none;
            color: inherit;
            transition: transform .2s, box-shadow .2s;
        }
        .s-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-lg); }
        .s-card__thumb {
            height: 190px;
            overflow: hidden;
            background: linear-gradient(135deg, #ECEFF1, #CFD8DC);
            flex-shrink: 0;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .s-card__thumb img { width:100%;height:100%;object-fit:cover;transition:transform .3s; }
        .s-card:hover .s-card__thumb img { transform:scale(1.06); }
        .s-card__placeholder { color:#90A4AE;display:flex;flex-direction:column;align-items:center;gap:.35rem; }
        .s-card__placeholder i { font-size:2.5rem; }
        .s-card__body { padding:.65rem .8rem .8rem;flex:1;display:flex;flex-direction:column; }
        .s-card__title { font-size:.85rem;font-weight:700;line-height:1.3;flex:1;color:var(--text);display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;margin-bottom:.2rem; }
        .s-card__author { font-size:.72rem;color:var(--text-muted);margin-bottom:.55rem; }
        .s-card__price { font-size:.95rem;font-weight:800;color:var(--accent-warm,#F57C00); }

        @media(max-width:768px){
            .detail-wrap { grid-template-columns:1fr;gap:1.5rem; }
            .detail-cover { position:static; }
            .detail-cover__frame { max-width:220px;margin:0 auto; }
            .detail-info__title { font-size:1.45rem; }
        }
    </style>
</head>
<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <%-- Breadcrumb --%>
    <nav class="breadcrumb">
        <a href="/">Trang chủ</a>
        <i class="fa-solid fa-chevron-right"></i>
        <a href="/books">Sách</a>
        <c:if test="${not empty book.category}">
            <i class="fa-solid fa-chevron-right"></i>
            <a href="/books?categoryId=${book.category.id}">${book.category.name}</a>
        </c:if>
        <i class="fa-solid fa-chevron-right"></i>
        <span>${book.title}</span>
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

    <%-- Detail --%>
    <div class="detail-wrap">

        <%-- Cover --%>
        <aside class="detail-cover">
            <div class="detail-cover__frame">
                <c:choose>
                    <c:when test="${not empty book.imageUrl}">
                        <img src="${book.imageUrl}" alt="${book.title}" />
                    </c:when>
                    <c:otherwise>
                        <div class="detail-cover__placeholder">
                            <i class="fa-solid fa-book"></i>
                            <span>No cover</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <a href="/books" class="detail-cover__back">
                <i class="fa-solid fa-arrow-left"></i> Quay lại danh sách
            </a>
        </aside>

        <%-- Info --%>
        <div class="detail-info">
            <c:if test="${not empty book.category}">
                <a href="/books?categoryId=${book.category.id}" class="detail-info__cat-link">
                    <i class="fa-solid fa-tag"></i> ${book.category.name}
                </a>
            </c:if>

            <h1 class="detail-info__title">${book.title}</h1>
            <p class="detail-info__author">Tác giả: <strong>${book.author}</strong></p>

            <div class="detail-price-row">
                <div class="detail-price">${book.price}</div>
                <c:choose>
                    <c:when test="${book.stockQuantity > 0}">
                        <span class="detail-stock detail-stock--in">
                            <i class="fa-solid fa-circle-check"></i> Còn hàng (${book.stockQuantity})
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="detail-stock detail-stock--out">
                            <i class="fa-solid fa-circle-xmark"></i> Hết hàng
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- Action buttons --%>
            <div class="detail-actions">
                <c:choose>
                    <c:when test="${book.active and book.stockQuantity > 0}">
                        <c:choose>
                            <c:when test="${not empty sessionScope.username}">
                                <form action="/cart/add" method="post" class="detail-cart-form">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <input type="hidden" name="bookId" value="${book.id}" />
                                    <input type="hidden" name="redirect" value="/books/${book.id}" />
                                    <label class="detail-qty-label" for="cartQty">Số lượng</label>
                                    <input type="number" id="cartQty" name="quantity" value="1"
                                           min="1" max="${book.stockQuantity}" class="detail-qty-input" required />
                                    <button type="submit" class="btn-detail-primary">
                                        <i class="fa-solid fa-cart-shopping"></i> Thêm vào giỏ hàng
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <a href="/login" class="btn-detail-primary">
                                    <i class="fa-solid fa-right-to-bracket"></i> Đăng nhập để mua
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <button type="button" class="btn-detail-primary" disabled style="opacity:.6;cursor:not-allowed;">
                            <i class="fa-solid fa-cart-shopping"></i> Không thể thêm vào giỏ
                        </button>
                    </c:otherwise>
                </c:choose>
                <a href="/cart" class="btn-detail-secondary">
                    <i class="fa-solid fa-basket-shopping"></i> Xem giỏ hàng
                </a>
            </div>

            <%-- Meta chips --%>
            <div class="detail-meta">
                <c:if test="${not empty book.isbn}">
                    <span class="meta-chip"><i class="fa-solid fa-barcode"></i> ISBN: ${book.isbn}</span>
                </c:if>
                <c:if test="${not empty book.category}">
                    <span class="meta-chip"><i class="fa-solid fa-layer-group"></i> ${book.category.name}</span>
                </c:if>
                <span class="meta-chip"><i class="fa-solid fa-boxes-stacked"></i> Tồn kho: ${book.stockQuantity}</span>
            </div>

            <%-- Description --%>
            <div class="detail-desc-label">Giới thiệu sách</div>
            <c:choose>
                <c:when test="${not empty book.description}">
                    <p class="detail-desc">${book.description}</p>
                </c:when>
                <c:otherwise>
                    <p class="detail-desc" style="color:var(--text-muted);font-style:italic;">Chưa có mô tả cho cuốn sách này.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <%-- Suggested books --%>
    <c:if test="${not empty suggestedBooks}">
        <section class="suggested-wrap">
            <div class="sec-head">
                <div class="sec-head__left">
                    <div class="sec-head__icon"><i class="fa-solid fa-wand-magic-sparkles"></i></div>
                    <h2 class="sec-head__title">Có thể bạn quan tâm</h2>
                </div>
                <a href="/books" class="sec-head__link">Xem thêm <i class="fa-solid fa-chevron-right"></i></a>
            </div>
            <div class="suggested-grid">
                <c:forEach items="${suggestedBooks}" var="s">
                    <a href="/books/${s.id}" class="s-card">
                        <div class="s-card__thumb">
                            <c:choose>
                                <c:when test="${not empty s.imageUrl}">
                                    <img src="${s.imageUrl}" alt="${s.title}" />
                                </c:when>
                                <c:otherwise>
                                    <div class="s-card__placeholder"><i class="fa-solid fa-book"></i></div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="s-card__body">
                            <div class="s-card__title">${s.title}</div>
                            <div class="s-card__author">${s.author}</div>
                            <div class="s-card__price">${s.price}</div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
</body>
</html>
