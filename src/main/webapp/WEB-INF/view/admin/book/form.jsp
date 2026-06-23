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
                    <form:form modelAttribute="book" action="${formAction}" method="post" class="admin-form" id="bookForm">

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

                        <%-- Price + Stock --%>
                        <div class="admin-form-grid">
                            <div class="admin-field">
                                <label>Price <span style="color:#EF4444;">*</span></label>
                                <form:input path="price" cssClass="admin-input" type="number" step="0.01" placeholder="0.00" />
                                <form:errors path="price" cssClass="admin-error" />
                            </div>
                            <div class="admin-field">
                                <label>Stock Quantity</label>
                                <form:input path="stockQuantity" cssClass="admin-input" type="number" placeholder="0" />
                                <form:errors path="stockQuantity" cssClass="admin-error" />
                            </div>
                        </div>

                        <%-- Image URL --%>
                        <div class="admin-field">
                            <label>Image URL</label>
                            <form:input path="imageUrl" cssClass="admin-input" placeholder="https://…" />
                            <form:errors path="imageUrl" cssClass="admin-error" />
                        </div>

                        <%-- Description --%>
                        <div class="admin-field">
                            <label>Description</label>
                            <form:textarea path="description" cssClass="admin-input admin-textarea" rows="5" placeholder="Enter book description…" />
                            <form:errors path="description" cssClass="admin-error" />
                        </div>

                        <%-- Active --%>
                        <div class="admin-field admin-field--inline">
                            <label class="admin-checkbox">
                                <form:checkbox path="active" />
                                Active (visible to users)
                            </label>
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
        document.getElementById('bookForm').addEventListener('submit', function(e) {
            var cat = document.getElementById('categoryId');
            var err = document.getElementById('categoryErrorMsg');
            if (cat && !cat.value) {
                e.preventDefault();
                err.style.display = 'flex';
                cat.style.borderColor = '#EF4444';
                cat.focus();
            }
        });
        var cat = document.getElementById('categoryId');
        if (cat) {
            cat.addEventListener('change', function() {
                if (this.value) {
                    document.getElementById('categoryErrorMsg').style.display = 'none';
                    this.style.borderColor = '';
                }
            });
        }
    </script>
</body>
</html>
