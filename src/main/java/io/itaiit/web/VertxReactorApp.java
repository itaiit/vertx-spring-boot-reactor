package io.itaiit.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VertxReactorApp {

    @GetMapping("/")
    public String hello() {
        System.out.println("VertxReactorApp.hello");
        return "Hello, Vert.x Reactor!";
    }

}
