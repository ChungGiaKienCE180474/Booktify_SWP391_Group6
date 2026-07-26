<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

            <link rel="stylesheet" href="/css/admin-dashboard.css" />
            <link rel="stylesheet" href="/css/rating.css" />


            <title>Review Detail — Booktify Admin</title>

        </head>

        <body class="admin-shell">

            <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

            <main class="admin-main">

                <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

                <section class="admin-content">

                    <div class="admin-toolbar">

                        <div>
                            <p class="admin-kicker">
                                <i class="fa-solid fa-star"></i>
                                Review Management
                            </p>

                            <h2>Review Details</h2>
                        </div>

                        <a href="/admin/reviews" class="admin-button admin-button--ghost">
                            <i class="fa-solid fa-arrow-left"></i>
                            Back
                        </a>

                    </div>


                    <div class="book-info-card">

                        <c:choose>

                            <c:when test="${not empty book.imageUrl}">
                                <img class="book-cover" src="<c:out value='${book.imageUrl}'/>" alt="Book">
                            </c:when>

                            <c:otherwise>
                                <div class="book-cover"
                                    style="display:flex;align-items:center;justify-content:center;color:#9CA3AF;">
                                    <i class="fa-regular fa-image"></i>
                                </div>
                            </c:otherwise>

                        </c:choose>


                        <div>

                            <div class="book-title">
                                <c:out value="${book.title}" />
                            </div>

                            <c:if test="${not empty book.author}">
                                <div class="book-author">
                                    Author:
                                    <c:out value="${book.author.authorName}" />
                                </div>
                            </c:if>

                            <div class="review-summary">

                                <span class="stars">
                                    <c:forEach begin="1" end="5" var="i">
                                        <c:choose>
                                            <c:when test="${i <= averageRating}">★</c:when>
                                            <c:otherwise>☆</c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </span>

                                <span class="review-count">
                                    ${averageRating} / 5 - ${reviewCount} Reviews
                                </span>

                            </div>

                        </div>

                    </div>


                    <div class="admin-table-wrap">

                        <table class="admin-table">

                            <thead>
                                <tr>
                                    <th style="width:50px;">#</th>
                                    <th>Customer</th>
                                    <th>Rating</th>
                                    <th>Review</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th style="width:120px;">Action</th>
                                </tr>
                            </thead>

                            <tbody>

                                <c:forEach items="${ratings}" var="rating" varStatus="vs">

                                    <tr>

                                        <td style="color:#9CA3AF;font-weight:600;">
                                            ${vs.index + 1}
                                        </td>

                                        <td>
                                            <strong>
                                                <c:out value="${rating.customer.fullName}" />
                                            </strong>
                                        </td>

                                        <td>
                                            <span class="stars">
                                                <c:forEach begin="1" end="5" var="i">
                                                    <c:choose>
                                                        <c:when test="${i <= rating.ratingValue}">★</c:when>
                                                        <c:otherwise>☆</c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </span>
                                        </td>

                                        <td>
                                            <div class="review-content">
                                                <c:out value="${rating.review}" />
                                            </div>
                                        </td>

                                        <td>
                                            <c:out value="${rating.createdAtFormatted}" />
                                        </td>

                                        <td>
                                            <c:choose>

                                                <c:when test="${rating.status == 'ACTIVE'}">

                                                    <span class="status-visible">
                                                        Visible
                                                    </span>

                                                </c:when>


                                                <c:when test="${rating.status == 'HIDDEN'}">

                                                    <span class="status-hidden">
                                                        Hidden
                                                    </span>

                                                </c:when>


                                                <c:when test="${rating.status == 'DELETED'}">

                                                    <span class="status-hidden">
                                                        Deleted
                                                    </span>

                                                </c:when>


                                            </c:choose>
                                        </td>

                                        <td class="admin-table__actions">

                                            <c:choose>

                                                <%-- Review đang ACTIVE thì Admin được Hide --%>
                                                    <c:when test="${rating.status == 'ACTIVE'}">

                                                        <form method="post"
                                                            action="/admin/reviews/book/${rating.ratingId}/hide">

                                                            <input type="hidden" name="${_csrf.parameterName}"
                                                                value="${_csrf.token}" />

                                                            <input type="hidden" name="bookId" value="${book.id}" />

                                                            <button type="submit" class="icon-link icon-link--hide"
                                                                title="Hide Review">

                                                                <i class="fa-solid fa-eye-slash"></i>

                                                            </button>

                                                        </form>

                                                    </c:when>


                                                    <%-- Review Hidden nhưng do ADMIN hide thì cho Visible lại --%>
                                                        <c:when test="${rating.deletedBy == 'ADMIN'}">

                                                            <form method="post"
                                                                action="/admin/reviews/book/${rating.ratingId}/visible">

                                                                <input type="hidden" name="${_csrf.parameterName}"
                                                                    value="${_csrf.token}" />

                                                                <input type="hidden" name="bookId" value="${book.id}" />

                                                                <button type="submit"
                                                                    class="icon-link icon-link--visible"
                                                                    title="Visible Review">

                                                                    <i class="fa-solid fa-eye"></i>

                                                                </button>

                                                            </form>

                                                        </c:when>


                                                        <%-- Review Hidden do CUSTOMER xóa thì không có Action --%>
                                                            <c:otherwise>

                                                                <span style="color:#9CA3AF;">
                                                                    -
                                                                </span>

                                                            </c:otherwise>


                                            </c:choose>

                                        </td>

                                    </tr>

                                </c:forEach>


                                <c:if test="${empty ratings}">
                                    <tr>
                                        <td colspan="7" style="text-align:center;padding:50px;color:#9CA3AF;">
                                            No reviews found.
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