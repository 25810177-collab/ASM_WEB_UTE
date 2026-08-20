package ute.edu.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ute.edu.model.TaiKhoan;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		TaiKhoan taiKhoan = (TaiKhoan) session.getAttribute("taiKhoan");

		// Lấy URL người dùng đang truy cập
		String requestUri = request.getRequestURI();

		// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
		if (taiKhoan == null) {
			// Chặn truy cập vào tất cả các URL yêu cầu đăng nhập
			if (requestUri.startsWith("/checkout") || requestUri.startsWith("/changepassword")
					|| requestUri.startsWith("/admin")) {
				response.sendRedirect("/login");
				return false;
			}
		}

		// Ngăn khách hàng (vai trò "khachhang") truy cập vào các trang admin
		if (taiKhoan != null && "khachhang".equals(taiKhoan.getVaiTro()) && requestUri.startsWith("/admin")) {
			response.sendRedirect("/index");
			return false;
		}

		// Nếu truy cập vào các URL yêu cầu quyền admin
		if ((requestUri.startsWith("/admin_user") || requestUri.startsWith("/admin_doanhthu"))
				&& !"admin".equals(taiKhoan.getVaiTro())) {
			response.sendRedirect("/admin");
			return false;
		}

		// Cho phép nhân viên truy cập vào admin_product và admin_donhang
		if (requestUri.startsWith("/admin_product") || requestUri.startsWith("/admin_donhang")) {
			if ("nhanvien".equals(taiKhoan.getVaiTro()) || "admin".equals(taiKhoan.getVaiTro())) {
				return true; // Cho phép truy cập
			} else {
				response.sendRedirect("/admin");
				return false;
			}
		}

		// Các điều kiện khác đều cho phép tiếp tục
		return true;
	}
}
