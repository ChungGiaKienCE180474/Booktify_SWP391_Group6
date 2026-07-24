<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <title>Promotion Management</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"/>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin-dashboard.css"/>

    <style>
        .promotion-toolbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            margin-top: 28px;
            margin-bottom: 24px;
        }

        .promotion-toolbar h3 {
            margin: 0;
            color: #111827;
            font-size: 22px;
            font-weight: 800;
        }

        .promotion-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 22px;
        }

        .promotion-card {
            padding: 22px;
            border: 1px solid #e5e7eb;
            border-radius: 18px;
            background: #ffffff;
            box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
            transition: all 0.2s ease;
        }

        .promotion-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 18px 36px rgba(15, 23, 42, 0.12);
        }

        .promotion-card-header {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            gap: 12px;
            margin-bottom: 12px;
        }

        .promotion-card h4 {
            margin: 0;
            color: #111827;
            font-size: 18px;
            font-weight: 800;
            line-height: 1.35;
        }

        .promotion-description {
            min-height: 44px;
            margin: 0 0 18px;
            color: #6b7280;
            font-size: 14px;
            line-height: 1.5;
        }

        .promotion-info {
            display: flex;
            flex-direction: column;
            gap: 10px;
            margin-bottom: 18px;
        }

        .promotion-info-row {
            display: flex;
            align-items: center;
            gap: 10px;
            color: #374151;
            font-size: 14px;
        }

        .promotion-info-row i {
            width: 18px;
            color: #2563eb;
        }

        .promo-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 7px 12px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 800;
            white-space: nowrap;
        }

        .promo-badge.active {
            color: #047857;
            background: #d1fae5;
        }

        .promo-badge.inactive {
            color: #6b7280;
            background: #f3f4f6;
        }

        .promo-discount {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 8px 12px;
            border-radius: 12px;
            color: #1d4ed8;
            background: #dbeafe;
            font-size: 14px;
            font-weight: 800;
        }

        .promotion-category-count {
            color: #475569;
            font-size: 13px;
            font-weight: 700;
        }

        .promotion-actions {
            display: flex;
            gap: 10px;
            padding-top: 16px;
            border-top: 1px solid #e5e7eb;
        }

        .card-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
            padding: 10px 14px;
            border: none;
            border-radius: 10px;
            font-size: 13px;
            font-weight: 800;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.18s ease;
        }

        .card-btn.edit {
            color: #92400e;
            background: #fef3c7;
        }

        .card-btn.edit:hover {
            background: #fde68a;
        }

        .card-btn.delete {
            color: #991b1b;
            background: #fee2e2;
        }

        .card-btn.delete:hover {
            background: #fecaca;
        }

        .empty-state {
            grid-column: 1 / -1;
            padding: 42px;
            border: 1px dashed #cbd5e1;
            border-radius: 18px;
            background: #ffffff;
            color: #64748b;
            text-align: center;
        }

        .empty-state i {
            margin-bottom: 14px;
            color: #94a3b8;
            font-size: 42px;
        }

        .empty-state h3 {
            margin: 0 0 8px;
            color: #111827;
            font-size: 20px;
        }

        .modal-overlay {
            position: fixed;
            inset: 0;
            z-index: 9999;
            display: none;
            align-items: center;
            justify-content: center;
            padding: 24px;
            background: rgba(15, 23, 42, 0.72);
            backdrop-filter: blur(5px);
        }

        .modal-overlay.show {
            display: flex;
        }

        .confirm-modal {
            width: 820px;
            max-width: 100%;
            max-height: calc(100vh - 48px);
            overflow-y: auto;
            border-radius: 20px;
            background: #ffffff;
            box-shadow: 0 30px 80px rgba(15, 23, 42, 0.35);
            animation: modalFade 0.2s ease;
        }

        @keyframes modalFade {
            from {
                opacity: 0;
                transform: translateY(14px) scale(0.98);
            }

            to {
                opacity: 1;
                transform: translateY(0) scale(1);
            }
        }

        .modal-header-custom {
            position: sticky;
            top: 0;
            z-index: 3;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 24px 28px;
            border-bottom: 1px solid #e5e7eb;
            background: #ffffff;
        }

        .modal-title-wrap {
            display: flex;
            flex-direction: column;
            gap: 4px;
        }

        .modal-title-wrap h3 {
            margin: 0;
            color: #111827;
            font-size: 24px;
            font-weight: 800;
        }

        .modal-title-wrap p {
            margin: 0;
            color: #6b7280;
            font-size: 14px;
        }

        .modal-close {
            padding: 6px 10px;
            border: none;
            border-radius: 10px;
            background: transparent;
            color: #6b7280;
            font-size: 22px;
            cursor: pointer;
        }

        .modal-close:hover {
            color: #111827;
            background: #f3f4f6;
        }

        .modal-body-custom {
            padding: 26px 28px 28px;
        }

        .promo-form-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 18px 20px;
        }

        .promo-field {
            display: flex;
            flex-direction: column;
        }

        .promo-field.full {
            grid-column: 1 / -1;
        }

        .form-label {
            margin-bottom: 8px;
            color: #374151;
            font-size: 14px;
            font-weight: 700;
        }

        .required {
            color: #ef4444;
        }

        .promo-input,
        .promo-select,
        .promo-textarea {
            width: 100%;
            box-sizing: border-box;
            padding: 12px 14px;
            border: 1px solid #d1d5db;
            border-radius: 12px;
            outline: none;
            background: #ffffff;
            color: #111827;
            font-family: inherit;
            font-size: 14px;
        }

        .promo-textarea {
            min-height: 96px;
            resize: vertical;
        }

        .promo-input:focus,
        .promo-select:focus,
        .promo-textarea:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.12);
        }

        .category-selection-section {
            grid-column: 1 / -1;
            padding: 16px;
            border: 1px solid #dbe2ea;
            border-radius: 14px;
            background: #f8fafc;
        }

        .category-selection-header {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            gap: 14px;
            margin-bottom: 12px;
        }

        .category-selection-header h4 {
            margin: 0 0 4px;
            color: #1f2937;
            font-size: 15px;
            font-weight: 800;
        }

        .category-selection-header p {
            margin: 0;
            color: #6b7280;
            font-size: 12px;
        }

        .category-selection-actions {
            display: flex;
            gap: 7px;
        }

        .category-small-btn {
            padding: 7px 10px;
            border: 1px solid #cfd6e2;
            border-radius: 8px;
            background: #ffffff;
            color: #344054;
            font-size: 12px;
            font-weight: 700;
            cursor: pointer;
        }

        .category-small-btn:hover {
            border-color: #2563eb;
            color: #2563eb;
        }

        .category-search-wrapper {
            position: relative;
            margin-bottom: 10px;
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
            padding: 10px 13px 10px 38px;
            border: 1px solid #d1d5db;
            border-radius: 10px;
            outline: none;
            background: #ffffff;
            color: #111827;
            font-size: 13px;
        }

        .category-search-input:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }

        .category-list {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 10px;
            max-height: 280px;
            padding: 10px;
            overflow-y: auto;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            background: #ffffff;
        }

        .category-item {
            display: flex;
            align-items: center;
            gap: 11px;
            min-width: 0;
            padding: 13px;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            background: #ffffff;
            cursor: pointer;
            transition: all 0.15s ease;
        }

        .category-item:hover {
            border-color: #93c5fd;
            background: #f5f8ff;
        }

        .category-item.selected {
            border-color: #2563eb;
            background: #eff6ff;
        }

        .category-checkbox {
            width: 18px;
            height: 18px;
            flex-shrink: 0;
            accent-color: #2563eb;
            cursor: pointer;
        }

        .category-icon {
            display: flex;
            width: 40px;
            height: 40px;
            flex: 0 0 40px;
            align-items: center;
            justify-content: center;
            border-radius: 10px;
            background: #eaf2ff;
            color: #2563eb;
        }

        .category-info {
            display: flex;
            min-width: 0;
            flex: 1;
            flex-direction: column;
        }

        .category-name {
            overflow: hidden;
            color: #1f2937;
            font-size: 13px;
            font-weight: 800;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .category-meta {
            margin-top: 3px;
            color: #6b7280;
            font-size: 11px;
        }

        .category-selection-summary {
            display: flex;
            align-items: center;
            gap: 7px;
            margin-top: 10px;
            color: #475569;
            font-size: 12px;
        }

        .category-selection-summary i {
            color: #16a34a;
        }

        .category-error {
            display: none;
            align-items: center;
            gap: 7px;
            margin-top: 10px;
            padding: 9px 11px;
            border: 1px solid #fecaca;
            border-radius: 8px;
            background: #fef2f2;
            color: #dc2626;
            font-size: 12px;
            font-weight: 700;
        }

        .category-error.show {
            display: flex;
        }

        .category-empty {
            grid-column: 1 / -1;
            padding: 26px;
            color: #94a3b8;
            text-align: center;
        }

        .category-empty i {
            display: block;
            margin-bottom: 8px;
            font-size: 25px;
        }

        .promo-checkbox-row {
            grid-column: 1 / -1;
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 12px 14px;
            border: 1px solid #e5e7eb;
            border-radius: 13px;
            background: #f8fafc;
        }

        .promo-checkbox-row input {
            width: 18px;
            height: 18px;
            accent-color: #2563eb;
        }

        .promo-checkbox-row label {
            color: #374151;
            font-weight: 700;
            cursor: pointer;
        }

        .promo-actions {
            grid-column: 1 / -1;
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 8px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
        }

        .promo-btn {
            display: inline-flex;
            min-width: 130px;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 12px 22px;
            border: none;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 800;
            cursor: pointer;
        }

        .promo-btn.cancel {
            border: 1px solid #d1d5db;
            background: #ffffff;
            color: #111827;
        }

        .promo-btn.primary {
            background: #2563eb;
            color: #ffffff;
            box-shadow: 0 8px 18px rgba(37, 99, 235, 0.25);
        }

        @media (max-width: 1100px) {
            .promotion-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 768px) {
            .promotion-toolbar {
                flex-direction: column;
                align-items: stretch;
            }

            .promotion-grid,
            .category-list {
                grid-template-columns: 1fr;
            }

            .confirm-modal {
                width: 100%;
            }

            .promo-form-grid {
                grid-template-columns: 1fr;
            }

            .category-selection-header {
                flex-direction: column;
            }

            .category-selection-actions {
                width: 100%;
            }

            .category-small-btn {
                flex: 1;
            }

            .promo-actions {
                flex-direction: column-reverse;
            }

            .promo-btn {
                width: 100%;
            }
        }
        .simple-confirm-modal {
            width: 440px;
            max-width: 94vw;
            overflow: hidden;
            border-radius: 18px;
            background: #ffffff;
            box-shadow: 0 30px 90px rgba(15, 23, 42, 0.4);
            animation: modalFade 0.2s ease;
        }

        .simple-confirm-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 20px 22px;
            border-bottom: 1px solid #e5e7eb;
        }

        .simple-confirm-title {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .simple-confirm-title i {
            color: #ef4444;
            font-size: 19px;
        }

        .simple-confirm-title h3 {
            margin: 0;
            color: #111827;
            font-size: 19px;
            font-weight: 800;
        }

        .simple-confirm-body {
            padding: 22px;
            color: #4b5563;
            line-height: 1.6;
        }

        .simple-confirm-body p {
            margin: 0;
        }

        .simple-confirm-name {
            margin-top: 12px !important;
            color: #111827;
        }

        .simple-confirm-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            padding: 16px 22px;
            border-top: 1px solid #e5e7eb;
            background: #f9fafb;
        }

        .simple-confirm-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 7px;
            min-width: 92px;
            padding: 10px 17px;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 800;
            cursor: pointer;
        }

        .simple-confirm-btn.no {
            border: 1px solid #d1d5db;
            background: #ffffff;
            color: #374151;
        }

        .simple-confirm-btn.yes {
            border: none;
            background: #ef4444;
            color: #ffffff;
        }

        .simple-confirm-btn.no:hover {
            background: #f3f4f6;
        }

        .simple-confirm-btn.yes:hover {
            background: #dc2626;
        }
        .promotion-table-wrap {
            overflow-x: auto;
            border: 1px solid #e2e8f0;
            border-radius: 14px;
            background: #ffffff;
            box-shadow: 0 8px 24px rgba(15, 23, 42, .06);
        }

        .promotion-table {
            width: 100%;
            min-width: 1180px;
            border-collapse: collapse;
        }

        .promotion-table th,
        .promotion-table td {
            padding: 14px 16px;
            border-bottom: 1px solid #e2e8f0;
            vertical-align: middle;
            text-align: left;
        }

        .promotion-table th {
            background: #f8fafc;
            color: #64748b;
            font-size: 12px;
            font-weight: 800;
            letter-spacing: .045em;
            text-transform: uppercase;
            white-space: nowrap;
        }

        .promotion-table tbody tr:hover {
            background: #f8fffd;
        }

        .promotion-table tbody tr:last-child td {
            border-bottom: none;
        }

        .promotion-name {
            max-width: 230px;
            color: #111827;
            font-weight: 800;
        }

        .promotion-description-cell {
            max-width: 260px;
            color: #475569;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .promotion-date {
            color: #475569;
            font-size: 13px;
            white-space: nowrap;
        }

        .muted-text {
            color: #94a3b8;
        }

        .promotion-action-heading {
            text-align: center !important;
        }

        .promotion-table-actions {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 7px;
            white-space: nowrap;
        }

        .promotion-icon-button {
            width: 34px;
            height: 34px;
            padding: 0;
            border: 1px solid #dbe3ea;
            border-radius: 5px;
            background: #ffffff;
            color: #64748b;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all .2s ease;
        }

        .promotion-icon-button.edit:hover {
            color: #d97706;
            border-color: #f59e0b;
            background: #fffbeb;
        }

        .promotion-icon-button.delete:hover {
            color: #dc2626;
            border-color: #ef4444;
            background: #fef2f2;
        }

        .promotion-empty-state {
            border: none;
            box-shadow: none;
        }

    </style>
