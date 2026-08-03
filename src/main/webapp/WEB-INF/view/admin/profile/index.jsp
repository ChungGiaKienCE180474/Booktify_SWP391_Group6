<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
    <link rel="stylesheet" href="/css/profile.css" />
    <title>My Profile — Booktify Admin</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

        <section class="admin-content profile-section">
            <c:set var="profileBase" value="/admin/profile" scope="request" />
            <c:set var="adminProfile" value="true" scope="request" />
            <jsp:include page="/WEB-INF/view/profile/_content.jsp" />
        </section>
    </main>

    <script>
        if (window.location.hash === '#password-section') {
            document.getElementById('password-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    </script>
</body>
</html>
