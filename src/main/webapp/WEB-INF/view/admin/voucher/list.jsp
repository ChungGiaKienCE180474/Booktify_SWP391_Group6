<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
                <link rel="stylesheet" href="/css/admin-dashboard.css" />
                <title>Voucher Management — Booktify Admin</title>
            </head>

            <body class="admin-shell">

                <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

                <main class="admin-main">

                    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

                    <section class="admin-content">

                        <div class="admin-toolbar">

                            <div>
                                <p class="admin-kicker"><i class="fa-solid fa-ticket"></i> Voucher Management</p>
                                <h2>Vouchers</h2>
                            </div>

                            <a href="/admin/vouchers/create" class="admin-button">
                                <i class="fa-solid fa-plus"></i> New Voucher
                            </a>

                        </div>


                        <div class="admin-table-wrap">

                            <table class="admin-table">

                                <thead>
                                    <tr>
                                        <th style="width:48px;">#</th>
                                        <th>Code</th>
                                        <th>Discount</th>
                                        <th>Order Condition</th>
                                        <th>Quantity</th>
                                        <th>Duration</th>
                                        <th>Status</th>
                                        <th>Description</th>
                                        <th>Last Update</th>
                                    </tr>
                                </thead>


                                <tbody>

                                    <c:forEach items="${vouchers}" var="voucher" varStatus="vs">

                                        <tr>

                                            <td style="color:#9CA3AF;font-weight:600;">
                                                ${vs.index + 1}
                                            </td>


                                            <td>
                                                <div style="font-weight:700;color:#111827;">
                                                    <c:out value="${voucher.voucherCode}" />
                                                </div>
                                            </td>


                                            <td>
                                                <c:out value="${voucher.discountValue}" />%
                                            </td>


                                            <td>
                                                <div>
                                                    Min:
                                                    <fmt:formatNumber value="${voucher.minOrderAmount}" type="number"
                                                        groupingUsed="true" /> ₫
                                                </div>

                                                <div>
                                                    Max:
                                                    <fmt:formatNumber value="${voucher.maxOrderAmount}" type="number"
                                                        groupingUsed="true" /> ₫
                                                </div>
                                            </td>


                                            <td>
                                                <c:out value="${voucher.quantity}" />
                                            </td>


                                            <td style="font-size:.85rem;">
                                                <div>
                                                    <c:out value="${voucher.startDate}" />
                                                </div>
                                                <div>-</div>
                                                <div>
                                                    <c:out value="${voucher.endDate}" />
                                                </div>
                                            </td>


                                            <td>

                                                <c:choose>

                                                    <c:when test="${voucher.status == 'ACTIVE'}">
                                                        <span style="color:#16A34A;font-weight:700;">
                                                            ACTIVE
                                                        </span>
                                                    </c:when>

                                                    <c:when test="${voucher.status == 'EXPIRED'}">
                                                        <span style="color:#DC2626;font-weight:700;">
                                                            EXPIRED
                                                        </span>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span style="color:#6B7280;font-weight:700;">
                                                            INACTIVE
                                                        </span>
                                                    </c:otherwise>

                                                </c:choose>

                                            </td>


                                            <td style="max-width:250px;">

                                                <c:choose>

                                                    <c:when test="${not empty voucher.description}">
                                                        <c:out value="${voucher.description}" />
                                                    </c:when>

                                                    <c:otherwise>
                                                        —
                                                    </c:otherwise>

                                                </c:choose>

                                            </td>


                                            <td style="font-size:.8rem;color:#6B7280;">
                                                <c:out value="${voucher.updatedAtString}" default="—" />
                                            </td>

                                        </tr>

                                    </c:forEach>


                                    <c:if test="${empty vouchers}">

                                        <tr>

                                            <td colspan="9" style="text-align:center;padding:56px 20px;color:#9CA3AF;">

                                                <i class="fa-solid fa-ticket"
                                                    style="font-size:2rem;display:block;margin-bottom:10px;opacity:.3;">
                                                </i>

                                                No vouchers found.

                                            </td>

                                        </tr>

                                    </c:if>


                                </tbody>

                            </table>

                        </div>

                    </section>

                </main>

            </body>

            </html>