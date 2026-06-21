<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Customer Management</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />

    <style>
        .customer-stats {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
            margin: 24px 0 36px;
        }

        .customer-stat-card {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-top: 4px solid #2563eb;
            border-radius: 20px;
            padding: 26px 28px;
            min-height: 118px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
        }

        .customer-stat-card:nth-child(2) {
            border-top-color: #22c55e;
        }

        .customer-stat-card:nth-child(3) {
            border-top-color: #ef4444;
        }

        .customer-stat-info span {
            display: block;
            color: #64748b;
            font-size: 15px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .customer-stat-info strong {
            color: #111827;
            font-size: 34px;
            font-weight: 900;
        }

        .customer-stat-icon {
            width: 56px;
            height: 56px;
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            background: #dbeafe;
            color: #2563eb;
        }

        .customer-stat-card:nth-child(2) .customer-stat-icon {
            background: #dcfce7;
            color: #16a34a;
        }

        .customer-stat-card:nth-child(3) .customer-stat-icon {
            background: #fee2e2;
            color: #dc2626;
        }

        .customer-toolbar {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 20px;
            padding: 18px;
            display: flex;
            gap: 12px;
            margin: 24px 0;
            box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
        }

        .customer-toolbar input,
        .customer-toolbar select {
            border: 1px solid #d1d5db;
            background: #ffffff;
            color: #111827;
            border-radius: 12px;
            padding: 13px 16px;
            outline: none;
            font-size: 14px;
        }

        .customer-toolbar input {
            flex: 1;
            min-width: 260px;
        }

        .customer-toolbar input::placeholder {
            color: #9ca3af;
        }

        .customer-toolbar select {
            min-width: 145px;
        }

        .customer-table {
            width: 100%;
            border-collapse: collapse;
            background: #ffffff;
        }

        .customer-table th,
        .customer-table td {
            padding: 16px;
            border-bottom: 1px solid #e5e7eb;
            text-align: left;
            color: #111827;
            vertical-align: middle;
        }

        .customer-table th {
            background: #f9fafb;
            color: #64748b;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: .08em;
            font-weight: 800;
        }

        .customer-table tbody tr:hover {
            background: #f8fafc;
        }

        .customer-email {
            color: #2563eb;
            font-weight: 700;
        }

        .status-pill {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 11px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 800;
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

        .customer-actions {
            display: flex;
            gap: 8px;
            align-items: center;
            flex-wrap: wrap;
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
            transition: all .2s ease;
        }

        .btn-small:hover {
            transform: translateY(-1px);
            box-shadow: 0 8px 18px rgba(15, 23, 42, 0.12);
        }

        .btn-view {
            background: #2563eb;
            color: #ffffff;
        }

        .btn-ban {
            background: #ef4444;
            color: #ffffff;
        }

        .btn-unban {
            background: #22c55e;
            color: #052e16;
        }

        .btn-reset {
            background: #ffffff;
            color: #111827;
            border: 1px solid #d1d5db;
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

        .pagination-info {
            font-size: 14px;
        }

        .pagination-actions {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
            align-items: center;
        }

        .page-btn {
            min-width: 42px;
            justify-content: center;
        }

        .page-active {
            background: #2563eb;
            color: #ffffff;
            border: 1px solid #2563eb;
        }

        .customer-empty {
            text-align: center;
            color: #64748b;
            font-weight: 700;
            padding: 32px !important;
        }

        .customer-modal-overlay,
        .status-modal-overlay {
            position: fixed;
            inset: 0;
            display: none;
            align-items: center;
            justify-content: center;
            background: rgba(15, 23, 42, 0.72);
            z-index: 9999;
            padding: 24px;
        }

        .customer-modal-overlay.show,
        .status-modal-overlay.show {
            display: flex;
        }

        .customer-modal {
            width: 640px;
            max-width: 96vw;
            max-height: 88vh;
            overflow-y: auto;
            background: #ffffff;
            color: #1f2937;
            border-radius: 18px;
            box-shadow: 0 30px 90px rgba(0, 0, 0, 0.4);
        }

        .customer-modal-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 20px 24px;
            border-bottom: 1px solid #e5e7eb;
        }

        .customer-modal-header h3 {
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

        .customer-modal-body {
            padding: 24px;
        }

        .modal-profile {
            display: flex;
            align-items: center;
            gap: 16px;
            margin-bottom: 24px;
        }

        .modal-avatar {
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

        .modal-name {
            font-size: 20px;
            font-weight: 900;
            color: #111827;
        }

        .modal-email {
            color: #6b7280;
            margin-top: 4px;
        }

        .modal-info-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 16px;
            margin-bottom: 22px;
        }

        .modal-info-item {
            background: #f9fafb;
            border: 1px solid #e5e7eb;
            border-radius: 14px;
            padding: 14px;
        }

        .modal-label {
            font-size: 12px;
            color: #6b7280;
            margin-bottom: 6px;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: .06em;
        }

        .modal-value {
            font-size: 14px;
            color: #111827;
            font-weight: 700;
            word-break: break-word;
        }

        .modal-badge {
            display: inline-flex;
            padding: 6px 10px;
            border-radius: 999px;
            font-weight: 800;
            font-size: 12px;
        }

        .modal-badge-active {
            background: #dcfce7;
            color: #15803d;
        }

        .modal-badge-inactive {
            background: #fee2e2;
            color: #b91c1c;
        }

        .order-section {
            border-top: 1px solid #e5e7eb;
            padding-top: 18px;
        }

        .order-section h4 {
            margin: 0 0 14px;
            color: #111827;
        }

        .order-empty {
            background: #f9fafb;
            border: 1px dashed #d1d5db;
            border-radius: 14px;
            padding: 18px;
            color: #6b7280;
            text-align: center;
        }

        .status-modal {
            width: 430px;
            max-width: 94vw;
            background: #ffffff;
            color: #1f2937;
            border-radius: 18px;
            padding: 26px;
            box-shadow: 0 30px 90px rgba(0, 0, 0, 0.4);
        }

        .status-modal h3 {
            margin: 0 0 12px;
            color: #111827;
            font-size: 21px;
        }

        .status-modal p {
            color: #4b5563;
            line-height: 1.5;
        }

        .status-warning {
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            color: #1e40af;
            border-radius: 12px;
            padding: 14px;
            margin: 16px 0;
            font-size: 14px;
        }

        .status-modal input {
            width: 100%;
            padding: 12px;
            border: 1px solid #d1d5db;
            border-radius: 10px;
            margin-top: 10px;
            outline: none;
            color: #111827;
            background: #ffffff;
        }

        .status-modal-actions {
            display: flex;
            gap: 12px;
            margin-top: 20px;
        }

        .status-modal-actions button {
            flex: 1;
            border: none;
            border-radius: 12px;
            padding: 12px;
            font-weight: 800;
            cursor: pointer;
        }

        .status-cancel {
            background: #f3f4f6;
            color: #374151;
        }

        .status-confirm {
            background: #ef4444;
            color: white;
            opacity: .45;
            cursor: not-allowed;
        }

        .status-confirm.unban {
            background: #22c55e;
            color: #052e16;
        }

        .status-confirm.enabled {
            opacity: 1;
            cursor: pointer;
        }

        @media (max-width: 900px) {
            .customer-stats {
                grid-template-columns: 1fr;
            }

            .customer-toolbar {
                flex-direction: column;
            }

            .customer-toolbar input,
            .customer-toolbar select {
                width: 100%;
            }

            .modal-info-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body class="admin-shell">
<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

<main class="admin-main">
    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

    <section class="admin-content">

        <div class="admin-hero">
            <div>
                <p class="admin-kicker">Customer Management</p>
                <h2>Customer Management</h2>
                <p>Manage customer information and account status.</p>
            </div>
        </div>

        <div class="customer-stats">
            <article class="customer-stat-card">
                <div class="customer-stat-info">
                    <span>Total Customers</span>
                    <strong>${totalCustomers}</strong>
                </div>
                <div class="customer-stat-icon">
                    <i class="fa-solid fa-users"></i>
                </div>
            </article>

            <article class="customer-stat-card">
                <div class="customer-stat-info">
                    <span>Active Customers</span>
                    <strong>${activeCustomers}</strong>
                </div>
                <div class="customer-stat-icon">
                    <i class="fa-solid fa-user-check"></i>
                </div>
            </article>

            <article class="customer-stat-card">
                <div class="customer-stat-info">
                    <span>Inactive Customers</span>
                    <strong>${inactiveCustomers}</strong>
                </div>
                <div class="customer-stat-icon">
                    <i class="fa-solid fa-user-slash"></i>
                </div>
            </article>
        </div>

        <form method="get" action="/admin/customers" class="customer-toolbar">
            <input type="hidden" name="page" value="0" />

            <input type="text"
                   name="keyword"
                   value="${keyword}"
                   placeholder="Search by name, email, phone, address..." />

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

            <a href="/admin/customers" class="btn-small btn-reset">
                Reset
            </a>
        </form>

        <div class="admin-table-card">
            <table class="customer-table">
                <thead>
                <tr>
                    <th>Email</th>
                    <th>Full Name</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>

                <tbody>
                <c:choose>
                    <c:when test="${empty customers}">
                        <tr>
                            <td colspan="6" class="customer-empty">No customers found.</td>
                        </tr>
                    </c:when>

                    <c:otherwise>
                        <c:forEach var="customer" items="${customers}">
                            <tr>
                                <td>
                                    <span class="customer-email">${customer.email}</span>
                                </td>
                                <td><strong>${customer.fullName}</strong></td>
                                <td>${empty customer.phone ? 'N/A' : customer.phone}</td>
                                <td>${empty customer.address ? 'N/A' : customer.address}</td>

                                <td>
                                    <c:choose>
                                        <c:when test="${customer.status}">
                                            <span class="status-pill status-active">● Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-pill status-banned">● Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <div class="customer-actions">
                                        <button type="button"
                                                class="btn-small btn-view"
                                                onclick="openCustomerModal(
                                                        '${customer.customerCode}',
                                                        '${customer.initial}',
                                                        '${customer.fullName}',
                                                        '${customer.email}',
                                                        '${empty customer.phone ? 'N/A' : customer.phone}',
                                                        '${empty customer.address ? 'N/A' : customer.address}',
                                                        '${customer.status}'
                                                        )">
                                            <i class="fa-solid fa-eye"></i>
                                            View
                                        </button>

                                        <c:choose>
                                            <c:when test="${customer.status}">
                                                <button type="button"
                                                        class="btn-small btn-ban"
                                                        onclick="openStatusModal('${customer.id}', '${customer.fullName}', '${customer.email}', 'BAN')">
                                                    <i class="fa-solid fa-ban"></i>
                                                    Ban
                                                </button>
                                            </c:when>

                                            <c:otherwise>
                                                <button type="button"
                                                        class="btn-small btn-unban"
                                                        onclick="openStatusModal('${customer.id}', '${customer.fullName}', '${customer.email}', 'UNBAN')">
                                                    <i class="fa-solid fa-circle-check"></i>
                                                    Unban
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
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
                    <div class="pagination-info">
                        Showing page ${currentPage + 1} of ${totalPages}
                        (${totalItems} customers)
                    </div>

                    <div class="pagination-actions">
                        <c:if test="${currentPage > 0}">
                            <a class="btn-small btn-reset"
                               href="/admin/customers?page=${currentPage - 1}&keyword=${keyword}&status=${status}&sort=${sort}">
                                Previous
                            </a>
                        </c:if>

                        <c:forEach begin="0" end="${totalPages - 1}" var="i">
                            <a class="btn-small page-btn ${i == currentPage ? 'page-active' : 'btn-reset'}"
                               href="/admin/customers?page=${i}&keyword=${keyword}&status=${status}&sort=${sort}">
                                ${i + 1}
                            </a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages - 1}">
                            <a class="btn-small btn-reset"
                               href="/admin/customers?page=${currentPage + 1}&keyword=${keyword}&status=${status}&sort=${sort}">
                                Next
                            </a>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>

    </section>
</main>

<div id="customerDetailModal" class="customer-modal-overlay" onclick="closeCustomerModalOnOverlay(event)">
    <div class="customer-modal">
        <div class="customer-modal-header">
            <h3>Customer Details</h3>
            <button type="button" class="modal-close" onclick="closeCustomerModal()">×</button>
        </div>

        <div class="customer-modal-body">
            <div class="modal-profile">
                <div id="modalInitial" class="modal-avatar">U</div>
                <div>
                    <div id="modalFullName" class="modal-name">Customer Name</div>
                    <div id="modalEmail" class="modal-email">customer@email.com</div>
                </div>
            </div>

            <div class="modal-info-grid">
                <div class="modal-info-item">
                    <div class="modal-label">Phone Number</div>
                    <div id="modalPhone" class="modal-value">N/A</div>
                </div>

                <div class="modal-info-item">
                    <div class="modal-label">Address</div>
                    <div id="modalAddress" class="modal-value">N/A</div>
                </div>

                <div class="modal-info-item">
                    <div class="modal-label">Status</div>
                    <div id="modalStatus" class="modal-value"></div>
                </div>

                <div class="modal-info-item">
                    <div class="modal-label">Customer Code</div>
                    <div id="modalCustomerId" class="modal-value"></div>
                </div>
            </div>

            <div class="order-section">
                <h4><i class="fa-solid fa-cart-shopping"></i> Order History</h4>
                <div class="order-empty">
                    Order history is not connected yet.
                </div>
            </div>
        </div>
    </div>
</div>

<div id="statusModal" class="status-modal-overlay">
    <div class="status-modal">
        <h3 id="statusModalTitle">Confirm Account Action</h3>

        <p>You are about to change the account status of customer:</p>

        <p>
            <strong>Name:</strong> <span id="statusCustomerName"></span><br>
            <strong>Email:</strong> <span id="statusCustomerEmail"></span>
        </p>

        <div class="status-warning" id="statusWarningText">
            This action will change customer account status.
        </div>

        <p>
            To confirm, type <strong id="statusConfirmWord">BAN</strong> below:
        </p>

        <form id="statusForm" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" id="statusUserId" name="userId" />

            <input type="text"
                   id="statusInput"
                   placeholder="Type to confirm"
                   oninput="checkStatusInput()" />

            <div class="status-modal-actions">
                <button type="button" class="status-cancel" onclick="closeStatusModal()">
                    Cancel
                </button>

                <button type="submit" id="statusSubmit" class="status-confirm" disabled>
                    Confirm
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    function openCustomerModal(id, initial, fullName, email, phone, address, status) {
        document.getElementById("modalCustomerId").innerText = id;
        document.getElementById("modalInitial").innerText = initial || "U";
        document.getElementById("modalFullName").innerText = fullName || "N/A";
        document.getElementById("modalEmail").innerText = email || "N/A";
        document.getElementById("modalPhone").innerText = phone || "N/A";
        document.getElementById("modalAddress").innerText = address || "N/A";

        const statusBox = document.getElementById("modalStatus");

        if (status === "true") {
            statusBox.innerHTML = '<span class="modal-badge modal-badge-active">● Active</span>';
        } else {
            statusBox.innerHTML = '<span class="modal-badge modal-badge-inactive">● Inactive</span>';
        }

        document.getElementById("customerDetailModal").classList.add("show");
    }

    function closeCustomerModal() {
        document.getElementById("customerDetailModal").classList.remove("show");
    }

    function closeCustomerModalOnOverlay(event) {
        if (event.target.id === "customerDetailModal") {
            closeCustomerModal();
        }
    }

    let requiredStatusWord = "BAN";

    function openStatusModal(userId, fullName, email, action) {
        requiredStatusWord = action;

        document.getElementById("statusUserId").value = userId;
        document.getElementById("statusCustomerName").innerText = fullName || "N/A";
        document.getElementById("statusCustomerEmail").innerText = email || "N/A";
        document.getElementById("statusConfirmWord").innerText = action;
        document.getElementById("statusInput").value = "";

        const title = document.getElementById("statusModalTitle");
        const warning = document.getElementById("statusWarningText");
        const form = document.getElementById("statusForm");
        const submit = document.getElementById("statusSubmit");

        if (action === "BAN") {
            title.innerText = "⚠️ Confirm Account Deactivation";
            warning.innerHTML =
                "This action will:<br>" +
                "- Prevent the customer from logging in<br>" +
                "- Send notification email to customer<br>" +
                "- Can be undone later";

            form.action = "/admin/customers/ban";
            submit.innerText = "Deactivate Account";
            submit.classList.remove("unban");
        } else {
            title.innerText = "✅ Confirm Account Activation";
            warning.innerHTML =
                "This action will:<br>" +
                "- Allow the customer to log in again<br>" +
                "- Send notification email to customer<br>" +
                "- Can be changed later";

            form.action = "/admin/customers/unban";
            submit.innerText = "Activate Account";
            submit.classList.add("unban");
        }

        checkStatusInput();
        document.getElementById("statusModal").classList.add("show");
    }

    function closeStatusModal() {
        document.getElementById("statusModal").classList.remove("show");
    }

    function checkStatusInput() {
        const input = document.getElementById("statusInput").value.trim();
        const submit = document.getElementById("statusSubmit");

        if (input === requiredStatusWord) {
            submit.disabled = false;
            submit.classList.add("enabled");
        } else {
            submit.disabled = true;
            submit.classList.remove("enabled");
        }
    }

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            closeCustomerModal();
            closeStatusModal();
        }
    });
</script>

</body>
</html>