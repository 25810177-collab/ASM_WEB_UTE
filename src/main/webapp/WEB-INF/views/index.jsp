<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ thống quản lý đề tài</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body class="bg-light">
    <div class="container py-5">
        <div class="card shadow">
            <div class="card-body">
                <h1 class="text-center mb-4">Hệ thống quản lý đề tài</h1>
                <p class="text-center text-muted">Quản lý đợt đăng ký, đề tài, hội đồng phản biện, chấm điểm và công bố kết quả.</p>

                <div class="row mt-4">
                    <div class="col-md-4">
                        <div class="card h-100 border-0 shadow-sm">
                            <div class="card-body">
                                <h5>Đợt đăng ký</h5>
                                <p>Quản lý thời gian đăng ký, loại đề tài và lịch công bố.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card h-100 border-0 shadow-sm">
                            <div class="card-body">
                                <h5>Đề tài</h5>
                                <p>Danh sách đề tài theo bộ môn, GVHD/GVPB và trạng thái phê duyệt.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card h-100 border-0 shadow-sm">
                            <div class="card-body">
                                <h5>Nhóm SV</h5>
                                <p>Quản lý nhóm tối đa 3 thành viên, nhóm trưởng và đăng ký đề tài.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="mt-5 text-center">
                    <a href="/login" class="btn btn-primary btn-lg me-2">Đăng nhập</a>
                    <a href="/register" class="btn btn-success btn-lg">Đăng ký</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
