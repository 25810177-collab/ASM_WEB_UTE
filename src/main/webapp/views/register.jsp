<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: linear-gradient(135deg, #f0fdf4, #f8fafc); }
        .register-card { border: none; border-radius: 18px; box-shadow: 0 12px 30px rgba(0,0,0,.08); }
    </style>
</head>
<body>
<jsp:include page="common/header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-7">
            <div class="card register-card">
                <div class="card-body p-4 p-lg-5">
                    <h3 class="text-center mb-4 fw-bold">Đăng ký tài khoản</h3>
                    <form method="post" action="/register">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label">Tên đăng nhập</label>
                                <input type="text" class="form-control" name="username" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Họ tên</label>
                                <input type="text" class="form-control" name="fullName" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Email</label>
                                <input type="email" class="form-control" name="email" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Số điện thoại</label>
                                <input type="text" class="form-control" name="phone" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Mật khẩu</label>
                                <input type="password" class="form-control" name="password" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Vai trò</label>
                                <select class="form-select" name="role">
                                    <option value="ADMIN">Admin</option>
                                    <option value="LECTURER">Giảng viên</option>
                                    <option value="STUDENT">Sinh viên</option>
                                    <option value="DEPARTMENT_HEAD">Trưởng khoa</option>
                                </select>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-success btn-lg w-100 mt-4">Đăng ký</button>
                    </form>
                    <div class="text-center mt-3">
                        <a href="/login" class="text-decoration-none">Đã có tài khoản? Đăng nhập</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
