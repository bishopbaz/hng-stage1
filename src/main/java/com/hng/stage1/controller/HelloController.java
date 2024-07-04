package com.hng.stage1.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.hng.stage1.entities.HelloResponse;
import com.hng.stage1.service.HelloService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public ResponseEntity<HelloResponse>sayHello(@RequestParam("visitor_name") String visitorName, HttpServletRequest request){
       return helloService.sayHello(visitorName, request);
    }


}
