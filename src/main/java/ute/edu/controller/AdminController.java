package ute.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/periods")
    public String registrationPeriods() {
        return "admin/periods";
    }

    @GetMapping("/topics")
    public String topics() {
        return "admin/topics";
    }

    @GetMapping("/reports")
    public String reports() {
        return "admin/reports";
    }
}
