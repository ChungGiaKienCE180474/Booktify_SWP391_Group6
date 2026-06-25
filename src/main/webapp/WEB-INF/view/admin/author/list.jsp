<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
            <link rel="stylesheet" href="/css/admin-dashboard.css" />
            <title>Authors — Booktify Admin</title>
        </head>

        <body class="admin-shell">
            <jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp" />

            <main class="admin-main">
                <jsp:include page="/WEB-INF/view/layout/admin/header.jsp" />

                <section class="admin-content">

                    <div class="admin-toolbar">
                        <div>
                            <p class="admin-kicker"><i class="fa-solid fa-feather"></i> Author Management</p>
                            <h2>Authors</h2>
                        </div>
                        <a href="/admin/authors/create" class="admin-button">
                            <i class="fa-solid fa-plus"></i> New Author
                        </a>
                    </div>

                

                    <div class="admin-table-wrap">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th style="width:48px;">#</th>
                                    <th>Author</th>
                                    <th>Nationality</th>
                                    <th>Biography</th>
                                    <th>Photo</th>
                                    <th>Last Update</th>
                                    <th style="width:120px;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>

                                <c:forEach items="${authors}" var="author" varStatus="vs">
                                    <tr>
                                        <td style="color:#9CA3AF;font-weight:600;">${vs.index+1}</td>

                                        <td>
                                            <div style="font-weight:700;color:#111827;">
                                                <c:out value="${author.authorName}" />
                                            </div>
                                        </td>

                                        <td>
                                            <c:out value="${author.nationality}" default="—" />
                                        </td>

                                        <td style="max-width:260px;">
                                            <c:choose>
                                                <c:when test="${not empty author.biography}">
                                                    <c:out
                                                        value="${author.biography.length()>80?author.biography.substring(0,80).concat('...'):author.biography}" />
                                                </c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty author.profileImage}">
                                                    <img src="${author.profileImage}"
                                                        style="width:52px;height:52px;border-radius:50%;object-fit:cover;border:2px solid #E5E7EB;">
                                                </c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td style="font-size:.8rem;color:#6B7280;">
                                            <c:out value="${author.updatedAtString}" default="—" />
                                        </td>

                                        <td class="admin-table__actions">

                                        <td class="admin-table__actions">
                                            <button type="button" class="icon-link js-view-author"
                                                data-name="<c:out value='${author.authorName}'/>"
                                                data-nationality="<c:out value='${author.nationality}'/>"
                                                data-bio="<c:out value='${author.biography}'/>"
                                                data-image="<c:out value='${author.profileImage}'/>"
                                                data-created="<c:out value='${author.createdAtString}'/>">
                                                <i class="fa-solid fa-eye"></i>
                                            </button>

                                            <a href="/admin/authors/${author.authorId}/edit"
                                                class="icon-link icon-link--edit">
                                                <i class="fa-solid fa-pen"></i>
                                            </a>

                                            <button type="button" class="icon-link icon-link--danger"
                                                onclick="openDeleteModal('/admin/authors/${author.authorId}/delete','Delete this author?')">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <c:if test="${empty authors}">
                                    <tr>
                                        <td colspan="7" style="text-align:center;padding:56px 20px;color:#9CA3AF;">
                                            <i class="fa-solid fa-feather"
                                                style="font-size:2rem;display:block;margin-bottom:10px;opacity:.3;"></i>
                                            No authors found.
                                        </td>
                                    </tr>
                                </c:if>

                            </tbody>
                        </table>
                    </div>

                </section>
            </main>

            <div id="deleteModal" class="modal-overlay" style="display:none;">
                <div class="modal-box" onclick="event.stopPropagation()">
                    <div class="modal-header">
                        <h3><i class="fa-solid fa-circle-exclamation" style="color:#EF4444;"></i> Confirm Delete</h3>
                    </div>
                    <div class="modal-body" style="display:block;">
                        <p id="deleteModalMsg"></p>
                        <p style="font-size:.8rem;color:#9CA3AF;">This action cannot be undone.</p>
                    </div>
                    <div class="modal-footer">
                        <button onclick="closeDeleteModal()" class="admin-button admin-button--ghost">Cancel</button>
                        <form id="deleteForm" method="post">
                            <button class="admin-button admin-button--danger">Delete</button>
                        </form>
                    </div>
                </div>
            </div>

            <div id="authorModal" class="modal-overlay" style="display:none;" onclick="closeModal('authorModal')">
                <div class="modal-box" onclick="event.stopPropagation()">
                    <div class="modal-header">
                        <h3><i class="fa-solid fa-feather"></i> Author Details</h3>
                        <button class="modal-close" onclick="closeModal('authorModal')"><i
                                class="fa-solid fa-xmark"></i></button>
                    </div>
                    <div class="modal-body">
                        <p><img id="mAuthorImage"
                                style="width:120px;height:120px;border-radius:50%;object-fit:cover;border:2px solid #E5E7EB;">
                        </p>
                        <div class="modal-row"><span class="modal-label">Name</span><span id="mAuthorName"
                                class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Nationality</span><span id="mAuthorNationality"
                                class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Biography</span><span id="mAuthorBio"
                                class="modal-value"></span></div>
                        <div class="modal-row"><span class="modal-label">Created</span><span id="mAuthorCreated"
                                class="modal-value"></span></div>
                    </div>
                </div>
            </div>

            <div id="toastContainer" class="toast-container"></div>

            <script>
                function openDeleteModal(action, msg) { document.getElementById('deleteForm').action = action; document.getElementById('deleteModalMsg').textContent = msg; document.getElementById('deleteModal').style.display = 'flex'; }
                function closeDeleteModal() { document.getElementById('deleteModal').style.display = 'none'; }
                function closeModal(id) { document.getElementById(id).style.display = 'none'; }
                document.querySelectorAll('.js-view-author').forEach(function (b) {
                    b.onclick = function () {
                        var d = this.dataset;
                        document.getElementById('mAuthorName').textContent = d.name || '—';
                        document.getElementById('mAuthorNationality').textContent = d.nationality || '—';
                        document.getElementById('mAuthorBio').textContent = d.bio || '—';
                        document.getElementById('mAuthorCreated').textContent = d.created || '—';
                        document.getElementById('mAuthorImage').src = d.image || '';
                        document.getElementById('authorModal').style.display = 'flex';
                    };
                });
            </script>

        </body>

        </html>