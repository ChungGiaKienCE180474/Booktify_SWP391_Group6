<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:choose>
    <c:when test="${formMode == 'edit'}">
        <c:url var="formAction" value="/admin/categories/${category.id}" />
    </c:when>
    <c:otherwise>
        <c:url var="formAction" value="/admin/categories" />
    </c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title><c:choose><c:when test="${formMode == 'edit'}">Edit Category</c:when><c:otherwise>Create Category</c:otherwise></c:choose></title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><c:out value="${formMode}" /></p>
                    <h2><c:choose><c:when test="${formMode == 'edit'}">Edit category</c:when><c:otherwise>Create category</c:otherwise></c:choose></h2>
                </div>
                <a href="/admin/categories" class="admin-button admin-button--ghost">Back</a>
            </div>

            <div class="admin-panel">
                <form:form modelAttribute="category" action="${formAction}" method="post" class="admin-form">
                    <div class="admin-field">
                        <label>Name</label>
                        <form:input path="name" cssClass="admin-input" />
                        <form:errors path="name" cssClass="admin-error" />
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
                        <button type="submit" class="admin-button">Save</button>
                    </div>
                </form:form>
            </div>
        </section>
    </main>
</body>
</html>