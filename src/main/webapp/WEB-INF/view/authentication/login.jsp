<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
                <link rel="stylesheet" href="/css/auth-theme.css" />
                <link rel="stylesheet" href="/css/style.css" />
                <title>Login Page</title>
            </head>

            <body class="auth-page-bg">
                <img src="/images/logo.jpg" alt="Shop" class="site-logo" style="position:absolute;top:1.5rem;left:50%;transform:translateX(-50%);z-index:10;" />
                <div class="container" id="container">
                    <div>

                    </div>
                    <div class="form-container sign-in">


                        <form method="post" action="/login">

                            <h1>Sign In</h1>
                            <c:if test="${param.error != null}">
                                <div class="my-2" style="color: red;">Email hoặc Password không hợp lệ</div>
                            </c:if>
                            <c:if test="${param.logout != null}">
                                <div class="my-2" style="color: green;">Logout thành công</div>
                            </c:if>

                            <c:if test="${param.resetsuccess != null}">
                                <div class="my-2" style="color: green;">Đổi mật khẩu thành công</div>
                           </c:if>

                            <c:if test="${param.locked != null}">
                                <div class="d-flex justify-content-center my-2">
                                    <div style="color: orange; text-align: center;">
                                        Your account is locked. Please contact the administrator for details.
                                    </div>
                                </div>

                            </c:if>

                            <c:if test="${param.registersuccess != null}">
                                <div class="my-2" style="color: green;">Đăng kí thành công</div>
                            </c:if>


                            <input type="email" placeholder="Email Address" name="username" />
                            <input type="password" placeholder="Password" name="password" />

                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <a href="/forgotpassword">Forget Your Password?</a>

                            <button>Sign In</button>

                        </form>


                    </div>
                    <div class="toggle-container">
                        <div class="toggle">
                            <div class="toggle-panel toggle-right">
                                <h1>Hello, Friend!</h1>
                                <p>
                                    Register with your personal details to use all of site features
                                </p>
                                <a href="/register"><button class="hidden" id="register">Sign Up</button></a>
                <a href="/"><button class="hidden" id="register">Back</button></a>

                            </div>
                        </div>
                    </div>
                </div>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
                    crossorigin="anonymous"></script>
                <script src="/js/script.js"></script>
            </body>

            </html>