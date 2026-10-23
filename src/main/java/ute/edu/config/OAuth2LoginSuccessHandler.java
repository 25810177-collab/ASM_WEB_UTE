package ute.edu.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import ute.edu.entity.UserAccount;
import ute.edu.enums.UserRole;
import ute.edu.repository.LectureRepository;
import ute.edu.repository.StudentRepository;
import ute.edu.repository.UserAccountRepository;
import ute.edu.service.AuthTokenService;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // Đối chiếu email Google với tài khoản và vai trò do hệ thống quản lý.
    private final UserAccountRepository userAccountRepository;

    // Tạo token đăng nhập riêng của ứng dụng, độc lập với access token của Google.
    private final AuthTokenService authTokenService;

    // Kiểm tra và lấy hồ sơ sinh viên liên kết với UserAccount.
    private final StudentRepository studentRepository;

    // Kiểm tra và lấy hồ sơ giảng viên, bao gồm các chức danh quản lý giảng viên.
    private final LectureRepository lectureRepository;
    public OAuth2LoginSuccessHandler(UserAccountRepository userAccountRepository,
            AuthTokenService authTokenService,
            StudentRepository studentRepository,
            LectureRepository lectureRepository) {
        this.userAccountRepository = userAccountRepository;
        this.authTokenService = authTokenService;
        this.studentRepository = studentRepository;
        this.lectureRepository = lectureRepository;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
       
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        if (email != null) email = email.trim().toLowerCase(java.util.Locale.ROOT);
        if (!Boolean.TRUE.equals(oauthUser.getAttribute("email_verified"))) {
            reject(request, response, "unverified_email");
            return;
        }

        if (email == null || !(email.endsWith("@hcmute.edu.vn") || email.endsWith("@student.hcmute.edu.vn")
                || email.endsWith("@teacher.hcmute.edu.vn"))) {
            reject(request, response, "invalid_domain");
            return;
        }

       
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(email);

        if (user == null && email.matches("[0-9]+@(student\\.)?hcmute\\.edu\\.vn")) {
            // Tạo session nếu chưa có, đổi mã phiên nhưng giữ các thuộc tính session.
            HttpSession pending = request.getSession(true);
            request.changeSessionId();

            // Xóa thông tin người dùng cũ trong session trước khi chuẩn bị hồ sơ mới.
            pending.removeAttribute("user");
            pending.removeAttribute("userRole");
            pending.removeAttribute("studentProfile");
            pending.removeAttribute("lecturerProfile");
            pending.setAttribute("googleRegistrationEmail", email);
            pending.setAttribute("googleRegistrationName", oauthUser.getAttribute("name"));
            pending.setAttribute("googleRegistrationToken", java.util.UUID.randomUUID().toString());
           
            SecurityContextHolder.clearContext();
            pending.removeAttribute("SPRING_SECURITY_CONTEXT");
          
            response.sendRedirect(request.getContextPath() + "/google/complete-profile");
            return;
        }
       
        if (user == null || !user.isEnabled()) {
            reject(request, response, user == null ? "account_not_found" : "account_disabled");
            return;
        }
       
        if ((user.getRole() == UserRole.STUDENT && studentRepository.findByUserId(user.getId()) == null)
                || (user.getRole() != UserRole.STUDENT && user.getRole() != UserRole.ADMIN
                && lectureRepository.findByUserId(user.getId()) == null)) {
            reject(request, response, "missing_profile");
            return;
        }

        
        HttpSession session = request.getSession(true);
        request.changeSessionId();

      
        session.removeAttribute("studentProfile");
        session.removeAttribute("lecturerProfile");
        session.removeAttribute("googleRegistrationEmail");
        session.removeAttribute("googleRegistrationName");
        session.removeAttribute("googleRegistrationToken");
       
        session.setAttribute("user", user);
        session.setAttribute("userRole", user.getRole());
        if (user.getRole() == UserRole.STUDENT) {
            session.setAttribute("studentProfile", studentRepository.findByUserId(user.getId()));
        } else if (user.getRole() == UserRole.LECTURER || user.getRole() == UserRole.DEAN
                || user.getRole() == UserRole.DEPARTMENT_HEAD) {
            session.setAttribute("lecturerProfile", lectureRepository.findByUserId(user.getId()));
        }
      
        String token = authTokenService.issue(user);

        // Chỉ thêm Secure khi request được nhận qua HTTPS.
        String secure = request.isSecure() ? "; Secure" : "";

      
        String contextPath = request.getContextPath();
        String path = (contextPath == null || contextPath.isEmpty()) ? "/" : contextPath;

       
        response.addHeader("Set-Cookie", AuthTokenService.COOKIE_NAME + "=" + token
                + "; Max-Age=" + authTokenService.getTokenTtlSeconds()
                + "; Path=" + path + "; HttpOnly; SameSite=Lax" + secure);

      
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.DEAN
                || user.getRole() == UserRole.DEPARTMENT_HEAD) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if (user.getRole() == UserRole.LECTURER) {
            response.sendRedirect(request.getContextPath() + "/lecturer/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }

    private void reject(HttpServletRequest request, HttpServletResponse response, String reason) throws IOException {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) request.getSession(false).invalidate();
        response.sendRedirect(request.getContextPath() + "/login?error=" + reason);
    }

}
