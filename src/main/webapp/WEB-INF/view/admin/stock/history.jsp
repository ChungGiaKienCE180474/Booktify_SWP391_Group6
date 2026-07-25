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

    <title>Stock History — Booktify Admin</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <link rel="stylesheet"
          href="${ctx}/css/admin-dashboard.css?v=6" />

    <link rel="stylesheet"
          href="${ctx}/css/stock-management.css?v=1" />
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

                    <i class="fa-solid fa-clock-rotate-left"></i>

                    Inventory Audit

                </p>

                <h2>Stock Transactions</h2>

                <p>
                    Track all inventory changes for books and
                    stationery products.
                </p>

            </div>

            <div class="stock-header-actions">

                <a href="${ctx}/admin/stock"
                   class="admin-button admin-button--ghost">

                    <i class="fa-solid fa-arrow-left"></i>

                    Back to Inventory

                </a>

            </div>

        </div>

        <%-- =====================================================
             SUCCESS AND ERROR MESSAGES
             ===================================================== --%>

        <c:if test="${not empty successMessage}">

            <div class="admin-alert admin-alert--success">

                <i class="fa-solid fa-circle-check"></i>

                <c:out value="${successMessage}" />

            </div>

        </c:if>

        <c:if test="${not empty errorMessage}">

            <div class="admin-alert admin-alert--error">

                <i class="fa-solid fa-circle-exclamation"></i>

                <c:out value="${errorMessage}" />

            </div>

        </c:if>

        <%-- =====================================================
             SUMMARY CARDS
             ===================================================== --%>

        <div class="stock-history-stats">

            <article class="stock-history-stat">

                <i class="fa-solid fa-arrow-right-arrow-left"></i>

                <div>

                    <span>Today's Movements</span>

                    <strong>
                        ${todayMovements}
                    </strong>

                </div>

            </article>

            <article class="stock-history-stat">

                <i class="fa-solid fa-sliders"></i>

                <div>

                    <span>Total Adjustments</span>

                    <strong>
                        ${totalAdjustments}
                    </strong>

                </div>

            </article>

            <article class="stock-history-stat">

                <i class="fa-solid fa-list-check"></i>

                <div>

                    <span>Filtered Transactions</span>

                    <strong>
                        ${totalTransactions}
                    </strong>

                </div>

            </article>

        </div>

        <%-- =====================================================
             FILTER FORM
             ===================================================== --%>

        <div class="admin-panel">

            <form method="get"
                  action="${ctx}/admin/stock/history">

                <input type="hidden"
                       name="page"
                       value="0" />

                <%--
                    productId is supplied when the administrator
                    clicks History from an inventory row.
                --%>

                <c:if test="${not empty selectedProductId}">

                    <input type="hidden"
                           name="productId"
                           value="${selectedProductId}" />

                </c:if>

                <div class="stock-filter-grid"
                     style="grid-template-columns:
                            minmax(280px, 2fr)
                            minmax(180px, 1fr)
                            minmax(190px, 1fr);">

                    <div class="admin-field stock-search-field">

                        <label for="historySearch">
                            Search
                        </label>

                        <div class="stock-search-wrapper">

                            <i class="fa-solid fa-magnifying-glass"></i>

                            <input id="historySearch"
                                   class="admin-input"
                                   type="text"
                                   name="q"
                                   value="<c:out value='${q}' />"
                                   placeholder="Search product, reference, reason, or user" />

                        </div>

                    </div>

                    <div class="admin-field">

                        <label for="historyProductType">
                            Product Type
                        </label>

                        <select id="historyProductType"
                                name="productType"
                                class="admin-input">

                            <option value="ALL"
                                ${selectedProductType == 'ALL'
                                    ? 'selected'
                                    : ''}>

                                All Products

                            </option>

                            <c:forEach items="${productTypes}"
                                       var="type">

                                <option value="${type.name()}"
                                    ${selectedProductType == type.name()
                                        ? 'selected'
                                        : ''}>

                                    <c:out value="${type.label}" />

                                </option>

                            </c:forEach>

                        </select>

                    </div>

                    <div class="admin-field">

                        <label for="historyMovementType">
                            Movement Type
                        </label>

                        <select id="historyMovementType"
                                name="movementType"
                                class="admin-input">

                            <option value="ALL"
                                ${selectedMovementType == 'ALL'
                                    ? 'selected'
                                    : ''}>

                                All Movements

                            </option>

                            <c:forEach items="${movementTypes}"
                                       var="movement">

                                <option value="${movement.name()}"
                                    ${selectedMovementType == movement.name()
                                        ? 'selected'
                                        : ''}>

                                    <c:out value="${movement.label}" />

                                </option>

                            </c:forEach>

                        </select>

                    </div>

                </div>

                <div class="stock-filter-actions">

                    <button type="submit"
                            class="admin-button">

                        <i class="fa-solid fa-filter"></i>

                        Filter

                    </button>

                    <a href="${ctx}/admin/stock/history"
                       class="admin-button admin-button--ghost">

                        <i class="fa-solid fa-rotate-right"></i>

                        Reset

                    </a>

                </div>

            </form>

            <%-- Active product filter information --%>

            <c:if test="${not empty selectedProductId}">

                <div style="
                        margin-top: 15px;
                        padding-top: 14px;
                        border-top: 1px solid var(--border);
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        gap: 12px;
                        flex-wrap: wrap;">

                    <span style="
                            display: inline-flex;
                            align-items: center;
                            gap: 7px;
                            padding: 6px 10px;
                            border: 1px solid #BFDBFE;
                            border-radius: 999px;
                            background: #EFF6FF;
                            color: #1D4ED8;
                            font-size: .75rem;
                            font-weight: 700;">

                        <i class="fa-solid fa-filter-circle-xmark"></i>

                        Product filter:
                        ${selectedProductType}
                        #${selectedProductId}

                    </span>

                    <a href="${ctx}/admin/stock/history"
                       style="
                            color: var(--danger);
                            font-size: .76rem;
                            font-weight: 700;
                            text-decoration: none;">

                        Clear product filter

                    </a>

                </div>

            </c:if>

        </div>

        <%-- =====================================================
             TRANSACTION TABLE
             ===================================================== --%>

        <div class="admin-panel stock-inventory-panel">

            <div class="stock-panel-title">

                <h3>

                    <i class="fa-solid fa-clipboard-list"></i>

                    Transaction History

                </h3>

                <span class="stock-threshold-note">

                    <i class="fa-solid fa-circle-info"></i>

                    Newest transactions are shown first

                </span>

            </div>

            <div class="admin-table-wrap stock-table-wrap">

                <table class="admin-table"
                       style="min-width: 1450px;">

                    <thead>

                    <tr>

                        <th style="width: 145px;">
                            Date
                        </th>

                        <th style="min-width: 220px;">
                            Product
                        </th>

                        <th>
                            Type
                        </th>

                        <th>
                            Movement
                        </th>

                        <th style="text-align: center;">
                            Change
                        </th>

                        <th style="text-align: center;">
                            Before
                        </th>

                        <th style="text-align: center;">
                            After
                        </th>

                        <th style="min-width: 130px;">
                            Reference
                        </th>

                        <th style="min-width: 220px;">
                            Reason / Note
                        </th>

                        <th style="min-width: 130px;">
                            Performed By
                        </th>

                    </tr>

                    </thead>

                    <tbody>

                    <c:choose>

                        <c:when test="${empty transactions}">

                            <tr>

                                <td colspan="10"
                                    class="stock-empty-state">

                                    <i class="fa-solid fa-clock-rotate-left"></i>

                                    No stock transactions found.

                                </td>

                            </tr>

                        </c:when>

                        <c:otherwise>

                            <c:forEach items="${transactions}"
                                       var="transaction">

                                <tr>

                                    <%-- DATE --%>

                                    <td class="stock-updated-cell">

                                        <c:out value="${transaction.createdAtFormatted}" />

                                    </td>

                                    <%-- PRODUCT --%>

                                    <td>

                                        <div class="stock-product-cell">

                                            <c:choose>

                                                <c:when test="${not empty transaction.imageUrl}">

                                                    <img
                                                        class="stock-product-image"
                                                        src="<c:out value='${transaction.imageUrl}' />"
                                                        alt="Product image"
                                                        onerror="this.style.display='none'; this.nextElementSibling.style.display='grid';" />

                                                    <span class="stock-product-placeholder"
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

                                                    <c:out value="${transaction.productName}" />

                                                </div>

                                                <div class="stock-product-code">

                                                    Product ID:
                                                    ${transaction.productId}

                                                </div>

                                            </div>

                                        </div>

                                    </td>

                                    <%-- PRODUCT TYPE --%>

                                    <td>

                                        <span class="stock-type-badge">

                                            <c:out value="${transaction.productTypeLabel}" />

                                        </span>

                                    </td>

                                    <%-- MOVEMENT TYPE --%>

                                    <td>

                                        <span class="
                                            movement-badge
                                            movement-badge--${transaction.movementCssClass}">

                                            <c:out value="${transaction.movementLabel}" />

                                        </span>

                                    </td>

                                    <%-- QUANTITY CHANGE --%>

                                    <td style="text-align: center;">

                                        <span class="
                                            ${transaction.quantityChange > 0
                                                ? 'stock-change-positive'
                                                : transaction.quantityChange < 0
                                                    ? 'stock-change-negative'
                                                    : ''}">

                                            <c:out value="${transaction.quantityChangeFormatted}" />

                                        </span>

                                    </td>

                                    <%-- BEFORE --%>

                                    <td style="text-align: center;">

                                        <strong>
                                            ${transaction.stockBefore}
                                        </strong>

                                    </td>

                                    <%-- AFTER --%>

                                    <td style="text-align: center;">

                                        <strong>
                                            ${transaction.stockAfter}
                                        </strong>

                                    </td>

                                    <%-- REFERENCE --%>

                                    <td>

                                        <c:choose>

                                            <c:when test="${not empty transaction.referenceCode}">

                                                <span style="
                                                        display: inline-flex;
                                                        align-items: center;
                                                        gap: 5px;
                                                        padding: 4px 8px;
                                                        border: 1px solid var(--border);
                                                        border-radius: 6px;
                                                        background: #F9FAFB;
                                                        color: var(--text-soft);
                                                        font-size: .73rem;
                                                        font-weight: 700;">

                                                    <i class="fa-solid fa-link"></i>

                                                    <c:out value="${transaction.referenceCode}" />

                                                </span>

                                            </c:when>

                                            <c:otherwise>

                                                <span style="color: var(--text-faint);">
                                                    —
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <%-- REASON AND NOTE --%>

                                    <td>

                                        <c:choose>

                                            <c:when test="${not empty transaction.reason
                                                            or not empty transaction.note}">

                                                <c:if test="${not empty transaction.reason}">

                                                    <div style="
                                                            color: var(--text);
                                                            font-size: .79rem;
                                                            font-weight: 700;">

                                                        <c:out value="${transaction.reason}" />

                                                    </div>

                                                </c:if>

                                                <c:if test="${not empty transaction.note}">

                                                    <div style="
                                                            max-width: 250px;
                                                            margin-top: 3px;
                                                            overflow: hidden;
                                                            color: var(--text-muted);
                                                            font-size: .72rem;
                                                            line-height: 1.4;
                                                            text-overflow: ellipsis;
                                                            white-space: nowrap;"
                                                         title="<c:out value='${transaction.note}' />">

                                                        <c:out value="${transaction.note}" />

                                                    </div>

                                                </c:if>

                                            </c:when>

                                            <c:otherwise>

                                                <span style="color: var(--text-faint);">
                                                    —
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <%-- PERFORMED BY --%>

                                    <td>

                                        <div style="
                                                display: flex;
                                                align-items: center;
                                                gap: 8px;">

                                            <span style="
                                                    width: 30px;
                                                    height: 30px;
                                                    display: grid;
                                                    place-items: center;
                                                    flex-shrink: 0;
                                                    border-radius: 50%;
                                                    background: var(--primary-lt);
                                                    color: var(--primary);
                                                    font-size: .72rem;">

                                                <i class="fa-solid fa-user"></i>

                                            </span>

                                            <span style="
                                                    color: var(--text-soft);
                                                    font-size: .77rem;
                                                    font-weight: 700;">

                                                <c:out value="${transaction.performedByName}" />

                                            </span>

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

                            transactions

                        </c:otherwise>

                    </c:choose>

                </div>

                <c:if test="${totalPages > 1}">

                    <div class="admin-pagination__nav">

                        <%-- PREVIOUS PAGE --%>

                        <c:if test="${currentPage > 0}">

                            <c:url var="previousPageUrl"
                                   value="/admin/stock/history">

                                <c:param
                                    name="page"
                                    value="${currentPage - 1}" />

                                <c:param
                                    name="q"
                                    value="${q}" />

                                <c:param
                                    name="productType"
                                    value="${selectedProductType}" />

                                <c:param
                                    name="movementType"
                                    value="${selectedMovementType}" />

                                <c:if test="${not empty selectedProductId}">

                                    <c:param
                                        name="productId"
                                        value="${selectedProductId}" />

                                </c:if>

                            </c:url>

                            <a href="${previousPageUrl}"
                               class="pag-btn">

                                <i class="fa-solid fa-chevron-left"
                                   style="font-size: .7rem;"></i>

                            </a>

                        </c:if>

                        <%-- PAGE NUMBERS --%>

                        <c:forEach begin="0"
                                   end="${totalPages - 1}"
                                   var="pageIndex">

                            <c:url var="pageUrl"
                                   value="/admin/stock/history">

                                <c:param
                                    name="page"
                                    value="${pageIndex}" />

                                <c:param
                                    name="q"
                                    value="${q}" />

                                <c:param
                                    name="productType"
                                    value="${selectedProductType}" />

                                <c:param
                                    name="movementType"
                                    value="${selectedMovementType}" />

                                <c:if test="${not empty selectedProductId}">

                                    <c:param
                                        name="productId"
                                        value="${selectedProductId}" />

                                </c:if>

                            </c:url>

                            <a href="${pageUrl}"
                               class="pag-btn
                                      ${pageIndex == currentPage
                                        ? 'pag-btn--active'
                                        : ''}">

                                ${pageIndex + 1}

                            </a>

                        </c:forEach>

                        <%-- NEXT PAGE --%>

                        <c:if test="${currentPage + 1 < totalPages}">

                            <c:url var="nextPageUrl"
                                   value="/admin/stock/history">

                                <c:param
                                    name="page"
                                    value="${currentPage + 1}" />

                                <c:param
                                    name="q"
                                    value="${q}" />

                                <c:param
                                    name="productType"
                                    value="${selectedProductType}" />

                                <c:param
                                    name="movementType"
                                    value="${selectedMovementType}" />

                                <c:if test="${not empty selectedProductId}">

                                    <c:param
                                        name="productId"
                                        value="${selectedProductId}" />

                                </c:if>

                            </c:url>

                            <a href="${nextPageUrl}"
                               class="pag-btn">

                                <i class="fa-solid fa-chevron-right"
                                   style="font-size: .7rem;"></i>

                            </a>

                        </c:if>

                    </div>

                </c:if>

            </div>

        </div>

    </section>

</main>

</body>
</html>