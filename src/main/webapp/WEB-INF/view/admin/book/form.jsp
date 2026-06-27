<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:choose>
    <c:when test="${formMode == 'edit'}"><c:url var="formAction" value="/admin/books/${book.id}" /></c:when>
    <c:otherwise><c:url var="formAction" value="/admin/books" /></c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title><c:choose><c:when test="${formMode=='edit'}">Edit Book</c:when><c:otherwise>Create Book</c:otherwise></c:choose> — Booktify Admin</title>
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
                        <c:choose><c:when test="${formMode=='edit'}">Edit Book</c:when><c:otherwise>Create Book</c:otherwise></c:choose>
                    </p>
                    <h2><c:choose><c:when test="${formMode=='edit'}">Edit Book</c:when><c:otherwise>New Book</c:otherwise></c:choose></h2>
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
                    <form:form modelAttribute="book" action="${formAction}" method="post" enctype="multipart/form-data" class="admin-form" id="bookForm">

                        <%-- Title + Author --%>
                        <div class="admin-form-grid">
                            <div class="admin-field">
                                <label>Title <span style="color:#EF4444;">*</span></label>
                                <form:input path="title" cssClass="admin-input" placeholder="Enter book title…" />
                                <form:errors path="title" cssClass="admin-error" />
                            </div>
                            <div class="admin-field">
                                <label>Author <span style="color:#EF4444;">*</span></label>
                                <form:input path="author" cssClass="admin-input" placeholder="Enter author name…" />
                                <form:errors path="author" cssClass="admin-error" />
                            </div>
                        </div>

                        <%-- ISBN + Category --%>
                        <div class="admin-form-grid">
                            <div class="admin-field">
                                <label>ISBN</label>
                                <form:input path="isbn" cssClass="admin-input" placeholder="Optional — e.g. 978-3-16-148410-0" />
                                <form:errors path="isbn" cssClass="admin-error" />
                            </div>
                            <div class="admin-field">
                                <label>Category <span style="color:#EF4444;">*</span></label>
                                <select name="categoryId" id="categoryId" class="admin-input" required>
                                    <option value="">— Select a category —</option>
                                    <c:forEach items="${categories}" var="cat">
                                        <option value="${cat.id}"
                                            <c:if test="${not empty book.category and book.category.id == cat.id}">selected</c:if>>
                                            <c:out value="${cat.name}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                                <c:if test="${not empty categoryError}">
                                    <span class="admin-error" id="categoryErrorMsg">
                                        <i class="fa-solid fa-circle-exclamation"></i>
                                        <c:out value="${categoryError}"/>
                                    </span>
                                </c:if>
                                <c:if test="${empty categoryError}">
                                    <span class="admin-error" id="categoryErrorMsg" style="display:none;">
                                        <i class="fa-solid fa-circle-exclamation"></i>
                                        Please select a category before saving the book.
                                    </span>
                                </c:if>
                            </div>
                        </div>

                        <%-- Price + Genre --%>
                        <div class="admin-form-grid">
                            <div class="admin-field">
                                <label>Price <span style="color:#EF4444;">*</span></label>
                                <div style="position:relative;">
                                    <form:input path="price" cssClass="admin-input" type="text" id="priceDisplay" placeholder="0" style="padding-right:36px;" />
                                    <span style="position:absolute;right:12px;top:50%;transform:translateY(-50%);color:#6B7280;font-size:.85rem;pointer-events:none;">₫</span>
                                </div>
                                <form:errors path="price" cssClass="admin-error" />
                            </div>
                            <div class="admin-field">
                                <label>Genre <span style="color:#9CA3AF;font-size:.8rem;">(optional)</span></label>
                                <select name="genreId" id="genreId" class="admin-input"
                                        <c:if test="${empty book.category}">disabled</c:if>>
                                    <c:choose>
                                        <c:when test="${empty book.category}">
                                            <option value="">— Select a category first —</option>
                                        </c:when>
                                        <c:when test="${empty genres}">
                                            <option value="">— No genres in this category —</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="">— None (optional) —</option>
                                            <c:forEach items="${genres}" var="g">
                                                <option value="${g.id}"
                                                    <c:if test="${not empty book.genre and book.genre.id == g.id}">selected</c:if>>
                                                    <c:out value="${g.name}"/>
                                                </option>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </select>
                                <c:if test="${not empty genreError}">
                                    <span class="admin-error" id="genreErrorMsg">
                                        <i class="fa-solid fa-circle-exclamation"></i>
                                        <c:out value="${genreError}"/>
                                    </span>
                                </c:if>
                                <c:if test="${empty genreError}">
                                    <span class="admin-error" id="genreErrorMsg" style="display:none;">
                                        <i class="fa-solid fa-circle-exclamation"></i>
                                        Selected genre does not belong to the selected category.
                                    </span>
                                </c:if>
                            </div>
                        </div>

                        <%-- Image Upload --%>
                        <div class="admin-field">
                            <label>Book Cover Image</label>
                            <c:if test="${formMode == 'edit' and not empty book.imageUrl}">
                                <div style="margin-bottom:10px;">
                                    <img src="<c:out value='${book.imageUrl}'/>" alt="Current cover"
                                         id="imgPreview"
                                         style="height:120px;width:90px;object-fit:cover;border-radius:6px;border:1px solid #E5E7EB;display:block;" />
                                    <span style="font-size:.75rem;color:#6B7280;margin-top:4px;display:block;">Ảnh hiện tại — upload file mới để thay thế</span>
                                </div>
                            </c:if>
                            <c:if test="${not (formMode == 'edit' and not empty book.imageUrl)}">
                                <div style="margin-bottom:10px;">
                                    <img id="imgPreview"
                                         style="height:120px;width:90px;object-fit:cover;border-radius:6px;border:1px solid #E5E7EB;display:none;" />
                                </div>
                            </c:if>
                            <input type="file" name="imageFile" id="imageFile"
                                   accept="image/jpeg,image/png,image/webp,image/gif"
                                   class="admin-input" style="padding:6px 10px;cursor:pointer;" />
                            <span style="font-size:.75rem;color:#6B7280;margin-top:4px;display:block;">
                                Chấp nhận: JPG, PNG, WEBP, GIF — tối đa 50MB
                            </span>
                        </div>

                        <%-- Description --%>
                        <div class="admin-field">
                            <label>Description</label>
                            <form:textarea path="description" cssClass="admin-input admin-textarea" rows="5" placeholder="Enter book description…" />
                            <form:errors path="description" cssClass="admin-error" />
                        </div>

                        <%-- Actions --%>
                        <div class="admin-form__actions">
                            <a href="/admin/books" class="admin-button admin-button--ghost">
                                <i class="fa-solid fa-xmark"></i> Cancel
                            </a>
                            <button type="submit" class="admin-button" id="saveBtn">
                                <i class="fa-solid fa-floppy-disk"></i>
                                <c:choose><c:when test="${formMode=='edit'}">Update Book</c:when><c:otherwise>Create Book</c:otherwise></c:choose>
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
        function formatPrice(val) {
            // val is either raw from Spring ("299000.00") or already stripped plain number ("299000")
            // Remove thousand-separator dots only (de-DE format), keep decimal dot from Spring
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

        // ── Image preview ────────────────────────────────────────────────────────
        document.getElementById('imageFile').addEventListener('change', function () {
            var file = this.files[0];
            if (!file) return;
            var preview = document.getElementById('imgPreview');
            preview.src = URL.createObjectURL(file);
            preview.style.display = 'block';
        });

        // ── Category → Genre cascade (AJAX) ──────────────────────────────────────
        var catSel   = document.getElementById('categoryId');
        var genreSel = document.getElementById('genreId');

        function loadGenres(categoryId, selectedGenreId) {
            if (!categoryId) {
                genreSel.innerHTML = '<option value="">— Select a category first —</option>';
                genreSel.disabled = true;
                return;
            }
            genreSel.disabled = true;
            genreSel.innerHTML = '<option value="">Loading…</option>';
            fetch('/admin/genres/by-category?categoryId=' + categoryId)
                .then(function (r) { return r.json(); })
                .then(function (data) {
                    if (!data || data.length === 0) {
                        genreSel.innerHTML = '<option value="">— No genres available —</option>';
                        genreSel.disabled = true;
                        return;
                    }
                    var html = '<option value="">— Select a genre —</option>';
                    data.forEach(function (g) {
                        var sel = (selectedGenreId && String(g.id) === String(selectedGenreId)) ? ' selected' : '';
                        html += '<option value="' + g.id + '"' + sel + '>' + g.name + '</option>';
                    });
                    genreSel.innerHTML = html;
                    genreSel.disabled = false;
                })
                .catch(function () {
                    genreSel.innerHTML = '<option value="">— Failed to load —</option>';
                    genreSel.disabled = true;
                });
        }

        catSel.addEventListener('change', function () {
            if (this.value) {
                document.getElementById('categoryErrorMsg').style.display = 'none';
                this.style.borderColor = '';
            }
            document.getElementById('genreErrorMsg').style.display = 'none';
            genreSel.style.borderColor = '';
            loadGenres(this.value, null);
        });

        // Khi edit: nếu category đã chọn sẵn và có genres thì enable dropdown
        (function () {
            if (catSel.value && genreSel.options.length > 1) {
                genreSel.disabled = false;
            }
        })();

        // ── Form validation ───────────────────────────────────────────────────────
        // Category is required. Genre is optional (but filtered by category).
        document.getElementById('bookForm').addEventListener('submit', function (e) {
            // Strip price formatting before submit so backend gets a plain number
            if (priceInput && priceInput.value) {
                priceInput.value = String(priceInput.value).replace(/\./g, '');
            }
            if (!catSel.value) {
                e.preventDefault();
                document.getElementById('categoryErrorMsg').style.display = 'flex';
                catSel.style.borderColor = '#EF4444';
                catSel.focus();
            }
        });

        genreSel.addEventListener('change', function () {
            document.getElementById('genreErrorMsg').style.display = 'none';
            this.style.borderColor = '';
        });
    </script>
</body>
</html>
