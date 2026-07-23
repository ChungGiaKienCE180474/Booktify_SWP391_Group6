<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
            <link rel="stylesheet" href="/css/admin-dashboard.css?v=4" />
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
                                    <th>Last Update</th>
                                    <th>Status</th>
                                    <th style="width:150px;">Actions</th>
                                </tr>
                            </thead>


                            <tbody>

                                <c:forEach items="${authors}" var="author" varStatus="vs">

                                    <tr>

                                        <td style="color:#9CA3AF;font-weight:600;">
                                            ${vs.index + 1}
                                        </td>

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
                                                    <c:out value="${author.biography.length()>80
                                                    ? author.biography.substring(0,80).concat('...')
                                                    : author.biography}" />
                                                </c:when>

                                                <c:otherwise>
                                                    —
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td style="font-size:.8rem;color:#6B7280;">
                                            <c:out value="${author.updatedAtString}" default="—" />
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${author.status}">
                                                    <span
                                                        style="background:#DCFCE7;color:#166534;padding:4px 10px;border-radius:999px;font-size:.75rem;font-weight:700;">
                                                        Active
                                                    </span>
                                                </c:when>

                                                <c:otherwise>
                                                    <span
                                                        style="background:#FEE2E2;color:#991B1B;padding:4px 10px;border-radius:999px;font-size:.75rem;font-weight:700;">
                                                        Deleted
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

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


                                            <c:choose>

                                                <c:when test="${author.status}">

                                                    <button type="button" class="icon-link icon-link--danger"
                                                        onclick="openConfirmModal('/admin/authors/${author.authorId}/delete','delete','Are you sure you want to delete this author? The author will be hidden, not permanently deleted.')">

                                                        <i class="fa-solid fa-trash"></i>

                                                    </button>

                                                </c:when>

                                                <c:otherwise>

                                                    <button type="button" class="icon-link" style="color:#16A34A;"
                                                        onclick="openConfirmModal('/admin/authors/${author.authorId}/restore','restore','Are you sure you want to restore this author?')">

                                                        <i class="fa-solid fa-rotate-left"></i>

                                                    </button>

                                                </c:otherwise>

                                            </c:choose>

                                        </td>

                                    </tr>

                                </c:forEach>


                                <c:if test="${empty authors}">

                                    <tr>

                                        <td colspan="7" style="text-align:center;padding:56px 20px;color:#9CA3AF;">

                                            <i class="fa-solid fa-feather"
                                                style="font-size:2rem;display:block;margin-bottom:10px;opacity:.3;">
                                            </i>

                                            No authors found.

                                        </td>

                                    </tr>

                                </c:if>

                            </tbody>

                        </table>

                    </div>

                </section>

            </main>

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
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <button type="submit" id="confirmSubmitBtn" class="admin-button admin-button--danger">
                                <i class="fa-solid fa-trash"></i> Delete
                            </button>
                        </form>
                    </div>
                </div>
            </div>



            <!-- AUTHOR DETAIL MODAL -->

            <div id="authorModal" class="modal-overlay" style="display:none;" onclick="closeModal('authorModal')">

                <div class="modal-box" onclick="event.stopPropagation()">

                    <div class="modal-header">

                        <h3>
                            <i class="fa-solid fa-feather"></i>
                            Author Details
                        </h3>

                        <button class="modal-close" onclick="closeModal('authorModal')">

                            <i class="fa-solid fa-xmark"></i>

                        </button>

                    </div>


                    <div class="modal-body">

                        <p>
                            <img id="mAuthorImage"
                                style="width:120px;height:120px;border-radius:50%;object-fit:cover;border:2px solid #E5E7EB;">
                        </p>


                        <div class="modal-row">
                            <span class="modal-label">Name</span>
                            <span id="mAuthorName" class="modal-value"></span>
                        </div>


                        <div class="modal-row">
                            <span class="modal-label">Nationality</span>
                            <span id="mAuthorNationality" class="modal-value"></span>
                        </div>


                        <div class="modal-row">
                            <span class="modal-label">Biography</span>
                            <span id="mAuthorBio" class="modal-value"></span>
                        </div>


                        <div class="modal-row">
                            <span class="modal-label">Created</span>
                            <span id="mAuthorCreated" class="modal-value"></span>
                        </div>

                    </div>

                </div>

            </div>

            <jsp:include page="/WEB-INF/view/layout/admin/toast.jsp" />

            <script>
                function openConfirmModal(action, type, msg) {
                    document.getElementById('confirmModalMsg').textContent = msg;
                    document.getElementById('confirmForm').action = action;
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

                function closeModal(id) {
                    document.getElementById(id).style.display = "none";
                }



                document.querySelectorAll('.js-view-author')
                    .forEach(function (btn) {

                        btn.onclick = function () {

                            let d = this.dataset;

                            document.getElementById('mAuthorName').textContent =
                                d.name || '—';

                            document.getElementById('mAuthorNationality').textContent =
                                d.nationality || '—';

                            document.getElementById('mAuthorBio').textContent =
                                d.bio || '—';

                            document.getElementById('mAuthorCreated').textContent =
                                d.created || '—';

                            document.getElementById('mAuthorImage').src =
                                d.image ? d.image + '?v=' + Date.now() : '';

                            document.getElementById('authorModal').style.display = 'flex';

                        }

                    });

            </script>


        </body>

        </html>