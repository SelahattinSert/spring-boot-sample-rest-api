package com.selo.springboot.demo.firstspringapp.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExRestController {

    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }

    // inject properties for: coach.name and team.name
    @Value("${coach.name}")
    private String coachName;

    @Value("${team.name}")
    private String teamName;

    // new endpoint for "teaminfo"

    @GetMapping("/teaminfo")
    public String teamInfo() {
        return "Coach:" + coachName + "   Team Name:" + teamName;
    }

    // expose new endpoint for "help"

    @GetMapping("/help")
    public String getHelp() {
        return "Help";
    }
}
