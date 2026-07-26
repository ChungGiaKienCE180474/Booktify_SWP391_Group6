<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>Contacts — Booktify Admin</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css" />

    <style>
        .status-pill--pending {
            background: var(--warn-lt);
            color: var(--warn);
            border: 1px solid #FDE68A;
        }

        .contact-pagination-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 16px;
            flex-wrap: wrap;
        }

        .contact-pagination {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;
        }

        .contact-pagination a {
            min-width: 34px;
            height: 34px;
            padding: 0 10px;
            border-radius: var(--radius-sm);
            border: 1px solid var(--border);
            background: var(--white);
            color: var(--text-muted);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            font-size: .84rem;
            font-weight: 700;
            transition: var(--ease);
        }

        .contact-pagination a:hover {
            background: var(--primary-lt);
            border-color: #BFDBFE;
            color: var(--primary);
        }

        .contact-pagination a.active {
            background: var(--primary);
            border-color: var(--primary);
            color: #fff;
            box-shadow: 0 2px 8px rgba(37,99,235,.25);
        }

        .contact-showing-text {
            font-size: .85rem;
            color: var(--text-muted);
            font-weight: 600;
        }

        @media (max-width: 768px) {
            .contact-pagination-row {
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

            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker">
                        <i class="fa-solid fa-headset"></i>
                        Contact Management
                    </p>
                    <h2>Contacts</h2>
                </div>
            </div>

            <section class="admin-cards">
                <div class="admin-card">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-inbox"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Total Contacts</span>
                        <strong>${totalContacts}</strong>
                    </div>
                </div>

                <div class="admin-card">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-circle-exclamation"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Open</span>
                        <strong>${openContacts}</strong>
                    </div>
                </div>

                <div class="admin-card">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-spinner"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>In Progress</span>
                        <strong>${inProgressContacts}</strong>
                    </div>
                </div>

                <div class="admin-card">
                    <div class="admin-card__icon">
                        <i class="fa-solid fa-circle-check"></i>
                    </div>
                    <div class="admin-card__body">
                        <span>Complete</span>
                        <strong>${completeContacts}</strong>
                    </div>
                </div>
            </section>

            <div class="admin-panel" style="padding:14px 22px;">
                <form action="/admin/contacts" method="get" class="admin-search-form">
                    <div style="position:relative;flex:1;max-width:620px;">
                        <i class="fa-solid fa-magnifying-glass"
                           style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>

                        <input type="text"
                               name="keyword"
                               value="<c:out value='${keyword}'/>"
                               class="admin-input"
                               style="padding-left:38px;"
                               placeholder="Search subject, content or customer email..." />
                    </div>

                    <select name="status" class="admin-input" style="max-width:220px;">
                        <option value="">All Status</option>

                        <c:forEach var="st" items="${statuses}">
                            <option value="${st}" ${st eq selectedStatus ? 'selected' : ''}>
                                ${st}
                            </option>
                        </c:forEach>
                    </select>

                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-filter"></i>
                        Filter
                    </button>

                    <c:if test="${not empty keyword or not empty selectedStatus}">
                        <a href="/admin/contacts" class="admin-button admin-button--ghost">
                            <i class="fa-solid fa-xmark"></i>
                            Clear
                        </a>
                    </c:if>
                </form>
            </div>

            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th style="width:70px;">ID</th>
                            <th>Subject</th>
                            <th>Customer</th>
                            <th>Created At</th>
                            <th>Status</th>
                            <th style="width:90px;">Action</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="item" items="${requests}">
                            <tr>
                                <td style="color:#9CA3AF;font-weight:600;">
                                    #${item.displayId}
                                </td>

                                <td>
                                    <div style="font-weight:700;color:#111827;">
                                        <c:out value="${item.subject}" />
                                    </div>
                                </td>

                                <td style="color:#374151;font-size:.875rem;">
                                    <c:out value="${item.customer.email}" />
                                </td>

                                <td style="font-size:.8rem;color:#6B7280;">
                                    <c:out value="${item.formattedCreatedAt}" />
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${item.status eq 'OPEN'}">
                                            <span class="status-pill status-pill--off">
                                                <i class="fa-solid fa-circle-exclamation" style="font-size:.6rem;"></i>
                                                OPEN
                                            </span>
                                        </c:when>

                                        <c:when test="${item.status eq 'IN_PROGRESS'}">
                                            <span class="status-pill status-pill--pending">
                                                <i class="fa-solid fa-spinner" style="font-size:.6rem;"></i>
                                                IN PROGRESS
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <span class="status-pill status-pill--on">
                                                <i class="fa-solid fa-circle-check" style="font-size:.6rem;"></i>
                                                <c:out value="${item.status}" />
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="admin-table__actions">
                                    <a href="/admin/contacts/${item.id}" class="icon-link" title="View">
                                        <i class="fa-solid fa-eye"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty requests}">
                            <tr>
                                <td colspan="6" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                    <i class="fa-solid fa-headset"
                                       style="font-size:2.2rem;display:block;margin-bottom:10px;opacity:.3;"></i>
                                    No contact requests found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPages > 0}">
                <div class="contact-pagination-row">
                    <div class="contact-pagination">
                        <c:if test="${currentPage > 0}">
                            <c:url var="prevUrl" value="/admin/contacts">
                                <c:param name="page" value="${currentPage - 1}" />

                                <c:if test="${not empty keyword}">
                                    <c:param name="keyword" value="${keyword}" />
                                </c:if>

                                <c:if test="${not empty selectedStatus}">
                                    <c:param name="status" value="${selectedStatus}" />
                                </c:if>
                            </c:url>

                            <a href="${prevUrl}">‹</a>
                        </c:if>

                        <c:forEach begin="0" end="${totalPages - 1}" var="i">
                            <c:url var="pageUrl" value="/admin/contacts">
                                <c:param name="page" value="${i}" />

                                <c:if test="${not empty keyword}">
                                    <c:param name="keyword" value="${keyword}" />
                                </c:if>

                                <c:if test="${not empty selectedStatus}">
                                    <c:param name="status" value="${selectedStatus}" />
                                </c:if>
                            </c:url>

                            <a href="${pageUrl}" class="${i eq currentPage ? 'active' : ''}">
                                ${i + 1}
                            </a>
                        </c:forEach>

                        <c:if test="${currentPage + 1 < totalPages}">
                            <c:url var="nextUrl" value="/admin/contacts">
                                <c:param name="page" value="${currentPage + 1}" />

                                <c:if test="${not empty keyword}">
                                    <c:param name="keyword" value="${keyword}" />
                                </c:if>

                                <c:if test="${not empty selectedStatus}">
                                    <c:param name="status" value="${selectedStatus}" />
                                </c:if>
                            </c:url>

                            <a href="${nextUrl}">›</a>
                        </c:if>
                    </div>

                    <c:if test="${totalItems > 0}">
                        <div class="contact-showing-text">
                            Showing ${startItem} to ${endItem} of ${totalItems} contacts
                        </div>
                    </c:if>
                </div>
            </c:if>

        </section>
    </main>
</body>
</html>