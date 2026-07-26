<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />
    <title>Voucher Management — Booktify Staff</title>
</head>

<body class="admin-shell">

    <jsp:include page="/WEB-INF/view/layout/staff/sidebar.jsp" />

    <main class="admin-main">

        <jsp:include page="/WEB-INF/view/layout/staff/header.jsp" />

        <section class="admin-content">

            <div class="admin-toolbar">
                <div>
                    <p class="admin-kicker">
                        <i class="fa-solid fa-ticket"></i>
                        Voucher Management
                    </p>
                    <h2>Vouchers</h2>
                </div>
            </div>

            <div class="admin-panel" style="padding:14px 22px;">

                <form method="get" action="${ctx}/staff/vouchers" class="admin-search-form"
                    style="flex-wrap:wrap;">

                    <div style="position:relative;flex:1;max-width:380px;">

                        <i class="fa-solid fa-magnifying-glass" style="position:absolute;
                      left:13px;
                      top:50%;
                      transform:translateY(-50%);
                      color:#9CA3AF;
                      font-size:.82rem;
                      pointer-events:none;">
                        </i>

                        <input type="text" name="keyword" value="${keyword}"
                            placeholder="Search voucher name or code..." class="admin-input"
                            style="padding-left:38px;" />

                    </div>

                    <select name="status" class="admin-input" style="max-width:180px;">

                        <option value="">
                            All Status
                        </option>

                        <option value="ACTIVE" <c:if test="${status == 'ACTIVE'}">
                            selected
                            </c:if>>
                            Active
                        </option>

                        <option value="UPCOMING" <c:if test="${status == 'UPCOMING'}">
                            selected
                            </c:if>>
                            Upcoming
                        </option>

                        <option value="EXPIRED" <c:if test="${status == 'EXPIRED'}">
                            selected
                            </c:if>>
                            Expired
                        </option>

                        <option value="INACTIVE" <c:if test="${status == 'INACTIVE'}">
                            selected
                            </c:if>>
                            Inactive
                        </option>

                    </select>

                    <button type="submit" class="admin-button">
                        <i class="fa-solid fa-filter"></i>
                        Filter
                    </button>

                    <a href="${ctx}/staff/vouchers" class="admin-button admin-button--ghost">
                        <i class="fa-solid fa-rotate-right"></i>
                        Reset
                    </a>

                </form>

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
                            <th style="width:80px;text-align:center;">View</th>
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
                                            <span style="color:#16A34A;font-weight:700;">ACTIVE</span>
                                        </c:when>
                                        <c:when test="${voucher.status == 'UPCOMING'}">
                                            <span style="color:#2563EB;font-weight:700;">UPCOMING</span>
                                        </c:when>
                                        <c:when test="${voucher.status == 'EXPIRED'}">
                                            <span style="color:#DC2626;font-weight:700;">EXPIRED</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color:#6B7280;font-weight:700;">INACTIVE</span>
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

                                <td class="admin-table__actions" style="text-align:center;">
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
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty vouchers}">
                            <tr>
                                <td colspan="8" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                    <i class="fa-solid fa-ticket"
                                        style="font-size:2rem;display:block;margin-bottom:10px;opacity:.3;"></i>
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
                    <span class="modal-label">Name</span>
                    <span id="mVoucherName" class="modal-value"></span>
                </div>

                <div class="modal-row">
                    <span class="modal-label">Code</span>
                    <span id="mVoucherCode" class="modal-value"></span>
                </div>

                <div class="modal-row">
                    <span class="modal-label">Discount</span>
                    <span id="mVoucherDiscount" class="modal-value"></span>
                </div>

                <div class="modal-row">
                    <span class="modal-label">Minimum Order</span>
                    <span id="mVoucherMin" class="modal-value"></span>
                </div>

                <div class="modal-row">
                    <span class="modal-label">Quantity</span>
                    <span id="mVoucherQuantity" class="modal-value"></span>
                </div>

                <div class="modal-row">
                    <span class="modal-label">Duration</span>
                    <span id="mVoucherDuration" class="modal-value"></span>
                </div>

                <div class="modal-row">
                    <span class="modal-label">Status</span>
                    <span id="mVoucherStatus" class="modal-value"></span>
                </div>

                <div class="modal-row">
                    <span class="modal-label">Description</span>
                    <span id="mVoucherDescription" class="modal-value"></span>
                </div>

                <div class="modal-row">
                    <span class="modal-label">Last Update</span>
                    <span id="mVoucherUpdated" class="modal-value"></span>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.querySelectorAll('.js-view-voucher').forEach(function (btn) {
            btn.onclick = function () {
                let d = this.dataset;

                document.getElementById("mVoucherName").textContent = d.name || "—";
                document.getElementById("mVoucherCode").textContent = d.code || "—";

                if (d.type === "PERCENT") {
                    document.getElementById("mVoucherDiscount").textContent = d.discount + "%";
                } else if (d.type === "FIXED") {
                    document.getElementById("mVoucherDiscount").textContent = Number(d.discount).toLocaleString('vi-VN') + " ₫";
                } else {
                    document.getElementById("mVoucherDiscount").textContent = "—";
                }

                document.getElementById("mVoucherMin").textContent = Number(d.min).toLocaleString('vi-VN') + " ₫";
                document.getElementById("mVoucherQuantity").textContent = d.quantity || "—";
                document.getElementById("mVoucherDuration").textContent = d.start + " - " + d.end;
                document.getElementById("mVoucherStatus").textContent = d.status || "—";
                document.getElementById("mVoucherDescription").textContent = d.description || "—";
                document.getElementById("mVoucherUpdated").textContent = d.updated || "—";

                document.getElementById("voucherModal").style.display = "flex";
            }
        });

        function closeModal(id) {
            document.getElementById(id).style.display = "none";
        }
    </script>

</body>

</html>
