<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/auth-theme.css" />
    <title>Shop</title>
    <style>
        .home-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
        }

        .home-card {
            background: rgba(255, 255, 255, 0.97);
            border-radius: 16px;
            padding: 3rem;
            max-width: 520px;
            width: 100%;
            text-align: center;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        }

        .home-card h1 {
            margin-bottom: 0.5rem;
            color: #1D3865;
        }

        .home-card p {
            color: #666;
            margin-bottom: 2rem;
        }

        .home-actions {
            display: flex;
            flex-direction: column;
            gap: 0.75rem;
        }

        .home-actions a,
        .home-actions button {
            display: inline-block;
            padding: 12px 24px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            border: none;
            cursor: pointer;
            font-size: 1rem;
        }

        .btn-primary-custom {
            background: linear-gradient(to right, #1D3865, #5c6bc0);
            color: #fff;
        }

        .btn-outline-custom {
            background: transparent;
            color: #1D3865;
            border: 2px solid #1D3865;
        }

        .user-greeting {
            color: #512da8;
            font-weight: 600;
            margin-bottom: 1rem;
        }

        form.logout-form {
            margin: 0;
        }
    </style>
</head>

<body class="auth-page-bg">
    <div class="home-wrapper">
        <div class="home-card">
            <img src="/images/logo.jpg" alt="Shop" class="site-logo" />
            <h1>Shop</h1>
            <p>Chào mừng bạn đến với cửa hàng</p>

            <c:choose>
                <c:when test="${not empty username}">
                    <p class="user-greeting">
                        Xin chào, ${not empty fullName ? fullName : username}
                    </p>
                    <c:if test="${not empty role}">
                        <p style="color: #666; margin-bottom: 1rem;">Vai trò: ${role}</p>
                    </c:if>
                    <div class="home-actions">
                        <a href="/changepass" class="btn-outline-custom">Đổi mật khẩu</a>
                        <form class="logout-form" method="post" action="/logout">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <button type="submit" class="btn-primary-custom" style="width: 100%;">Đăng xuất</button>
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="home-actions">
                        <a href="/login" class="btn-primary-custom">Đăng nhập</a>
                        <a href="/register" class="btn-outline-custom">Đăng ký</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>

</html>
