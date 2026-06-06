<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title>Category Management</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker">CRUD category</p>
                    <h2>Categories</h2>
                </div>
                <a href="/admin/categories/create" class="admin-button">New category</a>
            </div>

            <c:if test="${not empty param.created or not empty param.updated or not empty param.deleted}">
                <div class="admin-alert">Saved successfully.</div>
            </c:if>

            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Description</th>
                            <th>Status</th>
                            <th>Updated</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${categories}" var="category">
                            <tr>
                                <td>${category.id}</td>
                                <td>${category.name}</td>
                                <td>${category.description}</td>
                                <td>
                                    <span class="status-pill ${category.active ? 'status-pill--on' : 'status-pill--off'}">
                                        ${category.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td>${category.updatedAt}</td>
                                <td class="admin-table__actions">
                                    <a href="/admin/categories/${category.id}/edit" class="icon-link"><i class="fa-solid fa-pen"></i></a>
                                    <form action="/admin/categories/${category.id}/delete" method="post" class="inline-form"
                                          onsubmit="return confirm('Delete this category?');">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                        <button type="submit" class="icon-link icon-link--danger"><i class="fa-solid fa-trash"></i></button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</body>
</html>