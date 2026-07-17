<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <%-- ── TOP BAR ── --%>
            <div class="header-topbar">
                <div class="header-topbar-inner">
                    <div class="header-topbar-left">
                        <span><i class="fa-solid fa-book"></i> Sách gì cũng có mua hết ở Booktify</span>
                        <span class="topbar-sep">|</span>
                        <span><i class="fa-solid fa-phone"></i> Hotline: 1900-1234</span>
                    </div>
                    <div class="header-topbar-right">
                        <c:choose>
                            <c:when test="${empty sessionScope.username}">
                                <a href="/login">Đăng nhập</a>
                                <span class="topbar-sep">|</span>
                                <a href="/register">Đăng ký</a>
                            </c:when>
                            <c:otherwise>
                                <span>Xin chào, <strong>${not empty sessionScope.fullName ? sessionScope.fullName :
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
                                            value="${not empty q ? q : ''}"
                                            placeholder="Tìm kiếm sách, tác giả, ..." />
                                        <button type="submit" class="header-search__btn">
                                            <i class="fa-solid fa-magnifying-glass"></i> Tìm kiếm
                                        </button>
                                    </form>

                                    <%-- Right icons --%>
                                        <div class="header-icons">
                                            <%-- Cart (placeholder) --%>
                                                <a href="/cart" class="hdr-icon-btn" title="Giỏ hàng">
                                                    <i class="fa-solid fa-cart-shopping"></i>
                                                    <span class="hdr-icon-label">Giỏ hàng</span>
                                                </a>

                                                <%-- User / auth --%>
                                                    <c:choose>
                                                        <c:when test="${not empty sessionScope.username}">
                                                            <div class="hdr-user-wrap" id="hdrUserWrap">
                                                                <button type="button" class="hdr-icon-btn"
                                                                    id="hdrUserToggle">
                                                                    <i class="fa-solid fa-circle-user"></i>
                                                                    <span class="hdr-icon-label">Tài khoản</span>
                                                                </button>
                                                                <div class="hdr-user-menu" id="hdrUserMenu">
                                                                    <a href="/profile" class="hdr-dropdown-item">
                                                                        <i class="fa-solid fa-id-card"></i> Thông tin cá
                                                                        nhân
                                                                    </a>
                                                                    <a href="/orders" class="hdr-dropdown-item">
                                                                        <i class="fa-solid fa-box"></i> Đơn hàng của tôi
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
                                                                            Đăng xuất
                                                                        </button>
                                                                    </form>
                                                                </div>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="hdr-auth-btns">
                                                                <a href="/login" class="btn-hdr btn-hdr-ghost">Đăng
                                                                    nhập</a>
                                                                <a href="/register" class="btn-hdr btn-hdr-solid">Đăng
                                                                    ký</a>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                        </div>
                        </div>

                        <%-- ── CATEGORY NAV BAR ── --%>
                            <nav class="header-catbar">
                                <div class="header-catbar-inner">

                                    <a href="/books" class="catbar-link catbar-all">
                                        <i class="fa-solid fa-book"></i> Danh mục sách
                                    </a>

                                    <%-- Stationery module belongs to a teammate; left as a
                                         non-functional placeholder until it's wired up. --%>
                                    <a href="#" class="catbar-link catbar-disabled" title="Sắp ra mắt" onclick="return false;">
                                        <i class="fa-solid fa-pen-ruler"></i> Danh mục văn phòng phẩm
                                    </a>

                                </div>
                            </nav>

                </header>

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