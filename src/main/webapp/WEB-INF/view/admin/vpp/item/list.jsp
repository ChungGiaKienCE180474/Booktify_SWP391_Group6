<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />

    <title>VPP Products — Booktify Admin</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />

    <style>
        .vpp-table-wrap {
            width: 100%;
            overflow-x: auto;
        }

        .vpp-table {
            width: 100%;
            border-collapse: collapse;
            table-layout: auto;
        }

        .vpp-table th,
        .vpp-table td {
            vertical-align: middle;
            padding: 20px 14px;
            border-bottom: 1px solid #E5E7EB;
        }

        .vpp-table th {
            color: #6B7280;
            font-size: 0.82rem;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            white-space: nowrap;
            font-weight: 800;
            text-align: left;
        }

        /* Cột "#" căn giữa cho cả header lẫn dữ liệu. */
        .vpp-table th.vpp-id-cell {
            text-align: center;
        }

        .vpp-table td {
            color: #111827;
            font-size: 0.95rem;
        }

        .vpp-id-cell {
            width: 45px;
            text-align: center;
            color: #9CA3AF !important;
            font-weight: 700;
        }

        .vpp-image-cell {
            width: 95px;
        }

        .vpp-product-image {
            width: 58px;
            height: 58px;
            object-fit: contain;
            border-radius: 10px;
            background: #F9FAFB;
            padding: 5px;
            display: block;
            border: 1px solid #E5E7EB;
        }

        .vpp-empty-image {
            width: 58px;
            height: 58px;
            display: grid;
            place-items: center;
            border-radius: 10px;
            background: #F3F4F6;
            color: #9CA3AF;
            border: 1px solid #E5E7EB;
        }

        .vpp-name-cell {
            min-width: 150px;
            max-width: 230px;
        }

        .vpp-name {
            font-weight: 800;
            color: #111827;
            line-height: 1.45;
            word-break: break-word;
        }

        .vpp-category-cell {
            min-width: 130px;
            max-width: 170px;
            word-break: break-word;
        }

        .vpp-price-cell {
            width: 130px;
            white-space: nowrap;
        }

        .vpp-price {
            font-weight: 800;
            color: #040202;
            white-space: nowrap;
        }

        .vpp-stock-cell {
            width: 90px;
            white-space: nowrap;
        }

        .vpp-status-cell {
            width: 120px;
            white-space: nowrap;
        }

        .vpp-actions-cell {
            width: 170px;
            white-space: nowrap;
        }

        .vpp-action-box {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .vpp-action-box form {
            margin: 0;
            display: inline-flex;
        }

        .vpp-action-box button.icon-link {
            width: 42px;
            height: 42px;
            border: 1px solid #E5E7EB;
            border-radius: 10px;
            background: #fff;
            color: #6B7280;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            padding: 0;
        }

        .vpp-action-box button.icon-link:hover {
            background: #F9FAFB;
            color: #111827;
        }

        .vpp-action-box button.icon-link--danger {
            color: #6B7280;
        }

        .vpp-action-box button.icon-link--danger:hover {
            color: #DC2626;
            border-color: #FCA5A5;
            background: #FEF2F2;
        }

        .vpp-table .icon-link {
            width: 42px;
            height: 42px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .vpp-modal-body {
            display: grid;
            grid-template-columns: 200px 1fr;
            gap: 24px;
            align-items: start;
        }

        .vpp-modal-img {
            width: 200px;
            min-height: 220px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
            overflow: hidden;
            background: #F9FAFB;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .vpp-modal-img img {
            width: 100%;
            height: 220px;
            object-fit: contain;
            display: block;
            padding: 10px;
            box-sizing: border-box;
        }

        .vpp-modal-img .no-img {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 8px;
            color: #9CA3AF;
            font-size: .8rem;
            text-align: center;
            padding: 20px;
            height: 220px;
            box-sizing: border-box;
        }

        .vpp-modal-img .no-img i {
            font-size: 2.5rem;
            opacity: .35;
        }

        .vpp-modal-info {
            min-width: 0;
        }

        @media (max-width: 600px) {
            .vpp-modal-body {
                grid-template-columns: 1fr;
            }

            .vpp-modal-img {
                width: 100%;
            }
        }

        @media (max-width: 1100px) {
            .vpp-table th,
            .vpp-table td {
                padding: 16px 10px;
            }

            .vpp-product-image,
            .vpp-empty-image {
                width: 50px;
                height: 50px;
            }

            .vpp-name-cell {
                max-width: 170px;
            }

            .vpp-action-box {
                gap: 6px;
            }

            .vpp-table .icon-link {
                width: 38px;
                height: 38px;
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
                    <i class="fa-solid fa-pen-ruler"></i>
                    VPP Product Management
                </p>

                <h2>VPP Products</h2>
            </div>

            <div style="display:flex; gap:10px; flex-wrap:wrap;">
                <a href="${ctx}/admin/vpp/create" class="admin-button">
                    <i class="fa-solid fa-plus"></i>
                    New Product
                </a>
            </div>

        </div>

        <c:if test="${not empty successMessage}">
            <div class="admin-alert admin-alert--success">
                <c:out value="${successMessage}" />
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="admin-alert admin-alert--danger">
                <c:out value="${errorMessage}" />
            </div>
        </c:if>

        <div class="admin-panel" style="padding:14px 22px; margin-bottom:18px;">
            <form method="get"
                  action="${ctx}/admin/vpp"
                  class="admin-search-form">

                <div style="position:relative; flex:1; max-width:380px;">
                    <i class="fa-solid fa-magnifying-glass"
                       style="position:absolute; left:13px; top:50%; transform:translateY(-50%); color:#9CA3AF; font-size:.82rem; pointer-events:none;"></i>

                    <input type="text"
                           name="q"
                           value="<c:out value='${q}'/>"
                           placeholder="Search VPP products..."
                           class="admin-input"
                           style="padding-left:38px;" />
                </div>

                <select name="categoryId" class="admin-input" style="max-width:180px;">
                    <option value="">All Categories</option>

                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}" ${selectedCategoryId == category.id ? 'selected="selected"' : ''}>
                            <c:out value="${category.name}" />
                        </option>
                    </c:forEach>
                </select>

                <select name="status" class="admin-input" style="max-width:160px;">
                    <option value="" ${empty status ? 'selected="selected"' : ''}>
                        All Status
                    </option>

                    <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected="selected"' : ''}>
                        Active
                    </option>

                    <option value="INACTIVE" ${status == 'INACTIVE' ? 'selected="selected"' : ''}>
                        Hidden
                    </option>
                </select>

                <button type="submit" class="admin-button">
                    <i class="fa-solid fa-filter"></i>
                    Filter
                </button>

                <a href="${ctx}/admin/vpp"
                   class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-rotate-right"></i>
                    Reset
                </a>

            </form>
        </div>

        <div class="admin-panel">

            <div class="vpp-table-wrap">

                <table class="vpp-table">

                    <thead>
                    <tr>
                        <th class="vpp-id-cell">#</th>
                        <th class="vpp-image-cell">Image</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>

                    <c:choose>

                        <c:when test="${empty items}">
                            <tr>
                                <td colspan="8"
                                    style="text-align:center; padding:56px 20px; color:#9CA3AF;">
                                    <i class="fa-solid fa-pen-ruler"
                                       style="font-size:2.2rem; display:block; margin-bottom:10px; opacity:.3;"></i>
                                    No VPP products found.
                                </td>
                            </tr>
                        </c:when>

                        <c:otherwise>

                            <c:forEach items="${items}" var="item" varStatus="loop">

                                <c:set var="modalImageUrl" value="" />

                                <c:if test="${item.hasImage}">
                                    <c:url var="modalImageUrl" value="/uploads/vpp/${item.id}/image">
                                        <c:param name="v" value="${item.updatedAt}" />
                                    </c:url>
                                </c:if>

                                <tr>
                                    <td class="vpp-id-cell">
                                        ${fromItem + loop.index}
                                    </td>

                                    <td class="vpp-image-cell">
                                        <c:choose>
                                            <c:when test="${item.hasImage}">
                                                <img src="${modalImageUrl}"
                                                     alt="${item.name}"
                                                     class="vpp-product-image"
                                                     onerror="this.style.display='none'; this.nextElementSibling.style.display='grid';" />

                                                <div class="vpp-empty-image" style="display:none;">
                                                    <i class="fa-solid fa-image"></i>
                                                </div>
                                            </c:when>

                                            <c:otherwise>
                                                <div class="vpp-empty-image">
                                                    <i class="fa-solid fa-image"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="vpp-name-cell">
                                        <div class="vpp-name">
                                            <c:out value="${item.name}" />
                                        </div>
                                    </td>

                                    <td class="vpp-category-cell">
                                        <c:out value="${item.categoryName}" />
                                    </td>

                                    <td class="vpp-price-cell">
                                        <span class="vpp-price">
                                            <fmt:formatNumber value="${item.price}"
                                                              type="number"
                                                              groupingUsed="true"
                                                              maxFractionDigits="0" />
                                            đ
                                        </span>
                                    </td>

                                    <td class="vpp-stock-cell">
                                        <c:out value="${item.stockQuantity}" />
                                    </td>

                                    <td class="vpp-status-cell">
                                        <c:choose>
                                            <c:when test="${item.active}">
                                                <span class="status-pill status-pill--on">
                                                    <i class="fa-solid fa-circle-check"
                                                       style="font-size:.6rem;"></i>
                                                    Active
                                                </span>
                                            </c:when>

                                            <c:otherwise>
                                                <span class="status-pill status-pill--off">
                                                    <i class="fa-solid fa-circle-xmark"
                                                       style="font-size:.6rem;"></i>
                                                    Hidden
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="vpp-actions-cell">

                                        <div class="vpp-action-box">

                                            <button type="button"
                                                    class="icon-link js-view-vpp"
                                                    title="View"
                                                    data-name="<c:out value='${item.name}'/>"
                                                    data-category="<c:out value='${item.categoryName}'/>"
                                                    data-price="${item.price}"
                                                    data-stock="${item.stockQuantity}"
                                                    data-active="${item.active}"
                                                    data-supplier="<c:out value='${item.supplier}'/>"
                                                    data-image="${modalImageUrl}"
                                                    data-desc="<c:out value='${item.description}'/>"
                                                    data-created="<c:out value='${item.createdAtFormatted}'/>"
                                                    data-updated="<c:out value='${item.updatedAtFormatted}'/>">
                                                <i class="fa-solid fa-eye"></i>
                                            </button>

                                            <a href="${ctx}/admin/vpp/${item.id}/edit"
                                               class="icon-link icon-link--edit"
                                               title="Edit">
                                                <i class="fa-solid fa-pen"></i>
                                            </a>

                                            <c:choose>

                                                <c:when test="${item.active}">
                                                    <form method="post"
                                                          action="${ctx}/admin/vpp/${item.id}/delete">

                                                        <input type="hidden"
                                                               name="${_csrf.parameterName}"
                                                               value="${_csrf.token}" />

                                                        <button type="submit"
                                                                class="icon-link icon-link--danger"
                                                                title="Delete"
                                                                onclick="return confirm('Delete this product? It will be changed to Hidden status.')">
                                                            <i class="fa-solid fa-trash"></i>
                                                        </button>

                                                    </form>
                                                </c:when>

                                                <c:otherwise>
                                                    <form method="post"
                                                          action="${ctx}/admin/vpp/${item.id}/restore">

                                                        <input type="hidden"
                                                               name="${_csrf.parameterName}"
                                                               value="${_csrf.token}" />

                                                        <button type="submit"
                                                                class="icon-link"
                                                                title="Restore">
                                                            <i class="fa-solid fa-rotate-left"></i>
                                                        </button>

                                                    </form>
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

            <div class="admin-pagination">

                <div class="admin-pagination__info">

                    <c:choose>
                        <c:when test="${totalItems == 0}">
                            No entries found.
                        </c:when>

                        <c:otherwise>
                            Total:
                            <strong>${totalItems}</strong>
                            Products
                        </c:otherwise>
                    </c:choose>

                </div>

            </div>

        </div>

    </section>

</main>

<div id="vppModal"
     class="modal-overlay"
     style="display:none;"
     onclick="closeModal('vppModal')">

    <div class="modal-box"
         style="max-width:720px;"
         onclick="event.stopPropagation()">

        <div class="modal-header">
            <h3>
                <i class="fa-solid fa-pen-ruler" style="color:#2563EB;"></i>
                VPP Product Details
            </h3>

            <button type="button"
                    class="modal-close"
                    onclick="closeModal('vppModal')">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </div>

        <div class="modal-body">
            <div class="vpp-modal-body">

                <div class="vpp-modal-img">
                    <div class="no-img" id="mVNoImg">
                        <i class="fa-regular fa-image"></i>
                        No image available
                    </div>

                    <img id="mVImg"
                         src=""
                         alt="VPP product image"
                         style="display:none;" />
                </div>

                <div class="vpp-modal-info">

                    <div class="modal-row">
                        <span class="modal-label">Name</span>
                        <span id="mVName" class="modal-value"></span>
                    </div>

                    <div class="modal-row">
                        <span class="modal-label">Category</span>
                        <span id="mVCategory" class="modal-value"></span>
                    </div>

                    <div class="modal-row">
                        <span class="modal-label">Price</span>
                        <span id="mVPrice" class="modal-value"></span>
                    </div>

                    <div class="modal-row">
                        <span class="modal-label">Stock</span>
                        <span id="mVStock" class="modal-value"></span>
                    </div>

                    <div class="modal-row">
                        <span class="modal-label">Supplier</span>
                        <span id="mVSupplier" class="modal-value"></span>
                    </div>

                    <div class="modal-row">
                        <span class="modal-label">Status</span>
                        <span id="mVStatus" class="modal-value"></span>
                    </div>

                    <div class="modal-row">
                        <span class="modal-label">Description</span>
                        <span id="mVDesc"
                              class="modal-value"
                              style="white-space:pre-line;"></span>
                    </div>

                    <div class="modal-row">
                        <span class="modal-label">Created</span>
                        <span id="mVCreated" class="modal-value"></span>
                    </div>

                    <div class="modal-row">
                        <span class="modal-label">Updated</span>
                        <span id="mVUpdated" class="modal-value"></span>
                    </div>

                </div>

            </div>
        </div>

    </div>
</div>

<script>
    document.addEventListener('click', function (e) {
        var btn = e.target.closest('.js-view-vpp');

        if (!btn) {
            return;
        }

        var d = btn.dataset;

        document.getElementById('mVName').textContent = d.name || '—';
        document.getElementById('mVCategory').textContent = d.category || '—';
        document.getElementById('mVPrice').textContent = formatVnd(d.price);
        document.getElementById('mVStock').textContent = d.stock || '0';
        document.getElementById('mVSupplier').textContent = d.supplier || '—';
        document.getElementById('mVDesc').textContent = d.desc || 'No description.';
        document.getElementById('mVCreated').textContent = d.created || '—';
        document.getElementById('mVUpdated').textContent = d.updated || '—';

        document.getElementById('mVStatus').innerHTML = d.active === 'true'
            ? '<span class="status-pill status-pill--on"><i class="fa-solid fa-circle-check" style="font-size:.6rem;"></i> Active</span>'
            : '<span class="status-pill status-pill--off"><i class="fa-solid fa-circle-xmark" style="font-size:.6rem;"></i> Hidden</span>';

        var img = document.getElementById('mVImg');
        var noImg = document.getElementById('mVNoImg');

        if (d.image && d.image.trim()) {
            img.src = d.image;
            img.style.display = 'block';
            noImg.style.display = 'none';
        } else {
            img.removeAttribute('src');
            img.style.display = 'none';
            noImg.style.display = 'flex';
        }

        document.getElementById('vppModal').style.display = 'flex';
    });

    function formatVnd(value) {
        var numberValue = Number(value);

        if (Number.isNaN(numberValue)) {
            return '—';
        }

        return new Intl.NumberFormat('vi-VN').format(numberValue) + ' VND';
    }

    function closeModal(id) {
        document.getElementById(id).style.display = 'none';
    }

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-overlay').forEach(function (modal) {
                modal.style.display = 'none';
            });
        }
    });
</script>

</body>

</html>