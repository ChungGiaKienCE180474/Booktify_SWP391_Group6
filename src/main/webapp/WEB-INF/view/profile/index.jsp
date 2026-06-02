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
    <title>Booktify - Personal Information</title>
</head>

<body class="home-page">

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">
        <section class="section profile-section">
            <div class="section-header">
                <h2>Personal Information</h2>
                <p>Your account details on Booktify</p>
            </div>

            <div class="profile-card">
                <div class="profile-avatar">
                    <c:choose>
                        <c:when test="${not empty user.avatar}">
                            <img src="${user.avatar}" alt="Avatar" />
                        </c:when>
                        <c:otherwise>
                            <span class="profile-avatar-placeholder">
                                <i class="fa-solid fa-user"></i>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="profile-details">
                    <div class="profile-row">
                        <span class="profile-label">Full Name</span>
                        <span class="profile-value">${user.fullName}</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label">Email</span>
                        <span class="profile-value">${user.email}</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label">Phone</span>
                        <span class="profile-value">
                            <c:choose>
                                <c:when test="${not empty user.phone}">${user.phone}</c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label">Address</span>
                        <span class="profile-value">
                            <c:choose>
                                <c:when test="${not empty user.address}">${user.address}</c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label">Role</span>
                        <span class="profile-value">
                            <c:choose>
                                <c:when test="${user.role != null}">${user.role.name}</c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label">Account Status</span>
                        <span class="profile-value">
                            <span class="status-badge ${user.status ? 'status-active' : 'status-inactive'}">
                                ${user.status ? 'Active' : 'Inactive'}
                            </span>
                        </span>
                    </div>
                </div>

                <div class="profile-actions">
                    <a href="/changepass" class="btn-header btn-outline">Change Password</a>
                    <a href="/" class="btn-header btn-primary">Back to Home</a>
                </div>
            </div>
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
