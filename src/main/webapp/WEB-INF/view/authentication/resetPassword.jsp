<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/auth.css" />
    <title>Booktify — Reset password</title>
</head>
<body class="auth-page">
    <div class="auth-shell">
        <header class="auth-topbar">
            <a href="/" class="auth-brand">
                <span class="auth-brand-icon"><i class="fa-solid fa-book-open"></i></span>
                <span>Booktify</span>
            </a>
            <a href="/login" class="auth-back-home">
                <i class="fa-solid fa-arrow-left"></i> Back to login
            </a>
        </header>

        <main class="auth-main">
            <div class="auth-card">
                <div class="auth-card-header">
                    <div class="auth-otp-icon">
                        <i class="fa-solid fa-key"></i>
                    </div>
                    <h1>Reset password</h1>
                    <p>Enter a new password for your account</p>
                </div>

                <div class="auth-card-body">
                    <c:if test="${param.invalidpassword != null}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            Password and confirmation password must match.
                        </div>
                    </c:if>

                    <form:form method="post" action="/authentication/resetPassword"
                               modelAttribute="resetPasswordForm">
                        <div class="auth-form-group">
                            <label class="auth-label">New password</label>
                            <form:password path="password" cssClass="auth-input"
                                placeholder="Enter new password" autocomplete="off" />
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">Confirm password</label>
                            <form:password path="confPassword" cssClass="auth-input"
                                placeholder="Re-enter password" autocomplete="off" />
                        </div>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <button type="submit" class="auth-btn auth-btn-primary">
                            <i class="fa-solid fa-floppy-disk"></i> Save password
                        </button>
                    </form:form>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
