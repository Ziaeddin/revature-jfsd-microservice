package com.zia.ec2.spring.test.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from *** Dockerized *** Spring Boot app hosted by *** AWS EC2! ****   ;-)";
    }
}
