<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title>Category Management - Booktify</title>
</head>
<body>
    <jsp:include page="/WEB-INF/view/layout/header.jsp" />

    <main class="main-content stationery-page">
        <div class="page-container">

            <div class="page-header">
                <div class="page-header-left">
                    <h1><i class="fa-solid fa-tags"></i> Category Management</h1>
                    <p class="page-subtitle">Manage stationery categories</p>
                </div>
                <div class="page-header-right" style="display:flex;gap:10px;">
                    <a href="/stationery" class="btn-back">
                        <i class="fa-solid fa-arrow-left"></i> Back to Items
                    </a>
                    <a href="/stationery/categories/create" class="btn-primary-action">
                        <i class="fa-solid fa-plus"></i> Add Category
                    </a>
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> ${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> ${errorMessage}</div>
            </c:if>

            <div class="table-card">
                <div class="table-card-header">
                    <span class="result-count">
                        <i class="fa-solid fa-list"></i> ${categories.size()} category(ies)
                    </span>
                </div>

                <c:choose>
                    <c:when test="${empty categories}">
                        <div class="empty-state">
                            <i class="fa-solid fa-tags empty-icon"></i>
                            <p>No categories yet.</p>
                            <a href="/stationery/categories/create" class="btn-primary-action">Add First Category</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrapper">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Category Name</th>
                                        <th>Description</th>
                                        <th>Created At</th>
                                        <th class="text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="cat" items="${categories}" varStatus="loop">
                                        <tr>
                                            <td class="text-muted">${loop.index + 1}</td>
                                            <td><span class="category-badge">${cat.name}</span></td>
                                            <td class="text-muted">
                                                ${empty cat.description ? '—' : cat.description}
                                            </td>
                                            <td class="text-muted">
                                                ${fn:replace(fn:substring(cat.createdAt.toString(), 0, 19), 'T', ' ')}
                                            </td>
                                            <td class="actions-cell">
                                                <a href="/stationery/categories/${cat.id}/edit"
                                                   class="action-btn action-edit" title="Edit">
                                                    <i class="fa-solid fa-pen-to-square"></i>
                                                </a>
                                                <button type="button"
                                                        class="action-btn action-delete" title="Delete"
                                                        onclick="confirmDelete(${cat.id}, '${cat.name}')">
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
                <p>Delete category <strong id="deleteCatName"></strong>?</p>
                <p class="modal-warning">Cannot delete if items are using this category.</p>
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

        function confirmDelete(id, name) {
            document.getElementById('deleteCatName').textContent = name;
            document.getElementById('deleteForm').action = '/stationery/categories/' + id + '/delete';
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
    </script>
</body>
</html>
