package com.example.url.Controller;

import com.example.url.Dtos.RequestDto;
import com.example.url.Dtos.ResponseDto;
import com.example.url.Service.Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    private final Service linkService;
    public TestController(Service service){
        this.linkService = service;

    }

    @GetMapping("/")
    public String home() {
        return "URL Shortener is running 🚀";
    }
    @PostMapping("/")
    public ResponseDto createURL(@RequestBody RequestDto dto, HttpServletRequest request){
        String ip = request.getRemoteAddr();
        return linkService.createLink(dto,ip);

    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {

        String longUrl = linkService.redirectLogic(code);

        return ResponseEntity
                .status(302)
                .header("Location", longUrl)
                .build();
    } }