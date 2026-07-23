<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />

    <title>Stationery VPP — Booktify Staff</title>

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

        .staff-data-table td {
            color: #111827;
            font-size: .94rem;
        }

        .staff-item-img,
        .staff-no-img {
            width: 56px;
            height: 56px;
            border-radius: 10px;
            border: 1px solid #E5E7EB;
            background: #F9FAFB;
        }

        .staff-item-img {
            object-fit: contain;
            padding: 5px;
        }

        .staff-no-img {
            display: grid;
            place-items: center;
            color: #9CA3AF;
        }

        .staff-price {
            color: #DC2626;
            font-weight: 900;
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
            width: min(920px, 100%);
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

        .staff-detail-body--with-image {
            display: grid;
            grid-template-columns: 280px 1fr;
            gap: 34px;
        }

        .staff-detail-image {
            width: 280px;
            height: 280px;
            border: 1px solid #E5E7EB;
            border-radius: 12px;
            background: #FFFFFF;
            display: grid;
            place-items: center;
            overflow: hidden;
            color: #94A3B8;
            font-weight: 800;
        }

        .staff-detail-image img {
            width: 100%;
            height: 100%;
            object-fit: contain;
            padding: 12px;
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
            padding: 7px 0;
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
            .staff-detail-body--with-image {
                grid-template-columns: 1fr;
            }

            .staff-detail-image {
                width: 100%;
            }

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
                    <i class="fa-solid fa-pen-ruler"></i>
                    Stationery VPP
                </p>

                <h2>VPP Products</h2>
            </div>
        </div>

        <div class="admin-panel" style="padding:14px 22px;margin-bottom:24px;">
            <form action="${ctx}/staff/vpp" method="get" class="admin-search-form">

                <input type="text"
                       name="q"
                       value="<c:out value='${q}' />"
                       class="admin-input"
                       placeholder="Search VPP products..." />

                <select name="categoryId" class="admin-input" style="max-width:220px;">
                    <option value="">All Categories</option>

                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}" ${selectedCategoryId == category.id ? 'selected' : ''}>
                            <c:out value="${category.name}" />
                        </option>
                    </c:forEach>
                </select>

                <select name="status" class="admin-input" style="max-width:180px;">
                    <option value="" ${empty status ? 'selected' : ''}>All Status</option>
                    <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                    <option value="INACTIVE" ${status == 'INACTIVE' ? 'selected' : ''}>Hidden</option>
                </select>

                <button type="submit" class="admin-button">
                    <i class="fa-solid fa-filter"></i>
                    Filter
                </button>

                <a href="${ctx}/staff/vpp" class="admin-button admin-button--ghost">
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
                        <th>Image</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Status</th>
                        <th>View</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:choose>
                        <c:when test="${empty items}">
                            <tr>
                                <td colspan="8" style="text-align:center;padding:42px;color:#6B7280;">
                                    No VPP products found.
                                </td>
                            </tr>
                        </c:when>

                        <c:otherwise>
                            <c:forEach items="${items}" var="item" varStatus="loop">
                                <c:set var="itemImageUrl" value="" />

                                <c:if test="${item.hasImage}">
                                    <c:set var="itemImageUrl" value="${ctx}${item.imagePath}" />
                                </c:if>

                                <tr>
                                    <td>${fromItem + loop.index}</td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${item.hasImage}">
                                                <img src="${itemImageUrl}"
                                                     alt="${item.name}"
                                                     class="staff-item-img" />
                                            </c:when>

                                            <c:otherwise>
                                                <div class="staff-no-img">
                                                    <i class="fa-solid fa-image"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <strong><c:out value="${item.name}" /></strong>
                                    </td>

                                    <td>
                                        <c:out value="${item.categoryName}" />
                                    </td>

                                    <td>
                                        <span class="staff-price">
                                            <fmt:formatNumber value="${item.price}" type="number" maxFractionDigits="0" /> ₫
                                        </span>
                                    </td>

                                    <td>
                                        <c:out value="${item.stockQuantity}" />
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${item.active}">
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
                                        <button type="button"
                                                class="staff-view-btn js-vpp-detail"
                                                data-name="<c:out value='${item.name}' />"
                                                data-category="<c:out value='${item.categoryName}' />"
                                                data-price="<c:out value='${item.price}' />"
                                                data-stock="<c:out value='${item.stockQuantity}' />"
                                                data-supplier="<c:out value='${item.supplier}' />"
                                                data-status="${item.active ? 'Active' : 'Hidden'}"
                                                data-description="<c:out value='${item.description}' />"
                                                data-created="<c:out value='${item.createdAtFormatted}' />"
                                                data-updated="<c:out value='${item.updatedAtFormatted}' />"
                                                data-image="${itemImageUrl}">
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

<div id="vppDetailModal" class="staff-detail-modal">
    <div class="staff-detail-backdrop" onclick="closeVppDetailModal()"></div>

    <div class="staff-detail-dialog">
        <div class="staff-detail-header">
            <h3>
                <i class="fa-solid fa-pen-ruler"></i>
                VPP Product Details
            </h3>

            <button type="button" class="staff-detail-close" onclick="closeVppDetailModal()">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </div>

        <div class="staff-detail-body staff-detail-body--with-image">
            <div class="staff-detail-image" id="vppDetailImageBox">
                <span>No image</span>
            </div>

            <div class="staff-detail-info">
                <div class="staff-detail-row">
                    <span>Name</span>
                    <strong id="vppDetailName"></strong>
                </div>

                <div class="staff-detail-row">
                    <span>Category</span>
                    <strong id="vppDetailCategory"></strong>
                </div>

                <div class="staff-detail-row">
                    <span>Price</span>
                    <strong id="vppDetailPrice"></strong>
                </div>

                <div class="staff-detail-row">
                    <span>Stock</span>
                    <strong id="vppDetailStock"></strong>
                </div>

                <div class="staff-detail-row">
                    <span>Supplier</span>
                    <strong id="vppDetailSupplier"></strong>
                </div>

                <div class="staff-detail-row">
                    <span>Status</span>
                    <strong id="vppDetailStatus"></strong>
                </div>

                <div class="staff-detail-row">
                    <span>Description</span>
                    <strong id="vppDetailDescription"></strong>
                </div>

                <div class="staff-detail-row">
                    <span>Created</span>
                    <strong id="vppDetailCreated"></strong>
                </div>

                <div class="staff-detail-row">
                    <span>Updated</span>
                    <strong id="vppDetailUpdated"></strong>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.querySelectorAll('.js-vpp-detail').forEach(function (button) {
        button.addEventListener('click', function () {
            const imageBox = document.getElementById('vppDetailImageBox');
            const image = this.dataset.image;

            imageBox.innerHTML = '';

            if (image) {
                const img = document.createElement('img');
                img.src = image;
                img.alt = this.dataset.name || 'VPP image';
                imageBox.appendChild(img);
            } else {
                imageBox.innerHTML = '<span>No image</span>';
            }

            document.getElementById('vppDetailName').textContent = this.dataset.name || '—';
            document.getElementById('vppDetailCategory').textContent = this.dataset.category || '—';

            const rawPrice = Number(this.dataset.price || 0);
            document.getElementById('vppDetailPrice').textContent =
                rawPrice.toLocaleString('vi-VN') + ' VND';

            document.getElementById('vppDetailStock').textContent = this.dataset.stock || '—';
            document.getElementById('vppDetailSupplier').textContent = this.dataset.supplier || '—';

            document.getElementById('vppDetailStatus').innerHTML =
                this.dataset.status === 'Active'
                    ? '<span class="staff-modal-status active"><i class="fa-solid fa-circle-check"></i> Active</span>'
                    : '<span class="staff-modal-status hidden"><i class="fa-solid fa-eye-slash"></i> Hidden</span>';

            document.getElementById('vppDetailDescription').textContent = this.dataset.description || '—';
            document.getElementById('vppDetailCreated').textContent = this.dataset.created || '—';
            document.getElementById('vppDetailUpdated').textContent = this.dataset.updated || '—';

            document.getElementById('vppDetailModal').classList.add('show');
        });
    });

    function closeVppDetailModal() {
        document.getElementById('vppDetailModal').classList.remove('show');
    }
</script>

</body>
</html>