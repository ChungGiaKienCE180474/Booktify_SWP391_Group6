<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />

    <title>Contacts — Booktify Staff</title>

    <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <style>
        .staff-table-wrap {
            width: 100%;
            overflow-x: auto;
        }

        .staff-data-table {
            width: 100%;
            border-collapse: collapse;
        }

        .staff-data-table th,
        .staff-data-table td {
            padding: 18px 14px;
            border-bottom: 1px solid #E5E7EB;
            text-align: left;
            vertical-align: middle;
        }

        .staff-data-table th {
            color: #6B7280;
            font-size: .78rem;
            font-weight: 900;
            text-transform: uppercase;
            letter-spacing: .08em;
            white-space: nowrap;
        }

        .staff-status {
            display: inline-flex;
            align-items: center;
            gap: 7px;
            padding: 7px 12px;
            border-radius: 999px;
            font-size: .78rem;
            font-weight: 900;
            white-space: nowrap;
        }

        .staff-status.open {
            background: #DBEAFE;
            color: #1D4ED8;
        }

        .staff-status.progress {
            background: #FEF3C7;
            color: #B45309;
        }

        .staff-status.complete {
            background: #DCFCE7;
            color: #15803D;
        }

        .staff-view-btn {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            border: 1px solid #E5E7EB;
            background: #FFFFFF;
            color: #006B5E;
            cursor: pointer;
        }

        .staff-view-btn:hover {
            background: #E3F4F1;
        }

        .staff-flash {
            padding: 13px 16px;
            border-radius: 12px;
            margin-bottom: 16px;
            font-weight: 800;
        }

        .staff-flash.success {
            background: #DCFCE7;
            color: #15803D;
        }

        .staff-flash.error {
            background: #FEE2E2;
            color: #B91C1C;
        }

        .staff-modal-overlay {
            position: fixed;
            inset: 0;
            z-index: 1000;
            display: none;
            align-items: center;
            justify-content: center;
            padding: 24px;
            background: rgba(15, 23, 42, .55);
        }

        .staff-modal-box {
            width: min(760px, 100%);
            max-height: 90vh;
            overflow-y: auto;
            background: #FFFFFF;
            border-radius: 18px;
            box-shadow: 0 24px 80px rgba(15, 23, 42, .28);
        }

        .staff-modal-header {
            padding: 18px 22px;
            border-bottom: 1px solid #E5E7EB;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .staff-modal-header h3 {
            margin: 0;
            color: #0F172A;
            font-size: 1.2rem;
            font-weight: 900;
        }

        .staff-modal-close {
            border: 0;
            background: transparent;
            color: #64748B;
            font-size: 1.2rem;
            cursor: pointer;
        }

        .staff-modal-body {
            padding: 22px;
        }

        .staff-detail-row {
            display: grid;
            grid-template-columns: 150px 1fr;
            gap: 14px;
            padding: 11px 0;
            border-bottom: 1px dashed #E5E7EB;
        }

        .staff-detail-row span {
            color: #64748B;
            font-weight: 800;
        }

        .staff-detail-row strong {
            color: #111827;
            font-weight: 800;
            white-space: pre-line;
        }

        .staff-modal-actions {
            margin-top: 22px;
            display: grid;
            grid-template-columns: 1fr;
            gap: 16px;
        }

        .staff-modal-form {
            padding: 16px;
            border: 1px solid #E5E7EB;
            border-radius: 14px;
            background: #F9FAFB;
        }

        .staff-modal-form label {
            display: block;
            margin-bottom: 8px;
            color: #374151;
            font-weight: 900;
            font-size: .9rem;
        }

        .staff-modal-form select,
        .staff-modal-form textarea {
            width: 100%;
            border: 1px solid #D1D5DB;
            border-radius: 10px;
            padding: 11px 12px;
            font: inherit;
            background: #FFFFFF;
        }

        .staff-modal-form textarea {
            min-height: 110px;
            resize: vertical;
        }

        .staff-modal-form button {
            margin-top: 12px;
        }
    </style>
