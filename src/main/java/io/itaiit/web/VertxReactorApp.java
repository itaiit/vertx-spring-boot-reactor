package io.itaiit.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VertxReactorApp {

    @GetMapping("/")
    public String hello() {
        return "Hello, Vert.x Reactor!";
    }

    @PostMapping("/post/{info}")
    public String postInfo(@PathVariable("info") String info) {
        return "Received info: " + info;
    }

}
