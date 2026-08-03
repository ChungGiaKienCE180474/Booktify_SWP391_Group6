<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx"
       value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0" />

    <title>Stock Management — Booktify Admin</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <link rel="stylesheet"
          href="${ctx}/css/admin-dashboard.css?v=6" />

    <link rel="stylesheet"
          href="${ctx}/css/stock-management.css?v=5" />
</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

    <section class="admin-content">

        <%-- =====================================================
             PAGE HEADER
             ===================================================== --%>

        <div class="admin-toolbar stock-page-header">

            <div>

                <p class="admin-kicker">

                    <i class="fa-solid fa-boxes-stacked"></i>

                    Inventory Control

                </p>

                <h2>Stock Management</h2>

                <p>
                    Increase or decrease the available stock quantity.
                </p>

            </div>

        </div>

        <%-- =====================================================
             SUCCESS MESSAGE
             ===================================================== --%>

        <c:if test="${not empty successMessage}">

            <div class="admin-alert admin-alert--success">

                <i class="fa-solid fa-circle-check"></i>

                <c:out value="${successMessage}" />

            </div>

        </c:if>

        <%-- =====================================================
             ERROR MESSAGE
             ===================================================== --%>

        <c:if test="${not empty errorMessage}">

            <div class="admin-alert admin-alert--error">

                <i class="fa-solid fa-circle-exclamation"></i>

                <c:out value="${errorMessage}" />

            </div>

        </c:if>

        <%-- =====================================================
             SUMMARY CARDS
             ===================================================== --%>

        <div class="stock-stat-grid">

            <article class="stock-stat-card">

                <div class="stock-stat-icon stock-stat-icon--products">

                    <i class="fa-solid fa-cube"></i>

                </div>

                <div class="stock-stat-copy">

                    <span>Total Products</span>

                    <strong>
                        ${summary != null
                            ? summary.totalProducts
                            : 0}
                    </strong>

                </div>

            </article>

            <article class="stock-stat-card">

                <div class="stock-stat-icon stock-stat-icon--units">

                    <i class="fa-solid fa-boxes-stacked"></i>

                </div>

                <div class="stock-stat-copy">

                    <span>Total Stock Units</span>

                    <strong>
                        ${summary != null
                            ? summary.totalStockUnits
                            : 0}
                    </strong>

                </div>

            </article>

            <article class="stock-stat-card">

                <div class="stock-stat-icon stock-stat-icon--low">

                    <i class="fa-solid fa-triangle-exclamation"></i>

                </div>

                <div class="stock-stat-copy">

                    <span>Low Stock</span>

                    <strong>
                        ${summary != null
                            ? summary.lowStockProducts
                            : 0}
                    </strong>

                </div>

            </article>

            <article class="stock-stat-card">

                <div class="stock-stat-icon stock-stat-icon--out">

                    <i class="fa-solid fa-circle-minus"></i>

                </div>

                <div class="stock-stat-copy">

                    <span>Out of Stock</span>

                    <strong>
                        ${summary != null
                            ? summary.outOfStockProducts
                            : 0}
                    </strong>

                </div>

            </article>

        </div>

        <%-- =====================================================
             COMPACT FILTER BAR
             ===================================================== --%>

        <div class="admin-panel stock-filter-panel">

            <form method="get"
                  action="${ctx}/admin/stock"
                  class="stock-filter-form">

                <input type="hidden"
                       name="page"
                       value="0" />

                <%-- PRODUCT TYPE --%>

                <div class="admin-field stock-filter-field">

                    <label for="productType">
                        Product Type
                    </label>

                    <select id="productType"
                            name="productType"
                            class="admin-input">

                        <option value="ALL"
                            ${selectedProductType == 'ALL'
                                ? 'selected'
                                : ''}>

                            All Products

                        </option>

                        <option value="BOOK"
                            ${selectedProductType == 'BOOK'
                                ? 'selected'
                                : ''}>

                            Books

                        </option>

                        <option value="VPP"
                            ${selectedProductType == 'VPP'
                                ? 'selected'
                                : ''}>

                            Stationery

                        </option>

                    </select>

                </div>

                <%-- STOCK STATUS --%>

                <div class="admin-field stock-filter-field">

                    <label for="stockStatus">
                        Stock Status
                    </label>

                    <select id="stockStatus"
                            name="stockStatus"
                            class="admin-input">

                        <option value="ALL"
                            ${selectedStockStatus == 'ALL'
                                ? 'selected'
                                : ''}>

                            All Statuses

                        </option>

                        <option value="IN_STOCK"
                            ${selectedStockStatus == 'IN_STOCK'
                                ? 'selected'
                                : ''}>

                            In Stock

                        </option>

                        <option value="LOW_STOCK"
                            ${selectedStockStatus == 'LOW_STOCK'
                                ? 'selected'
                                : ''}>

                            Low Stock

                        </option>

                        <option value="OUT_OF_STOCK"
                            ${selectedStockStatus == 'OUT_OF_STOCK'
                                ? 'selected'
                                : ''}>

                            Out of Stock

                        </option>

                    </select>

                </div>

                <%-- SUPPLIER --%>

                <div class="admin-field stock-filter-field">

                    <label for="supplier">
                        Supplier
                    </label>

                    <select id="supplier"
                            name="supplier"
                            class="admin-input">

                        <option value="ALL"
                            ${selectedSupplier == 'ALL'
                                ? 'selected'
                                : ''}>

                            All Suppliers

                        </option>

                        <c:forEach items="${supplierOptions}"
                                   var="supplierOption">

                            <option
                                value="<c:out value='${supplierOption}' />"
                                ${selectedSupplier == supplierOption
                                    ? 'selected'
                                    : ''}>

                                <c:out value="${supplierOption}" />

                            </option>

                        </c:forEach>

                    </select>

                </div>

                <%-- CATEGORY --%>

                <div class="admin-field stock-filter-field">

                    <label for="category">
                        Category
                    </label>

                    <select id="category"
                            name="category"
                            class="admin-input">

                        <option value="ALL"
                            ${selectedCategory == 'ALL'
                                ? 'selected'
                                : ''}>

                            All Categories

                        </option>

                        <c:forEach items="${categoryOptions}"
                                   var="categoryOption">

                            <option
                                value="<c:out value='${categoryOption}' />"
                                ${selectedCategory == categoryOption
                                    ? 'selected'
                                    : ''}>

                                <c:out value="${categoryOption}" />

                            </option>

                        </c:forEach>

                    </select>

                </div>

                <%-- FILTER ACTIONS --%>

                <div class="stock-filter-actions">

                    <button type="submit"
                            class="admin-button">

                        <i class="fa-solid fa-filter"></i>

                        Filter

                    </button>

                    <a href="${ctx}/admin/stock"
                       class="admin-button admin-button--ghost">

                        <i class="fa-solid fa-rotate-right"></i>

                        Reset

                    </a>

                </div>

            </form>

        </div>

        <%-- =====================================================
             INVENTORY TABLE
             ===================================================== --%>

        <div class="admin-panel stock-inventory-panel">

            <div class="stock-panel-title">

                <h3>

                    <i class="fa-solid fa-warehouse"></i>

                    Inventory List

                </h3>

                
            </div>

            <div class="admin-table-wrap stock-table-wrap">

                <table class="admin-table">

                    <thead>

                    <tr>

                        <th style="width: 48px;">
                            #
                        </th>

                        <th>
                            Product
                        </th>

                        <th>
                            Type
                        </th>

                        <th>
                            Category
                        </th>

                        <th>
                            Supplier
                        </th>

                        <th>
                            Current Stock
                        </th>

                        <th>
                            Stock Status
                        </th>

                        

                        <th style="min-width: 110px;">
                            Actions
                        </th>

                    </tr>

                    </thead>

                    <tbody>

                    <c:choose>

                        <c:when test="${empty items}">

                            <tr>

                                <td colspan="9"
                                    class="stock-empty-state">

                                    <i class="fa-solid fa-box-open"></i>

                                    No inventory products found.

                                </td>

                            </tr>

                        </c:when>

                        <c:otherwise>

                            <c:forEach items="${items}"
                                       var="item"
                                       varStatus="status">

                                <tr>

                                    <td class="stock-row-number">

                                        ${fromItem + status.index}

                                    </td>

                                    <td>

                                        <div class="stock-product-cell">

                                            <c:choose>

                                                <c:when test="${not empty item.imageUrl}">

                                                    <img
                                                        class="stock-product-image"
                                                        src="<c:out value='${item.imageUrl}' />"
                                                        alt="Product image"
                                                        onerror="
                                                            this.style.display='none';
                                                            this.nextElementSibling.style.display='grid';
                                                        " />

                                                    <span
                                                        class="stock-product-placeholder"
                                                        style="display: none;">

                                                        <i class="fa-solid fa-box"></i>

                                                    </span>

                                                </c:when>

                                                <c:otherwise>

                                                    <span class="stock-product-placeholder">

                                                        <i class="fa-solid fa-box"></i>

                                                    </span>

                                                </c:otherwise>

                                            </c:choose>

                                            <div>

                                                <div class="stock-product-name">

                                                    <c:out value="${item.productName}" />

                                                </div>

                                                <div class="stock-product-code">

                                                    <c:out value="${item.code}" />

                                                </div>

                                            </div>

                                        </div>

                                    </td>

                                    <td>

                                        <span class="stock-type-badge">

                                            <c:out value="${item.productTypeLabel}" />

                                        </span>

                                    </td>

                                    <td>

                                        <c:choose>

                                            <c:when test="${not empty item.categoryName}">

                                                <c:out value="${item.categoryName}" />

                                            </c:when>

                                            <c:otherwise>
                                                —
                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <td>

                                        <c:choose>

                                            <c:when test="${not empty item.supplierName}">

                                                <c:out value="${item.supplierName}" />

                                            </c:when>

                                            <c:otherwise>
                                                —
                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <td>

                                        <span class="stock-quantity">

                                            ${item.currentStock}

                                        </span>

                                    </td>

                                    <td>

                                        <span class="
                                            stock-status
                                            stock-status--${item.stockStatusCssClass}">

                                            <c:choose>

                                                <c:when test="${item.stockStatus == 'IN_STOCK'}">

                                                    <i class="fa-solid fa-circle-check"></i>

                                                </c:when>

                                                <c:when test="${item.stockStatus == 'LOW_STOCK'}">

                                                    <i class="fa-solid fa-triangle-exclamation"></i>

                                                </c:when>

                                                <c:otherwise>

                                                    <i class="fa-solid fa-circle-xmark"></i>

                                                </c:otherwise>

                                            </c:choose>

                                            <c:out value="${item.stockStatusLabel}" />

                                        </span>

                                    </td>

                                    

                                    <td>

                                        <div class="stock-actions">

                                            <%-- INCREASE STOCK:
                                                 ARROW POINTS UP --%>

                                            <button
                                                type="button"
                                                class="
                                                    icon-link
                                                    icon-link--stock-in
                                                    js-stock-action"
                                                title="Increase Stock"
                                                aria-label="Increase Stock"
                                                data-mode="in"
                                                data-product-id="${item.productId}"
                                                data-product-type="${item.productType}"
                                                data-product-name="<c:out value='${item.productName}' />"
                                                data-current-stock="${item.currentStock}">

                                                <i class="fa-solid fa-arrow-up"></i>

                                            </button>

                                            <%-- DECREASE STOCK:
                                                 ARROW POINTS DOWN --%>

                                            <button
                                                type="button"
                                                class="
                                                    icon-link
                                                    icon-link--stock-out
                                                    js-stock-action"
                                                title="Decrease Stock"
                                                aria-label="Decrease Stock"
                                                data-mode="out"
                                                data-product-id="${item.productId}"
                                                data-product-type="${item.productType}"
                                                data-product-name="<c:out value='${item.productName}' />"
                                                data-current-stock="${item.currentStock}"
                                                ${item.currentStock <= 0
                                                    ? 'disabled'
                                                    : ''}>

                                                <i class="fa-solid fa-arrow-down"></i>

                                            </button>

                                        </div>

                                    </td>

                                </tr>

                            </c:forEach>

                        </c:otherwise>

                    </c:choose>

                    </tbody>

                </table>

            </div>

            <%-- =================================================
                 PAGINATION
                 ================================================= --%>

            <div class="admin-pagination stock-pagination">

                <div class="admin-pagination__info">

                    <c:choose>

                        <c:when test="${totalItems == 0}">

                            No entries found.

                        </c:when>

                        <c:otherwise>

                            Showing

                            <strong>${fromItem}</strong>

                            to

                            <strong>${toItem}</strong>

                            of

                            <strong>${totalItems}</strong>

                            products

                        </c:otherwise>

                    </c:choose>

                </div>

                <c:if test="${totalPages > 1}">

                    <div class="admin-pagination__nav">

                        <%-- PREVIOUS PAGE --%>

                        <c:if test="${currentPage > 0}">

                            <c:url var="previousPageUrl"
                                   value="/admin/stock">

                                <c:param
                                    name="page"
                                    value="${currentPage - 1}" />

                                <c:param
                                    name="productType"
                                    value="${selectedProductType}" />

                                <c:param
                                    name="stockStatus"
                                    value="${selectedStockStatus}" />

                                <c:param
                                    name="supplier"
                                    value="${selectedSupplier}" />

                                <c:param
                                    name="category"
                                    value="${selectedCategory}" />

                            </c:url>

                            <a href="${previousPageUrl}"
                               class="pag-btn"
                               aria-label="Previous page">

                                <i class="fa-solid fa-chevron-left"></i>

                            </a>

                        </c:if>

                        <%-- PAGE NUMBERS --%>

                        <c:forEach begin="0"
                                   end="${totalPages - 1}"
                                   var="pageIndex">

                            <c:url var="pageUrl"
                                   value="/admin/stock">

                                <c:param
                                    name="page"
                                    value="${pageIndex}" />

                                <c:param
                                    name="productType"
                                    value="${selectedProductType}" />

                                <c:param
                                    name="stockStatus"
                                    value="${selectedStockStatus}" />

                                <c:param
                                    name="supplier"
                                    value="${selectedSupplier}" />

                                <c:param
                                    name="category"
                                    value="${selectedCategory}" />

                            </c:url>

                            <a href="${pageUrl}"
                               class="
                                   pag-btn
                                   ${pageIndex == currentPage
                                       ? 'pag-btn--active'
                                       : ''}">

                                ${pageIndex + 1}

                            </a>

                        </c:forEach>

                        <%-- NEXT PAGE --%>

                        <c:if test="${currentPage + 1 < totalPages}">

                            <c:url var="nextPageUrl"
                                   value="/admin/stock">

                                <c:param
                                    name="page"
                                    value="${currentPage + 1}" />

                                <c:param
                                    name="productType"
                                    value="${selectedProductType}" />

                                <c:param
                                    name="stockStatus"
                                    value="${selectedStockStatus}" />

                                <c:param
                                    name="supplier"
                                    value="${selectedSupplier}" />

                                <c:param
                                    name="category"
                                    value="${selectedCategory}" />

                            </c:url>

                            <a href="${nextPageUrl}"
                               class="pag-btn"
                               aria-label="Next page">

                                <i class="fa-solid fa-chevron-right"></i>

                            </a>

                        </c:if>

                    </div>

                </c:if>

            </div>

        </div>

    </section>

