<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />

            <title>Staff Details — Booktify Admin</title>

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
            <link rel="stylesheet" href="/css/admin-dashboard.css" />

            <style>
                .staff-detail-card {
                    overflow: hidden;
                    border: 1px solid #e5e7eb;
                    border-radius: 16px;
                    background: #ffffff;
                    box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
                }

                .staff-profile {
                    display: flex;
                    align-items: center;
                    gap: 20px;
                    padding: 28px;
                    border-bottom: 1px solid #e5e7eb;
                    background: #f9fafb;
                }

                .staff-avatar {
                    width: 76px;
                    height: 76px;
                    flex: 0 0 76px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-radius: 18px;
                    background: #ecfdf5;
                    color: #047857;
                    font-size: 30px;
                    font-weight: 900;
                }

                .staff-profile__content {
                    min-width: 0;
                    flex: 1;
                }

                .staff-profile__content h3 {
                    margin: 0 0 6px;
                    color: #111827;
                    font-size: 24px;
                    font-weight: 900;
                }

                .staff-profile__email {
                    color: #6b7280;
                    font-size: 14px;
                    word-break: break-word;
                }

                .staff-profile__badges {
                    display: flex;
                    gap: 8px;
                    margin-top: 12px;
                    flex-wrap: wrap;
                }

                .detail-badge {
                    display: inline-flex;
                    align-items: center;
                    gap: 6px;
                    padding: 6px 11px;
                    border-radius: 999px;
                    font-size: 12px;
                    font-weight: 800;
                }

                .detail-badge--role {
                    border: 1px solid #fed7aa;
                    background: #fff7ed;
                    color: #c2410c;
                }

                .detail-badge--active {
                    border: 1px solid #bbf7d0;
                    background: #dcfce7;
                    color: #15803d;
                }

                .detail-badge--inactive {
                    border: 1px solid #fecaca;
                    background: #fee2e2;
                    color: #b91c1c;
                }

                .detail-badge--deleted {
                    border: 1px solid #cbd5e1;
                    background: #f1f5f9;
                    color: #475569;
                }

                .staff-detail-body {
                    padding: 28px;
                }

                .detail-section-title {
                    display: flex;
                    align-items: center;
                    gap: 9px;
                    margin: 0 0 18px;
                    color: #111827;
                    font-size: 18px;
                    font-weight: 900;
                }

                .detail-section-title i {
                    color: #f97316;
                }

                .staff-detail-grid {
                    display: grid;
                    grid-template-columns: repeat(2, minmax(0, 1fr));
                    gap: 16px;
                }

                .staff-detail-item {
                    padding: 18px;
                    border: 1px solid #e5e7eb;
                    border-radius: 14px;
                    background: #ffffff;
                }

                .staff-detail-item--full {
                    grid-column: 1 / -1;
                }

                .staff-detail-label {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    margin-bottom: 8px;
                    color: #6b7280;
                    font-size: 12px;
                    font-weight: 800;
                    letter-spacing: .06em;
                    text-transform: uppercase;
                }

                .staff-detail-label i {
                    width: 16px;
                    color: #f97316;
                    text-align: center;
                }

                .staff-detail-value {
                    color: #111827;
                    font-size: 15px;
                    font-weight: 700;
                    line-height: 1.6;
                    word-break: break-word;
                }

                .staff-detail-footer {
                    display: flex;
                    justify-content: flex-end;
                    gap: 12px;
                    padding: 20px 28px;
                    border-top: 1px solid #e5e7eb;
                    background: #f9fafb;
                }

                @media (max-width: 700px) {
                    .staff-profile {
                        align-items: flex-start;
                        flex-direction: column;
                    }

                    .staff-detail-grid {
                        grid-template-columns: 1fr;
                    }

                    .staff-detail-item--full {
                        grid-column: auto;
                    }

                    .staff-detail-footer {
                        flex-direction: column;
                    }

                    .staff-detail-footer .admin-button {
                        width: 100%;
                        justify-content: center;
                    }
                }
            </style>
        </head>

        <body class="admin-shell">

            <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

            <main class="admin-main">

                <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

                <section class="admin-content">

                    <div class="admin-toolbar">
                        <div>
                            <p class="admin-kicker">
                                <i class="fa-solid fa-user-tie"></i>
                                Staff Management
                            </p>

                            <h2>Staff Details</h2>
                            <p>View complete staff account information.</p>
                        </div>

                        <a href="/admin/staff" class="admin-button admin-button--ghost">

                            <i class="fa-solid fa-arrow-left"></i>
                            Back to Staff
                        </a>
                    </div>

                    <div class="staff-detail-card">

                        <div class="staff-profile">

                            <div class="staff-avatar">
                                <c:out value="${staff.initial}" />
                            </div>

                            <div class="staff-profile__content">

                                <h3>
                                    <c:out value="${empty staff.fullName ? 'N/A' : staff.fullName}" />
                                </h3>

                                <div class="staff-profile__email">
                                    <i class="fa-solid fa-envelope"></i>
                                    <c:out value="${empty staff.email ? 'N/A' : staff.email}" />
                                </div>

                                <div class="staff-profile__badges">

                                    <span class="detail-badge detail-badge--role">
                                        <i class="fa-solid fa-briefcase"></i>
                                        <c:out value="${empty staff.staffRole ? 'N/A' : staff.staffRole}" />
                                    </span>

                                    <c:choose>
                                        <c:when test="${staff.status}">
                                            <span class="detail-badge detail-badge--active">
                                                <i class="fa-solid fa-circle-check"></i>
                                                Active
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <span class="detail-badge detail-badge--inactive">
                                                <i class="fa-solid fa-circle-xmark"></i>
                                                Inactive
                                            </span>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:if test="${staff.deleted}">
                                        <span class="detail-badge detail-badge--deleted">
                                            <i class="fa-solid fa-trash-can"></i>
                                            Deleted
                                        </span>
                                    </c:if>

                                </div>
                            </div>
                        </div>

                        <div class="staff-detail-body">

                            <h3 class="detail-section-title">
                                <i class="fa-solid fa-address-card"></i>
                                Account Information
                            </h3>

                            <div class="staff-detail-grid">

                                <div class="staff-detail-item">
                                    <div class="staff-detail-label">
                                        <i class="fa-solid fa-id-badge"></i>
                                        Staff Code
                                    </div>

                                    <div class="staff-detail-value">
                                        <c:out value="${empty staff.staffCode ? 'N/A' : staff.staffCode}" />
                                    </div>
                                </div>

                                <div class="staff-detail-item">
                                    <div class="staff-detail-label">
                                        <i class="fa-solid fa-briefcase"></i>
                                        Staff Role
                                    </div>

                                    <div class="staff-detail-value">
                                        <c:out value="${empty staff.staffRole ? 'N/A' : staff.staffRole}" />
                                    </div>
                                </div>

                                <div class="staff-detail-item">
                                    <div class="staff-detail-label">
                                        <i class="fa-solid fa-envelope"></i>
                                        Email
                                    </div>

                                    <div class="staff-detail-value">
                                        <c:out value="${empty staff.email ? 'N/A' : staff.email}" />
                                    </div>
                                </div>

                                <div class="staff-detail-item">
                                    <div class="staff-detail-label">
                                        <i class="fa-solid fa-phone"></i>
                                        Phone
                                    </div>

                                    <div class="staff-detail-value">
                                        <c:out value="${empty staff.phone ? 'N/A' : staff.phone}" />
                                    </div>
                                </div>

                                <div class="staff-detail-item">
                                    <div class="staff-detail-label">
                                        <i class="fa-solid fa-toggle-on"></i>
                                        Account Status
                                    </div>

                                    <div class="staff-detail-value">
                                        <c:choose>
                                            <c:when test="${staff.status}">
                                                Active
                                            </c:when>

                                            <c:otherwise>
                                                Inactive
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="staff-detail-item">
                                    <div class="staff-detail-label">
                                        <i class="fa-solid fa-trash-can"></i>
                                        Deleted Status
                                    </div>

                                    <div class="staff-detail-value">
                                        <c:choose>
                                            <c:when test="${staff.deleted}">
                                                Deleted
                                            </c:when>

                                            <c:otherwise>
                                                Not Deleted
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="staff-detail-item staff-detail-item--full">
                                    <div class="staff-detail-label">
                                        <i class="fa-solid fa-location-dot"></i>
                                        Address
                                    </div>

                                    <div class="staff-detail-value">
                                        <c:out value="${empty staff.address ? 'N/A' : staff.address}" />
                                    </div>
                                </div>

                            </div>
                        </div>

                        <div class="staff-detail-footer">

                            <a href="/admin/staff" class="admin-button admin-button--ghost">

                                <i class="fa-solid fa-arrow-left"></i>
                                Back
                            </a>

                        </div>
                    </div>

                </section>
            </main>

        </body>

        </html>