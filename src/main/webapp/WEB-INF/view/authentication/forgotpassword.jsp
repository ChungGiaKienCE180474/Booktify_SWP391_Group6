<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/auth.css" />
    <title>Booktify — Quên mật khẩu</title>
</head>
<body class="auth-page">
    <div class="auth-shell">
        <header class="auth-topbar">
            <a href="/" class="auth-brand">
                <span class="auth-brand-icon"><i class="fa-solid fa-book-open"></i></span>
                <span>Booktify</span>
            </a>
            <a href="/login" class="auth-back-home">
                <i class="fa-solid fa-arrow-left"></i> Về đăng nhập
            </a>
        </header>

        <main class="auth-main">
            <div class="auth-card">
                <div class="auth-card-header">
                    <h1>Quên mật khẩu?</h1>
                    <p>Khôi phục mật khẩu qua email trong 3 bước</p>
                </div>

                <div class="auth-card-body">
                    <div class="auth-steps">
                        <h3><i class="fa-solid fa-list-ol"></i> Hướng dẫn</h3>
                        <ol>
                            <li><span>1.</span> Nhập email đã đăng ký bên dưới.</li>
                            <li><span>2.</span> Hệ thống gửi mã OTP về email của bạn.</li>
                            <li><span>3.</span> Nhập OTP ở trang tiếp theo để đặt lại mật khẩu.</li>
                        </ol>
                    </div>

                    <c:if test="${not empty errorMessage}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            ${errorMessage}
                        </div>
                    </c:if>
                    <c:if test="${param.invalidemail != null}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            Email không hợp lệ hoặc chưa được đăng ký.
                        </div>
                    </c:if>

                    <form method="post" action="/authentication/forgotpassword">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="auth-form-group">
                            <label class="auth-label" for="email-for-pass">Email</label>
                            <input type="email" id="email-for-pass" name="email" class="auth-input" required
                                   placeholder="name@example.com" />
                            <p class="auth-hint">Nhập email đã đăng ký. Chúng tôi sẽ gửi mã OTP tới email này.</p>
                        </div>
                        <div class="auth-btn-group">
                            <button type="submit" class="auth-btn auth-btn-primary">
                                <i class="fa-solid fa-paper-plane"></i> Gửi OTP
                            </button>
                            <a href="/login" class="auth-btn auth-btn-secondary" style="text-align:center;text-decoration:none;">
                                Quay lại
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
