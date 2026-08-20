package ute.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentController {

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard() {
        return "student/dashboard";
    }

    @GetMapping("/topics")
    public String topics() {
        return "student/topics";
    }

    @GetMapping("/group")
    public String group() {
        return "student/group";
    }
}