<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<aside class="admin-sidebar">
    <div class="admin-sidebar__section">
        <span class="admin-sidebar__label">Overview</span>
        <a href="/admin" class="admin-sidebar__item ${pageContext.request.requestURI == '/admin' ? 'active' : ''}">
            <i class="fa-solid fa-chart-line"></i>
            Dashboard
        </a>
    </div>

    <div class="admin-sidebar__section">
        <span class="admin-sidebar__label">Management</span>
        <a href="/admin/categories" class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/categories') ? 'active' : ''}">
            <i class="fa-solid fa-list"></i>
            Categories
        </a>
        <a href="/admin/books" class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/books') ? 'active' : ''}">
            <i class="fa-solid fa-book"></i>
            Books / Products
        </a>
        <a href="#" class="admin-sidebar__item">
            <i class="fa-solid fa-users"></i>
            Users
        </a>
    </div>

    <div class="admin-sidebar__section admin-sidebar__section--footer">
        <a href="/logout" class="admin-sidebar__logout">
            <i class="fa-solid fa-right-from-bracket"></i>
            Logout
        </a>
    </div>
</aside>