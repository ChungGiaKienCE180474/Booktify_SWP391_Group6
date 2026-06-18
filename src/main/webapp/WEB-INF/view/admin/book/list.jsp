<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title>Books — Booktify Admin</title>
    <style>
        .cat-badge {
            display:inline-flex; align-items:center; gap:4px;
            background:#EFF6FF; border:1px solid #BFDBFE;
            color:#2563EB; padding:3px 10px; border-radius:999px;
            font-size:.72rem; font-weight:700;
        }
        .isbn-tag {
            font-size:.72rem; color:#9CA3AF; margin-top:2px;
        }
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
                    <p class="admin-kicker"><i class="fa-solid fa-book"></i> Book Management</p>
                    <h2>Books</h2>
                </div>
                <a href="/admin/books/create" class="admin-button">
                    <i class="fa-solid fa-plus"></i> New Book
                </a>
            </div>

            <%-- Search --%>
            <div class="admin-panel" style="padding:14px 22px;">
                <form method="get" action="/admin/books" class="admin-search-form">
                    <div style="position:relative;flex:1;max-width:440px;">
                        <i class="fa-solid fa-magnifying-glass"
                           style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>
                        <input type="text" name="q" value="<c:out value='${q}'/>"
                               placeholder="Search by title, author, ISBN or category…"
                               class="admin-input" style="padding-left:38px;" />
                    </div>
                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-magnifying-glass"></i> Search
                    </button>
                    <c:if test="${not empty q}">
                        <a href="/admin/books" class="admin-button admin-button--ghost">
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
                            <th>Title / ISBN</th>
                            <th>Author</th>
                            <th>Category</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Status</th>
                            <th>Updated</th>
                            <th style="width:116px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${books}" var="book" varStatus="vs">
                            <tr>
                                <td style="color:#9CA3AF;font-weight:600;">${vs.index + 1}</td>
                                <td>
                                    <div style="font-weight:600;color:#111827;">
                                        <c:out value="${book.title}"/>
                                    </div>
                                    <c:if test="${not empty book.isbn}">
                                        <div class="isbn-tag">ISBN: <c:out value="${book.isbn}"/></div>
                                    </c:if>
                                </td>
                                <td style="color:#374151;"><c:out value="${book.author}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty book.category}">
                                            <span class="cat-badge">
                                                <i class="fa-solid fa-tag" style="font-size:.6rem;"></i>
                                                <c:out value="${book.category.name}"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise><span style="color:#9CA3AF;">—</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="font-weight:700;color:#111827;">
                                    <c:out value="${book.price}"/>
                                </td>
                                <td style="color:#374151;">${book.stockQuantity}</td>
                                <td>
                                    <span class="status-pill ${book.active ? 'status-pill--on' : 'status-pill--off'}">
                                        <i class="fa-solid ${book.active ? 'fa-circle-check' : 'fa-circle-xmark'}"
                                           style="font-size:.6rem;"></i>
                                        ${book.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td style="font-size:.8rem;color:#6B7280;">
                                    <c:out value="${book.updatedAtString}" default="—"/>
                                </td>
                                <td class="admin-table__actions">
                                    <%-- View --%>
                                    <button type="button" class="icon-link js-view-book" title="View details"
                                            data-title="<c:out value='${book.title}'/>"
                                            data-author="<c:out value='${book.author}'/>"
                                            data-isbn="<c:out value='${book.isbn}'/>"
                                            data-category="<c:out value='${not empty book.category ? book.category.name : &quot;&quot;}'/>"
                                            data-price="<c:out value='${book.price}'/>"
                                            data-stock="${book.stockQuantity}"
                                            data-active="${book.active}"
                                            data-desc="<c:out value='${book.description}'/>"
                                            data-updated="<c:out value='${book.updatedAtString}'/>">
                                        <i class="fa-solid fa-eye"></i>
                                    </button>
                                    <%-- Edit --%>
                                    <a href="/admin/books/${book.id}/edit" class="icon-link icon-link--edit" title="Edit">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <%-- Delete --%>
                                    <button type="button" class="icon-link icon-link--danger" title="Delete"
                                            onclick="openDeleteModal('/admin/books/${book.id}/delete','Are you sure you want to delete this book?')">
                                        <i class="fa-solid fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty books}">
                            <tr>
                                <td colspan="9" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                    <i class="fa-solid fa-book-open"
                                       style="font-size:2.2rem;display:block;margin-bottom:10px;opacity:.3;"></i>
                                    No books found.
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
    <div id="bookModal" class="modal-overlay" style="display:none;" onclick="closeModal('bookModal')">
        <div class="modal-box" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3><i class="fa-solid fa-book" style="color:#2563EB;"></i> Book Details</h3>
                <button class="modal-close" onclick="closeModal('bookModal')">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>
            <div class="modal-body">
                <div class="modal-row"><span class="modal-label">Title</span><span id="mBTitle" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Author</span><span id="mBAuthor" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">ISBN</span><span id="mBIsbn" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Category</span><span id="mBCategory" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Price</span><span id="mBPrice" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Stock</span><span id="mBStock" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Status</span><span id="mBStatus" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Description</span><span id="mBDesc" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Updated</span><span id="mBUpdated" class="modal-value"></span></div>
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

        document.querySelectorAll('.js-view-book').forEach(function(btn) {
            btn.addEventListener('click', function() {
                var d = this.dataset;
                document.getElementById('mBTitle').textContent    = d.title    || '—';
                document.getElementById('mBAuthor').textContent   = d.author   || '—';
                document.getElementById('mBIsbn').textContent     = d.isbn     || '—';
                document.getElementById('mBCategory').textContent = d.category || '—';
                document.getElementById('mBPrice').textContent    = d.price    || '—';
                document.getElementById('mBStock').textContent    = d.stock    || '0';
                document.getElementById('mBStatus').innerHTML     = d.active === 'true'
                    ? '<span class="status-pill status-pill--on">Active</span>'
                    : '<span class="status-pill status-pill--off">Inactive</span>';
                document.getElementById('mBDesc').textContent     = d.desc     || '—';
                document.getElementById('mBUpdated').textContent  = d.updated  || '—';
                document.getElementById('bookModal').style.display = 'flex';
            });
        });

        function closeModal(id) { document.getElementById(id).style.display = 'none'; }
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') document.querySelectorAll('.modal-overlay').forEach(function(m){ m.style.display='none'; });
        });
    </script>
</body>
</html>
