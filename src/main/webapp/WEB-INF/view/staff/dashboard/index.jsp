<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />

    <title>Dashboard — Booktify Staff</title>

    <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/staff/sidebar.jsp" />

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/staff/header.jsp" />

    <section class="admin-content">

        <div class="admin-hero">
            <div>
                <p class="admin-kicker">Welcome Back</p>

                <h2>Dashboard Overview</h2>

                <p>
                    Monitor staff modules including customer contact requests,
                    stationery VPP items, and supplier management from one place.
                </p>
            </div>
        </div>

        <section class="admin-cards">

            <div class="admin-card">
                <div class="admin-card__icon">
                    <i class="fa-solid fa-headset"></i>
                </div>

                <div class="admin-card__body">
                    <span>Contact Requests</span>
                    <strong>
                        <c:out value="${totalContacts}" default="0" />
                    </strong>
                </div>
            </div>

            <div class="admin-card">
                <div class="admin-card__icon">
                    <i class="fa-solid fa-pen-ruler"></i>
                </div>

                <div class="admin-card__body">
                    <span>Stationery VPP</span>
                    <strong>
                        <c:out value="${totalVppItems}" default="0" />
                    </strong>
                </div>
            </div>

            <div class="admin-card">
                <div class="admin-card__icon">
                    <i class="fa-solid fa-truck-field"></i>
                </div>

                <div class="admin-card__body">
                    <span>Suppliers</span>
                    <strong>
                        <c:out value="${totalSuppliers}" default="0" />
                    </strong>
                </div>
            </div>

        </section>

    </section>

</main>

</body>
</html>