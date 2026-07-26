<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
            <link rel="stylesheet" href="/css/header.css">
            <link rel="stylesheet" href="/css/footer.css">
            <link rel="stylesheet" href="/css/homepage.css">
            <!-- CSS specific to Author Detail -->
            <link rel="stylesheet" href="/css/author.css">
            <title>${author.authorName} — Booktify</title>
        </head>

        <body class="home-page">

            <jsp:include page="/WEB-INF/view/layout/header.jsp" />

            <div class="author-wrap">
                <nav class="author-breadcrumb">
                    <a href="/">Home</a>
                    <i class="fa-solid fa-chevron-right"></i>
                    <a href="/authors">Authors</a>
                    <i class="fa-solid fa-chevron-right"></i>
                    <span>${author.authorName}</span>
                </nav>

                <div class="author-detail">
                    <div class="author-img">
                        <c:choose>
                            <c:when test="${not empty author.profileImage}">
                                <img src="${author.profileImage}" alt="${author.authorName}">
                            </c:when>
                            <c:otherwise>
                                <div class="author-no-img">
                                    <i class="fa-solid fa-user"></i>
                                    <span>No Image</span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="author-info">
                        <h1>${author.authorName}</h1>
                        <div class="author-nationality">
                            ${author.nationality}
                        </div>
                        <div class="author-biography">
                            ${author.biography}
                        </div>
                    </div>
                </div>
            </div>


            <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

        </body>

        </html>