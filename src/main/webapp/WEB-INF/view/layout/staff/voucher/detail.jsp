<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <link rel="stylesheet" href="${ctx}/css/admin-dashboard.css" />
    <link rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />

    <title>Voucher Detail — Booktify Staff</title>

    <style>
        .detail-card {
            background: #fff;
            border-radius: 18px;
            padding: 35px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, .06);
        }

        .detail-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
        }

        .detail-item {
            display: flex;
            flex-direction: column;
        }

        .detail-item label {
            font-size: 13px;
            text-transform: uppercase;
            color: #94a3b8;
            margin-bottom: 8px;
            font-weight: 700;
            letter-spacing: .5px;
        }

        .detail-value {
            background: #f8fafc;
            border: 1px solid #e5e7eb;
            border-radius: 10px;
            min-height: 48px;
            padding: 14px 16px;
            color: #1f2937;
            font-weight: 500;
        }

        .detail-item-full {
            grid-column: 1 / span 2;
        }

        @media (max-width:900px) {
            .detail-grid {
                grid-template-columns: 1fr;
            }

            .detail-item-full {
                grid-column: auto;
            }
        }
    </style>
</head>

<body class="admin-shell">

    <jsp:include page="/WEB-INF/view/layout/staff/sidebar.jsp" />

    <main class="admin-main">

        <jsp:include page="/WEB-INF/view/layout/staff/header.jsp" />

        <section class="admin-content">

            <div class="admin-toolbar">

                <div>
                    <p class="admin-kicker">
                        <i class="fa-solid fa-ticket"></i>
                        VOUCHER MANAGEMENT
                    </p>

                    <h2>${voucher.voucherName}</h2>
                </div>

                <a href="${ctx}/staff/vouchers" class="admin-button admin-button--ghost">
                    <i class="fa-solid fa-arrow-left"></i>
                    Back
                </a>

            </div>

            <div class="detail-card">

                <div class="detail-grid">

                    <div class="detail-item">
                        <label>Voucher Name</label>
                        <div class="detail-value">${voucher.voucherName}</div>
                    </div>

                    <div class="detail-item">
                        <label>Code</label>
                        <div class="detail-value">${voucher.voucherCode}</div>
                    </div>

                    <div class="detail-item">
                        <label>Discount Type</label>
                        <div class="detail-value">${voucher.discountType}</div>
                    </div>

                    <div class="detail-item">
                        <label>Discount Value</label>
                        <div class="detail-value">${voucher.discountValue}</div>
                    </div>

                    <div class="detail-item">
                        <label>Minimum Order</label>
                        <div class="detail-value">${voucher.minOrderAmount}</div>
                    </div>

                    <div class="detail-item">
                        <label>Maximum Order</label>
                        <div class="detail-value">${voucher.maxOrderAmount}</div>
                    </div>

                    <div class="detail-item">
                        <label>Quantity</label>
                        <div class="detail-value">${voucher.quantity}</div>
                    </div>

                    <div class="detail-item">
                        <label>Status</label>
                        <div class="detail-value">${voucher.status}</div>
                    </div>

                    <div class="detail-item">
                        <label>Start Date</label>
                        <div class="detail-value">${voucher.startDate}</div>
                    </div>

                    <div class="detail-item">
                        <label>End Date</label>
                        <div class="detail-value">${voucher.endDate}</div>
                    </div>

                    <div class="detail-item detail-item-full">
                        <label>Description</label>
                        <div class="detail-value">
                            ${empty voucher.description ? "No description." : voucher.description}
                        </div>
                    </div>

                </div>

            </div>

        </section>

    </main>

</body>

</html>
