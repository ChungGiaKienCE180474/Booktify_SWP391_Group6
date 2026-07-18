<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:if test="${empty profileBase}">
    <c:set var="profileBase" value="/profile" scope="request" />
</c:if>

<div class="section-header">
    <h2>My profile</h2>
    <p>View and manage your Booktify account</p>
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
                    <span class="profile-meta-label">Role</span>
                    <span class="profile-meta-value">
                        <c:choose>
                            <c:when test="${not empty profile.roleName}">${profile.roleName}</c:when>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </span>
                </li>
                <li>
                    <span class="profile-meta-label">Status</span>
                    <span class="status-badge ${profile.status ? 'status-active' : 'status-inactive'}">
                        ${profile.status ? 'Active' : 'Locked'}</span>
                </li>
            </ul>
            <c:choose>
                <c:when test="${adminProfile}">
                    <a href="/admin" class="profile-btn profile-btn-outline profile-btn-block">
                        <i class="fa-solid fa-chart-pie"></i> Back to dashboard
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="/" class="profile-btn profile-btn-outline profile-btn-block">
                        <i class="fa-solid fa-house"></i> Back to home
                    </a>
                </c:otherwise>
            </c:choose>
        </aside>
    </div>

    <div class="profile-main">
        <div class="profile-panel">
            <div class="profile-panel-header profile-panel-header--actions">
                <div class="profile-panel-header-left">
                    <i class="fa-solid fa-id-card"></i>
                    <div>
                        <h3>Account information</h3>
                        <p>Full name, email, phone number and address</p>
                    </div>
                </div>
                <c:if test="${!editMode}">
                    <a href="${profileBase}?edit=true" class="profile-btn profile-btn-outline profile-btn-sm">
                        <i class="fa-solid fa-pen-to-square"></i> Edit
                    </a>
                </c:if>
            </div>

            <c:if test="${!editMode}">
                <dl class="profile-info-list">
                    <div class="profile-info-item">
                        <dt>Full name</dt>
                        <dd>${profile.fullName}</dd>
                    </div>
                    <div class="profile-info-item">
                        <dt>Email</dt>
                        <dd>${profile.email}</dd>
                    </div>
                    <div class="profile-info-item">
                        <dt>Phone number</dt>
                        <dd>
                            <c:choose>
                                <c:when test="${not empty profile.phone}">${profile.phone}</c:when>
                                <c:otherwise><span class="profile-info-empty">Not updated</span></c:otherwise>
                            </c:choose>
                        </dd>
                    </div>
                    <div class="profile-info-item">
                        <dt>Address</dt>
                        <dd>
                            <c:choose>
                                <c:when test="${not empty profile.address}">${profile.address}</c:when>
                                <c:otherwise><span class="profile-info-empty">Not updated</span></c:otherwise>
                            </c:choose>
                        </dd>
                    </div>
                </dl>
            </c:if>

            <c:if test="${editMode}">
                <form:form method="post" action="${profileBase}/update" modelAttribute="profileUpdateForm"
                    cssClass="profile-form">

                    <div class="profile-form-group">
                        <label for="fullName">Full name <span class="required">*</span></label>
                        <form:input path="fullName" id="fullName" cssClass="profile-input"
                            placeholder="Enter your full name" />
                        <form:errors path="fullName" cssClass="profile-field-error" />
                    </div>

                    <div class="profile-form-group">
                        <label>Email</label>
                        <input type="email" class="profile-input profile-input-readonly"
                            value="${profile.email}" readonly disabled />
                        <span class="profile-hint">Email cannot be changed</span>
                    </div>

                    <div class="profile-form-group">
                        <label for="phone">Phone number</label>
                        <form:input path="phone" id="phone" cssClass="profile-input"
                            placeholder="VD: 0901234567" />
                        <form:errors path="phone" cssClass="profile-field-error" />
                    </div>

                    <div class="profile-form-group">
                        <label for="address">Address</label>
                        <form:textarea path="address" id="address" cssClass="profile-textarea" rows="3"
                            placeholder="Enter your shipping address" />
                        <form:errors path="address" cssClass="profile-field-error" />
                    </div>

                    <div class="profile-form-actions profile-form-actions--split">
                        <a href="${profileBase}" class="profile-btn profile-btn-outline">Cancel</a>
                        <button type="submit" class="profile-btn profile-btn-primary">
                            <i class="fa-solid fa-floppy-disk"></i> Save changes
                        </button>
                    </div>
                </form:form>
            </c:if>
        </div>

        <div class="profile-panel" id="password-section">
            <div class="profile-panel-header profile-panel-header--actions">
                <div class="profile-panel-header-left">
                    <i class="fa-solid fa-lock"></i>
                    <div>
                        <h3>Password</h3>
                        <p>Protect your account with a strong password</p>
                    </div>
                </div>
                <c:if test="${!passwordEditMode}">
                    <a href="${profileBase}?password=edit" class="profile-btn profile-btn-outline profile-btn-sm">
                        <i class="fa-solid fa-key"></i>
                        <c:choose>
                            <c:when test="${profile.googleAccount}">Set password</c:when>
                            <c:otherwise>Change password</c:otherwise>
                        </c:choose>
                    </a>
                </c:if>
            </div>

            <c:if test="${!passwordEditMode}">
                <dl class="profile-info-list">
                    <div class="profile-info-item">
                        <dt>Login password</dt>
                        <dd class="profile-password-mask">••••••••</dd>
                    </div>
                </dl>
                <p class="profile-hint profile-hint-block">
                    <i class="fa-solid fa-shield-halved"></i>
                    <c:choose>
                        <c:when test="${profile.googleAccount}">
                            This account signs in with Google. You can set a password to log in by email — just verify with OTP.
                        </c:when>
                        <c:otherwise>
                            Changing your password requires OTP verification via your registered email.
                        </c:otherwise>
                    </c:choose>
                </p>
            </c:if>

            <c:if test="${passwordEditMode}">
                <c:if test="${not empty otpSentMessage}">
                    <div class="profile-alert profile-alert-success profile-alert-inline">
                        <i class="fa-solid fa-envelope-circle-check"></i> ${otpSentMessage}
                    </div>
                </c:if>
                <c:if test="${not empty otpVerifiedMessage}">
                    <div class="profile-alert profile-alert-success profile-alert-inline">
                        <i class="fa-solid fa-circle-check"></i> ${otpVerifiedMessage}
                    </div>
                </c:if>
                <c:if test="${not empty passwordErrorMessage}">
                    <div class="profile-alert profile-alert-error profile-alert-inline">
                        <i class="fa-solid fa-circle-exclamation"></i> ${passwordErrorMessage}
                    </div>
                </c:if>

                <c:if test="${!otpSent}">
                    <p class="profile-hint profile-hint-block">
                        Click the button below to receive an OTP code at <strong>${profile.email}</strong>
                    </p>
                    <form method="post" action="${profileBase}/password/send-otp" class="profile-form">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="profile-form-actions profile-form-actions--split">
                            <a href="${profileBase}" class="profile-btn profile-btn-outline">Cancel</a>
                            <button type="submit" class="profile-btn profile-btn-primary">
                                <i class="fa-solid fa-paper-plane"></i> Send OTP code
                            </button>
                        </div>
                    </form>
                </c:if>

                <c:if test="${otpSent && !otpVerified}">
                    <p class="profile-hint profile-hint-block">
                        An OTP code has been sent to <strong>${profile.email}</strong>.
                    </p>
                    <form method="post" action="${profileBase}/password/verify-otp" class="profile-form">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="profile-form-group">
                            <label for="otp">OTP code <span class="required">*</span></label>
                            <input type="number" name="otp" id="otp" class="profile-input profile-otp-input"
                                   placeholder="Enter 6 digits" min="100000" max="999999" required />
                        </div>
                        <div class="profile-form-actions profile-form-actions--split">
                            <a href="${profileBase}" class="profile-btn profile-btn-outline">Cancel</a>
                            <button type="submit" class="profile-btn profile-btn-primary">
                                <i class="fa-solid fa-shield-check"></i> Confirm OTP
                            </button>
                        </div>
                    </form>
                    <form method="post" action="${profileBase}/password/send-otp" class="profile-resend-otp">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <button type="submit" class="profile-link-btn">Resend OTP code</button>
                    </form>
                </c:if>

                <c:if test="${otpVerified}">
                    <c:if test="${profile.googleAccount}">
                        <p class="profile-hint profile-hint-block">
                            OTP verified. Enter a new password — no current password needed since you sign in with Google.
                        </p>
                    </c:if>
                    <form:form method="post" action="${profileBase}/password" modelAttribute="passwordChangeForm"
                        cssClass="profile-form">

                        <c:if test="${!profile.googleAccount}">
                            <div class="profile-form-group">
                                <label for="currentPassword">Current password <span class="required">*</span></label>
                                <form:password path="currentPassword" id="currentPassword"
                                    cssClass="profile-input" placeholder="Enter your current password" />
                                <form:errors path="currentPassword" cssClass="profile-field-error" />
                            </div>
                        </c:if>

                        <div class="profile-form-group">
                            <label for="newPassword">New password <span class="required">*</span></label>
                            <form:password path="newPassword" id="newPassword" cssClass="profile-input"
                                placeholder="At least 3 characters" />
                            <form:errors path="newPassword" cssClass="profile-field-error" />
                        </div>

                        <div class="profile-form-group">
                            <label for="confirmPassword">Confirm new password <span class="required">*</span></label>
                            <form:password path="confirmPassword" id="confirmPassword"
                                cssClass="profile-input" placeholder="Re-enter your new password" />
                            <form:errors path="confirmPassword" cssClass="profile-field-error" />
                        </div>

                        <div class="profile-form-actions profile-form-actions--split">
                            <a href="${profileBase}" class="profile-btn profile-btn-outline">Cancel</a>
                            <button type="submit" class="profile-btn profile-btn-primary">
                                <i class="fa-solid fa-check"></i>
                                <c:choose>
                                    <c:when test="${profile.googleAccount}">Confirm set password</c:when>
                                    <c:otherwise>Confirm change password</c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </form:form>
                </c:if>
            </c:if>
        </div>
    </div>
</div>
