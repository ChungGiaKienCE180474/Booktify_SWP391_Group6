<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Staff Management</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"/>
    <link rel="stylesheet" href="/css/admin-dashboard.css"/>

    <style>
        .staff-stats {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin: 24px 0 36px;
        }

        .staff-stat-card {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-top: 4px solid #2563eb;
            border-radius: 20px;
            padding: 24px;
            min-height: 118px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
        }

        .staff-stat-card:nth-child(2) { border-top-color: #22c55e; }
        .staff-stat-card:nth-child(3) { border-top-color: #ef4444; }
        .staff-stat-card:nth-child(4) { border-top-color: #64748b; }

        .staff-stat-info span {
            display: block;
            color: #64748b;
            font-size: 14px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .staff-stat-info strong {
            color: #111827;
            font-size: 32px;
            font-weight: 900;
        }

        .staff-stat-icon {
            width: 54px;
            height: 54px;
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            background: #dbeafe;
            color: #2563eb;
        }

        .staff-stat-card:nth-child(2) .staff-stat-icon {
            background: #dcfce7;
            color: #16a34a;
        }

        .staff-stat-card:nth-child(3) .staff-stat-icon {
            background: #fee2e2;
            color: #dc2626;
        }

        .staff-stat-card:nth-child(4) .staff-stat-icon {
            background: #f1f5f9;
            color: #475569;
        }

        .staff-toolbar {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 20px;
            padding: 18px;
            display: flex;
            gap: 12px;
            margin: 24px 0;
            box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
        }

        .staff-toolbar input,
        .staff-toolbar select {
            border: 1px solid #d1d5db;
            background: #ffffff;
            color: #111827;
            border-radius: 12px;
            padding: 13px 16px;
            outline: none;
            font-size: 14px;
        }

        .staff-toolbar input {
            flex: 1;
            min-width: 260px;
        }

        .staff-toolbar select {
            min-width: 145px;
        }

        .staff-table {
            width: 100%;
            border-collapse: collapse;
            background: #ffffff;
        }

        .staff-table th,
        .staff-table td {
            padding: 16px;
            border-bottom: 1px solid #e5e7eb;
            text-align: left;
            color: #111827;
            vertical-align: middle;
        }

        .staff-table th {
            background: #f9fafb;
            color: #64748b;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: .08em;
            font-weight: 800;
        }

        .staff-email {
            color: #2563eb;
            font-weight: 700;
        }

        .role-pill,
        .status-pill {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 11px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 800;
        }

        .role-pill {
            background: #eef2ff;
            color: #4338ca;
            border: 1px solid #c7d2fe;
        }

        .status-active {
            background: #dcfce7;
            color: #15803d;
            border: 1px solid #bbf7d0;
        }

        .status-banned {
            background: #fee2e2;
            color: #b91c1c;
            border: 1px solid #fecaca;
        }

        .staff-actions {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
            align-items: center;
        }

        .btn-small {
            border: none;
            border-radius: 10px;
            padding: 9px 13px;
            cursor: pointer;
            font-weight: 800;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            font-size: 13px;
        }

        .btn-view { background: #2563eb; color: white; }
        .btn-edit { background: #f59e0b; color: #111827; }
        .btn-ban { background: #ef4444; color: white; }
        .btn-unban { background: #22c55e; color: #052e16; }
        .btn-delete { background: #991b1b; color: white; }
        .btn-reset { background: #ffffff; color: #111827; border: 1px solid #d1d5db; }
        .btn-secondary { background: #475569; color: white; }

        .staff-empty {
            text-align: center;
            color: #64748b;
            font-weight: 700;
            padding: 32px !important;
        }

        .pagination-box {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 24px;
            flex-wrap: wrap;
            gap: 16px;
            color: #64748b;
        }

        .pagination-actions {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }

        .page-btn {
            min-width: 42px;
            justify-content: center;
        }

        .page-active {
            background: #2563eb;
            color: white;
            border: 1px solid #2563eb;
        }

        .modal-overlay {
            position: fixed;
            inset: 0;
            display: none;
            align-items: center;
            justify-content: center;
            background: rgba(15, 23, 42, 0.72);
            z-index: 9999;
            padding: 24px;
        }

        .modal-overlay.show {
            display: flex;
        }

        .staff-modal {
            width: 720px;
            max-width: 96vw;
            max-height: 90vh;
            overflow-y: auto;
            background: #ffffff;
            color: #111827;
            border-radius: 18px;
            box-shadow: 0 30px 90px rgba(0, 0, 0, .4);
        }

        .staff-modal-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 20px 24px;
            border-bottom: 1px solid #e5e7eb;
        }

        .staff-modal-header h3 {
            margin: 0;
            font-size: 22px;
            color: #111827;
        }

        .modal-close {
            border: none;
            background: transparent;
            font-size: 22px;
            cursor: pointer;
            color: #6b7280;
        }

        .staff-modal-body {
            padding: 24px;
        }

        .form-grid,
        .detail-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 16px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 7px;
        }

        .form-group.full {
            grid-column: 1 / -1;
        }

        .form-group label {
            font-size: 13px;
            font-weight: 800;
            color: #374151;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            border: 1px solid #d1d5db;
            border-radius: 12px;
            padding: 12px 14px;
            outline: none;
            color: #111827;
            background: #ffffff;
        }

        .form-group textarea {
            min-height: 84px;
            resize: vertical;
        }

        .modal-actions-row {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 22px;
        }

        .btn-submit {
            background: #2563eb;
            color: #ffffff;
        }

        .detail-profile {
            display: flex;
            align-items: center;
            gap: 16px;
            margin-bottom: 24px;
        }

        .detail-avatar {
            width: 72px;
            height: 72px;
            border-radius: 50%;
            background: #dbeafe;
            color: #1d4ed8;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 30px;
            font-weight: 900;
        }

        .detail-name {
            font-size: 22px;
            font-weight: 900;
            color: #111827;
        }

        .detail-email {
            margin-top: 4px;
            color: #6b7280;
        }

        .detail-item {
            background: #f9fafb;
            border: 1px solid #e5e7eb;
            border-radius: 14px;
            padding: 14px;
        }

        .detail-label {
            font-size: 12px;
            color: #6b7280;
            font-weight: 800;
            text-transform: uppercase;
            margin-bottom: 7px;
        }

        .detail-value {
            color: #111827;
            font-weight: 700;
            word-break: break-word;
        }

        .confirm-modal {
            width: 440px;
            max-width: 94vw;
            background: #ffffff;
            color: #111827;
            border-radius: 18px;
            padding: 26px;
            box-shadow: 0 30px 90px rgba(0,0,0,.4);
        }

        .confirm-warning {
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            color: #1e40af;
            border-radius: 12px;
            padding: 14px;
            margin: 16px 0;
            font-size: 14px;
        }

        @media (max-width: 1100px) {
            .staff-stats {
                grid-template-columns: repeat(2, 1fr);
            }

            .staff-toolbar {
                flex-direction: column;
            }

            .staff-toolbar input,
            .staff-toolbar select {
                width: 100%;
            }
        }

        @media (max-width: 700px) {
            .staff-stats,
            .form-grid,
            .detail-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body class="admin-shell">
<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp"/>

<main class="admin-main">
    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp"/>

    <section class="admin-content">

        <div class="admin-hero">
            <div>
                <p class="admin-kicker">Staff Management</p>
                <h2>Staff Management</h2>
                <p>Manage staff accounts, roles, status and information.</p>
            </div>

            <div style="display:flex; gap:12px; flex-wrap:wrap;">
                <a href="/admin/staff/deleted" class="admin-button">
                    <i class="fa-solid fa-trash-can"></i>
                    Deleted Staff
                </a>

                <button type="button" class="admin-button" onclick="openCreateModal()">
                    <i class="fa-solid fa-plus"></i>
                    Add Staff
                </button>
            </div>
        </div>

        <div class="staff-stats">
            <article class="staff-stat-card">
                <div class="staff-stat-info">
                    <span>Total Staff</span>
                    <strong>${totalStaff}</strong>
                </div>
                <div class="staff-stat-icon">
                    <i class="fa-solid fa-user-tie"></i>
                </div>
            </article>

            <article class="staff-stat-card">
                <div class="staff-stat-info">
                    <span>Active Staff</span>
                    <strong>${activeStaff}</strong>
                </div>
                <div class="staff-stat-icon">
                    <i class="fa-solid fa-user-check"></i>
                </div>
            </article>

            <article class="staff-stat-card">
                <div class="staff-stat-info">
                    <span>Inactive Staff</span>
                    <strong>${inactiveStaff}</strong>
                </div>
                <div class="staff-stat-icon">
                    <i class="fa-solid fa-user-slash"></i>
                </div>
            </article>

            <article class="staff-stat-card">
                <div class="staff-stat-info">
                    <span>Deleted Staff</span>
                    <strong>${deletedStaff}</strong>
                </div>
                <div class="staff-stat-icon">
                    <i class="fa-solid fa-trash-can"></i>
                </div>
            </article>
        </div>

        <form method="get" action="/admin/staff" class="staff-toolbar">
            <input type="hidden" name="page" value="0"/>

            <input type="text"
                   name="keyword"
                   value="${keyword}"
                   placeholder="Search by name, email, phone, address..."/>

            <select name="staffRole">
                <option value="all" ${staffRole == 'all' ? 'selected' : ''}>All Roles</option>
                <option value="Sales Staff" ${staffRole == 'Sales Staff' ? 'selected' : ''}>Sales Staff</option>
                <option value="Warehouse Staff" ${staffRole == 'Warehouse Staff' ? 'selected' : ''}>Warehouse Staff</option>
                <option value="Customer Support" ${staffRole == 'Customer Support' ? 'selected' : ''}>Customer Support</option>
            </select>

            <select name="sort">
                <option value="default" ${sort == 'default' ? 'selected' : ''}>Default</option>
                <option value="id_desc" ${sort == 'id_desc' ? 'selected' : ''}>Newest</option>
                <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name A-Z</option>
                <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name Z-A</option>
                <option value="email_asc" ${sort == 'email_asc' ? 'selected' : ''}>Email A-Z</option>
                <option value="email_desc" ${sort == 'email_desc' ? 'selected' : ''}>Email Z-A</option>
            </select>

            <select name="status">
                <option value="all" ${status == 'all' ? 'selected' : ''}>All</option>
                <option value="active" ${status == 'active' ? 'selected' : ''}>Active</option>
                <option value="inactive" ${status == 'inactive' ? 'selected' : ''}>Inactive</option>
            </select>

            <button type="submit" class="admin-button">
                <i class="fa-solid fa-filter"></i>
                Filter
            </button>

            <a href="/admin/staff" class="btn-small btn-reset">Reset</a>
        </form>

        <div class="admin-table-card">
            <table class="staff-table">
                <thead>
                <tr>
                    <th>Email</th>
                    <th>Full Name</th>
                    <th>Role</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>

                <tbody>
                <c:choose>
                    <c:when test="${empty staffList}">
                        <tr>
                            <td colspan="7" class="staff-empty">No staff found.</td>
                        </tr>
                    </c:when>

                    <c:otherwise>
                        <c:forEach var="staff" items="${staffList}">
                            <tr>
                                <td><span class="staff-email">${staff.email}</span></td>
                                <td><strong>${staff.fullName}</strong></td>
                                <td>
                                    <span class="role-pill">
                                        ${empty staff.staffRole ? 'N/A' : staff.staffRole}
                                    </span>
                                </td>
                                <td>${empty staff.phone ? 'N/A' : staff.phone}</td>
                                <td>${empty staff.address ? 'N/A' : staff.address}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${staff.status}">
                                            <span class="status-pill status-active">● Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-pill status-banned">● Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <div class="staff-actions">
                                        <button type="button"
                                                class="btn-small btn-view"
                                                onclick="openDetailModal(
                                                        '${staff.staffCode}',
                                                        '${staff.initial}',
                                                        '${staff.fullName}',
                                                        '${staff.email}',
                                                        '${empty staff.staffRole ? 'N/A' : staff.staffRole}',
                                                        '${empty staff.phone ? 'N/A' : staff.phone}',
                                                        '${empty staff.address ? 'N/A' : staff.address}',
                                                        '${staff.status}'
                                                        )">
                                            <i class="fa-solid fa-eye"></i>
                                            View
                                        </button>

                                        <button type="button"
                                                class="btn-small btn-edit"
                                                onclick="openUpdateModal(
                                                        '${staff.id}',
                                                        '${staff.fullName}',
                                                        '${empty staff.staffRole ? '' : staff.staffRole}',
                                                        '${empty staff.phone ? '' : staff.phone}',
                                                        '${empty staff.address ? '' : staff.address}'
                                                        )">
                                            <i class="fa-solid fa-pen"></i>
                                            Edit
                                        </button>

                                        <c:choose>
                                            <c:when test="${staff.status}">
                                                <button type="button"
                                                        class="btn-small btn-ban"
                                                        onclick="openConfirmModal('${staff.id}', '${staff.fullName}', '${staff.email}', 'BAN')">
                                                    <i class="fa-solid fa-ban"></i>
                                                    Ban
                                                </button>
                                            </c:when>

                                            <c:otherwise>
                                                <button type="button"
                                                        class="btn-small btn-unban"
                                                        onclick="openConfirmModal('${staff.id}', '${staff.fullName}', '${staff.email}', 'UNBAN')">
                                                    <i class="fa-solid fa-circle-check"></i>
                                                    Unban
                                                </button>
                                            </c:otherwise>
                                        </c:choose>

                                        <button type="button"
                                                class="btn-small btn-delete"
                                                onclick="openConfirmModal('${staff.id}', '${staff.fullName}', '${staff.email}', 'DELETE')">
                                            <i class="fa-solid fa-trash"></i>
                                            Delete
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>

            <c:if test="${totalPages > 0}">
                <div class="pagination-box">
                    <div>
                        Showing page ${currentPage + 1} of ${totalPages}
                        (${totalItems} staff)
                    </div>

                    <div class="pagination-actions">
                        <c:if test="${currentPage > 0}">
                            <a class="btn-small btn-reset"
                               href="/admin/staff?page=${currentPage - 1}&keyword=${keyword}&staffRole=${staffRole}&status=${status}&sort=${sort}">
                                Previous
                            </a>
                        </c:if>

                        <c:forEach begin="0" end="${totalPages - 1}" var="i">
                            <a class="btn-small page-btn ${i == currentPage ? 'page-active' : 'btn-reset'}"
                               href="/admin/staff?page=${i}&keyword=${keyword}&staffRole=${staffRole}&status=${status}&sort=${sort}">
                                ${i + 1}
                            </a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages - 1}">
                            <a class="btn-small btn-reset"
                               href="/admin/staff?page=${currentPage + 1}&keyword=${keyword}&staffRole=${staffRole}&status=${status}&sort=${sort}">
                                Next
                            </a>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>
    </section>
</main>

<div id="createModal" class="modal-overlay">
    <div class="staff-modal">
        <div class="staff-modal-header">
            <h3>Create Staff</h3>
            <button type="button" class="modal-close" onclick="closeCreateModal()">×</button>
        </div>

        <div class="staff-modal-body">
            <form method="post" action="/admin/staff/create">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="form-grid">
                    <div class="form-group">
                        <label>Full Name *</label>
                        <input type="text" name="fullName" required/>
                    </div>

                    <div class="form-group">
                        <label>Email *</label>
                        <input type="email" name="email" required/>
                    </div>

                    <div class="form-group">
                        <label>Staff Role *</label>
                        <select name="staffRole" required>
                            <option value="">Select Role</option>
                            <option value="Sales Staff">Sales Staff</option>
                            <option value="Warehouse Staff">Warehouse Staff</option>
                            <option value="Customer Support">Customer Support</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Password *</label>
                        <input type="password" name="password" required/>
                    </div>

                    <div class="form-group">
                        <label>Phone</label>
                        <input type="text" name="phone"/>
                    </div>

                    <div class="form-group full">
                        <label>Address</label>
                        <textarea name="address"></textarea>
                    </div>
                </div>

                <div class="modal-actions-row">
                    <button type="button" class="btn-small btn-reset" onclick="closeCreateModal()">Cancel</button>
                    <button type="submit" class="btn-small btn-submit">Create Staff</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div id="updateModal" class="modal-overlay">
    <div class="staff-modal">
        <div class="staff-modal-header">
            <h3>Update Staff</h3>
            <button type="button" class="modal-close" onclick="closeUpdateModal()">×</button>
        </div>

        <div class="staff-modal-body">
            <form method="post" action="/admin/staff/update">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" id="updateStaffId" name="staffId"/>

                <div class="form-grid">
                    <div class="form-group">
                        <label>Full Name *</label>
                        <input type="text" id="updateFullName" name="fullName" required/>
                    </div>

                    <div class="form-group">
                        <label>Staff Role *</label>
                        <select id="updateStaffRole" name="staffRole" required>
                            <option value="Sales Staff">Sales Staff</option>
                            <option value="Warehouse Staff">Warehouse Staff</option>
                            <option value="Customer Support">Customer Support</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>New Password</label>
                        <input type="password" name="newPassword" placeholder="Leave blank if not changing"/>
                    </div>

                    <div class="form-group">
                        <label>Phone</label>
                        <input type="text" id="updatePhone" name="phone"/>
                    </div>

                    <div class="form-group full">
                        <label>Address</label>
                        <textarea id="updateAddress" name="address"></textarea>
                    </div>
                </div>

                <div class="modal-actions-row">
                    <button type="button" class="btn-small btn-reset" onclick="closeUpdateModal()">Cancel</button>
                    <button type="submit" class="btn-small btn-submit">Update Staff</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div id="detailModal" class="modal-overlay">
    <div class="staff-modal">
        <div class="staff-modal-header">
            <h3>Staff Details</h3>
            <button type="button" class="modal-close" onclick="closeDetailModal()">×</button>
        </div>

        <div class="staff-modal-body">
            <div class="detail-profile">
                <div id="detailInitial" class="detail-avatar">S</div>
                <div>
                    <div id="detailFullName" class="detail-name">Staff Name</div>
                    <div id="detailEmail" class="detail-email">staff@email.com</div>
                </div>
            </div>

            <div class="detail-grid">
                <div class="detail-item">
                    <div class="detail-label">Staff Code</div>
                    <div id="detailCode" class="detail-value"></div>
                </div>

                <div class="detail-item">
                    <div class="detail-label">Staff Role</div>
                    <div id="detailRole" class="detail-value"></div>
                </div>

                <div class="detail-item">
                    <div class="detail-label">Status</div>
                    <div id="detailStatus" class="detail-value"></div>
                </div>

                <div class="detail-item">
                    <div class="detail-label">Phone</div>
                    <div id="detailPhone" class="detail-value"></div>
                </div>

                <div class="detail-item full">
                    <div class="detail-label">Address</div>
                    <div id="detailAddress" class="detail-value"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="confirmModal" class="modal-overlay">
    <div class="confirm-modal">
        <h3 id="confirmTitle">Confirm Action</h3>

        <p>
            <strong>Name:</strong> <span id="confirmName"></span><br>
            <strong>Email:</strong> <span id="confirmEmail"></span>
        </p>

        <div class="confirm-warning" id="confirmWarning">
            This action will change staff account status.
        </div>

        <div id="confirmTypeBox" style="margin-top:16px;">
            <p style="margin-bottom:8px;">
                To confirm, type <strong id="confirmWord">BAN</strong> below:
            </p>

            <input type="text"
                   id="confirmInput"
                   placeholder="Type to confirm"
                   oninput="checkConfirmInput()"
                   style="width:100%; padding:12px 14px; border:1px solid #d1d5db; border-radius:12px;" />
        </div>

        <form id="confirmForm" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="hidden" id="confirmStaffId" name="staffId"/>

            <div class="modal-actions-row">
                <button type="button" class="btn-small btn-reset" onclick="closeConfirmModal()">Cancel</button>
                <button type="submit" id="confirmSubmit" class="btn-small btn-ban">Confirm</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openCreateModal() {
        document.getElementById("createModal").classList.add("show");
    }

    function closeCreateModal() {
        document.getElementById("createModal").classList.remove("show");
    }

    function openUpdateModal(id, fullName, staffRole, phone, address) {
        document.getElementById("updateStaffId").value = id;
        document.getElementById("updateFullName").value = fullName || "";
        document.getElementById("updateStaffRole").value = staffRole || "Sales Staff";
        document.getElementById("updatePhone").value = phone || "";
        document.getElementById("updateAddress").value = address || "";
        document.getElementById("updateModal").classList.add("show");
    }

    function closeUpdateModal() {
        document.getElementById("updateModal").classList.remove("show");
    }

    function openDetailModal(code, initial, fullName, email, staffRole, phone, address, status) {
        document.getElementById("detailCode").innerText = code || "N/A";
        document.getElementById("detailInitial").innerText = initial || "S";
        document.getElementById("detailFullName").innerText = fullName || "N/A";
        document.getElementById("detailEmail").innerText = email || "N/A";
        document.getElementById("detailRole").innerHTML = '<span class="role-pill">' + (staffRole || "N/A") + '</span>';
        document.getElementById("detailPhone").innerText = phone || "N/A";
        document.getElementById("detailAddress").innerText = address || "N/A";

        if (status === "true") {
            document.getElementById("detailStatus").innerHTML =
                '<span class="status-pill status-active">● Active</span>';
        } else {
            document.getElementById("detailStatus").innerHTML =
                '<span class="status-pill status-banned">● Inactive</span>';
        }

        document.getElementById("detailModal").classList.add("show");
    }

    function closeDetailModal() {
        document.getElementById("detailModal").classList.remove("show");
    }

    let requiredConfirmWord = "";

    function openConfirmModal(id, fullName, email, action) {
        document.getElementById("confirmStaffId").value = id;
        document.getElementById("confirmName").innerText = fullName || "N/A";
        document.getElementById("confirmEmail").innerText = email || "N/A";

        const form = document.getElementById("confirmForm");
        const title = document.getElementById("confirmTitle");
        const warning = document.getElementById("confirmWarning");
        const submit = document.getElementById("confirmSubmit");
        const typeBox = document.getElementById("confirmTypeBox");
        const word = document.getElementById("confirmWord");
        const input = document.getElementById("confirmInput");

        submit.className = "btn-small";
        input.value = "";

        if (action === "BAN") {
            requiredConfirmWord = "BAN";
            form.action = "/admin/staff/ban";
            title.innerText = "Confirm Ban Staff";
            warning.innerText = "This staff account will be deactivated and an email notification will be sent.";
            submit.innerText = "Ban Staff";
            submit.classList.add("btn-ban");
            typeBox.style.display = "block";
            word.innerText = "BAN";
        } else if (action === "UNBAN") {
            requiredConfirmWord = "UNBAN";
            form.action = "/admin/staff/unban";
            title.innerText = "Confirm Unban Staff";
            warning.innerText = "This staff account will be activated and an email notification will be sent.";
            submit.innerText = "Unban Staff";
            submit.classList.add("btn-unban");
            typeBox.style.display = "block";
            word.innerText = "UNBAN";
        } else {
            requiredConfirmWord = "DELETE";
            form.action = "/admin/staff/delete";
            title.innerText = "Confirm Soft Delete Staff";
            warning.innerText = "This staff account will be moved to Deleted Staff. You can restore it later.";
            submit.innerText = "Delete Staff";
            submit.classList.add("btn-delete");
            typeBox.style.display = "block";
            word.innerText = "DELETE";
        }

        checkConfirmInput();
        document.getElementById("confirmModal").classList.add("show");
    }
    function checkConfirmInput() {
        const input = document.getElementById("confirmInput").value.trim();
        const submit = document.getElementById("confirmSubmit");

        if (input === requiredConfirmWord) {
            submit.disabled = false;
            submit.style.opacity = "1";
            submit.style.cursor = "pointer";
        } else {
            submit.disabled = true;
            submit.style.opacity = "0.45";
            submit.style.cursor = "not-allowed";
        }
    }

    function closeConfirmModal() {
        document.getElementById("confirmModal").classList.remove("show");
        document.getElementById("confirmInput").value = "";
        checkConfirmInput();
    }

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            closeCreateModal();
            closeUpdateModal();
            closeDetailModal();
            closeConfirmModal();
        }
    });
</script>

</body>
</html>