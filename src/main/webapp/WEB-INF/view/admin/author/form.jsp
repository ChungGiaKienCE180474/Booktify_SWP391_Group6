<%-- Author form.jsp (layout based on new Category page) --%>
    <%@ page contentType="text/html" pageEncoding="UTF-8" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
            <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

                <c:choose>
                    <c:when test="${formMode=='edit'}">
                        <c:url var="formAction" value="/admin/authors/${authorId}" />
                    </c:when>
                    <c:otherwise>
                        <c:url var="formAction" value="/admin/authors" />
                    </c:otherwise>
                </c:choose>

                <!DOCTYPE html>
                <html>

                <head>
                    <meta charset="UTF-8" />
                    <link rel="stylesheet" href="/css/admin-dashboard.css" />
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
                    <title>Author</title>
                </head>

                <body class="admin-shell">
                    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />
                    <main class="admin-main">
                        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />
                        <section class="admin-content">
                            <div class="admin-toolbar">
                                <div>
                                    <p class="admin-kicker"><i class="fa-solid fa-feather"></i> ${formMode=='edit'?'Edit
                                        Author':'Create Author'}</p>
                                    <h2>${formMode=='edit'?'Edit Author':'New Author'}</h2>
                                </div>
                                <a href="/admin/authors" class="admin-button admin-button--ghost"><i
                                        class="fa-solid fa-arrow-left"></i> Back</a>
                            </div>
                            <div class="admin-form-card">
                                <div class="admin-form-card__header"><i class="fa-solid fa-feather"></i>
                                    <h3>Author Information</h3>
                                </div>
                                <div class="admin-form-card__body">
                                    <form:form modelAttribute="authorDTO" action="${formAction}" method="post"
                                        class="admin-form">
                                        <c:if test="${formMode=='edit'}">
                                            <form:hidden path="authorId" />
                                        </c:if>
                                        <div class="admin-field"><label>Author Name <span style="color:#EF4444;">*</span></label>
                                            <form:input path="authorName" cssClass="admin-input" />
                                            <form:errors path="authorName" cssClass="admin-error" />
                                        </div>
                                        <div class="admin-field"><label>Biography <span style="color:#EF4444;">*</span></label>
                                            <form:textarea path="biography" rows="5"
                                                cssClass="admin-input admin-textarea" />
                                            <form:errors path="biography" cssClass="admin-error" />
                                        </div>
                                        <div class="admin-field"><label>Nationality <span style="color:#EF4444;">*</span></label>
                                            <form:input path="nationality" cssClass="admin-input" />
                                            <form:errors path="nationality" cssClass="admin-error" />
                                        </div>
                                        <div class="admin-field"><label>Profile Image</label>
                                            <form:input id="profileImage" path="profileImage" cssClass="admin-input" />
                                            <img id="previewImage"
                                                style="margin-top:10px;max-width:180px;display:none;border-radius:8px;border:1px solid #ddd;">
                                            <form:errors path="profileImage" cssClass="admin-error" />
                                        </div>
                                        <div class="admin-form__actions">
                                            <a href="/admin/authors" class="admin-button admin-button--ghost">Cancel</a>
                                            <button class="admin-button"><i class="fa-solid fa-floppy-disk"></i>
                                                ${formMode=='edit'?'Update Author':'Create Author'}</button>
                                        </div>
                                    </form:form>
                                </div>
                            </div>
                        </section>
                    </main>
                    <script>
                        const i = document.getElementById('profileImage'), p = document.getElementById('previewImage');
                        function u() { if (i.value.trim()) { p.src = i.value; p.style.display = 'block'; } else p.style.display = 'none'; }
                        i.addEventListener('input', u); u();
                    </script>
                </body>

                </html>