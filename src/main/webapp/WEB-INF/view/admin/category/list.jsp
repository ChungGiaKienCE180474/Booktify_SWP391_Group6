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
                    <input type="text" name="q" value="<c:out value='${q}'/>" placeholder="Search by name or description…"
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
                                <td><c:out value="${category.name}"/></td>
                                <td><c:out value="${category.description}"/></td>
                                <td>
                                    <span class="status-pill ${category.active ? 'status-pill--on' : 'status-pill--off'}">
                                        ${category.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td><c:out value="${category.updatedAtString}" default="—"/></td>
                                <td class="admin-table__actions">
                                    <%-- View --%>
                                    <button type="button" class="icon-link js-view-category" style="cursor:pointer;"
                                            data-name="<c:out value='${category.name}'/>"
                                            data-desc="<c:out value='${category.description}'/>"
                                            data-active="${category.active}"
                                            data-updated="<c:out value='${category.updatedAtString}'/>">
                                        <i class="fa-solid fa-eye"></i>
                                    </button>
                                    <%-- Edit --%>
                                    <a href="/admin/categories/${category.id}/edit" class="icon-link" style="cursor:pointer;">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <%-- Delete --%>
                                    <button type="button" class="icon-link icon-link--danger" style="cursor:pointer;"
                                            onclick="openDeleteModal('/admin/categories/${category.id}/delete', 'Are you sure you want to delete this category?')">
                                        <i class="fa-solid fa-trash"></i>
                                    </button>
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

    <%-- Delete Confirmation Modal --%>
    <div id="deleteModal" class="modal-overlay" style="display:none;">
        <div class="modal-box" style="max-width:420px;" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3><i class="fa-solid fa-triangle-exclamation" style="color:#ef4444;"></i> Confirm Delete</h3>
            </div>
            <div class="modal-body">
                <p id="deleteModalMsg" style="margin:0;font-size:0.95rem;"></p>
            </div>
            <div style="display:flex;justify-content:flex-end;gap:12px;padding:16px 24px;border-top:1px solid var(--admin-border,#e5e7eb);">
                <button onclick="closeDeleteModal()" class="admin-button admin-button--ghost" style="cursor:pointer;">Cancel</button>
                <form id="deleteForm" method="post" style="display:inline;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <button type="submit" class="admin-button" style="background:#ef4444;border-color:#ef4444;cursor:pointer;">
                        <i class="fa-solid fa-trash"></i> Delete
                    </button>
                </form>
            </div>
        </div>
    </div>

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
    <c:if test="${not empty successMessage}">
        <div id="toastSuccessMessage" style="display:none;"><c:out value="${successMessage}"/></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div id="toastErrorMessage" style="display:none;"><c:out value="${errorMessage}"/></div>
    </c:if>

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

        var successToastEl = document.getElementById('toastSuccessMessage');
        if (successToastEl) { showToast(successToastEl.textContent.trim(), 'success'); }
        var errorToastEl = document.getElementById('toastErrorMessage');
        if (errorToastEl) { showToast(errorToastEl.textContent.trim(), 'error'); }

        // ── Delete modal ──
        function openDeleteModal(action, message) {
            document.getElementById('deleteModalMsg').textContent = message;
            document.getElementById('deleteForm').action = action;
            document.getElementById('deleteModal').style.display = 'flex';
        }
        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }

        // ── View modal (data-* approach) ──
        document.querySelectorAll('.js-view-category').forEach(function(btn) {
            btn.addEventListener('click', function() {
                var d = this.dataset;
                document.getElementById('mCatName').textContent    = d.name    || '—';
                document.getElementById('mCatDesc').textContent    = d.desc    || '—';
                document.getElementById('mCatStatus').innerHTML    = d.active === 'true'
                    ? '<span class="status-pill status-pill--on">Active</span>'
                    : '<span class="status-pill status-pill--off">Inactive</span>';
                document.getElementById('mCatUpdated').textContent = d.updated || '—';
                document.getElementById('categoryModal').style.display = 'flex';
            });
        });

        function closeModal(id) {
            document.getElementById(id).style.display = 'none';
        }
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                document.querySelectorAll('.modal-overlay').forEach(function(m){ m.style.display='none'; });
            }
        });
    </script>
</body>
</html>
