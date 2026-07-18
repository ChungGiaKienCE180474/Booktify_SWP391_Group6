<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/auth.css" />
    <title>Booktify — Forgot password</title>
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
                    <h1>Forgot password?</h1>
                    <p>Recover your password via email in 3 steps</p>
                </div>

                <div class="auth-card-body">
                    <div class="auth-steps">
                        <h3><i class="fa-solid fa-list-ol"></i> Instructions</h3>
                        <ol>
                            <li><span>1.</span> Enter your registered email below.</li>
                            <li><span>2.</span> The system sends an OTP code to your email.</li>
                            <li><span>3.</span> Enter the OTP on the next page to reset your password.</li>
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
                            Invalid email or not registered.
                        </div>
                    </c:if>

                    <form method="post" action="/authentication/forgotpassword">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="auth-form-group">
                            <label class="auth-label" for="email-for-pass">Email</label>
                            <input type="email" id="email-for-pass" name="email" class="auth-input" required
                                   placeholder="name@example.com" />
                            <p class="auth-hint">Enter your registered email. We will send an OTP code to this email.</p>
                        </div>
                        <div class="auth-btn-group">
                            <button type="submit" class="auth-btn auth-btn-primary">
                                <i class="fa-solid fa-paper-plane"></i> Send OTP
                            </button>
                            <a href="/login" class="auth-btn auth-btn-secondary" style="text-align:center;text-decoration:none;">
                                Back
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
