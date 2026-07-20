<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="selectedStatusValue" value="${not empty selectedStatusName ? selectedStatusName : param.status}" />

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />

            <title>Contact Support — Booktify</title>

            <link rel="stylesheet" href="${ctx}/css/header.css" />
            <link rel="stylesheet" href="${ctx}/css/footer.css" />
            <link rel="stylesheet" href="${ctx}/css/homepage.css" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

            <style>
                .contact-page {
                    background: #F5F7FB;
                    min-height: 100vh;
                }

                .contact-wrap {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 1.5rem 1.5rem 3.5rem;
                    display: grid;
                    grid-template-columns: 220px 1fr;
                    gap: 1.5rem;
                    align-items: start;
                }

                .contact-sidebar,
                .contact-toolbar,
                .request-panel {
                    background: #fff;
                    border: 1px solid var(--border);
                    border-radius: var(--radius-lg);
                    box-shadow: var(--shadow-sm);
                }

                .contact-sidebar {
                    overflow: hidden;
                    position: sticky;
                    top: 112px;
                }

                .sidebar-head {
                    background: var(--primary);
                    color: #fff;
                    padding: .75rem 1rem;
                    font-size: .8rem;
                    font-weight: 900;
                    text-transform: uppercase;
                    letter-spacing: .08em;
                    display: flex;
                    align-items: center;
                    gap: .55rem;
                }

                .sidebar-list {
                    padding: .5rem 0;
                }

                .sidebar-item {
                    display: flex;
                    align-items: center;
                    gap: .55rem;
                    padding: .65rem 1rem;
                    text-decoration: none;
                    color: var(--text-muted);
                    font-size: .9rem;
                    font-weight: 650;
                }

                .sidebar-item:hover,
                .sidebar-item.active {
                    background: rgba(0, 107, 94, .08);
                    color: var(--primary);
                }

                .sidebar-item.active {
                    font-weight: 850;
                }

                .sidebar-item .dot {
                    width: 7px;
                    height: 7px;
                    border-radius: 50%;
                    background: #E5E7EB;
                    flex-shrink: 0;
                }

                .sidebar-item:hover .dot,
                .sidebar-item.active .dot {
                    background: var(--primary);
                }

                .contact-main {
                    min-width: 0;
                }

                .contact-breadcrumb {
                    display: flex;
                    align-items: center;
                    gap: .45rem;
                    font-size: .82rem;
                    color: var(--text-muted);
                    margin-bottom: 1rem;
                }

                .contact-breadcrumb a {
                    color: var(--text-muted);
                    text-decoration: none;
                }

                .contact-breadcrumb a:hover {
                    color: var(--primary);
                }

                .contact-breadcrumb i {
                    font-size: .6rem;
                    opacity: .45;
                }

                .contact-toolbar {
                    padding: 1.35rem 1.5rem;
                    margin-bottom: 1.25rem;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 1rem;
                    flex-wrap: wrap;
                }

                .contact-kicker {
                    margin: 0 0 .35rem;
                    color: var(--accent-warm, #F57C00);
                    font-size: .72rem;
                    font-weight: 900;
                    letter-spacing: .12em;
                    text-transform: uppercase;
                    display: flex;
                    align-items: center;
                    gap: .45rem;
                }

                .contact-toolbar h1 {
                    margin: 0;
                    font-size: 1.45rem;
                    line-height: 1.2;
                    font-weight: 900;
                    color: var(--text, #111827);
                }

                .contact-toolbar p {
                    margin: .35rem 0 0;
                    color: var(--text-muted, #6B7280);
                    font-size: .86rem;
                    line-height: 1.5;
                }

                .contact-button {
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    gap: .45rem;
                    min-height: 42px;
                    padding: 0 1.2rem;
                    border-radius: var(--radius);
                    border: 0;
                    background: var(--primary);
                    color: #fff;
                    font-weight: 850;
                    font-size: .88rem;
                    cursor: pointer;
                    text-decoration: none;
                    box-shadow: 0 8px 16px rgba(0, 107, 94, .16);
                }

                .contact-button:hover {
                    background: var(--primary-light);
                    transform: translateY(-1px);
                }

                .contact-alert {
                    border-radius: var(--radius);
                    padding: .85rem 1rem;
                    margin-bottom: 1rem;
                    font-size: .88rem;
                    font-weight: 750;
                    display: flex;
                    align-items: center;
                    gap: .55rem;
                }

                .contact-alert--success {
                    background: #ECFDF5;
                    color: #047857;
                    border: 1px solid #BBF7D0;
                }

                .contact-alert--danger {
                    background: #FEF2F2;
                    color: #DC2626;
                    border: 1px solid #FECACA;
                }

                .request-panel {
                    overflow: hidden;
                }

                .contact-card-head {
                    padding: 1rem 1.25rem;
                    border-bottom: 1px solid var(--border);
                    display: flex;
                    align-items: center;
                    gap: .65rem;
                }

                .contact-card-head i {
                    color: var(--primary);
                }

                .contact-card-head h2 {
                    margin: 0;
                    font-size: 1.05rem;
                    font-weight: 900;
                    color: var(--text);
                }

                .request-table-wrap {
                    width: 100%;
                    overflow-x: auto;
                }

                .request-table {
                    width: 100%;
                    border-collapse: collapse;
                }

                .request-table th,
                .request-table td {
                    padding: 1rem .9rem;
                    border-bottom: 1px solid #E5E7EB;
                    text-align: left;
                    vertical-align: middle;
                }

                .request-table th {
                    font-size: .74rem;
                    color: #6B7280;
                    font-weight: 900;
                    letter-spacing: .08em;
                    text-transform: uppercase;
                    white-space: nowrap;
                }

                .request-table td {
                    font-size: .9rem;
                    color: var(--text);
                }

                .request-id {
                    width: 56px;
                    text-align: center !important;
                    color: #6B7280 !important;
                    font-weight: 800;
                }

                .request-subject {
                    min-width: 220px;
                    font-weight: 850;
                    color: var(--text);
                }

                .status-pill-contact {
                    display: inline-flex;
                    align-items: center;
                    gap: .35rem;
                    min-height: 30px;
                    padding: 0 .7rem;
                    border-radius: 999px;
                    font-size: .72rem;
                    font-weight: 900;
                    white-space: nowrap;
                }

                .status-open {
                    background: #EFF6FF;
                    color: #2563EB;
                    border: 1px solid #BFDBFE;
                }

                .status-progress {
                    background: #FFF7ED;
                    color: #EA580C;
                    border: 1px solid #FED7AA;
                }

                .status-completed {
                    background: #ECFDF5;
                    color: #059669;
                    border: 1px solid #BBF7D0;
                }

                .status-other {
                    background: #F3F4F6;
                    color: #6B7280;
                    border: 1px solid #E5E7EB;
                }

                .request-view {
                    width: 190px;
                    text-align: center !important;
                }

                .contact-action-box {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: .5rem;
                }

                .contact-action-button {
                    height: 38px;
                    padding: 0 .8rem;
                    border: 1px solid #E5E7EB;
                    border-radius: 10px;
                    color: #6B7280;
                    background: #fff;
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    gap: .4rem;
                    font-size: .78rem;
                    font-weight: 800;
                    cursor: pointer;
                }

                .contact-action-button:hover {
                    color: var(--primary);
                    border-color: rgba(0, 107, 94, .25);
                    background: rgba(0, 107, 94, .06);
                }

                .contact-action-button--chat {
                    color: var(--primary);
                }

                .contact-action-button--chat:hover {
                    background: var(--primary);
                    color: #fff;
                    border-color: var(--primary);
                }

                .empty-state {
                    padding: 3.5rem 1rem;
                    text-align: center;
                    color: var(--text-muted);
                }

                .empty-state i {
                    display: block;
                    font-size: 2.8rem;
                    opacity: .25;
                    margin-bottom: .8rem;
                }

                .empty-state h3 {
                    margin: 0 0 .3rem;
                    font-size: 1rem;
                    color: var(--text);
                    font-weight: 900;
                }

                .empty-state p {
                    margin: 0;
                    font-size: .84rem;
                    line-height: 1.5;
                }

                .request-summary {
                    padding: .9rem 1.25rem;
                    border-top: 1px solid var(--border);
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: .75rem;
                    color: var(--text-muted);
                    font-size: .84rem;
                    flex-wrap: wrap;
                }

                .request-summary strong {
                    color: var(--text);
                }

                .contact-pagination {
                    display: flex;
                    gap: .35rem;
                    flex-wrap: wrap;
                }

                .contact-page-link {
                    min-width: 32px;
                    height: 32px;
                    padding: 0 .55rem;
                    border-radius: 7px;
                    border: 1px solid var(--border);
                    background: #fff;
                    color: var(--text-muted);
                    text-decoration: none;
                    font-size: .76rem;
                    font-weight: 800;
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                }

                .contact-page-link.active,
                .contact-page-link:hover {
                    background: var(--primary);
                    border-color: var(--primary);
                    color: #fff;
                }

                .contact-modal-overlay,
                .contact-chat-overlay {
                    position: fixed;
                    inset: 0;
                    z-index: 9999;
                    background: rgba(15, 23, 42, .45);
                    display: none;
                    align-items: center;
                    justify-content: center;
                    padding: 1.25rem;
                }

                .contact-modal-box {
                    width: min(680px, 100%);
                    background: #fff;
                    border-radius: 18px;
                    box-shadow: 0 24px 60px rgba(15, 23, 42, .22);
                    overflow: hidden;
                }

                .contact-modal-header {
                    padding: 1rem 1.25rem;
                    border-bottom: 1px solid var(--border);
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 1rem;
                }

                .contact-modal-header h3 {
                    margin: 0;
                    font-size: 1.05rem;
                    font-weight: 900;
                    color: var(--text);
                    display: flex;
                    align-items: center;
                    gap: .55rem;
                }

                .contact-modal-close,
                .contact-chat-close {
                    width: 36px;
                    height: 36px;
                    border-radius: 10px;
                    border: 1px solid #E5E7EB;
                    background: #fff;
                    color: #6B7280;
                    cursor: pointer;
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                }

                .contact-modal-close:hover,
                .contact-chat-close:hover {
                    background: #F9FAFB;
                    color: #111827;
                }

                .contact-modal-body {
                    padding: 1.25rem;
                }

                .contact-modal-row {
                    display: grid;
                    grid-template-columns: 140px 1fr;
                    gap: 1rem;
                    padding: .75rem 0;
                    border-bottom: 1px solid #F1F5F9;
                }

                .contact-modal-row:last-child {
                    border-bottom: 0;
                }

                .contact-modal-label {
                    color: #6B7280;
                    font-size: .78rem;
                    font-weight: 900;
                    letter-spacing: .06em;
                    text-transform: uppercase;
                }

                .contact-modal-value {
                    color: #111827;
                    font-size: .9rem;
                    font-weight: 700;
                    line-height: 1.55;
                    word-break: break-word;
                }

                .contact-modal-content {
                    white-space: pre-line;
                }

                .contact-detail-files {
                    display: grid;
                    gap: .75rem;
                }

                .contact-detail-file {
                    display: inline-flex;
                    align-items: center;
                    gap: .5rem;
                    width: fit-content;
                    max-width: 100%;
                    padding: .55rem .7rem;
                    border: 1px solid #E5E7EB;
                    border-radius: 10px;
                    background: #F9FAFB;
                    color: #374151;
                    text-decoration: none;
                    font-size: .84rem;
                    font-weight: 750;
                }

                .contact-detail-file:hover {
                    color: var(--primary);
                    border-color: rgba(0, 107, 94, .25);
                    background: rgba(0, 107, 94, .05);
                }

                .contact-attachment-image {
                    max-width: 190px;
                    max-height: 150px;
                    object-fit: cover;
                    border-radius: 12px;
                    border: 1px solid #E5E7EB;
                    background: #F9FAFB;
                    display: block;
                }

                .contact-chat-box {
                    width: min(620px, 96vw);
                    height: 560px;
                    max-height: 82vh;
                    background: #fff;
                    border-radius: 16px;
                    box-shadow: 0 24px 60px rgba(15, 23, 42, .24);
                    overflow: hidden;
                    display: flex;
                    flex-direction: column;
                }

                .contact-chat-header {
                    padding: .95rem 1.15rem;
                    border-bottom: 1px solid #E5E7EB;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 1rem;
                    background: #fff;
                    flex-shrink: 0;
                }

                .contact-chat-title h3 {
                    margin: 0;
                    font-size: 1rem;
                    font-weight: 900;
                    color: #111827;
                    display: flex;
                    align-items: center;
                    gap: .5rem;
                }

                .contact-chat-title h3 i {
                    color: var(--primary);
                }

                .contact-chat-title p {
                    margin: .22rem 0 0;
                    color: #6B7280;
                    font-size: .8rem;
                    font-weight: 600;
                }

                .contact-chat-messages {
                    flex: 1;
                    min-height: 0;
                    overflow-y: auto;
                    padding: 1rem 1.1rem;
                    background: #F8FAFC;
                    display: flex;
                    flex-direction: column;
                    gap: .6rem;
                    scroll-behavior: smooth;
                }

                .contact-chat-messages::-webkit-scrollbar {
                    width: 7px;
                }

                .contact-chat-messages::-webkit-scrollbar-track {
                    background: #EEF2F7;
                }

                .contact-chat-messages::-webkit-scrollbar-thumb {
                    background: #CBD5E1;
                    border-radius: 999px;
                }

                .chat-row {
                    display: flex;
                    flex-direction: column;
                    width: fit-content;
                    max-width: 74%;
                }

                .chat-row.is-me {
                    align-self: flex-end;
                    align-items: flex-start;
                }

                .chat-row.is-other {
                    align-self: flex-start;
                    align-items: flex-start;
                }

                .chat-meta {
                    display: flex;
                    align-items: center;
                    gap: .55rem;
                    margin-bottom: .25rem;
                    padding-left: .15rem;
                    color: #6B7280;
                    font-size: .78rem;
                    line-height: 1.2;
                }

                .chat-meta strong {
                    color: #374151;
                    font-size: .82rem;
                    font-weight: 900;
                }

                .chat-time {
                    color: #6B7280;
                    font-size: .78rem;
                    font-weight: 500;
                }

                .chat-bubble {
                    display: inline-flex;
                    align-items: center;
                    width: fit-content;
                    max-width: 330px;
                    min-width: 0;
                    min-height: 0;
                    padding: 8px 14px;
                    border-radius: 15px;
                    box-sizing: border-box;
                    font-size: .9rem;
                    line-height: 1.35;
                    box-shadow: 0 2px 8px rgba(15, 23, 42, .05);
                }

                .chat-bubble-text {
                    display: inline;
                    white-space: pre-line;
                    word-break: break-word;
                }

                .chat-row.is-me .chat-bubble {
                    background: var(--primary);
                    color: #fff;
                    border: 1px solid var(--primary);
                }

                .chat-row.is-other .chat-bubble {
                    background: #fff;
                    color: #111827;
                    border: 1px solid #E5E7EB;
                }

                .empty-chat {
                    margin: auto;
                    text-align: center;
                    color: #9CA3AF;
                    font-size: .86rem;
                    font-weight: 700;
                }

                .contact-chat-form {
                    padding: .85rem;
                    border-top: 1px solid #E5E7EB;
                    display: flex;
                    gap: .65rem;
                    background: #fff;
                    flex-shrink: 0;
                }

                .contact-chat-input {
                    flex: 1;
                    height: 42px;
                    border: 1px solid #D1D5DB;
                    border-radius: 11px;
                    padding: 0 .85rem;
                    font: inherit;
                    font-size: .86rem;
                    outline: none;
                    background: #fff;
                }

                .contact-chat-input:focus {
                    border-color: var(--primary);
                    box-shadow: 0 0 0 3px rgba(0, 107, 94, .1);
                }

                .contact-chat-send {
                    width: 46px;
                    height: 42px;
                    border: 0;
                    border-radius: 11px;
                    background: var(--primary);
                    color: #fff;
                    cursor: pointer;
                    font-size: .95rem;
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    box-shadow: 0 8px 16px rgba(0, 107, 94, .14);
                }

                .contact-chat-send:hover {
                    background: var(--primary-light);
                    transform: translateY(-1px);
                }

                @media (max-width: 980px) {
                    .contact-wrap {
                        grid-template-columns: 1fr;
                    }

                    .contact-sidebar {
                        position: static;
                    }

                    .sidebar-list {
                        display: flex;
                        flex-wrap: wrap;
                        gap: .2rem;
                        padding: .4rem;
                    }

                    .sidebar-item {
                        border-radius: var(--radius);
                        padding: .5rem .75rem;
                    }
                }

                @media (max-width: 640px) {
                    .contact-wrap {
                        padding: 1rem 1rem 2.5rem;
                    }

                    .contact-toolbar {
                        padding: 1.1rem;
                    }

                    .contact-toolbar h1 {
                        font-size: 1.25rem;
                    }

                    .contact-button {
                        width: 100%;
                    }

                    .contact-modal-row {
                        grid-template-columns: 1fr;
                        gap: .35rem;
                    }

                    .contact-chat-box {
                        width: 96vw;
                        height: 78vh;
                        border-radius: 14px;
                    }

                    .chat-row {
                        max-width: 88%;
                    }

                    .chat-bubble {
                        max-width: 280px;
                    }

                    .contact-chat-messages {
                        padding: .85rem;
                    }
                }
            </style>
        </head>

        <body class="home-page contact-page">

            <jsp:include page="/WEB-INF/view/layout/header.jsp" />

            <div class="contact-wrap">
                <aside class="contact-sidebar">
                    <div class="sidebar-head">
                        <i class="fa-solid fa-bars"></i>
                        Contact Status
                    </div>

                    <div class="sidebar-list">
                        <a href="${ctx}/contact" class="sidebar-item ${empty selectedStatusValue ? 'active' : ''}">
                            <span class="dot"></span>
                            All Requests
                        </a>

                        <a href="${ctx}/contact?status=OPEN"
                            class="sidebar-item ${selectedStatusValue == 'OPEN' ? 'active' : ''}">
                            <span class="dot"></span>
                            Open
                        </a>

                        <a href="${ctx}/contact?status=IN_PROGRESS"
                            class="sidebar-item ${selectedStatusValue == 'IN_PROGRESS' ? 'active' : ''}">
                            <span class="dot"></span>
                            In Progress
                        </a>

                        <a href="${ctx}/contact?status=COMPLETE"
                            class="sidebar-item ${selectedStatusValue == 'COMPLETE' ? 'active' : ''}">
                            <span class="dot"></span>
                            Completed
                        </a>
                    </div>
                </aside>

                <main class="contact-main">
                    <nav class="contact-breadcrumb">
                        <a href="${ctx}/">Home</a>
                        <i class="fa-solid fa-chevron-right"></i>
                        <span>Contact Support</span>
                    </nav>

                    <section class="contact-toolbar">
                        <div>
                            <p class="contact-kicker">
                                <i class="fa-solid fa-paper-plane"></i>
                                Customer Support
                            </p>
                            <h1>Contact Support</h1>
                            <p>Create a support request and track all requests you have submitted.</p>
                        </div>

                        <a href="${ctx}/contact/create" class="contact-button">
                            <i class="fa-solid fa-plus"></i>
                            Create Contact
                        </a>
                    </section>

                    <c:if test="${not empty success}">
                        <div class="contact-alert contact-alert--success">
                            <i class="fa-solid fa-circle-check"></i>
                            <c:out value="${success}" />
                        </div>
                    </c:if>

                    <c:if test="${not empty successMessage}">
                        <div class="contact-alert contact-alert--success">
                            <i class="fa-solid fa-circle-check"></i>
                            <c:out value="${successMessage}" />
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="contact-alert contact-alert--danger">
                            <i class="fa-solid fa-triangle-exclamation"></i>
                            <c:out value="${error}" />
                        </div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="contact-alert contact-alert--danger">
                            <i class="fa-solid fa-triangle-exclamation"></i>
                            <c:out value="${errorMessage}" />
                        </div>
                    </c:if>

                    <section class="request-panel">
                        <div class="contact-card-head">
                            <i class="fa-solid fa-list-check"></i>
                            <h2>Support Requests</h2>
                        </div>

                        <c:choose>
                            <c:when test="${empty requests}">
                                <div class="empty-state">
                                    <i class="fa-regular fa-message"></i>
                                    <h3>No support requests yet</h3>
                                    <p>Your submitted support requests will appear here.</p>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <div class="request-table-wrap">
                                    <table class="request-table">
                                        <thead>
                                            <tr>
                                                <th class="request-id">#</th>
                                                <th>Subject</th>
                                                <th>Status</th>
                                                <th class="request-view">Actions</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            <c:forEach items="${requests}" var="request">
                                                <c:set var="statusText" value="${request.status}" />

                                                <tr>
                                                    <td class="request-id">
                                                        <c:out value="${request.id}" />
                                                    </td>

                                                    <td class="request-subject">
                                                        <c:choose>
                                                            <c:when test="${not empty request.issueType}">
                                                                <c:out value="${request.issueType}" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:out value="${request.subject}" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>

                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${statusText == 'OPEN'}">
                                                                <span class="status-pill-contact status-open">
                                                                    <i class="fa-solid fa-circle"></i>
                                                                    Open
                                                                </span>
                                                            </c:when>

                                                            <c:when test="${statusText == 'IN_PROGRESS'}">
                                                                <span class="status-pill-contact status-progress">
                                                                    <i class="fa-solid fa-clock"></i>
                                                                    In Progress
                                                                </span>
                                                            </c:when>

                                                            <c:when test="${statusText == 'COMPLETE'}">
                                                                <span class="status-pill-contact status-completed">
                                                                    <i class="fa-solid fa-circle-check"></i>
                                                                    Completed
                                                                </span>
                                                            </c:when>

                                                            <c:otherwise>
                                                                <span class="status-pill-contact status-other">
                                                                    <i class="fa-solid fa-circle-info"></i>
                                                                    <c:out value="${statusText}" />
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>

                                                    <td class="request-view">
                                                        <div class="contact-action-box">
                                                            <button type="button"
                                                                class="contact-action-button js-open-detail"
                                                                data-detail-id="contactDetailModal-${request.id}">
                                                                <i class="fa-solid fa-eye"></i>
                                                                View
                                                            </button>

                                                            <button type="button"
                                                                class="contact-action-button contact-action-button--chat js-open-chat"
                                                                data-chat-id="contactChatModal-${request.id}">
                                                                <i class="fa-solid fa-comments"></i>
                                                                Chat
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <div class="request-summary">
                            <div>
                                Total:
                                <strong>
                                    <c:choose>
                                        <c:when test="${not empty totalItems}">
                                            <c:out value="${totalItems}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${requests.size()}" />
                                        </c:otherwise>
                                    </c:choose>
                                </strong>
                                Requests
                            </div>

                            <c:if test="${totalPages > 1}">
                                <div class="contact-pagination">
                                    <c:forEach begin="0" end="${totalPages - 1}" var="pageIndex">
                                        <a href="${ctx}/contact?page=${pageIndex}&status=${selectedStatusValue}"
                                            class="contact-page-link ${pageIndex == currentPage ? 'active' : ''}">
                                            ${pageIndex + 1}
                                        </a>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </section>
                </main>
            </div>

            <c:forEach items="${requests}" var="request">
                <c:set var="requestAttachments" value="${attachmentsByRequestId[request.id]}" />
                <c:set var="requestMessages" value="${messagesByRequestId[request.id]}" />

                <div id="contactDetailModal-${request.id}" class="contact-modal-overlay"
                    onclick="closeContactDetailModal('contactDetailModal-${request.id}')">

                    <div class="contact-modal-box" onclick="event.stopPropagation()">
                        <div class="contact-modal-header">
                            <h3>
                                <i class="fa-solid fa-message" style="color:var(--primary);"></i>
                                Support Request Details
                            </h3>

                            <button type="button" class="contact-modal-close"
                                onclick="closeContactDetailModal('contactDetailModal-${request.id}')">
                                <i class="fa-solid fa-xmark"></i>
                            </button>
                        </div>

                        <div class="contact-modal-body">
                            <div class="contact-modal-row">
                                <div class="contact-modal-label">ID</div>
                                <div class="contact-modal-value">#
                                    <c:out value="${request.id}" />
                                </div>
                            </div>

                            <div class="contact-modal-row">
                                <div class="contact-modal-label">Issue Type</div>
                                <div class="contact-modal-value">
                                    <c:choose>
                                        <c:when test="${not empty request.issueType}">
                                            <c:out value="${request.issueType}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${request.subject}" />
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="contact-modal-row">
                                <div class="contact-modal-label">Email</div>
                                <div class="contact-modal-value">
                                    <c:choose>
                                        <c:when test="${not empty request.contactEmail}">
                                            <c:out value="${request.contactEmail}" />
                                        </c:when>
                                        <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="contact-modal-row">
                                <div class="contact-modal-label">Status</div>
                                <div class="contact-modal-value">
                                    <c:choose>
                                        <c:when test="${request.status == 'OPEN'}">
                                            <span class="status-pill-contact status-open">
                                                <i class="fa-solid fa-circle"></i>
                                                Open
                                            </span>
                                        </c:when>

                                        <c:when test="${request.status == 'IN_PROGRESS'}">
                                            <span class="status-pill-contact status-progress">
                                                <i class="fa-solid fa-clock"></i>
                                                In Progress
                                            </span>
                                        </c:when>

                                        <c:when test="${request.status == 'COMPLETE'}">
                                            <span class="status-pill-contact status-completed">
                                                <i class="fa-solid fa-circle-check"></i>
                                                Completed
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <span class="status-pill-contact status-other">
                                                <i class="fa-solid fa-circle-info"></i>
                                                <c:out value="${request.status}" />
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="contact-modal-row">
                                <div class="contact-modal-label">Created Date</div>
                                <div class="contact-modal-value">
                                    <c:out value="${request.createdAtFormatted}" />
                                </div>
                            </div>

                            <div class="contact-modal-row">
                                <div class="contact-modal-label">Content</div>
                                <div class="contact-modal-value contact-modal-content">
                                    <c:out value="${request.content}" />
                                </div>
                            </div>

                            <div class="contact-modal-row">
                                <div class="contact-modal-label">Attachments</div>
                                <div class="contact-modal-value">
                                    <c:choose>
                                        <c:when test="${not empty requestAttachments}">
                                            <div class="contact-detail-files">
                                                <c:forEach items="${requestAttachments}" var="attachment">
                                                    <div>
                                                        <a href="${ctx}${attachment.filePath}" target="_blank"
                                                            class="contact-detail-file">
                                                            <c:choose>
                                                                <c:when test="${attachment.type == 'IMAGE'}">
                                                                    <i class="fa-regular fa-image"></i>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <i class="fa-regular fa-file-lines"></i>
                                                                </c:otherwise>
                                                            </c:choose>

                                                            <span>
                                                                <c:choose>
                                                                    <c:when test="${not empty attachment.fileName}">
                                                                        <c:out value="${attachment.fileName}" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        View attachment
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </span>
                                                        </a>

                                                        <c:if test="${attachment.type == 'IMAGE'}">
                                                            <a href="${ctx}${attachment.filePath}" target="_blank">
                                                                <img src="${ctx}${attachment.filePath}"
                                                                    alt="${attachment.fileName}"
                                                                    class="contact-attachment-image" />
                                                            </a>
                                                        </c:if>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:when>

                                        <c:otherwise>
                                            No attachments.
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="contactChatModal-${request.id}" class="contact-chat-overlay"
                    onclick="closeContactChatModal('contactChatModal-${request.id}')">

                    <div class="contact-chat-box" onclick="event.stopPropagation()">
                        <div class="contact-chat-header">
                            <div class="contact-chat-title">
                                <h3>
                                    <i class="fa-solid fa-comments"></i>
                                    Support Chat
                                </h3>

                                <p>
                                    Request #
                                    <c:out value="${request.id}" /> —
                                    <c:choose>
                                        <c:when test="${not empty request.issueType}">
                                            <c:out value="${request.issueType}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${request.subject}" />
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>

                            <button type="button" class="contact-chat-close"
                                onclick="closeContactChatModal('contactChatModal-${request.id}')">
                                <i class="fa-solid fa-xmark"></i>
                            </button>
                        </div>

                        <div class="contact-chat-messages">
                            <c:choose>
                                <c:when test="${not empty requestMessages}">
                                    <c:forEach items="${requestMessages}" var="msg">
                                        <c:set var="isMe"
                                            value="${msg.sender != null && pageContext.request.userPrincipal != null && msg.sender.email == pageContext.request.userPrincipal.name}" />

                                        <div class="chat-row ${isMe ? 'is-me' : 'is-other'}">
                                            <div class="chat-meta">
                                                <strong>
                                                    <c:choose>
                                                        <c:when test="${isMe}">
                                                            You
                                                        </c:when>
                                                        <c:otherwise>
                                                            Support Team
                                                        </c:otherwise>
                                                    </c:choose>
                                                </strong>

                                                <span class="chat-time">
                                                    <c:choose>
                                                        <c:when test="${not empty msg.formattedSentAt}">
                                                            <c:out value="${msg.formattedSentAt}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:out value="${msg.sentAt}" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>

                                            <div class="chat-bubble"><span class="chat-bubble-text">
                                                    <c:out value="${msg.message}" />
                                                </span></div>
                                        </div>
                                    </c:forEach>
                                </c:when>

                                <c:otherwise>
                                    <div class="empty-chat">No messages yet.</div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <form action="${ctx}/contact/${request.id}/chat" method="post" class="contact-chat-form">

                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                            <input type="text" name="message" class="contact-chat-input"
                                placeholder="Type your message..." maxlength="1000" required />

                            <button type="submit" class="contact-chat-send" title="Send message">
                                <i class="fa-solid fa-paper-plane"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </c:forEach>

            <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

            <script>
                document.addEventListener("click", function (event) {
                    const detailButton = event.target.closest(".js-open-detail");

                    if (detailButton) {
                        openContactDetailModal(detailButton.dataset.detailId);
                        return;
                    }

                    const chatButton = event.target.closest(".js-open-chat");

                    if (chatButton) {
                        openContactChatModal(chatButton.dataset.chatId);
                    }
                });

                function openContactDetailModal(id) {
                    const modal = document.getElementById(id);

                    if (modal) {
                        modal.style.display = "flex";
                    }
                }

                function closeContactDetailModal(id) {
                    const modal = document.getElementById(id);

                    if (modal) {
                        modal.style.display = "none";
                    }
                }

                function openContactChatModal(id) {
                    const modal = document.getElementById(id);

                    if (!modal) {
                        return;
                    }

                    modal.style.display = "flex";

                    const messagesBox = modal.querySelector(".contact-chat-messages");

                    if (messagesBox) {
                        setTimeout(function () {
                            messagesBox.scrollTop = messagesBox.scrollHeight;
                        }, 50);
                    }
                }

                function closeContactChatModal(id) {
                    const modal = document.getElementById(id);

                    if (modal) {
                        modal.style.display = "none";
                    }
                }

                document.addEventListener("keydown", function (event) {
                    if (event.key === "Escape") {
                        document.querySelectorAll(".contact-modal-overlay, .contact-chat-overlay")
                            .forEach(function (modal) {
                                modal.style.display = "none";
                            });
                    }
                });

                const chatIdFromUrl = new URLSearchParams(window.location.search).get("chatId");

                if (chatIdFromUrl) {
                    openContactChatModal("contactChatModal-" + chatIdFromUrl);
                }
            </script>

        </body>

        </html>