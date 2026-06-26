<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />

            <title>Contact Detail — Booktify Admin</title>

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
            <link rel="stylesheet" href="/css/admin-dashboard.css" />

            <style>
                .status-pill--pending {
                    background: var(--warn-lt);
                    color: var(--warn);
                    border: 1px solid #FDE68A;
                }

                .admin-alert--error {
                    background: var(--danger-lt);
                    border-color: var(--danger-bd);
                    color: var(--danger);
                }

                .contact-detail-grid {
                    display: grid;
                    grid-template-columns: minmax(0, 1.05fr) minmax(360px, .95fr);
                    gap: 20px;
                    align-items: start;
                }

                .contact-detail-column {
                    display: flex;
                    flex-direction: column;
                    gap: 20px;
                    min-width: 0;
                }

                .contact-panel-title {
                    margin: 0 0 16px;
                    font-size: .8rem;
                    font-weight: 700;
                    text-transform: uppercase;
                    letter-spacing: .1em;
                    color: var(--text-muted);
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .contact-detail-table {
                    width: 100%;
                    border-collapse: collapse;
                    font-size: .875rem;
                }

                .contact-detail-table th {
                    width: 130px;
                    padding: 12px 0;
                    color: var(--text-muted);
                    font-size: .72rem;
                    font-weight: 700;
                    text-transform: uppercase;
                    letter-spacing: .08em;
                    text-align: left;
                    vertical-align: top;
                    border-bottom: 1px solid var(--border);
                }

                .contact-detail-table td {
                    padding: 12px 0;
                    color: var(--text);
                    font-weight: 600;
                    vertical-align: top;
                    border-bottom: 1px solid var(--border);
                    word-break: break-word;
                }

                .contact-detail-table tr:last-child th,
                .contact-detail-table tr:last-child td {
                    border-bottom: none;
                }

                .contact-status-form {
                    display: flex;
                    align-items: end;
                    gap: 10px;
                    margin-top: 20px;
                    padding-top: 18px;
                    border-top: 1px solid var(--border);
                    flex-wrap: wrap;
                }

                .contact-status-form label {
                    display: block;
                    margin-bottom: 6px;
                    font-size: .72rem;
                    font-weight: 700;
                    text-transform: uppercase;
                    letter-spacing: .08em;
                    color: var(--text-muted);
                }

                .contact-message-box {
                    margin-top: 20px;
                    padding: 16px 18px;
                    border: 1px solid var(--border);
                    border-radius: var(--radius);
                    background: #F9FAFB;
                }

                .contact-message-box h4 {
                    margin: 0 0 8px;
                    font-size: .8rem;
                    color: var(--text-muted);
                    text-transform: uppercase;
                    letter-spacing: .08em;
                }

                .contact-message-box p {
                    margin: 0;
                    color: var(--text-soft);
                    line-height: 1.7;
                    white-space: pre-wrap;
                }

                .contact-image-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
                    gap: 12px;
                }

                .contact-image-item {
                    display: block;
                    border: 1px solid var(--border);
                    border-radius: var(--radius);
                    overflow: hidden;
                    background: #F9FAFB;
                    text-decoration: none;
                    color: var(--text);
                    transition: var(--ease);
                }

                .contact-image-item:hover {
                    border-color: #BFDBFE;
                    box-shadow: var(--shadow-sm);
                }

                .contact-image-item img {
                    width: 100%;
                    height: 110px;
                    object-fit: cover;
                    display: block;
                    background: #E5E7EB;
                }

                .contact-image-item span {
                    display: block;
                    padding: 8px 10px;
                    font-size: .78rem;
                    color: var(--text-muted);
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                }

                .contact-file-list {
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                    margin-top: 12px;
                }

                .contact-file-item {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    padding: 10px 12px;
                    border: 1px solid var(--border);
                    border-radius: var(--radius-sm);
                    background: #F9FAFB;
                    text-decoration: none;
                    color: var(--text-soft);
                    font-weight: 600;
                    font-size: .85rem;
                    transition: var(--ease);
                }

                .contact-file-item:hover {
                    background: var(--primary-lt);
                    border-color: #BFDBFE;
                    color: var(--primary);
                }

                .contact-muted {
                    margin: 0;
                    color: var(--text-faint);
                    font-weight: 600;
                    font-size: .875rem;
                }

                .contact-chat-box {
                    height: 420px;
                    overflow-y: auto;
                    padding: 14px;
                    border: 1px solid var(--border);
                    border-radius: var(--radius);
                    background: #F9FAFB;
                    display: flex;
                    flex-direction: column;
                    gap: 12px;
                }

                .contact-chat-message {
                    display: flex;
                    flex-direction: column;
                    align-self: flex-start;
                    align-items: flex-start;
                    width: fit-content;
                    width: -moz-fit-content;
                    max-width: 82%;
                }

                .contact-chat-message.me {
                    align-self: flex-end;
                    align-items: flex-end;
                }

                .contact-chat-message.other {
                    align-self: flex-start;
                    align-items: flex-start;
                }

                .contact-chat-meta {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    margin-bottom: 4px;
                    font-size: .72rem;
                    color: var(--text-muted);
                    width: fit-content;
                    width: -moz-fit-content;
                }

                .contact-chat-meta strong {
                    color: var(--text-soft);
                }

                .contact-chat-bubble {
                    display: inline-block;
                    width: auto;
                    min-width: 42px;
                    max-width: 100%;
                    padding: 8px 12px;
                    border-radius: 14px;
                    background: var(--white);
                    border: 1px solid var(--border);
                    color: var(--text-soft);
                    line-height: 1.45;
                    white-space: normal;
                    word-break: break-word;
                    overflow-wrap: anywhere;
                    box-shadow: var(--shadow-xs);
                }

                .contact-chat-message.me .contact-chat-bubble {
                    background: var(--primary);
                    border-color: var(--primary);
                    color: #fff;
                }

                .contact-chat-form {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    margin-top: 14px;
                }

                .contact-send-button {
                    width: 42px;
                    height: 42px;
                    padding: 0;
                }

                .contact-activity-list {
                    display: flex;
                    flex-direction: column;
                    gap: 12px;
                }

                .contact-activity-item {
                    padding: 12px 14px;
                    border: 1px solid var(--border);
                    border-radius: var(--radius-sm);
                    background: #F9FAFB;
                }

                .contact-activity-item strong {
                    display: block;
                    margin-bottom: 4px;
                    font-size: .78rem;
                    color: var(--text);
                }

                .contact-activity-item span {
                    display: block;
                    color: var(--text-muted);
                    font-size: .84rem;
                    line-height: 1.55;
                }

                @media (max-width: 1100px) {
                    .contact-detail-grid {
                        grid-template-columns: 1fr;
                    }
                }

                @media (max-width: 640px) {

                    .contact-status-form,
                    .contact-chat-form {
                        align-items: stretch;
                        flex-direction: column;
                    }

                    .contact-send-button {
                        width: 100%;
                    }

                    .contact-chat-message {
                        max-width: 100%;
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
                                <i class="fa-solid fa-headset"></i>
                                Contact Management
                            </p>
                            <h2>Contact Detail</h2>
                        </div>

                        <a href="/admin/contacts" class="admin-button admin-button--ghost">
                            <i class="fa-solid fa-arrow-left"></i>
                            Back
                        </a>
                    </div>

                    <c:if test="${not empty success}">
                        <div class="admin-alert">
                            <i class="fa-solid fa-circle-check"></i>
                            <c:out value="${success}" />
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="admin-alert admin-alert--error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            <c:out value="${error}" />
                        </div>
                    </c:if>

                    <div class="contact-detail-grid">

                        <div class="contact-detail-column">

                            <section class="admin-panel">
                                <h3 class="contact-panel-title">
                                    <i class="fa-solid fa-user"></i>
                                    Customer Details
                                </h3>

                                <table class="contact-detail-table">
                                    <tr>
                                        <th>Name</th>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty request.customer.fullName}">
                                                    <c:out value="${request.customer.fullName}" />
                                                </c:when>
                                                <c:otherwise>
                                                    Customer
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th>Email</th>
                                        <td>
                                            <c:out value="${request.customer.email}" />
                                        </td>
                                    </tr>

                                    <tr>
                                        <th>Subject</th>
                                        <td>
                                            <c:out value="${request.subject}" />
                                        </td>
                                    </tr>

                                    <tr>
                                        <th>Created At</th>
                                        <td>
                                            <c:out value="${request.formattedCreatedAt}" />
                                        </td>
                                    </tr>

                                    <tr>
                                        <th>Status</th>
                                        <td>
                                            <c:choose>
                                                <c:when test="${request.status eq 'OPEN'}">
                                                    <span class="status-pill status-pill--off">
                                                        <i class="fa-solid fa-circle-exclamation"
                                                            style="font-size:.6rem;"></i>
                                                        OPEN
                                                    </span>
                                                </c:when>

                                                <c:when test="${request.status eq 'IN_PROGRESS'}">
                                                    <span class="status-pill status-pill--pending">
                                                        <i class="fa-solid fa-spinner" style="font-size:.6rem;"></i>
                                                        IN PROGRESS
                                                    </span>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="status-pill status-pill--on">
                                                        <i class="fa-solid fa-circle-check"
                                                            style="font-size:.6rem;"></i>
                                                        <c:out value="${request.status}" />
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </table>

                                <form action="/admin/contacts/${request.id}/status" method="post"
                                    class="contact-status-form">
                                    <c:if test="${_csrf != null}">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                    </c:if>

                                    <div style="flex:1;min-width:220px;">
                                        <label>Status</label>
                                        <select name="status" class="admin-input">
                                            <c:forEach var="st" items="${statuses}">
                                                <option value="${st}" ${st eq request.status ? 'selected' : '' }>
                                                    <c:out value="${st}" />
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <button type="submit" class="admin-button">
                                        <i class="fa-solid fa-floppy-disk"></i>
                                        Save Status
                                    </button>
                                </form>

                                <div class="contact-message-box">
                                    <h4>Content</h4>
                                    <p><c:out value="${request.content}" />
                                                                           </p>
                                </div>
                            </section>

                            <section class="admin-panel">
                                <h3 class="contact-panel-title">
                                    <i class="fa-solid fa-paperclip"></i>
                                    Attachments
                                </h3>

                                <c:set var="hasImage" value="false" />
                                <c:set var="hasFile" value="false" />

                                <div class="contact-image-grid">
                                    <c:forEach var="att" items="${attachments}">
                                        <c:if test="${att.type eq 'IMAGE'}">
                                            <c:set var="hasImage" value="true" />

                                            <a href="${att.filePath}" target="_blank" class="contact-image-item">
                                                <img src="${att.filePath}" alt="${att.fileName}">
                                                <span>
                                                    <c:out value="${att.fileName}" />
                                                </span>
                                            </a>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <div class="contact-file-list">
                                    <c:forEach var="att" items="${attachments}">
                                        <c:if test="${att.type eq 'FILE'}">
                                            <c:set var="hasFile" value="true" />

                                            <a href="${att.filePath}" target="_blank" class="contact-file-item">
                                                <i class="fa-solid fa-file-arrow-down"></i>
                                                <span>
                                                    <c:out value="${att.fileName}" />
                                                </span>
                                            </a>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <c:if test="${hasImage == false && hasFile == false}">
                                    <p class="contact-muted">No attachments.</p>
                                </c:if>
                            </section>

                        </div>

                        <div class="contact-detail-column">

                            <section class="admin-panel">
                                <h3 class="contact-panel-title">
                                    <i class="fa-solid fa-comments"></i>
                                    Conversation
                                </h3>

                                <div class="contact-chat-box">
                                    <c:choose>
                                        <c:when test="${not empty messages}">
                                            <c:forEach var="msg" items="${messages}">

                                                <c:set var="isMe"
                                                    value="${msg.sender != null && msg.sender.email == currentPrincipalName}" />

                                                <div class="contact-chat-message ${isMe ? 'me' : 'other'}">
                                                    <div class="contact-chat-meta">
                                                        <strong>
                                                            <c:choose>
                                                                <c:when test="${isMe}">
                                                                    You
                                                                </c:when>

                                                                <c:when
                                                                    test="${msg.sender != null && msg.sender.email == request.customer.email}">
                                                                    Customer
                                                                </c:when>

                                                                <c:when
                                                                    test="${msg.sender != null && msg.sender.role.name == 'ADMIN'}">
                                                                    Administrator
                                                                </c:when>

                                                                <c:when
                                                                    test="${msg.sender != null && msg.sender.role.name == 'STAFF'}">
                                                                    Staff
                                                                </c:when>

                                                                <c:otherwise>
                                                                    Support Team
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </strong>

                                                        <span>
                                                            <c:out value="${msg.formattedSentAt}" />
                                                        </span>
                                                    </div>

                                                    <div class="contact-chat-bubble">
                                                        <c:out value="${msg.message}" />
                                                    </div>
                                                </div>

                                            </c:forEach>
                                        </c:when>

                                        <c:otherwise>
                                            <div
                                                style="text-align:center;padding:44px 20px;color:#9CA3AF;font-weight:600;">
                                                <i class="fa-solid fa-comments"
                                                    style="font-size:2rem;display:block;margin-bottom:10px;opacity:.35;"></i>
                                                No messages yet.
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <form action="/admin/contacts/${request.id}/chat" method="post"
                                    class="contact-chat-form">
                                    <c:if test="${_csrf != null}">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                    </c:if>

                                    <input type="text" name="message" class="admin-input"
                                        placeholder="Type your reply..." maxlength="1000" required>

                                    <button type="submit" class="admin-button contact-send-button" title="Send">
                                        <i class="fa-solid fa-paper-plane"></i>
                                    </button>
                                </form>
                            </section>

                            <section class="admin-panel">
                                <h3 class="contact-panel-title">
                                    <i class="fa-solid fa-clock-rotate-left"></i>
                                    Activity Log
                                </h3>

                                <div class="contact-activity-list">
                                    <div class="contact-activity-item">
                                        <strong>
                                            <c:out value="${request.formattedCreatedAt}" />
                                        </strong>
                                        <span>Customer created this request.</span>
                                    </div>

                                    <c:if test="${not empty request.staff}">
                                        <div class="contact-activity-item">
                                            <strong>Staff</strong>
                                            <span>
                                                Handled by
                                                <c:out value="${request.staff.email}" />
                                            </span>
                                        </div>
                                    </c:if>

                                    <div class="contact-activity-item">
                                        <strong>Status</strong>
                                        <span>
                                            Current status:
                                            <c:out value="${request.status}" />
                                        </span>
                                    </div>

                                    <c:forEach var="msg" items="${messages}">
                                        <div class="contact-activity-item">
                                            <strong>
                                                <c:out value="${msg.formattedSentAt}" />
                                            </strong>

                                            <span>
                                                Message from
                                                <c:choose>
                                                    <c:when
                                                        test="${msg.sender != null && msg.sender.email == request.customer.email}">
                                                        Customer
                                                    </c:when>

                                                    <c:when
                                                        test="${msg.sender != null && msg.sender.role.name == 'ADMIN'}">
                                                        Administrator
                                                    </c:when>

                                                    <c:when
                                                        test="${msg.sender != null && msg.sender.role.name == 'STAFF'}">
                                                        Staff
                                                    </c:when>

                                                    <c:otherwise>
                                                        Support Team
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </c:forEach>
                                </div>
                            </section>

                        </div>

                    </div>

                </section>
            </main>
        </body>

        </html>