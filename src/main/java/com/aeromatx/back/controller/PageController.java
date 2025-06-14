package com.aeromatx.back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {

    @GetMapping("/")
    public String login() {
        return "login";
    }

   
}
