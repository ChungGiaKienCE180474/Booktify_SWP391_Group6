<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="admin-topbar">
    <div class="admin-topbar__brand">
        <div class="admin-topbar__badge">
            <i class="fa-solid fa-shield-halved"></i>
        </div>
        <div>
            <h1>Booktify Admin</h1>
            <p>Booktify Administration Center</p>
        </div>
    </div>

    <div class="admin-topbar__meta">
        <div class="admin-topbar__user">
            <div class="admin-topbar__avatar">
                <i class="fa-solid fa-user"></i>
            </div>
            <div>
                <strong>${not empty sessionScope.fullName ? sessionScope.fullName : 'Administrator'}</strong>
                <span>${not empty sessionScope.role ? sessionScope.role : 'ADMIN'}</span>
            </div>
        </div>
        <a class="admin-topbar__link" href="/admin/profile">
            <i class="fa-solid fa-id-card"></i>
            My Profile
        </a>
        <a class="admin-topbar__link" href="/">
            <i class="fa-solid fa-arrow-up-right-from-square"></i>
            Customer View
        </a>
    </div>
</header>
