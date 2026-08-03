<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <title>
        <c:choose>
            <c:when test="${formMode=='edit'}">Edit book set</c:when>
            <c:otherwise>Create book set</c:otherwise>
        </c:choose>
        — Booktify Admin
    </title>
    <style>
        .set-hint { font-size:.82rem; color:#6B7280; margin:.25rem 0 0; }
        .set-items-toolbar {
            display:flex; gap:.75rem; align-items:center; flex-wrap:wrap;
            margin-bottom:.75rem;
        }
        .set-items-toolbar input[type="search"] {
            flex:1; min-width:180px; max-width:320px;
            padding:.55rem .75rem; border:1px solid #D1D5DB; border-radius:8px;
        }
        .set-item-row {
            display:grid;
            grid-template-columns: 1fr 100px 40px;
            gap:.65rem;
            align-items:center;
            margin-bottom:.65rem;
        }
        .set-item-row select,
        .set-item-row input[type="number"] {
            width:100%;
            padding:.55rem .7rem;
            border:1px solid #D1D5DB;
            border-radius:8px;
            background:#fff;
        }
        .set-item-remove {
            border:none; background:#FEE2E2; color:#B91C1C;
            width:36px; height:36px; border-radius:8px; cursor:pointer;
        }
        .set-item-remove:hover { background:#FECACA; }
        #bookOptionsTemplate { display:none; }
        .admin-input.is-invalid,
        .book-select.is-invalid,
        input.is-invalid {
            border-color: #DC2626 !important;
            box-shadow: 0 0 0 3px rgba(220, 38, 38, .12);
        }
        .set-items.has-error {
            padding: .75rem;
            border: 1px solid #FECACA;
            border-radius: 10px;
            background: #FEF2F2;
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
                    <p class="admin-kicker"><i class="fa-solid fa-layer-group"></i> Catalog</p>
                    <h2>
                        <c:choose>
                            <c:when test="${formMode=='edit'}">Edit book set</c:when>
                            <c:otherwise>Create book set</c:otherwise>
                        </c:choose>
                    </h2>
                    <p class="set-hint">
                        Create any bundle: textbooks, manga/comics series, novel box sets, custom packs…
                    </p>
                </div>
                <a href="/admin/book-sets" class="admin-button admin-button--ghost">Back</a>
            </div>

            <div class="admin-panel" style="padding:22px;">
                <form:form modelAttribute="bookSetForm" method="post" action="/admin/book-sets/save" id="bookSetForm">
                    <form:hidden path="id" />
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <div style="display:grid;gap:1rem;max-width:820px;">
                        <div>
                            <label style="display:block;margin-bottom:.35rem;font-weight:600;">Name *</label>
                            <form:input path="name" cssClass="admin-input" cssErrorClass="admin-input is-invalid"
                                        maxlength="200"
                                        placeholder="e.g. One Piece Vol.1–5, Grade 1 textbook set" />
                            <form:errors path="name" cssClass="admin-error" />
                        </div>

                        <div>
                            <label style="display:block;margin-bottom:.35rem;font-weight:600;">
                                Tag / Series
                            </label>
                            <form:input path="gradeLevel" cssClass="admin-input" cssErrorClass="admin-input is-invalid"
                                        maxlength="100"
                                        placeholder="Type any name you want" />
                            <form:errors path="gradeLevel" cssClass="admin-error" />
                            <p class="set-hint">Nhập tự do theo ý bạn (vd. One Piece, Truyện tranh, Bộ lớp 1…). Để trống = General.</p>
                        </div>

                        <div>
                            <label style="display:block;margin-bottom:.35rem;font-weight:600;">Set price (VND) *</label>
                            <form:input path="setPrice" cssClass="admin-input" cssErrorClass="admin-input is-invalid"
                                        type="number" min="0" step="1000" />
                            <form:errors path="setPrice" cssClass="admin-error" />
                        </div>

                        <div>
                            <label style="display:block;margin-bottom:.35rem;font-weight:600;">Image URL</label>
                            <form:input path="imageUrl" cssClass="admin-input" cssErrorClass="admin-input is-invalid"
                                        maxlength="500" />
                            <form:errors path="imageUrl" cssClass="admin-error" />
                        </div>

                        <div>
                            <label style="display:block;margin-bottom:.35rem;font-weight:600;">Description</label>
                            <form:textarea path="description" cssClass="admin-input"
                                           cssErrorClass="admin-input is-invalid" rows="4"
                                           placeholder="What is included and who it is for" />
                            <form:errors path="description" cssClass="admin-error" />
                        </div>

                        <div>
                            <label style="display:inline-flex;align-items:center;gap:.5rem;font-weight:600;">
                                <form:checkbox path="active" /> Active
                            </label>
                        </div>

                        <div>
                            <label style="display:block;margin-bottom:.35rem;font-weight:600;">
                                Books in set * (min 2 different titles)
                            </label>
                            <p class="set-hint" style="margin-bottom:.75rem;">
                                Pick any active books (comics, textbooks, novels…) and set quantity per title.
                            </p>
                            <form:errors path="items" cssClass="admin-error" />

                            <div class="set-items-toolbar">
                                <input type="search" id="bookFilter" placeholder="Filter books in dropdowns…" />
                                <button type="button" class="admin-button" id="addItemBtn">
                                    <i class="fa-solid fa-plus"></i> Add book
                                </button>
                            </div>

                            <spring:bind path="items">
                                <div id="setItems" class="set-items ${status.error ? 'has-error' : ''}">
                                    <c:forEach items="${bookSetForm.items}" var="item" varStatus="st">
                                        <div class="set-item-row" data-row>
                                            <select name="items[${st.index}].bookId"
                                                    class="book-select ${status.error ? 'is-invalid' : ''}" required>
                                                <option value="">— Select a book —</option>
                                                <c:forEach items="${activeBooks}" var="book">
                                                    <option value="${book.id}"
                                                        data-title="${book.title}"
                                                        ${item.bookId == book.id ? 'selected' : ''}>
                                                        <c:out value="${book.title}" />
                                                        <c:if test="${not empty book.category}"> — ${book.category.name}</c:if>
                                                        — ${book.priceFormatted} &#8363;
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <input type="number" name="items[${st.index}].quantity"
                                                   min="1" step="1" value="${empty item.quantity ? 1 : item.quantity}"
                                                   title="Quantity in set" required />
                                            <button type="button" class="set-item-remove" title="Remove"
                                                    onclick="removeItemRow(this)">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </div>
                                    </c:forEach>
                                </div>
                            </spring:bind>
                        </div>

                        <div>
                            <button type="submit" class="admin-button">
                                <i class="fa-solid fa-save"></i> Save book set
                            </button>
                        </div>
                    </div>
                </form:form>
            </div>
        </section>
    </main>

    <select id="bookOptionsTemplate">
        <option value="">— Select a book —</option>
        <c:forEach items="${activeBooks}" var="book">
            <option value="${book.id}" data-title="${book.title}">
                <c:out value="${book.title}" />
                <c:if test="${not empty book.category}"> — ${book.category.name}</c:if>
                — ${book.priceFormatted} &#8363;
            </option>
        </c:forEach>
    </select>

    <script>
        (function () {
            const container = document.getElementById('setItems');
            const template = document.getElementById('bookOptionsTemplate');
            const filterInput = document.getElementById('bookFilter');

            function reindexRows() {
                container.querySelectorAll('[data-row]').forEach(function (row, index) {
                    const select = row.querySelector('select');
                    const qty = row.querySelector('input[type="number"]');
                    select.name = 'items[' + index + '].bookId';
                    qty.name = 'items[' + index + '].quantity';
                });
            }

            window.removeItemRow = function (btn) {
                const rows = container.querySelectorAll('[data-row]');
                if (rows.length <= 2) {
                    alert('A set needs at least 2 book rows.');
                    return;
                }
                btn.closest('[data-row]').remove();
                reindexRows();
            };

            document.getElementById('addItemBtn').addEventListener('click', function () {
                const row = document.createElement('div');
                row.className = 'set-item-row';
                row.setAttribute('data-row', '');

                const select = document.createElement('select');
                select.className = 'book-select';
                select.required = true;
                select.innerHTML = template.innerHTML;

                const qty = document.createElement('input');
                qty.type = 'number';
                qty.min = '1';
                qty.step = '1';
                qty.value = '1';
                qty.required = true;
                qty.title = 'Quantity in set';

                const removeBtn = document.createElement('button');
                removeBtn.type = 'button';
                removeBtn.className = 'set-item-remove';
                removeBtn.title = 'Remove';
                removeBtn.innerHTML = '<i class="fa-solid fa-trash"></i>';
                removeBtn.onclick = function () { removeItemRow(removeBtn); };

                row.appendChild(select);
                row.appendChild(qty);
                row.appendChild(removeBtn);
                container.appendChild(row);
                reindexRows();
                applyFilter();
            });

            function applyFilter() {
                const q = (filterInput.value || '').trim().toLowerCase();
                container.querySelectorAll('.book-select').forEach(function (select) {
                    Array.from(select.options).forEach(function (opt, i) {
                        if (i === 0) {
                            opt.hidden = false;
                            return;
                        }
                        const title = (opt.getAttribute('data-title') || opt.textContent || '').toLowerCase();
                        opt.hidden = q !== '' && title.indexOf(q) === -1;
                    });
                });
            }

            filterInput.addEventListener('input', applyFilter);
        })();
    </script>
</body>
</html>
