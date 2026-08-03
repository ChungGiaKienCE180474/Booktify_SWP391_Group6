<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <title>Book sets — Booktify Admin</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />
    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />
        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-layer-group"></i> Catalog</p>
                    <h2>Book sets</h2>
                </div>
                <a href="/admin/book-sets/create" class="admin-button">
                    <i class="fa-solid fa-plus"></i> New book set
                </a>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="admin-alert admin-alert--success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert--error">${errorMessage}</div>
            </c:if>

            <%-- Filter Bar: plain GET form, each field becomes a query param the controller re-reads. --%>
            <div class="admin-panel" style="padding:14px 22px;">
                <form method="get" action="/admin/book-sets" class="admin-search-form" style="flex-wrap:wrap;">
                    <div style="position:relative;flex:1;max-width:380px;">
                        <i class="fa-solid fa-magnifying-glass"
                           style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>
                        <input type="text" name="q" value="<c:out value='${q}'/>"
                               placeholder="Search by name, tag/series or description..."
                               class="admin-input" style="padding-left:38px;" />
                    </div>
                    <select name="status" class="admin-input" style="max-width:160px;">
                        <option value="" <c:if test="${empty status}">selected</c:if>>All Status</option>
                        <option value="active" <c:if test="${status == 'active'}">selected</c:if>>Active</option>
                        <option value="inactive" <c:if test="${status == 'inactive'}">selected</c:if>>Inactive</option>
                    </select>
                    <button type="submit" class="admin-button"><i class="fa-solid fa-filter"></i> Filter</button>
                    <a href="/admin/book-sets" class="admin-button admin-button--ghost">
                        <i class="fa-solid fa-rotate-right"></i> Reset
                    </a>
                </form>
            </div>

            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Tag / Series</th>
                        <th>Books</th>
                        <th>Set price</th>
                        <th>Retail total</th>
                        <th>Available</th>
                        <th>Status</th>
                        <th style="width:120px;">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${bookSets}" var="set" varStatus="st">
                        <%-- Build a readable component-book list for the view modal --%>
                        <c:set var="bookList" value="" />
                        <c:forEach items="${set.items}" var="it" varStatus="is">
                            <c:set var="bookList"
                                   value="${bookList}${is.first ? '' : ', '}${it.book.title} x${it.quantity}" />
                        </c:forEach>
                        <tr>
                            <td>${st.index + 1}</td>
                            <td><strong><c:out value="${set.name}"/></strong></td>
                            <td><c:out value="${tagLabelMap[set.id]}" default="General"/></td>
                            <td>${set.items.size()}</td>
                            <td>${set.setPrice} &#8363;</td>
                            <td>${retailTotalMap[set.id]} &#8363;</td>
                            <td>${availableQtyMap[set.id]}</td>
                            <td>
                                <span class="status-pill ${set.active ? 'status-pill--on' : 'status-pill--off'}">
                                    ${set.active ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td class="admin-table__actions">
                                <button type="button" class="icon-link js-view-set" title="View details"
                                        data-name="<c:out value='${set.name}'/>"
                                        data-tag="<c:out value='${tagLabelMap[set.id]}'/>"
                                        data-setprice="<c:out value='${set.setPrice}'/>"
                                        data-retail="<c:out value='${retailTotalMap[set.id]}'/>"
                                        data-available="<c:out value='${availableQtyMap[set.id]}'/>"
                                        data-active="${set.active}"
                                        data-count="${set.items.size()}"
                                        data-books="<c:out value='${bookList}'/>"
                                        data-desc="<c:out value='${set.description}'/>">
                                    <i class="fa-solid fa-eye"></i>
                                </button>
                                <a href="/admin/book-sets/${set.id}/edit" class="icon-link icon-link--edit" title="Edit">
                                    <i class="fa-solid fa-pen"></i>
                                </a>
                                <c:choose>
                                    <c:when test="${set.active}">
                                        <button type="button" class="icon-link icon-link--danger" title="Delete"
                                                onclick="openConfirmModal('/admin/book-sets/${set.id}/delete','delete','Are you sure you want to delete this book set?')">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="icon-link icon-link--restore" title="Restore"
                                                onclick="openConfirmModal('/admin/book-sets/${set.id}/restore','restore','Are you sure you want to restore this book set?')">
                                            <i class="fa-solid fa-rotate-left"></i>
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty bookSets}">
                        <tr>
                            <td colspan="9" style="text-align:center;padding:2rem;color:#9CA3AF;">
                                No book sets found.
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </section>
    </main>

    <%-- View Book Set Modal --%>
    <div id="bookSetModal" class="modal-overlay" style="display:none;" onclick="closeModal('bookSetModal')">
        <div class="modal-box" style="max-width:620px;" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3><i class="fa-solid fa-layer-group" style="color:#006B5E;"></i> Book Set Details</h3>
                <button class="modal-close" onclick="closeModal('bookSetModal')"><i class="fa-solid fa-xmark"></i></button>
            </div>
            <div class="modal-body">
                <div class="modal-row"><span class="modal-label">Name</span><span id="mSName" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Tag / Series</span><span id="mSTag" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Set price</span><span id="mSPrice" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Retail total</span><span id="mSRetail" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Available sets</span><span id="mSAvail" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Status</span><span id="mSStatus" class="modal-value"></span></div>
                <div class="modal-row"><span class="modal-label">Books (<span id="mSCount"></span>)</span><span id="mSBooks" class="modal-value" style="white-space:pre-line;"></span></div>
                <div class="modal-row"><span class="modal-label">Description</span><span id="mSDesc" class="modal-value" style="white-space:pre-line;"></span></div>
            </div>
        </div>
    </div>

    <%-- Confirm Delete/Restore modal (same pattern as Book list) --%>
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

    <script>
        function closeModal(id) {
            document.getElementById(id).style.display = 'none';
        }

        // View details modal — delegated so it also catches clicks on the icon.
        document.addEventListener('click', function (e) {
            var btn = e.target.closest('.js-view-set');
            if (!btn) return;
            var d = btn.dataset;
            document.getElementById('mSName').textContent = d.name || '—';
            document.getElementById('mSTag').textContent = d.tag || 'General';
            document.getElementById('mSPrice').textContent = (d.setprice || '0') + ' ₫';
            document.getElementById('mSRetail').textContent = (d.retail || '0') + ' ₫';
            document.getElementById('mSAvail').textContent = d.available || '0';
            document.getElementById('mSCount').textContent = d.count || '0';
            document.getElementById('mSBooks').textContent = d.books || '—';
            document.getElementById('mSDesc').textContent = d.desc || '—';
            document.getElementById('mSStatus').innerHTML = d.active === 'true'
                ? '<span class="status-pill status-pill--on">Active</span>'
                : '<span class="status-pill status-pill--off">Inactive</span>';
            document.getElementById('bookSetModal').style.display = 'flex';
        });

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
