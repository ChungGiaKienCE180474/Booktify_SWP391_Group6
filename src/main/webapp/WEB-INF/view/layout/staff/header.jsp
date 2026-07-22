<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<header class="admin-topbar">

    <div class="admin-topbar__brand">
        <div class="admin-topbar__badge">
            <i class="fa-solid fa-shield-halved"></i>
        </div>

        <div>
            <h1>Booktify Staff</h1>
            <p>Staff management workspace</p>
        </div>
    </div>

    <div class="admin-topbar__meta">
        <div class="admin-topbar__user">
            <div class="admin-topbar__avatar">
                <i class="fa-solid fa-user"></i>
            </div>

            <div>
                <strong>${not empty sessionScope.fullName ? sessionScope.fullName : 'Booktify Staff'}</strong>
                <span>${not empty sessionScope.role ? sessionScope.role : 'STAFF'}</span>
            </div>
        </div>

        <a href="${pageContext.request.contextPath}/" class="admin-topbar__link">
            <i class="fa-solid fa-arrow-up-right-from-square"></i>
            Customer Views
        </a>
    </div>

</header>