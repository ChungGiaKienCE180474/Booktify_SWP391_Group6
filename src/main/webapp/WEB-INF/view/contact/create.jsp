<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>Create Contact — Booktify</title>

    <link rel="stylesheet" href="${ctx}/css/header.css" />
    <link rel="stylesheet" href="${ctx}/css/footer.css" />
    <link rel="stylesheet" href="${ctx}/css/homepage.css" />

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <style>
        .contact-create-page {
            background: #F5F7FB;
            min-height: 100vh;
        }

        .contact-create-wrap {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1.5rem 1.5rem 3.5rem;
            display: grid;
            grid-template-columns: 220px 1fr;
            gap: 1.5rem;
            align-items: start;
        }

        .contact-sidebar {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: var(--shadow-sm);
            position: sticky;
            top: 112px;
        }

        .sidebar-head {
            background: var(--primary);
            color: #fff;
            padding: .75rem 1rem;
            font-size: .8rem;
            font-weight: 900;
            text-transform: uppercase;
            letter-spacing: .08em;
            display: flex;
            align-items: center;
            gap: .55rem;
        }

        .sidebar-list {
            padding: .5rem 0;
        }

        .sidebar-item {
            display: flex;
            align-items: center;
            gap: .55rem;
            padding: .65rem 1rem;
            text-decoration: none;
            color: var(--text-muted);
            font-size: .9rem;
            font-weight: 650;
            transition: background .12s, color .12s;
        }

        .sidebar-item:hover {
            background: rgba(0, 107, 94, .06);
            color: var(--primary);
        }

        .sidebar-item.active {
            background: rgba(0, 107, 94, .08);
            color: var(--primary);
            font-weight: 850;
        }

        .sidebar-item .dot {
            width: 7px;
            height: 7px;
            border-radius: 50%;
            background: #E5E7EB;
            flex-shrink: 0;
        }

        .sidebar-item.active .dot,
        .sidebar-item:hover .dot {
            background: var(--primary);
        }

        .contact-create-main {
            min-width: 0;
        }

        .contact-breadcrumb {
            display: flex;
            align-items: center;
            gap: .45rem;
            font-size: .82rem;
            color: var(--text-muted);
            margin-bottom: 1rem;
        }

        .contact-breadcrumb a {
            color: var(--text-muted);
            text-decoration: none;
        }

        .contact-breadcrumb a:hover {
            color: var(--primary);
        }

        .contact-breadcrumb i {
            font-size: .6rem;
            opacity: .45;
        }

        .contact-create-card {
            background: #fff;
            border: 1px solid #E5E7EB;
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-sm);
            overflow: hidden;
        }

        .form-title {
            padding: 1rem 1.25rem;
            border-bottom: 1px solid #E5E7EB;
            font-size: 1.05rem;
            font-weight: 900;
            color: #111827;
            display: flex;
            align-items: center;
            gap: .55rem;
        }

        .form-title i {
            color: var(--primary);
        }

        .form-body {
            padding: 1.25rem;
        }

        .form-row {
            margin-bottom: 1rem;
        }

        .form-label {
            display: block;
            margin-bottom: .45rem;
            color: #374151;
            font-size: .75rem;
            font-weight: 900;
            letter-spacing: .08em;
            text-transform: uppercase;
        }

        .required {
            color: #ee4d2d;
        }

        .field-help {
            display: block;
            margin-top: .25rem;
            color: #6B7280;
            font-size: .72rem;
            line-height: 1.45;
        }

        .form-input,
        .form-select,
        .form-textarea {
            width: 100%;
            box-sizing: border-box;
            border: 1px solid #D1D5DB;
            background: #fff;
            border-radius: var(--radius);
            padding: .8rem .9rem;
            color: #111827;
            font: inherit;
            font-size: .9rem;
            outline: none;
        }

        .form-select {
            cursor: pointer;
        }

        .form-textarea {
            min-height: 150px;
            resize: vertical;
            line-height: 1.55;
        }

        .form-input:focus,
        .form-select:focus,
        .form-textarea:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(0, 107, 94, .1);
        }

        .upload-box {
            position: relative;
            border: 1px dashed #CBD5E1;
            border-radius: var(--radius);
            background: #F8FAFC;
            min-height: 112px;
            padding: 1rem;
            display: flex;
            align-items: center;
            gap: .85rem;
            cursor: pointer;
            overflow: hidden;
        }

        .upload-box:hover {
            border-color: var(--primary);
            background: rgba(0, 107, 94, .04);
        }

        .upload-box input {
            position: absolute;
            inset: 0;
            opacity: 0;
            cursor: pointer;
        }

        .upload-icon {
            width: 44px;
            height: 44px;
            border-radius: 14px;
            background: #EAF2FF;
            color: var(--primary);
            display: grid;
            place-items: center;
            flex: 0 0 auto;
            font-size: 1rem;
        }

        .upload-text strong {
            display: block;
            font-size: .84rem;
            color: #111827;
            line-height: 1.35;
            text-transform: uppercase;
            letter-spacing: .04em;
        }

        .upload-text span {
            display: block;
            margin-top: .25rem;
            font-size: .72rem;
            color: #6B7280;
            line-height: 1.45;
        }

        .upload-preview {
            margin-top: .65rem;
            display: grid;
            gap: .45rem;
        }

        .preview-item {
            padding: .48rem .6rem;
            border: 1px solid #E5E7EB;
            background: #F9FAFB;
            border-radius: 8px;
            color: #374151;
            font-size: .78rem;
            display: flex;
            align-items: center;
            gap: .45rem;
        }

        .preview-item > i {
            color: var(--primary);
        }

        .preview-item-name {
            flex: 1;
            min-width: 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .preview-remove {
            width: 26px;
            height: 26px;
            border: 1px solid #E5E7EB;
            border-radius: 7px;
            background: #fff;
            color: #DC2626;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .preview-remove i {
            color: #DC2626;
        }

        .preview-remove:hover {
            background: #FEF2F2;
            border-color: #FCA5A5;
        }

        .privacy-check {
            display: flex;
            align-items: flex-start;
            gap: .55rem;
            color: #374151;
            font-size: .82rem;
            line-height: 1.5;
            margin-top: .5rem;
        }

        .privacy-check input {
            margin-top: .18rem;
            flex-shrink: 0;
        }

        .privacy-link {
            display: inline-block;
            margin-top: .7rem;
            color: var(--primary);
            text-decoration: none;
            font-size: .78rem;
            font-weight: 800;
        }

        .privacy-link:hover {
            text-decoration: underline;
        }

        .form-actions {
            padding: 1rem 1.25rem;
            border-top: 1px solid #E5E7EB;
            display: flex;
            justify-content: flex-end;
            gap: .75rem;
            background: #fff;
        }

        .btn-submit {
            min-width: 86px;
            height: 42px;
            border: 0;
            border-radius: var(--radius);
            background: var(--primary);
            color: #fff;
            font-weight: 850;
            cursor: pointer;
        }

        .btn-submit:hover {
            background: var(--primary-light);
        }

        .btn-cancel {
            min-width: 86px;
            height: 42px;
            border: 1px solid #D1D5DB;
            border-radius: var(--radius);
            background: #fff;
            color: #374151;
            font-weight: 850;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .btn-cancel:hover {
            background: #F9FAFB;
        }

        .contact-alert {
            margin-bottom: 1rem;
            border-radius: var(--radius);
            padding: .85rem 1rem;
            font-size: .84rem;
            font-weight: 750;
        }

        .contact-alert--danger {
            background: #FEF2F2;
            color: #DC2626;
            border: 1px solid #FECACA;
        }

        @media (max-width: 980px) {
            .contact-create-wrap {
                grid-template-columns: 1fr;
            }

            .contact-sidebar {
                position: static;
            }

            .sidebar-list {
                display: flex;
                flex-wrap: wrap;
                padding: .4rem;
                gap: .2rem;
            }

            .sidebar-item {
                border-radius: var(--radius);
                padding: .5rem .75rem;
            }
        }

        @media (max-width: 640px) {
            .contact-create-wrap {
                padding: 1rem 1rem 2.5rem;
            }

            .form-body {
                padding: 1rem;
            }

            .form-actions {
                flex-direction: column-reverse;
            }

            .btn-submit,
            .btn-cancel {
                width: 100%;
            }
        }
    </style>
</head>

<body class="contact-create-page">

<jsp:include page="/WEB-INF/view/layout/header.jsp" />

<div class="contact-create-wrap">

    <aside class="contact-sidebar">
        <div class="sidebar-head">
            <i class="fa-solid fa-bars"></i>
            Contact Status
        </div>

        <div class="sidebar-list">
            <a href="${ctx}/contact" class="sidebar-item active">
                <span class="dot"></span>
                All Requests
            </a>

            <a href="${ctx}/contact?status=OPEN" class="sidebar-item">
                <span class="dot"></span>
                Open
            </a>

            <a href="${ctx}/contact?status=IN_PROGRESS" class="sidebar-item">
                <span class="dot"></span>
                In Progress
            </a>

            <a href="${ctx}/contact?status=COMPLETE" class="sidebar-item">
                <span class="dot"></span>
                Completed
            </a>
        </div>
    </aside>

    <main class="contact-create-main">

        <nav class="contact-breadcrumb">
            <a href="${ctx}/contact">Contact Support</a>
            <i class="fa-solid fa-chevron-right"></i>
            <span>New Support Request</span>
        </nav>

        <c:if test="${not empty error}">
            <div class="contact-alert contact-alert--danger">
                <c:out value="${error}" />
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="contact-alert contact-alert--danger">
                <c:out value="${errorMessage}" />
            </div>
        </c:if>

        <section class="contact-create-card">
            <div class="form-title">
                <i class="fa-solid fa-pen-to-square"></i>
                New Support Request
            </div>

            <div class="form-body">
                <form method="post"
                      action="${ctx}/contact"
                      enctype="multipart/form-data"
                      id="contactCreateForm">

                    <input type="hidden"
                           name="${_csrf.parameterName}"
                           value="${_csrf.token}" />

                    <div class="form-row">
                        <label class="form-label" for="issueType">
                            <span class="required">*</span>
                            What issue are you having?
                        </label>

                        <select id="issueType"
                                name="issueType"
                                class="form-select"
                                required>
                            <option value="">Please select</option>
                            <option value="Order support">Order support</option>
                            <option value="Payment issue">Payment issue</option>
                            <option value="Shipping issue">Shipping issue</option>
                            <option value="Book information">Book information</option>
                            <option value="Stationery product">Stationery product</option>
                            <option value="Account support">Account support</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>

                    <div class="form-row">
                        <label class="form-label" for="contactEmail">
                            <span class="required">*</span>
                            Email
                        </label>

                        <input type="email"
                               id="contactEmail"
                               name="contactEmail"
                               class="form-input"
                               placeholder="Enter your email"
                               required />

                        <span class="field-help">
                            Please enter an email address that Booktify can use to contact you.
                        </span>
                    </div>

                    <div class="form-row">
                        <label class="form-label" for="content">
                            <span class="required">*</span>
                            Feedback Content
                        </label>

                        <textarea id="content"
                                  name="content"
                                  class="form-textarea"
                                  placeholder="Enter your feedback"
                                  required></textarea>
                    </div>

                    <div class="form-row">
                        <label class="form-label">
                            Attachments
                        </label>

                        <label class="upload-box">
                            <input type="file"
                                   name="attachmentFiles"
                                   id="attachmentFiles"
                                   accept=".png,.jpg,.jpeg,.webp,.pdf,.doc,.docx,.xls,.xlsx,.txt,.zip"
                                   multiple />

                            <span class="upload-icon">
                                <i class="fa-regular fa-file-lines"></i>
                            </span>

                            <span class="upload-text">
                                <strong>Click to upload files</strong>
                                <span>PNG, JPG, JPEG, WEBP, PDF, DOC, DOCX, XLS, XLSX, TXT, ZIP. Each file up to 10MB.</span>
                            </span>
                        </label>

                        <div id="attachmentPreviewList" class="upload-preview"></div>
                    </div>

                    <label class="privacy-check">
                        <input type="checkbox"
                               name="consentAccepted"
                               value="true"
                               required />

                        <span>
                            I agree that Booktify may collect and process my information according to the privacy policy.
                        </span>
                    </label>

                    <a href="#" class="privacy-link">
                        Booktify Privacy Policy
                    </a>

                </form>
            </div>

            <div class="form-actions">
                <a href="${ctx}/contact" class="btn-cancel">
                    <i class="fa-solid fa-xmark"></i>
                    &nbsp;Cancel
                </a>

                <button type="reset"
                        form="contactCreateForm"
                        class="btn-cancel">
                    <i class="fa-solid fa-rotate-left"></i>
                    &nbsp;Reset
                </button>

                <button type="submit"
                        form="contactCreateForm"
                        class="btn-submit">
                    Send
                </button>
            </div>
        </section>

    </main>

</div>

<jsp:include page="/WEB-INF/view/layout/footer.jsp" />

<script>
    const attachmentFiles = document.getElementById("attachmentFiles");
    const attachmentPreviewList = document.getElementById("attachmentPreviewList");

    let selectedAttachments = [];

    function fileKey(file) {
        return file.name + "_" + file.size + "_" + file.lastModified;
    }

    function syncFileInput() {
        if (!attachmentFiles) {
            return;
        }

        const dataTransfer = new DataTransfer();

        selectedAttachments.forEach(function (file) {
            dataTransfer.items.add(file);
        });

        attachmentFiles.files = dataTransfer.files;
    }

    function renderAttachmentList() {
        if (!attachmentPreviewList) {
            return;
        }

        attachmentPreviewList.innerHTML = "";

        if (selectedAttachments.length === 0) {
            return;
        }

        selectedAttachments.forEach(function (file, index) {
            const item = document.createElement("div");
            item.className = "preview-item";

            const icon = document.createElement("i");
            icon.className = file.type && file.type.startsWith("image/")
                ? "fa-regular fa-image"
                : "fa-regular fa-file-lines";

            const name = document.createElement("span");
            name.className = "preview-item-name";
            name.textContent = file.name;

            const removeButton = document.createElement("button");
            removeButton.type = "button";
            removeButton.className = "preview-remove";
            removeButton.title = "Remove file";
            removeButton.innerHTML = '<i class="fa-solid fa-xmark"></i>';

            removeButton.addEventListener("click", function () {
                selectedAttachments.splice(index, 1);
                syncFileInput();
                renderAttachmentList();
            });

            item.appendChild(icon);
            item.appendChild(name);
            item.appendChild(removeButton);

            attachmentPreviewList.appendChild(item);
        });
    }

    if (attachmentFiles) {
        attachmentFiles.addEventListener("change", function () {
            const newFiles = Array.from(this.files);

            newFiles.forEach(function (file) {
                const exists = selectedAttachments.some(function (currentFile) {
                    return fileKey(currentFile) === fileKey(file);
                });

                if (!exists) {
                    selectedAttachments.push(file);
                }
            });

            syncFileInput();
            renderAttachmentList();
        });
    }

    document.addEventListener("reset", function () {
        setTimeout(function () {
            selectedAttachments = [];
            syncFileInput();
            renderAttachmentList();
        }, 0);
    });
</script>

</body>

</html>