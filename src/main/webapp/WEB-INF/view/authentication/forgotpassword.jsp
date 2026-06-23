<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Forgot password</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/auth-theme.css" />
    <style>
        body {
            color: #505050;
            font-family: "Rubik", Helvetica, Arial, sans-serif;
            font-size: 14px;
            line-height: 1.5;
        }
        .forgot {
            background-color: #fff;
            padding: 12px;
            border: 1px solid #dfdfdf;
        }
        .padding-bottom-3x { padding-bottom: 72px !important; }
        .card-footer { background-color: #fff; }
    </style>
</head>
<body class="auth-page-bg">
    <div class="container padding-bottom-3x mb-2 mt-5">
        <img src="/images/logo.jpg" alt="Shop" class="site-logo" />
        <div class="row justify-content-center">
            <div class="col-lg-8 col-md-10">
                <div class="forgot">
                    <h2>Forgot your password?</h2>
                    <p>Change your password in three easy steps.</p>
                    <ol class="list-unstyled">
                        <li><span class="text-primary">1.</span> Enter your email address below.</li>
                        <li><span class="text-primary">2.</span> Our system will send you an OTP to your email.</li>
                        <li><span class="text-primary">3.</span> Enter the OTP on the next page.</li>
                    </ol>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger mt-3">${errorMessage}</div>
                </c:if>

                <form method="post" action="/authentication/forgotpassword" class="card mt-4">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <div class="card-body">
                        <div class="form-group">
                            <c:if test="${param.invalidemail != null}">
                                <div class="alert alert-danger">Email không hợp lệ hoặc chưa đăng ký.</div>
                            </c:if>
                            <label for="email-for-pass">Email:</label>
                            <input type="email" id="email-for-pass" name="email" class="form-control" required
                                   placeholder="name@example.com" />
                            <small class="form-text text-muted">
                                Enter the registered email address. We'll email an OTP to this address.
                            </small>
                        </div>
                    </div>
                    <div class="card-footer d-flex gap-2">
                        <button type="submit" class="btn btn-primary">Get OTP</button>
                        <a href="/login" class="btn btn-secondary">Back</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
