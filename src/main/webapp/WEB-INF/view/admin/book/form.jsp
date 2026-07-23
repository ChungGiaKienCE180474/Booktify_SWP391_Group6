<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
            <c:choose>
                <c:when test="${formMode == 'edit'}">
                    <c:url var="formAction" value="/admin/books/${book.id}" />
                </c:when>
                <c:otherwise>
                    <c:url var="formAction" value="/admin/books" />
                </c:otherwise>
            </c:choose>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
                <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
                <link rel="stylesheet" href="/css/book.css" />

                <title>
                    <c:choose>
                        <c:when test="${formMode=='edit'}">Edit Book</c:when>
                        <c:otherwise>Create Book</c:otherwise>
                    </c:choose> — Booktify Admin
                </title>
               
            </head>

            <body class="admin-shell">
                <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

                <main class="admin-main">
                    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

                    <section class="admin-content">

                        <%-- Page Header --%>
                            <div class="admin-toolbar">
                                <div>
                                    <p class="admin-kicker">
                                        <i class="fa-solid fa-book"></i>
                                        <c:choose>
                                            <c:when test="${formMode=='edit'}">Edit Book</c:when>
                                            <c:otherwise>Create Book</c:otherwise>
                                        </c:choose>
                                    </p>
                                    <h2>
                                        <c:choose>
                                            <c:when test="${formMode=='edit'}">Edit Book</c:when>
                                            <c:otherwise>New Book</c:otherwise>
                                        </c:choose>
                                    </h2>
                                </div>
                                <a href="/admin/books" class="admin-button admin-button--ghost">
                                    <i class="fa-solid fa-arrow-left"></i> Back to Books
                                </a>
                            </div>

                            <%-- Form Card --%>
                                <div class="admin-form-card">
                                    <div class="admin-form-card__header">
                                        <i class="fa-solid fa-file-pen"></i>
                                        <h3>Book Information</h3>
                                    </div>
                                    <div class="admin-form-card__body">
                                        <form:form modelAttribute="book" action="${formAction}" method="post"
                                            enctype="multipart/form-data" class="admin-form" id="bookForm">

                                            <%-- Title & Author --%>
                                                <div class="admin-form-grid">
                                                    <div class="admin-field">
                                                        <label>Title <span style="color:#EF4444;">*</span></label>
                                                        <form:input path="title" cssClass="admin-input"
                                                            placeholder="Enter book title…" />
                                                        <form:errors path="title" cssClass="admin-error" />
                                                    </div>
                                                    <div class="admin-field">
                                                        <label>AUTHOR
                                                            <span
                                                                style="color:#9CA3AF;font-size:.78rem;font-weight:500;">—
                                                                Optional</span>
                                                        </label>

                                                        <%-- Actual submitted value; Book.author is plain text, not a
                                                            foreign key --%>
                                                            <input type="hidden" name="author" id="authorHidden"
                                                                value="<c:out value='${book.author}'/>" />

                                                            <c:choose>
                                                                <c:when test="${empty authors}">
                                                                    <div class="admin-input"
                                                                        style="display:flex;align-items:center;color:#9CA3AF;cursor:not-allowed;height:44px;">
                                                                        <i class="fa-solid fa-user-slash"
                                                                            style="margin-right:8px;font-size:.8rem;"></i>
                                                                        No authors available
                                                                    </div>
                                                                    <span
                                                                        style="font-size:.75rem;color:#9CA3AF;margin-top:4px;display:flex;align-items:center;gap:4px;">
                                                                        <i class="fa-solid fa-circle-info"></i>
                                                                        No authors yet — you can still save this book,
                                                                        or
                                                                        <a href="/admin/authors/create" target="_blank"
                                                                            style="color:#006B5E;text-decoration:underline;">add
                                                                            an author</a> first.
                                                                    </span>
                                                                </c:when>

                                                                <c:otherwise>
                                                                    <div class="ac-wrap" id="authorCombobox">
                                                                        <div class="ac-control admin-input"
                                                                            id="authorControl">
                                                                            <input type="text" class="ac-input"
                                                                                id="authorInput"
                                                                                placeholder="Search or select an author..."
                                                                                autocomplete="off"
                                                                                value="<c:out value='${book.author}'/>" />
                                                                            <span class="ac-chevron">
                                                                                <i class="fa-solid fa-chevron-down"></i>
                                                                            </span>
                                                                        </div>
                                                                        <ul class="ac-dropdown" id="authorDropdown"
                                                                            role="listbox">
                                                                            <c:forEach items="${authors}" var="au">
                                                                                <li class="ac-option${au.authorName == book.author ? ' is-selected' : ''}"
                                                                                    data-value="<c:out value='${au.authorName}'/>"
                                                                                    data-nat="<c:out value='${au.nationality}'/>"
                                                                                    role="option"><span
                                                                                        class="ac-opt-name">
                                                                                        <c:out
                                                                                            value="${au.authorName}" />
                                                                                    </span>
                                                                                    <c:if
                                                                                        test="${not empty au.nationality}">
                                                                                        <span class="ac-opt-nat">
                                                                                            <c:out
                                                                                                value="${au.nationality}" />
                                                                                        </span>
                                                                                    </c:if>
                                                                                </li>
                                                                            </c:forEach>
                                                                            <li class="ac-empty" id="authorNoMatch"
                                                                                style="display:none;">No
                                                                                authors match your
                                                                                search</li>
                                                                        </ul>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>

                                                            <form:errors path="author" cssClass="admin-error" />
                                                    </div>
                                                </div>

                                                <%-- ISBN & Category --%>
                                                    <div class="admin-form-grid">
                                                        <div class="admin-field">
                                                            <label>ISBN</label>
                                                            <form:input path="isbn" cssClass="admin-input"
                                                                placeholder="Optional — e.g. 978-3-16-148410-0" />
                                                            <form:errors path="isbn" cssClass="admin-error" />
                                                        </div>
                                                        <div class="admin-field">
                                                            <label>Category <span
                                                                    style="color:#EF4444;">*</span></label>
                                                            <select name="categoryId" id="categoryId"
                                                                class="admin-input" required>
                                                                <option value="">— Select a category —</option>
                                                                <c:forEach items="${categories}" var="cat">
                                                                    <option value="${cat.id}" <c:if
                                                                        test="${not empty book.category and book.category.id == cat.id}">
                                                                        selected</c:if>>
                                                                        <c:out value="${cat.name}" />
                                                                    </option>
                                                                </c:forEach>
                                                            </select>
                                                            <c:if test="${not empty categoryError}">
                                                                <span class="admin-error" id="categoryErrorMsg">
                                                                    <i class="fa-solid fa-circle-exclamation"></i>
                                                                    <c:out value="${categoryError}" />
                                                                </span>
                                                            </c:if>
                                                            <c:if test="${empty categoryError}">
                                                                <span class="admin-error" id="categoryErrorMsg"
                                                                    style="display:none;">
                                                                    <i class="fa-solid fa-circle-exclamation"></i>
                                                                    Please select a category before saving the book.
                                                                </span>
                                                            </c:if>
                                                        </div>
                                                    </div>

                                                    <%-- Supplier --%>
                                                        <div class="admin-form-grid">
                                                            <div class="admin-field">
                                                                <label>SUPPLIER
                                                                    <span
                                                                        style="color:#9CA3AF;font-size:.78rem;font-weight:500;">—
                                                                        Optional</span>
                                                                </label>
                                                                <c:choose>
                                                                    <c:when test="${empty suppliers}">
                                                                        <div class="admin-input"
                                                                            style="display:flex;align-items:center;color:#9CA3AF;cursor:not-allowed;height:44px;">
                                                                            <i class="fa-solid fa-truck"
                                                                                style="margin-right:8px;font-size:.8rem;"></i>
                                                                            No suppliers available
                                                                        </div>
                                                                        <span
                                                                            style="font-size:.75rem;color:#9CA3AF;margin-top:4px;display:flex;align-items:center;gap:4px;">
                                                                            <i class="fa-solid fa-circle-info"></i>
                                                                            No suppliers yet — you can still save this
                                                                            book, or
                                                                            <a href="/admin/suppliers/create"
                                                                                target="_blank"
                                                                                style="color:#006B5E;text-decoration:underline;">add
                                                                                a supplier</a> first.
                                                                        </span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <select name="supplierId" id="supplierId"
                                                                            class="admin-input">
                                                                            <option value="">— No supplier —</option>
                                                                            <c:forEach items="${suppliers}"
                                                                                var="sup">
                                                                                <option value="${sup.id}" <c:if
                                                                                    test="${not empty book.supplier and book.supplier.id == sup.id}">
                                                                                    selected</c:if>>
                                                                                    <c:out
                                                                                        value="${sup.supplierName}" />
                                                                                    <c:if test="${not sup.active}">
                                                                                        (Hidden)</c:if>
                                                                                </option>
                                                                            </c:forEach>
                                                                        </select>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>

                                                    <%-- Pricing & Stock --%>
                                                        <div class="admin-form-grid">
                                                            <div class="admin-field">
                                                                <label>Price <span
                                                                        style="color:#EF4444;">*</span></label>
                                                                <div style="position:relative;">
                                                                    <form:input path="price" cssClass="admin-input"
                                                                        type="text" id="priceDisplay" placeholder="0"
                                                                        maxlength="16" style="padding-right:36px;" />
                                                                    <span
                                                                        style="position:absolute;right:12px;top:50%;transform:translateY(-50%);color:#6B7280;font-size:.85rem;pointer-events:none;">₫</span>
                                                                </div>
                                                                <form:errors path="price" cssClass="admin-error" />
                                                            </div>
                                                            <div class="admin-field">
                                                                <label>Stock Quantity <span
                                                                        style="color:#EF4444;">*</span></label>
                                                                <form:input path="stockQuantity" cssClass="admin-input"
                                                                    type="number" min="0" step="1" placeholder="0"
                                                                    disabled="${formMode == 'edit'}" />
                                                                <c:if test="${formMode == 'edit'}">
                                                                    <span style="color:#9CA3AF;font-size:.78rem;">
                                                                        Stock is updated automatically from orders and
                                                                        can't be edited here.
                                                                    </span>
                                                                </c:if>
                                                                <form:errors path="stockQuantity"
                                                                    cssClass="admin-error" />
                                                            </div>
                                                        </div>

                                                        <%-- Genre Selection --%>
                                                            <div class="admin-field">
                                                                <label>Genres <span style="color:#EF4444;">*</span>
                                                                    <span
                                                                        style="color:#9CA3AF;font-size:.78rem;font-weight:500;">—
                                                                        Cần chọn ít nhất 1</span>
                                                                </label>
                                                                <c:choose>
                                                                    <c:when test="${empty genres}">
                                                                        <div class="admin-input"
                                                                            style="display:flex;align-items:center;color:#9CA3AF;cursor:not-allowed;height:44px;">
                                                                            <i class="fa-solid fa-triangle-exclamation"
                                                                                style="margin-right:8px;font-size:.8rem;"></i>
                                                                            No active genres available
                                                                        </div>
                                                                        <span
                                                                            style="font-size:.75rem;color:#F59E0B;margin-top:4px;display:flex;align-items:center;gap:4px;">
                                                                            <a href="/admin/genres/create"
                                                                                target="_blank"
                                                                                style="color:#F59E0B;text-decoration:underline;">Add
                                                                                a genre</a> first, then come back.
                                                                        </span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <div id="genreCheckList" class="admin-input"
                                                                            style="height:auto;min-height:44px;max-height:220px;overflow-y:auto;display:flex;flex-wrap:wrap;gap:6px 16px;align-items:flex-start;padding:10px 12px;">
                                                                            <c:forEach items="${genres}" var="g">
                                                                                <label
                                                                                    style="display:flex;align-items:center;gap:6px;font-size:.85rem;font-weight:500;color:#374151;cursor:pointer;white-space:nowrap;">
                                                                                    <input type="checkbox"
                                                                                        name="genreIds" value="${g.id}"
                                                                                        class="genre-checkbox" <c:if
                                                                                        test="${selectedGenreIds.contains(g.id)}">checked
                                                                                    </c:if> />
                                                                                    <c:out value="${g.name}" />
                                                                                </label>
                                                                            </c:forEach>
                                                                        </div>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <c:if test="${not empty genreError}">
                                                                    <span class="admin-error" id="genreErrorMsg">
                                                                        <i class="fa-solid fa-circle-exclamation"></i>
                                                                        <c:out value="${genreError}" />
                                                                    </span>
                                                                </c:if>
                                                                <c:if test="${empty genreError}">
                                                                    <span class="admin-error" id="genreErrorMsg"
                                                                        style="display:none;">
                                                                        <i class="fa-solid fa-circle-exclamation"></i>
                                                                        Please select at least 1 genre.
                                                                    </span>
                                                                </c:if>
                                                            </div>

                                                            <%-- Image Upload --%>
                                                                <div class="admin-field">
                                                                    <label>Book Cover Image</label>
                                                                    <c:if
                                                                        test="${formMode == 'edit' and not empty book.imageUrl}">
                                                                        <div style="margin-bottom:10px;">
                                                                            <img src="<c:out value='${book.imageUrl}'/>"
                                                                                alt="Current cover" id="imgPreview"
                                                                                style="height:120px;width:90px;object-fit:cover;border-radius:6px;border:1px solid #E5E7EB;display:block;" />
                                                                            <span
                                                                                style="font-size:.75rem;color:#6B7280;margin-top:4px;display:block;">Ảnh
                                                                                hiện tại — upload file mới để thay
                                                                                thế</span>
                                                                        </div>
                                                                    </c:if>
                                                                    <c:if
                                                                        test="${not (formMode == 'edit' and not empty book.imageUrl)}">
                                                                        <div style="margin-bottom:10px;">
                                                                            <img id="imgPreview"
                                                                                style="height:120px;width:90px;object-fit:cover;border-radius:6px;border:1px solid #E5E7EB;display:none;" />
                                                                        </div>
                                                                    </c:if>
                                                                    <input type="file" name="imageFile" id="imageFile"
                                                                        accept="image/jpeg,image/png,image/webp,image/gif"
                                                                        class="admin-input"
                                                                        style="padding:6px 10px;cursor:pointer;" />
                                                                    <span
                                                                        style="font-size:.75rem;color:#6B7280;margin-top:4px;display:block;">
                                                                        Chấp nhận: JPG, PNG, WEBP, GIF — tối đa 50MB
                                                                    </span>
                                                                </div>

                                                                <%-- Description --%>
                                                                    <div class="admin-field">
                                                                        <label>Description</label>
                                                                        <form:textarea path="description"
                                                                            cssClass="admin-input admin-textarea"
                                                                            rows="5"
                                                                            placeholder="Enter book description…" />
                                                                        <form:errors path="description"
                                                                            cssClass="admin-error" />
                                                                    </div>

                                                                    <%-- Action Buttons --%>
                                                                        <div class="admin-form__actions">
                                                                            <a href="/admin/books"
                                                                                class="admin-button admin-button--ghost">
                                                                                <i class="fa-solid fa-xmark"></i> Cancel
                                                                            </a>
                                                                            <button type="submit" class="admin-button"
                                                                                id="saveBtn">
                                                                                <i class="fa-solid fa-floppy-disk"></i>
                                                                                <c:choose>
                                                                                    <c:when test="${formMode=='edit'}">
                                                                                        Update Book</c:when>
                                                                                    <c:otherwise>Create Book
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </button>
                                                                        </div>

                                        </form:form>
                                    </div>
                                </div>

                    </section>
                </main>

                <script>
                    // ── Price format (xxx.xxx ₫) ─────────────────────────────────────────────
                    var priceInput = document.getElementById('priceDisplay');
                    // formatted ("299.000") — only strip thousand-separator dots,
                    // never the decimal point.
                    function formatPrice(val) {
                        var cleaned = String(val).replace(/\.(?=\d{3}(\.|$))/g, '');
                        var num = parseFloat(cleaned);
                        if (isNaN(num)) return val;
                        return Math.round(num).toLocaleString('de-DE');
                    }
                    if (priceInput && priceInput.value) {
                        priceInput.value = formatPrice(priceInput.value);
                    }
                    priceInput.addEventListener('blur', function () {
                        if (this.value) this.value = formatPrice(this.value);
                    });
                    priceInput.addEventListener('focus', function () {
                        // Strip de-DE thousand-separator dots so user can type plain number
                        this.value = String(this.value).replace(/\./g, '');
                    });

                    // Preview the picked file locally before it's ever uploaded.
                    document.getElementById('imageFile').addEventListener('change', function () {
                        var file = this.files[0];
                        if (!file) return;
                        var preview = document.getElementById('imgPreview');
                        preview.src = URL.createObjectURL(file);
                        preview.style.display = 'block';
                    });

                    var catSel = document.getElementById('categoryId');
                    catSel.addEventListener('change', function () {
                        if (this.value) {
                            document.getElementById('categoryErrorMsg').style.display = 'none';
                            this.style.borderColor = '';
                        }
                    });

                    // Clears the error state as soon as any genre checkbox gets checked.
                    var genreChecks = document.querySelectorAll('.genre-checkbox');
                    genreChecks.forEach(function (cb) {
                        cb.addEventListener('change', function () {
                            var anyChecked = Array.prototype.some.call(genreChecks, function (c) { return c.checked; });
                            if (anyChecked) {
                                document.getElementById('genreErrorMsg').style.display = 'none';
                                var list = document.getElementById('genreCheckList');
                                if (list) list.style.borderColor = '';
                            }
                        });
                    });

                    // Searchable author dropdown: a plain <select> can't do the
                    // type-to-filter + "add new author" UX we want, so this is a
                    // hand-rolled combobox backed by the hidden "author" input.
                    (function () {
                        var wrap = document.getElementById('authorCombobox');
                        var control = document.getElementById('authorControl');
                        var inputEl = document.getElementById('authorInput');
                        var hidden = document.getElementById('authorHidden');
                        var noMatch = document.getElementById('authorNoMatch');
                        if (!wrap) return; // no authors yet — fallback message is shown instead

                        var allOpts = Array.prototype.slice.call(
                            wrap.querySelectorAll('.ac-option')
                        );

                        function openList() {
                            wrap.classList.add('is-open');
                            filterList('');
                            inputEl.value = '';
                            inputEl.focus();
                        }

                        function closeList() {
                            wrap.classList.remove('is-open');
                            inputEl.value = hidden.value || '';
                            allOpts.forEach(function (o) { o.classList.remove('is-highlighted'); });
                        }

                        function pick(val) {
                            hidden.value = val;
                            inputEl.value = val;
                            allOpts.forEach(function (o) {
                                o.classList.toggle('is-selected', o.dataset.value === val);
                            });
                            wrap.classList.remove('is-open');
                            allOpts.forEach(function (o) { o.classList.remove('is-highlighted'); });
                            var err = document.getElementById('authorClientError');
                            if (err) err.style.display = 'none';
                            control.style.borderColor = '';
                        }

                        function filterList(q) {
                            var lq = q.toLowerCase();
                            var cnt = 0;
                            allOpts.forEach(function (o) {
                                var show = !lq
                                    || o.dataset.value.toLowerCase().indexOf(lq) !== -1
                                    || (o.dataset.nat || '').toLowerCase().indexOf(lq) !== -1;
                                o.style.display = show ? '' : 'none';
                                if (show) cnt++;
                            });
                            noMatch.style.display = cnt === 0 ? 'block' : 'none';
                        }

                        // Ignore clicks that land on the input itself — it has its
                        // own focus handler below, so toggling here too would fight it.
                        control.addEventListener('mousedown', function (e) {
                            if (e.target === inputEl) return;
                            e.preventDefault();
                            wrap.classList.contains('is-open') ? closeList() : openList();
                        });

                        inputEl.addEventListener('focus', function () {
                            if (!wrap.classList.contains('is-open')) openList();
                        });

                        inputEl.addEventListener('input', function () {
                            if (!wrap.classList.contains('is-open')) wrap.classList.add('is-open');
                            filterList(this.value);
                        });

                        // mousedown instead of click so this fires before the input's
                        // blur event closes the dropdown.
                        allOpts.forEach(function (o) {
                            o.addEventListener('mousedown', function (e) {
                                e.preventDefault();
                                pick(o.dataset.value);
                            });
                        });

                        document.addEventListener('mousedown', function (e) {
                            if (!wrap.contains(e.target)) closeList();
                        });

                        inputEl.addEventListener('keydown', function (e) {
                            var visible = allOpts.filter(function (o) {
                                return o.style.display !== 'none';
                            });
                            var hi = wrap.querySelector('.is-highlighted');
                            var idx = visible.indexOf(hi);

                            if (e.key === 'ArrowDown') {
                                e.preventDefault();
                                if (!wrap.classList.contains('is-open')) { openList(); return; }
                                if (hi) hi.classList.remove('is-highlighted');
                                var next = visible[idx + 1] || visible[0];
                                if (next) { next.classList.add('is-highlighted'); next.scrollIntoView({ block: 'nearest' }); }
                            } else if (e.key === 'ArrowUp') {
                                e.preventDefault();
                                if (hi) hi.classList.remove('is-highlighted');
                                var prev = visible[idx - 1] || visible[visible.length - 1];
                                if (prev) { prev.classList.add('is-highlighted'); prev.scrollIntoView({ block: 'nearest' }); }
                            } else if (e.key === 'Enter') {
                                e.preventDefault();
                                if (hi) pick(hi.dataset.value);
                            } else if (e.key === 'Escape') {
                                closeList();
                            }
                        });
                    })();

                    document.getElementById('bookForm').addEventListener('submit', function (e) {
                        // Backend expects a plain number, not the de-DE display format.
                        if (priceInput && priceInput.value) {
                            priceInput.value = String(priceInput.value).replace(/\./g, '');
                        }
                        var valid = true;

                        if (!catSel.value) {
                            e.preventDefault();
                            valid = false;
                            document.getElementById('categoryErrorMsg').style.display = 'flex';
                            catSel.style.borderColor = '#EF4444';
                        }

                        if (genreChecks.length > 0) {
                            var anyGenreChecked = Array.prototype.some.call(genreChecks, function (c) { return c.checked; });
                            if (!anyGenreChecked) {
                                e.preventDefault();
                                valid = false;
                                document.getElementById('genreErrorMsg').style.display = 'flex';
                                var list = document.getElementById('genreCheckList');
                                if (list) list.style.borderColor = '#EF4444';
                            }
                        }

                        if (!valid) return;
                    });
                </script>
            </body>

            </html>