</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp"/>

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp"/>

    <section class="admin-content">

        <div class="admin-toolbar">
            <div>
                <p class="admin-kicker">
                    <i class="fa-solid fa-percent"></i>
                    Promotion Management
                </p>
                <h2>Promotions</h2>
                <p>Manage promotion campaigns and applicable categories.</p>
            </div>

            <button type="button"
                    onclick="openCreateModal()"
                    class="admin-button">

                <i class="fa-solid fa-plus"></i>
                Create New Promotion
            </button>
        </div>

        <div class="promotion-toolbar">
            <h3>Promotion List</h3>
        </div>

        <div class="promotion-table-wrap">

            <c:choose>

                <c:when test="${empty promotions}">

                    <div class="empty-state promotion-empty-state">
                        <i class="fa-solid fa-tags"></i>
                        <h3>No promotions found</h3>
                        <p>Create the first promotion campaign for the system.</p>
                    </div>

                </c:when>

                <c:otherwise>

                    <table class="promotion-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Discount</th>
                                <th>Categories</th>
                                <th>Start date</th>
                                <th>End date</th>
                                <th>Status</th>
                                <th class="promotion-action-heading">Actions</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="p" items="${promotions}">
                                <tr>
                                    <td class="promotion-name">
                                        <c:out value="${p.name}"/>
                                    </td>

                                    <td class="promotion-description-cell">
                                        <c:choose>
                                            <c:when test="${empty p.description}">
                                                <span class="muted-text">No description</span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:out value="${p.description}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <span class="promo-discount">
                                            ${p.discountValue}${p.percentage ? '%' : ' VND'}
                                        </span>
                                    </td>

                                    <td>
                                        <span class="promotion-category-count">
                                            ${empty p.applicableCategories
                                                    ? 0
                                                    : p.applicableCategories.size()}
                                            category(s)
                                        </span>
                                    </td>

                                    <td class="promotion-date">
                                        ${p.startDateFormatted}
                                    </td>

                                    <td class="promotion-date">
                                        ${p.endDateFormatted}
                                    </td>

                                    <td>
                                        <span class="promo-badge ${p.active ? 'active' : 'inactive'}">
                                            ${p.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>

                                    <td>
                                        <div class="promotion-table-actions">
                                            <button type="button"
                                                    class="promotion-icon-button edit"
                                                    title="Edit promotion"
                                                    data-id="${p.id}"
                                                    data-name="<c:out value='${p.name}'/>"
                                                    data-description="<c:out value='${p.description}'/>"
                                                    data-percentage="${p.percentage}"
                                                    data-discount-value="${p.discountValue}"
                                                    data-start-date="${p.startDate}"
                                                    data-end-date="${p.endDate}"
                                                    data-active="${p.active}"
                                                    data-category-ids="<c:forEach var='selectedCategory'
                                                                                     items='${p.applicableCategories}'
                                                                                     varStatus='status'>${selectedCategory.id}<c:if test='${!status.last}'>,</c:if></c:forEach>"
                                                    onclick="openEditModal(this)">
                                                <i class="fa-solid fa-pen"></i>
                                            </button>

                                            <button type="button"
                                                    class="promotion-icon-button delete"
                                                    title="Delete promotion"
                                                    onclick="openPromotionDeleteModal(
                                                            '${p.id}',
                                                            '<c:out value="${p.name}"/>'
                                                            )">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                </c:otherwise>

            </c:choose>

        </div>

    </section>
