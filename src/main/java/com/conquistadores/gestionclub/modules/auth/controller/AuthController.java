package com.conquistadores.gestionclub.modules.auth.controller;

import com.conquistadores.gestionclub.modules.auth.dto.AuthResponse;
import com.conquistadores.gestionclub.modules.auth.dto.LoginRequest;
import com.conquistadores.gestionclub.modules.auth.dto.RegisterRequest;
import com.conquistadores.gestionclub.modules.auth.dto.UsuarioDTO;
import com.conquistadores.gestionclub.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registerUser(registerRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
