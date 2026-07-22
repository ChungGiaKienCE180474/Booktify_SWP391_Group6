<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <%-- ── TOP BAR ── --%>
            <div class="header-topbar">
                <div class="header-topbar-inner">
                    <div class="header-topbar-left">
                        <span><i class="fa-solid fa-book"></i> Find any book you want at Booktify</span>
                        <span class="topbar-sep">|</span>
                        <span><i class="fa-solid fa-phone"></i> Hotline: 1900-1234</span>
                    </div>
                    <div class="header-topbar-right">
                        <c:choose>
                            <c:when test="${empty sessionScope.username}">
                                <a href="/login">Login</a>
                                <span class="topbar-sep">|</span>
                                <a href="/register">Register</a>
                            </c:when>
                            <c:otherwise>
                                <span>Hello, <strong>${not empty sessionScope.fullName ? sessionScope.fullName :
                                        sessionScope.username}</strong></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <%-- ── STICKY HEADER WRAPPER ── --%>
                <header class="site-header">

                    <%-- ── MAIN ROW: Logo + Search + Icons ── --%>
                        <div class="header-main">

                            <%-- Logo --%>
                                <a href="/" class="brand">
                                    <span class="brand-icon"><i class="fa-solid fa-book-open"></i></span>
                                    <span>Booktify</span>
                                </a>

                                <%-- Search bar --%>
                                    <form class="header-search" action="/books" method="get">
                                        <input type="text" name="q" class="header-search__input"
                                            value="${not empty q ? q : ''}" placeholder="Search books, authors, ..." />
                                        <button type="submit" class="header-search__btn">
                                            <i class="fa-solid fa-magnifying-glass"></i> Search
                                        </button>
                                    </form>

                                    <%-- Right icons --%>
                                        <div class="header-icons">
                                            <%-- Cart --%>
                                                <a href="/cart" class="hdr-icon-btn hdr-icon-btn--cart" title="Cart">
                                                    <i class="fa-solid fa-cart-shopping"></i>
                                                    <span id="hdrCartBadge" class="hdr-cart-badge"
                                                        <c:if test="${cartItemCount <= 0}">hidden</c:if>>
                                                        <c:choose>
                                                            <c:when test="${cartItemCount > 99}">99+</c:when>
                                                            <c:otherwise>${cartItemCount}</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                    <span class="hdr-icon-label">Cart</span>
                                                </a>

                                                <%-- User / auth --%>
                                                    <c:choose>
                                                        <c:when test="${not empty sessionScope.username}">
                                                            <div class="hdr-user-wrap" id="hdrUserWrap">
                                                                <button type="button" class="hdr-icon-btn"
                                                                    id="hdrUserToggle">
                                                                    <i class="fa-solid fa-circle-user"></i>
                                                                    <span class="hdr-icon-label">Account</span>
                                                                </button>
                                                                <div class="hdr-user-menu" id="hdrUserMenu">
                                                                    <a href="/profile" class="hdr-dropdown-item">
                                                                        <i class="fa-solid fa-id-card"></i> My profile
                                                                    </a>
                                                                    <a href="/orders" class="hdr-dropdown-item">
                                                                        <i class="fa-solid fa-box"></i> My orders
                                                                    </a>
                                                                    <div class="hdr-dropdown-sep"></div>
                                                                    <form class="hdr-dropdown-form" method="post"
                                                                        action="/logout">
                                                                        <input type="hidden"
                                                                            name="${_csrf.parameterName}"
                                                                            value="${_csrf.token}" />
                                                                        <button type="submit"
                                                                            class="hdr-dropdown-item hdr-dropdown-btn">
                                                                            <i
                                                                                class="fa-solid fa-right-from-bracket"></i>
                                                                            Logout
                                                                        </button>
                                                                    </form>
                                                                </div>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="hdr-auth-btns">
                                                                <a href="/login" class="btn-hdr btn-hdr-ghost">Login</a>
                                                                <a href="/register"
                                                                    class="btn-hdr btn-hdr-solid">Register</a>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                        </div>
                        </div>

                        <%-- ── CATEGORY NAV BAR ── --%>
                            <nav class="header-catbar">
                                <div class="header-catbar-inner">

                                    <a href="/books" class="catbar-link catbar-all">
                                        <i class="fa-solid fa-book"></i> Book
                                    </a>

                                    <a href="/authors" class="catbar-link">
                                        Author
                                        <%-- Stationery module belongs to a teammate; left as a non-functional
                                            placeholder until it's wired up. --%>
                                            <a href="#" class="catbar-link catbar-disabled" title="Coming soon"
                                                onclick="return false;">
                                                <i class="fa-solid fa-pen-ruler"></i> Stationery
                                            </a>

                                            <%-- Stationery module belongs to a teammate; left as a non-functional
                                                placeholder until it's wired up. --%>
                                                <a href="${pageContext.request.contextPath}/customer/vpp"
                                                    class="catbar-link">
                                                    <i class="fa-solid fa-pen-ruler"></i> Stationery
                                                </a>
                                                <a href="${pageContext.request.contextPath}/contact" class="catbar-link">
    <i class="fa-solid fa-headset"></i> Contact Support
</a>

                                </div>
                            </nav>

                </header>

                <c:if test="${not empty cartSuccessMessage}">
                    <div id="cartFlashSuccess" hidden><c:out value="${cartSuccessMessage}" /></div>
                </c:if>
                <c:if test="${not empty cartErrorMessage}">
                    <div id="cartFlashError" hidden><c:out value="${cartErrorMessage}" /></div>
                </c:if>

                <link rel="stylesheet" href="/css/cart-toast.css" />
                <script src="/js/cart-toast.js"></script>

                <script>
                    (function () {
                        var wrap = document.getElementById('hdrUserWrap');
                        var toggle = document.getElementById('hdrUserToggle');
                        var menu = document.getElementById('hdrUserMenu');
                        if (!wrap || !toggle || !menu) return;
                        toggle.addEventListener('click', function (e) {
                            e.stopPropagation();
                            wrap.classList.toggle('open');
                        });
                        document.addEventListener('click', function (e) {
                            if (!wrap.contains(e.target)) wrap.classList.remove('open');
                        });
                    })();
                </script>