</main>

<div id="promotionModal" class="modal-overlay">

    <div class="confirm-modal">

        <div class="modal-header-custom">

            <div class="modal-title-wrap">

                <h3 id="modalTitle">
                    Create New Promotion
                </h3>

                <p id="modalSubtitle">
                    Fill in the promotion campaign information
                </p>

            </div>

            <button type="button"
                    class="modal-close"
                    onclick="closeModal()">

                <i class="fa-solid fa-xmark"></i>
            </button>

        </div>

        <div class="modal-body-custom">

            <form id="promotionForm"
                  method="post"
                  action="${pageContext.request.contextPath}/admin/promotions"
                  onsubmit="return validatePromotionForm()">

                <c:if test="${not empty _csrf}">

                    <input type="hidden"
                           name="${_csrf.parameterName}"
                           value="${_csrf.token}"/>

                </c:if>

                <input type="hidden"
                       name="id"
                       id="formId"
                       value="${promotion.id}"/>

                <div class="promo-form-grid">

                    <div class="promo-field full">

                        <label class="form-label" for="name">

                            Promotion Name
                            <span class="required">*</span>

                        </label>

                        <input type="text"
                               name="name"
                               id="name"
                               class="promo-input"
                               value="<c:out value='${promotion.name}'/>"
                               placeholder="Example: Summer Sale"
                               required/>

                    </div>

                    <div class="promo-field full">

                        <label class="form-label" for="description">
                            Description
                        </label>

                        <textarea name="description"
                                  id="description"
                                  class="promo-textarea"
                                  rows="3"
                                  placeholder="Enter a short description for the promotion campaign"><c:out value="${promotion.description}"/></textarea>

                    </div>

                    <div class="promo-field">

                        <label class="form-label" for="percentage">
                            Discount Type
                        </label>

                        <select name="percentage"
                                id="percentage"
                                class="promo-select">

                            <option value="true"
                                    ${promotion.percentage ? 'selected' : ''}>

                                Percentage (%)
                            </option>

                            <option value="false"
                                    ${!promotion.percentage ? 'selected' : ''}>

                                Fixed Amount (VND)
                            </option>

                        </select>

                    </div>

                    <div class="promo-field">

                        <label class="form-label"
                               for="discountValue">

                            Discount Value
                            <span class="required">*</span>

                        </label>

                        <input type="number"
                               name="discountValue"
                               id="discountValue"
                               step="0.01"
                               min="0"
                               value="${promotion.discountValue}"
                               class="promo-input"
                               placeholder="Example: 10"
                               required/>

                    </div>

                    <div class="promo-field">

                        <label class="form-label"
                               for="startDate">

                            Start Date
                            <span class="required">*</span>

                        </label>

                        <input type="datetime-local"
                               name="startDate"
                               id="startDate"
                               value="${promotion.startDate}"
                               class="promo-input"
                               required/>

                    </div>

                    <div class="promo-field">

                        <label class="form-label"
                               for="endDate">

                            End Date
                            <span class="required">*</span>

                        </label>

                        <input type="datetime-local"
                               name="endDate"
                               id="endDate"
                               value="${promotion.endDate}"
                               class="promo-input"
                               required/>

                    </div>

                    <div class="category-selection-section">

                        <div class="category-selection-header">

                            <div>

                                <h4>
                                    Applicable Categories
                                    <span class="required">*</span>
                                </h4>

                                <p>
                                    All books in the selected categories will receive this promotion.
                                </p>

                            </div>

                            <div class="category-selection-actions">

                                <button type="button"
                                        class="category-small-btn"
                                        onclick="selectAllVisibleCategories()">

                                    Select All
                                </button>

                                <button type="button"
                                        class="category-small-btn"
                                        onclick="clearAllCategories()">

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

                        <div class="category-list"
                             id="categoryList">

                            <c:choose>

                                <c:when test="${empty categories}">

                                    <div class="category-empty">

                                        <i class="fa-solid fa-layer-group"></i>

                                        No active categories are currently available.

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

                                        <label class="category-item ${categorySelected ? 'selected' : ''}"
                                               data-search="${category.name}">

                                            <input type="checkbox"
                                                   name="categoryIds"
                                                   value="${category.id}"
                                                   class="category-checkbox"
                                                   ${categorySelected ? 'checked' : ''}/>

                                            <span class="category-icon">

                                                <i class="fa-solid fa-layer-group"></i>

                                            </span>

                                            <span class="category-info">

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

                                <strong id="selectedCategoryCount">
                                    0
                                </strong>

                                category(s) selected

                            </span>

                        </div>

                        <div id="categorySelectionError"
                             class="category-error">

                            <i class="fa-solid fa-circle-exclamation"></i>

                            Please select at least one category.

                        </div>

                    </div>

                    <div class="promo-checkbox-row">

                        <input type="hidden"
                               name="_active"
                               value="on"/>

                        <input type="checkbox"
                               name="active"
                               id="active"
                               value="true"
                               ${promotion.active ? 'checked' : ''}/>

                        <label for="active">
                            Activate this promotion
                        </label>

                    </div>

                    <div class="promo-actions">

                        <button type="button"
                                class="promo-btn cancel"
                                onclick="closeModal()">

                            Cancel
                        </button>

                        <button type="submit"
                                class="promo-btn primary">

                            <i class="fa-solid fa-floppy-disk"></i>

                            <span id="submitButtonText">
                                Save Promotion
                            </span>

                        </button>

                    </div>

                </div>

            </form>

        </div>

    </div>

