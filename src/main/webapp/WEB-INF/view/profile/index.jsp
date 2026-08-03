<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/profile.css" />
    <title>Booktify - My profile</title>
</head>

<body class="home-page">

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">
        <section class="section profile-section">
            <c:set var="profileBase" value="/profile" scope="request" />
            <c:set var="adminProfile" value="false" scope="request" />
            <jsp:include page="/WEB-INF/view/profile/_content.jsp" />
        </section>
    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

    <script>
        document.getElementById('navToggle')?.addEventListener('click', function () {
            document.querySelector('.main-nav')?.classList.toggle('open');
        });

        if (window.location.hash === '#password-section') {
            document.getElementById('password-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    </script>
</body>

</html>
