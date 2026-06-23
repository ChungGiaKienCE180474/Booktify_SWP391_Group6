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
    <title>Edit: ${stationeryItem.name} - Booktify</title>
</head>
<body>

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content stationery-page">
        <div class="page-container page-container-narrow">

            <nav class="breadcrumb">
                <a href="/stationery"><i class="fa-solid fa-box-open"></i> Stationery</a>
                <span class="breadcrumb-sep"><i class="fa-solid fa-chevron-right"></i></span>
                <a href="/stationery/${stationeryItem.id}">${stationeryItem.name}</a>
                <span class="breadcrumb-sep"><i class="fa-solid fa-chevron-right"></i></span>
                <span class="breadcrumb-current">Edit</span>
            </nav>

            <div class="form-card">
                <div class="form-card-header form-card-header-edit">
                    <i class="fa-solid fa-pen-to-square"></i>
                    <h2>Edit Stationery Item</h2>
                </div>

                <div class="form-card-body">

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i> ${errorMessage}
                        </div>
                    </c:if>

                    <form:form method="post"
                               action="/stationery/${stationeryItem.id}/edit"
                               modelAttribute="stationeryItem"
                               enctype="multipart/form-data"
                               novalidate="true">

                        <%-- Name --%>
                        <div class="form-group">
                            <label class="form-label">Item Name <span class="required">*</span></label>
                            <form:input path="name" cssClass="form-input" />
                            <form:errors path="name" cssClass="form-error" />
                        </div>

                        <%-- Category --%>
                        <div class="form-group">
                            <label class="form-label">Category <span class="required">*</span></label>
                            <form:select path="category" cssClass="form-select">
                                <form:option value="" label="-- Select category --" />
                                <c:forEach var="cat" items="${categories}">
                                    <form:option value="${cat.name}" label="${cat.name}" />
                                </c:forEach>
                            </form:select>
                            <form:errors path="category" cssClass="form-error" />
                        </div>

                        <%-- Image --%>
                        <div class="form-group">
                            <label class="form-label">Product Image</label>
                            <div class="image-upload-area" id="imageUploadArea">
                                <div class="image-preview-wrap" id="previewWrap"
                                     style="${not empty stationeryItem.imagePath ? 'display:flex' : 'display:none'}">
                                    <img id="imagePreview"
                                         src="${not empty stationeryItem.imagePath ? stationeryItem.imagePath : ''}"
                                         alt="preview" class="image-preview" />
                                    <button type="button" class="image-remove-btn" onclick="removeImage()">
                                        <i class="fa-solid fa-xmark"></i>
                                    </button>
                                </div>
                                <div class="image-upload-placeholder" id="uploadPlaceholder"
                                     style="${not empty stationeryItem.imagePath ? 'display:none' : 'display:flex'}">
                                    <i class="fa-solid fa-cloud-arrow-up"></i>
                                    <span>Click to upload new image</span>
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
                            <form:textarea path="description" cssClass="form-textarea" rows="3" />
                        </div>

                        <%-- Price & Stock --%>
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Price (₫) <span class="required">*</span></label>
                                <div class="input-with-icon">
                                    <i class="fa-solid fa-dong-sign input-icon"></i>
                                    <form:input path="price" type="number"
                                                cssClass="form-input input-has-icon"
                                                min="1000" step="500" />
                                </div>
                                <form:errors path="price" cssClass="form-error" />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Stock Quantity <span class="required">*</span></label>
                                <div class="input-with-icon">
                                    <i class="fa-solid fa-warehouse input-icon"></i>
                                    <form:input path="stockQuantity" type="number"
                                                cssClass="form-input input-has-icon"
                                                min="1" />
                                </div>
                                <form:errors path="stockQuantity" cssClass="form-error" />
                            </div>
                        </div>

                        <%-- Supplier --%>
                        <div class="form-group">
                            <label class="form-label">Supplier</label>
                            <div class="input-with-icon">
                                <i class="fa-solid fa-truck input-icon"></i>
                                <form:input path="supplier" cssClass="form-input input-has-icon" />
                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="/stationery/${stationeryItem.id}" class="btn-back">
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
