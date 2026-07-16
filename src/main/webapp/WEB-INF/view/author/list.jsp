<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

            <link rel="stylesheet" href="/css/header.css" />
            <link rel="stylesheet" href="/css/footer.css" />
            <link rel="stylesheet" href="/css/homepage.css" />

            <link rel="stylesheet" href="/css/author.css" />

            <title>Authors — Booktify</title>
        </head>

        <body class="home-page">

            <jsp:include page="/WEB-INF/view/layout/header.jsp" />

            <div class="author-wrap">

                <div class="author-breadcrumb">
                    <a href="/">Home</a>
                    <i class="fa-solid fa-chevron-right"></i>
                    <span>Authors</span>
                </div>

                <div class="author-header">
                    <div>
                        <h2>Authors</h2>
                        <p>${authors.size()} authors</p>
                    </div>
                </div>

                <div class="author-grid">

                    <c:choose>
                        <c:when test="${empty authors}">
                            <div class="empty-state">
                                <i class="fa-solid fa-user-slash"></i>
                                <p>Không có tác giả nào.</p>
                            </div>
                        </c:when>

                        <c:otherwise>
                            <c:forEach items="${authors}" var="a">
                                <a href="/authors/${a.authorId}" class="author-card">

                                    <div class="author-img">
                                        <c:choose>
                                            <c:when test="${not empty a.profileImage}">
                                                <img src="${a.profileImage}" alt="${a.authorName}" />
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fa-solid fa-user"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="author-body">
                                        <div class="author-name">${a.authorName}</div>
                                        <div class="author-nationality">
                                            ${a.nationality}
                                        </div>

                                        <span class="author-btn">Chi tiết</span>
                                    </div>

                                </a>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>

                </div>

            </div>

            <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

        </body>

        </html>