</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/staff/sidebar.jsp" />

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/staff/header.jsp" />

    <section class="admin-content">

        <div class="admin-toolbar">
            <div>
                <p class="admin-kicker">
                    <i class="fa-solid fa-headset"></i>
                    Contact Requests
                </p>

                <h2>Contacts</h2>
            </div>
        </div>

        <c:if test="${not empty success}">
            <div class="staff-flash success">
                <c:out value="${success}" />
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="staff-flash error">
                <c:out value="${error}" />
            </div>
        </c:if>

        <div class="admin-panel" style="padding:14px 22px;margin-bottom:24px;">
            <form action="${ctx}/staff/contacts" method="get" class="admin-search-form">

                <input type="text"
                       name="keyword"
                       value="<c:out value='${keyword}' />"
                       class="admin-input"
                       placeholder="Search contact requests..." />

                <select name="status" class="admin-input" style="max-width:220px;">
                    <option value="">All Status</option>

                    <c:forEach items="${statuses}" var="st">
                        <option value="${st}" ${selectedStatus == st ? 'selected' : ''}>
                            <c:choose>
                                <c:when test="${st == 'OPEN'}">Open</c:when>
                                <c:when test="${st == 'IN_PROGRESS'}">In Progress</c:when>
                                <c:when test="${st == 'COMPLETE'}">Completed</c:when>
                                <c:otherwise>${st}</c:otherwise>
                            </c:choose>
                        </option>
                    </c:forEach>
                </select>

                <button type="submit" class="admin-button">
                    <i class="fa-solid fa-filter"></i>
                    Filter
                </button>

                <a href="${ctx}/staff/contacts" class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-rotate-right"></i>
                    Reset
                </a>
            </form>
        </div>

        <div class="admin-panel">
            <div class="staff-table-wrap">
                <table class="staff-data-table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Issue Type</th>
                        <th>Email</th>
                        <th>Status</th>
                        <th>Created</th>
                        <th>View</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:choose>
                        <c:when test="${empty requests}">
                            <tr>
                                <td colspan="6" style="text-align:center;padding:42px;color:#6B7280;">
                                    No contact requests found.
                                </td>
                            </tr>
                        </c:when>

                        <c:otherwise>
                            <c:forEach items="${requests}" var="request" varStatus="loop">
                                <tr>
                                    <td>${fromItem + loop.index}</td>

                                    <td>
                                        <strong>
                                            <c:out value="${request.issueType}" default="General Support" />
                                        </strong>
                                    </td>

                                    <td>
                                        <c:out value="${request.contactEmail}" default="—" />
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${request.status == 'OPEN'}">
                                                <span class="staff-status open">
                                                    <i class="fa-solid fa-envelope-open-text"></i>
                                                    Open
                                                </span>
                                            </c:when>

                                            <c:when test="${request.status == 'IN_PROGRESS'}">
                                                <span class="staff-status progress">
                                                    <i class="fa-solid fa-spinner"></i>
                                                    In Progress
                                                </span>
                                            </c:when>

                                            <c:otherwise>
                                                <span class="staff-status complete">
                                                    <i class="fa-solid fa-circle-check"></i>
                                                    Completed
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:out value="${request.createdAtFormatted}" />
                                    </td>

                                    <td>
                                        <button type="button"
                                                class="staff-view-btn js-view-contact"
                                                data-id="${request.id}"
                                                data-issue="<c:out value='${request.issueType}' />"
                                                data-email="<c:out value='${request.contactEmail}' />"
                                                data-status="${request.status}"
                                                data-status-text="<c:out value='${request.statusText}' />"
                                                data-created="<c:out value='${request.createdAtFormatted}' />"
                                                data-content="<c:out value='${request.content}' />">
                                            <i class="fa-solid fa-eye"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

    </section>

</main>

<div id="contactModal" class="staff-modal-overlay" onclick="closeContactModal()">
    <div class="staff-modal-box" onclick="event.stopPropagation()">

        <div class="staff-modal-header">
            <h3>
                <i class="fa-solid fa-headset" style="color:#006B5E;"></i>
                Contact Request Details
            </h3>

            <button type="button" class="staff-modal-close" onclick="closeContactModal()">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </div>

        <div class="staff-modal-body">

            <div class="staff-detail-row">
                <span>Issue Type</span>
                <strong id="mContactIssue"></strong>
            </div>

            <div class="staff-detail-row">
                <span>Email</span>
                <strong id="mContactEmail"></strong>
            </div>

            <div class="staff-detail-row">
                <span>Status</span>
                <strong id="mContactStatus"></strong>
            </div>

            <div class="staff-detail-row">
                <span>Created</span>
                <strong id="mContactCreated"></strong>
            </div>

            <div class="staff-detail-row">
                <span>Content</span>
                <strong id="mContactContent"></strong>
            </div>

            <div class="staff-modal-actions">

                <form id="contactStatusForm" method="post" class="staff-modal-form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <label for="modalStatus">Update Status</label>

                    <select name="status" id="modalStatus">
                        <option value="OPEN">Open</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="COMPLETE">Completed</option>
                    </select>

                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-floppy-disk"></i>
                        Save Status
                    </button>
                </form>

                <form id="contactReplyForm" method="post" class="staff-modal-form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <label for="modalMessage">Reply Message</label>

                    <textarea name="message"
                              id="modalMessage"
                              placeholder="Enter reply message..."
                              required></textarea>

                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-paper-plane"></i>
                        Send Reply
                    </button>
                </form>

            </div>

        </div>

    </div>
</div>

<script>
    document.querySelectorAll('.js-view-contact').forEach(function (button) {
        button.addEventListener('click', function () {
            const id = this.dataset.id;
            const status = this.dataset.status || 'OPEN';

            document.getElementById('mContactIssue').textContent = this.dataset.issue || 'General Support';
            document.getElementById('mContactEmail').textContent = this.dataset.email || '—';
            document.getElementById('mContactStatus').textContent = this.dataset.statusText || status;
            document.getElementById('mContactCreated').textContent = this.dataset.created || '—';
            document.getElementById('mContactContent').textContent = this.dataset.content || '—';

            document.getElementById('modalStatus').value = status;

            document.getElementById('contactStatusForm').action =
                '${ctx}/staff/contact/' + id + '/status';

            document.getElementById('contactReplyForm').action =
                '${ctx}/staff/contact/' + id + '/chat';

            document.getElementById('contactModal').style.display = 'flex';
        });
    });

    function closeContactModal() {
        document.getElementById('contactModal').style.display = 'none';
    }
</script>

</body>
</html>