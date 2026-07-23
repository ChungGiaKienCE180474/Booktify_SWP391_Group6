<%-- Genre list with search, category/status filters and pagination. --%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <title>Genres — Booktify Admin</title>
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
                    <p class="admin-kicker"><i class="fa-solid fa-layer-group"></i> Genre Management</p>
                    <h2>Genres</h2>
                </div>
                <a href="/admin/genres/create" class="admin-button">
                    <i class="fa-solid fa-plus"></i> New Genre
                </a>
            </div>

            <%-- Search + Filter --%>
            <div class="admin-panel" style="padding:14px 22px;">
                <form method="get" action="/admin/genres" class="admin-search-form">
                    <div style="position:relative;flex:1;max-width:300px;">
                        <i class="fa-solid fa-magnifying-glass"
                           style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>
                        <input type="text" name="q" value="<c:out value='${q}'/>"
                               placeholder="Search by name or description…"
                               class="admin-input" style="padding-left:38px;" />
                    </div>
                    <select name="categoryId" class="admin-input" style="max-width:200px;">
                        <option value="">All Categories</option>
                        <c:forEach items="${categories}" var="cat">
                            <option value="${cat.id}"
                                <c:if test="${cat.id == selectedCategoryId}">selected</c:if>>
                                <c:out value="${cat.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <select name="status" class="admin-input" style="max-width:150px;">
                        <option value="" <c:if test="${empty status}">selected</c:if>>All Status</option>
                        <option value="active" <c:if test="${status == 'active'}">selected</c:if>>Active</option>
                        <option value="inactive" <c:if test="${status == 'inactive'}">selected</c:if>>Inactive</option>
                    </select>
                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-filter"></i> Filter
                    </button>
                    <a href="/admin/genres" class="admin-button admin-button--ghost">
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
                            <th>Category</th>
                            <th>Description</th>
                            <th>Status</th>
                            <th>Last Updated</th>
                            <th style="width:120px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${genres}" var="genre" varStatus="vs">
                            <tr>
                                <td style="color:#9CA3AF;font-weight:600;">${fromItem + vs.index}</td>
                                <td>
                                    <div style="font-weight:700;color:#111827;">
                                        <c:out value="${genre.name}"/>
                                    </div>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty genre.category}">
                                            <span style="display:inline-flex;align-items:center;gap:4px;
                                                         background:#E3F4F1;border:1px solid #B7DED7;
                                                         color:#006B5E;padding:3px 10px;border-radius:999px;
                                                         font-size:.72rem;font-weight:700;">
                                                <i class="fa-solid fa-tag" style="font-size:.6rem;"></i>
                                                <c:out value="${genre.category.name}"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise><span style="color:#9CA3AF;">—</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="color:#374151;font-size:.875rem;max-width:240px;">
                                    <c:out value="${genre.description}" default="—"/>
                                </td>
                                <td>
                                    <span class="status-pill ${genre.active ? 'status-pill--on' : 'status-pill--off'}">
                                        <i class="fa-solid ${genre.active ? 'fa-circle-check' : 'fa-circle-xmark'}"
                                           style="font-size:.6rem;"></i>
                                        ${genre.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td style="font-size:.8rem;color:#6B7280;">
                                    <c:out value="${genre.updatedAtString}" default="—"/>
                                </td>
                                <td class="admin-table__actions">
                                    <button type="button" class="icon-link js-view-genre" title="View"
                                            data-id="${genre.id}">
                                        <i class="fa-solid fa-eye"></i>
                                    </button>

                                    <%-- Edit/Delete for active rows, Restore for soft-deleted ones --%>
                                    <c:choose>
                                        <c:when test="${genre.active}">
                                            <a href="/admin/genres/${genre.id}/edit"
                                               class="icon-link icon-link--edit" title="Edit">
                                                <i class="fa-solid fa-pen"></i>
                                            </a>
                                            <button type="button" class="icon-link icon-link--danger" title="Delete"
                                                    data-genre-name="<c:out value='${genre.name}'/>"
                                                    onclick="openConfirmModal('/admin/genres/${genre.id}/delete','delete','Are you sure you want to delete this genre?')">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="icon-link icon-link--restore" title="Restore"
                                                    onclick="openConfirmModal('/admin/genres/${genre.id}/restore','restore','Are you sure you want to restore this genre?')">
                                                <i class="fa-solid fa-rotate-left"></i>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty genres}">
                            <tr>
                                <td colspan="7" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                    <i class="fa-solid fa-layer-group"
                                       style="font-size:2.2rem;display:block;margin-bottom:10px;opacity:.3;"></i>
                                    No genres found.
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
                                Showing <strong>${fromItem}</strong> to <strong>${toItem}</strong> of <strong>${totalItems}</strong> entr${totalItems == 1 ? 'y' : 'ies'}<c:if test="${not empty q or not empty status or not empty selectedCategoryId}"> (filtered)</c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <c:set var="pagBase" value="/admin/genres?q=${q}&status=${status}&categoryId=${selectedCategoryId}"/>
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
    <div id="genreModal" class="modal-overlay" style="display:none;" onclick="closeGenreModal()">
        <div class="modal-box" style="max-width:520px;" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3><i class="fa-solid fa-layer-group" style="color:#006B5E;"></i> Genre Details</h3>
                <button class="modal-close" onclick="closeGenreModal()">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body" id="genreModalBody">
                <div style="text-align:center;padding:24px;color:#9CA3AF;">
                    <i class="fa-solid fa-spinner fa-spin"></i> Loading…
                </div>
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

        // Genre details (including book count/list) are fetched on demand
        // instead of being embedded in the table, since most rows never get opened.
        document.addEventListener('click', function (e) {
            var btn = e.target.closest('.js-view-genre');
            if (!btn) return;
            var id = btn.dataset.id;
            document.getElementById('genreModal').style.display = 'flex';
            document.getElementById('genreModalBody').innerHTML =
                '<div style="text-align:center;padding:24px;color:#9CA3AF;"><i class="fa-solid fa-spinner fa-spin"></i> Loading…</div>';

            fetch('/admin/genres/' + id + '/detail')
                .then(function (r) { return r.json(); })
                .then(function (d) {
                    var statusHtml = d.active
                        ? '<span class="status-pill status-pill--on"><i class="fa-solid fa-circle-check" style="font-size:.6rem;"></i> Active</span>'
                        : '<span class="status-pill status-pill--off"><i class="fa-solid fa-circle-xmark" style="font-size:.6rem;"></i> Inactive</span>';

                    var booksHtml = '';
                    if (d.books && d.books.length > 0) {
                        booksHtml = '<ul style="margin:6px 0 0;padding-left:18px;font-size:.85rem;color:#374151;">';
                        d.books.forEach(function (b) { booksHtml += '<li>' + b + '</li>'; });
                        booksHtml += '</ul>';
                    } else {
                        booksHtml = '<span style="color:#9CA3AF;font-size:.85rem;">No books yet.</span>';
                    }

                    document.getElementById('genreModalBody').innerHTML =
                        '<div class="modal-row"><span class="modal-label">Name</span><span class="modal-value">' + d.name + '</span></div>' +
                        '<div class="modal-row"><span class="modal-label">Category</span><span class="modal-value">' + d.category + '</span></div>' +
                        '<div class="modal-row"><span class="modal-label">Description</span><span class="modal-value">' + (d.description || '—') + '</span></div>' +
                        '<div class="modal-row"><span class="modal-label">Status</span><span class="modal-value">' + statusHtml + '</span></div>' +
                        '<div class="modal-row"><span class="modal-label">Total Books</span><span class="modal-value">' + d.totalBooks + '</span></div>' +
                        '<div class="modal-row"><span class="modal-label">Created</span><span class="modal-value">' + d.createdAt + '</span></div>' +
                        '<div class="modal-row"><span class="modal-label">Updated</span><span class="modal-value">' + d.updatedAt + '</span></div>' +
                        '<div class="modal-row" style="align-items:flex-start;"><span class="modal-label">Books</span><span class="modal-value">' + booksHtml + '</span></div>';
                })
                .catch(function () {
                    document.getElementById('genreModalBody').innerHTML =
                                                '<div style="text-align:center;padding:24px;color:#EF4444;"><i class="fa-solid fa-circle-exclamation"></i> Failed to load details.</div>';
                });
        });
        function closeGenreModal() { document.getElementById('genreModal').style.display = 'none'; }
    </script>
</body>
</html>
