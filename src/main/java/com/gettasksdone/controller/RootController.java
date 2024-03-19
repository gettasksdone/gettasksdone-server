package com.gettasksdone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class RootController {

    @GetMapping("/ping")
    public String ping(){
        return "Pong";
    }
    @GetMapping("/authedPing")
    public String authedPing(){
        return "Authed Pong";
    }

}