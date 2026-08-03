<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/auth.css" />
    <title>Booktify — Verify OTP</title>
</head>
<body class="auth-page">
    <div class="auth-shell">
        <header class="auth-topbar">
            <a href="/" class="auth-brand">
                <span class="auth-brand-icon"><i class="fa-solid fa-book-open"></i></span>
                <span>Booktify</span>
            </a>
            <a href="/forgotpassword" class="auth-back-home">
                <i class="fa-solid fa-arrow-left"></i> Back
            </a>
        </header>

        <main class="auth-main">
            <div class="auth-card">
                <div class="auth-card-header">
                    <div class="auth-otp-icon">
                        <i class="fa-solid fa-shield-halved"></i>
                    </div>
                    <h1>Verify OTP</h1>
                    <p>Check your email and enter the OTP code to reset your password</p>
                </div>

                <div class="auth-card-body">
                    <c:if test="${param.error != null}">
                        <div class="auth-alert auth-alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            Invalid OTP. Please try again.
                        </div>
                    </c:if>

                    <form method="post" action="/authentication/enterOTP">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="auth-form-group">
                            <label class="auth-label" for="otp">OTP code</label>
                            <input type="text" id="otp" name="otp" class="auth-input auth-otp-input"
                                   placeholder="000000" maxlength="6" inputmode="numeric" required />
                            <p class="auth-hint">A 6-digit code has been sent to your email.</p>
                        </div>
                        <button type="submit" class="auth-btn auth-btn-primary">
                            <i class="fa-solid fa-check"></i> Confirm
                        </button>
                    </form>
                </div>

                <div class="auth-card-footer">
                    Didn't receive the code? <a href="/forgotpassword">Resend OTP</a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
