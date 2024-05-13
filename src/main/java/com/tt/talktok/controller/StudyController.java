package com.tt.talktok.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/study")
public class StudyController {

    @GetMapping("/room")
    public String room(int lec_no, Model model) {
        model.addAttribute("len_no", lec_no);
        return "study/videoroomtest";
    }
}
