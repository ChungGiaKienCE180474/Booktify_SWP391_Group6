<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
    <c:when test="${formMode == 'edit'}">
        <c:set var="formAction"
               value="${pageContext.request.contextPath}/admin/vpp/categories/${categoryId}" />
        <c:set var="pageKicker" value="EDIT VPP CATEGORY" />
        <c:set var="pageTitle" value="Edit VPP Category" />
        <c:set var="submitLabel" value="Update Category" />
    </c:when>

    <c:otherwise>
        <c:set var="formAction"
               value="${pageContext.request.contextPath}/admin/vpp/categories" />
        <c:set var="pageKicker" value="CREATE VPP CATEGORY" />
        <c:set var="pageTitle" value="New VPP Category" />
        <c:set var="submitLabel" value="Create Category" />
    </c:otherwise>
</c:choose>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />

    <title>${pageTitle}</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin-dashboard.css" />

    <style>
        .vpp-category-wrapper {
            max-width: 1180px;
        }

        .vpp-category-page-card {
            background: #fff;
            border: 1px solid #E5E7EB;
            border-radius: 18px;
            box-shadow: 0 8px 22px rgba(15, 23, 42, .06);
            padding: 32px 36px;
            margin-bottom: 28px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 24px;
        }

        .vpp-category-kicker {
            margin: 0 0 10px;
            color: #F97316;
            font-size: .9rem;
            font-weight: 900;
            letter-spacing: .22em;
            text-transform: uppercase;
            display: flex;
            align-items: center;
            gap: 9px;
        }

        .vpp-category-title {
            margin: 0;
            color: #111827;
            font-size: 2rem;
            font-weight: 900;
            line-height: 1.2;
        }

        .vpp-category-back-btn {
            min-width: 190px;
            height: 52px;
            border-radius: 10px;
            border: 1px solid #D1D5DB;
            background: #fff;
            color: #374151;
            font-size: 1rem;
            font-weight: 900;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .vpp-category-back-btn:hover {
            background: #F9FAFB;
            color: #111827;
        }

        .vpp-category-card {
            background: #fff;
            border: 1px solid #E5E7EB;
            border-radius: 18px;
            box-shadow: 0 8px 22px rgba(15, 23, 42, .06);
            overflow: hidden;
        }

        .vpp-category-card-header {
            height: 76px;
            padding: 0 32px;
            border-bottom: 1px solid #E5E7EB;
            background: #FAFAFC;
            display: flex;
            align-items: center;
            gap: 12px;
            color: #111827;
            font-size: 1.25rem;
            font-weight: 900;
        }

        .vpp-category-card-header i {
            color: #2563EB;
            font-size: 1.35rem;
        }

        .vpp-category-card-body {
            padding: 34px 32px 26px;
        }

        .vpp-category-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            column-gap: 28px;
            row-gap: 24px;
        }

        .vpp-category-field {
            min-width: 0;
        }

        .vpp-category-field.full {
            grid-column: 1 / -1;
        }

        .vpp-category-label {
            display: block;
            margin-bottom: 10px;
            color: #374151;
            font-size: .95rem;
            font-weight: 900;
            letter-spacing: .12em;
            text-transform: uppercase;
        }

        .vpp-category-label .required {
            color: #EF4444;
        }

        .vpp-category-control {
            width: 100%;
            height: 58px;
            border: 1px solid #D1D5DB;
            border-radius: 10px;
            background: #fff;
            color: #111827;
            padding: 0 20px;
            font-size: 1rem;
            outline: none;
            box-sizing: border-box;
        }

        .vpp-category-control::placeholder {
            color: #9CA3AF;
        }

        .vpp-category-control:focus {
            border-color: #2563EB;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, .12);
        }

        .vpp-category-textarea {
            width: 100%;
            min-height: 170px;
            border: 1px solid #D1D5DB;
            border-radius: 10px;
            background: #fff;
            color: #111827;
            padding: 18px 20px;
            font-size: 1rem;
            outline: none;
            resize: vertical;
            box-sizing: border-box;
            font-family: inherit;
        }

        .vpp-category-textarea::placeholder {
            color: #9CA3AF;
        }

        .vpp-category-textarea:focus {
            border-color: #2563EB;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, .12);
        }

        .vpp-category-help {
            margin-top: 8px;
            color: #6B7280;
            font-size: .92rem;
        }

        .vpp-category-error {
            display: block;
            margin-top: 7px;
            color: #DC2626;
            font-weight: 800;
            font-size: .86rem;
        }

        .vpp-category-global-error {
            margin-top: 22px;
            color: #DC2626;
            font-weight: 800;
        }

        .vpp-category-footer {
            margin-top: 32px;
            padding-top: 22px;
            border-top: 1px solid #E5E7EB;
            display: flex;
            justify-content: flex-end;
            align-items: center;
            gap: 14px;
        }

        .vpp-category-cancel-btn {
            min-width: 140px;
            height: 52px;
            border-radius: 10px;
            border: 1px solid #D1D5DB;
            background: #fff;
            color: #374151;
            font-size: 1rem;
            font-weight: 900;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 9px;
        }

        .vpp-category-cancel-btn:hover {
            background: #F9FAFB;
            color: #111827;
        }

        .vpp-category-submit-btn {
            min-width: 180px;
            height: 52px;
            border: none;
            border-radius: 10px;
            background: #2563EB;
            color: #fff;
            font-size: 1rem;
            font-weight: 900;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 9px;
        }

        .vpp-category-submit-btn:hover {
            background: #1D4ED8;
        }

        @media (max-width: 900px) {
            .vpp-category-page-card {
                align-items: flex-start;
                flex-direction: column;
            }

            .vpp-category-grid {
                grid-template-columns: 1fr;
            }

            .vpp-category-card-body {
                padding: 26px 22px 22px;
            }

            .vpp-category-footer {
                align-items: stretch;
                flex-direction: column-reverse;
            }

            .vpp-category-cancel-btn,
            .vpp-category-submit-btn,
            .vpp-category-back-btn {
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

        <div class="vpp-category-wrapper">

            <div class="vpp-category-page-card">

                <div>
                    <p class="vpp-category-kicker">
                        <i class="fa-solid fa-folder-tree"></i>
                        ${pageKicker}
                    </p>

                    <h2 class="vpp-category-title">
                        ${pageTitle}
                    </h2>
                </div>

                <a href="${pageContext.request.contextPath}/admin/vpp/categories"
                   class="vpp-category-back-btn">
                    <i class="fa-solid fa-arrow-left"></i>
                    Back to Categories
                </a>

            </div>

            <div class="vpp-category-card">

                <div class="vpp-category-card-header">
                    <i class="fa-solid fa-folder-plus"></i>
                    Category Information
                </div>

                <div class="vpp-category-card-body">

                    <form:form modelAttribute="categoryDTO"
                               method="post"
                               action="${formAction}">

                        <input type="hidden"
                               name="${_csrf.parameterName}"
                               value="${_csrf.token}" />

                        <div class="vpp-category-grid">

                            <div class="vpp-category-field full">

                                <label class="vpp-category-label">
                                    Category Name <span class="required">*</span>
                                </label>

                                <form:input path="name"
                                            cssClass="vpp-category-control"
                                            placeholder="Paper, Pen, Notebook..." />

                                <form:errors path="name"
                                             cssClass="vpp-category-error" />

                                <div class="vpp-category-help">
                                   Enter the name of the stationery  
                                   Example: Pens, Note-taking tools, Other.
                                </div>

                            </div>

                            <div class="vpp-category-field full">

                                <label class="vpp-category-label">
                                    Description
                                </label>

                                <form:textarea path="description"
                                               cssClass="vpp-category-textarea"
                                               placeholder="Category description..." />

                                <form:errors path="description"
                                             cssClass="vpp-category-error" />

                            </div>

                        </div>

                        <form:errors cssClass="vpp-category-global-error"
                                     element="div" />

                        <div class="vpp-category-footer">

                            <a href="${pageContext.request.contextPath}/admin/vpp/categories"
                               class="vpp-category-cancel-btn">
                                <i class="fa-solid fa-xmark"></i>
                                Cancel
                            </a>

                            <button type="submit"
                                    class="vpp-category-submit-btn">
                                <i class="fa-solid fa-floppy-disk"></i>
                                ${submitLabel}
                            </button>

                        </div>

                    </form:form>

                </div>

            </div>

        </div>

    </section>

</main>

</body>

</html>