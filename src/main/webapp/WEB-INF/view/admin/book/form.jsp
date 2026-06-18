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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title><c:choose><c:when test="${formMode == 'edit'}">Edit Book</c:when><c:otherwise>Create Book</c:otherwise></c:choose></title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><c:out value="${formMode}" /></p>
                    <h2><c:choose><c:when test="${formMode == 'edit'}">Edit book</c:when><c:otherwise>Create book</c:otherwise></c:choose></h2>
                </div>
                <a href="/admin/books" class="admin-button admin-button--ghost">Back</a>
            </div>

            <div class="admin-panel">
                <form:form modelAttribute="book" action="${formAction}" method="post" class="admin-form">

                    <div class="admin-field">
                        <label>Title</label>
                        <form:input path="title" cssClass="admin-input" />
                        <form:errors path="title" cssClass="admin-error" />
                    </div>

                    <div class="admin-field">
                        <label>Author</label>
                        <form:input path="author" cssClass="admin-input" />
                        <form:errors path="author" cssClass="admin-error" />
                    </div>

                    <div class="admin-field">
                        <label>ISBN</label>
                        <form:input path="isbn" cssClass="admin-input" />
                        <form:errors path="isbn" cssClass="admin-error" />
                    </div>

                    <div class="admin-field">
                        <label>Category <span style="color:#ef4444;">*</span></label>
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
                            <span class="admin-error" id="categoryErrorMsg"><c:out value="${categoryError}"/></span>
                        </c:if>
                        <c:if test="${empty categoryError}">
                            <span class="admin-error" id="categoryErrorMsg" style="display:none;">Please select a category before saving the book.</span>
                        </c:if>
                    </div>

                    <div class="admin-field">
                        <label>Price</label>
                        <form:input path="price" cssClass="admin-input" type="number" step="0.01" />
                        <form:errors path="price" cssClass="admin-error" />
                    </div>

                    <div class="admin-field">
                        <label>Stock Quantity</label>
                        <form:input path="stockQuantity" cssClass="admin-input" type="number" />
                        <form:errors path="stockQuantity" cssClass="admin-error" />
                    </div>

                    <div class="admin-field">
                        <label>Image URL</label>
                        <form:input path="imageUrl" cssClass="admin-input" />
                        <form:errors path="imageUrl" cssClass="admin-error" />
                    </div>

                    <div class="admin-field">
                        <label>Description</label>
                        <form:textarea path="description" cssClass="admin-input admin-textarea" rows="5" />
                        <form:errors path="description" cssClass="admin-error" />
                    </div>

                    <div class="admin-field admin-field--inline">
                        <label class="admin-checkbox">
                            <form:checkbox path="active" />
                            Active
                        </label>
                    </div>

                    <div class="admin-form__actions">
                        <button type="submit" class="admin-button" id="saveBtn">Save</button>
                    </div>
                </form:form>
            </div>
        </section>
    </main>

    <script>
        document.getElementById('saveBtn').closest('form').addEventListener('submit', function(e) {
            var catSelect = document.getElementById('categoryId');
            var errorMsg = document.getElementById('categoryErrorMsg');
            if (catSelect && !catSelect.value) {
                e.preventDefault();
                errorMsg.style.display = 'block';
                catSelect.focus();
                catSelect.style.borderColor = '#ef4444';
            } else if (errorMsg) {
                errorMsg.style.display = 'none';
            }
        });
        var catSelect = document.getElementById('categoryId');
        if (catSelect) {
            catSelect.addEventListener('change', function() {
                var errorMsg = document.getElementById('categoryErrorMsg');
                if (this.value) {
                    if (errorMsg) errorMsg.style.display = 'none';
                    this.style.borderColor = '';
                }
            });
        }
    </script>
</body>
</html>
