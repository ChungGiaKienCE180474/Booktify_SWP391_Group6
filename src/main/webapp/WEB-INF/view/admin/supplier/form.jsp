<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:choose>
    <c:when test="${formMode == 'edit'}">
        <c:url var="formAction" value="/admin/suppliers/${supplierId}" />
        <c:set var="pageKicker" value="EDIT SUPPLIER" />
        <c:set var="pageTitle" value="Edit Supplier" />
        <c:set var="submitLabel" value="Update Supplier" />
    </c:when>

    <c:otherwise>
        <c:url var="formAction" value="/admin/suppliers" />
        <c:set var="pageKicker" value="CREATE SUPPLIER" />
        <c:set var="pageTitle" value="New Supplier" />
        <c:set var="submitLabel" value="Create Supplier" />
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

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin-dashboard.css" />

    <style>
        .supplier-single-grid {
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
                    <i class="fa-solid fa-truck"></i>
                    ${pageKicker}
                </p>

                <h2>${pageTitle}</h2>
            </div>

            <a href="${pageContext.request.contextPath}/admin/suppliers"
               class="admin-button admin-button--ghost">
                <i class="fa-solid fa-arrow-left"></i>
                Back to Suppliers
            </a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="admin-alert admin-alert--danger">
                <c:out value="${errorMessage}" />
            </div>
        </c:if>

        <div class="admin-form-card">

            <div class="admin-form-card__header">
                <i class="fa-solid fa-file-pen"></i>
                <h3>Supplier Information</h3>
            </div>

            <div class="admin-form-card__body">

                <form:form modelAttribute="supplierDTO"
                           action="${formAction}"
                           method="post"
                           class="admin-form"
                           id="supplierForm">

                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>
                                Supplier Name <span style="color:#EF4444;">*</span>
                            </label>

                            <form:input path="supplierName"
                                        cssClass="admin-input"
                                        placeholder="Enter supplier name" />

                            <form:errors path="supplierName"
                                         cssClass="admin-error" />
                        </div>

                        <div class="admin-field">
                            <label>
                                Contact Person
                            </label>

                            <form:input path="contactPerson"
                                        cssClass="admin-input"
                                        placeholder="Enter contact person" />

                            <form:errors path="contactPerson"
                                         cssClass="admin-error" />
                        </div>
                    </div>

                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>
                                Email
                            </label>

                            <form:input path="email"
                                        type="email"
                                        cssClass="admin-input"
                                        placeholder="Enter email address" />

                            <form:errors path="email"
                                         cssClass="admin-error" />
                        </div>

                        <div class="admin-field">
                            <label>
                                Phone
                            </label>

                            <form:input path="phone"
                                        cssClass="admin-input"
                                        placeholder="Enter phone number" />

                            <form:errors path="phone"
                                         cssClass="admin-error" />
                        </div>
                    </div>

                    <div class="admin-form-grid supplier-single-grid">
                        <div class="admin-field">
                            <label>
                                Address
                            </label>

                            <form:input path="address"
                                        cssClass="admin-input"
                                        placeholder="Enter supplier address" />

                            <form:errors path="address"
                                         cssClass="admin-error" />
                        </div>
                    </div>

                    <div class="admin-form-grid supplier-single-grid">
                        <div class="admin-field">
                            <label>
                                Description
                            </label>

                            <form:textarea path="description"
                                           cssClass="admin-input admin-textarea"
                                           placeholder="Enter supplier description" />

                            <form:errors path="description"
                                         cssClass="admin-error" />
                        </div>
                    </div>

                    <form:errors cssClass="admin-error" element="div" />

                    <div class="admin-form__actions">

                        <a href="${pageContext.request.contextPath}/admin/suppliers"
                           class="admin-button admin-button--ghost">
                            <i class="fa-solid fa-xmark"></i>
                            Cancel
                        </a>

                        <button type="submit"
                                class="admin-button">
                            <i class="fa-solid fa-floppy-disk"></i>
                            ${submitLabel}
                        </button>

                    </div>

                </form:form>

            </div>

        </div>

    </section>

</main>

</body>

</html>