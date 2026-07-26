<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <meta name="description" content="Booktify — Vietnam's leading online bookstore" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
            <link rel="stylesheet" href="/css/header.css" />
            <link rel="stylesheet" href="/css/footer.css" />
            <link rel="stylesheet" href="/css/homepage.css" />
            <style>
                .book-card__footer {
                    align-items: flex-end;
                }

                .book-card__price-box {
                    display: flex;
                    min-width: 0;
                    flex: 1;
                    flex-direction: column;
                    gap: 4px;
                }

                .book-card__discount-line {
                    display: flex;
                    align-items: center;
                    gap: 6px;
                    flex-wrap: wrap;
                }

                .book-card__old-price {
                    color: #94a3b8;
                    font-size: .72rem;
                    font-weight: 700;
                    text-decoration: line-through;
                }

                .book-card__discount-badge {
                    display: inline-flex;
                    align-items: center;
                    padding: 3px 7px;
                    border-radius: 999px;
                    background: #fee2e2;
                    color: #dc2626;
                    font-size: .68rem;
                    font-weight: 800;
                    white-space: nowrap;
                }

                .book-card__price--sale {
                    color: #dc2626;
                    font-size: 1rem;
                }
            </style>
            <title>Booktify — Online Bookstore</title>
        </head>

        <body class="home-page">
            <jsp:include page="/WEB-INF/view/layout/header.jsp" />

            <main class="main-content">

                <%-- ═══ BANNER ═══ --%>
                    <section class="banner">
                        <div class="banner-inner">
                            <div class="banner-text">
                                <div class="banner-badge">
                                    <i class="fa-solid fa-star"></i> trusted online bookstore
                                </div>
                                <h1 class="banner-title">Explore a world of knowledge through every page</h1>
                                <p class="banner-sub">Thousands of books across many genres — literature, business, science,
                                    life skills and more. Fast delivery, best prices.</p>
                                <div class="banner-actions">
                                    <a href="/books" class="btn-banner btn-banner-primary">
                                        <i class="fa-solid fa-book-open"></i> Buy now
                                    </a>
                                    <c:if test="${empty sessionScope.username}">
                                        <a href="/register" class="btn-banner btn-banner-outline">
                                            <i class="fa-solid fa-user-plus"></i> Sign up for free
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                            <div class="banner-visual">
                                <div class="banner-books">
                                    <div class="banner-book-tile">
                                        <i class="fa-solid fa-feather-pointed"></i>
                                        <span>Literature</span>
                                    </div>
                                    <div class="banner-book-tile">
                                        <i class="fa-solid fa-flask"></i>
                                        <span>Science</span>
                                    </div>
                                    <div class="banner-book-tile">
                                        <i class="fa-solid fa-laptop-code"></i>
                                        <span>IT</span>
                                    </div>
                                    <div class="banner-book-tile">
                                        <i class="fa-solid fa-chart-line"></i>
                                        <span>Business</span>
                                    </div>
                                    <div class="banner-book-tile">
                                        <i class="fa-solid fa-lightbulb"></i>
                                        <span>Life skills</span>
                                    </div>
                                    <div class="banner-book-tile">
                                        <i class="fa-solid fa-language"></i>
                                        <span>Languages</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <%-- ═══ FEATURED CATEGORIES ═══ --%>
                        <c:if test="${not empty categories}">
                            <div class="section" style="padding-bottom:.5rem;">
                                <div class="sec-head">
                                    <div class="sec-head__left">
                                        <div class="sec-head__icon"><i class="fa-solid fa-layer-group"></i></div>
                                        <h2 class="sec-head__title">Featured categories</h2>
                                    </div>
                                    <a href="/books" class="sec-head__link">View all <i
                                            class="fa-solid fa-chevron-right"></i></a>
                                </div>
                                <div class="cat-grid">
                                    <c:forEach items="${categories}" var="cat">
                                        <c:if test="${cat.active}">
                                            <a href="/books?categoryId=${cat.id}" class="cat-card">
                                                <div class="cat-card__icon"><i class="fa-solid fa-book-open"></i></div>
                                                <span class="cat-card__name">${cat.name}</span>
                                            </a>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <%-- ═══ BESTSELLERS ═══ --%>
                            <c:if test="${not empty featuredBooks}">
                                <div class="section">
                                    <div class="sec-head">
                                        <div class="sec-head__left">
                                            <div class="sec-head__icon" style="background:var(--accent-red,#E53935);">
                                                <i class="fa-solid fa-fire"></i>
                                            </div>
                                            <h2 class="sec-head__title" style="color:var(--accent-red,#E53935);">Bestsellers</h2>
                                        </div>
                                        <a href="/books" class="sec-head__link">View all <i
                                                class="fa-solid fa-chevron-right"></i></a>
                                    </div>
                                    <div class="book-grid">
                                        <c:forEach items="${featuredBooks}" var="book" end="9">
                                            <a href="/books/${book.id}" class="book-card">
                                                <div class="book-card__thumb">
                                                    <c:choose>
                                                        <c:when test="${not empty book.imageUrl}">
                                                            <img src="${book.imageUrl}" alt="${book.title}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="book-card__no-img">
                                                                <i class="fa-solid fa-book"></i>
                                                                <span>No cover</span>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:if test="${not empty book.category}">
                                                        <span
                                                            class="book-card__badge book-card__badge--cat">${book.category.name}</span>
                                                    </c:if>
                                                </div>
                                                <div class="book-card__body">
                                                    <div class="book-card__title">${book.title}</div>
                                                    <div class="book-card__author">
                                                        <c:out value="${book.author.authorName}" default="—" />
                                                    </div>
                                                    <div class="book-card__footer">
                                                        <div class="book-card__price-box">
                                                            <c:choose>
                                                                <c:when test="${not empty bestPromotionMap[book.id]}">
                                                                    <div class="book-card__discount-line">
                                                                        <span class="book-card__old-price">
                                                                            ${book.priceFormatted} &#8363;
                                                                        </span>
                                                                        <span class="book-card__discount-badge">
                                                                            ${discountLabelMap[book.id]}
                                                                        </span>
                                                                    </div>
                                                                    <span class="book-card__price book-card__price--sale">
                                                                        ${discountedPriceFormattedMap[book.id]} &#8363;
                                                                    </span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="book-card__price">
                                                                        ${book.priceFormatted} &#8363;
                                                                    </span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                        <span class="book-card__detail-btn">Details</span>
                                                    </div>
                                                </div>
                                            </a>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>

                            <%-- ═══ NEW BOOKS ═══ (same list, different heading + offset) --%>
                                <c:if test="${not empty featuredBooks}">
                                    <div class="section" style="padding-top:0;">
                                        <div class="sec-head">
                                            <div class="sec-head__left">
                                                <div class="sec-head__icon"
                                                    style="background:var(--accent-warm,#F57C00);">
                                                    <i class="fa-solid fa-bolt"></i>
                                                </div>
                                                <h2 class="sec-head__title" style="color:var(--accent-warm,#F57C00);">
                                                    Latest books</h2>
                                            </div>
                                            <a href="/books" class="sec-head__link">View all <i
                                                    class="fa-solid fa-chevron-right"></i></a>
                                        </div>
                                        <div class="book-grid">
                                            <c:forEach items="${featuredBooks}" var="book" begin="0" end="4">
                                                <a href="/books/${book.id}" class="book-card">
                                                    <div class="book-card__thumb">
                                                        <c:choose>
                                                            <c:when test="${not empty book.imageUrl}">
                                                                <img src="${book.imageUrl}" alt="${book.title}" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="book-card__no-img">
                                                                    <i class="fa-solid fa-book"></i>
                                                                    <span>No cover</span>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <span class="book-card__badge"
                                                            style="background:var(--accent-warm,#F57C00);">NEW</span>
                                                    </div>
                                                    <div class="book-card__body">
                                                        <c:if test="${not empty book.category}">
                                                            <div class="book-card__cat">${book.category.name}</div>
                                                        </c:if>
                                                        <div class="book-card__title">${book.title}</div>
                                                        <div class="book-card__author">
                                                            <c:out value="${book.author.authorName}" default="—" />
                                                        </div>
                                                        <div class="book-card__footer">
                                                            <div class="book-card__price-box">
                                                                <c:choose>
                                                                    <c:when test="${not empty bestPromotionMap[book.id]}">
                                                                        <div class="book-card__discount-line">
                                                                            <span class="book-card__old-price">
                                                                                ${book.priceFormatted} &#8363;
                                                                            </span>
                                                                            <span class="book-card__discount-badge">
                                                                                ${discountLabelMap[book.id]}
                                                                            </span>
                                                                        </div>
                                                                        <span class="book-card__price book-card__price--sale">
                                                                            ${discountedPriceFormattedMap[book.id]} &#8363;
                                                                        </span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="book-card__price">
                                                                            ${book.priceFormatted} &#8363;
                                                                        </span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                            <span class="book-card__detail-btn">Details</span>
                                                        </div>
                                                    </div>
                                                </a>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </c:if>

                                <%-- ═══ WHY CHOOSE BOOKTIFY ═══ --%>
                                    <div class="section" style="padding-top:0;">
                                        <div class="sec-head" style="margin-bottom:1.5rem;">
                                            <div class="sec-head__left">
                                                <div class="sec-head__icon"><i class="fa-solid fa-shield-halved"></i>
                                                </div>
                                                <h2 class="sec-head__title">Why choose Booktify?</h2>
                                            </div>
                                        </div>
                                        <div class="why-grid">
                                            <div class="why-card">
                                                <div class="why-card__icon"><i class="fa-solid fa-truck-fast"></i></div>
                                                <div class="why-card__text">
                                                    <h4>Fast delivery</h4>
                                                    <p>Orders are processed and delivered to you quickly nationwide.</p>
                                                </div>
                                            </div>
                                            <div class="why-card">
                                                <div class="why-card__icon"><i class="fa-solid fa-shield-halved"></i>
                                                </div>
                                                <div class="why-card__text">
                                                    <h4>Secure payment</h4>
                                                    <p>Your information is protected with industry-standard encryption.</p>
                                                </div>
                                            </div>
                                            <div class="why-card">
                                                <div class="why-card__icon"><i class="fa-solid fa-rotate-left"></i>
                                                </div>
                                                <div class="why-card__text">
                                                    <h4>Easy returns</h4>
                                                    <p>Flexible return policy if a book does not match its description.</p>
                                                </div>
                                            </div>
                                            <div class="why-card">
                                                <div class="why-card__icon"><i class="fa-solid fa-headset"></i></div>
                                                <div class="why-card__text">
                                                    <h4>24/7 support</h4>
                                                    <p>Our customer care team is always ready to help you.</p>
                                                </div>
                                            </div>
                                        </div>

                                        <c:if test="${empty sessionScope.username}">
                                            <div class="cta-strip" style="margin-top:2rem;">
                                                <div class="cta-strip__text">
                                                    <h3>Join Booktify today</h3>
                                                    <p>Create a free account to track your orders and receive special offers.</p>
                                                </div>
                                                <a href="/register" class="cta-strip__btn">
                                                    <i class="fa-solid fa-user-plus"></i> Sign up for free
                                                </a>
                                            </div>
                                        </c:if>
                                    </div>

            </main>

            <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

            <script>
                document.getElementById('navToggle')?.addEventListener('click', function () {
                    document.querySelector('.main-nav')?.classList.toggle('open');
                });
            </script>
        </body>

        </html>