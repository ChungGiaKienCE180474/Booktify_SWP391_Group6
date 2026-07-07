<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

            <link rel="stylesheet" href="/css/header.css">
            <link rel="stylesheet" href="/css/footer.css">
            <link rel="stylesheet" href="/css/homepage.css">

            <title>${author.authorName} — Booktify</title>

            <style>
                /* ==========================
           Wrapper
        ========================== */

                .author-wrap {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 1.5rem 1.5rem 3rem;
                }

                /* ==========================
           Breadcrumb
        ========================== */

                .author-breadcrumb {
                    display: flex;
                    align-items: center;
                    gap: .45rem;
                    font-size: .8rem;
                    color: var(--text-muted);
                    margin-bottom: 1.2rem;
                    flex-wrap: wrap;
                }

                .author-breadcrumb a {
                    color: var(--text-muted);
                    text-decoration: none;
                    transition: .2s;
                }

                .author-breadcrumb a:hover {
                    color: var(--primary);
                }

                .author-breadcrumb i {
                    font-size: .6rem;
                    opacity: .4;
                }

                /* ==========================
           Detail
        ========================== */

                .author-detail {

                    display: grid;
                    grid-template-columns: 280px 1fr;
                    gap: 2.5rem;

                    background: #fff;
                    border: 1px solid var(--border);
                    border-radius: var(--radius-lg);

                    padding: 2rem;

                    box-shadow: var(--shadow-sm);

                    align-items: start;

                }

                /* ==========================
           Image
        ========================== */

                .author-img {
                    width: 100%;
                }

                .author-img img {
                    width: 100%;
                    height: 320px;
                    /* cố định chiều cao */
                    object-fit: cover;
                    border-radius: 12px;
                    display: block;
                }

                .author-no-img {

                    aspect-ratio: 2/3;

                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;

                    border: 2px dashed #ddd;

                    border-radius: 12px;

                    color: #999;

                }

                .author-no-img i {

                    font-size: 60px;
                    margin-bottom: 10px;

                }

                /* ==========================
           Info
        ========================== */

                .author-info h1 {

                    margin: 0;

                    font-size: 2.2rem;

                    font-weight: 800;

                    color: var(--text);

                }

                .author-nationality {

                    margin-top: 12px;
                    margin-bottom: 25px;

                    color: var(--text-muted);

                    font-size: .95rem;

                }

                .author-biography {

                    line-height: 1.9;

                    color: var(--text);

                    font-size: .95rem;

                    text-align: justify;

                }

                /* ==========================
           Responsive
        ========================== */

                @media(max-width:768px) {

                    .author-detail {

                        grid-template-columns: 1fr;

                        gap: 1.5rem;

                    }

                    .author-img {

                        max-width: 260px;

                        margin: auto;

                    }

                    .author-info h1 {

                        font-size: 1.8rem;

                    }

                }
            </style>

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