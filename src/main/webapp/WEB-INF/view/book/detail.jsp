<%-- Storefront book detail page: full book info plus a "related books" section. --%>
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
    <link rel="stylesheet" href="/css/rating.css" />
    <title>${book.title} ΓÇö Booktify</title>
    <style>
        /* ΓöÇΓöÇ Breadcrumb ΓöÇΓöÇ */
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

        /* ΓöÇΓöÇ Detail layout ΓöÇΓöÇ */
        .detail-wrap {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1.25rem 1.5rem 2.5rem;
            display: grid;
            grid-template-columns: 280px 1fr;
            gap: 2.5rem;
            align-items: start;
        }

        /* ΓöÇΓöÇ Left: cover ΓöÇΓöÇ */
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

        /* ΓöÇΓöÇ Right: info ΓöÇΓöÇ */
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
        .detail-info__author-link {
            text-decoration: none;
            cursor: pointer;
        }
        .detail-info__author-link strong {
            color: var(--primary);
            text-decoration: underline;
            text-decoration-color: rgba(0,107,94,.35);
            text-underline-offset: 2px;
            transition: color .12s, text-decoration-color .12s;
        }
        .detail-info__author-link:hover strong {
            color: var(--primary-light);
            text-decoration-color: currentColor;
        }

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
            white-space: nowrap;
        }
        .detail-price-stack {
            display: flex;
            flex-direction: column;
            gap: .35rem;
        }
        .detail-discount-line {
            display: flex;
            align-items: center;
            gap: .65rem;
            flex-wrap: wrap;
        }
        .detail-old-price {
            color: #94a3b8;
            font-size: 1rem;
            font-weight: 700;
            text-decoration: line-through;
        }
        .detail-discount-badge {
            display: inline-flex;
            align-items: center;
            padding: .35rem .7rem;
            border-radius: 999px;
            background: #fee2e2;
            color: #dc2626;
            font-size: .78rem;
            font-weight: 800;
        }
        .detail-price--sale {
            color: #dc2626;
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

        /* ΓöÇΓöÇ Suggested ΓöÇΓöÇ */
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
            height: 300px;
            cursor: pointer;
            text-decoration: none;
            color: inherit;
            transition: transform .2s, box-shadow .2s;
        }
        .s-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-lg); }
        .s-card__thumb {
            height: 170px;
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
        .s-card__body { padding:.6rem .75rem .7rem;flex:1;min-width:0;min-height:0;display:flex;flex-direction:column;overflow:hidden; }
        .s-card__title { font-size:.8rem;font-weight:700;line-height:1.3;color:var(--text);display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;margin-bottom:.2rem;min-height:calc(1.3em * 2); }
        .s-card__author { font-size:.7rem;color:var(--text-muted);margin-bottom:.4rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;line-height:1.2;min-height:1.2em;display:block; }
        .s-card__price { font-size:.82rem;font-weight:800;color:var(--accent-warm,#F57C00);white-space:nowrap;overflow:hidden;text-overflow:ellipsis;margin-top:auto; }
        .s-card__price-box { display:flex;flex-direction:column;gap:3px;margin-top:auto; }
        .s-card__discount-line { display:flex;align-items:center;gap:5px;flex-wrap:wrap; }
        .s-card__old-price { color:#94a3b8;font-size:.68rem;font-weight:700;text-decoration:line-through; }
        .s-card__discount-badge { display:inline-flex;padding:2px 6px;border-radius:999px;background:#fee2e2;color:#dc2626;font-size:.62rem;font-weight:800; }
        .s-card__price--sale { color:#dc2626;font-size:.9rem; }

        /* ΓöÇΓöÇ Responsive ΓöÇΓöÇ */
        @media(max-width:768px){
            .detail-wrap { grid-template-columns:1fr;gap:1.5rem; }
            .detail-cover { position:static; }
            .detail-cover__frame { max-width:220px;margin:0 auto; }
            .detail-info__title { font-size:1.45rem; }
            .s-card { height: 270px; }
            .s-card__thumb { height: 145px; }
        }
    </style>
</head>
<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <%-- Breadcrumb --%>
    <nav class="breadcrumb">
        <a href="/">Home</a>
        <i class="fa-solid fa-chevron-right"></i>
        <a href="/books">Books</a>
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
                <i class="fa-solid fa-arrow-left"></i> Back to list
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
            <p class="detail-info__author">
                Author:
                <c:choose>
                    <c:when test="${not empty authorProfile}">
                        <a href="/authors/${authorProfile.authorId}" class="detail-info__author-link">
                            <strong>${book.author}</strong>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <strong>${book.author}</strong>
                    </c:otherwise>
                </c:choose>
            </p>

            <div class="detail-price-row">
                <c:choose>
                    <c:when test="${not empty bestPromotion}">
                        <div class="detail-price-stack">
                            <div class="detail-discount-line">
                                <span class="detail-old-price">
                                    ${book.priceFormatted} &#8363;
                                </span>
                                <span class="detail-discount-badge">
                                    ${discountLabel}
                                </span>
                            </div>
                            <div class="detail-price detail-price--sale">
                                ${discountedPriceFormatted} &#8363;
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="detail-price">
                            ${book.priceFormatted} &#8363;
                        </div>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${book.stockQuantity > 0}">
                        <span class="detail-stock detail-stock--in">
                            <i class="fa-solid fa-circle-check"></i> In stock (${book.stockQuantity})
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="detail-stock detail-stock--out">
                            <i class="fa-solid fa-circle-xmark"></i> Out of stock
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
                                    <label class="detail-qty-label" for="cartQty">Quantity</label>
                                    <input type="number" id="cartQty" name="quantity" value="1"
                                           min="1" max="${book.stockQuantity}" step="1" class="detail-qty-input" required />
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
                        <button type="button" class="btn-detail-primary" disabled style="opacity:.6;cursor:not-allowed;">
                            <i class="fa-solid fa-cart-shopping"></i> Cannot add to cart
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- Meta chips --%>
            <div class="detail-meta">
                <c:if test="${not empty book.isbn}">
                    <span class="meta-chip"><i class="fa-solid fa-barcode"></i> ISBN: ${book.isbn}</span>
                </c:if>
                <c:if test="${not empty book.category}">
                    <span class="meta-chip"><i class="fa-solid fa-layer-group"></i> ${book.category.name}</span>
                </c:if>
                <span class="meta-chip"><i class="fa-solid fa-boxes-stacked"></i> Stock: ${book.stockQuantity}</span>
            </div>

            <%-- Genre tags: independent from Category --%>
            <c:if test="${not empty book.genres}">
                <div class="detail-desc-label" style="margin-top:.4rem;">Genres</div>
                <div class="detail-meta" style="margin-bottom:1.25rem;padding-bottom:0;border-bottom:none;">
                    <c:forEach items="${book.genres}" var="g">
                        <c:if test="${g.active and not g.deleted}">
                            <span class="meta-chip" style="border-color:var(--primary);color:var(--primary);background:rgba(0,107,94,.06);">
                                <i class="fa-solid fa-tags"></i> <c:out value="${g.name}"/>
                            </span>
                        </c:if>
                    </c:forEach>
                </div>
            </c:if>

            <%-- Description --%>
            <div class="detail-desc-label">About this book</div>
            <c:choose>
                <c:when test="${not empty book.description}">
                    <p class="detail-desc">${book.description}</p>
                </c:when>
                <c:otherwise>
                    <p class="detail-desc" style="color:var(--text-muted);font-style:italic;">No description available for this book.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <%-- Reviews --%>
    <section class="rating-section">
        <h2 class="rating-title">Product reviews</h2>

        <div id="ratingFormContainer" class="rating-create">
            <c:choose>
                <c:when test="${canReview}">
                    <form id="ratingForm" action="/ratings/create" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="bookId" value="${book.id}" />

                        <div class="mb-3">
                            <label class="form-label">Rating</label>
                            <div class="rating-stars-select">
                                <input type="radio" id="star5" name="ratingValue" value="5" required>
                                <label for="star5"><i class="fa-solid fa-star"></i></label>
                                <input type="radio" id="star4" name="ratingValue" value="4">
                                <label for="star4"><i class="fa-solid fa-star"></i></label>
                                <input type="radio" id="star3" name="ratingValue" value="3">
                                <label for="star3"><i class="fa-solid fa-star"></i></label>
                                <input type="radio" id="star2" name="ratingValue" value="2">
                                <label for="star2"><i class="fa-solid fa-star"></i></label>
                                <input type="radio" id="star1" name="ratingValue" value="1">
                                <label for="star1"><i class="fa-solid fa-star"></i></label>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Review</label>
                            <textarea id="reviewText" name="reviewText" rows="5" class="form-control"
                                maxlength="1000" required></textarea>
                        </div>

                        <button id="ratingSubmitBtn" type="submit" class="btn-detail-primary">
                            Submit review
                        </button>
                    </form>
                </c:when>

                <c:when test="${not empty myRating}">
                    <div class="alert alert-success">
                        You have already reviewed this book.
                    </div>

                    <form id="ratingForm" action="/ratings/update" method="post" style="display:none;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="bookId" value="${book.id}" />

                        <div class="mb-3">
                            <label class="form-label">Rating</label>
                            <div class="rating-stars-select">
                                <input type="radio" id="star5" name="ratingValue" value="5">
                                <label for="star5"><i class="fa-solid fa-star"></i></label>
                                <input type="radio" id="star4" name="ratingValue" value="4">
                                <label for="star4"><i class="fa-solid fa-star"></i></label>
                                <input type="radio" id="star3" name="ratingValue" value="3">
                                <label for="star3"><i class="fa-solid fa-star"></i></label>
                                <input type="radio" id="star2" name="ratingValue" value="2">
                                <label for="star2"><i class="fa-solid fa-star"></i></label>
                                <input type="radio" id="star1" name="ratingValue" value="1">
                                <label for="star1"><i class="fa-solid fa-star"></i></label>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Review</label>
                            <textarea id="reviewText" name="reviewText" rows="5" class="form-control"
                                maxlength="1000"></textarea>
                        </div>

                        <button id="ratingSubmitBtn" type="submit" class="btn-detail-primary">
                            Update review
                        </button>
                    </form>
                </c:when>

                <c:when test="${not empty currentUser}">
                    <div class="alert alert-warning">
                        You need to purchase and receive this book before you can review it.
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="alert alert-info">
                        Please <a href="/login">log in</a> to review this product.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <c:choose>
            <c:when test="${not empty ratings}">
                <c:forEach items="${ratings}" var="rating">
                    <div class="rating-item">
                        <div class="rating-user">${rating.customer.fullName}</div>

                        <c:if test="${not empty currentUser && currentUser.id == rating.customer.id}">
                            <button type="button" class="rating-edit-btn"
                                onclick="editRating('${rating.ratingValue}', '${rating.review}')">
                                <i class="fa-solid fa-pen"></i>
                            </button>

                            <form action="/ratings/delete" method="post" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <input type="hidden" name="bookId" value="${book.id}" />
                                <button type="submit" class="rating-delete-btn"
                                    onclick="return confirm('Delete this review?')">
                                    <i class="fa-solid fa-trash-can"></i>
                                </button>
                            </form>
                        </c:if>

                        <div class="rating-stars">
                            <c:forEach begin="1" end="5" var="i">
                                <c:choose>
                                    <c:when test="${i <= rating.ratingValue}">
                                        <i class="fa-solid fa-star star-filled"></i>
                                    </c:when>
                                    <c:otherwise>
                                        <i class="fa-regular fa-star star-empty"></i>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </div>
                        <div class="rating-date">${rating.createdAtFormatted}</div>
                        <div class="rating-content">${rating.review}</div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="no-rating">No reviews yet for this product.</div>
            </c:otherwise>
        </c:choose>
    </section>

    <%-- Suggested books --%>
    <c:if test="${not empty suggestedBooks}">
        <section class="suggested-wrap">
            <div class="sec-head">
                <div class="sec-head__left">
                    <div class="sec-head__icon"><i class="fa-solid fa-wand-magic-sparkles"></i></div>
                    <h2 class="sec-head__title">You may also like</h2>
                </div>
                <a href="/books" class="sec-head__link">See more <i class="fa-solid fa-chevron-right"></i></a>
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
                            <div class="s-card__author"><c:out value="${s.author}" default="—"/></div>
                            <div class="s-card__price-box">
                                <c:choose>
                                    <c:when test="${not empty suggestedPromotionMap[s.id]}">
                                        <div class="s-card__discount-line">
                                            <span class="s-card__old-price">${s.priceFormatted} &#8363;</span>
                                            <span class="s-card__discount-badge">${suggestedDiscountLabelMap[s.id]}</span>
                                        </div>
                                        <div class="s-card__price s-card__price--sale">${suggestedDiscountedPriceFormattedMap[s.id]} &#8363;</div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="s-card__price">${s.priceFormatted} &#8363;</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

    <script>
        function editRating(value, review) {
            const form = document.getElementById("ratingForm");
            if (!form) return;

            form.style.display = "block";

            const starInput = document.querySelector(
                'input[name="ratingValue"][value="' + value + '"]'
            );
            if (starInput) starInput.checked = true;

            const reviewField = document.getElementById("reviewText");
            if (reviewField) reviewField.value = review || "";

            form.scrollIntoView({ behavior: "smooth" });
        }
    </script>
</body>

</html>
