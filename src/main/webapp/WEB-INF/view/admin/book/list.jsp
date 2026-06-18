<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title>Book Management</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker">CRUD book</p>
                    <h2>Books</h2>
                </div>
                <a href="/admin/books/create" class="admin-button" style="cursor:pointer;">
                    <i class="fa-solid fa-plus"></i> New book
                </a>
            </div>

            <%-- Search bar --%>
            <div class="admin-panel" style="padding:16px 24px;">
                <form method="get" action="/admin/books" class="admin-search-form">
                    <input type="text" name="q" value="<c:out value='${q}'/>"
                           placeholder="Search by title, author, ISBN or category…"
                           class="admin-input" style="max-width:420px;" />
                    <button type="submit" class="admin-button" style="cursor:pointer;">
                        <i class="fa-solid fa-magnifying-glass"></i> Search
                    </button>
                    <c:if test="${not empty q}">
                        <a href="/admin/books" class="admin-button admin-button--ghost" style="cursor:pointer;">Clear</a>
                    </c:if>
                </form>
            </div>

            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Title</th>
                            <th>Author</th>
                            <th>ISBN</th>
                            <th>Category</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Status</th>
                            <th>Updated</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${books}" var="book" varStatus="vs">
                            <%-- FIX 1: store all data-* on the button; no raw EL inside JS strings --%>
                            <tr>
                                <td>${vs.index + 1}</td>
                                <%-- FIX 4: escape HTML to prevent XSS --%>
                                <td><c:out value="${book.title}"/></td>
                                <td><c:out value="${book.author}"/></td>
                                <td><c:out value="${book.isbn}" default="—"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty book.category}"><c:out value="${book.category.name}"/></c:when>
                                        <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${book.price}"/></td>
                                <td>${book.stockQuantity}</td>
                                <td>
                                    <span class="status-pill ${book.active ? 'status-pill--on' : 'status-pill--off'}">
                                        ${book.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <%-- FIX 3: format LocalDateTime as yyyy-MM-dd HH:mm --%>
                                <td>
                                    <c:out value="${book.updatedAtString}" default="—"/>
                                </td>
                                <td class="admin-table__actions">
                                    <%-- FIX 1: use data-* attributes instead of inline JS string args --%>
                                    <button type="button" class="icon-link js-view-book" style="cursor:pointer;"
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
                                    <a href="/admin/books/${book.id}/edit" class="icon-link" style="cursor:pointer;">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <%-- Delete --%>
                                    <button type="button" class="icon-link icon-link--danger" style="cursor:pointer;"
                                            onclick="openDeleteModal('/admin/books/${book.id}/delete', 'Are you sure you want to delete this book?')">
                                        <i class="fa-solid fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty books}">
                            <tr><td colspan="10" style="text-align:center;color:var(--admin-muted);padding:2rem;">
                                No books found.
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
    <div id="bookModal" class="modal-overlay" style="display:none;" onclick="closeModal('bookModal')">
        <div class="modal-box" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3><i class="fa-solid fa-book"></i> Book Details</h3>
                <button class="modal-close" onclick="closeModal('bookModal')" style="cursor:pointer;">
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
                <div class="modal-row"><span class="modal-label">Last updated</span><span id="mBUpdated" class="modal-value"></span></div>
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

        // FIX 2: trigger toast for flash messages from hidden DOM nodes
        var successToastEl = document.getElementById('toastSuccessMessage');
        if (successToastEl) {
            showToast(successToastEl.textContent.trim(), 'success');
        }
        var errorToastEl = document.getElementById('toastErrorMessage');
        if (errorToastEl) {
            showToast(errorToastEl.textContent.trim(), 'error');
        }

        // FIX 1: read data-* attributes — safe against any special chars
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
                document.getElementById('mBUpdated').textContent  = d.updated || '—';
                document.getElementById('bookModal').style.display = 'flex';
            });
        });

        // ── Delete modal ──
        function openDeleteModal(action, message) {
            document.getElementById('deleteModalMsg').textContent = message;
            document.getElementById('deleteForm').action = action;
            document.getElementById('deleteModal').style.display = 'flex';
        }
        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }

        // ── Modal close ──
        function closeModal(id) {
            document.getElementById(id).style.display = 'none';
        }
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                document.querySelectorAll('.modal-overlay').forEach(function(m) { m.style.display = 'none'; });
            }
        });
    </script>
</body>
</html>
