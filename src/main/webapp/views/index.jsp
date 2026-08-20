<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ thống quản lý đề tài</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hero { background: linear-gradient(135deg, #0d6efd, #198754); color: white; border-radius: 20px; }
        .card-box { border: none; border-radius: 16px; box-shadow: 0 6px 18px rgba(0,0,0,.08); }
    </style>
</head>
<body>
<jsp:include page="common/header.jsp" />

<div class="container py-5">
    <div class="hero p-5 mb-4">
        <h1 class="display-5 fw-bold mb-3">Hệ thống quản lý đề tài</h1>
        <p class="lead mb-4">Quản lý đợt đăng ký, đề tài, hội đồng phản biện, chấm điểm và công bố kết quả cho khoa CNTT.</p>
        <div class="d-flex gap-3 flex-wrap">
            <a class="btn btn-light btn-lg" href="/login">Đăng nhập</a>
            <a class="btn btn-outline-light btn-lg" href="/register">Đăng ký</a>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-md-4">
            <div class="card card-box h-100">
                <div class="card-body">
                    <h5 class="card-title">1. Đợt đăng ký</h5>
                    <p class="card-text">Tạo và quản lý các đợt đăng ký theo từng giai đoạn với thời gian cho GV và SV.</p>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card card-box h-100">
                <div class="card-body">
                    <h5 class="card-title">2. Đề tài</h5>
                    <p class="card-text">Danh sách đề tài theo bộ môn, phân công GVHD/GVPB và trạng thái phê duyệt.</p>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card card-box h-100">
                <div class="card-body">
                    <h5 class="card-title">3. Phản biện & điểm</h5>
                    <p class="card-text">Tổ chức hội đồng phản biện, chấm điểm và công bố kết quả sau khi hoàn tất.</p>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
