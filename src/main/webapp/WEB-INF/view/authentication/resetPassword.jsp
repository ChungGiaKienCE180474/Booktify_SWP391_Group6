<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
            <!DOCTYPE html>
            <html>

            <head>
                <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet"
                    id="bootstrap-css">
                <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
                <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>

                <link rel="stylesheet"
                    href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
                <link rel="stylesheet" href="/css/auth-theme.css" />

                <style type="text/css">
                    .form-gap {
                        padding-top: 70px;
                    }

                    .panel-default {
                        border-radius: 12px;
                        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
                    }
                </style>
            </head>

            <body class="auth-page-bg">
                <div class="form-gap"></div>
                <div class="container">
                    <div class="row">
                        <div class="col-md-4 col-md-offset-4">
                            <div class="panel panel-default">
                                <div class="panel-body">
                                    <div class="text-center">
                                        <img src="/images/logo.jpg" alt="Shop" class="site-logo-sm" />
                                        <div class="text-center mb-4">
                                            <h2><strong>Reset Password</strong></h2>
                                        </div>
                                        <c:if test="${param.invalidpassword != null}">
                                            <div class="my-2" style="color: red;">Password và ConfirmPassword phải trùng
                                                nhau</div>
                                        </c:if>
                                        <!-- Sử dụng Spring form:form để tận dụng Spring binding -->
                                        <form:form method="post" action="/authentication/resetPassword"
                                            modelAttribute="resetPasswordForm">
                                            <div class="form-group">
                                                <!-- Tránh lưu trữ mật khẩu trong trình duyệt với autocomplete="off" -->
                                                <form:input path="password" type="password" placeholder="New Password"
                                                    required="required" autocomplete="off" class="form-control" />
                                            </div>
                                            <div class="form-group">
                                                <form:input path="confPassword" type="password"
                                                    placeholder="Confirm Password" autocomplete="off"
                                                    required="required" class="form-control" />
                                            </div>

                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                                            <button type="submit" class="btn btn-primary">Reset Password</button>
                                        </form:form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </body>

            </html>