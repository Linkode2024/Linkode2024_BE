package com.linkode.api_server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/linkode")
    public String linkode(){
        return "Linkode Alive!";
    }

}
