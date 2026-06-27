<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="description" content="Booktify — Nhà sách trực tuyến hàng đầu Việt Nam" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/homepage.css" />
    <title>Booktify — Nhà sách trực tuyến</title>
</head>
<body class="home-page">
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">

        <%-- ═══ BANNER ═══ --%>
        <section class="banner">
            <div class="banner-inner">
                <div class="banner-text">
                    <div class="banner-badge">
                        <i class="fa-solid fa-star"></i> Nhà sách trực tuyến hàng đầu
                    </div>
                    <h1 class="banner-title">Khám phá thế giới tri thức qua từng trang sách</h1>
                    <p class="banner-sub">Hàng nghìn đầu sách đa thể loại — văn học, kinh doanh, khoa học, kỹ năng sống và hơn thế nữa. Giao hàng nhanh, giá tốt nhất.</p>
                    <div class="banner-actions">
                        <a href="/books" class="btn-banner btn-banner-primary">
                            <i class="fa-solid fa-book-open"></i> Mua sách ngay
                        </a>
                        <c:if test="${empty sessionScope.username}">
                            <a href="/register" class="btn-banner btn-banner-outline">
                                <i class="fa-solid fa-user-plus"></i> Đăng ký miễn phí
                            </a>
                        </c:if>
                    </div>
                </div>
                <div class="banner-visual">
                    <div class="banner-books">
                        <div class="banner-book-tile">
                            <i class="fa-solid fa-feather-pointed"></i>
                            <span>Văn học</span>
                        </div>
                        <div class="banner-book-tile">
                            <i class="fa-solid fa-chart-line"></i>
                            <span>Kinh doanh</span>
                        </div>
                        <div class="banner-book-tile">
                            <i class="fa-solid fa-flask"></i>
                            <span>Khoa học</span>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <%-- ═══ DANH MỤC NỔI BẬT ═══ --%>
        <c:if test="${not empty categories}">
        <div class="section" style="padding-bottom:.5rem;">
            <div class="sec-head">
                <div class="sec-head__left">
                    <div class="sec-head__icon"><i class="fa-solid fa-layer-group"></i></div>
                    <h2 class="sec-head__title">Danh mục nổi bật</h2>
                </div>
                <a href="/books" class="sec-head__link">Xem tất cả <i class="fa-solid fa-chevron-right"></i></a>
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

        <%-- ═══ SÁCH BÁN CHẠY ═══ --%>
        <c:if test="${not empty featuredBooks}">
        <div class="section">
            <div class="sec-head">
                <div class="sec-head__left">
                    <div class="sec-head__icon" style="background:var(--accent-red,#E53935);">
                        <i class="fa-solid fa-fire"></i>
                    </div>
                    <h2 class="sec-head__title" style="color:var(--accent-red,#E53935);">Sách bán chạy</h2>
                </div>
                <a href="/books" class="sec-head__link">Xem tất cả <i class="fa-solid fa-chevron-right"></i></a>
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
                                <span class="book-card__badge book-card__badge--cat">${book.category.name}</span>
                            </c:if>
                        </div>
                        <div class="book-card__body">
                            <div class="book-card__title">${book.title}</div>
                            <div class="book-card__author">${book.author}</div>
                            <div class="book-card__footer">
                                <span class="book-card__price">${book.priceFormatted} &#8363;</span>
                                <span class="book-card__detail-btn">Chi tiết</span>
                            </div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </div>
        </c:if>

        <%-- ═══ SÁCH MỚI ═══ (same list, different heading + offset) --%>
        <c:if test="${not empty featuredBooks}">
        <div class="section" style="padding-top:0;">
            <div class="sec-head">
                <div class="sec-head__left">
                    <div class="sec-head__icon" style="background:var(--accent-warm,#F57C00);">
                        <i class="fa-solid fa-bolt"></i>
                    </div>
                    <h2 class="sec-head__title" style="color:var(--accent-warm,#F57C00);">Sách mới nhất</h2>
                </div>
                <a href="/books" class="sec-head__link">Xem tất cả <i class="fa-solid fa-chevron-right"></i></a>
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
                            <span class="book-card__badge" style="background:var(--accent-warm,#F57C00);">MỚI</span>
                        </div>
                        <div class="book-card__body">
                            <c:if test="${not empty book.category}">
                                <div class="book-card__cat">${book.category.name}</div>
                            </c:if>
                            <div class="book-card__title">${book.title}</div>
                            <div class="book-card__author">${book.author}</div>
                            <div class="book-card__footer">
                                <span class="book-card__price">${book.priceFormatted} &#8363;</span>
                                <span class="book-card__detail-btn">Chi tiết</span>
                            </div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </div>
        </c:if>

        <%-- ═══ TẠI SAO CHỌN BOOKTIFY ═══ --%>
        <div class="section" style="padding-top:0;">
            <div class="sec-head" style="margin-bottom:1.5rem;">
                <div class="sec-head__left">
                    <div class="sec-head__icon"><i class="fa-solid fa-shield-halved"></i></div>
                    <h2 class="sec-head__title">Tại sao chọn Booktify?</h2>
                </div>
            </div>
            <div class="why-grid">
                <div class="why-card">
                    <div class="why-card__icon"><i class="fa-solid fa-truck-fast"></i></div>
                    <div class="why-card__text">
                        <h4>Giao hàng nhanh</h4>
                        <p>Đơn hàng được xử lý và giao đến tay bạn nhanh chóng trên toàn quốc.</p>
                    </div>
                </div>
                <div class="why-card">
                    <div class="why-card__icon"><i class="fa-solid fa-shield-halved"></i></div>
                    <div class="why-card__text">
                        <h4>Thanh toán an toàn</h4>
                        <p>Thông tin được bảo mật bằng mã hóa tiêu chuẩn ngành.</p>
                    </div>
                </div>
                <div class="why-card">
                    <div class="why-card__icon"><i class="fa-solid fa-rotate-left"></i></div>
                    <div class="why-card__text">
                        <h4>Đổi trả dễ dàng</h4>
                        <p>Chính sách hoàn trả linh hoạt nếu sách không đúng mô tả.</p>
                    </div>
                </div>
                <div class="why-card">
                    <div class="why-card__icon"><i class="fa-solid fa-headset"></i></div>
                    <div class="why-card__text">
                        <h4>Hỗ trợ 24/7</h4>
                        <p>Đội ngũ chăm sóc khách hàng luôn sẵn sàng hỗ trợ bạn.</p>
                    </div>
                </div>
            </div>

            <c:if test="${empty sessionScope.username}">
                <div class="cta-strip" style="margin-top:2rem;">
                    <div class="cta-strip__text">
                        <h3>Tham gia Booktify ngay hôm nay</h3>
                        <p>Tạo tài khoản miễn phí để theo dõi đơn hàng và nhận ưu đãi đặc biệt.</p>
                    </div>
                    <a href="/register" class="cta-strip__btn">
                        <i class="fa-solid fa-user-plus"></i> Đăng ký miễn phí
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
