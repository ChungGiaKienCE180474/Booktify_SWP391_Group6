<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <title>
        ${formMode == 'create' ? 'Create Promotion' : 'Edit Promotion'}
    </title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"/>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin-dashboard.css"/>

    <style>
        .promotion-form-card {
            max-width: 950px;
            margin: 0 auto;
            overflow: hidden;
            border-radius: 18px;
            background: #ffffff;
            box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
        }

        .promotion-form-header {
            padding: 24px 28px;
            border-bottom: 1px solid #e5e7eb;
        }

        .promotion-form-header h3 {
            margin: 0;
            color: #172033;
            font-size: 24px;
            font-weight: 800;
        }

        .promotion-form-header p {
            margin: 7px 0 0;
            color: #7c8799;
            font-size: 14px;
        }

        .promotion-form-body {
            padding: 28px;
        }

        .promotion-form-group {
            margin-bottom: 20px;
        }

        .promotion-form-row {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 18px;
            margin-bottom: 20px;
        }

        .promotion-label {
            display: block;
            margin-bottom: 8px;
            color: #344054;
            font-size: 14px;
            font-weight: 700;
        }

        .required {
            color: #dc2626;
        }

        .promotion-input,
        .promotion-select,
        .promotion-textarea {
            width: 100%;
            box-sizing: border-box;
            padding: 12px 14px;
            border: 1px solid #d0d5dd;
            border-radius: 10px;
            background: #ffffff;
            color: #1d2939;
            font-size: 14px;
            outline: none;
            transition: border-color 0.2s ease,
                        box-shadow 0.2s ease;
        }

        .promotion-input:focus,
        .promotion-select:focus,
        .promotion-textarea:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }

        .promotion-textarea {
            min-height: 105px;
            resize: vertical;
        }

        .promotion-error {
            display: block;
            margin-top: 6px;
            color: #dc2626;
            font-size: 12px;
            font-weight: 600;
        }

        .global-error-box {
            margin-bottom: 20px;
            padding: 12px 14px;
            border: 1px solid #fecaca;
            border-radius: 10px;
            background: #fef2f2;
            color: #b91c1c;
            font-size: 13px;
            font-weight: 600;
        }

        .category-selection-section {
            margin-top: 8px;
            margin-bottom: 22px;
            padding: 18px;
            border: 1px solid #dfe5ee;
            border-radius: 14px;
            background: #f8fafc;
        }

        .category-selection-header {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            gap: 16px;
            margin-bottom: 14px;
        }

        .category-selection-title {
            margin: 0;
            color: #344054;
            font-size: 15px;
            font-weight: 800;
        }

        .category-selection-description {
            margin: 5px 0 0;
            color: #7c8799;
            font-size: 13px;
        }

        .category-selection-actions {
            display: flex;
            gap: 8px;
        }

        .category-action-button {
            padding: 8px 12px;
            border: 1px solid #cfd6e2;
            border-radius: 8px;
            background: #ffffff;
            color: #344054;
            font-size: 12px;
            font-weight: 700;
            cursor: pointer;
            transition: 0.2s ease;
        }

        .category-action-button:hover {
            border-color: #2563eb;
            color: #2563eb;
        }

        .category-search-wrapper {
            position: relative;
            margin-bottom: 12px;
        }

        .category-search-wrapper i {
            position: absolute;
            top: 50%;
            left: 13px;
            color: #98a2b3;
            transform: translateY(-50%);
        }

        .category-search-input {
            width: 100%;
            box-sizing: border-box;
            padding: 11px 14px 11px 39px;
            border: 1px solid #d0d5dd;
            border-radius: 10px;
            background: #ffffff;
            color: #1d2939;
            font-size: 14px;
            outline: none;
        }

        .category-search-input:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }

        .category-selection-list {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 10px;
            max-height: 340px;
            padding: 10px;
            overflow-y: auto;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            background: #ffffff;
        }

        .category-selection-item {
            display: flex;
            align-items: center;
            gap: 12px;
            min-width: 0;
            padding: 14px;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            background: #ffffff;
            cursor: pointer;
            transition: border-color 0.15s ease,
                        background 0.15s ease,
                        box-shadow 0.15s ease;
        }

        .category-selection-item:hover {
            border-color: #93c5fd;
            background: #f8fbff;
        }

        .category-selection-item.selected {
            border-color: #2563eb;
            background: #eff6ff;
            box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.1);
        }

        .category-checkbox {
            position: absolute;
            opacity: 0;
            pointer-events: none;
        }

        .category-checkbox-custom {
            display: flex;
            flex: 0 0 20px;
            align-items: center;
            justify-content: center;
            width: 20px;
            height: 20px;
            border: 2px solid #c8d0dc;
            border-radius: 5px;
            background: #ffffff;
            color: transparent;
            transition: 0.15s ease;
        }

        .category-checkbox:checked + .category-checkbox-custom {
            border-color: #2563eb;
            background: #2563eb;
            color: #ffffff;
        }

        .category-checkbox-custom i {
            font-size: 11px;
        }

        .category-icon {
            display: flex;
            flex: 0 0 42px;
            align-items: center;
            justify-content: center;
            width: 42px;
            height: 42px;
            border-radius: 10px;
            background: #eaf2ff;
            color: #2563eb;
            font-size: 17px;
        }

        .category-information {
            display: flex;
            min-width: 0;
            flex: 1;
            flex-direction: column;
        }

        .category-name {
            overflow: hidden;
            color: #1d2939;
            font-size: 14px;
            font-weight: 800;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .category-meta {
            margin-top: 4px;
            color: #7b8799;
            font-size: 12px;
        }

        .category-empty-state {
            grid-column: 1 / -1;
            padding: 35px 20px;
            color: #98a2b3;
            text-align: center;
        }

        .category-empty-state i {
            margin-bottom: 10px;
            font-size: 28px;
        }

        .category-empty-state p {
            margin: 0;
            font-size: 14px;
        }

        .category-selection-summary {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-top: 12px;
            color: #475467;
            font-size: 13px;
        }

        .category-selection-summary i {
            color: #16a34a;
        }

        .promotion-active-box {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 14px;
            border: 1px solid #dfe5ee;
            border-radius: 10px;
            background: #f8fafc;
        }

        .promotion-active-box input {
            width: 18px;
            height: 18px;
            accent-color: #2563eb;
            cursor: pointer;
        }

        .promotion-active-box label {
            margin: 0;
            color: #344054;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
        }

        .promotion-form-footer {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 24px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
        }

        .promotion-button {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            min-width: 140px;
            padding: 12px 18px;
            border: none;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 800;
            text-decoration: none;
            cursor: pointer;
            transition: transform 0.15s ease,
                        box-shadow 0.15s ease;
        }

        .promotion-button:hover {
            transform: translateY(-1px);
        }

        .promotion-button-cancel {
            border: 1px solid #d0d5dd;
            background: #ffffff;
            color: #344054;
        }

        .promotion-button-save {
            background: #2563eb;
            color: #ffffff;
            box-shadow: 0 8px 18px rgba(37, 99, 235, 0.22);
        }

        @media (max-width: 768px) {
            .promotion-form-row {
                grid-template-columns: 1fr;
            }

            .category-selection-header {
                flex-direction: column;
            }

            .category-selection-actions {
                width: 100%;
            }

            .category-action-button {
                flex: 1;
            }

            .category-selection-list {
                grid-template-columns: 1fr;
            }

            .promotion-form-footer {
                flex-direction: column-reverse;
            }

            .promotion-button {
                width: 100%;
            }
        }
    </style>
</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp"/>

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp"/>

    <section class="admin-content">

        <div class="admin-hero">
            <div>
                <p class="admin-kicker">MARKETING</p>

                <h2>
                    ${formMode == 'create'
                            ? 'Create New Promotion'
                            : 'Edit Promotion'}
                </h2>
            </div>

            <a href="${pageContext.request.contextPath}/admin/promotions"
               class="admin-button">

                <i class="fa-solid fa-arrow-left"></i>
                Back to List
            </a>
        </div>

        <div class="promotion-form-card">

            <div class="promotion-form-header">

                <h3>
                    ${formMode == 'create'
                            ? 'Create New Promotion'
                            : 'Update Promotion'}
                </h3>

                <p>
                    ${formMode == 'create'
                            ? 'Fill in the promotion information and select the categories to apply it to.'
                            : 'Update the promotion information and applicable categories.'}
                </p>

            </div>

            <div class="promotion-form-body">

                <c:choose>
                    <c:when test="${formMode == 'create'}">
                        <c:set var="promotionFormAction"
                               value="${pageContext.request.contextPath}/admin/promotions"/>
                    </c:when>

                    <c:otherwise>
                        <c:set var="promotionFormAction"
                               value="${pageContext.request.contextPath}/admin/promotions/${promotion.id}"/>
                    </c:otherwise>
                </c:choose>

                <form:form
                        modelAttribute="promotion"
                        method="post"
                        action="${promotionFormAction}">

                    <form:hidden path="id"/>

                    <form:errors
                            path="*"
                            element="div"
                            cssClass="global-error-box"/>

                    <c:if test="${not empty errorMessage}">
                        <div class="global-error-box">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            <c:out value="${errorMessage}"/>
                        </div>
                    </c:if>

                    <div class="promotion-form-group">

                        <label class="promotion-label"
                               for="promotionName">

                            Promotion Name
                            <span class="required">*</span>
                        </label>

                        <form:input
                                id="promotionName"
                                path="name"
                                cssClass="promotion-input"
                                placeholder="Example: Summer Sale 2026"/>

                        <form:errors
                                path="name"
                                cssClass="promotion-error"/>

                    </div>

                    <div class="promotion-form-group">

                        <label class="promotion-label"
                               for="promotionDescription">

                            Description
                        </label>

                        <form:textarea
                                id="promotionDescription"
                                path="description"
                                cssClass="promotion-textarea"
                                rows="4"
                                placeholder="Enter a short description for the promotion campaign"/>

                        <form:errors
                                path="description"
                                cssClass="promotion-error"/>

                    </div>

                    <div class="promotion-form-row">

                        <div>
                            <label class="promotion-label"
                                   for="discountType">

                                Discount Type
                            </label>

                            <form:select
                                    id="discountType"
                                    path="percentage"
                                    cssClass="promotion-select">

                                <form:option value="true">
                                    Percentage (%)
                                </form:option>

                                <form:option value="false">
                                    Fixed Amount (VND)
                                </form:option>

                            </form:select>
                        </div>

                        <div>
                            <label class="promotion-label"
                                   for="discountValue">

                                Discount Value
                                <span class="required">*</span>
                            </label>

                            <form:input
                                    id="discountValue"
                                    path="discountValue"
                                    type="number"
                                    min="0"
                                    step="0.01"
                                    cssClass="promotion-input"
                                    placeholder="Example: 10"/>

                            <form:errors
                                    path="discountValue"
                                    cssClass="promotion-error"/>
                        </div>

                    </div>

                    <div class="promotion-form-row">

                        <div>
                            <label class="promotion-label"
                                   for="startDate">

                                Start Date
                                <span class="required">*</span>
                            </label>

                            <form:input
                                    id="startDate"
                                    path="startDate"
                                    type="datetime-local"
                                    cssClass="promotion-input"/>

                            <form:errors
                                    path="startDate"
                                    cssClass="promotion-error"/>
                        </div>

                        <div>
                            <label class="promotion-label"
                                   for="endDate">

                                End Date
                                <span class="required">*</span>
                            </label>

                            <form:input
                                    id="endDate"
                                    path="endDate"
                                    type="datetime-local"
                                    cssClass="promotion-input"/>

                            <form:errors
                                    path="endDate"
                                    cssClass="promotion-error"/>
                        </div>

                    </div>

                    <div class="category-selection-section">

                        <div class="category-selection-header">

                            <div>
                                <h4 class="category-selection-title">

                                    Applicable Categories
                                    <span class="required">*</span>
                                </h4>

                                <p class="category-selection-description">
                                    All active books belonging to the selected
                                    categories will receive this promotion.
                                </p>
                            </div>

                            <div class="category-selection-actions">

                                <button type="button"
                                        id="selectAllCategories"
                                        class="category-action-button">

                                    Select All
                                </button>

                                <button type="button"
                                        id="clearAllCategories"
                                        class="category-action-button">

                                    Clear All
                                </button>

                            </div>

                        </div>

                        <div class="category-search-wrapper">

                            <i class="fa-solid fa-magnifying-glass"></i>

                            <input type="text"
                                   id="categorySearch"
                                   class="category-search-input"
                                   placeholder="Search by category name"/>

                        </div>

                        <div class="category-selection-list"
                             id="categorySelectionList">

                            <c:choose>

                                <c:when test="${empty categories}">

                                    <div class="category-empty-state">

                                        <i class="fa-solid fa-layer-group"></i>

                                        <p>
                                            No active categories are available.
                                        </p>

                                    </div>

                                </c:when>

                                <c:otherwise>

                                    <c:forEach var="category"
                                               items="${categories}">

                                        <c:set var="categorySelected"
                                               value="${false}"/>

                                        <c:forEach var="selectedCategoryId"
                                                   items="${selectedCategoryIds}">

                                            <c:if test="${selectedCategoryId == category.id}">

                                                <c:set var="categorySelected"
                                                       value="${true}"/>

                                            </c:if>

                                        </c:forEach>

                                        <label class="category-selection-item ${categorySelected ? 'selected' : ''}"
                                               data-search="${category.name}">

                                            <input type="checkbox"
                                                   name="categoryIds"
                                                   value="${category.id}"
                                                   class="category-checkbox"
                                                   ${categorySelected ? 'checked="checked"' : ''}/>

                                            <span class="category-checkbox-custom">

                                                <i class="fa-solid fa-check"></i>

                                            </span>

                                            <span class="category-icon">

                                                <i class="fa-solid fa-layer-group"></i>

                                            </span>

                                            <span class="category-information">

                                                <span class="category-name">
                                                    <c:out value="${category.name}"/>
                                                </span>

                                                <span class="category-meta">
                                                    Category ID:
                                                    <c:out value="${category.id}"/>
                                                </span>

                                            </span>

                                        </label>

                                    </c:forEach>

                                </c:otherwise>

                            </c:choose>

                        </div>

                        <div class="category-selection-summary">

                            <i class="fa-solid fa-circle-check"></i>

                            <span>
                                <strong id="selectedCategoryCount">0</strong>
                                category(s) selected
                            </span>

                        </div>

                    </div>

                    <div class="promotion-active-box">

                        <form:checkbox
                                id="promotionActive"
                                path="active"/>

                        <label for="promotionActive">
                            Activate this promotion
                        </label>

                    </div>

                    <div class="promotion-form-footer">

                        <a href="${pageContext.request.contextPath}/admin/promotions"
                           class="promotion-button promotion-button-cancel">

                            Cancel
                        </a>

                        <button type="submit"
                                class="promotion-button promotion-button-save">

                            <i class="fa-solid fa-floppy-disk"></i>

                            ${formMode == 'create'
                                    ? 'Save Promotion'
                                    : 'Update Promotion'}

                        </button>

                    </div>

                </form:form>

            </div>

        </div>

    </section>

</main>

<script>
    document.addEventListener("DOMContentLoaded", function () {

        const searchInput =
            document.getElementById("categorySearch");

        const categoryItems =
            document.querySelectorAll(".category-selection-item");

        const categoryCheckboxes =
            document.querySelectorAll(".category-checkbox");

        const selectedCategoryCount =
            document.getElementById("selectedCategoryCount");

        const selectAllButton =
            document.getElementById("selectAllCategories");

        const clearAllButton =
            document.getElementById("clearAllCategories");

        function updateSelectedCount() {

            const checkedCheckboxes =
                document.querySelectorAll(
                    ".category-checkbox:checked"
                );

            if (selectedCategoryCount) {
                selectedCategoryCount.textContent =
                    checkedCheckboxes.length;
            }

            categoryItems.forEach(function (item) {

                const checkbox =
                    item.querySelector(".category-checkbox");

                if (!checkbox) {
                    return;
                }

                if (checkbox.checked) {
                    item.classList.add("selected");
                } else {
                    item.classList.remove("selected");
                }

            });
        }

        if (searchInput) {

            searchInput.addEventListener(
                "input",
                function () {

                    const keyword =
                        this.value
                            .trim()
                            .toLowerCase();

                    categoryItems.forEach(function (item) {

                        const searchValue =
                            (item.dataset.search || "")
                                .toLowerCase();

                        item.style.display =
                            searchValue.includes(keyword)
                                ? "flex"
                                : "none";

                    });
                }
            );
        }

        categoryCheckboxes.forEach(function (checkbox) {

            checkbox.addEventListener(
                "change",
                updateSelectedCount
            );

        });

        if (selectAllButton) {

            selectAllButton.addEventListener(
                "click",
                function () {

                    categoryItems.forEach(function (item) {

                        if (item.style.display !== "none") {

                            const checkbox =
                                item.querySelector(
                                    ".category-checkbox"
                                );

                            if (checkbox) {
                                checkbox.checked = true;
                            }
                        }

                    });

                    updateSelectedCount();
                }
            );
        }

        if (clearAllButton) {

            clearAllButton.addEventListener(
                "click",
                function () {

                    categoryCheckboxes.forEach(
                        function (checkbox) {

                            checkbox.checked = false;
                        }
                    );

                    updateSelectedCount();
                }
            );
        }

        updateSelectedCount();
    });
</script>

</body>
</html>