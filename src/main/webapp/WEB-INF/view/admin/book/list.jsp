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
        .isbn-tag { font-size:.72rem; color:#9CA3AF; margin-top:2px; }
        .icon-link--restore { color:#059669; }
        .icon-link--restore:hover { color:#047857; background:#D1FAE5; }

        /* View Book modal — 2-column layout */
        .book-modal-body {
            display: grid;
            grid-template-columns: 200px 1fr;
            gap: 24px;
            align-items: start;
        }
        .book-modal-img {
            width: 200px;
            min-height: 260px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
            overflow: hidden;
            background: #F9FAFB;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }
        .book-modal-img img {
            width: 100%;
            height: 260px;
            object-fit: cover;
            display: block;
        }
        .book-modal-img .no-img {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 8px;
            color: #9CA3AF;
            font-size: .8rem;
            text-align: center;
            padding: 20px;
            height: 260px;
            box-sizing: border-box;
        }
        .book-modal-img .no-img i { font-size: 2.5rem; opacity: .35; }
        .book-modal-info { min-width: 0; }
        .showing-info {
            font-size: .82rem;
            color: #6B7280;
            padding: 10px 4px 0;
        }
        @media (max-width: 600px) {
            .book-modal-body { grid-template-columns: 1fr; }
            .book-modal-img { width: 100%; }
        }
    </style>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />
    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />
        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-book"></i> Book Management</p>
                    <h2>Books</h2>
                </div>
                <a href="/admin/books/create" class="admin-button">
                    <i class="fa-solid fa-plus"></i> New Book
                </a>
            </div>
            <div class="admin-panel" style="padding:14px 22px;">
                <form method="get" action="/admin/books" class="admin-search-form">
                    <div style="position:relative;flex:1;max-width:380px;">
                        <i class="fa-solid fa-magnifying-glass" style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>
                        <input type="text" name="q" value="<c:out value='${q}'/>" placeholder="Search by title, author, ISBN or category..." class="admin-input" style="padding-left:38px;" />
                    </div>
                    <select name="status" class="admin-input" style="max-width:160px;">
                        <option value="" <c:if test="${empty status}">selected</c:if>>All Status</option>
                        <option value="active" <c:if test="${status == 'active'}">selected</c:if>>Active</option>
                        <option value="inactive" <c:if test="${status == 'inactive'}">selected</c:if>>Inactive</option>
                    </select>
                    <button type="submit" class="admin-button"><i class="fa-solid fa-filter"></i> Filter</button>
                    <a href="/admin/books" class="admin-button admin-button--ghost"><i class="fa-solid fa-rotate-right"></i> Reset</a>
                </form>
            </div>
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
                            <th style="width:120px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${books}" var="book" varStatus="vs">
                            <tr>
                                <td style="color:#9CA3AF;font-weight:600;">${vs.index + 1}</td>
                                <td>
                                    <div style="font-weight:600;color:#111827;"><c:out value="${book.title}"/></div>
                                    <c:if test="${not empty book.isbn}">
                                        <div class="isbn-tag">ISBN: <c:out value="${book.isbn}"/></div>
                                    </c:if>
                                </td>
                                <td style="color:#374151;"><c:out value="${book.author}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty book.category}">
                                            <span class="cat-badge"><i class="fa-solid fa-tag" style="font-size:.6rem;"></i> <c:out value="${book.category.name}"/></span>
                                        </c:when>
                                        <c:otherwise><span style="color:#9CA3AF;">&#8212;</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="font-weight:700;color:#111827;"><c:out value="${book.priceFormatted}"/> &#8363;</td>
                                <td style="color:#374151;">${book.stockQuantity}</td>
                                <td>
                                    <span class="status-pill ${book.active ? 'status-pill--on' : 'status-pill--off'}">
                                        <i class="fa-solid ${book.active ? 'fa-circle-check' : 'fa-circle-xmark'}" style="font-size:.6rem;"></i>
                                        ${book.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td style="font-size:.8rem;color:#6B7280;"><c:out value="${book.updatedAtString}" default="&#8212;"/></td>
                                <td class="admin-table__actions">
                                    <button type="button" class="icon-link js-view-book" title="View details"
                                            data-title="<c:out value='${book.title}'/>"
                                            data-author="<c:out value='${book.author}'/>"
                                            data-isbn="<c:out value='${book.isbn}'/>"
                                            data-category="<c:out value='${not empty book.category ? book.category.name : &quot;&quot;}'/>"
                                            data-genre="<c:out value='${not empty book.genre ? book.genre.name : &quot;&quot;}'/>"
                                            data-price="<c:out value='${book.priceFormatted}'/>"
                                            data-stock="${book.stockQuantity}"
                                            data-active="${book.active}"
                                            data-image="<c:out value='${book.imageUrl}'/>"
                                            data-desc="<c:out value='${book.description}'/>"
                                            data-created="<c:out value='${book.createdAtString}'/>"
                                            data-updated="<c:out value='${book.updatedAtString}'/>">
                                        <i class="fa-solid fa-eye"></i>
                                    </button>
                                    <a href="/admin/books/${book.id}/edit" class="icon-link icon-link--edit" title="Edit">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <c:choose>
                                        <c:when test="${book.active}">
                                            <button type="button" class="icon-link icon-link--danger" title="Delete"
                                                    onclick="openConfirmModal('/admin/books/${book.id}/delete','delete','Are you sure you want to delete this book?')">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="icon-link icon-link--restore" title="Restore"
                                                    onclick="openConfirmModal('/admin/books/${book.id}/restore','restore','Are you sure you want to restore this book?')">
                                                <i class="fa-solid fa-rotate-left"></i>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty books}">
                            <tr>
                                <td colspan="9" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                    <i class="fa-solid fa-book-open" style="font-size:2.2rem;display:block;margin-bottom:10px;opacity:.3;"></i>
                                    No books found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
                <%-- Pagination --%>
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
                        <div class="admin-pagination__nav">
                            <a class="pag-btn ${currentPage == 0 ? 'pag-btn--disabled' : ''}"
                               href="/admin/books?page=${currentPage - 1}&q=<c:out value='${q}'/>&status=<c:out value='${status}'/>">
                                <i class="fa-solid fa-chevron-left" style="font-size:.7rem;"></i>
                            </a>
                            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                <a class="pag-btn ${i == currentPage ? 'pag-btn--active' : ''}"
                                   href="/admin/books?page=${i}&q=<c:out value='${q}'/>&status=<c:out value='${status}'/>">
                                    ${i + 1}
                                </a>
                            </c:forEach>
                            <a class="pag-btn ${currentPage >= totalPages - 1 ? 'pag-btn--disabled' : ''}"
                               href="/admin/books?page=${currentPage + 1}&q=<c:out value='${q}'/>&status=<c:out value='${status}'/>">
                                <i class="fa-solid fa-chevron-right" style="font-size:.7rem;"></i>
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </section>
    </main>

    <%-- Confirm Modal --%>
    <div id="confirmModal" class="modal-overlay" style="display:none;" onclick="closeConfirmModal()">
        <div class="modal-box" style="max-width:420px;" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3 id="confirmModalTitle">
                    <i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i> Confirm Action
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

    <%-- View Book Modal --%>
    <div id="bookModal" class="modal-overlay" style="display:none;" onclick="closeModal('bookModal')">
        <div class="modal-box" style="max-width:680px;" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3><i class="fa-solid fa-book" style="color:#2563EB;"></i> Book Details</h3>
                <button class="modal-close" onclick="closeModal('bookModal')"><i class="fa-solid fa-xmark"></i></button>
            </div>
            <div class="modal-body">
                <div class="book-modal-body">
                    <%-- Image --%>
                    <div class="book-modal-img" id="mBImgWrap">
                        <div class="no-img" id="mBNoImg">
                            <i class="fa-regular fa-image"></i>
                            No image available
                        </div>
                        <img id="mBImg" src="" alt="Book cover" style="display:none;" />
                    </div>
                    <%-- Info --%>
                    <div class="book-modal-info">
                        <div class="modal-row"><span class="modal-label">Title</span><span id="mBTitle" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Author</span><span id="mBAuthor" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">ISBN</span><span id="mBIsbn" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Category</span><span id="mBCategory" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Genre</span><span id="mBGenre" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Price</span><span id="mBPrice" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Stock</span><span id="mBStock" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Status</span><span id="mBStatus" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Description</span><span id="mBDesc" class="modal-value" style="white-space:pre-line;"></span></div>
                        <div class="modal-row"><span class="modal-label">Created</span><span id="mBCreated" class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Updated</span><span id="mBUpdated" class="modal-value"></span></div>
                    </div>
                </div>
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
            t.innerHTML = '<i class="fa-solid ' + (type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation') + '"></i> ' + msg;
            tc.appendChild(t);
            setTimeout(function () { t.classList.add('toast--show'); }, 10);
            setTimeout(function () { t.classList.remove('toast--show'); setTimeout(function () { t.remove(); }, 320); }, 3500);
        }
        var s = document.getElementById('toastSuccessMessage');
        if (s) showToast(s.textContent.trim(), 'success');
        var e = document.getElementById('toastErrorMessage');
        if (e) showToast(e.textContent.trim(), 'error');

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

        /* D\u00f9ng event delegation: l\u1eafng nghe \u1edf document, d\u00f9ng closest()
           \u0111\u1ec3 b\u1eaft click d\u00f9 user click v\u00e0o <i> icon b\u00ean trong button */
        document.addEventListener('click', function (e) {
            var btn = e.target.closest('.js-view-book');
            if (!btn) return;

            var d = btn.dataset;
            document.getElementById('mBTitle').textContent    = d.title    || '\u2014';
            document.getElementById('mBAuthor').textContent   = d.author   || '\u2014';
            document.getElementById('mBIsbn').textContent     = d.isbn     || '\u2014';
            document.getElementById('mBCategory').textContent = d.category || '\u2014';
            document.getElementById('mBGenre').textContent    = d.genre    || '\u2014';
            document.getElementById('mBPrice').textContent    = (d.price   || '0') + ' \u20ab';
            document.getElementById('mBStock').textContent    = d.stock    || '0';
            document.getElementById('mBStatus').innerHTML     = d.active === 'true'
                ? '<span class="status-pill status-pill--on"><i class="fa-solid fa-circle-check" style="font-size:.6rem;"></i> Active</span>'
                : '<span class="status-pill status-pill--off"><i class="fa-solid fa-circle-xmark" style="font-size:.6rem;"></i> Inactive</span>';
            document.getElementById('mBDesc').textContent     = d.desc     || '\u2014';
            document.getElementById('mBCreated').textContent  = d.created  || '\u2014';
            document.getElementById('mBUpdated').textContent  = d.updated  || '\u2014';

            var img   = document.getElementById('mBImg');
            var noImg = document.getElementById('mBNoImg');
            if (d.image && d.image.trim()) {
                img.src             = d.image;
                img.style.display   = 'block';
                noImg.style.display = 'none';
            } else {
                img.style.display   = 'none';
                noImg.style.display = 'flex';
            }
            document.getElementById('bookModal').style.display = 'flex';
        });

        function closeModal(id) { document.getElementById(id).style.display = 'none'; }
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') document.querySelectorAll('.modal-overlay').forEach(function (m) { m.style.display = 'none'; });
        });
    </script>
</body>
</html>