<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />

            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css" />

            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

            <title>Suppliers — Booktify Admin</title>

            <style>
                .supplier-filter-card {
                    margin-bottom: 24px;
                    padding: 20px 24px;
                    background: #ffffff;
                    border: 1px solid #e2e8f0;
                    border-radius: 16px;
                }

                .supplier-filter-form {
                    display: grid;
                    grid-template-columns: minmax(280px, 1fr) 200px auto auto;
                    gap: 12px;
                    align-items: center;
                }

                .supplier-search-box {
                    position: relative;
                }

                .supplier-search-box i {
                    position: absolute;
                    top: 50%;
                    left: 16px;
                    color: #94a3b8;
                    transform: translateY(-50%);
                }

                .supplier-filter-control {
                    width: 100%;
                    height: 48px;
                    padding: 0 14px;
                    box-sizing: border-box;
                    border: 1px solid #cbd5e1;
                    border-radius: 10px;
                    background: #ffffff;
                    color: #1e293b;
                    font: inherit;
                }

                .supplier-search-box .supplier-filter-control {
                    padding-left: 44px;
                }

                .supplier-filter-control:focus {
                    outline: none;
                    border-color: #2563eb;
                    box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
                }

                .supplier-filter-button,
                .supplier-reset-button {
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    gap: 8px;
                    height: 48px;
                    padding: 0 20px;
                    border-radius: 10px;
                    font: inherit;
                    font-weight: 700;
                    white-space: nowrap;
                }

                .supplier-filter-button {
                    border: 0;
                    background: #2563eb;
                    color: #ffffff;
                    cursor: pointer;
                }

                .supplier-reset-button {
                    border: 1px solid #cbd5e1;
                    background: #ffffff;
                    color: #334155;
                    text-decoration: none;
                }

                .supplier-table td {
                    vertical-align: middle;
                }

                .supplier-name {
                    color: #111827;
                    font-weight: 750;
                }

                .supplier-status {
                    display: inline-flex;
                    align-items: center;
                    gap: 7px;
                    padding: 7px 14px;
                    border-radius: 999px;
                    font-size: 13px;
                    font-weight: 750;
                    white-space: nowrap;
                }

                .supplier-status--active {
                    color: #15803d;
                    background: #dcfce7;
                }

                .supplier-status--hidden {
                    color: #b91c1c;
                    background: #fee2e2;
                }

                .supplier-updated {
                    color: #475569;
                    font-size: 14px;
                    white-space: nowrap;
                }

                .supplier-actions {
                    display: flex;
                    align-items: center;
                    gap: 7px;
                    white-space: nowrap;
                }

                .supplier-action-button {
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    width: 38px;
                    height: 38px;
                    flex: 0 0 38px;
                    padding: 0;
                    box-sizing: border-box;
                    border: 1px solid #e2e8f0;
                    border-radius: 7px;
                    background: #ffffff;
                    color: #64748b;
                    text-decoration: none;
                    cursor: pointer;
                    font: inherit;
                }

                .supplier-action-button:hover {
                    border-color: #bfdbfe;
                    background: #eff6ff;
                    color: #2563eb;
                }

                .supplier-action-button--edit:hover {
                    border-color: #fde68a;
                    background: #fffbeb;
                    color: #d97706;
                }

                .supplier-action-button--danger:hover {
                    border-color: #fecaca;
                    background: #fef2f2;
                    color: #dc2626;
                }

                .supplier-action-button--restore:hover {
                    border-color: #bbf7d0;
                    background: #f0fdf4;
                    color: #15803d;
                }

                .supplier-action-form {
                    display: inline-flex;
                    margin: 0;
                }

                .supplier-empty {
                    padding: 42px 20px !important;
                    text-align: center;
                    color: #64748b;
                }

                @media (max-width: 1000px) {
                    .supplier-filter-form {
                        grid-template-columns: 1fr 1fr;
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
                                <i class="fa-solid fa-truck"></i>
                                SUPPLIER MANAGEMENT
                            </p>

                            <h2>Suppliers</h2>
                        </div>

                        <a href="${pageContext.request.contextPath}/admin/suppliers/create" class="admin-button">
                            <i class="fa-solid fa-plus"></i>
                            New Supplier
                        </a>
                    </div>

                    <div class="admin-panel" style="padding:14px 22px; margin-bottom:18px;">
                        <form action="${pageContext.request.contextPath}/admin/suppliers" method="get"
                            class="admin-search-form">

                            <div style="position:relative; flex:1; max-width:380px;">
                                <i class="fa-solid fa-magnifying-glass"
                                    style="position:absolute; left:13px; top:50%; transform:translateY(-50%); color:#9CA3AF; font-size:.82rem; pointer-events:none;"></i>

                                <input type="text" name="q" value="${q}" class="admin-input" style="padding-left:38px;"
                                    placeholder="Search supplier, contact, phone or address..." />
                            </div>

                            <select name="status" class="admin-input" style="max-width:160px;">
                                <option value="all" ${status=='all' ? 'selected' : '' }>
                                    All Status
                                </option>

                                <option value="active" ${status=='active' ? 'selected' : '' }>
                                    Active
                                </option>

                                <option value="hidden" ${status=='hidden' ? 'selected' : '' }>
                                    Hidden
                                </option>
                            </select>

                            <button type="submit" class="admin-button">
                                <i class="fa-solid fa-filter"></i>
                                Filter
                            </button>

                            <a href="${pageContext.request.contextPath}/admin/suppliers"
                                class="admin-button admin-button--ghost">
                                <i class="fa-solid fa-rotate-right"></i>
                                Reset
                            </a>

                        </form>
                    </div>

                    <div class="admin-table-wrap">

                        <table class="admin-table supplier-table">

                            <thead>
                                <tr>
                                    <th style="width:55px;">#</th>
                                    <th>Supplier</th>
                                    <th>Contact Person</th>
                                    <th>Phone</th>
                                    <th style="width:130px;">Status</th>
                                    <th style="width:155px;">Create</th>
                                    <th style="width:180px;">Actions</th>
                                </tr>
                            </thead>

                            <tbody>

                                <c:choose>
                                    <c:when test="${empty suppliers}">
                                        <tr>
                                            <td colspan="7" class="supplier-empty">
                                                No suppliers found.
                                            </td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="supplier" items="${suppliers}" varStatus="loop">

                                            <tr>
                                                <td>${loop.index + 1}</td>

                                                <td>
                                                    <span class="supplier-name">
                                                        <c:out value="${supplier.supplierName}" />
                                                    </span>
                                                </td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty supplier.contactPerson}">
                                                            <c:out value="${supplier.contactPerson}" />
                                                        </c:when>

                                                        <c:otherwise>
                                                            —
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty supplier.phone}">
                                                            <c:out value="${supplier.phone}" />
                                                        </c:when>

                                                        <c:otherwise>
                                                            —
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${supplier.active}">
                                                            <span class="supplier-status supplier-status--active">
                                                                <i class="fa-solid fa-circle-check"></i>
                                                                Active
                                                            </span>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <span class="supplier-status supplier-status--hidden">
                                                                <i class="fa-solid fa-circle-minus"></i>
                                                                Hidden
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>
                                                    <span class="supplier-updated">
                                                        ${supplier.updatedAtFormatted}
                                                    </span>
                                                </td>

                                                <td>
                                                    <div class="supplier-actions">

                                                        <button type="button"
                                                            class="supplier-action-button js-view-supplier"
                                                            title="View supplier"
                                                            data-name="<c:out value='${supplier.supplierName}'/>"
                                                            data-contact="<c:out value='${supplier.contactPerson}'/>"
                                                            data-email="<c:out value='${supplier.email}'/>"
                                                            data-phone="<c:out value='${supplier.phone}'/>"
                                                            data-address="<c:out value='${supplier.address}'/>"
                                                            data-desc="<c:out value='${supplier.description}'/>"
                                                            data-active="${supplier.active}"
                                                            data-created="<c:out value='${supplier.createdAtFormatted}'/>"
                                                            data-updated="<c:out value='${supplier.updatedAtFormatted}'/>">
                                                            <i class="fa-solid fa-eye"></i>
                                                        </button>

                                                        <c:if test="${supplier.active}">
                                                            <a href="${pageContext.request.contextPath}/admin/suppliers/${supplier.id}/edit"
                                                                class="supplier-action-button supplier-action-button--edit"
                                                                title="Edit supplier">
                                                                <i class="fa-solid fa-pen"></i>
                                                            </a>
                                                        </c:if>

                                                        <c:choose>
                                                            <c:when test="${supplier.active}">
                                                                <button type="button"
                                                                    class="supplier-action-button supplier-action-button--danger"
                                                                    title="Hide supplier"
                                                                    onclick="openConfirmModal('${pageContext.request.contextPath}/admin/suppliers/${supplier.id}/delete','delete','Are you sure you want to delete this supplier? It will be hidden, not permanently deleted.','<c:out value="${q}"/>','<c:out value="${status}"/>')">
                                                                    <i class="fa-solid fa-trash"></i>
                                                                </button>
                                                            </c:when>

                                                            <c:otherwise>
                                                                <button type="button"
                                                                    class="supplier-action-button supplier-action-button--restore"
                                                                    title="Restore supplier"
                                                                    onclick="openConfirmModal('${pageContext.request.contextPath}/admin/suppliers/${supplier.id}/restore','restore','Are you sure you want to restore this supplier?','<c:out value="${q}"/>','<c:out value="${status}"/>')">
                                                                    <i class="fa-solid fa-rotate-left"></i>
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>

                                                    </div>
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
            <%-- View Supplier Modal --%>
                <div id="supplierModal" class="modal-overlay" style="display:none;"
                    onclick="closeModal('supplierModal')">
                    <div class="modal-box" style="max-width:620px;" onclick="event.stopPropagation()">
                        <div class="modal-header">
                            <h3>
                                <i class="fa-solid fa-truck" style="color:#2563EB;"></i>
                                Supplier Details
                            </h3>

                            <button type="button" class="modal-close" onclick="closeModal('supplierModal')">
                                <i class="fa-solid fa-xmark"></i>
                            </button>
                        </div>

                        <div class="modal-body">
                            <div class="modal-row">
                                <span class="modal-label">Supplier</span>
                                <span id="mSName" class="modal-value"></span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">Contact</span>
                                <span id="mSContact" class="modal-value"></span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">Email</span>
                                <span id="mSEmail" class="modal-value"></span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">Phone</span>
                                <span id="mSPhone" class="modal-value"></span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">Status</span>
                                <span id="mSStatus" class="modal-value"></span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">Address</span>
                                <span id="mSAddress" class="modal-value"></span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">Description</span>
                                <span id="mSDesc" class="modal-value" style="white-space:pre-line;"></span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">Created</span>
                                <span id="mSCreated" class="modal-value"></span>
                            </div>

                            <div class="modal-row">
                                <span class="modal-label">Updated</span>
                                <span id="mSUpdated" class="modal-value"></span>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Unified Confirm Modal (Delete / Restore) --%>
                <div id="confirmModal" class="modal-overlay" style="display:none;" onclick="closeConfirmModal()">
                    <div class="modal-box" style="max-width:420px;" onclick="event.stopPropagation()">
                        <div class="modal-header">
                            <h3 id="confirmModalTitle">
                                <i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i>
                                Confirm Delete
                            </h3>
                        </div>
                        <div class="modal-body" style="display:block;">
                            <p id="confirmModalMsg" style="margin:0;font-size:.9rem;color:#374151;line-height:1.65;"></p>
                        </div>
                        <div class="modal-footer">
                            <button onclick="closeConfirmModal()" class="admin-button admin-button--ghost">
                                <i class="fa-solid fa-xmark"></i> Cancel
                            </button>
                            <form id="confirmForm" method="post" style="display:inline;">
                                <c:if test="${not empty _csrf}">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                </c:if>
                                <input type="hidden" name="q" id="confirmQ" value="" />
                                <input type="hidden" name="status" id="confirmStatus" value="" />
                                <button type="submit" id="confirmSubmitBtn" class="admin-button admin-button--danger">
                                    <i class="fa-solid fa-trash"></i> Delete
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

                <jsp:include page="/WEB-INF/view/layout/admin/toast.jsp" />

                <script>
                    function openConfirmModal(action, type, msg, q, status) {
                        document.getElementById('confirmModalMsg').textContent = msg;
                        document.getElementById('confirmForm').action = action;
                        document.getElementById('confirmQ').value = q || '';
                        document.getElementById('confirmStatus').value = status || '';
                        var title = document.getElementById('confirmModalTitle');
                        var btn = document.getElementById('confirmSubmitBtn');
                        if (type === 'restore') {
                            title.innerHTML = '<i class="fa-solid fa-rotate-left" style="color:#059669;"></i> Confirm Restore';
                            btn.className = 'admin-button';
                            btn.innerHTML = '<i class="fa-solid fa-rotate-left"></i> Restore';
                        } else {
                            title.innerHTML = '<i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i> Confirm Delete';
                            btn.className = 'admin-button admin-button--danger';
                            btn.innerHTML = '<i class="fa-solid fa-trash"></i> Delete';
                        }
                        document.getElementById('confirmModal').style.display = 'flex';
                    }

                    function closeConfirmModal() {
                        document.getElementById('confirmModal').style.display = 'none';
                    }

                    document.addEventListener('click', function (e) {
                        var btn = e.target.closest('.js-view-supplier');
                        if (!btn) return;

                        var d = btn.dataset;

                        document.getElementById('mSName').textContent = d.name || '—';
                        document.getElementById('mSContact').textContent = d.contact || '—';
                        document.getElementById('mSEmail').textContent = d.email || '—';
                        document.getElementById('mSPhone').textContent = d.phone || '—';
                        document.getElementById('mSAddress').textContent = d.address || '—';
                        document.getElementById('mSDesc').textContent = d.desc || 'No description.';
                        document.getElementById('mSCreated').textContent = d.created || '—';
                        document.getElementById('mSUpdated').textContent = d.updated || '—';

                        document.getElementById('mSStatus').innerHTML = d.active === 'true'
                            ? '<span class="status-pill status-pill--on"><i class="fa-solid fa-circle-check" style="font-size:.6rem;"></i> Active</span>'
                            : '<span class="status-pill status-pill--off"><i class="fa-solid fa-circle-xmark" style="font-size:.6rem;"></i> Hidden</span>';

                        document.getElementById('supplierModal').style.display = 'flex';
                    });

                    function closeModal(id) {
                        document.getElementById(id).style.display = 'none';
                    }

                    document.addEventListener('keydown', function (e) {
                        if (e.key === 'Escape') {
                            document.querySelectorAll('.modal-overlay').forEach(function (m) {
                                m.style.display = 'none';
                            });
                        }
                    });
                </script>

        </body>

        </html>