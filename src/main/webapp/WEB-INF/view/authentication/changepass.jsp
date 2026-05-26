<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/auth-theme.css" />
    <link rel="stylesheet" href="/css/register.css" />
    <title>Change Password</title>
</head>

<body class="bg-primary auth-page-bg">
    <div id="layoutAuthentication">
        <div id="layoutAuthentication_content">
            <main>
                <div class="container">
                    <div class="row justify-content-center">
                        <div class="col-lg-6">
                            <div class="card shadow-lg border-0 rounded-lg mt-5">
                                <div class="card-header">
                                    <img src="/images/logo.jpg" alt="Shop" class="site-logo-sm" />
                                    <h3 class="text-center font-weight-light my-4">Change Password</h3>
                                </div>
                                <div class="card-body">
                                    <c:if test="${not empty successMessage}">
                                        <div class="alert alert-success" role="alert">${successMessage}</div>
                                    </c:if>
                                    <c:if test="${not empty errorMessage}">
                                        <div class="alert alert-danger" role="alert">${errorMessage}</div>
                                    </c:if>

                                    <form:form method="post" action="/changepass" modelAttribute="passwordChangeForm">
                                        <div class="mb-3">
                                            <label class="form-label">Current Password</label>
                                            <form:input type="password" path="currentPassword" class="form-control" />
                                            <form:errors path="currentPassword" cssClass="text-danger small" />
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">New Password</label>
                                            <form:input type="password" path="newPassword" class="form-control" />
                                            <form:errors path="newPassword" cssClass="text-danger small" />
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">Confirm New Password</label>
                                            <form:input type="password" path="confirmPassword" class="form-control" />
                                            <form:errors path="confirmPassword" cssClass="text-danger small" />
                                        </div>
                                        <div class="d-flex justify-content-between">
                                            <a href="/" class="btn btn-secondary">Back</a>
                                            <button type="submit" class="btn btn-primary">Change Password</button>
                                        </div>
                                    </form:form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>
</body>

</html>
