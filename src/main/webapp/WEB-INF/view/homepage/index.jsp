<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="description" content="Booktify - Shop books online and explore the world of knowledge" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/homepage.css" />
    <title>Booktify - Home</title>
</head>

<body class="home-page">

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">

        <section class="hero">
            <div class="hero-inner">
                <div class="hero-text">
                    <h1>Explore the World of Books with Booktify</h1>
                    <p>Thousands of titles from literature and business to self-help — fast delivery, great prices, and a simple shopping experience.</p>
                    <div class="hero-actions">
                        <c:choose>
                            <c:when test="${not empty username}">
                                <a href="#" class="btn-hero btn-hero-primary">Browse Books</a>
                            </c:when>
                            <c:otherwise>
                                <a href="/register" class="btn-hero btn-hero-primary">Get Started</a>
                                <a href="/login" class="btn-hero btn-hero-secondary">Sign In</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="hero-visual">
                    <div class="hero-books">
                        <div class="book-card-visual">
                            <i class="fa-solid fa-book"></i>
                            <span>Literature</span>
                        </div>
                        <div class="book-card-visual">
                            <i class="fa-solid fa-chart-line"></i>
                            <span>Business</span>
                        </div>
                        <div class="book-card-visual">
                            <i class="fa-solid fa-child"></i>
                            <span>Children</span>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section class="section">
            <div class="section-header">
                <h2>Featured Categories</h2>
                <p>Choose your favorite genre</p>
            </div>
            <div class="category-grid">
                <a href="#" class="category-card">
                    <i class="fa-solid fa-feather-pointed"></i>
                    <h3>Literature</h3>
                </a>
                <a href="#" class="category-card">
                    <i class="fa-solid fa-briefcase"></i>
                    <h3>Business</h3>
                </a>
                <a href="#" class="category-card">
                    <i class="fa-solid fa-children"></i>
                    <h3>Children</h3>
                </a>
                <a href="#" class="category-card">
                    <i class="fa-solid fa-lightbulb"></i>
                    <h3>Self-Help</h3>
                </a>
                <a href="#" class="category-card">
                    <i class="fa-solid fa-flask"></i>
                    <h3>Science</h3>
                </a>
                <a href="#" class="category-card">
                    <i class="fa-solid fa-language"></i>
                    <h3>Languages</h3>
                </a>
            </div>
        </section>

        <section class="section" style="padding-top: 0;">
            <div class="section-header">
                <h2>Why Choose Booktify?</h2>
                <p>A trusted online book shopping experience</p>
            </div>
            <div class="features-grid">
                <article class="feature-card">
                    <div class="feature-icon"><i class="fa-solid fa-truck-fast"></i></div>
                    <h3>Fast Delivery</h3>
                    <p>Orders are processed and shipped quickly nationwide.</p>
                </article>
                <article class="feature-card">
                    <div class="feature-icon"><i class="fa-solid fa-shield-halved"></i></div>
                    <h3>Secure Payments</h3>
                    <p>Your information is protected with convenient payment options.</p>
                </article>
                <article class="feature-card">
                    <div class="feature-icon"><i class="fa-solid fa-rotate-left"></i></div>
                    <h3>Easy Returns</h3>
                    <p>Flexible return policy if books do not match the description.</p>
                </article>
            </div>

            <c:if test="${empty username}">
                <div class="cta-banner">
                    <h2>Join Booktify Today</h2>
                    <p>Create an account to unlock deals and track your orders with ease.</p>
                    <a href="/register" class="btn-hero btn-hero-primary">Sign Up for Free</a>
                </div>
            </c:if>
        </section>

    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

    <script>
        document.getElementById('navToggle')?.addEventListener('click', function () {
            document.querySelector('.main-nav')?.classList.toggle('open');
        });
    </script>
</body>

</html>
