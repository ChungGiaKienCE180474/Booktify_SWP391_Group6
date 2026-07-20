<%-- Create/Edit Genre form. Category here is only for organizing genres in
     the admin UI — it doesn't restrict which books this genre can attach to.
     Layout đồng nhất với Book management (admin-form-card + admin-form-grid + admin-field). --%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:choose>
    <c:when test="${formMode == 'edit'}">
        <c:url var="formAction" value="/admin/genres/${genre.id}" />
    </c:when>
    <c:otherwise>
        <c:url var="formAction" value="/admin/genres" />
    </c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <title>
        <c:choose>
            <c:when test="${formMode=='edit'}">Edit Genre</c:when>
            <c:otherwise>Create Genre</c:otherwise>
        </c:choose> — Booktify Admin
    </title>
</head>

<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content">

            <%-- Page Header --%>
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker">
                        <i class="fa-solid fa-layer-group"></i>
                        <c:choose>
                            <c:when test="${formMode=='edit'}">Edit Genre</c:when>
                            <c:otherwise>Create Genre</c:otherwise>
                        </c:choose>
                    </p>
                    <h2>
                        <c:choose>
                            <c:when test="${formMode=='edit'}">Edit Genre</c:when>
                            <c:otherwise>New Genre</c:otherwise>
                        </c:choose>
                    </h2>
                </div>
                <a href="/admin/genres" class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-arrow-left"></i> Back to Genres
                </a>
            </div>

            <%-- Form Card --%>
            <div class="admin-form-card">
                <div class="admin-form-card__header">
                    <i class="fa-solid fa-file-pen"></i>
                    <h3>Genre Information</h3>
                </div>
                <div class="admin-form-card__body">
                    <form:form modelAttribute="genre" action="${formAction}" method="post"
                        class="admin-form" id="genreForm">

                        <%-- Name & Category --%>
                        <div class="admin-form-grid">
                            <div class="admin-field">
                                <label>Genre Name <span style="color:#EF4444;">*</span></label>
                                <form:input path="name" cssClass="admin-input" placeholder="Enter genre name…" />
                                <form:errors path="name" cssClass="admin-error" />
                            </div>
                            <div class="admin-field">
                                <label>Category
                                    <span style="color:#9CA3AF;font-size:.78rem;font-weight:500;">— Optional, for organization only</span>
                                </label>
                                <select name="categoryId" id="categoryId" class="admin-input">
                                    <option value="">— None —</option>
                                    <c:forEach items="${categories}" var="cat">
                                        <option value="${cat.id}"
                                            <c:if test="${not empty genre.category and genre.category.id == cat.id}">selected</c:if>>
                                            <c:out value="${cat.name}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <%-- Description --%>
                        <div class="admin-field">
                            <label>Description</label>
                            <form:textarea path="description" cssClass="admin-input admin-textarea" rows="5"
                                placeholder="Optional — describe this genre…" />
                            <form:errors path="description" cssClass="admin-error" />
                        </div>

                        <%-- Edit metadata --%>
                        <c:if test="${formMode == 'edit'}">
                            <div class="admin-field">
                                <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;padding:14px 16px;background:#F9FAFB;border:1px solid #E5E7EB;border-radius:8px;">
                                    <div>
                                        <div style="font-size:.7rem;font-weight:700;text-transform:uppercase;letter-spacing:.08em;color:#9CA3AF;margin-bottom:4px;">Created</div>
                                        <div style="font-size:.875rem;color:#374151;font-weight:500;">
                                            <c:out value="${genre.createdAtString}" default="—" />
                                        </div>
                                    </div>
                                    <div>
                                        <div style="font-size:.7rem;font-weight:700;text-transform:uppercase;letter-spacing:.08em;color:#9CA3AF;margin-bottom:4px;">Last Updated</div>
                                        <div style="font-size:.875rem;color:#374151;font-weight:500;">
                                            <c:out value="${genre.updatedAtString}" default="—" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <%-- Action Buttons --%>
                        <div class="admin-form__actions">
                            <a href="/admin/genres" class="admin-button admin-button--ghost">
                                <i class="fa-solid fa-xmark"></i> Cancel
                            </a>
                            <button type="submit" class="admin-button">
                                <i class="fa-solid fa-floppy-disk"></i>
                                <c:choose>
                                    <c:when test="${formMode=='edit'}">Update Genre</c:when>
                                    <c:otherwise>Create Genre</c:otherwise>
                                </c:choose>
                            </button>
                        </div>

                    </form:form>
                </div>
            </div>

        </section>
    </main>
</body>

</html>