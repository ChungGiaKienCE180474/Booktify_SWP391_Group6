<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/admin-dashboard.css?v=5" />
    <title>Orders — Booktify Admin</title>
</head>
<body class="admin-shell">
    <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />
    <main class="admin-main">
        <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />
        <section class="admin-content">
            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker"><i class="fa-solid fa-receipt"></i> Order Management</p>
                    <h2>Orders</h2>
                    <p>View and manage customer orders</p>
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="admin-alert admin-alert--success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert--error">${errorMessage}</div>
            </c:if>

            <div class="admin-panel" style="padding:14px 22px;">
                <form method="get" action="/admin/orders" class="admin-search-form">
                    <input type="hidden" name="page" value="0" />
                    <div style="position:relative;flex:1;max-width:380px;">
                        <i class="fa-solid fa-magnifying-glass" style="position:absolute;left:13px;top:50%;transform:translateY(-50%);color:#9CA3AF;font-size:.82rem;pointer-events:none;"></i>
                        <input type="text" name="keyword" value="<c:out value='${keyword}'/>"
                               placeholder="Search by order code, customer, recipient..."
                               class="admin-input" style="padding-left:38px;" />
                    </div>
                    <select name="sort" class="admin-input" style="max-width:180px;">
                        <option value="default" ${sort == 'default' ? 'selected' : ''}>Newest</option>
                        <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>Oldest</option>
                        <option value="code_asc" ${sort == 'code_asc' ? 'selected' : ''}>Code A-Z</option>
                        <option value="code_desc" ${sort == 'code_desc' ? 'selected' : ''}>Code Z-A</option>
                        <option value="customer_asc" ${sort == 'customer_asc' ? 'selected' : ''}>Customer A-Z</option>
                        <option value="customer_desc" ${sort == 'customer_desc' ? 'selected' : ''}>Customer Z-A</option>
                        <option value="total_desc" ${sort == 'total_desc' ? 'selected' : ''}>Total High-Low</option>
                        <option value="total_asc" ${sort == 'total_asc' ? 'selected' : ''}>Total Low-High</option>
                    </select>
                    <select name="status" class="admin-input" style="max-width:180px;">
                        <option value="all" ${status == 'all' ? 'selected' : ''}>All Status</option>
                        <c:forEach items="${orderStatuses}" var="orderStatus">
                            <option value="${orderStatus.name()}" ${status == orderStatus.name() ? 'selected' : ''}>
                                ${orderStatus.label}
                            </option>
                        </c:forEach>
                    </select>
                    <button type="submit" class="admin-button"><i class="fa-solid fa-filter"></i> Filter</button>
                    <a href="/admin/orders" class="admin-button admin-button--ghost"><i class="fa-solid fa-rotate-right"></i> Reset</a>
                </form>
            </div>

            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Order Code</th>
                            <th>Customer</th>
                            <th>Recipient</th>
                            <th class="col-money">Total</th>
                            <th>Status</th>
                            <th>Date</th>
                            <th style="width:72px;text-align:center;">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty orders}">
                                <tr>
                                    <td colspan="7" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                        <i class="fa-solid fa-box-open" style="font-size:2.2rem;display:block;margin-bottom:10px;opacity:.3;"></i>
                                        No orders found.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${orders}" var="order">
                                    <tr>
                                        <td><strong>${order.orderCode}</strong></td>
                                        <td>
                                            <div style="font-weight:700;color:#111827;">${order.customerName}</div>
                                            <div style="font-size:.75rem;color:#6B7280;">${order.customerEmail}</div>
                                        </td>
                                        <td>${order.recipientName}</td>
                                        <td class="col-money"><strong>${order.totalAmountFormatted} &#8363;</strong></td>
                                        <td>
                                            <span class="order-status-badge order-status-badge--${order.status}">
                                                ${order.statusLabel}
                                            </span>
                                        </td>
                                        <td>${order.createdAtFormatted}</td>
                                        <td class="admin-table__actions" style="text-align:center;">
                                            <a href="/admin/orders/${order.id}" class="icon-link" title="View details">
                                                <i class="fa-solid fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>

                <div class="admin-pagination">
                    <div class="admin-pagination__info">
                        <c:choose>
                            <c:when test="${totalItems == 0}">No entries found.</c:when>
                            <c:otherwise>
                                Showing <strong>${fromItem}</strong> to <strong>${toItem}</strong> of <strong>${totalItems}</strong> order${totalItems == 1 ? '' : 's'}
                                <c:if test="${not empty keyword or (not empty status and status != 'all')}"> (filtered)</c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <div class="admin-pagination__nav">
                            <a class="pag-btn ${currentPage == 0 ? 'pag-btn--disabled' : ''}"
                               href="/admin/orders?page=${currentPage - 1}&keyword=<c:out value='${keyword}'/>&status=<c:out value='${status}'/>&sort=<c:out value='${sort}'/>">
                                <i class="fa-solid fa-chevron-left" style="font-size:.7rem;"></i>
                            </a>
                            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                <a class="pag-btn ${i == currentPage ? 'pag-btn--active' : ''}"
                                   href="/admin/orders?page=${i}&keyword=<c:out value='${keyword}'/>&status=<c:out value='${status}'/>&sort=<c:out value='${sort}'/>">
                                    ${i + 1}
                                </a>
                            </c:forEach>
                            <a class="pag-btn ${currentPage >= totalPages - 1 ? 'pag-btn--disabled' : ''}"
                               href="/admin/orders?page=${currentPage + 1}&keyword=<c:out value='${keyword}'/>&status=<c:out value='${status}'/>&sort=<c:out value='${sort}'/>">
                                <i class="fa-solid fa-chevron-right" style="font-size:.7rem;"></i>
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </section>
    </main>
</body>
</html>
