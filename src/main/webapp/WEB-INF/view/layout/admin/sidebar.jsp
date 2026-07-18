<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

            <aside class="admin-sidebar">
                <%-- Logo --%>
                    <a href="/admin" class="sidebar-logo" style="text-decoration:none;">
                        <div class="sidebar-logo__icon">
                            <i class="fa-solid fa-book-open"></i>
                        </div>
                        <div class="sidebar-logo__text">
                            <span class="sidebar-logo__name">Booktify</span>
                            <span class="sidebar-logo__sub">Admin Panel</span>
                        </div>
                    </a>

                    <%-- Navigation --%>
                        <nav class="admin-sidebar__nav">
                            <%-- Overview Section --%>
                                <div class="admin-sidebar__section">
                                    <span class="admin-sidebar__label">Overview</span>
                                    <a href="/admin"
                                        class="admin-sidebar__item ${pageContext.request.requestURI == '/admin' ? 'active' : ''}">
                                        <i class="fa-solid fa-chart-pie"></i>
                                        Dashboard
                                    </a>
                                </div>
                               

                                <%-- Management Section (Đã sửa lỗi đóng/mở thẻ ở đây) --%>
                                    <div class="admin-sidebar__section">
                                        <span class="admin-sidebar__label">Management</span>

                                        <a href="/admin/categories"
                                            class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/categories') ? 'active' : ''}">
                                            <i class="fa-solid fa-tag"></i>
                                            Categories
                                        </a>

                                        <a href="/admin/genres"
                                            class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/genres') ? 'active' : ''}">
                                            <i class="fa-solid fa-layer-group"></i>
                                            Genres
                                        </a>

                                        <a href="/admin/books"
                                            class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/books') ? 'active' : ''}">
                                            <i class="fa-solid fa-book"></i>
                                            Books
                                        </a>
                                      <a href="/admin/orders"
               class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/orders') ? 'active' : ''}">
                <i class="fa-solid fa-receipt"></i>
                Orders
            </a>

                                        <a href="/admin/authors"
                                            class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/authors') ? 'active' : ''}">
                                            <i class="fa-solid fa-feather"></i>
                                            Authors
                                        </a>

                                        <a href="/admin/vouchers"
                                            class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/vouchers') ? 'active' : ''}">
                                            <i class="fa-solid fa-ticket"></i>
                                            Vouchers
                                        </a>

                                        <a href="/admin/customers"
                                            class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/customers') ? 'active' : ''}">
                                            <i class="fa-solid fa-users"></i>
                                            Customers
                                        </a>

                                        <a href="/admin/staff"
                                            class="admin-sidebar__item ${fn:contains(pageContext.request.requestURI, '/admin/staff') ? 'active' : ''}">
                                            <i class="fa-solid fa-user-tie"></i>
                                            Staff
                                        </a>
                                    </div> <%-- Đóng thẻ chuẩn cho khối Management --%>
                        </nav>

                        <%-- Footer --%>
                            <div class="admin-sidebar__footer">
                                <form method="post" action="/logout" class="admin-sidebar__logout-form">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <button type="submit" class="admin-sidebar__logout">
                                        <i class="fa-solid fa-right-from-bracket"></i>
                                        Logout
                                    </button>
                                </form>
                            </div>
            </aside>
