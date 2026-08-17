package com.conquistadores.gestionclub.modules.auth.service;

import com.conquistadores.gestionclub.modules.auth.dto.AuthResponse;
import com.conquistadores.gestionclub.modules.auth.dto.LoginRequest;
import com.conquistadores.gestionclub.modules.auth.dto.RegisterRequest;
import com.conquistadores.gestionclub.modules.auth.dto.UserUpdateRequest;
import com.conquistadores.gestionclub.modules.auth.dto.UsuarioDTO;
import com.conquistadores.gestionclub.modules.auth.model.Rol;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auth.repository.RolRepository;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import com.conquistadores.gestionclub.security.JwtTokenProvider;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.conquistadores.gestionclub.modules.auth.dto.UsuariosAgrupadosDTO;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Debe estar autenticado para registrar usuarios.");
        }

        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        Usuario creator = usuarioRepository.findById(currentUser.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado."));
        String creatorRole = creator.getRol().getNombre().toUpperCase();

        Rol rol = rolRepository.findById(registerRequest.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + registerRequest.getIdRol()));
        String roleToCreateName = rol.getNombre().toUpperCase();

        if ("ADMINISTRADOR".equals(creatorRole)) {
            // Administrador general del sistema tiene acceso total
        } else if ("DIRECTOR".equals(creatorRole)) {
            // Director solo puede registrar roles en su propio club, y no puede crear Administradores ni Directores
            if (creator.getClub() == null || !creator.getClub().getIdClub().equals(registerRequest.getIdClub())) {
                throw new RuntimeException("Un Director solo puede registrar usuarios en su propio club.");
            }
            if ("ADMINISTRADOR".equals(roleToCreateName) || "DIRECTOR".equals(roleToCreateName)) {
                throw new RuntimeException("Un Director no puede registrar Administradores o Directores.");
            }
        } else {
            throw new RuntimeException("No tiene permisos para registrar usuarios.");
        }

        if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado!");
        }

        Club club = null;
        if (registerRequest.getIdClub() != null) {
            club = clubRepository.findById(registerRequest.getIdClub())
                    .orElseThrow(() -> new RuntimeException("Club no encontrado con ID: " + registerRequest.getIdClub()));
        }

        if (!"ADMINISTRADOR".equals(roleToCreateName) && club == null) {
            throw new RuntimeException("Solo el rol de ADMINISTRADOR puede registrarse sin estar asociado a un club.");
        }

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
        dto.setIdClub(usuario.getClub() != null ? usuario.getClub().getIdClub() : null);
        dto.setEstado(usuario.getEstado());
        return dto;
    }

    public List<UsuarioDTO> getUsersForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Debe estar autenticado.");
        }

        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        Usuario creator = usuarioRepository.findById(currentUser.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado."));
        String creatorRole = creator.getRol().getNombre().toUpperCase();

        if ("ADMINISTRADOR".equals(creatorRole)) {
            return usuarioRepository.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } else if ("DIRECTOR".equals(creatorRole)) {
            if (creator.getClub() == null) {
                throw new RuntimeException("El director no pertenece a ningún club.");
            }
            return usuarioRepository.findByClubIdClub(creator.getClub().getIdClub()).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("No tiene permisos para ver los usuarios.");
        }
    }

    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    @Transactional
    public UsuarioDTO toggleUserStatus(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Debe estar autenticado.");
        }

        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        Usuario creator = usuarioRepository.findById(currentUser.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado."));
        String creatorRole = creator.getRol().getNombre().toUpperCase();

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        if ("ADMINISTRADOR".equals(creatorRole)) {
            // Admin can toggle status of any user
        } else if ("DIRECTOR".equals(creatorRole)) {
            if (creator.getClub() == null || usuario.getClub() == null || !creator.getClub().getIdClub().equals(usuario.getClub().getIdClub())) {
                throw new RuntimeException("Un Director solo puede modificar usuarios de su propio club.");
            }
            String targetRole = usuario.getRol().getNombre().toUpperCase();
            if ("ADMINISTRADOR".equals(targetRole) || "DIRECTOR".equals(targetRole)) {
                throw new RuntimeException("Un Director no puede modificar el estado de Administradores o Directores.");
            }
        } else {
            throw new RuntimeException("No tiene permisos para modificar usuarios.");
        }

        usuario.setEstado("ACTIVO".equals(usuario.getEstado()) ? "INACTIVO" : "ACTIVO");
        Usuario updated = usuarioRepository.save(usuario);
        return mapToDTO(updated);
    }

    public List<UsuariosAgrupadosDTO> getGroupedUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Debe estar autenticado.");
        }
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        Usuario creator = usuarioRepository.findById(currentUser.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado."));
        String creatorRole = creator.getRol().getNombre().toUpperCase();

        if (!"ADMINISTRADOR".equals(creatorRole)) {
            throw new RuntimeException("Solo el rol de ADMINISTRADOR puede ver los usuarios agrupados por club.");
        }

        List<Usuario> allUsers = usuarioRepository.findAll();
        List<Club> clubes = clubRepository.findAll();

        List<UsuariosAgrupadosDTO> groupedList = new ArrayList<>();

        for (Club club : clubes) {
            List<UsuarioDTO> clubUsers = allUsers.stream()
                    .filter(u -> u.getClub() != null && u.getClub().getIdClub().equals(club.getIdClub()))
                    .sorted((u1, u2) -> getRolePriority(u1.getRol().getNombre()).compareTo(getRolePriority(u2.getRol().getNombre())))
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            
            groupedList.add(new UsuariosAgrupadosDTO(club.getIdClub(), club.getNombre(), clubUsers));
        }

        List<UsuarioDTO> noClubUsers = allUsers.stream()
                .filter(u -> u.getClub() == null)
                .sorted((u1, u2) -> getRolePriority(u1.getRol().getNombre()).compareTo(getRolePriority(u2.getRol().getNombre())))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        if (!noClubUsers.isEmpty()) {
            groupedList.add(new UsuariosAgrupadosDTO(null, "Sin Club / Administración Global", noClubUsers));
        }

        return groupedList;
    }

    private Integer getRolePriority(String roleName) {
        if (roleName == null) return 99;
        switch (roleName.toUpperCase()) {
            case "ADMINISTRADOR": return 1;
            case "DIRECTOR": return 2;
            case "DIRECTOR_ASOCIADO": return 3;
            case "SECRETARIO": return 4;
            case "INSTRUCTOR": return 5;
            case "CONSEJERO": return 6;
            case "CONQUISTADOR": return 7;
            case "PADRE": return 8;
            default: return 99;
        }
    }

    @Transactional
    public UsuarioDTO updateUser(Long id, UserUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        if (request.getNombre() != null) usuario.setNombre(request.getNombre());
        if (request.getApellido() != null) usuario.setApellido(request.getApellido());
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya está registrado por otro usuario.");
            }
            usuario.setEmail(request.getEmail());
        }
        
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getIdRol() != null) {
            Rol rol = rolRepository.findById(request.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            usuario.setRol(rol);
        }
        
        if (request.getIdClub() != null) {
            Club club = clubRepository.findById(request.getIdClub())
                    .orElseThrow(() -> new RuntimeException("Club no encontrado"));
            usuario.setClub(club);
        } else {
            if (usuario.getRol() != null && "ADMINISTRADOR".equals(usuario.getRol().getNombre())) {
                usuario.setClub(null);
            }
        }
        
        Usuario updated = usuarioRepository.save(usuario);
        return mapToDTO(updated);
    }
}
