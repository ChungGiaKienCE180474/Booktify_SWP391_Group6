<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="uri" value="${pageContext.request.requestURI}" />

            <aside class="admin-sidebar">

                <a href="${ctx}/staff" class="sidebar-logo">
                    <span class="sidebar-logo__icon">
                        <i class="fa-solid fa-user-tie"></i>
                    </span>

                    <span class="sidebar-logo__text">
                        <span class="sidebar-logo__name">Booktify</span>
                        <span class="sidebar-logo__sub">Staff Panel</span>
                    </span>
                </a>

                <nav class="admin-sidebar__nav">

                    <div class="admin-sidebar__section">
                        <div class="admin-sidebar__label">Overview</div>

                        <a href="${ctx}/staff" class="admin-sidebar__item ${uri.endsWith('/staff') ? 'active' : ''}">
                            <i class="fa-solid fa-chart-pie"></i>
                            <span>Dashboard</span>
                        </a>
                    </div>

                    <div class="admin-sidebar__section">
                        <div class="admin-sidebar__label">Operations</div>

                        <a href="${ctx}/staff/orders" class="admin-sidebar__item ${fn:contains(uri, '/staff/orders') ? 'active' : ''}">
                            <i class="fa-solid fa-receipt"></i>
                            <span>Orders</span>
                        </a>

                        <a href="${ctx}/staff/reviews" class="admin-sidebar__item ${fn:contains(uri, '/staff/reviews') ? 'active' : ''}">
                            <i class="fa-solid fa-star"></i>
                            <span>Reviews</span>
                        </a>

                        <a href="${ctx}/staff/vouchers" class="admin-sidebar__item ${fn:contains(uri, '/staff/vouchers') ? 'active' : ''}">
                            <i class="fa-solid fa-ticket"></i>
                            <span>Vouchers</span>
                        </a>

                        <a href="${ctx}/staff/suppliers" class="admin-sidebar__item ${fn:contains(uri, '/staff/suppliers') ? 'active' : ''}">
                            <i class="fa-solid fa-truck"></i>
                            <span>Suppliers</span>
                        </a>

                        <a href="${ctx}/staff/vpp" class="admin-sidebar__item ${fn:contains(uri, '/staff/vpp') ? 'active' : ''}">
                            <i class="fa-solid fa-pen-ruler"></i>
                            <span>Stationery</span>
                        </a>

                        <a href="${ctx}/staff/contact" class="admin-sidebar__item ${fn:contains(uri, '/staff/contact') ? 'active' : ''}">
                            <i class="fa-solid fa-headset"></i>
                            <span>Contact Requests</span>
                        </a>
                    </div>

                </nav>

                <div class="admin-sidebar__footer">
                    <a href="${ctx}/" class="admin-sidebar__item">
                        <i class="fa-solid fa-arrow-up-right-from-square"></i>
                        <span>Customer View</span>
                    </a>

                    <form method="post" action="${ctx}/logout" style="margin:0;">
                        <c:if test="${_csrf != null}">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        </c:if>

                        <button type="submit" class="admin-sidebar__logout"
                            style="width:100%;border:0;background:transparent;cursor:pointer;text-align:left;">
                            <i class="fa-solid fa-right-from-bracket"></i>
                            <span>Logout</span>
                        </button>
                    </form>
                </div>

            </aside>