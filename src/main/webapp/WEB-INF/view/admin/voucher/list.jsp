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
                                <p class="admin-kicker">
                                    <i class="fa-solid fa-ticket"></i>
                                    Voucher Management
                                </p>
                                <h2>Vouchers</h2>
                            </div>

                            <a href="/admin/vouchers/create" class="admin-button">
                                <i class="fa-solid fa-plus"></i>
                                New Voucher
                            </a>
                        </div>

                        <div class="admin-table-wrap">

                            <table class="admin-table">

                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Code</th>
                                        <th>Discount</th>
                                        <th>Quantity</th>
                                        <th>Duration</th>
                                        <th>Status</th>
                                        <th>Description</th>
                                        <th style="width:150px;text-align:center;">
                                            Action
                                        </th>
                                    </tr>
                                </thead>

                                <tbody>

                                    <c:forEach items="${vouchers}" var="voucher" varStatus="vs">

                                        <tr>

                                            <td>
                                                <div style="font-weight:700;color:#111827;">
                                                    <c:out value="${voucher.voucherName}" />
                                                </div>
                                            </td>

                                            <td>
                                                <div style="font-weight:700;color:#111827;">
                                                    <c:out value="${voucher.voucherCode}" />
                                                </div>
                                            </td>

                                            <td>

                                                <c:choose>

                                                    <c:when test="${voucher.discountType == 'PERCENT'}">
                                                        <c:out value="${voucher.discountValue}" />%
                                                    </c:when>


                                                    <c:when test="${voucher.discountType == 'FIXED'}">

                                                        <fmt:formatNumber value="${voucher.discountValue}" type="number"
                                                            groupingUsed="true" /> ₫

                                                    </c:when>

                                                    <c:otherwise>
                                                        -
                                                    </c:otherwise>

                                                </c:choose>

                                            </td>

                                            <td>
                                                <c:out value="${voucher.quantity}" />
                                            </td>

                                            <td style="font-size:.85rem;">
                                                <div>
                                                    <c:out value="${voucher.startDateString}" />
                                                </div>

                                                <div>-</div>

                                                <div>
                                                    <c:out value="${voucher.endDateString}" />
                                                </div>
                                            </td>

                                            <td>
                                                <c:choose>

                                                    <c:when test="${voucher.status == 'ACTIVE'}">
                                                        <span style="color:#16A34A;font-weight:700;">
                                                            ACTIVE
                                                        </span>
                                                    </c:when>

                                                    <c:when test="${voucher.status == 'UPCOMING'}">
                                                        <span style="color:#2563EB;font-weight:700;">
                                                            UPCOMING
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

                                            <td class="admin-table__actions">


                                                <!-- VIEW -->

                                                <button type="button" class="icon-link js-view-voucher"
                                                    data-name="<c:out value='${voucher.voucherName}'/>"
                                                    data-code="<c:out value='${voucher.voucherCode}'/>"
                                                    data-type="<c:out value='${voucher.discountType}'/>"
                                                    data-discount="<c:out value='${voucher.discountValue}'/>"
                                                    data-min="<c:out value='${voucher.minOrderAmount}'/>"
                                                    data-quantity="<c:out value='${voucher.quantity}'/>"
                                                    data-start="<c:out value='${voucher.startDateString}'/>"
                                                    data-end="<c:out value='${voucher.endDateString}'/>"
                                                    data-status="<c:out value='${voucher.status}'/>"
                                                    data-description="<c:out value='${voucher.description}'/>"
                                                    data-updated="<c:out value='${voucher.updatedAtString}'/>">

                                                    <i class="fa-solid fa-eye"></i>

                                                </button>



                                                <!-- EDIT -->

                                                <a href="/admin/vouchers/edit/${voucher.voucherId}"
                                                    class="icon-link icon-link--edit">

                                                    <i class="fa-solid fa-pen"></i>

                                                </a>

                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty vouchers}">

                                        <tr>

                                            <td colspan="8" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
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



                <!-- VOUCHER DETAIL MODAL -->

                <div id="voucherModal" class="modal-overlay" style="display:none;" onclick="closeModal('voucherModal')">

                    <div class="modal-box voucher-ticket-modal" onclick="event.stopPropagation()">
                        <div class="modal-header">
                            <h3>
                                <i class="fa-solid fa-ticket"></i>
                                Voucher Details
                            </h3>

                            <button class="modal-close" onclick="closeModal('voucherModal')">
                                <i class="fa-solid fa-xmark"></i>
                            </button>
                        </div>

                        <div class="modal-body">
                            <div class="modal-row">
                                <span class="modal-label">
                                    Name
                                </span>
                                <span id="mVoucherName" class="modal-value">
                                </span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">
                                    Code
                                </span>
                                <span id="mVoucherCode" class="modal-value">
                                </span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">
                                    Discount
                                </span>
                                <span id="mVoucherDiscount" class="modal-value">
                                </span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">
                                    Minimum Order
                                </span>
                                <span id="mVoucherMin" class="modal-value">
                                </span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">
                                    Quantity
                                </span>
                                <span id="mVoucherQuantity" class="modal-value">
                                </span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">
                                    Duration
                                </span>
                                <span id="mVoucherDuration" class="modal-value">
                                </span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">
                                    Status
                                </span>
                                <span id="mVoucherStatus" class="modal-value">
                                </span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">
                                    Description
                                </span>
                                <span id="mVoucherDescription" class="modal-value">
                                </span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">
                                    Last Update
                                </span>
                                <span id="mVoucherUpdated" class="modal-value">
                                </span>
                            </div>

                        </div>
                    </div>
                </div>

                <script>

                    document.querySelectorAll('.js-view-voucher')
                        .forEach(function (btn) {


                            btn.onclick = function () {


                                let d = this.dataset;


                                document.getElementById("mVoucherName").textContent =
                                    d.name || "—";


                                document.getElementById("mVoucherCode").textContent =
                                    d.code || "—";


                                if (d.type === "PERCENT") {

                                    document.getElementById("mVoucherDiscount").textContent =
                                        d.discount + "%";

                                } else if (d.type === "FIXED") {

                                    document.getElementById("mVoucherDiscount").textContent =
                                        Number(d.discount).toLocaleString('vi-VN') + " ₫";

                                } else {

                                    document.getElementById("mVoucherDiscount").textContent =
                                        "—";

                                }

                                document.getElementById("mVoucherMin").textContent =
                                    Number(d.min).toLocaleString('vi-VN') + " ₫";


                                document.getElementById("mVoucherQuantity").textContent =
                                    d.quantity || "—";


                                document.getElementById("mVoucherDuration").textContent =
                                    d.start + " - " + d.end;


                                document.getElementById("mVoucherStatus").textContent =
                                    d.status || "—";


                                document.getElementById("mVoucherDescription").textContent =
                                    d.description || "—";

                                document.getElementById("mVoucherUpdated").textContent =
                                    d.updated || "—";


                                document.getElementById("voucherModal")
                                    .style.display = "flex";


                            }

                        });



                    function closeModal(id) {

                        document.getElementById(id)
                            .style.display = "none";

                    }

                </script>

            </body>

            </html>