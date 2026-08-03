<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <link rel="stylesheet" href="/css/admin-dashboard.css">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <title>Supplier Detail</title>

    <style>

        .detail-card{
            background:#fff;
            border-radius:18px;
            padding:35px;
            box-shadow:0 10px 30px rgba(0,0,0,.06);
        }

        .detail-grid{
            display:grid;
            grid-template-columns:1fr 1fr;
            gap:24px;
        }

        .detail-item{
            display:flex;
            flex-direction:column;
        }

        .detail-item label{
            font-size:13px;
            text-transform:uppercase;
            color:#94a3b8;
            margin-bottom:8px;
            font-weight:700;
            letter-spacing:.5px;
        }

        .detail-value{
            background:#f8fafc;
            border:1px solid #e5e7eb;
            border-radius:10px;
            min-height:48px;
            padding:14px 16px;
            color:#1f2937;
            font-weight:500;
        }

        .detail-item-full{
            grid-column:1 / span 2;
        }

        .status-active{
            display:inline-block;
            padding:7px 18px;
            border-radius:30px;
            background:#dcfce7;
            color:#15803d;
            font-weight:700;
        }

        .status-hidden{
            display:inline-block;
            padding:7px 18px;
            border-radius:30px;
            background:#fee2e2;
            color:#dc2626;
            font-weight:700;
        }

        .toolbar-buttons{
            display:flex;
            gap:12px;
        }

        @media(max-width:900px){

            .detail-grid{
                grid-template-columns:1fr;
            }

            .detail-item-full{
                grid-column:auto;
            }

        }

    </style>

</head>

<body class="admin-shell">

<jsp:include page="/WEB-INF/view/layout/admin/sidebar.jsp"/>

<main class="admin-main">

    <jsp:include page="/WEB-INF/view/layout/admin/header.jsp"/>

    <section class="admin-content">

        <div class="admin-toolbar">

            <div>

                <p class="admin-kicker">
                    <i class="fa-solid fa-truck"></i>
                    SUPPLIER MANAGEMENT
                </p>

                <h2>${supplier.supplierName}</h2>

            </div>

            <div class="toolbar-buttons">

                <a href="/admin/suppliers/${supplier.id}/edit"
                   class="admin-button">

                    <i class="fa-solid fa-pen"></i>
                    Edit

                </a>

                <a href="/admin/suppliers"
                   class="admin-button admin-button--ghost">

                    <i class="fa-solid fa-arrow-left"></i>
                    Back

                </a>

            </div>

        </div>


        <div class="detail-card">

            <div class="detail-grid">

                <div class="detail-item">

                    <label>Supplier Name</label>

                    <div class="detail-value">
                        ${supplier.supplierName}
                    </div>

                </div>

                <div class="detail-item">

                    <label>Contact Person</label>

                    <div class="detail-value">
                        ${supplier.contactPerson}
                    </div>

                </div>

                <div class="detail-item">

                    <label>Email</label>

                    <div class="detail-value">
                        ${supplier.email}
                    </div>

                </div>

                <div class="detail-item">

                    <label>Phone</label>

                    <div class="detail-value">
                        ${supplier.phone}
                    </div>

                </div>

                <div class="detail-item">

                    <label>Status</label>

                    <div class="detail-value">

                        <c:choose>

                            <c:when test="${supplier.active}">
                                <span class="status-active">
                                    Active
                                </span>
                            </c:when>

                            <c:otherwise>
                                <span class="status-hidden">
                                    Hidden
                                </span>
                            </c:otherwise>

                        </c:choose>

                    </div>

                </div>

                <div class="detail-item">

                    <label>Created At</label>

                    <div class="detail-value">
                        ${supplier.createdAt}
                    </div>

                </div>

                <div class="detail-item detail-item-full">

                    <label>Address</label>

                    <div class="detail-value">
                        ${supplier.address}
                    </div>

                </div>

                <div class="detail-item detail-item-full">

                    <label>Description</label>

                    <div class="detail-value">

                        ${empty supplier.description ? "No description." : supplier.description}

                    </div>

                </div>

            </div>

        </div>

    </section>

</main>

</body>

</html>