package com.conquistadores.gestionclub.modules.auth.controller;

import com.conquistadores.gestionclub.modules.auth.dto.AuthResponse;
import com.conquistadores.gestionclub.modules.auth.dto.LoginRequest;
import com.conquistadores.gestionclub.modules.auth.dto.RegisterRequest;
import com.conquistadores.gestionclub.modules.auth.dto.UsuarioDTO;
import com.conquistadores.gestionclub.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.conquistadores.gestionclub.modules.auth.model.Rol;

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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioDTO> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registerUser(registerRequest));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioDTO> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR')")
    public ResponseEntity<List<UsuarioDTO>> getUsers() {
        return ResponseEntity.ok(authService.getUsersForCurrentUser());
    }

    @GetMapping("/users/grouped")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<com.conquistadores.gestionclub.modules.auth.dto.UsuariosAgrupadosDTO>> getGroupedUsers() {
        return ResponseEntity.ok(authService.getGroupedUsers());
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR')")
    public ResponseEntity<List<Rol>> getRoles() {
        return ResponseEntity.ok(authService.getAllRoles());
    }

    @PutMapping("/users/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR')")
    public ResponseEntity<UsuarioDTO> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(authService.toggleUserStatus(id));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> updateUser(@PathVariable Long id, @RequestBody com.conquistadores.gestionclub.modules.auth.dto.UserUpdateRequest request) {
        return ResponseEntity.ok(authService.updateUser(id, request));
    }
}
