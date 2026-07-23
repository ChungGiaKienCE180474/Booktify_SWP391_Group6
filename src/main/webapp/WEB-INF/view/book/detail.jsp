<%-- Storefront book detail page: full book info plus a "related books" section. --%>
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
                <link rel="stylesheet" href="/css/rating.css" />
                <link rel="stylesheet" href="/css/book.css" />

                <title>${book.title} - Booktify</title>

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
                            <div
                                style="background:#dcfce7;color:#166534;padding:.75rem 1rem;border-radius:8px;margin-bottom:.5rem;">
                                ${successMessage}
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${not empty errorMessage}">
                        <div style="max-width:1200px;margin:0 auto;padding:0 1.5rem;">
                            <div
                                style="background:#fee2e2;color:#991b1b;padding:.75rem 1rem;border-radius:8px;margin-bottom:.5rem;">
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
                                            <a href="/books?categoryId=${book.category.id}"
                                                class="detail-info__cat-link">
                                                <i class="fa-solid fa-tag"></i> ${book.category.name}
                                            </a>
                                        </c:if>

                                        <h1 class="detail-info__title">${book.title}</h1>
                                        <p class="detail-info__author">
                                            Author:
                                            <c:choose>
                                                <c:when test="${not empty authorProfile}">
                                                    <a href="/authors/${authorProfile.authorId}"
                                                        class="detail-info__author-link">
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
                                                        <i class="fa-solid fa-circle-check"></i> In stock
                                                        (${book.stockQuantity})
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
                                                                <form action="/cart/add" method="post"
                                                                    class="detail-cart-form">
                                                                    <input type="hidden" name="${_csrf.parameterName}"
                                                                        value="${_csrf.token}" />
                                                                    <input type="hidden" name="bookId"
                                                                        value="${book.id}" />
                                                                    <input type="hidden" name="redirect"
                                                                        value="/books/${book.id}" />
                                                                    <label class="detail-qty-label"
                                                                        for="cartQty">Quantity</label>
                                                                    <input type="number" id="cartQty" name="quantity"
                                                                        value="1" min="1" max="${book.stockQuantity}"
                                                                        step="1" class="detail-qty-input" required />
                                                                    <button type="submit" class="btn-detail-primary">
                                                                        <i class="fa-solid fa-cart-shopping"></i> Add to
                                                                        cart
                                                                    </button>
                                                                </form>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="/login" class="btn-detail-primary">
                                                                    <i class="fa-solid fa-right-to-bracket"></i> Log in
                                                                    to buy
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

                                            <%-- Meta chips --%>
                                                <div class="detail-meta">
                                                    <c:if test="${not empty book.isbn}">
                                                        <span class="meta-chip"><i class="fa-solid fa-barcode"></i>
                                                            ISBN: ${book.isbn}</span>
                                                    </c:if>
                                                    <c:if test="${not empty book.category}">
                                                        <span class="meta-chip"><i class="fa-solid fa-layer-group"></i>
                                                            ${book.category.name}</span>
                                                    </c:if>
                                                    <span class="meta-chip"><i class="fa-solid fa-boxes-stacked"></i>
                                                        Stock: ${book.stockQuantity}</span>
                                                </div>

                                                <%-- Genre tags: independent from Category --%>
                                                    <c:if test="${not empty book.genres}">
                                                        <div class="detail-desc-label" style="margin-top:.4rem;">Genres
                                                        </div>
                                                        <div class="detail-meta"
                                                            style="margin-bottom:1.25rem;padding-bottom:0;border-bottom:none;">
                                                            <c:forEach items="${book.genres}" var="g">
                                                                <c:if test="${g.active and not g.deleted}">
                                                                    <span class="meta-chip"
                                                                        style="border-color:var(--primary);color:var(--primary);background:rgba(0,107,94,.06);">
                                                                        <i class="fa-solid fa-tags"></i>
                                                                        <c:out value="${g.name}" />
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
                                                                <p class="detail-desc"
                                                                    style="color:var(--text-muted);font-style:italic;">
                                                                    No description available for this book.</p>
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
                                            <form id="ratingForm" action="/ratings/create" method="post"
                                                onsubmit="return validateRatingForm()">
                                                <input type="hidden" name="${_csrf.parameterName}"
                                                    value="${_csrf.token}" />
                                                <input type="hidden" name="bookId" value="${book.id}" />

                                                <div class="mb-3">
                                                    <label class="form-label">Rating</label>
                                                    <div class="rating-stars-select">
                                                        <input type="radio" id="star5" name="ratingValue" value="5"
                                                            required>
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

                                                    <c:if test="${not empty ratingError}">
                                                        <div class="text-danger">
                                                            ${ratingError}
                                                        </div>
                                                    </c:if>
                                                </div>

                                                <div class="mb-3">
                                                    <label class="form-label">Review</label>
                                                    <textarea id="reviewText" name="reviewText" rows="5"
                                                        class="form-control" maxlength="1000" required></textarea>
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

                                            <form id="ratingForm" action="/ratings/update" method="post"
                                                style="display:none;">
                                                <input type="hidden" name="${_csrf.parameterName}"
                                                    value="${_csrf.token}" />
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
                                                    <textarea id="reviewText" name="reviewText" rows="5"
                                                        class="form-control" maxlength="1000"></textarea>
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
                                                <div class="rating-header">

                                                    <div class="rating-user">
                                                        ${rating.customer.fullName}
                                                    </div>


                                                    <c:if
                                                        test="${not empty currentUser && currentUser.id == rating.customer.id}">

                                                        <div class="rating-actions">

                                                            <button type="button" class="rating-edit-btn"
                                                                onclick="editRating('${rating.ratingValue}', '${rating.review}')">
                                                                <i class="fa-solid fa-pen"></i>
                                                            </button>


                                                            <form action="/ratings/delete" method="post"
                                                                style="display:inline;">

                                                                <input type="hidden" name="${_csrf.parameterName}"
                                                                    value="${_csrf.token}" />

                                                                <input type="hidden" name="bookId" value="${book.id}" />

                                                                <button type="submit" class="rating-delete-btn"
                                                                    onclick="return confirm('Delete this review?')">
                                                                    <i class="fa-solid fa-trash-can"></i>
                                                                </button>

                                                            </form>

                                                        </div>

                                                    </c:if>

                                                </div>

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
                                                <div class="sec-head__icon"><i
                                                        class="fa-solid fa-wand-magic-sparkles"></i></div>
                                                <h2 class="sec-head__title">You may also like</h2>
                                            </div>
                                            <a href="/books" class="sec-head__link">See more <i
                                                    class="fa-solid fa-chevron-right"></i></a>
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
                                                                <div class="s-card__placeholder"><i
                                                                        class="fa-solid fa-book"></i></div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="s-card__body">
                                                        <div class="s-card__title">${s.title}</div>
                                                        <div class="s-card__author">
                                                            <c:out value="${s.author}" default="—" />
                                                        </div>
                                                        <div class="s-card__price-box">
                                                            <c:choose>
                                                                <c:when test="${not empty suggestedPromotionMap[s.id]}">
                                                                    <div class="s-card__discount-line">
                                                                        <span
                                                                            class="s-card__old-price">${s.priceFormatted}
                                                                            &#8363;</span>
                                                                        <span
                                                                            class="s-card__discount-badge">${suggestedDiscountLabelMap[s.id]}</span>
                                                                    </div>
                                                                    <div class="s-card__price s-card__price--sale">
                                                                        ${suggestedDiscountedPriceFormattedMap[s.id]}
                                                                        &#8363;</div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="s-card__price">${s.priceFormatted}
                                                                        &#8363;</div>
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

                                        // hiện form
                                        form.style.display = "block";

                                        // chọn sao cũ
                                        document.querySelector(
                                            'input[name="ratingValue"][value="' + value + '"]'
                                        ).checked = true;

                                        // điền review cũ
                                        document.getElementById("reviewText").value = review;

                                        // scroll tới form
                                        form.scrollIntoView({
                                            behavior: "smooth"
                                        });
                                    }


                                    function validateRatingForm() {
                                        const rating = document.querySelector('input[name="ratingValue"]:checked');

                                        if (!rating) {
                                            alert("Please select a star rating before submitting.");
                                            return false;
                                        }

                                        return true;
                                    }
                                </script>
            </body>

            </html>