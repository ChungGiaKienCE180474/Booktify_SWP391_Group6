<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/auth.css" />
    <title>Booktify — Đăng nhập</title>
</head>
<body class="auth-page">
    <div class="auth-shell">
        <header class="auth-topbar">
            <a href="/" class="auth-brand">
                <span class="auth-brand-icon"><i class="fa-solid fa-book-open"></i></span>
                <span>Booktify</span>
            </a>
            <a href="/" class="auth-back-home">
                <i class="fa-solid fa-arrow-left"></i> Về trang chủ
            </a>
        </header>

        <main class="auth-main">
            <div class="auth-card auth-card--split">
                <div class="auth-split-form">
                    <div class="auth-card-header" style="padding: 0 0 1rem; text-align: left;">
                        <h1>Đăng nhập</h1>
                        <p>Chào mừng bạn quay lại Booktify</p>
                    </div>

                    <c:if test="${param.error != null}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            Email hoặc mật khẩu không hợp lệ.
                        </div>
                    </c:if>
                    <c:if test="${param.logout != null}">
                        <div class="auth-alert auth-alert-success">
                            <i class="fa-solid fa-circle-check"></i>
                            Đăng xuất thành công.
                        </div>
                    </c:if>
                    <c:if test="${param.resetsuccess != null}">
                        <div class="auth-alert auth-alert-success">
                            <i class="fa-solid fa-circle-check"></i>
                            Đổi mật khẩu thành công. Vui lòng đăng nhập.
                        </div>
                    </c:if>
                    <c:if test="${param.locked != null}">
                        <div class="auth-alert auth-alert-warn">
                            <i class="fa-solid fa-lock"></i>
                            Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.
                        </div>
                    </c:if>
                    <c:if test="${param.registersuccess != null}">
                        <div class="auth-alert auth-alert-success">
                            <i class="fa-solid fa-circle-check"></i>
                            Đăng ký thành công. Bạn có thể đăng nhập ngay.
                        </div>
                    </c:if>

                    <form method="post" action="/login">
                        <div class="auth-form-group">
                            <label class="auth-label" for="username">Email</label>
                            <input class="auth-input" type="email" id="username" name="username"
                                   placeholder="name@example.com" required />
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label" for="password">Mật khẩu</label>
                            <input class="auth-input" type="password" id="password" name="password"
                                   placeholder="Nhập mật khẩu" required />
                        </div>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <a href="/forgotpassword" class="auth-forgot">Quên mật khẩu?</a>
                        <button type="submit" class="auth-btn auth-btn-primary">
                            <i class="fa-solid fa-right-to-bracket"></i> Đăng nhập
                        </button>
                    </form>

                    <div class="auth-card-footer" style="margin-top: 1.25rem; border: none; background: transparent; padding: 0;">
                        Chưa có tài khoản? <a href="/register">Đăng ký ngay</a>
                    </div>
                </div>

                <div class="auth-split-promo">
                    <h2>Xin chào!</h2>
                    <p>Đăng ký tài khoản để mua sách, theo dõi đơn hàng và nhận ưu đãi từ Booktify.</p>
                    <a href="/register" class="auth-btn auth-btn-outline">Tạo tài khoản</a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
