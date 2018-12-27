package com.example.demo.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author anonymity
 * @create 2018-12-26 16:33
 **/
@RestController
@RequestMapping
public class DemoController {

    @GetMapping("/index")
    public String index() {
        return "hello";
    }
}
