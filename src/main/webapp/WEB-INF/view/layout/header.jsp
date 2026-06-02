<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="site-header">
    <div class="header-inner">
        <a href="/" class="brand">
            <span class="brand-icon"><i class="fa-solid fa-book-open"></i></span>
            <span class="brand-text">Booktify</span>
        </a>

        <nav class="main-nav" aria-label="Main navigation">
            <a href="/" class="nav-link active">Home</a>
            <a href="#" class="nav-link">New Releases</a>
            <a href="#" class="nav-link">Best Sellers</a>
            <a href="#" class="nav-link">Promotions</a>
        </nav>

        <div class="header-actions">
            <c:choose>
                <c:when test="${not empty sessionScope.username}">
                    <div class="user-dropdown" id="userDropdown">
                        <button type="button" class="user-dropdown-toggle" id="userDropdownToggle"
                            aria-expanded="false" aria-haspopup="true">
                            <i class="fa-solid fa-user-circle"></i>
                            <span class="user-dropdown-name">
                                ${not empty sessionScope.fullName ? sessionScope.fullName : sessionScope.username}
                            </span>
                            <i class="fa-solid fa-chevron-down user-dropdown-chevron"></i>
                        </button>
                        <div class="user-dropdown-menu" id="userDropdownMenu" role="menu">
                            <a href="/profile" class="dropdown-item" role="menuitem">
                                <i class="fa-solid fa-id-card"></i>
                                Personal Information
                            </a>
                            <div class="dropdown-divider"></div>
                            <form class="dropdown-logout-form" method="post" action="/logout">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <button type="submit" class="dropdown-item dropdown-item-btn" role="menuitem">
                                    <i class="fa-solid fa-right-from-bracket"></i>
                                    Log Out
                                </button>
                            </form>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="/login" class="btn-header btn-outline">Sign In</a>
                    <a href="/register" class="btn-header btn-primary">Sign Up</a>
                </c:otherwise>
            </c:choose>
        </div>

        <button type="button" class="nav-toggle" id="navToggle" aria-label="Open menu">
            <i class="fa-solid fa-bars"></i>
        </button>
    </div>
</header>

<script>
    (function () {
        var dropdown = document.getElementById('userDropdown');
        var toggle = document.getElementById('userDropdownToggle');
        var menu = document.getElementById('userDropdownMenu');

        if (!dropdown || !toggle || !menu) {
            return;
        }

        toggle.addEventListener('click', function (e) {
            e.stopPropagation();
            var isOpen = dropdown.classList.toggle('open');
            toggle.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
        });

        document.addEventListener('click', function (e) {
            if (!dropdown.contains(e.target)) {
                dropdown.classList.remove('open');
                toggle.setAttribute('aria-expanded', 'false');
            }
        });

        menu.addEventListener('click', function (e) {
            if (e.target.closest('a, button[type="submit"]')) {
                dropdown.classList.remove('open');
                toggle.setAttribute('aria-expanded', 'false');
            }
        });
    })();
</script>
