<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />

            <title>Dashboard — Booktify Staff</title>

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
            <link rel="stylesheet" href="/css/admin-dashboard.css" />

            <style>
                .status-pill--pending {
                    background: var(--warn-lt);
                    color: var(--warn);
                    border: 1px solid #FDE68A;
                }
            </style>
        </head>

        <body class="admin-shell">

            <aside class="admin-sidebar">
                <a href="/staff" class="sidebar-logo">
                    <span class="sidebar-logo__icon">
                        <i class="fa-solid fa-book-open"></i>
                    </span>

                    <span class="sidebar-logo__text">
                        <span class="sidebar-logo__name">Booktify</span>
                        <span class="sidebar-logo__sub">Staff Panel</span>
                    </span>
                </a>

                <nav class="admin-sidebar__nav">
                    <div class="admin-sidebar__section">
                        <div class="admin-sidebar__label">Overview</div>

                        <a href="/staff" class="admin-sidebar__item active">
                            <i class="fa-solid fa-chart-pie"></i>
                            <span>Dashboard</span>
                        </a>
                    </div>

                    <div class="admin-sidebar__section">
                        <div class="admin-sidebar__label">Management</div>

                        <a href="/staff/contacts" class="admin-sidebar__item">
                            <i class="fa-solid fa-headset"></i>
                            <span>Contacts</span>
                        </a>
                    </div>
                </nav>

                <div class="admin-sidebar__footer">
                    <a href="/" class="admin-sidebar__item">
                        <i class="fa-solid fa-arrow-up-right-from-square"></i>
                        <span>View Site</span>
                    </a>

                    <form action="/logout" method="post" style="margin:0;">
                        <c:if test="${_csrf != null}">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        </c:if>

                        <button type="submit" class="admin-sidebar__logout"
                            style="width:100%;border:0;background:transparent;cursor:pointer;text-align:left;">
                            <i class="fa-solid fa-right-from-bracket"></i>
                            <span>Logout</span>
                        </button>
                    </form>
                </div>
            </aside>

            <main class="admin-main">

                <header class="admin-topbar">
                    <div class="admin-topbar__brand">
                        <div class="admin-topbar__badge">
                            <i class="fa-solid fa-shield-halved"></i>
                        </div>

                        <div>
                            <h1>Booktify Staff</h1>
                            <p>Manage customer contacts & support requests</p>
                        </div>
                    </div>

                    <div class="admin-topbar__meta">
                        <div class="admin-topbar__user">
                            <div class="admin-topbar__avatar">
                                <i class="fa-solid fa-user"></i>
                            </div>

                            <div>
                                <strong>Booktify Staff</strong>
                                <span>STAFF</span>
                            </div>
                        </div>

                        <a href="/" class="admin-topbar__link">
                            <i class="fa-solid fa-arrow-up-right-from-square"></i>
                            View Site
                        </a>
                    </div>
                </header>

                <section class="admin-content">

                    <section class="admin-hero">
                        <div>
                            <p class="admin-kicker">
                                <i class="fa-solid fa-user-tie"></i>
                                Welcome Back
                            </p>
                            <h2>Dashboard Overview</h2>
                            <p>
                                Monitor your store's books, categories, and user activity from one place.
                            </p>
                        </div>
                    </section>
            </main>
        </body>

        </html>