</div>
<div id="promotionDeleteModal" class="modal-overlay">

    <div class="simple-confirm-modal">

        <div class="simple-confirm-header">

            <div class="simple-confirm-title">
                <i class="fa-solid fa-circle-exclamation"></i>
                <h3>Confirm Delete Promotion</h3>
            </div>

            <button type="button"
                    class="modal-close"
                    onclick="closePromotionDeleteModal()">

                <i class="fa-solid fa-xmark"></i>
            </button>

        </div>

        <div class="simple-confirm-body">

            <p>
                Are you sure you want to delete this promotion?
            </p>

            <p class="simple-confirm-name">
                <strong>Promotion:</strong>
                <span id="promotionDeleteName"></span>
            </p>

        </div>

        <form id="promotionDeleteForm"
              method="post">

            <c:if test="${not empty _csrf}">
                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}"/>
            </c:if>

            <div class="simple-confirm-actions">

                <button type="button"
                        class="simple-confirm-btn no"
                        onclick="closePromotionDeleteModal()">

                    <i class="fa-solid fa-xmark"></i>
                    No
                </button>

                <button type="submit"
                        class="simple-confirm-btn yes">

                    <i class="fa-solid fa-trash"></i>
                    Yes
                </button>

            </div>

        </form>

    </div>

