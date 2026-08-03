<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

            <c:set var="ctx" value="${pageContext.request.contextPath}" />

            <c:choose>
                <c:when test="${formMode == 'edit'}">
                    <c:url var="formAction" value="/admin/vouchers/update/${voucherId}" />
                </c:when>
                <c:otherwise>
                    <c:url var="formAction" value="/admin/vouchers" />
                </c:otherwise>
            </c:choose>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />

                <title>${formMode == 'edit' ? 'Edit Voucher' : 'Create Voucher'}</title>

                <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css?v=4" />

                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

                <style>
                    .voucher-money-wrap {
                        position: relative;
                    }

                    .voucher-money-wrap .admin-input {
                        padding-right: 52px;
                    }

                    .voucher-money-suffix {
                        position: absolute;
                        right: 14px;
                        top: 50%;
                        transform: translateY(-50%);
                        color: #6B7280;
                        font-size: .82rem;
                        font-weight: 800;
                        pointer-events: none;
                    }

                    .voucher-grid {
                        display: grid;
                        grid-template-columns: repeat(2, minmax(0, 1fr));
                        gap: 18px;
                    }

                    .voucher-grid-full {
                        grid-column: 1 / -1;
                    }

                    @media (max-width: 768px) {
                        .voucher-grid {
                            grid-template-columns: 1fr;
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
                                    <i class="fa-solid fa-ticket"></i>
                                    ${formMode == 'edit' ? 'Edit Voucher' : 'Create Voucher'}
                                </p>

                                <h2>
                                    ${formMode == 'edit' ? 'Edit Voucher' : 'New Voucher'}
                                </h2>
                            </div>

                            <a href="${ctx}/admin/vouchers" class="admin-button admin-button--ghost">
                                <i class="fa-solid fa-arrow-left"></i>
                                Back
                            </a>

                        </div>

                        <div class="admin-form-card">

                            <div class="admin-form-card__header">
                                <i class="fa-solid fa-ticket"></i>
                                <h3>Voucher Information</h3>
                            </div>

                            <div class="admin-form-card__body">

                                <form:form modelAttribute="voucherDTO" action="${formAction}" method="post"
                                    cssClass="admin-form" id="voucherForm">

                                    <form:errors cssClass="admin-error" element="div" />

                                    <div class="voucher-grid">

                                        <div class="admin-field">
                                            <label>
                                                Voucher Name
                                                <span style="color:#EF4444;">*</span>
                                            </label>

                                            <form:input path="voucherName" cssClass="admin-input"
                                                placeholder="Example: Summer Sale 2026" />

                                            <form:errors path="voucherName" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field">
                                            <label>
                                                Voucher Code
                                                <span style="color:#EF4444;">*</span>
                                            </label>

                                            <form:input path="voucherCode" cssClass="admin-input"
                                                placeholder="Example: SUMMER10" />

                                            <form:errors path="voucherCode" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field">
                                            <label>
                                                Discount Type
                                                <span style="color:#EF4444;">*</span>
                                            </label>

                                            <form:select path="discountType" cssClass="admin-input" id="discountType">
                                                <form:option value="" label="-- Select Type --" />
                                                <form:option value="PERCENT" label="Percentage (%)" />
                                                <form:option value="FIXED" label="Fixed Amount" />
                                            </form:select>

                                            <form:errors path="discountType" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field">
                                            <label>
                                                Discount Value
                                                <span style="color:#EF4444;">*</span>
                                            </label>

                                            <div class="voucher-money-wrap">
                                                <form:input path="discountValue" type="text"
                                                    cssClass="admin-input js-integer-money"
                                                    placeholder="Example: 10 or 50000" autocomplete="off" />

                                                <span class="voucher-money-suffix" id="discountValueSuffix">₫</span>
                                            </div>

                                            <form:errors path="discountValue" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field">
                                            <label>
                                                Minimum Order Amount
                                            </label>

                                            <div class="voucher-money-wrap">
                                                <form:input path="minOrderAmount" type="text"
                                                    cssClass="admin-input js-integer-money" placeholder="Example: 20000"
                                                    autocomplete="off" />

                                                <span class="voucher-money-suffix">₫</span>
                                            </div>

                                            <form:errors path="minOrderAmount" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field">
                                            <label>
                                                Maximum Order Amount
                                            </label>

                                            <div class="voucher-money-wrap">
                                                <form:input path="maxOrderAmount" type="text"
                                                    cssClass="admin-input js-integer-money"
                                                    placeholder="Example: 100000" autocomplete="off" />

                                                <span class="voucher-money-suffix">₫</span>
                                            </div>

                                            <form:errors path="maxOrderAmount" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field">
                                            <label>
                                                Quantity
                                                <span style="color:#EF4444;">*</span>
                                            </label>

                                            <form:input path="quantity" type="number" min="1" step="1"
                                                cssClass="admin-input" />

                                            <form:errors path="quantity" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field">
                                            <label>
                                                Start Date
                                                <span style="color:#EF4444;">*</span>
                                            </label>

                                            <input type="date" name="startDate" value="${voucherDTO.startDate}"
                                                class="admin-input" />

                                            <form:errors path="startDate" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field">
                                            <label>
                                                End Date
                                                <span style="color:#EF4444;">*</span>
                                            </label>

                                            <input type="date" name="endDate" value="${voucherDTO.endDate}"
                                                class="admin-input" />

                                            <form:errors path="endDate" cssClass="admin-error" />
                                        </div>

                                        <div class="admin-field voucher-grid-full">
                                            <label>
                                                Description
                                            </label>

                                            <form:textarea path="description" rows="5"
                                                cssClass="admin-input admin-textarea" />

                                            <form:errors path="description" cssClass="admin-error" />
                                        </div>

                                    </div>

                                    <div class="admin-form__actions">

                                        <a href="${ctx}/admin/vouchers" class="admin-button admin-button--ghost">
                                            Cancel
                                        </a>

                                        <button type="submit" class="admin-button">
                                            <i class="fa-solid fa-floppy-disk"></i>

                                            ${formMode == 'edit' ? 'Update Voucher' : 'Create Voucher'}
                                        </button>

                                    </div>

                                </form:form>

                            </div>

                        </div>

                    </section>

                </main>

                <script>
                    const voucherForm = document.getElementById("voucherForm");
                    const discountType = document.getElementById("discountType");
                    const discountValueSuffix = document.getElementById("discountValueSuffix");

                    function onlyDigits(value) {
                        if (!value) {
                            return "";
                        }

                        return String(value)
                            .replace(/\.00$/, "")
                            .replace(/,/g, "")
                            .replace(/\./g, "")
                            .replace(/[^\d]/g, "");
                    }

                    function formatInteger(value) {
                        const digits = onlyDigits(value);

                        if (!digits) {
                            return "";
                        }

                        return new Intl.NumberFormat("vi-VN").format(Number(digits));
                    }

                    function normalizeBeforeSubmit(input) {
                        input.value = onlyDigits(input.value);
                    }

                    function setupIntegerInput(input) {
                        input.value = formatInteger(input.value);

                        input.addEventListener("focus", function () {
                            input.value = onlyDigits(input.value);
                        });

                        input.addEventListener("input", function () {
                            input.value = onlyDigits(input.value);
                        });

                        input.addEventListener("blur", function () {
                            input.value = formatInteger(input.value);
                        });
                    }

                    function updateDiscountSuffix() {
                        if (!discountType || !discountValueSuffix) {
                            return;
                        }

                        if (discountType.value === "PERCENT") {
                            discountValueSuffix.textContent = "%";
                        } else {
                            discountValueSuffix.textContent = "₫";
                        }
                    }

                    document.querySelectorAll(".js-integer-money").forEach(setupIntegerInput);

                    if (discountType) {
                        discountType.addEventListener("change", updateDiscountSuffix);
                        updateDiscountSuffix();
                    }

                    if (voucherForm) {
                        voucherForm.addEventListener("submit", function () {
                            document.querySelectorAll(".js-integer-money").forEach(normalizeBeforeSubmit);
                        });
                    }
                </script>

            </body>

            </html>