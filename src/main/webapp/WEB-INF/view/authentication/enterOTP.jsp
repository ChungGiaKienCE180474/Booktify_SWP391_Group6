<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/auth-theme.css" />
    <title>OTP Verification</title>
    <style>
        .otp-container {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
            max-width: 420px;
            width: 100%;
        }
    </style>
</head>

<body class="auth-page-bg d-flex align-items-center justify-content-center flex-column">
    <div class="otp-container mx-3">
        <img src="/images/logo.jpg" alt="Shop" class="site-logo-sm" />
        <h1 class="text-center h4">Verify OTP</h1>
        <p class="text-center text-muted">Check your email and enter the OTP</p>
        <form method="post" action="/authentication/enterOTP">
            <div class="form-group">
                <c:if test="${param.error != null}">
                    <div class="my-2 text-danger">OTP không hợp lệ</div>
                </c:if>
                <label for="otp">Enter OTP</label>
                <input type="text" id="otp" name="otp" placeholder="Enter OTP" class="form-control" required />
            </div>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <button type="submit" class="btn btn-primary btn-block">Submit OTP</button>
        </form>
    </div>
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