</main>

<%-- =============================================================
     INCREASE / DECREASE STOCK MODAL
     ============================================================= --%>

<div id="stockModalBackdrop"
     class="stock-modal-backdrop"
     aria-hidden="true">

    <div class="stock-modal"
         role="dialog"
         aria-modal="true"
         aria-labelledby="stockModalTitle">

        <div class="stock-modal-header">

            <h3 id="stockModalTitle">
                Stock Action
            </h3>

            <button type="button"
                    class="stock-modal-close"
                    id="stockModalClose"
                    aria-label="Close">

                <i class="fa-solid fa-xmark"></i>

            </button>

        </div>

        <form id="stockActionForm"
              method="post">

            <%-- CSRF TOKEN --%>

            <c:if test="${not empty _csrf}">

                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}" />

            </c:if>

            <input type="hidden"
                   name="productType"
                   id="modalProductType" />

            <input type="hidden"
                   name="productId"
                   id="modalProductId" />

            <div class="stock-modal-body">

                <div class="admin-field stock-field-full">

                    <label for="modalProductName">
                        Product
                    </label>

                    <input id="modalProductName"
                           class="admin-input stock-readonly"
                           type="text"
                           readonly />

                </div>

                <div class="admin-field">

                    <label for="modalCurrentStock">
                        Current Stock
                    </label>

                    <input id="modalCurrentStock"
                           class="admin-input stock-readonly"
                           type="number"
                           readonly />

                </div>

                <div class="admin-field"
                     id="quantityField">

                    <label for="modalQuantity">
                        Quantity
                    </label>

                    <input id="modalQuantity"
                           class="admin-input"
                           type="number"
                           name="quantity"
                           min="1"
                           step="1"
                           required />

                </div>

                <%-- SHOWN ONLY WHEN INCREASING STOCK --%>

                <div class="admin-field stock-field-full"
                     id="referenceField"
                     style="display: none;">

                    <label for="modalReferenceCode">
                        Reference Number
                    </label>

                    <input id="modalReferenceCode"
                           class="admin-input"
                           type="text"
                           name="referenceCode"
                           maxlength="100"
                           placeholder="Example: GRN-001" />

                </div>

                <%-- SHOWN ONLY WHEN DECREASING STOCK --%>

                <div class="admin-field stock-field-full"
                     id="reasonField"
                     style="display: none;">

                    <label for="modalReason">
                        Reason
                    </label>

                    <select id="modalReason"
                            class="admin-input"
                            name="reason">

                        <option value="">
                            Select a reason
                        </option>

                        <option value="Damaged items">
                            Damaged Items
                        </option>

                        <option value="Lost items">
                            Lost Items
                        </option>

                        <option value="Returned to supplier">
                            Returned to Supplier
                        </option>

                        <option value="Internal use">
                            Internal Use
                        </option>

                        <option value="Other">
                            Other
                        </option>

                    </select>

                </div>

                <div class="admin-field stock-field-full">

                    <label for="modalNote">
                        Note
                    </label>

                    <textarea id="modalNote"
                              class="admin-input"
                              name="note"
                              rows="4"
                              maxlength="2000"
                              placeholder="Optional stock note"></textarea>

                </div>

            </div>

            <div class="stock-modal-footer">

                <button type="button"
                        class="admin-button admin-button--ghost"
                        id="stockModalCancel">

                    Cancel

                </button>

                <button type="submit"
                        class="admin-button"
                        id="stockModalSubmit">

                    Save

                </button>

            </div>

        </form>

    </div>

