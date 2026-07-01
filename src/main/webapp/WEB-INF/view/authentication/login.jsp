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

                    <div class="auth-divider">
                        <span>hoặc</span>
                    </div>

                    <a href="/oauth2/authorization/google" class="auth-btn auth-btn-google">
                        <svg class="auth-google-icon" viewBox="0 0 24 24" aria-hidden="true">
                            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                        </svg>
                        Đăng nhập với Google
                    </a>

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
