<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>

    <title>VPP Product Detail — Booktify Admin</title>

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
                    VPP PRODUCT DETAIL
                </p>

                <h2>
                    <c:out value="${item.name}"/>
                </h2>
            </div>

            <div style="display:flex; gap:10px; flex-wrap:wrap;">

                <a href="${pageContext.request.contextPath}/admin/vpp"
                   class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-arrow-left"></i>
                    Back
                </a>

                <a href="${pageContext.request.contextPath}/admin/vpp/${item.id}/edit"
                   class="admin-button">
                    <i class="fa-solid fa-pen"></i>
                    Edit
                </a>

            </div>

        </div>

        <div class="admin-panel"
             style="padding:22px;">

            <div style="display:grid; grid-template-columns:260px minmax(0,1fr); gap:24px; align-items:start;">

                <div style="border:1px solid #E5E7EB; border-radius:18px; background:#F9FAFB; height:260px; display:grid; place-items:center; overflow:hidden;">

                    <c:choose>
                        <c:when test="${not empty item.imagePath}">
                            <img src="${pageContext.request.contextPath}${item.imagePath}"
                                 alt="${item.name}"
                                 style="width:100%; height:100%; object-fit:contain; padding:14px;">
                        </c:when>

                        <c:otherwise>
                            <i class="fa-solid fa-image"
                               style="font-size:3rem; color:#9CA3AF;"></i>
                        </c:otherwise>
                    </c:choose>

                </div>

                <div style="display:grid; grid-template-columns:1fr 1fr; gap:14px;">

                    <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                        <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                            ID
                        </span>
                        <strong>${item.id}</strong>
                    </div>

                    <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                        <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                            Status
                        </span>

                        <c:choose>
                            <c:when test="${item.active}">
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
                            Category
                        </span>
                        <strong>
                            <c:out value="${item.categoryName}"/>
                        </strong>
                    </div>

                    <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                        <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                            Price
                        </span>
                        <strong style="color:#DC2626;">
                            <fmt:formatNumber value="${item.price}"
                                              type="number"
                                              groupingUsed="true"/>
                            ₫
                        </strong>
                    </div>

                    <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                        <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                            Stock
                        </span>
                        <strong>${item.stockQuantity}</strong>
                    </div>

                    <div style="background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                        <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                            Supplier
                        </span>
                        <strong>
                            <c:choose>
                                <c:when test="${not empty item.supplier}">
                                    <c:out value="${item.supplier}"/>
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </strong>
                    </div>

                    <div style="grid-column:1 / -1; background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                        <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                            Image Path
                        </span>
                        <strong>
                            <c:choose>
                                <c:when test="${not empty item.imagePath}">
                                    <c:out value="${item.imagePath}"/>
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </strong>
                    </div>

                    <div style="grid-column:1 / -1; background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px; padding:14px;">
                        <span style="display:block; color:#6B7280; font-size:.82rem; font-weight:700; margin-bottom:5px;">
                            Description
                        </span>

                        <p style="margin:0; line-height:1.7; color:#374151;">
                            <c:choose>
                                <c:when test="${not empty item.description}">
                                    <c:out value="${item.description}"/>
                                </c:when>
                                <c:otherwise>No description.</c:otherwise>
                            </c:choose>
                        </p>
                    </div>

                </div>

            </div>

        </div>

    </section>

</main>

</body>
</html>