</div>

<script>
(function () {

    const contextPath =
        '${ctx}';

    const backdrop =
        document.getElementById('stockModalBackdrop');

    const form =
        document.getElementById('stockActionForm');

    const modalTitle =
        document.getElementById('stockModalTitle');

    const submitButton =
        document.getElementById('stockModalSubmit');

    const productType =
        document.getElementById('modalProductType');

    const productId =
        document.getElementById('modalProductId');

    const productName =
        document.getElementById('modalProductName');

    const currentStock =
        document.getElementById('modalCurrentStock');

    const quantity =
        document.getElementById('modalQuantity');

    const referenceField =
        document.getElementById('referenceField');

    const referenceCode =
        document.getElementById('modalReferenceCode');

    const reasonField =
        document.getElementById('reasonField');

    const reason =
        document.getElementById('modalReason');

    const note =
        document.getElementById('modalNote');

    function resetFields() {

        quantity.value = '';
        quantity.removeAttribute('max');

        referenceCode.value = '';
        reason.value = '';
        note.value = '';

        reason.required = false;

        referenceField.style.display = 'none';
        reasonField.style.display = 'none';

    }

    function openModal(button) {

        resetFields();

        const mode =
            button.dataset.mode;

        const existingStock =
            Number(button.dataset.currentStock || 0);

        productType.value =
            button.dataset.productType;

        productId.value =
            button.dataset.productId;

        productName.value =
            button.dataset.productName;

        currentStock.value =
            existingStock;

        if (mode === 'in') {

            modalTitle.textContent =
                'Increase Stock';

            submitButton.innerHTML =
                '<i class="fa-solid fa-arrow-up"></i> Increase Stock';

            form.action =
                contextPath + '/admin/stock/in';

            referenceField.style.display =
                '';

        } else if (mode === 'out') {

            modalTitle.textContent =
                'Decrease Stock';

            submitButton.innerHTML =
                '<i class="fa-solid fa-arrow-down"></i> Decrease Stock';

            form.action =
                contextPath + '/admin/stock/out';

            reasonField.style.display =
                '';

            reason.required =
                true;

            quantity.max =
                existingStock;

        } else {

            return;

        }

        backdrop.classList.add('open');

        backdrop.setAttribute(
            'aria-hidden',
            'false'
        );

        quantity.focus();

    }

    function closeModal() {

        backdrop.classList.remove('open');

        backdrop.setAttribute(
            'aria-hidden',
            'true'
        );

    }

    document
        .querySelectorAll('.js-stock-action')
        .forEach(function (button) {

            button.addEventListener(
                'click',
                function () {

                    if (!button.disabled) {
                        openModal(button);
                    }

                }
            );

        });

    document
        .getElementById('stockModalClose')
        .addEventListener(
            'click',
            closeModal
        );

    document
        .getElementById('stockModalCancel')
        .addEventListener(
            'click',
            closeModal
        );

    backdrop.addEventListener(
        'click',
        function (event) {

            if (event.target === backdrop) {
                closeModal();
            }

        }
    );

    document.addEventListener(
        'keydown',
        function (event) {

            if (event.key === 'Escape'
                    && backdrop.classList.contains('open')) {

                closeModal();

            }

        }
    );

})();
</script>

</body>
</html>