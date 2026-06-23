<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title>Categories — Booktify Admin</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content">

            <%-- Page Header --%>
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-tag"></i> Category Management</p>
                    <h2>Categories</h2>
                </div>
                <a href="/admin/categories/create" class="admin-button">
                    <i class="fa-solid fa-plus"></i> New Category
                </a>
            </div>

            <%-- Search --%>
            <div class="admin-panel" style="padding:14px 22px;">
                <form method="get" action="/admin/categories" class="admin-search-form">
                    <div style="position:relative;flex:1;max-width:440px;">
                        <i class="fa-solid fa-magnifying-glass"
                           style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>
                        <input type="text" name="q" value="<c:out value='${q}'/>"
                               placeholder="Search by name or description…"
                               class="admin-input" style="padding-left:38px;" />
                    </div>
                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-magnifying-glass"></i> Search
                    </button>
                    <c:if test="${not empty q}">
                        <a href="/admin/categories" class="admin-button admin-button--ghost">
                            <i class="fa-solid fa-xmark"></i> Clear
                        </a>
                    </c:if>
                </form>
            </div>

            <%-- Table --%>
            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th style="width:48px;">#</th>
                            <th>Name</th>
                            <th>Description</th>
                            <th>Status</th>
                            <th>Last Updated</th>
                            <th style="width:116px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${categories}" var="category" varStatus="vs">
                            <tr>
                                <td style="color:#9CA3AF;font-weight:600;">${vs.index + 1}</td>
                                <td>
                                    <div style="font-weight:700;color:#111827;">
                                        <c:out value="${category.name}"/>
                                    </div>
                                </td>
                                <td style="color:#374151;font-size:.875rem;max-width:280px;">
                                    <c:out value="${category.description}" default="—"/>
                                </td>
                                <td>
                                    <span class="status-pill ${category.active ? 'status-pill--on' : 'status-pill--off'}">
                                        <i class="fa-solid ${category.active ? 'fa-circle-check' : 'fa-circle-xmark'}"
                                           style="font-size:.6rem;"></i>
                                        ${category.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td style="font-size:.8rem;color:#6B7280;">
                                    <c:out value="${category.updatedAtString}" default="—"/>
                                </td>
                                <td class="admin-table__actions">
                                    <%-- View --%>
                                    <button type="button" class="icon-link js-view-category" title="View"
                                            data-name="<c:out value='${category.name}'/>"
                                            data-desc="<c:out value='${category.description}'/>"
                                            data-active="${category.active}"
                                            data-updated="<c:out value='${category.updatedAtString}'/>">
                                        <i class="fa-solid fa-eye"></i>
                                    </button>
                                    <%-- Edit --%>
                                    <a href="/admin/categories/${category.id}/edit" class="icon-link icon-link--edit" title="Edit">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <%-- Delete --%>
                                    <button type="button" class="icon-link icon-link--danger" title="Delete"
                                            onclick="openDeleteModal('/admin/categories/${category.id}/delete','Are you sure you want to delete this category?')">
                                        <i class="fa-solid fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty categories}">
                            <tr>
                                <td colspan="6" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                    <i class="fa-solid fa-tag"
                                       style="font-size:2.2rem;display:block;margin-bottom:10px;opacity:.3;"></i>
                                    No categories found.
                                </td>
                            </tr>
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
                <h3>
                    <i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i>
                    Confirm Delete
                </h3>
            </div>
            <div class="modal-body" style="display:block;">
                <p id="deleteModalMsg" style="margin:0;font-size:.9rem;color:#374151;line-height:1.65;"></p>
                <p style="margin:10px 0 0;font-size:.8rem;color:#9CA3AF;">
                    This action cannot be undone.
                </p>
            </div>
            <div class="modal-footer">
                <button onclick="closeDeleteModal()" class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-xmark"></i> Cancel
                </button>
                <form id="deleteForm" method="post" style="display:inline;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <button type="submit" class="admin-button admin-button--danger">
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
                <h3><i class="fa-solid fa-tag" style="color:#2563EB;"></i> Category Details</h3>
                <button class="modal-close" onclick="closeModal('categoryModal')">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <div class="modal-row"><span class="modal-label">Name</span><span id="mCatName" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Description</span><span id="mCatDesc" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Status</span><span id="mCatStatus" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Updated</span><span id="mCatUpdated" class="modal-value"></span></div>
            </div>
        </div>
    </div>

    <%-- Toast --%>
    <div id="toastContainer" class="toast-container"></div>
    <c:if test="${not empty successMessage}">
        <div id="toastSuccessMessage" style="display:none;"><c:out value="${successMessage}"/></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div id="toastErrorMessage" style="display:none;"><c:out value="${errorMessage}"/></div>
    </c:if>

    <script>
        function showToast(msg, type) {
            var tc = document.getElementById('toastContainer');
            var t = document.createElement('div');
            t.className = 'toast toast--' + type;
            t.innerHTML = '<i class="fa-solid ' + (type==='success' ? 'fa-circle-check' : 'fa-circle-exclamation') + '"></i> ' + msg;
            tc.appendChild(t);
            setTimeout(function(){ t.classList.add('toast--show'); }, 10);
            setTimeout(function(){ t.classList.remove('toast--show'); setTimeout(function(){ t.remove(); }, 320); }, 3500);
        }
        var s = document.getElementById('toastSuccessMessage');
        if (s) showToast(s.textContent.trim(), 'success');
        var e = document.getElementById('toastErrorMessage');
        if (e) showToast(e.textContent.trim(), 'error');

        function openDeleteModal(action, msg) {
            document.getElementById('deleteModalMsg').textContent = msg;
            document.getElementById('deleteForm').action = action;
            document.getElementById('deleteModal').style.display = 'flex';
        }
        function closeDeleteModal() { document.getElementById('deleteModal').style.display = 'none'; }

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

        function closeModal(id) { document.getElementById(id).style.display = 'none'; }
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') document.querySelectorAll('.modal-overlay').forEach(function(m){ m.style.display='none'; });
        });
    </script>
</body>
</html>
