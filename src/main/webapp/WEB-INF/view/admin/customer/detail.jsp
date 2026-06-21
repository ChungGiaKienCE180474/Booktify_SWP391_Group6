<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Customer Detail</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />

    <style>
        .customer-detail-card {
            max-width: 820px;
            margin: 0 auto;
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 24px;
            padding: 28px;
            box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
        }

        .customer-profile-header {
            display: flex;
            align-items: center;
            gap: 20px;
            padding-bottom: 24px;
            border-bottom: 1px solid #e5e7eb;
            margin-bottom: 24px;
        }

        .customer-avatar {
            width: 82px;
            height: 82px;
            border-radius: 24px;
            background: #dbeafe;
            color: #1d4ed8;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 34px;
            font-weight: 900;
        }

        .customer-name {
            font-size: 26px;
            font-weight: 900;
            color: #111827;
        }

        .customer-email {
            color: #2563eb;
            margin-top: 6px;
            font-weight: 700;
        }

        .customer-info-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 18px;
        }

        .customer-info-box {
            background: #f9fafb;
            border: 1px solid #e5e7eb;
            border-radius: 18px;
            padding: 18px;
        }

        .customer-label {
            color: #64748b;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: .08em;
            margin-bottom: 8px;
            font-weight: 800;
        }

        .customer-value {
            color: #111827;
            font-size: 16px;
            font-weight: 700;
            word-break: break-word;
        }

        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 7px 12px;
            border-radius: 999px;
            font-size: 14px;
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
            gap: 12px;
            margin-top: 26px;
            flex-wrap: wrap;
        }

        .btn-action {
            border: none;
            border-radius: 14px;
            padding: 12px 18px;
            cursor: pointer;
            font-weight: 800;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all .2s ease;
        }

        .btn-action:hover {
            transform: translateY(-1px);
            box-shadow: 0 8px 18px rgba(15, 23, 42, 0.12);
        }

        .btn-back {
            background: #ffffff;
            color: #111827;
            border: 1px solid #d1d5db;
        }

        .btn-ban {
            background: #ef4444;
            color: white;
        }

        .btn-unban {
            background: #22c55e;
            color: #052e16;
        }

        .modal-overlay {
            position: fixed;
            inset: 0;
            background: rgba(15, 23, 42, 0.72);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 9999;
            padding: 24px;
        }

        .modal-overlay.show {
            display: flex;
        }

        .confirm-modal {
            width: 430px;
            max-width: 94vw;
            background: #ffffff;
            color: #1f2937;
            border-radius: 18px;
            padding: 26px;
            box-shadow: 0 30px 80px rgba(0, 0, 0, 0.35);
        }

        .confirm-modal h3 {
            margin: 0 0 12px;
            font-size: 22px;
            color: #111827;
        }

        .confirm-modal p {
            color: #4b5563;
            line-height: 1.5;
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

        .confirm-modal input[type="text"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #d1d5db;
            border-radius: 10px;
            margin-top: 10px;
            outline: none;
            color: #111827;
            background: #ffffff;
        }

        .modal-actions {
            display: flex;
            gap: 12px;
            margin-top: 20px;
        }

        .modal-actions button {
            flex: 1;
            border: none;
            border-radius: 12px;
            padding: 12px;
            font-weight: 800;
            cursor: pointer;
        }

        .btn-cancel {
            background: #f3f4f6;
            color: #374151;
        }

        .btn-confirm-ban {
            background: #ef4444;
            color: white;
            opacity: .45;
            cursor: not-allowed;
        }

        .btn-confirm-unban {
            background: #22c55e;
            color: #052e16;
            opacity: .45;
            cursor: not-allowed;
        }

        .btn-enabled {
            opacity: 1;
            cursor: pointer;
        }

        @media (max-width: 780px) {
            .customer-info-grid {
                grid-template-columns: 1fr;
            }

            .customer-profile-header {
                align-items: flex-start;
                flex-direction: column;
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
                <p class="admin-kicker">Customer Detail</p>
                <h2>Customer Details</h2>
                <p>View customer information and manage account status.</p>
            </div>

            <a href="/admin/customers" class="admin-button">
                <i class="fa-solid fa-arrow-left"></i>
                Back to Customers
            </a>
        </div>

        <div class="customer-detail-card">

            <div class="customer-profile-header">
                <div class="customer-avatar">
                    ${customer.initial}
                </div>

                <div>
                    <div class="customer-name">${customer.fullName}</div>
                    <div class="customer-email">${customer.email}</div>
                </div>
            </div>

            <div class="customer-info-grid">

                <div class="customer-info-box">
                    <div class="customer-label">Customer ID</div>
                    <div class="customer-value">${customer.id}</div>
                </div>

                <div class="customer-info-box">
                    <div class="customer-label">Account Status</div>
                    <div class="customer-value">
                        <c:choose>
                            <c:when test="${customer.status}">
                                <span class="status-badge status-active">● Active</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-badge status-banned">● Inactive</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="customer-info-box">
                    <div class="customer-label">Full Name</div>
                    <div class="customer-value">${customer.fullName}</div>
                </div>

                <div class="customer-info-box">
                    <div class="customer-label">Email</div>
                    <div class="customer-value">${customer.email}</div>
                </div>

                <div class="customer-info-box">
                    <div class="customer-label">Phone</div>
                    <div class="customer-value">${empty customer.phone ? 'N/A' : customer.phone}</div>
                </div>

                <div class="customer-info-box">
                    <div class="customer-label">Address</div>
                    <div class="customer-value">${empty customer.address ? 'N/A' : customer.address}</div>
                </div>

            </div>

            <div class="customer-actions">
                <a href="/admin/customers" class="btn-action btn-back">
                    <i class="fa-solid fa-arrow-left"></i>
                    Back
                </a>

                <c:choose>
                    <c:when test="${customer.status}">
                        <button type="button"
                                class="btn-action btn-ban"
                                onclick="openBanModal()">
                            <i class="fa-solid fa-ban"></i>
                            Ban Account
                        </button>
                    </c:when>

                    <c:otherwise>
                        <button type="button"
                                class="btn-action btn-unban"
                                onclick="openUnbanModal()">
                            <i class="fa-solid fa-circle-check"></i>
                            Unban Account
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>

    </section>
</main>

<div id="banModal" class="modal-overlay">
    <div class="confirm-modal">
        <h3>⚠️ Confirm Account Deactivation</h3>

        <p>
            You are about to deactivate the account of customer:
        </p>

        <p>
            <strong>Name:</strong> ${customer.fullName}<br>
            <strong>Email:</strong> ${customer.email}
        </p>

        <div class="confirm-warning">
            This action will:<br>
            - Prevent the customer from logging in<br>
            - Send notification email to customer<br>
            - Can be undone later
        </div>

        <p>To confirm, type <strong>BAN</strong> below:</p>

        <form method="post" action="/admin/customers/ban">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="userId" value="${customer.id}" />

            <input type="text"
                   id="banInput"
                   placeholder="Type BAN to confirm"
                   oninput="checkBanInput()" />

            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeBanModal()">Cancel</button>
                <button type="submit" id="banSubmit" class="btn-confirm-ban" disabled>
                    Deactivate Account
                </button>
            </div>
        </form>
    </div>
</div>

<div id="unbanModal" class="modal-overlay">
    <div class="confirm-modal">
        <h3>✅ Confirm Account Activation</h3>

        <p>
            You are about to activate the account of customer:
        </p>

        <p>
            <strong>Name:</strong> ${customer.fullName}<br>
            <strong>Email:</strong> ${customer.email}
        </p>

        <div class="confirm-warning">
            This action will:<br>
            - Allow the customer to log in again<br>
            - Send notification email to customer<br>
            - Can be changed later
        </div>

        <p>To confirm, type <strong>UNBAN</strong> below:</p>

        <form method="post" action="/admin/customers/unban">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="userId" value="${customer.id}" />

            <input type="text"
                   id="unbanInput"
                   placeholder="Type UNBAN to confirm"
                   oninput="checkUnbanInput()" />

            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeUnbanModal()">Cancel</button>
                <button type="submit" id="unbanSubmit" class="btn-confirm-unban" disabled>
                    Activate Account
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    function openBanModal() {
        document.getElementById("banModal").classList.add("show");
    }

    function closeBanModal() {
        document.getElementById("banModal").classList.remove("show");
        document.getElementById("banInput").value = "";
        checkBanInput();
    }

    function checkBanInput() {
        const input = document.getElementById("banInput").value.trim();
        const submit = document.getElementById("banSubmit");

        if (input === "BAN") {
            submit.disabled = false;
            submit.classList.add("btn-enabled");
        } else {
            submit.disabled = true;
            submit.classList.remove("btn-enabled");
        }
    }

    function openUnbanModal() {
        document.getElementById("unbanModal").classList.add("show");
    }

    function closeUnbanModal() {
        document.getElementById("unbanModal").classList.remove("show");
        document.getElementById("unbanInput").value = "";
        checkUnbanInput();
    }

    function checkUnbanInput() {
        const input = document.getElementById("unbanInput").value.trim();
        const submit = document.getElementById("unbanSubmit");

        if (input === "UNBAN") {
            submit.disabled = false;
            submit.classList.add("btn-enabled");
        } else {
            submit.disabled = true;
            submit.classList.remove("btn-enabled");
        }
    }

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            closeBanModal();
            closeUnbanModal();
        }
    });
</script>

</body>
</html>