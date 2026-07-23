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

        <div class="admin-panel">

            <table class="admin-table">

                <thead>
                <tr>
                    <th style="width:48px;">#</th>
                    <th>Category Name</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th style="width:220px;">Actions</th>
                </tr>
                </thead>

                <tbody>

                <c:choose>

                    <c:when test="${empty categories}">
                        <tr>
                            <td colspan="5"
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
                                            <button type="button"
                                                    class="icon-link"
                                                    title="Hide"
                                                    onclick="openConfirmModal('${pageContext.request.contextPath}/admin/vpp/categories/${category.id}/hide','delete','Are you sure you want to delete this category? It will be hidden, not permanently deleted.')">
                                                <i class="fa-solid fa-eye-slash"></i>
                                            </button>
                                        </c:when>

                                        <c:otherwise>
                                            <button type="button"
                                                    class="icon-link"
                                                    title="Restore"
                                                    onclick="openConfirmModal('${pageContext.request.contextPath}/admin/vpp/categories/${category.id}/restore','restore','Are you sure you want to restore this category?')">
                                                <i class="fa-solid fa-rotate-left"></i>
                                            </button>
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

<%-- Unified Confirm Modal (Delete / Restore) --%>
<div id="confirmModal" class="modal-overlay" style="display:none;" onclick="closeConfirmModal()">
    <div class="modal-box" style="max-width:420px;" onclick="event.stopPropagation()">
        <div class="modal-header">
            <h3 id="confirmModalTitle">
                <i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i>
                Confirm Delete
            </h3>
        </div>
        <div class="modal-body" style="display:block;">
            <p id="confirmModalMsg" style="margin:0;font-size:.9rem;color:#374151;line-height:1.65;"></p>
        </div>
        <div class="modal-footer">
            <button onclick="closeConfirmModal()" class="admin-button admin-button--ghost">
                <i class="fa-solid fa-xmark"></i> Cancel
            </button>
            <form id="confirmForm" method="post" style="display:inline;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <button type="submit" id="confirmSubmitBtn" class="admin-button admin-button--danger">
                    <i class="fa-solid fa-trash"></i> Delete
                </button>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/layout/admin/toast.jsp" />

<script>
    function openConfirmModal(action, type, msg) {
        document.getElementById('confirmModalMsg').textContent = msg;
        document.getElementById('confirmForm').action = action;
        var title = document.getElementById('confirmModalTitle');
        var btn = document.getElementById('confirmSubmitBtn');
        if (type === 'restore') {
            title.innerHTML = '<i class="fa-solid fa-rotate-left" style="color:#059669;"></i> Confirm Restore';
            btn.className = 'admin-button';
            btn.innerHTML = '<i class="fa-solid fa-rotate-left"></i> Restore';
        } else {
            title.innerHTML = '<i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i> Confirm Delete';
            btn.className = 'admin-button admin-button--danger';
            btn.innerHTML = '<i class="fa-solid fa-trash"></i> Delete';
        }
        document.getElementById('confirmModal').style.display = 'flex';
    }

    function closeConfirmModal() {
        document.getElementById('confirmModal').style.display = 'none';
    }
</script>

</body>
</html>