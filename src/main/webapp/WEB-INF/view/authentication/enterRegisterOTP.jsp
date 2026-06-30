<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/auth.css" />
    <title>Booktify — Xác nhận OTP đăng ký</title>
</head>
<body class="auth-page">
    <div class="auth-shell">
        <header class="auth-topbar">
            <a href="/" class="auth-brand">
                <span class="auth-brand-icon"><i class="fa-solid fa-book-open"></i></span>
                <span>Booktify</span>
            </a>
            <a href="/register" class="auth-back-home">
                <i class="fa-solid fa-arrow-left"></i> Quay lại đăng ký
            </a>
        </header>

        <main class="auth-main">
            <div class="auth-card">
                <div class="auth-card-header">
                    <div class="auth-otp-icon">
                        <i class="fa-solid fa-envelope-circle-check"></i>
                    </div>
                    <h1>Xác nhận đăng ký</h1>
                    <p>Nhập mã OTP đã gửi tới email của bạn để hoàn tất đăng ký</p>
                </div>

                <div class="auth-card-body">
                    <c:if test="${not empty message}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            ${message}
                        </div>
                    </c:if>

                    <form method="post" action="/authentication/enterRegisterOTP">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="auth-form-group">
                            <label class="auth-label" for="otp">Mã OTP</label>
                            <input type="text" id="otp" name="otp" class="auth-input auth-otp-input"
                                   placeholder="000000" maxlength="6" inputmode="numeric" required />
                            <p class="auth-hint">Mã gồm 6 chữ số được gửi tới email đăng ký.</p>
                        </div>
                        <button type="submit" class="auth-btn auth-btn-primary">
                            <i class="fa-solid fa-check"></i> Hoàn tất đăng ký
                        </button>
                    </form>
                </div>

                <div class="auth-card-footer">
                    Không nhận được mã? <a href="/register">Đăng ký lại</a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
