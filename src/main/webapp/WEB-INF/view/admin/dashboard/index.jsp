<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title>Admin Dashboard</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content">
            <div class="admin-hero">
                <div>
                    <p class="admin-kicker">Reusable admin shell</p>
                    <h2>Dashboard overview</h2>
                    <p>Sidebar and header are split into reusable fragments for all admin screens.</p>
                </div>
                <a class="admin-button" href="/admin/categories">Manage categories</a>
            </div>

            <div class="admin-cards">
                <article class="admin-card">
                    <span>Total categories</span>
                    <strong>${totalCategories}</strong>
                </article>
                <article class="admin-card">
                    <span>Active categories</span>
                    <strong>${activeCategories}</strong>
                </article>
                <article class="admin-card">
                    <span>Total books</span>
                    <strong>${totalBooks}</strong>
                </article>
                <article class="admin-card">
                    <span>Active books</span>
                    <strong>${activeBooks}</strong>
                </article>
            </div>

            <div class="admin-panel">
                <h3>Quick actions</h3>
                <div class="admin-actions">
                    <a href="/admin/categories/create" class="admin-action">Create category</a>
                    <a href="/admin/categories" class="admin-action admin-action--secondary">Browse categories</a>
                    <a href="/admin/books/create" class="admin-action">Create book</a>
                    <a href="/admin/books" class="admin-action admin-action--secondary">Browse books</a>
                </div>
            </div>
        </section>
    </main>
</body>
</html>