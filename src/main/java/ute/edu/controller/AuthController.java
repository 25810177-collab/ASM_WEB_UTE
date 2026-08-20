package ute.edu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ute.edu.dto.RegisterRequest;
import ute.edu.enums.UserRole;
import ute.edu.model.UserAccount;
import ute.edu.service.AuthService;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        UserAccount user = authService.loginUser(username, password);
        if (user == null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "login";
        }

        session.setAttribute("user", user);
        session.setAttribute("userRole", user.getRole());

        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.DEPARTMENT_HEAD) {
            return "redirect:/admin";
        }
        if (user.getRole() == UserRole.LECTURER) {
            return "redirect:/lecturer";
        }
        return "redirect:/student";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(RegisterRequest request, Model model) {
        try {
            UserAccount user = new UserAccount();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setRole(UserRole.valueOf(request.getRole()));

            authService.register(user);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại. Vui lòng kiểm tra lại thông tin.");
            model.addAttribute("roles", UserRole.values());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
