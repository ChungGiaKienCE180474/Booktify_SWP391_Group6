<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Deleted Staff</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"/>
    <link rel="stylesheet" href="/css/admin-dashboard.css"/>

    <style>
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
        }

        .staff-table th {
            background: #f9fafb;
            color: #64748b;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: .08em;
            font-weight: 800;
        }

        .role-pill {
            display: inline-flex;
            padding: 6px 11px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 800;
            background: #eef2ff;
            color: #4338ca;
            border: 1px solid #c7d2fe;
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

        .btn-restore {
            background: #22c55e;
            color: #052e16;
        }

        .btn-reset {
            background: #ffffff;
            color: #111827;
            border: 1px solid #d1d5db;
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
    </style>
</head>

<body class="admin-shell">
<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp"/>

<main class="admin-main">
    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp"/>

    <section class="admin-content">
        <div class="admin-hero">
            <div>
                <p class="admin-kicker">Recycle Bin</p>
                <h2>Deleted Staff</h2>
                <p>View and restore soft-deleted staff accounts.</p>
            </div>

            <a href="/admin/staff" class="admin-button">
                <i class="fa-solid fa-arrow-left"></i>
                Back to Staff
            </a>
        </div>

        <form method="get" action="/admin/staff/deleted" class="staff-toolbar">
            <input type="text"
                   name="keyword"
                   value="${keyword}"
                   placeholder="Search deleted staff..."/>

            <select name="staffRole">
                <option value="all" ${staffRole == 'all' ? 'selected' : ''}>All Roles</option>
                <option value="Sales Staff" ${staffRole == 'Sales Staff' ? 'selected' : ''}>Sales Staff</option>
                <option value="Warehouse Staff" ${staffRole == 'Warehouse Staff' ? 'selected' : ''}>Warehouse Staff</option>
                <option value="Customer Support" ${staffRole == 'Customer Support' ? 'selected' : ''}>Customer Support</option>
            </select>

            <button type="submit" class="admin-button">
                <i class="fa-solid fa-filter"></i>
                Filter
            </button>

            <a href="/admin/staff/deleted" class="btn-small btn-reset">Reset</a>
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
                    <th>Action</th>
                </tr>
                </thead>

                <tbody>
                <c:choose>
                    <c:when test="${empty staffList}">
                        <tr>
                            <td colspan="6" style="text-align:center; padding:32px; color:#64748b; font-weight:700;">
                                No deleted staff found.
                            </td>
                        </tr>
                    </c:when>

                    <c:otherwise>
                        <c:forEach var="staff" items="${staffList}">
                            <tr>
                                <td>${staff.email}</td>
                                <td><strong>${staff.fullName}</strong></td>
                                <td>
                                    <span class="role-pill">
                                        ${empty staff.staffRole ? 'N/A' : staff.staffRole}
                                    </span>
                                </td>
                                <td>${empty staff.phone ? 'N/A' : staff.phone}</td>
                                <td>${empty staff.address ? 'N/A' : staff.address}</td>
                                <td>
                                    <form method="post" action="/admin/staff/restore">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <input type="hidden" name="staffId" value="${staff.id}"/>

                                        <button type="submit" class="btn-small btn-restore">
                                            <i class="fa-solid fa-rotate-left"></i>
                                            Restore
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </section>
</main>
</body>
</html>