</div>
<jsp:include page="/WEB-INF/view/layout/admin/toast.jsp" />

<script>
    const contextPath = '${pageContext.request.contextPath}';

    document.addEventListener('DOMContentLoaded', function () {

        const searchInput =
            document.getElementById('categorySearch');

        if (searchInput) {

            searchInput.addEventListener(
                'input',
                filterCategories
            );
        }

        document.querySelectorAll('.category-checkbox')
            .forEach(function (checkbox) {

                checkbox.addEventListener(
                    'change',
                    updateCategorySelection
                );
            });

        const promotionModal =
            document.getElementById('promotionModal');

        if (promotionModal) {

            promotionModal.addEventListener(
                'click',
                function (event) {

                    if (event.target === this) {
                        closeModal();
                    }
                });
        }

        document.addEventListener(
            'keydown',
            function (event) {

                if (event.key === 'Escape') {
                    closeModal();
                    closePromotionDeleteModal();
                }
            });

        normalizeDateTimeInputs();
        updateCategorySelection();

        const shouldOpenPromotionModal =
            '${openPromotionModal}' === 'true';

        if (shouldOpenPromotionModal) {

            const formMode = '${formMode}';
            const editId = '${editId}';

            if (formMode === 'edit' && editId) {

                document.getElementById('modalTitle').innerText =
                    'Edit Promotion';

                document.getElementById('modalSubtitle').innerText =
                    'Update campaign information and applicable categories';

                document.getElementById('submitButtonText').innerText =
                    'Update Promotion';

                document.getElementById('promotionForm').action =
                    contextPath + '/admin/promotions/' + editId;

            } else {

                document.getElementById('modalTitle').innerText =
                    'Create New Promotion';

                document.getElementById('modalSubtitle').innerText =
                    'Fill in the campaign information and select applicable categories';

                document.getElementById('submitButtonText').innerText =
                    'Save Promotion';

                document.getElementById('promotionForm').action =
                    contextPath + '/admin/promotions';
            }

            document.getElementById('promotionModal')
                .classList.add('show');
        }
    });

    function openCreateModal() {

        const form =
            document.getElementById('promotionForm');

        document.getElementById('modalTitle').innerText =
            'Create New Promotion';

        document.getElementById('modalSubtitle').innerText =
            'Fill in the campaign information and select applicable categories';

        document.getElementById('submitButtonText').innerText =
            'Save Promotion';

        form.reset();
        form.action =
            contextPath + '/admin/promotions';

        document.getElementById('formId').value = '';
        document.getElementById('percentage').value = 'true';
        document.getElementById('active').checked = true;
        document.getElementById('categorySearch').value = '';

        clearAllCategories();
        showAllCategories();

        document.getElementById('categorySelectionError')
            .classList.remove('show');

        document.getElementById('promotionModal')
            .classList.add('show');
    }

    function openEditModal(button) {

        const form =
            document.getElementById('promotionForm');

        const id =
            button.dataset.id;

        const name =
            button.dataset.name || '';

        const description =
            button.dataset.description || '';

        const percentage =
            button.dataset.percentage;

        const discountValue =
            button.dataset.discountValue || '';

        const startDate =
            button.dataset.startDate || '';

        const endDate =
            button.dataset.endDate || '';

        const active =
            button.dataset.active;

        const selectedCategoryIds =
            button.dataset.categoryIds || '';

        document.getElementById('modalTitle').innerText =
            'Edit Promotion';

        document.getElementById('modalSubtitle').innerText =
            'Update campaign information and applicable categories';

        document.getElementById('submitButtonText').innerText =
            'Update Promotion';

        form.action =
            contextPath + '/admin/promotions/' + id;

        document.getElementById('formId').value =
            id;

        document.getElementById('name').value =
            name;

        document.getElementById('description').value =
            description;

        document.getElementById('percentage').value =
            percentage === 'true'
                ? 'true'
                : 'false';

        document.getElementById('discountValue').value =
            discountValue;

        document.getElementById('startDate').value =
            formatDateTimeLocal(startDate);

        document.getElementById('endDate').value =
            formatDateTimeLocal(endDate);

        document.getElementById('active').checked =
            active === 'true';

        document.getElementById('categorySearch').value = '';

        showAllCategories();
        clearAllCategories();

        const categoryIds =
            selectedCategoryIds
                .split(',')
                .map(function (value) {
                    return value.trim();
                })
                .filter(function (value) {
                    return value !== '';
                });

        document.querySelectorAll('.category-checkbox')
            .forEach(function (checkbox) {

                checkbox.checked =
                    categoryIds.includes(
                        checkbox.value
                    );
            });

        document.getElementById('categorySelectionError')
            .classList.remove('show');

        updateCategorySelection();

        document.getElementById('promotionModal')
            .classList.add('show');
    }

    function formatDateTimeLocal(value) {

        if (!value) {
            return '';
        }

        value =
            value.trim().replace(' ', 'T');

        if (value.length >= 16) {
            return value.substring(0, 16);
        }

        return value;
    }

    function normalizeDateTimeInputs() {

        const startDate =
            document.getElementById('startDate');

        const endDate =
            document.getElementById('endDate');

        if (startDate && startDate.value) {

            startDate.value =
                formatDateTimeLocal(
                    startDate.value
                );
        }

        if (endDate && endDate.value) {

            endDate.value =
                formatDateTimeLocal(
                    endDate.value
                );
        }
    }

    function filterCategories() {

        const keyword =
            document.getElementById('categorySearch')
                .value
                .trim()
                .toLowerCase();

        document.querySelectorAll('.category-item')
            .forEach(function (item) {

                const searchText =
                    (item.dataset.search || '')
                        .toLowerCase();

                item.style.display =
                    searchText.includes(keyword)
                        ? 'flex'
                        : 'none';
            });
    }

    function showAllCategories() {

        document.querySelectorAll('.category-item')
            .forEach(function (item) {

                item.style.display = 'flex';
            });
    }

    function selectAllVisibleCategories() {

        document.querySelectorAll('.category-item')
            .forEach(function (item) {

                if (item.style.display !== 'none') {

                    const checkbox =
                        item.querySelector(
                            '.category-checkbox'
                        );

                    if (checkbox) {
                        checkbox.checked = true;
                    }
                }
            });

        updateCategorySelection();
    }

    function clearAllCategories() {

        document.querySelectorAll('.category-checkbox')
            .forEach(function (checkbox) {

                checkbox.checked = false;
            });

        updateCategorySelection();
    }

    function updateCategorySelection() {

        const checkedCategories =
            document.querySelectorAll(
                '.category-checkbox:checked'
            );

        const countElement =
            document.getElementById(
                'selectedCategoryCount'
            );

        if (countElement) {

            countElement.innerText =
                checkedCategories.length;
        }

        document.querySelectorAll('.category-item')
            .forEach(function (item) {

                const checkbox =
                    item.querySelector(
                        '.category-checkbox'
                    );

                if (checkbox && checkbox.checked) {

                    item.classList.add('selected');

                } else {

                    item.classList.remove('selected');
                }
            });

        if (checkedCategories.length > 0) {

            document.getElementById(
                'categorySelectionError'
            ).classList.remove('show');
        }
    }

    function validatePromotionForm() {

        const checkedCategories =
            document.querySelectorAll(
                '.category-checkbox:checked'
            );

        if (checkedCategories.length === 0) {

            document.getElementById(
                'categorySelectionError'
            ).classList.add('show');

            document.getElementById(
                'categoryList'
            ).scrollIntoView({
                behavior: 'smooth',
                block: 'center'
            });

            return false;
        }

        const percentage =
            document.getElementById(
                'percentage'
            ).value;

        const discountValue =
            Number(
                document.getElementById(
                    'discountValue'
                ).value
            );

        if (discountValue < 0) {

            alert(
                'Discount value cannot be negative.'
            );

            return false;
        }

        if (percentage === 'true'
                && discountValue > 100) {

            alert(
                'Percentage discount cannot be greater than 100%.'
            );

            return false;
        }

        const startDate =
            document.getElementById(
                'startDate'
            ).value;

        const endDate =
            document.getElementById(
                'endDate'
            ).value;

        if (startDate
                && endDate
                && endDate <= startDate) {

            alert(
                'End date must be after start date.'
            );

            return false;
        }

        return true;
    }
    function openPromotionDeleteModal(promotionId, promotionName) {

        document.getElementById(
            'promotionDeleteName'
        ).innerText = promotionName || 'N/A';

        document.getElementById(
            'promotionDeleteForm'
        ).action =
            contextPath
            + '/admin/promotions/'
            + promotionId
            + '/delete';

        document.getElementById(
            'promotionDeleteModal'
        ).classList.add('show');
    }

    function closePromotionDeleteModal() {

        document.getElementById(
            'promotionDeleteModal'
        ).classList.remove('show');
    }
    function closeModal() {

        document.getElementById(
            'promotionModal'
        ).classList.remove('show');
    }
</script>

</body>
</html>


