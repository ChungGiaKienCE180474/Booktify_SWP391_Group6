<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/profile.css" />
    <title>Booktify - Thông tin cá nhân</title>
</head>

<body class="home-page">

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content">
        <section class="section profile-section">
            <div class="section-header">
                <h2>Thông tin cá nhân</h2>
                <p>Quản lý thông tin tài khoản Booktify của bạn</p>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="profile-alert profile-alert-success">
                    <i class="fa-solid fa-circle-check"></i> ${successMessage}
                </div>
            </c:if>
            <c:if test="${not empty passwordSuccessMessage}">
                <div class="profile-alert profile-alert-success">
                    <i class="fa-solid fa-circle-check"></i> ${passwordSuccessMessage}
                </div>
            </c:if>

            <div class="profile-layout">
                <aside class="profile-sidebar">
                    <div class="profile-avatar">
                        <c:choose>
                            <c:when test="${not empty user.avatar}">
                                <img src="${user.avatar}" alt="Avatar" />
                            </c:when>
                            <c:otherwise>
                                <span class="profile-avatar-placeholder">
                                    <i class="fa-solid fa-user"></i>
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <h3 class="profile-sidebar-name">${user.fullName}</h3>
                    <p class="profile-sidebar-email">${user.email}</p>
                    <ul class="profile-meta-list">
                        <li>
                            <span class="profile-meta-label">Vai trò</span>
                            <span class="profile-meta-value">
                                <c:choose>
                                    <c:when test="${user.role != null}">${user.role.name}</c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </span>
                        </li>
                        <li>
                            <span class="profile-meta-label">Trạng thái</span>
                            <span class="status-badge ${user.status ? 'status-active' : 'status-inactive'}">
                                ${user.status ? 'Hoạt động' : 'Bị khóa'}</span>
                        </li>
                    </ul>
                    <a href="/" class="profile-btn profile-btn-outline profile-btn-block">
                        <i class="fa-solid fa-house"></i> Về trang chủ
                    </a>
                </aside>

                <div class="profile-main">
                    <div class="profile-panel">
                        <div class="profile-panel-header">
                            <i class="fa-solid fa-pen-to-square"></i>
                            <div>
                                <h3>Cập nhật thông tin</h3>
                                <p>Chỉnh sửa họ tên, số điện thoại và địa chỉ</p>
                            </div>
                        </div>

                        <form:form method="post" action="/profile/update" modelAttribute="profileUpdateForm"
                            cssClass="profile-form">

                            <div class="profile-form-group">
                                <label for="fullName">Họ và tên <span class="required">*</span></label>
                                <form:input path="fullName" id="fullName" cssClass="profile-input"
                                    placeholder="Nhập họ và tên" />
                                <form:errors path="fullName" cssClass="profile-field-error" />
                            </div>

                            <div class="profile-form-group">
                                <label>Email</label>
                                <input type="email" class="profile-input profile-input-readonly" value="${user.email}"
                                    readonly disabled />
                                <span class="profile-hint">Email không thể thay đổi</span>
                            </div>

                            <div class="profile-form-group">
                                <label for="phone">Số điện thoại</label>
                                <form:input path="phone" id="phone" cssClass="profile-input"
                                    placeholder="VD: 0901234567" />
                                <form:errors path="phone" cssClass="profile-field-error" />
                            </div>

                            <div class="profile-form-group">
                                <label for="address">Địa chỉ</label>
                                <form:textarea path="address" id="address" cssClass="profile-textarea" rows="3"
                                    placeholder="Nhập địa chỉ giao hàng" />
                                <form:errors path="address" cssClass="profile-field-error" />
                            </div>

                            <div class="profile-form-actions">
                                <button type="submit" class="profile-btn profile-btn-primary">
                                    <i class="fa-solid fa-floppy-disk"></i> Lưu thay đổi
                                </button>
                            </div>
                        </form:form>
                    </div>

                    <div class="profile-panel" id="password-section">
                        <div class="profile-panel-header">
                            <i class="fa-solid fa-lock"></i>
                            <div>
                                <h3>Đổi mật khẩu</h3>
                                <p>Cập nhật mật khẩu để bảo vệ tài khoản</p>
                            </div>
                        </div>

                        <c:if test="${not empty passwordErrorMessage}">
                            <div class="profile-alert profile-alert-error">
                                <i class="fa-solid fa-circle-exclamation"></i> ${passwordErrorMessage}
                            </div>
                        </c:if>

                        <form:form method="post" action="/profile/password" modelAttribute="passwordChangeForm"
                            cssClass="profile-form">

                            <div class="profile-form-group">
                                <label for="currentPassword">Mật khẩu hiện tại <span class="required">*</span></label>
                                <form:password path="currentPassword" id="currentPassword" cssClass="profile-input"
                                    placeholder="Nhập mật khẩu hiện tại" showPassword="false" />
                                <form:errors path="currentPassword" cssClass="profile-field-error" />
                            </div>

                            <div class="profile-form-group">
                                <label for="newPassword">Mật khẩu mới <span class="required">*</span></label>
                                <form:password path="newPassword" id="newPassword" cssClass="profile-input"
                                    placeholder="Tối thiểu 3 ký tự" showPassword="false" />
                                <form:errors path="newPassword" cssClass="profile-field-error" />
                            </div>

                            <div class="profile-form-group">
                                <label for="confirmPassword">Xác nhận mật khẩu mới <span class="required">*</span></label>
                                <form:password path="confirmPassword" id="confirmPassword" cssClass="profile-input"
                                    placeholder="Nhập lại mật khẩu mới" showPassword="false" />
                                <form:errors path="confirmPassword" cssClass="profile-field-error" />
                            </div>

                            <div class="profile-form-actions">
                                <button type="submit" class="profile-btn profile-btn-primary">
                                    <i class="fa-solid fa-key"></i> Đổi mật khẩu
                                </button>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

    <script>
        document.getElementById('navToggle')?.addEventListener('click', function () {
            document.querySelector('.main-nav')?.classList.toggle('open');
        });

        if (window.location.hash === '#password-section') {
            document.getElementById('password-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    </script>
</body>

</html>
