<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <c:set var="ctx" value="${pageContext.request.contextPath}" />

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />

            <title>Suppliers — Booktify Staff</title>

            <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

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

                .staff-data-table td {
                    color: #111827;
                    font-size: .94rem;
                }

                .staff-status {
                    display: inline-flex;
                    align-items: center;
                    gap: 7px;
                    padding: 7px 12px;
                    border-radius: 999px;
                    font-size: .78rem;
                    font-weight: 900;
                }

                .staff-status.active {
                    background: #DCFCE7;
                    color: #15803D;
                }

                .staff-status.hidden {
                    background: #FEE2E2;
                    color: #B91C1C;
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

                .staff-detail-modal {
                    position: fixed;
                    inset: 0;
                    z-index: 9999;
                    display: none;
                    align-items: center;
                    justify-content: center;
                    padding: 24px;
                }

                .staff-detail-modal.show {
                    display: flex;
                }

                .staff-detail-backdrop {
                    position: absolute;
                    inset: 0;
                    background: rgba(15, 23, 42, 0.58);
                    backdrop-filter: blur(3px);
                }

                .staff-detail-dialog {
                    position: relative;
                    z-index: 1;
                    width: min(720px, 100%);
                    max-height: 90vh;
                    overflow: hidden;
                    background: #FFFFFF;
                    border-radius: 18px;
                    box-shadow: 0 28px 90px rgba(15, 23, 42, 0.35);
                }

                .staff-detail-header {
                    height: 76px;
                    padding: 0 28px;
                    border-bottom: 1px solid #E5E7EB;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    background: #FBFBFF;
                }

                .staff-detail-header h3 {
                    margin: 0;
                    display: flex;
                    align-items: center;
                    gap: 11px;
                    color: #111827;
                    font-size: 1.18rem;
                    font-weight: 900;
                }

                .staff-detail-header h3 i {
                    color: #2563EB;
                }

                .staff-detail-close {
                    width: 44px;
                    height: 44px;
                    border: 1px solid #E5E7EB;
                    border-radius: 10px;
                    background: #FFFFFF;
                    color: #6B7280;
                    cursor: pointer;
                    font-size: 1.1rem;
                }

                .staff-detail-close:hover {
                    background: #F3F4F6;
                    color: #111827;
                }

                .staff-detail-body {
                    padding: 30px 34px;
                    max-height: calc(90vh - 76px);
                    overflow-y: auto;
                }

                .staff-detail-info {
                    display: flex;
                    flex-direction: column;
                    gap: 0;
                }

                .staff-detail-row {
                    display: grid;
                    grid-template-columns: 145px 1fr;
                    gap: 18px;
                    padding: 8px 0;
                }

                .staff-detail-row span {
                    color: #6B7280;
                    font-size: .82rem;
                    font-weight: 900;
                    text-transform: uppercase;
                    letter-spacing: .11em;
                }

                .staff-detail-row strong {
                    color: #111827;
                    font-size: 1rem;
                    font-weight: 500;
                    line-height: 1.5;
                    white-space: pre-line;
                }

                .staff-modal-status {
                    display: inline-flex;
                    align-items: center;
                    gap: 7px;
                    width: fit-content;
                    padding: 7px 12px;
                    border-radius: 999px;
                    font-size: .82rem;
                    font-weight: 900;
                }

                .staff-modal-status.active {
                    background: #DCFCE7;
                    color: #16A34A;
                }

                .staff-modal-status.hidden {
                    background: #FEE2E2;
                    color: #B91C1C;
                }

                @media (max-width: 760px) {
                    .staff-detail-row {
                        grid-template-columns: 1fr;
                        gap: 4px;
                    }
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
                                <i class="fa-solid fa-truck-field"></i>
                                Supplier Management
                            </p>

                            <h2>Suppliers</h2>
                        </div>
                    </div>

                    <div class="admin-panel" style="padding:14px 22px;margin-bottom:24px;">
                        <form action="${ctx}/staff/suppliers" method="get" class="admin-search-form">

                            <input type="text" name="q" value="<c:out value='${q}' />" class="admin-input"
                                placeholder="Search supplier, email, phone or address..." />

                            <select name="status" class="admin-input" style="max-width:180px;">
                                <option value="all" ${empty status || status=='all' ? 'selected' : '' }>All Status
                                </option>
                                <option value="active" ${status=='active' ? 'selected' : '' }>Active</option>
                                <option value="hidden" ${status=='hidden' ? 'selected' : '' }>Hidden</option>
                            </select>

                            <button type="submit" class="admin-button">
                                <i class="fa-solid fa-filter"></i>
                                Filter
                            </button>

                            <a href="${ctx}/staff/suppliers" class="admin-button admin-button--ghost">
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
                                        <th>Supplier</th>
                                        <th>Contact Person</th>
                                        <th>Email</th>
                                        <th>Phone</th>
                                        <th>Status</th>
                                        
                                        <th>View</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty suppliers}">
                                            <tr>
                                                <td colspan="7" style="text-align:center;padding:42px;color:#6B7280;">
                                                    No suppliers found.
                                                </td>
                                            </tr>
                                        </c:when>

                                        <c:otherwise>
                                            <c:forEach items="${suppliers}" var="supplier" varStatus="loop">
                                                <tr>
                                                    <td>${loop.index + 1}</td>

                                                    <td>
                                                        <strong>
                                                            <c:out value="${supplier.supplierName}" />
                                                        </strong>
                                                    </td>

                                                    <td>
                                                        <c:out value="${supplier.contactPerson}" default="—" />
                                                    </td>

                                                    <td>
                                                        <c:out value="${supplier.email}" default="—" />
                                                    </td>

                                                    <td>
                                                        <c:out value="${supplier.phone}" default="—" />
                                                    </td>

                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${supplier.active}">
                                                                <span class="staff-status active">
                                                                    <i class="fa-solid fa-circle-check"></i>
                                                                    Active
                                                                </span>
                                                            </c:when>

                                                            <c:otherwise>
                                                                <span class="staff-status hidden">
                                                                    <i class="fa-solid fa-eye-slash"></i>
                                                                    Hidden
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>



                                                    <td>
                                                        <button type="button" class="staff-view-btn js-supplier-detail"
                                                            data-name="<c:out value='${supplier.supplierName}' />"
                                                            data-contact="<c:out value='${supplier.contactPerson}' />"
                                                            data-email="<c:out value='${supplier.email}' />"
                                                            data-phone="<c:out value='${supplier.phone}' />"
                                                            data-status="${supplier.active ? 'Active' : 'Hidden'}"
                                                            data-address="<c:out value='${supplier.address}' />"
                                                            data-description="<c:out value='${supplier.description}' />"
                                                            data-created="<c:out value='${supplier.createdAtFormatted}' />"
                                                            data-updated="<c:out value='${supplier.updatedAtFormatted}' />">
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

            <div id="supplierDetailModal" class="staff-detail-modal">
                <div class="staff-detail-backdrop" onclick="closeSupplierDetailModal()"></div>

                <div class="staff-detail-dialog">
                    <div class="staff-detail-header">
                        <h3>
                            <i class="fa-solid fa-truck-field"></i>
                            Supplier Details
                        </h3>

                        <button type="button" class="staff-detail-close" onclick="closeSupplierDetailModal()">
                            <i class="fa-solid fa-xmark"></i>
                        </button>
                    </div>

                    <div class="staff-detail-body">
                        <div class="staff-detail-info">
                            <div class="staff-detail-row">
                                <span>Supplier</span>
                                <strong id="supplierDetailName"></strong>
                            </div>

                            <div class="staff-detail-row">
                                <span>Contact</span>
                                <strong id="supplierDetailContact"></strong>
                            </div>

                            <div class="staff-detail-row">
                                <span>Email</span>
                                <strong id="supplierDetailEmail"></strong>
                            </div>

                            <div class="staff-detail-row">
                                <span>Phone</span>
                                <strong id="supplierDetailPhone"></strong>
                            </div>

                            <div class="staff-detail-row">
                                <span>Status</span>
                                <strong id="supplierDetailStatus"></strong>
                            </div>

                            <div class="staff-detail-row">
                                <span>Address</span>
                                <strong id="supplierDetailAddress"></strong>
                            </div>

                            <div class="staff-detail-row">
                                <span>Description</span>
                                <strong id="supplierDetailDescription"></strong>
                            </div>





                        </div>
                    </div>
                </div>
            </div>

            <script>
                document.querySelectorAll('.js-supplier-detail').forEach(function (button) {
                    button.addEventListener('click', function () {
                        document.getElementById('supplierDetailName').textContent = this.dataset.name || '—';
                        document.getElementById('supplierDetailContact').textContent = this.dataset.contact || '—';
                        document.getElementById('supplierDetailEmail').textContent = this.dataset.email || '—';
                        document.getElementById('supplierDetailPhone').textContent = this.dataset.phone || '—';

                        document.getElementById('supplierDetailStatus').innerHTML =
                            this.dataset.status === 'Active'
                                ? '<span class="staff-modal-status active"><i class="fa-solid fa-circle-check"></i> Active</span>'
                                : '<span class="staff-modal-status hidden"><i class="fa-solid fa-eye-slash"></i> Hidden</span>';

                        document.getElementById('supplierDetailAddress').textContent = this.dataset.address || '—';
                        document.getElementById('supplierDetailDescription').textContent = this.dataset.description || '—';


                        document.getElementById('supplierDetailModal').classList.add('show');
                    });
                });

                function closeSupplierDetailModal() {
                    document.getElementById('supplierDetailModal').classList.remove('show');
                }
            </script>

        </body>

        </html>