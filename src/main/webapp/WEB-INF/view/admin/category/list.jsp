<%-- Category list with search, status filter and pagination. --%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <title>Categories — Booktify Admin</title>
    <style>
        .icon-link--restore { color:#059669; }
        .icon-link--restore:hover { color:#047857; background:#D1FAE5; }
    </style>
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
                <a href="/admin/categories/create" id="btnNewCategory" class="admin-button">
                    <i class="fa-solid fa-plus"></i> New Category
                </a>
            </div>

            <%-- Book / Stationery tabs — same page, same "Categories" sidebar entry,
                 just two independent panes switched client-side. --%>
            <div class="admin-tabs">
                <button type="button" class="admin-tab active" data-tab="book">
                    <i class="fa-solid fa-book"></i> Book Categories
                </button>
                <button type="button" class="admin-tab" data-tab="stationery">
                    <i class="fa-solid fa-pen-ruler"></i> Stationery Categories
                </button>
            </div>

            <div id="tab-book" class="tab-pane active">

            <%-- Search + Filter --%>
            <div class="admin-panel" style="padding:14px 22px;">
                <form method="get" action="/admin/categories" class="admin-search-form">
                    <div style="position:relative;flex:1;max-width:380px;">
                        <i class="fa-solid fa-magnifying-glass"
                           style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>
                        <input type="text" name="q" value="<c:out value='${q}'/>"
                               placeholder="Search by name or description…"
                               class="admin-input" style="padding-left:38px;" />
                    </div>
                    <select name="status" class="admin-input" style="max-width:160px;">
                        <option value="" <c:if test="${empty status}">selected</c:if>>All Status</option>
                        <option value="active" <c:if test="${status == 'active'}">selected</c:if>>Active</option>
                        <option value="inactive" <c:if test="${status == 'inactive'}">selected</c:if>>Inactive</option>
                    </select>
                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-filter"></i> Filter
                    </button>
                    <a href="/admin/categories" class="admin-button admin-button--ghost">
                        <i class="fa-solid fa-rotate-right"></i> Reset
                    </a>
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
                            <th style="width:120px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${categories}" var="category" varStatus="vs">
                            <tr>
                                <td style="color:#9CA3AF;font-weight:600;">${fromItem + vs.index}</td>
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
                                    <button type="button" class="icon-link js-view-category" title="View"
                                            data-name="<c:out value='${category.name}'/>"
                                            data-desc="<c:out value='${category.description}'/>"
                                            data-active="${category.active}"
                                            data-updated="<c:out value='${category.updatedAtString}'/>">
                                        <i class="fa-solid fa-eye"></i>
                                    </button>

                                    <%-- Edit/Delete for active rows, Restore for soft-deleted ones --%>
                                    <c:choose>
                                        <c:when test="${category.active}">
                                            <a href="/admin/categories/${category.id}/edit"
                                               class="icon-link icon-link--edit" title="Edit">
                                                <i class="fa-solid fa-pen"></i>
                                            </a>
                                            <button type="button" class="icon-link icon-link--danger" title="Delete"
                                                    onclick="openConfirmModal('/admin/categories/${category.id}/delete','delete','Are you sure you want to delete this category?')">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="icon-link icon-link--restore" title="Restore"
                                                    onclick="openConfirmModal('/admin/categories/${category.id}/restore','restore','Are you sure you want to restore this category?')">
                                                <i class="fa-solid fa-rotate-left"></i>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
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
                <div class="admin-pagination">
                    <div class="admin-pagination__info">
                        <c:choose>
                            <c:when test="${totalItems == 0}">No entries found.</c:when>
                            <c:otherwise>
                                Showing <strong>${fromItem}</strong> to <strong>${toItem}</strong> of <strong>${totalItems}</strong> entr${totalItems == 1 ? 'y' : 'ies'}<c:if test="${not empty q or not empty status}"> (filtered)</c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <c:set var="pagBase" value="/admin/categories?q=${q}&status=${status}"/>
                        <div class="admin-pagination__nav">
                            <a class="pag-btn ${currentPage == 0 || viewingAll ? 'pag-btn--disabled' : ''}"
                               href="${pagBase}&page=0">First</a>
                            <a class="pag-btn ${currentPage == 0 || viewingAll ? 'pag-btn--disabled' : ''}"
                               href="${pagBase}&page=${currentPage - 1}">Prev</a>
                            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                <a class="pag-btn ${i == currentPage && !viewingAll ? 'pag-btn--active' : ''}"
                                   href="${pagBase}&page=${i}">
                                    ${i + 1}
                                </a>
                            </c:forEach>
                            <a class="pag-btn ${currentPage >= totalPages - 1 || viewingAll ? 'pag-btn--disabled' : ''}"
                               href="${pagBase}&page=${currentPage + 1}">Next</a>
                            <a class="pag-btn ${currentPage >= totalPages - 1 || viewingAll ? 'pag-btn--disabled' : ''}"
                               href="${pagBase}&page=${totalPages - 1}">Last</a>
                            <a class="pag-btn pag-btn--all ${viewingAll ? 'pag-btn--active' : ''}"
                               href="${pagBase}&all=true">All</a>
                        </div>
                    </c:if>
                </div>
            </div>

            </div> <%-- /tab-book --%>

            <div id="tab-stationery" class="tab-pane" style="display:none;">

                <%-- Categories are all loaded at once (small list, no pagination),
                     so search/status filter run as a plain client-side row
                     filter — see script below — instead of a server round-trip. --%>
                <div class="admin-panel" style="padding:14px 22px;">
                    <div class="admin-search-form">
                        <div style="position:relative;flex:1;max-width:380px;">
                            <i class="fa-solid fa-magnifying-glass"
                               style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>
                            <input type="text" id="vppSearchInput"
                                   placeholder="Search by name or description…"
                                   class="admin-input" style="padding-left:38px;" />
                        </div>
                        <select id="vppStatusFilter" class="admin-input" style="max-width:160px;">
                            <option value="">All Status</option>
                            <option value="active">Active</option>
                            <option value="hidden">Hidden</option>
                        </select>
                        <button type="button" id="vppFilterBtn" class="admin-button">
                            <i class="fa-solid fa-filter"></i> Filter
                        </button>
                        <button type="button" id="vppResetBtn" class="admin-button admin-button--ghost">
                            <i class="fa-solid fa-rotate-right"></i> Reset
                        </button>
                    </div>
                </div>

                <div class="admin-table-wrap">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th style="width:48px;">#</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th style="width:120px;">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="vppTableBody">
                            <c:forEach items="${vppCategories}" var="vcat" varStatus="vs">
                                <tr class="vpp-row"
                                    data-search="<c:out value='${vcat.name}'/> <c:out value='${vcat.description}'/>"
                                    data-status="${vcat.active ? 'active' : 'hidden'}">
                                    <td style="color:#9CA3AF;font-weight:600;">${vs.index + 1}</td>
                                    <td>
                                        <div style="font-weight:700;color:#111827;">
                                            <c:out value="${vcat.name}"/>
                                        </div>
                                    </td>
                                    <td style="color:#374151;font-size:.875rem;max-width:280px;">
                                        <c:out value="${vcat.description}" default="—"/>
                                    </td>
                                    <td>
                                        <span class="status-pill ${vcat.active ? 'status-pill--on' : 'status-pill--off'}">
                                            <i class="fa-solid ${vcat.active ? 'fa-circle-check' : 'fa-circle-xmark'}"
                                               style="font-size:.6rem;"></i>
                                            ${vcat.active ? 'Active' : 'Hidden'}
                                        </span>
                                    </td>
                                    <td class="admin-table__actions">
                                        <a href="/admin/vpp/categories/${vcat.id}" class="icon-link" title="View">
                                            <i class="fa-solid fa-eye"></i>
                                        </a>
                                        <a href="/admin/vpp/categories/${vcat.id}/edit" class="icon-link icon-link--edit" title="Edit">
                                            <i class="fa-solid fa-pen"></i>
                                        </a>
                                        <c:choose>
                                            <c:when test="${vcat.active}">
                                                <form method="post" action="/admin/vpp/categories/${vcat.id}/hide" style="display:inline;">
                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                    <button type="submit" class="icon-link" title="Delete">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <form method="post" action="/admin/vpp/categories/${vcat.id}/restore" style="display:inline;">
                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                    <button type="submit" class="icon-link icon-link--restore" title="Restore">
                                                        <i class="fa-solid fa-rotate-left"></i>
                                                    </button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty vppCategories}">
                                <tr>
                                    <td colspan="5" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                        No stationery categories found.
                                    </td>
                                </tr>
                            </c:if>
                            <tr id="vppNoMatchRow" style="display:none;">
                                <td colspan="5" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                    No categories match your search.
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

            </div> <%-- /tab-stationery --%>

        </section>
    </main>

    <%-- Unified Confirm Modal (Remove / Restore) --%>
    <div id="confirmModal" class="modal-overlay" style="display:none;" onclick="closeConfirmModal()">
        <div class="modal-box" style="max-width:420px;" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3 id="confirmModalTitle">
                    <i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i>
                    Confirm Action
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
        function closeConfirmModal() { document.getElementById('confirmModal').style.display = 'none'; }

        document.addEventListener('click', function (e) {
            var btn = e.target.closest('.js-view-category');
            if (!btn) return;
            var d = btn.dataset;
            document.getElementById('mCatName').textContent    = d.name    || '—';
            document.getElementById('mCatDesc').textContent    = d.desc    || '—';
            document.getElementById('mCatStatus').innerHTML    = d.active === 'true'
                ? '<span class="status-pill status-pill--on">Active</span>'
                : '<span class="status-pill status-pill--off">Inactive</span>';
                        document.getElementById('mCatUpdated').textContent = d.updated || '—';
            document.getElementById('categoryModal').style.display = 'flex';
        });
        function closeModal(id) { document.getElementById(id).style.display = 'none'; }

        // Book / Stationery tabs — client-side only, no reload. Defaults to
        // Book; re-opens Stationery after a redirect if the URL says so.
        var newCategoryLinks = {
            book: '/admin/categories/create',
            stationery: '/admin/vpp/categories/create'
        };
        document.querySelectorAll('.admin-tab').forEach(function (tab) {
            tab.addEventListener('click', function () {
                document.querySelectorAll('.admin-tab').forEach(function (t) { t.classList.remove('active'); });
                document.querySelectorAll('.tab-pane').forEach(function (p) { p.style.display = 'none'; p.classList.remove('active'); });
                tab.classList.add('active');
                var pane = document.getElementById('tab-' + tab.dataset.tab);
                pane.style.display = '';
                pane.classList.add('active');
                document.getElementById('btnNewCategory').href = newCategoryLinks[tab.dataset.tab];
            });
        });
        if (new URLSearchParams(window.location.search).get('tab') === 'stationery') {
            document.querySelector('.admin-tab[data-tab="stationery"]').click();
        }

        // Stationery search + status filter — plain client-side filter over the
        // already-loaded rows (no pagination on this tab, so no server round-trip).
        var vppSearchInput = document.getElementById('vppSearchInput');
        var vppStatusFilter = document.getElementById('vppStatusFilter');
        var vppFilterBtn = document.getElementById('vppFilterBtn');
        var vppResetBtn = document.getElementById('vppResetBtn');

        function applyVppFilter() {
            var query = vppSearchInput.value.trim().toLowerCase();
            var status = vppStatusFilter.value;
            var rows = document.querySelectorAll('#vppTableBody .vpp-row');
            var visibleCount = 0;
            rows.forEach(function (row) {
                var matchesQuery = row.dataset.search.toLowerCase().indexOf(query) > -1;
                var matchesStatus = !status || row.dataset.status === status;
                var match = matchesQuery && matchesStatus;
                row.style.display = match ? '' : 'none';
                if (match) visibleCount++;
            });
            document.getElementById('vppNoMatchRow').style.display = visibleCount === 0 ? '' : 'none';
        }

        if (vppSearchInput) {
            vppSearchInput.addEventListener('input', applyVppFilter);
            vppStatusFilter.addEventListener('change', applyVppFilter);
            vppFilterBtn.addEventListener('click', applyVppFilter);
            vppResetBtn.addEventListener('click', function () {
                vppSearchInput.value = '';
                vppStatusFilter.value = '';
                applyVppFilter();
            });
        }
    </script>
</body>
</html>
