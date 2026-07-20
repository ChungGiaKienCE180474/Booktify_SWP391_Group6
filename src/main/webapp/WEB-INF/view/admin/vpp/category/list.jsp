<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>

    <title>VPP Categories — Booktify Admin</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"/>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin-dashboard.css"/>
</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp"/>

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp"/>

    <section class="admin-content">

        <div class="admin-toolbar">

            <div>
                <p class="admin-kicker">
                    <i class="fa-solid fa-folder-tree"></i>
                     STATIONERY MANAGEMENT
                </p>

                <h2>VPP </h2>
            </div>

            <div style="display:flex; gap:10px; flex-wrap:wrap;">

                <a href="${pageContext.request.contextPath}/admin/vpp"
                   class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-box"></i>
                    VPP 
                </a>

                <a href="${pageContext.request.contextPath}/admin/vpp/categories/create"
                   class="admin-button">
                    <i class="fa-solid fa-plus"></i>
                    New Category
                </a>

            </div>

        </div>

        <c:if test="${not empty successMessage}">
            <div class="admin-alert admin-alert--success">
                <c:out value="${successMessage}"/>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="admin-alert admin-alert--danger">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <div class="admin-panel">

            <table class="admin-table">

                <thead>
                <tr>
                    <th style="width:48px;">#</th>
                    <th>Category Name</th>
                    <th>Description</th>
                    <th>Products</th>
                    <th>Status</th>
                    <th style="width:220px;">Actions</th>
                </tr>
                </thead>

                <tbody>

                <c:choose>

                    <c:when test="${empty categories}">
                        <tr>
                            <td colspan="6"
                                style="text-align:center; padding:56px 20px; color:#9CA3AF;">
                                No VPP categories found.
                            </td>
                        </tr>
                    </c:when>

                    <c:otherwise>

                        <c:forEach items="${categories}"
                                   var="category"
                                   varStatus="loop">

                            <tr>
                                <td style="color:#9CA3AF; font-weight:600;">
                                    ${loop.index + 1}
                                </td>

                                <td>
                                    <strong>
                                        <c:out value="${category.name}"/>
                                    </strong>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty category.description}">
                                            <c:out value="${category.description}"/>
                                        </c:when>
                                        <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <strong>${category.itemCount}</strong>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${category.active}">
                                            <span class="status-pill status-pill--on">
                                                Active
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <span class="status-pill status-pill--off">
                                                Hidden
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="admin-table__actions">

                                    <a href="${pageContext.request.contextPath}/admin/vpp/categories/${category.id}"
                                       class="icon-link"
                                       title="View">
                                        <i class="fa-solid fa-eye"></i>
                                    </a>

                                    <a href="${pageContext.request.contextPath}/admin/vpp/categories/${category.id}/edit"
                                       class="icon-link icon-link--edit"
                                       title="Edit">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>

                                    <c:choose>

                                        <c:when test="${category.active}">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/admin/vpp/categories/${category.id}/hide"
                                                  style="display:inline;">
                                                <input type="hidden"
                                                       name="${_csrf.parameterName}"
                                                       value="${_csrf.token}"/>

                                                <button type="submit"
                                                        class="icon-link"
                                                        title="Hide">
                                                    <i class="fa-solid fa-eye-slash"></i>
                                                </button>
                                            </form>
                                        </c:when>

                                        <c:otherwise>
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/admin/vpp/categories/${category.id}/restore"
                                                  style="display:inline;">
                                                <input type="hidden"
                                                       name="${_csrf.parameterName}"
                                                       value="${_csrf.token}"/>

                                                <button type="submit"
                                                        class="icon-link"
                                                        title="Restore">
                                                    <i class="fa-solid fa-rotate-left"></i>
                                                </button>
                                            </form>
                                        </c:otherwise>

                                    </c:choose>

                                </td>
                            </tr>

                        </c:forEach>

                    </c:otherwise>

                </c:choose>

                </tbody>

            </table>

        </div>

    </section>

</main>

</body>
</html>