package com.trust.ayzis.ayzis.controller;

import com.trust.ayzis.ayzis.dto.AuthResponseDTO;
import com.trust.ayzis.ayzis.dto.LoginRequestDTO;
import com.trust.ayzis.ayzis.dto.RegisterRequestDTO;
import com.trust.ayzis.ayzis.dto.UsuarioResponseDTO;
import com.trust.ayzis.ayzis.model.Usuario;
import com.trust.ayzis.ayzis.service.IUsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private IUsuarioService usuarioService;
    
    /**
     * Registra um novo usuário
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            logger.info("Tentativa de registro para email: {}", registerRequest.getEmail());
            
            AuthResponseDTO response = usuarioService.registrarUsuario(registerRequest);
            
            logger.info("Usuário registrado com sucesso: {}", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Erro no registro: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado no registro: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Autentica um usuário
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            logger.info("Tentativa de login para email: {}", loginRequest.getEmail());
            
            AuthResponseDTO response = usuarioService.autenticarUsuario(loginRequest);
            
            logger.info("Login realizado com sucesso: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Erro no login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado no login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Obtém informações do usuário autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUsuarioAutenticado() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Usuário não autenticado"));
            }
            
            Usuario usuario = (Usuario) authentication.getPrincipal();
            UsuarioResponseDTO response = UsuarioResponseDTO.fromUsuario(usuario);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erro ao obter usuário autenticado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Verifica se o usuário está autenticado
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Usuário não autenticado"));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Logout (não é necessário para JWT stateless, mas pode ser usado para logs)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            logger.info("Logout realizado para: {}", authentication.getName());
        }
        
        SecurityContextHolder.clearContext();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout realizado com sucesso");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Verifica se um email já está em uso
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            boolean exists = usuarioService.existeUsuarioComEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("email", email);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erro ao verificar email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Helper method para criar respostas de erro
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
