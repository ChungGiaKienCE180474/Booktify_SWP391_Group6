<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>

    <title>VPP Stationery Detail — Booktify Admin</title>

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
                    <i class="fa-solid fa-eye"></i>
                    VPP STATIONERY DETAIL
                </p>

                <h2>
                    <c:out value="${category.name}"/>
                </h2>
            </div>

            <div style="display:flex; gap:10px; flex-wrap:wrap;">

                <a href="${pageContext.request.contextPath}/admin/vpp/categories"
                   class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-arrow-left"></i>
                    Back
                </a>

                <a href="${pageContext.request.contextPath}/admin/vpp/categories/${category.id}/edit"
                   class="admin-button">
                    <i class="fa-solid fa-pen"></i>
                    Edit
                </a>

            </div>

        </div>

        <div class="admin-panel"
             style="padding:22px;">

            <div style="display:grid; grid-template-columns:1fr 1fr; gap:14px;">

                <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                    <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                        ID
                    </span>
                    <strong>${category.id}</strong>
                </div>

                <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                    <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                        Status
                    </span>

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
                </div>

                <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                    <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                        Category Name
                    </span>
                    <strong>
                        <c:out value="${category.name}"/>
                    </strong>
                </div>

                <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                    <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                        Products
                    </span>
                    <strong>${category.itemCount}</strong>
                </div>

                <div style="grid-column:1 / -1; background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                    <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                        Description
                    </span>

                    <p style="margin:0; line-height:1.7; color:#374151;">
                        <c:choose>
                            <c:when test="${not empty category.description}">
                                <c:out value="${category.description}"/>
                            </c:when>
                            <c:otherwise>No description.</c:otherwise>
                        </c:choose>
                    </p>
                </div>

                <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                    <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                        Created At
                    </span>
                    <strong>${category.createdAt}</strong>
                </div>

                <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                    <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                        Updated At
                    </span>
                    <strong>${category.updatedAt}</strong>
                </div>

            </div>

        </div>

    </section>

</main>

</body>
</html>