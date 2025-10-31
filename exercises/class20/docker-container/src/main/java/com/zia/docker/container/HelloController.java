package com.zia.docker.container;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping
    public String Hello() {
        return "Hello from SPRING APP in Cdocker container";
    }

}
