<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f3f6fb; }
        .sidebar { background: #111827; min-height: 100vh; }
        .sidebar .nav-link { color: #d1d5db; padding: 12px 18px; border-radius: 10px; }
        .sidebar .nav-link:hover { background: #1f2937; color: white; }
        .box { border-radius: 16px; box-shadow: 0 8px 20px rgba(0,0,0,.05); }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-2 sidebar p-3">
            <h4 class="text-white mb-4">Admin</h4>
            <nav class="nav flex-column">
                <a class="nav-link active" href="/admin">Dashboard</a>
                <a class="nav-link" href="#">Đợt đăng ký</a>
                <a class="nav-link" href="#">Đề tài</a>
                <a class="nav-link" href="#">Nhóm SV</a>
                <a class="nav-link" href="#">Phản biện</a>
                <a class="nav-link" href="#">Thông báo</a>
                <a class="nav-link" href="/">Về trang chủ</a>
            </nav>
        </div>
        <div class="col-md-10 p-4">
            <h2 class="mb-4">Tổng quan hệ thống</h2>
            <div class="row g-4">
                <div class="col-md-3">
                    <div class="card box p-3 bg-primary text-white">
                        <div class="small">Đợt đăng ký</div>
                        <div class="display-6 fw-bold">12</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card box p-3 bg-success text-white">
                        <div class="small">Đề tài</div>
                        <div class="display-6 fw-bold">86</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card box p-3 bg-warning text-dark">
                        <div class="small">Nhóm SV</div>
                        <div class="display-6 fw-bold">58</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card box p-3 bg-danger text-white">
                        <div class="small">Hội đồng</div>
                        <div class="display-6 fw-bold">9</div>
                    </div>
                </div>
            </div>

            <div class="card box mt-4">
                <div class="card-body">
                    <h5>Quy trình hệ thống</h5>
                    <ul class="mb-0">
                        <li>Tạo đợt đăng ký theo từng loại đề tài</li>
                        <li>GV đăng ký đề tài và quản lý danh sách</li>
                        <li>SV đăng ký nhóm và đề tài</li>
                        <li>Phê duyệt, phân công GVHD/GVPB</li>
                        <li>Phản biện, chấm điểm và công bố kết quả</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
