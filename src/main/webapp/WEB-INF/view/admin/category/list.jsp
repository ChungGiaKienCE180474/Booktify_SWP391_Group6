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
                <a href="/admin/categories/create" class="admin-button" style="cursor:pointer;">
                    <i class="fa-solid fa-plus"></i> New category
                </a>
            </div>

            <%-- Search bar --%>
            <div class="admin-panel" style="padding:16px 24px;">
                <form method="get" action="/admin/categories" class="admin-search-form">
                    <input type="text" name="q" value="${q}" placeholder="Search by name or description…"
                           class="admin-input" style="max-width:400px;" />
                    <button type="submit" class="admin-button" style="cursor:pointer;">
                        <i class="fa-solid fa-magnifying-glass"></i> Search
                    </button>
                    <c:if test="${not empty q}">
                        <a href="/admin/categories" class="admin-button admin-button--ghost" style="cursor:pointer;">Clear</a>
                    </c:if>
                </form>
            </div>

            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Description</th>
                            <th>Status</th>
                            <th>Updated</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${categories}" var="category" varStatus="vs">
                            <tr>
                                <td>${vs.index + 1}</td>
                                <td>${category.name}</td>
                                <td>${category.description}</td>
                                <td>
                                    <span class="status-pill ${category.active ? 'status-pill--on' : 'status-pill--off'}">
                                        ${category.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td>${category.updatedAt}</td>
                                <td class="admin-table__actions">
                                    <%-- View --%>
                                    <button type="button" class="icon-link" style="cursor:pointer;"
                                            onclick="openCategoryModal(${category.id}, '${category.name}', '${category.description}', '${category.active}', '${category.updatedAt}')">
                                        <i class="fa-solid fa-eye"></i>
                                    </button>
                                    <%-- Edit --%>
                                    <a href="/admin/categories/${category.id}/edit" class="icon-link" style="cursor:pointer;">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <%-- Delete --%>
                                    <form action="/admin/categories/${category.id}/delete" method="post" class="inline-form"
                                          onsubmit="return confirm('Delete this category?');">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                        <button type="submit" class="icon-link icon-link--danger" style="cursor:pointer;">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty categories}">
                            <tr><td colspan="6" style="text-align:center;color:var(--admin-muted);padding:2rem;">
                                No categories found.
                            </td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </section>
    </main>

    <%-- View Detail Modal --%>
    <div id="categoryModal" class="modal-overlay" style="display:none;" onclick="closeModal('categoryModal')">
        <div class="modal-box" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3><i class="fa-solid fa-tag"></i> Category Details</h3>
                <button class="modal-close" onclick="closeModal('categoryModal')" style="cursor:pointer;">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <div class="modal-row"><span class="modal-label">Name</span><span id="mCatName" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Description</span><span id="mCatDesc" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Status</span><span id="mCatStatus" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Last updated</span><span id="mCatUpdated" class="modal-value"></span></div>
            </div>
        </div>
    </div>

    <%-- Toast container --%>
    <div id="toastContainer" class="toast-container"></div>

    <script>
        // ── Toast ──
        function showToast(message, type) {
            var tc = document.getElementById('toastContainer');
            var t = document.createElement('div');
            t.className = 'toast toast--' + type;
            t.innerHTML = '<i class="fa-solid ' + (type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation') + '"></i> ' + message;
            tc.appendChild(t);
            setTimeout(function() { t.classList.add('toast--show'); }, 10);
            setTimeout(function() { t.classList.remove('toast--show'); setTimeout(function(){ t.remove(); }, 400); }, 3500);
        }

        <c:if test="${not empty successMessage}">
            window.addEventListener('DOMContentLoaded', function() { showToast('${successMessage}', 'success'); });
        </c:if>
        <c:if test="${not empty errorMessage}">
            window.addEventListener('DOMContentLoaded', function() { showToast('${errorMessage}', 'error'); });
        </c:if>

        // ── Modal ──
        function openCategoryModal(id, name, desc, active, updated) {
            document.getElementById('mCatName').textContent    = name || '—';
            document.getElementById('mCatDesc').textContent    = desc || '—';
            document.getElementById('mCatStatus').innerHTML    = active === 'true'
                ? '<span class="status-pill status-pill--on">Active</span>'
                : '<span class="status-pill status-pill--off">Inactive</span>';
            document.getElementById('mCatUpdated').textContent = updated || '—';
            document.getElementById('categoryModal').style.display = 'flex';
        }
        function closeModal(id) {
            document.getElementById(id).style.display = 'none';
        }
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') { document.querySelectorAll('.modal-overlay').forEach(function(m){ m.style.display='none'; }); }
        });
    </script>
</body>
</html>
