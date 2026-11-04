package com.mo5ul.ticketforge.auth;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/auth")
public class AuthController{
    private final AuthService service;
    public AuthController(AuthService service){
        this.service =service;
    }
    @PostMapping("/register")
    @ResponseStatus (HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req){
        return service.register(req);
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req){
        return service.login(req);
    }
}