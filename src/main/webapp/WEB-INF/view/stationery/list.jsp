<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/stationery.css" />
    <title>Stationery Management - Booktify</title>
</head>
<body>

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content stationery-page">
        <div class="page-container">

            <div class="page-header">
                <div class="page-header-left">
                    <h1><i class="fa-solid fa-box-open"></i> Stationery Management</h1>
                    <p class="page-subtitle">Manage all stationery items in the system</p>
                </div>
                <div class="page-header-right" style="display:flex;gap:10px;">
                    <a href="/stationery/categories" class="btn-back">
                        <i class="fa-solid fa-tags"></i> Categories
                    </a>
                    <a href="/stationery/create" class="btn-primary-action">
                        <i class="fa-solid fa-plus"></i> Add New Item
                    </a>
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">
                    <i class="fa-solid fa-circle-check"></i> ${successMessage}
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <i class="fa-solid fa-circle-exclamation"></i> ${errorMessage}
                </div>
            </c:if>

            <%-- Search & Filter --%>
            <div class="search-panel">
                <form method="get" action="/stationery" class="search-form">
                    <div class="search-field">
                        <i class="fa-solid fa-magnifying-glass search-icon"></i>
                        <input type="text" name="keyword" class="search-input"
                               placeholder="Search by item name..." value="${keyword}" />
                    </div>
                    <div class="filter-field">
                        <select name="category" class="filter-select">
                            <option value="">All Categories</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.name}" ${cat.name == category ? 'selected' : ''}>${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="btn-search">Search</button>
                    <c:if test="${not empty keyword or not empty category}">
                        <a href="/stationery" class="btn-clear">Clear</a>
                    </c:if>
                </form>
            </div>

            <%-- Table --%>
            <div class="table-card">
                <div class="table-card-header">
                    <span class="result-count">
                        <i class="fa-solid fa-list"></i> ${items.size()} item(s) found
                    </span>
                </div>

                <c:choose>
                    <c:when test="${empty items}">
                        <div class="empty-state">
                            <i class="fa-solid fa-box-open empty-icon"></i>
                            <p>No stationery items found.</p>
                            <a href="/stationery/create" class="btn-primary-action">Add First Item</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrapper">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Image</th>
                                        <th>Name</th>
                                        <th>Category</th>
                                        <th>Price</th>
                                        <th>Stock</th>
                                        <th>Supplier</th>
                                        <th class="text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${items}" varStatus="loop">
                                        <tr>
                                            <td class="text-muted">${loop.index + 1}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty item.imagePath}">
                                                        <img src="${item.imagePath}" alt="${item.name}"
                                                             class="item-thumbnail" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="item-thumbnail-placeholder">
                                                            <i class="fa-solid fa-image"></i>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <a href="/stationery/${item.id}" class="item-name-link">
                                                    ${item.name}
                                                </a>
                                            </td>
                                            <td><span class="category-badge">${item.category}</span></td>
                                            <td class="price-cell">
                                                <fmt:formatNumber value="${item.price}" pattern="#,##0" /> ₫
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${item.stockQuantity <= 0}">
                                                        <span class="stock-badge stock-out">${item.stockQuantity}</span>
                                                    </c:when>
                                                    <c:when test="${item.stockQuantity <= 10}">
                                                        <span class="stock-badge stock-low">${item.stockQuantity}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="stock-badge stock-ok">${item.stockQuantity}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-muted">${empty item.supplier ? '—' : item.supplier}</td>
                                            <td class="actions-cell">
                                                <a href="/stationery/${item.id}"
                                                   class="action-btn action-view" title="View Detail">
                                                    <i class="fa-solid fa-eye"></i>
                                                </a>
                                                <a href="/stationery/${item.id}/edit"
                                                   class="action-btn action-edit" title="Edit">
                                                    <i class="fa-solid fa-pen-to-square"></i>
                                                </a>
                                                <button type="button"
                                                        class="action-btn action-delete" title="Delete"
                                                        data-id="${item.id}" data-name="${item.name}"
                                                        onclick="confirmDelete(this)">
                                                    <i class="fa-solid fa-trash"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </main>

    <div class="modal-overlay" id="deleteModal">
        <div class="modal-box">
            <div class="modal-header modal-header-danger">
                <i class="fa-solid fa-triangle-exclamation"></i>
                <h3>Confirm Delete</h3>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete <strong id="deleteItemName"></strong>?</p>
                <p class="modal-warning">The item will be hidden but kept for order history.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" onclick="closeModal()">Cancel</button>
                <form id="deleteForm" method="post" style="display:inline;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <button type="submit" class="btn-danger">
                        <i class="fa-solid fa-trash"></i> Delete
                    </button>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/view/layout/footer.jsp" />

    <script>
        (function(){
            function catHash(s){var h=0;for(var i=0;i<s.length;i++)h=s.charCodeAt(i)+((h<<5)-h)|0;return Math.abs(h)%10;}
            document.querySelectorAll('.category-badge').forEach(function(el){el.classList.add('cat-c-'+catHash(el.textContent.trim()));});
        })();

        function confirmDelete(btn) {
            var id   = btn.dataset.id;
            var name = btn.dataset.name;
            document.getElementById('deleteItemName').textContent = name;
            document.getElementById('deleteForm').action = '/stationery/' + id + '/delete';
            document.getElementById('deleteModal').classList.add('active');
        }
        function closeModal() {
            document.getElementById('deleteModal').classList.remove('active');
        }
        document.getElementById('deleteModal').addEventListener('click', function(e) {
            if (e.target === this) closeModal();
        });
        setTimeout(function() {
            document.querySelectorAll('.alert').forEach(function(el) {
                el.style.opacity = '0';
                setTimeout(function() { el.remove(); }, 400);
            });
        }, 4000);
        document.getElementById('navToggle')?.addEventListener('click', function() {
            document.querySelector('.main-nav')?.classList.toggle('open');
        });
    </script>

</body>
</html>
