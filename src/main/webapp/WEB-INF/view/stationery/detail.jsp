<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
    <link rel="stylesheet" href="/css/header.css" />
    <link rel="stylesheet" href="/css/footer.css" />
    <link rel="stylesheet" href="/css/stationery.css" />
    <title>${item.name} - Booktify</title>
</head>
<body>

    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content stationery-page">
        <div class="page-container">

            <nav class="breadcrumb">
                <a href="/stationery"><i class="fa-solid fa-box-open"></i> Stationery</a>
                <span class="breadcrumb-sep"><i class="fa-solid fa-chevron-right"></i></span>
                <span class="breadcrumb-current">${item.name}</span>
            </nav>

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

            <div class="detail-card">
                <div class="detail-card-header">
                    <div class="detail-title-group">
                        <h2>${item.name}</h2>
                        <span class="category-badge">${item.category}</span>
                    </div>
                    <div class="detail-actions">
                        <a href="/stationery/${item.id}/edit" class="btn-edit-action">
                            <i class="fa-solid fa-pen-to-square"></i> Edit
                        </a>
                        <button type="button" class="btn-delete-action"
                                data-id="${item.id}" data-name="${item.name}"
                                onclick="confirmDelete(this)">
                            <i class="fa-solid fa-trash"></i> Delete
                        </button>
                    </div>
                </div>

                <%-- Product image --%>
                <c:if test="${not empty item.imagePath}">
                    <div class="detail-image-wrap">
                        <img src="${item.imagePath}" alt="${item.name}" class="detail-image" />
                    </div>
                </c:if>

                <div class="detail-body">
                    <div class="detail-grid">

                        <div class="detail-field">
                            <span class="detail-label"><i class="fa-solid fa-hashtag"></i> ID</span>
                            <span class="detail-value text-muted">#${item.id}</span>
                        </div>

                        <div class="detail-field">
                            <span class="detail-label"><i class="fa-solid fa-money-bill"></i> Price</span>
                            <span class="detail-value price-highlight">
                                <fmt:formatNumber value="${item.price}" pattern="#,##0" /> ₫
                            </span>
                        </div>

                        <div class="detail-field">
                            <span class="detail-label"><i class="fa-solid fa-warehouse"></i> Stock</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${item.stockQuantity <= 0}">
                                        <span class="stock-badge stock-out">${item.stockQuantity} — Out of Stock</span>
                                    </c:when>
                                    <c:when test="${item.stockQuantity <= 10}">
                                        <span class="stock-badge stock-low">${item.stockQuantity} — Low Stock</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="stock-badge stock-ok">${item.stockQuantity} — In Stock</span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="detail-field">
                            <span class="detail-label"><i class="fa-solid fa-truck"></i> Supplier</span>
                            <span class="detail-value">${empty item.supplier ? '—' : item.supplier}</span>
                        </div>

                        <div class="detail-field">
                            <span class="detail-label"><i class="fa-solid fa-calendar-plus"></i> Created At</span>
                            <span class="detail-value text-muted">
                                ${fn:replace(fn:substring(item.createdAt.toString(), 0, 19), 'T', ' ')}
                            </span>
                        </div>

                        <div class="detail-field">
                            <span class="detail-label"><i class="fa-solid fa-calendar-check"></i> Last Updated</span>
                            <span class="detail-value text-muted">
                                ${fn:replace(fn:substring(item.updatedAt.toString(), 0, 19), 'T', ' ')}
                            </span>
                        </div>

                        <c:if test="${not empty item.description}">
                            <div class="detail-field detail-field-full">
                                <span class="detail-label"><i class="fa-solid fa-align-left"></i> Description</span>
                                <span class="detail-value"><c:out value="${item.description}" /></span>
                            </div>
                        </c:if>

                    </div>
                </div>

                <div class="detail-card-footer">
                    <a href="/stationery" class="btn-back">
                        <i class="fa-solid fa-arrow-left"></i> Back to List
                    </a>
                </div>
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
        document.getElementById('navToggle')?.addEventListener('click', function() {
            document.querySelector('.main-nav')?.classList.toggle('open');
        });
    </script>

</body>
</html>
