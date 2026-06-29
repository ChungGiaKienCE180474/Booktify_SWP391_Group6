<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/auth.css" />
    <title>Booktify — Đăng ký</title>
</head>
<body class="auth-page">
    <div class="auth-shell">
        <header class="auth-topbar">
            <a href="/" class="auth-brand">
                <span class="auth-brand-icon"><i class="fa-solid fa-book-open"></i></span>
                <span>Booktify</span>
            </a>
            <a href="/login" class="auth-back-home">
                <i class="fa-solid fa-arrow-left"></i> Đã có tài khoản
            </a>
        </header>

        <main class="auth-main">
            <div class="auth-card auth-card--wide">
                <div class="auth-card-header">
                    <h1>Tạo tài khoản</h1>
                    <p>Tham gia Booktify để khám phá hàng nghìn đầu sách</p>
                </div>

                <div class="auth-card-body">
                    <c:if test="${param.exist != null}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            Email đã được đăng ký. Vui lòng đăng nhập.
                        </div>
                    </c:if>
                    <c:if test="${param.password != null}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            Mật khẩu và xác nhận mật khẩu phải trùng khớp.
                        </div>
                    </c:if>
                    <c:if test="${not empty message}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            ${message}
                        </div>
                    </c:if>

                    <form:form method="post" action="/register" modelAttribute="registerUser">
                        <div class="auth-form-row">
                            <div class="auth-form-group">
                                <label class="auth-label">Họ</label>
                                <form:input path="firstName" cssClass="auth-input" placeholder="Nguyễn" />
                                <form:errors path="firstName" cssClass="auth-invalid-feedback" />
                            </div>
                            <div class="auth-form-group">
                                <label class="auth-label">Tên</label>
                                <form:input path="lastName" cssClass="auth-input" placeholder="Văn A" />
                                <form:errors path="lastName" cssClass="auth-invalid-feedback" />
                            </div>
                        </div>

                        <div class="auth-form-group">
                            <label class="auth-label">Email</label>
                            <form:input path="email" type="email" cssClass="auth-input"
                                placeholder="name@example.com" />
                            <form:errors path="email" cssClass="auth-invalid-feedback" />
                        </div>

                        <div class="auth-form-row">
                            <div class="auth-form-group">
                                <label class="auth-label">Mật khẩu</label>
                                <form:password path="password" cssClass="auth-input"
                                    placeholder="Tối thiểu 3 ký tự" />
                                <form:errors path="password" cssClass="auth-invalid-feedback" />
                            </div>
                            <div class="auth-form-group">
                                <label class="auth-label">Xác nhận mật khẩu</label>
                                <form:password path="confirmPassword" cssClass="auth-input"
                                    placeholder="Nhập lại mật khẩu" />
                                <form:errors path="confirmPassword" cssClass="auth-invalid-feedback" />
                            </div>
                        </div>

                        <button type="submit" class="auth-btn auth-btn-primary">
                            <i class="fa-solid fa-user-plus"></i> Đăng ký
                        </button>
                    </form:form>
                </div>

                <div class="auth-card-footer">
                    Đã có tài khoản? <a href="/login">Đăng nhập</a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
