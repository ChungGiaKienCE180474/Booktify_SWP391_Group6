<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Contact Us - Booktify</title>

            <link rel="stylesheet" href="/resources/client/css/bootstrap.min.css">
            <link rel="stylesheet" href="/resources/css/header.css">
            <link rel="stylesheet" href="/resources/css/footer.css">
            <link rel="stylesheet" href="/resources/css/contact.css">

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        </head>

        <body>

            <jsp:include page="../layout/header.jsp" />

            <div class="contact-page">
                <div class="contact-container">

                    <div class="contact-breadcrumb">
                        <a href="/">Home</a>
                        <span>›</span>
                        <span>Contact Us</span>
                    </div>

                    <div class="contact-grid">

                        <div class="contact-card">
                            <h3>Contact Support</h3>

                            <c:if test="${not empty success}">
                                <div class="contact-alert success">${success}</div>
                            </c:if>

                            <c:if test="${not empty error}">
                                <div class="contact-alert error">${error}</div>
                            </c:if>

                            <form action="/contact" method="post" enctype="multipart/form-data">
                                <c:if test="${_csrf != null}">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                </c:if>

                                <div class="contact-form-group">
                                    <label>Subject</label>

                                    <select name="subject" class="contact-input" required>
                                        <option value="">-- Select a contact support --</option>

                                        <option value="Order support">Order support</option>
                                        <option value="Payment issue">Payment issue</option>
                                        <option value="Shipping issue">Shipping issue</option>
                                        <option value="Book information">Book information</option>
                                        <option value="Return or refund">Return or refund</option>
                                        <option value="Account issue">Account issue</option>
                                        <option value="Promotion or voucher"> Voucher</option>
                                        <option value="Other support">Other</option>
                                    </select>
                                </div>

                                <div class="contact-form-group">
                                    <label>Content</label>
                                    <textarea name="content" class="contact-textarea" rows="4"
                                        placeholder="Please describe your issue or question in detail..."
                                        required></textarea>
                                </div>

                                <div class="contact-form-group">
                                    <label>Upload images <span>(optional)</span></label>

                                    <label class="upload-box" for="imageInput">
                                        <input type="file" id="imageInput" name="images" multiple
                                            accept=".png,.jpg,.jpeg,image/png,image/jpeg">

                                        <span class="upload-icon">
                                            <i class="fa-solid fa-image"></i>
                                        </span>

                                        <span class="upload-text">
                                            <span id="imageFileName">
                                                Drag & drop images here or <strong>click to browse</strong>
                                            </span>
                                            <small>PNG, JPG, JPEG. Each image up to 5MB</small>
                                        </span>
                                    </label>

                                    <div id="imagePreviewBox" class="multi-preview-box" style="display: none;"></div>
                                </div>

                                <div class="contact-form-group">
                                    <label>Upload files <span>(optional)</span></label>

                                    <label class="upload-box" for="fileInput">
                                        <input type="file" id="fileInput" name="files" multiple
                                            accept=".pdf,.doc,.docx,.txt,application/pdf,text/plain">

                                        <span class="upload-icon">
                                            <i class="fa-solid fa-file-lines"></i>
                                        </span>

                                        <span class="upload-text">
                                            <span id="normalFileName">
                                                Drag & drop files here or <strong>click to browse</strong>
                                            </span>
                                            <small>PDF, DOC, DOCX, TXT. Each file up to 10MB</small>
                                        </span>
                                    </label>

                                    <div id="filePreviewBox" class="multi-preview-box" style="display: none;"></div>
                                </div>

                                <button type="submit" class="contact-submit-btn">
                                    <i class="fa-solid fa-paper-plane"></i>
                                    Submit
                                </button>
                            </form>
                        </div>

                        <div class="contact-card">
                            <h3>Support Requests</h3>

                            <table class="support-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Subject</th>
                                        <th>Created Date</th>
                                        <th>View</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <c:forEach var="item" items="${requests}" varStatus="loop">
                                        <tr>
                                            <td>${totalItems - (currentPage * pageSize + loop.index)}</td>
                                            <td>${item.subject}</td>
                                            <td class="date-cell">${item.formattedCreatedAt}</td>
                                            <td>
                                                <a href="/contact/${item.id}" class="view-btn">
                                                    View
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty requests}">
                                        <tr>
                                            <td colspan="4" class="empty-table">
                                                No support requests found.
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>

                            <c:if test="${totalPages > 0}">
                                <div class="pagination-row">
                                    <div class="pagination-box">
                                        <c:if test="${currentPage > 0}">
                                            <a href="/contact?page=${currentPage - 1}" class="page-btn">‹</a>
                                        </c:if>

                                        <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                            <a href="/contact?page=${i}"
                                                class="page-btn ${i == currentPage ? 'active' : ''}">
                                                ${i + 1}
                                            </a>
                                        </c:forEach>

                                        <c:if test="${currentPage + 1 < totalPages}">
                                            <a href="/contact?page=${currentPage + 1}" class="page-btn">›</a>
                                        </c:if>
                                    </div>

                                    <c:if test="${totalItems > 0}">
                                        <div class="showing-text">
                                            Showing ${startItem} to ${endItem} of ${totalItems} requests
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>
                        </div>

                    </div>
                </div>
            </div>

            <jsp:include page="../layout/footer.jsp" />

            <script>
                const imageInput = document.getElementById("imageInput");
                const fileInput = document.getElementById("fileInput");

                const imageFileName = document.getElementById("imageFileName");
                const normalFileName = document.getElementById("normalFileName");

                const imagePreviewBox = document.getElementById("imagePreviewBox");
                const filePreviewBox = document.getElementById("filePreviewBox");

                let selectedImages = [];
                let selectedFiles = [];

                if (imageInput) {
                    imageInput.addEventListener("change", function () {
                        const newFiles = Array.from(this.files);

                        for (const file of newFiles) {
                            if (!file.type.startsWith("image/")) {
                                alert("Please choose image files only.");
                                syncInputFiles(imageInput, selectedImages);
                                return;
                            }

                            if (file.size > 5 * 1024 * 1024) {
                                alert("Each image must be less than 5MB.");
                                syncInputFiles(imageInput, selectedImages);
                                return;
                            }

                            addUniqueFile(selectedImages, file);
                        }

                        syncInputFiles(imageInput, selectedImages);
                        renderImagePreview();
                    });
                }

                if (fileInput) {
                    fileInput.addEventListener("change", function () {
                        const newFiles = Array.from(this.files);
                        const allowedExtensions = [".pdf", ".doc", ".docx", ".txt"];

                        for (const file of newFiles) {
                            const lowerName = file.name.toLowerCase();
                            const valid = allowedExtensions.some(ext => lowerName.endsWith(ext));

                            if (!valid) {
                                alert("Only PDF, DOC, DOCX, TXT files are allowed.");
                                syncInputFiles(fileInput, selectedFiles);
                                return;
                            }

                            if (file.size > 10 * 1024 * 1024) {
                                alert("Each file must be less than 10MB.");
                                syncInputFiles(fileInput, selectedFiles);
                                return;
                            }

                            addUniqueFile(selectedFiles, file);
                        }

                        syncInputFiles(fileInput, selectedFiles);
                        renderFilePreview();
                    });
                }

                function addUniqueFile(list, file) {
                    const exists = list.some(item =>
                        item.name === file.name &&
                        item.size === file.size &&
                        item.lastModified === file.lastModified
                    );

                    if (!exists) {
                        list.push(file);
                    }
                }

                function syncInputFiles(input, files) {
                    const dataTransfer = new DataTransfer();

                    files.forEach(file => {
                        dataTransfer.items.add(file);
                    });

                    input.files = dataTransfer.files;
                }

                function renderImagePreview() {
                    imagePreviewBox.innerHTML = "";

                    if (selectedImages.length === 0) {
                        imageFileName.innerHTML = 'Drag & drop images here or <strong>click to browse</strong>';
                        imagePreviewBox.style.display = "none";
                        return;
                    }

                    imageFileName.innerHTML = "<strong>" + selectedImages.length + " image(s) selected</strong>";
                    imagePreviewBox.style.display = "grid";

                    selectedImages.forEach((file, index) => {
                        const reader = new FileReader();

                        reader.onload = function (e) {
                            const item = document.createElement("div");
                            item.className = "multi-preview-item";

                            const removeBtn = document.createElement("button");
                            removeBtn.type = "button";
                            removeBtn.className = "multi-remove-btn";
                            removeBtn.innerHTML = "×";
                            removeBtn.onclick = function () {
                                removeImage(index);
                            };

                            const img = document.createElement("img");
                            img.src = e.target.result;
                            img.className = "multi-image-preview";
                            img.alt = file.name;

                            const name = document.createElement("div");
                            name.className = "multi-preview-name";
                            name.textContent = file.name;

                            item.appendChild(removeBtn);
                            item.appendChild(img);
                            item.appendChild(name);

                            imagePreviewBox.appendChild(item);
                        };

                        reader.readAsDataURL(file);
                    });
                }

                function renderFilePreview() {
                    filePreviewBox.innerHTML = "";

                    if (selectedFiles.length === 0) {
                        normalFileName.innerHTML = 'Drag & drop files here or <strong>click to browse</strong>';
                        filePreviewBox.style.display = "none";
                        return;
                    }

                    normalFileName.innerHTML = "<strong>" + selectedFiles.length + " file(s) selected</strong>";
                    filePreviewBox.style.display = "grid";

                    selectedFiles.forEach((file, index) => {
                        const item = document.createElement("div");
                        item.className = "multi-preview-item file-preview-item";

                        const removeBtn = document.createElement("button");
                        removeBtn.type = "button";
                        removeBtn.className = "multi-remove-btn";
                        removeBtn.innerHTML = "×";
                        removeBtn.onclick = function () {
                            removeFile(index);
                        };

                        const icon = document.createElement("div");
                        icon.className = "multi-file-icon";
                        icon.innerHTML = '<i class="fa-solid fa-file"></i>';

                        const name = document.createElement("div");
                        name.className = "multi-preview-name";
                        name.textContent = file.name;

                        const size = document.createElement("div");
                        size.className = "multi-preview-size";
                        size.textContent = formatFileSize(file.size);

                        item.appendChild(removeBtn);
                        item.appendChild(icon);
                        item.appendChild(name);
                        item.appendChild(size);

                        filePreviewBox.appendChild(item);
                    });
                }

                function removeImage(index) {
                    selectedImages.splice(index, 1);
                    syncInputFiles(imageInput, selectedImages);
                    renderImagePreview();
                }

                function removeFile(index) {
                    selectedFiles.splice(index, 1);
                    syncInputFiles(fileInput, selectedFiles);
                    renderFilePreview();
                }

                function formatFileSize(bytes) {
                    if (bytes < 1024) {
                        return bytes + " B";
                    }

                    if (bytes < 1024 * 1024) {
                        return (bytes / 1024).toFixed(1) + " KB";
                    }

                    return (bytes / (1024 * 1024)).toFixed(1) + " MB";
                }
            </script>

        </body>

        </html>