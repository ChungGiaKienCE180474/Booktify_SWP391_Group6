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
    <title>Add New Stationery - Booktify</title>
</head>
<body>

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content stationery-page">
        <div class="page-container page-container-narrow">

            <nav class="breadcrumb">
                <a href="/stationery"><i class="fa-solid fa-box-open"></i> Stationery</a>
                <span class="breadcrumb-sep"><i class="fa-solid fa-chevron-right"></i></span>
                <span class="breadcrumb-current">Add New Item</span>
            </nav>

            <div class="form-card">
                <div class="form-card-header">
                    <i class="fa-solid fa-plus-circle"></i>
                    <h2>Add New Stationery Item</h2>
                </div>

                <div class="form-card-body">

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i> ${errorMessage}
                        </div>
                    </c:if>

                    <form:form method="post" action="/stationery/create"
                               modelAttribute="stationeryItem"
                               enctype="multipart/form-data"
                               novalidate="true">

                        <%-- Name --%>
                        <div class="form-group">
                            <label class="form-label">Item Name <span class="required">*</span></label>
                            <form:input path="name" cssClass="form-input"
                                        placeholder="Enter item name..." />
                            <form:errors path="name" cssClass="form-error" />
                        </div>

                        <%-- Category --%>
                        <div class="form-group">
                            <label class="form-label">Category <span class="required">*</span></label>
                            <c:choose>
                                <c:when test="${empty categories}">
                                    <div class="alert alert-error" style="margin-bottom:0">
                                        <i class="fa-solid fa-circle-exclamation"></i>
                                        No categories yet.
                                        <a href="/stationery/categories/create" style="color:inherit;font-weight:600">Add one first</a>.
                                    </div>
                                    <form:hidden path="category" />
                                </c:when>
                                <c:otherwise>
                                    <form:select path="category" cssClass="form-select">
                                        <form:option value="" label="-- Select category --" />
                                        <c:forEach var="cat" items="${categories}">
                                            <form:option value="${cat.name}" label="${cat.name}" />
                                        </c:forEach>
                                    </form:select>
                                </c:otherwise>
                            </c:choose>
                            <form:errors path="category" cssClass="form-error" />
                        </div>

                        <%-- Image --%>
                        <div class="form-group">
                            <label class="form-label">Product Image</label>
                            <div class="image-upload-area" id="imageUploadArea">
                                <div class="image-preview-wrap" id="previewWrap" style="display:none;">
                                    <img id="imagePreview" src="" alt="preview" class="image-preview" />
                                    <button type="button" class="image-remove-btn" onclick="removeImage()">
                                        <i class="fa-solid fa-xmark"></i>
                                    </button>
                                </div>
                                <div class="image-upload-placeholder" id="uploadPlaceholder">
                                    <i class="fa-solid fa-cloud-arrow-up"></i>
                                    <span>Click to upload image</span>
                                    <small>JPG, PNG, GIF — max 5 MB</small>
                                </div>
                                <input type="file" name="imageFile" id="imageFile"
                                       accept="image/*" style="display:none;"
                                       onchange="previewImage(this)" />
                            </div>
                        </div>

                        <%-- Description --%>
                        <div class="form-group">
                            <label class="form-label">Description</label>
                            <form:textarea path="description" cssClass="form-textarea" rows="3"
                                           placeholder="Describe this item (optional)..." />
                        </div>

                        <%-- Price & Stock --%>
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Price (₫) <span class="required">*</span></label>
                                <div class="input-with-icon">
                                    <i class="fa-solid fa-dong-sign input-icon"></i>
                                    <form:input path="price" type="number"
                                                cssClass="form-input input-has-icon"
                                                placeholder="Min 1.000" min="1000" step="500" />
                                </div>
                                <form:errors path="price" cssClass="form-error" />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Initial Stock <span class="required">*</span></label>
                                <div class="input-with-icon">
                                    <i class="fa-solid fa-warehouse input-icon"></i>
                                    <form:input path="stockQuantity" type="number"
                                                cssClass="form-input input-has-icon"
                                                placeholder="1" min="1" />
                                </div>
                                <form:errors path="stockQuantity" cssClass="form-error" />
                            </div>
                        </div>

                        <%-- Supplier --%>
                        <div class="form-group">
                            <label class="form-label">Supplier</label>
                            <div class="input-with-icon">
                                <i class="fa-solid fa-truck input-icon"></i>
                                <form:input path="supplier" cssClass="form-input input-has-icon"
                                            placeholder="Supplier name (optional)" />
                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="/stationery" class="btn-back">
                                <i class="fa-solid fa-arrow-left"></i> Cancel
                            </a>
                            <button type="submit" class="btn-primary-action">
                                <i class="fa-solid fa-floppy-disk"></i> Save Item
                            </button>
                        </div>

                    </form:form>
                </div>
            </div>

        </div>
    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

    <script>
        document.getElementById('imageUploadArea').addEventListener('click', function(e) {
            if (!e.target.closest('.image-remove-btn')) {
                document.getElementById('imageFile').click();
            }
        });

        function previewImage(input) {
            if (input.files && input.files[0]) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('imagePreview').src = e.target.result;
                    document.getElementById('previewWrap').style.display = 'flex';
                    document.getElementById('uploadPlaceholder').style.display = 'none';
                };
                reader.readAsDataURL(input.files[0]);
            }
        }

        function removeImage() {
            document.getElementById('imageFile').value = '';
            document.getElementById('previewWrap').style.display = 'none';
            document.getElementById('uploadPlaceholder').style.display = 'flex';
        }

        document.getElementById('navToggle')?.addEventListener('click', function() {
            document.querySelector('.main-nav')?.classList.toggle('open');
        });
    </script>

</body>
</html>
