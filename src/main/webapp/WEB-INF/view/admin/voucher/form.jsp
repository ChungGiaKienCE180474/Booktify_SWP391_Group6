<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

            <c:choose>
                <c:when test="${formMode=='edit'}">
                    <c:url var="formAction" value="/admin/vouchers/${voucherId}" />
                </c:when>

                <c:otherwise>
                    <c:url var="formAction" value="/admin/vouchers" />
                </c:otherwise>
            </c:choose>

            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8" />
                <link rel="stylesheet" href="/css/admin-dashboard.css" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

                <title>Voucher</title>
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
                                    ${formMode=='edit'?'Edit Voucher':'Create Voucher'}
                                </p>

                                <h2>
                                    ${formMode=='edit'?'Edit Voucher':'New Voucher'}
                                </h2>
                            </div>

                            <a href="/admin/vouchers" class="admin-button admin-button--ghost">
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
                                    class="admin-form">


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
                                            Discount Value
                                            <span style="color:#EF4444;">*</span>
                                        </label>

                                        <form:input path="discountValue" type="number" step="0.01" min="0.1" max="100"
                                            cssClass="admin-input" placeholder="Example: 10" />

                                        <form:errors path="discountValue" cssClass="admin-error" />
                                    </div>


                                    <div class="admin-field">
                                        <label>
                                            Minimum Order Amount
                                        </label>

                                        <form:input path="minOrderAmount" type="number" step="1000" min="0"
                                            cssClass="admin-input" placeholder="Example: 20000" />
                                        <form:errors path="minOrderAmount" cssClass="admin-error" />
                                    </div>


                                    <div class="admin-field">
                                        <label>
                                            Maximum Order Amount
                                        </label>

                                        <form:input path="maxOrderAmount" type="number" step="1000" min="0"
                                            cssClass="admin-input" placeholder="Example: 100000" />
                                        <form:errors path="maxOrderAmount" cssClass="admin-error" />
                                    </div>


                                    <div class="admin-field">
                                        <label>
                                            Quantity
                                            <span style="color:#EF4444;">*</span>
                                        </label>

                                        <form:input path="quantity" type="number" cssClass="admin-input" />

                                        <form:errors path="quantity" cssClass="admin-error" />
                                    </div>


                                    <div class="admin-field">
                                        <label>
                                            Start Date
                                            <span style="color:#EF4444;">*</span>
                                        </label>

                                        <form:input path="startDate" type="date" cssClass="admin-input" />

                                        <form:errors path="startDate" cssClass="admin-error" />
                                    </div>


                                    <div class="admin-field">
                                        <label>
                                            End Date
                                            <span style="color:#EF4444;">*</span>
                                        </label>

                                        <form:input path="endDate" type="date" cssClass="admin-input" />

                                        <form:errors path="endDate" cssClass="admin-error" />
                                    </div>


                                    <div class="admin-field">

                                        <label>
                                            Description
                                        </label>

                                        <form:textarea path="description" rows="5"
                                            cssClass="admin-input admin-textarea" />

                                        <form:errors path="description" cssClass="admin-error" />

                                    </div>


                                    <div class="admin-form__actions">

                                        <a href="/admin/vouchers" class="admin-button admin-button--ghost">
                                            Cancel
                                        </a>


                                        <button class="admin-button">

                                            <i class="fa-solid fa-floppy-disk"></i>

                                            ${formMode=='edit'
                                            ?'Update Voucher'
                                            :'Create Voucher'}

                                        </button>

                                    </div>


                                </form:form>

                            </div>

                        </div>


                    </section>

                </main>

            </body>

            </html>