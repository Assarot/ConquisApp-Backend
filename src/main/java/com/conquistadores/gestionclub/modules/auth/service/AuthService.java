package com.conquistadores.gestionclub.modules.auth.service;

import com.conquistadores.gestionclub.modules.auth.dto.AuthResponse;
import com.conquistadores.gestionclub.modules.auth.dto.LoginRequest;
import com.conquistadores.gestionclub.modules.auth.dto.RegisterRequest;
import com.conquistadores.gestionclub.modules.auth.dto.UsuarioDTO;
import com.conquistadores.gestionclub.modules.auth.model.Rol;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auth.repository.RolRepository;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import com.conquistadores.gestionclub.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return new AuthResponse(jwt, "Bearer");
    }

    @Transactional
    public UsuarioDTO registerUser(RegisterRequest registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado!");
        }

        Club club = clubRepository.findById(registerRequest.getIdClub())
                .orElseThrow(() -> new RuntimeException("Club no encontrado con ID: " + registerRequest.getIdClub()));

        Rol rol = rolRepository.findById(registerRequest.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + registerRequest.getIdRol()));

        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setApellido(registerRequest.getApellido());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setClub(club);
        usuario.setRol(rol);
        usuario.setEstado("ACTIVO");

        Usuario savedUser = usuarioRepository.save(usuario);
        return mapToDTO(savedUser);
    }

    public UsuarioDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("No hay usuario autenticado en la sesión");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return mapToDTO(userDetails.getUsuario());
    }

    private UsuarioDTO mapToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().getNombre());
        dto.setIdClub(usuario.getClub().getIdClub());
        dto.setEstado(usuario.getEstado());
        return dto;
    }
}
