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
                <p>Xem và quản lý tài khoản Booktify của bạn</p>
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
                <div class="profile-sidebar-sticky">
                <aside class="profile-sidebar">
                    <div class="profile-avatar">
                        <c:choose>
                            <c:when test="${not empty profile.avatar}">
                                <img src="${profile.avatar}" alt="Avatar" />
                            </c:when>
                            <c:otherwise>
                                <span class="profile-avatar-placeholder">
                                    <i class="fa-solid fa-user"></i>
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <h3 class="profile-sidebar-name">${profile.fullName}</h3>
                    <p class="profile-sidebar-email">${profile.email}</p>
                    <ul class="profile-meta-list">
                        <li>
                            <span class="profile-meta-label">Vai trò</span>
                            <span class="profile-meta-value">
                                <c:choose>
                                    <c:when test="${not empty profile.roleName}">${profile.roleName}</c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </span>
                        </li>
                        <li>
                            <span class="profile-meta-label">Trạng thái</span>
                            <span class="status-badge ${profile.status ? 'status-active' : 'status-inactive'}">
                                ${profile.status ? 'Hoạt động' : 'Bị khóa'}</span>
                        </li>
                    </ul>
                    <a href="/" class="profile-btn profile-btn-outline profile-btn-block">
                        <i class="fa-solid fa-house"></i> Về trang chủ
                    </a>
                </aside>
                </div>

                <div class="profile-main">
                    <%-- Thông tin cá nhân --%>
                    <div class="profile-panel">
                        <div class="profile-panel-header profile-panel-header--actions">
                            <div class="profile-panel-header-left">
                                <i class="fa-solid fa-id-card"></i>
                                <div>
                                    <h3>Thông tin tài khoản</h3>
                                    <p>Họ tên, email, số điện thoại và địa chỉ</p>
                                </div>
                            </div>
                            <c:if test="${!editMode}">
                                <a href="/profile?edit=true" class="profile-btn profile-btn-outline profile-btn-sm">
                                    <i class="fa-solid fa-pen-to-square"></i> Thay đổi
                                </a>
                            </c:if>
                        </div>

                        <c:if test="${!editMode}">
                            <dl class="profile-info-list">
                                <div class="profile-info-item">
                                    <dt>Họ và tên</dt>
                                    <dd>${profile.fullName}</dd>
                                </div>
                                <div class="profile-info-item">
                                    <dt>Email</dt>
                                    <dd>${profile.email}</dd>
                                </div>
                                <div class="profile-info-item">
                                    <dt>Số điện thoại</dt>
                                    <dd>
                                        <c:choose>
                                            <c:when test="${not empty profile.phone}">${profile.phone}</c:when>
                                            <c:otherwise><span class="profile-info-empty">Chưa cập nhật</span></c:otherwise>
                                        </c:choose>
                                    </dd>
                                </div>
                                <div class="profile-info-item">
                                    <dt>Địa chỉ</dt>
                                    <dd>
                                        <c:choose>
                                            <c:when test="${not empty profile.address}">${profile.address}</c:when>
                                            <c:otherwise><span class="profile-info-empty">Chưa cập nhật</span></c:otherwise>
                                        </c:choose>
                                    </dd>
                                </div>
                            </dl>
                        </c:if>

                        <c:if test="${editMode}">
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
                                    <input type="email" class="profile-input profile-input-readonly"
                                        value="${profile.email}" readonly disabled />
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

                                <div class="profile-form-actions profile-form-actions--split">
                                    <a href="/profile" class="profile-btn profile-btn-outline">Hủy</a>
                                    <button type="submit" class="profile-btn profile-btn-primary">
                                        <i class="fa-solid fa-floppy-disk"></i> Lưu thay đổi
                                    </button>
                                </div>
                            </form:form>
                        </c:if>
                    </div>

                    <%-- Đổi mật khẩu --%>
                    <div class="profile-panel" id="password-section">
                        <div class="profile-panel-header profile-panel-header--actions">
                            <div class="profile-panel-header-left">
                                <i class="fa-solid fa-lock"></i>
                                <div>
                                    <h3>Mật khẩu</h3>
                                    <p>Bảo vệ tài khoản bằng mật khẩu mạnh</p>
                                </div>
                            </div>
                            <c:if test="${!passwordEditMode}">
                                <a href="/profile?password=edit" class="profile-btn profile-btn-outline profile-btn-sm">
                                    <i class="fa-solid fa-key"></i> Đổi mật khẩu
                                </a>
                            </c:if>
                        </div>

                        <c:if test="${!passwordEditMode}">
                            <dl class="profile-info-list">
                                <div class="profile-info-item">
                                    <dt>Mật khẩu đăng nhập</dt>
                                    <dd class="profile-password-mask">••••••••</dd>
                                </div>
                            </dl>
                            <p class="profile-hint profile-hint-block">
                                <i class="fa-solid fa-shield-halved"></i>
                                Đổi mật khẩu yêu cầu xác thực OTP qua email đã đăng ký.
                            </p>
                        </c:if>

                        <c:if test="${passwordEditMode}">
                            <c:if test="${not empty otpSentMessage}">
                                <div class="profile-alert profile-alert-success profile-alert-inline">
                                    <i class="fa-solid fa-envelope-circle-check"></i> ${otpSentMessage}
                                </div>
                            </c:if>
                            <c:if test="${not empty passwordErrorMessage}">
                                <div class="profile-alert profile-alert-error profile-alert-inline">
                                    <i class="fa-solid fa-circle-exclamation"></i> ${passwordErrorMessage}
                                </div>
                            </c:if>

                            <c:if test="${!otpSent}">
                                <p class="profile-hint profile-hint-block">
                                    Nhấn nút bên dưới để nhận mã OTP tại email <strong>${profile.email}</strong>
                                </p>
                                <form method="post" action="/profile/password/send-otp" class="profile-form">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <div class="profile-form-actions profile-form-actions--split">
                                        <a href="/profile" class="profile-btn profile-btn-outline">Hủy</a>
                                        <button type="submit" class="profile-btn profile-btn-primary">
                                            <i class="fa-solid fa-paper-plane"></i> Gửi mã OTP
                                        </button>
                                    </div>
                                </form>
                            </c:if>

                            <c:if test="${otpSent}">
                                <form:form method="post" action="/profile/password" modelAttribute="passwordChangeForm"
                                    cssClass="profile-form">

                                    <div class="profile-form-group">
                                        <label for="otp">Mã OTP <span class="required">*</span></label>
                                        <form:input path="otp" id="otp" cssClass="profile-input profile-otp-input"
                                            placeholder="Nhập 6 chữ số" maxlength="6" />
                                        <form:errors path="otp" cssClass="profile-field-error" />
                                    </div>

                                    <div class="profile-form-group">
                                        <label for="currentPassword">Mật khẩu hiện tại <span class="required">*</span></label>
                                        <form:password path="currentPassword" id="currentPassword"
                                            cssClass="profile-input" placeholder="Nhập mật khẩu hiện tại" />
                                        <form:errors path="currentPassword" cssClass="profile-field-error" />
                                    </div>

                                    <div class="profile-form-group">
                                        <label for="newPassword">Mật khẩu mới <span class="required">*</span></label>
                                        <form:password path="newPassword" id="newPassword" cssClass="profile-input"
                                            placeholder="Tối thiểu 3 ký tự" />
                                        <form:errors path="newPassword" cssClass="profile-field-error" />
                                    </div>

                                    <div class="profile-form-group">
                                        <label for="confirmPassword">Xác nhận mật khẩu mới <span class="required">*</span></label>
                                        <form:password path="confirmPassword" id="confirmPassword"
                                            cssClass="profile-input" placeholder="Nhập lại mật khẩu mới" />
                                        <form:errors path="confirmPassword" cssClass="profile-field-error" />
                                    </div>

                                    <div class="profile-form-actions profile-form-actions--split">
                                        <a href="/profile" class="profile-btn profile-btn-outline">Hủy</a>
                                        <button type="submit" class="profile-btn profile-btn-primary">
                                            <i class="fa-solid fa-check"></i> Xác nhận đổi mật khẩu
                                        </button>
                                    </div>
                                </form:form>

                                <form method="post" action="/profile/password/send-otp" class="profile-resend-otp">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <button type="submit" class="profile-link-btn">Gửi lại mã OTP</button>
                                </form>
                            </c:if>
                        </c:if>
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
