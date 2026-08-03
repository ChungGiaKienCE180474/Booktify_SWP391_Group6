<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<c:choose>
    <c:when test="${formMode == 'edit'}">
        <c:set var="formAction" value="${ctx}/admin/vpp/${itemId}" />
        <c:set var="pageKicker" value="EDIT VPP PRODUCT" />
        <c:set var="pageTitle" value="Edit VPP Product" />
        <c:set var="submitLabel" value="Update Product" />
    </c:when>

    <c:otherwise>
        <c:set var="formAction" value="${ctx}/admin/vpp" />
        <c:set var="pageKicker" value="CREATE VPP PRODUCT" />
        <c:set var="pageTitle" value="New VPP Product" />
        <c:set var="submitLabel" value="Create Product" />
    </c:otherwise>
</c:choose>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>${pageTitle} — Booktify Admin</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />

    <style>
        .vpp-form-file {
            width: 100%;
            padding: 9px 12px;
            border-radius: var(--radius-sm);
            border: 1px solid var(--border-md);
            background: var(--white);
            color: var(--text);
            font-family: inherit;
            font-size: .875rem;
            box-sizing: border-box;
            outline: none;
        }

        .vpp-form-file:hover {
            border-color: #9CA3AF;
        }

        .vpp-form-file:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37, 99, 235, .12);
        }

        .vpp-current-image {
            margin-top: 12px;
            width: 128px;
            height: 128px;
            border: 1px solid var(--border);
            border-radius: var(--radius-sm);
            background: #F9FAFB;
            display: grid;
            place-items: center;
            overflow: hidden;
        }

        .vpp-current-image img {
            width: 100%;
            height: 100%;
            object-fit: contain;
            padding: 6px;
            box-sizing: border-box;
            display: block;
        }

        .vpp-current-image-empty {
            color: #9CA3AF;
            font-size: .78rem;
            text-align: center;
            padding: 10px;
        }

        .vpp-current-image-empty i {
            display: block;
            font-size: 2rem;
            margin-bottom: 6px;
            opacity: .5;
        }

        .vpp-price-wrap {
            position: relative;
        }

        .vpp-price-wrap .admin-input {
            padding-right: 36px;
        }

        .vpp-currency {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #6B7280;
            font-size: .85rem;
            pointer-events: none;
        }

        .vpp-single-grid {
            grid-template-columns: 1fr;
        }

        @media (max-width: 900px) {
            .admin-form-grid {
                grid-template-columns: 1fr;
            }

            .admin-form__actions {
                align-items: stretch;
                flex-direction: column-reverse;
            }

            .admin-form__actions .admin-button {
                width: 100%;
            }
        }
    </style>
