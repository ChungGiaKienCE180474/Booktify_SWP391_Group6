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

            <title>Authors — Booktify</title>

            <style>
                .author-wrap {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 1.5rem;
                }

                /* breadcrumb */
                .author-breadcrumb {
                    font-size: .85rem;
                    color: var(--text-muted);
                    margin-bottom: 1rem;
                }

                .author-breadcrumb a {
                    text-decoration: none;
                    color: var(--text-muted);
                }

                .author-breadcrumb a:hover {
                    color: var(--primary);
                }

                /* header */
                .author-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 1.2rem;
                    flex-wrap: wrap;
                }

                .author-header h2 {
                    margin: 0;
                    color: var(--primary);
                    font-size: 1.4rem;
                    font-weight: 800;
                }

                .author-header p {
                    margin: .2rem 0 0;
                    color: var(--text-muted);
                    font-size: .85rem;
                }

                /* grid */
                .author-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
                    gap: 1rem;
                }

                /* card */
                .author-card {
                    background: #fff;
                    border: 1px solid var(--border);
                    border-radius: var(--radius);
                    overflow: hidden;
                    text-decoration: none;
                    color: inherit;
                    display: flex;
                    flex-direction: column;
                    transition: .2s;
                }

                .author-card:hover {
                    transform: translateY(-5px);
                    box-shadow: var(--shadow-lg);
                }

                .author-img {
                    height: 220px;
                    background: linear-gradient(135deg, #ECEFF1, #CFD8DC);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    overflow: hidden;
                }

                .author-img img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }

                .author-no-img {
                    text-align: center;
                    color: #90A4AE;
                }

                .author-no-img i {
                    font-size: 3rem;
                    display: block;
                    margin-bottom: .3rem;
                }

                .author-body {
                    padding: .8rem;
                    display: flex;
                    flex-direction: column;
                    flex: 1;
                }

                .author-name {
                    font-size: 1rem;
                    font-weight: 700;
                    margin-bottom: .2rem;
                    color: var(--text);
                }

                .author-nationality {
                    font-size: .8rem;
                    color: var(--text-muted);
                    margin-bottom: .8rem;
                }

                .author-btn {
                    margin-top: auto;
                    display: inline-block;
                    padding: .4rem .7rem;
                    background: var(--primary);
                    color: #fff;
                    font-size: .75rem;
                    font-weight: 700;
                    text-align: center;
                    border-radius: 6px;
                    text-decoration: none;
                    transition: .15s;
                }

                .author-btn:hover {
                    background: var(--primary-light);
                }

                /* empty */
                .empty-state {
                    text-align: center;
                    padding: 4rem 1rem;
                    color: var(--text-muted);
                    grid-column: 1/-1;
                }

                .empty-state i {
                    font-size: 3rem;
                    margin-bottom: 1rem;
                    opacity: .3;
                }

                @media(max-width:768px) {
                    .author-grid {
                        grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
                    }
                }
            </style>
        </head>

        <body>

            <jsp:include page="/WEB-INF/view/layout/header.jsp" />

            <div class="author-wrap">

                <!-- breadcrumb -->
                <div class="author-breadcrumb">
                    <a href="/">Home</a> <i class="fa-solid fa-chevron-right"></i>   Authors
                </div>

                <!-- header -->
                <div class="author-header">
                    <div>
                        <h2>Authors</h2>
                        <p>${authors.size()} authors</p>
                    </div>
                </div>

                <!-- grid -->
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
                                                <div class="author-no-img">
                                                    <i class="fa-solid fa-user"></i>
                                                    <span>No Image</span>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="author-body">
                                        <div class="author-name">${a.authorName}</div>
                                        <div class="author-nationality">
                                            ${not empty a.nationality ? a.nationality : "Unknown"}
                                        </div>

                                        <span class="author-btn">View Details</span>
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