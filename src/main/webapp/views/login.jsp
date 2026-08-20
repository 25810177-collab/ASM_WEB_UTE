<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: linear-gradient(135deg, #eef4ff, #f8fafc); }
        .login-card { border: none; border-radius: 18px; box-shadow: 0 12px 30px rgba(0,0,0,.08); }
    </style>
</head>
<body>
<jsp:include page="common/header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card login-card">
                <div class="card-body p-4 p-lg-5">
                    <h3 class="text-center mb-4 fw-bold">Đăng nhập</h3>
                    <form method="post" action="/login">
                        <div class="mb-3">
                            <label class="form-label">Tên đăng nhập</label>
                            <input type="text" class="form-control form-control-lg" name="username" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mật khẩu</label>
                            <input type="password" class="form-control form-control-lg" name="password" required>
                        </div>
                        <button type="submit" class="btn btn-primary btn-lg w-100">Đăng nhập</button>
                    </form>
                    <div class="text-center mt-3">
                        <a href="/register" class="text-decoration-none">Chưa có tài khoản? Đăng ký</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