</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

    <section class="admin-content">

        <div class="admin-toolbar">
            <div>
                <p class="admin-kicker">
                    <i class="fa-solid fa-pen-ruler"></i>
                    ${pageKicker}
                </p>

                <h2>${pageTitle}</h2>
            </div>

            <a href="${ctx}/admin/vpp"
               class="admin-button admin-button--ghost">
                <i class="fa-solid fa-arrow-left"></i>
                Back to VPP
            </a>
        </div>

        <div class="admin-form-card">

            <div class="admin-form-card__header">
                <i class="fa-solid fa-file-pen"></i>
                <h3>Product Information</h3>
            </div>

            <div class="admin-form-card__body">

                <form:form modelAttribute="itemDTO"
                           method="post"
                           action="${formAction}"
                           enctype="multipart/form-data"
                           cssClass="admin-form"
                           id="vppItemForm">

                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <form:hidden path="imagePath" />

                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>
                                Product Name
                                <span style="color:#EF4444;">*</span>
                            </label>

                            <form:input path="name"
                                        cssClass="admin-input"
                                        placeholder="Enter product name..." />

                            <form:errors path="name" cssClass="admin-error" />
                        </div>

                        <div class="admin-field">
                            <label>Supplier</label>

                            <form:select path="supplier" cssClass="admin-input">
                                <form:option value="">— Select a supplier —</form:option>

                                <c:forEach items="${activeSuppliers}" var="supplier">
                                    <form:option value="${supplier.supplierName}">
                                        <c:out value="${supplier.supplierName}" />
                                    </form:option>
                                </c:forEach>
                            </form:select>

                            <c:if test="${empty activeSuppliers}">
                                <span class="admin-hint">
                                    No active suppliers available. Please create or restore a supplier first.
                                </span>
                            </c:if>
                        </div>
                    </div>

                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>
                                Category
                                <span style="color:#EF4444;">*</span>
                            </label>

                            <form:select path="categoryName" cssClass="admin-input">
                                <form:option value="">— Select a category —</form:option>

                                <c:forEach items="${categories}" var="category">
                                    <form:option value="${category.name}">
                                        <c:out value="${category.name}" />
                                    </form:option>
                                </c:forEach>
                            </form:select>

                            <form:errors path="categoryName" cssClass="admin-error" />
                        </div>

                        <div class="admin-field">
                            <label>Status</label>

                            <form:select path="status" cssClass="admin-input">
                                <form:option value="ACTIVE">Active</form:option>
                                <form:option value="INACTIVE">Hidden</form:option>
                            </form:select>
                        </div>
                    </div>

                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>
                                Price
                                <span style="color:#EF4444;">*</span>
                            </label>

                            <div class="vpp-price-wrap">
                                <form:input path="price"
                                            type="text"
                                            id="priceDisplay"
                                            inputmode="numeric"
                                            pattern="[0-9.]*"
                                            cssClass="admin-input"
                                            placeholder="0" />

                                <span class="vpp-currency">₫</span>
                            </div>

                            <form:errors path="price" cssClass="admin-error" />
                        </div>

                        <div class="admin-field">
                            <label>Stock Quantity</label>

                            <div class="admin-qty-stepper">
                                <%-- readonly, not disabled: stockQuantity is @NotNull on the DTO,
                                     so a disabled (unsubmitted) field would fail validation on save. --%>
                                <form:input path="stockQuantity"
                                            id="stockQuantityInput"
                                            type="number"
                                            min="0"
                                            step="1"
                                            cssClass="admin-qty-input"
                                            readonly="${formMode == 'edit'}"
                                            style="${formMode == 'edit' ? 'background:#F3F4F6;color:#6B7280;cursor:not-allowed;' : ''}" />
                            </div>
                            <c:choose>
                                <c:when test="${formMode == 'edit'}">
                                    <span class="admin-hint">                                        
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="admin-hint">
                                    </span>
                                </c:otherwise>
                            </c:choose>

                            <form:errors path="stockQuantity" cssClass="admin-error" />
                        </div>
                    </div>

                    <div class="admin-form-grid vpp-single-grid">
                        <div class="admin-field">
                            <label>Product Image</label>

                            <input type="file"
                                   name="imageFile"
                                   id="imageFile"
                                   accept="image/*"
                                   class="vpp-form-file" />

                            <span class="admin-hint">
                                Accept: JPG, JPEG, PNG, WEBP, GIF, JFIF, AVIF, BMP.
                            </span>

                            <div class="vpp-current-image">
                                <c:choose>
                                    <c:when test="${not empty itemDTO.imagePath}">
                                        <img id="imagePreview"
                                             src="${ctx}${itemDTO.imagePath}?v=${itemDTO.updatedAtFormatted}"
                                             alt="Current product image" />
                                    </c:when>

                                    <c:otherwise>
                                        <img id="imagePreview"
                                             src=""
                                             alt="Product image preview"
                                             style="display:none;" />

                                        <div id="imageEmptyState" class="vpp-current-image-empty">
                                            <i class="fa-regular fa-image"></i>
                                            No image selected
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <div class="admin-form-grid vpp-single-grid">
                        <div class="admin-field">
                            <label>Description</label>

                            <form:textarea path="description"
                                           cssClass="admin-input admin-textarea"
                                           placeholder="Enter product description..." />

                            <form:errors path="description" cssClass="admin-error" />
                        </div>
                    </div>

                    <form:errors cssClass="admin-error" element="div" />

                    <div class="admin-form__actions">

                        <a href="${ctx}/admin/vpp"
                           class="admin-button admin-button--ghost">
                            <i class="fa-solid fa-xmark"></i>
                            Cancel
                        </a>

                        <button type="submit" class="admin-button">
                            <i class="fa-solid fa-floppy-disk"></i>
                            ${submitLabel}
                        </button>

                    </div>

                </form:form>

            </div>

        </div>

    </section>

</main>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const priceInput = document.getElementById("priceDisplay");

        function normalizePrice(value) {
            let raw = String(value || "").trim();

            if (!raw) {
                return "";
            }

            if (/^\d+\.\d{1,2}$/.test(raw)) {
                return String(Math.round(parseFloat(raw)));
            }

            return raw.replace(/[^\d]/g, "");
        }

        function formatPrice(value) {
            const plainNumber = normalizePrice(value);

            if (!plainNumber) {
                return "";
            }

            return Number(plainNumber).toLocaleString("de-DE");
        }

        if (priceInput) {
            if (priceInput.value) {
                priceInput.value = formatPrice(priceInput.value);
            }

            priceInput.addEventListener("focus", function () {
                this.value = normalizePrice(this.value);
            });

            priceInput.addEventListener("input", function () {
                this.value = this.value.replace(/[^\d]/g, "");
            });

            priceInput.addEventListener("blur", function () {
                this.value = formatPrice(this.value);
            });
        }

        const form = document.getElementById("vppItemForm");

        if (form) {
            form.addEventListener("submit", function () {
                if (priceInput) {
                    priceInput.value = normalizePrice(priceInput.value);
                }
            });
        }

        const stockInput = document.getElementById("stockQuantityInput");
        const decButton = document.getElementById("stockDecBtn");
        const incButton = document.getElementById("stockIncBtn");

        function getStockValue() {
            let value = parseInt(stockInput.value || "0", 10);

            if (Number.isNaN(value) || value < 0) {
                value = 0;
            }

            return value;
        }

        if (stockInput && decButton && incButton) {
            decButton.addEventListener("click", function () {
                let value = getStockValue();
                value = Math.max(0, value - 1);
                stockInput.value = value;
            });

            incButton.addEventListener("click", function () {
                let value = getStockValue();
                stockInput.value = value + 1;
            });
        }

        const imageFile = document.getElementById("imageFile");
        const imagePreview = document.getElementById("imagePreview");
        const imageEmptyState = document.getElementById("imageEmptyState");

        if (imageFile && imagePreview) {
            imageFile.addEventListener("change", function () {
                const file = this.files && this.files[0];

                if (!file) {
                    return;
                }

                if (!file.type || !file.type.startsWith("image/")) {
                    alert("Please select a valid image file.");
                    this.value = "";
                    return;
                }

                const previewUrl = URL.createObjectURL(file);

                imagePreview.src = previewUrl;
                imagePreview.style.display = "block";

                if (imageEmptyState) {
                    imageEmptyState.style.display = "none";
                }
            });
        }
    });
</script>

</body>

</html>