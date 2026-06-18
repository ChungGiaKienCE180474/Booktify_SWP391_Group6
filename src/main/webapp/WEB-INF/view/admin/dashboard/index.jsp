<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title>Dashboard — Booktify Admin</title>
    <style>
        .card-icon-blue   { background:#EFF6FF; border-color:#BFDBFE; color:#2563EB; }
        .card-icon-green  { background:#F0FDF4; border-color:#BBF7D0; color:#16A34A; }
        .card-icon-orange { background:#FFF7ED; border-color:#FED7AA; color:#F97316; }
        .card-icon-purple { background:#F5F3FF; border-color:#DDD6FE; color:#7C3AED; }
    </style>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content">

            <%-- Hero --%>
            <div class="admin-hero">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-hand-wave"></i> Welcome back</p>
                    <h2>Dashboard Overview</h2>
                    <p>Monitor your store's books, categories, and user activity from one place.</p>
                </div>
                <div style="display:flex;gap:10px;flex-wrap:wrap;position:relative;z-index:1;">
                    <a class="admin-button" href="/admin/books/create" style="background:#fff;color:#1D4ED8;border-color:#fff;">
                        <i class="fa-solid fa-plus"></i> New Book
                    </a>
                    <a class="admin-button admin-button--ghost" href="/admin/categories"
                       style="background:rgba(255,255,255,.15);border-color:rgba(255,255,255,.35);color:#fff;">
                        <i class="fa-solid fa-tag"></i> Categories
                    </a>
                </div>
            </div>

            <%-- Stats --%>
            <div class="admin-cards">
                <article class="admin-card">
                    <div class="admin-card__icon card-icon-blue">
                        <i class="fa-solid fa-layer-group"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Total Categories</span>
                        <strong>${totalCategories != null ? totalCategories : 0}</strong>
                    </div>
                </article>

                <article class="admin-card">
                    <div class="admin-card__icon card-icon-green">
                        <i class="fa-solid fa-circle-check"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Active Categories</span>
                        <strong>${activeCategories != null ? activeCategories : 0}</strong>
                    </div>
                </article>

                <article class="admin-card">
                    <div class="admin-card__icon card-icon-orange">
                        <i class="fa-solid fa-book"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Total Books</span>
                        <strong>${totalBooks != null ? totalBooks : 0}</strong>
                    </div>
                </article>

                <article class="admin-card">
                    <div class="admin-card__icon card-icon-purple">
                        <i class="fa-solid fa-book-open"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Active Books</span>
                        <strong>${activeBooks != null ? activeBooks : 0}</strong>
                    </div>
                </article>
            </div>

        </section>
    </main>
</body>
</html>
