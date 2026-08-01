<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <title>Book sets — Booktify Admin</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />
    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />
        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-layer-group"></i> Catalog</p>
                    <h2>Book sets</h2>
                </div>
                <a href="/admin/book-sets/create" class="admin-button">
                    <i class="fa-solid fa-plus"></i> New book set
                </a>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="admin-alert admin-alert--success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert--error">${errorMessage}</div>
            </c:if>

            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Tag / Series</th>
                        <th>Books</th>
                        <th>Set price</th>
                        <th>Retail total</th>
                        <th>Available</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${bookSets}" var="set" varStatus="st">
                        <tr>
                            <td>${st.index + 1}</td>
                            <td><strong><c:out value="${set.name}"/></strong></td>
                            <td><c:out value="${tagLabelMap[set.id]}" default="General"/></td>
                            <td>${set.items.size()}</td>
                            <td>${set.setPrice} &#8363;</td>
                            <td>${retailTotalMap[set.id]} &#8363;</td>
                            <td>${availableQtyMap[set.id]}</td>
                            <td>
                                <span class="status-pill ${set.active ? 'status-pill--on' : 'status-pill--off'}">
                                    ${set.active ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td class="admin-table__actions">
                                <a href="/admin/book-sets/${set.id}/edit" class="icon-link" title="Edit">
                                    <i class="fa-solid fa-pen"></i>
                                </a>
                                <form method="post" action="/admin/book-sets/${set.id}/status" style="display:inline;">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <input type="hidden" name="active" value="${!set.active}" />
                                    <button type="submit" class="icon-link" title="${set.active ? 'Deactivate' : 'Activate'}">
                                        <i class="fa-solid ${set.active ? 'fa-eye-slash' : 'fa-eye'}"></i>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty bookSets}">
                        <tr>
                            <td colspan="9" style="text-align:center;padding:2rem;color:#9CA3AF;">
                                No book sets yet.
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</body>
</html>
