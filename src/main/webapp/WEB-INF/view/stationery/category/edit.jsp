<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/stationery.css" />
    <title>Edit Category - Booktify</title>
</head>
<body>
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content stationery-page">
        <div class="page-container page-container-narrow">

            <nav class="breadcrumb">
                <a href="/stationery"><i class="fa-solid fa-box-open"></i> Stationery</a>
                <span class="breadcrumb-sep"><i class="fa-solid fa-chevron-right"></i></span>
                <a href="/stationery/categories">Categories</a>
                <span class="breadcrumb-sep"><i class="fa-solid fa-chevron-right"></i></span>
                <span class="breadcrumb-current">Edit</span>
            </nav>

            <div class="form-card">
                <div class="form-card-header form-card-header-edit">
                    <i class="fa-solid fa-pen-to-square"></i>
                    <h2>Edit Category</h2>
                </div>
                <div class="form-card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i> ${errorMessage}
                        </div>
                    </c:if>

                    <form:form method="post"
                               action="/stationery/categories/${stationeryCategory.id}/edit"
                               modelAttribute="stationeryCategory" novalidate="true">

                        <div class="form-group">
                            <label class="form-label">Category Name <span class="required">*</span></label>
                            <form:input path="name" cssClass="form-input" />
                            <form:errors path="name" cssClass="form-error" />
                        </div>

                        <div class="form-group">
                            <label class="form-label">Description</label>
                            <form:textarea path="description" cssClass="form-textarea" rows="3" />
                        </div>

                        <div class="form-actions">
                            <a href="/stationery/categories" class="btn-back">
                                <i class="fa-solid fa-arrow-left"></i> Cancel
                            </a>
                            <button type="submit" class="btn-edit-submit">
                                <i class="fa-solid fa-floppy-disk"></i> Save Changes
                            </button>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />
</body>
</html>
