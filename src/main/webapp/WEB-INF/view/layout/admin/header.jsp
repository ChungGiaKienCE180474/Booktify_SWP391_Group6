<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="admin-topbar">
    <div class="admin-topbar__brand">
        <span class="admin-topbar__badge">Admin</span>
        <div>
            <h1>Booktify Dashboard</h1>
            <p>Manage content, users, and categories</p>
        </div>
    </div>

    <div class="admin-topbar__meta">
        <div class="admin-topbar__user">
            <span class="admin-topbar__avatar">AD</span>
            <div>
                <strong>${not empty sessionScope.fullName ? sessionScope.fullName : 'Administrator'}</strong>
                <span>${not empty sessionScope.role ? sessionScope.role : 'ADMIN'}</span>
            </div>
        </div>
        <a class="admin-topbar__link" href="/">View site</a>
    </div>
</header>