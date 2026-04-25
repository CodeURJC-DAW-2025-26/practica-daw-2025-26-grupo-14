package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class reactController {
    
    @GetMapping("/new")
    public String reactApp() {
        return "react";
    }
}
