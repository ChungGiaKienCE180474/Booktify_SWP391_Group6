<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:choose>
    <c:when test="${formMode == 'edit'}"><c:url var="formAction" value="/admin/genres/${genre.id}" /></c:when>
    <c:otherwise><c:url var="formAction" value="/admin/genres" /></c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />
    <title><c:choose><c:when test="${formMode=='edit'}">Edit Genre</c:when><c:otherwise>Create Genre</c:otherwise></c:choose> — Booktify Admin</title>
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
                        <c:choose><c:when test="${formMode=='edit'}">Edit Genre</c:when><c:otherwise>Create Genre</c:otherwise></c:choose>
                    </p>
                    <h2><c:choose><c:when test="${formMode=='edit'}">Edit Genre</c:when><c:otherwise>New Genre</c:otherwise></c:choose></h2>
                </div>
                <a href="/admin/genres" class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-arrow-left"></i> Back to Genres
                </a>
            </div>

            <%-- Form Card --%>
            <div class="admin-form-card">
                <div class="admin-form-card__header">
                    <i class="fa-solid fa-pen-to-square"></i>
                    <h3>Genre Information</h3>
                </div>
                <div class="admin-form-card__body">
                    <form:form modelAttribute="genre" action="${formAction}" method="post"
                               class="admin-form" id="genreForm">

                        <%-- Genre Name --%>
                        <div class="admin-field">
                            <label>Genre Name <span style="color:#EF4444;">*</span></label>
                            <form:input path="name" cssClass="admin-input" placeholder="Enter genre name…" />
                            <form:errors path="name" cssClass="admin-error" />
                        </div>

                        <%-- Category --%>
                        <div class="admin-field">
                            <label>Category <span style="color:#EF4444;">*</span></label>
                            <select name="categoryId" id="categoryId" class="admin-input" required>
                                <option value="">— Select a category —</option>
                                <c:forEach items="${categories}" var="cat">
                                    <option value="${cat.id}"
                                        <c:if test="${not empty genre.category and genre.category.id == cat.id}">selected</c:if>>
                                        <c:out value="${cat.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty categoryError}">
                                <span class="admin-error">
                                    <i class="fa-solid fa-circle-exclamation"></i>
                                    <c:out value="${categoryError}"/>
                                </span>
                            </c:if>
                            <c:if test="${empty categoryError}">
                                <span class="admin-error" id="categoryErrorMsg" style="display:none;">
                                    <i class="fa-solid fa-circle-exclamation"></i>
                                    Please select a category.
                                </span>
                            </c:if>
                        </div>

                        <%-- Description --%>
                        <div class="admin-field">
                            <label>Description</label>
                            <form:textarea path="description" cssClass="admin-input admin-textarea"
                                           rows="4" placeholder="Optional — describe this genre…" />
                            <form:errors path="description" cssClass="admin-error" />
                        </div>

                        <%-- Edit metadata --%>
                        <c:if test="${formMode == 'edit'}">
                            <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;padding:14px 16px;
                                        background:#F9FAFB;border:1px solid #E5E7EB;border-radius:8px;">
                                <div>
                                    <div style="font-size:.7rem;font-weight:700;text-transform:uppercase;
                                                letter-spacing:.08em;color:#9CA3AF;margin-bottom:4px;">Created</div>
                                    <div style="font-size:.875rem;color:#374151;font-weight:500;">
                                        <c:out value="${genre.createdAtString}" default="—"/>
                                    </div>
                                </div>
                                <div>
                                    <div style="font-size:.7rem;font-weight:700;text-transform:uppercase;
                                                letter-spacing:.08em;color:#9CA3AF;margin-bottom:4px;">Last Updated</div>
                                    <div style="font-size:.875rem;color:#374151;font-weight:500;">
                                        <c:out value="${genre.updatedAtString}" default="—"/>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <%-- Active --%>
                        <div class="admin-field admin-field--inline">
                            <label class="admin-checkbox">
                                <form:checkbox path="active" />
                                Active (visible to users)
                            </label>
                        </div>

                        <%-- Actions --%>
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

    <script>
        // Client-side validation: category required
        document.getElementById('genreForm').addEventListener('submit', function (e) {
            var cat = document.getElementById('categoryId');
            var err = document.getElementById('categoryErrorMsg');
            if (cat && !cat.value) {
                e.preventDefault();
                if (err) { err.style.display = 'flex'; }
                cat.style.borderColor = '#EF4444';
                cat.focus();
            }
        });
        var cat = document.getElementById('categoryId');
        if (cat) {
            cat.addEventListener('change', function () {
                if (this.value) {
                    var err = document.getElementById('categoryErrorMsg');
                    if (err) err.style.display = 'none';
                    this.style.borderColor = '';
                }
            });
        }
    </script>
</body>
</html>
