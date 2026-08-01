<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />
    <link rel="stylesheet" href="${ctx}/css/rating.css" />

    <title>Reviews — Booktify Staff</title>
</head>

<body class="admin-shell">

    <jsp:include page="/WEB-INF/view/layout/staff/sidebar.jsp" />

    <main class="admin-main">

        <jsp:include page="/WEB-INF/view/layout/staff/header.jsp" />

        <section class="admin-content">

            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker">
                        <i class="fa-solid fa-star"></i>
                        Review Management
                    </p>
                    <h2>Products</h2>
                </div>
            </div>

            <div class="admin-panel" style="padding: 14px 22px;">
                <form method="get" action="${ctx}/staff/reviews" class="admin-search-form" style="flex-wrap: wrap;">
                    <div style="position: relative; flex: 1; max-width: 380px;">
                        <i class="fa-solid fa-magnifying-glass"
                            style="position: absolute; left: 13px; top: 50%; transform: translateY(-50%); color: #9CA3AF; font-size: .82rem; pointer-events: none;"></i>
                        <input type="text" name="keyword" value="${keyword}"
                            placeholder="Search by book title or author..." class="admin-input"
                            style="padding-left: 38px;">
                    </div>

                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-filter"></i>
                        Filter
                    </button>

                    <a href="${ctx}/staff/reviews" class="admin-button admin-button--ghost">
                        <i class="fa-solid fa-rotate-right"></i>
                        Reset
                    </a>
                </form>
            </div>

            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th style="width: 60px; text-align: center;">#</th>
                            <th style="width: 45%;">Product</th>
                            <th style="width: 220px;">Reviews</th>
                            <th style="width: 120px; text-align: center;">Action</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:set var="index" value="0" />

                        <%-- Books List --%>
                        <c:forEach items="${books}" var="book">
                            <c:set var="index" value="${index + 1}" />
                            <tr>
                                <td style="text-align: center; color: #9CA3AF; font-weight: 600;">
                                    ${index}
                                </td>
                                <td>
                                    <div class="book-title">
                                        <c:out value="${book.title}" />
                                    </div>
                                    <c:if test="${not empty book.author}">
                                        <div class="book-author">
                                            <c:out value="${book.author.authorName}" />
                                        </div>
                                    </c:if>
                                </td>

                                <td>
                                    <div class="review-summary">
                                        <c:choose>
                                            <c:when test="${reviewCounts[book.id] > 0}">
                                                <div class="review-count">
                                                    ${reviewCounts[book.id]} Reviews
                                                </div>

                                                <div class="review-star">
                                                    <c:forEach begin="1" end="5" var="i">
                                                        <c:choose>
                                                            <c:when test="${i <= averageRatings[book.id]}">★
                                                            </c:when>
                                                            <c:otherwise>☆</c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>

                                                    <span style="color:#374151;margin-left:5px;">
                                                        ${averageRatings[book.id]}
                                                    </span>
                                                </div>
                                            </c:when>

                                            <c:otherwise>
                                                <div class="review-count">0 Reviews</div>
                                                <div class="no-rating">No ratings</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>

                                <td class="admin-table__actions">
                                    <a href="${ctx}/staff/reviews/${book.id}" class="icon-link"
                                        title="View Reviews">
                                        <i class="fa-solid fa-comments"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>

                        <%-- Empty State --%>
                        <c:if test="${empty books}">
                            <tr>
                                <td colspan="4"
                                    style="text-align: center; padding: 56px 20px; color: #9CA3AF;">
                                    <i class="fa-solid fa-star"
                                        style="font-size: 2.2rem; display: block; margin-bottom: 10px; opacity: .3;"></i>
                                    No review data found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

        </section>

    </main>

</body>

</html>
