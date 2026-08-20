package ute.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lecturer")
public class LecturerController {

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard() {
        return "lecturer/dashboard";
    }

    @GetMapping("/topics")
    public String topics() {
        return "lecturer/topics";
    }

    @GetMapping("/reviews")
    public String reviews() {
        return "lecturer/reviews";
    }
}