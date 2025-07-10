package com.trust.ayzis.ayzis.controller;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    
    @Autowired
    private IUsuarioService usuarioService;
    
    /**
     * Lista todos os usuários (apenas ADMIN e MANAGER)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> listarUsuarios() {
        try {
            List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            logger.error("Erro ao listar usuários: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Lista usuários ativos (apenas ADMIN e MANAGER)
     */
    @GetMapping("/ativos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> listarUsuariosAtivos() {
        try {
            List<UsuarioResponseDTO> usuarios = usuarioService.listarAtivos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            logger.error("Erro ao listar usuários ativos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Busca usuário por ID (apenas ADMIN e MANAGER)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> buscarUsuario(@PathVariable Long id) {
        try {
            Optional<UsuarioResponseDTO> usuario = usuarioService.buscarPorId(id);
            
            if (usuario.isPresent()) {
                return ResponseEntity.ok(usuario.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Busca usuários por nome (apenas ADMIN e MANAGER)
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> buscarUsuariosPorNome(@RequestParam String nome) {
        try {
            List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorNome(nome);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários por nome: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Lista usuários por role (apenas ADMIN)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarUsuariosPorRole(@PathVariable Usuario.Role role) {
        try {
            List<UsuarioResponseDTO> usuarios = usuarioService.listarPorRole(role);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            logger.error("Erro ao listar usuários por role: {}", e.getMessage(), e);
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
     * Atualiza informações do usuário autenticado
     */
    @PutMapping("/me")
    public ResponseEntity<?> atualizarUsuarioAutenticado(@Valid @RequestBody RegisterRequestDTO updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            UsuarioResponseDTO response = usuarioService.atualizarUsuario(usuario.getId(), updateRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao atualizar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar usuário: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Altera senha do usuário autenticado
     */
    @PutMapping("/me/senha")
    public ResponseEntity<?> alterarSenha(@RequestBody Map<String, String> senhas) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            String senhaAtual = senhas.get("senhaAtual");
            String novaSenha = senhas.get("novaSenha");
            
            if (senhaAtual == null || novaSenha == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Senha atual e nova senha são obrigatórias"));
            }
            
            usuarioService.alterarSenha(usuario.getId(), senhaAtual, novaSenha);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Senha alterada com sucesso");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao alterar senha: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao alterar senha: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Atualiza usuário por ID (apenas ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody RegisterRequestDTO updateRequest) {
        try {
            UsuarioResponseDTO response = usuarioService.atualizarUsuario(id, updateRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao atualizar usuário {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar usuário {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Altera status do usuário (ativo/inativo) - apenas ADMIN
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> alterarStatusUsuario(@PathVariable Long id, @RequestBody Map<String, Boolean> status) {
        try {
            Boolean ativo = status.get("ativo");
            if (ativo == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Status 'ativo' é obrigatório"));
            }
            
            UsuarioResponseDTO response = usuarioService.alterarStatusUsuario(id, ativo);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao alterar status do usuário {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao alterar status do usuário {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Altera role do usuário - apenas ADMIN
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> alterarRoleUsuario(@PathVariable Long id, @RequestBody Map<String, Usuario.Role> roleData) {
        try {
            Usuario.Role novaRole = roleData.get("role");
            if (novaRole == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Role é obrigatória"));
            }
            
            UsuarioResponseDTO response = usuarioService.alterarRoleUsuario(id, novaRole);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao alterar role do usuário {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao alterar role do usuário {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Deleta usuário - apenas ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletarUsuario(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário deletado com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao deletar usuário {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar usuário {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }
    
    /**
     * Estatísticas de usuários - apenas ADMIN
     */
    @GetMapping("/admin/estatisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEstatisticasUsuarios() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsuarios", usuarioService.listarTodos().size());
            stats.put("usuariosAtivos", usuarioService.contarUsuariosAtivos());
            stats.put("administradores", usuarioService.listarPorRole(Usuario.Role.ADMIN).size());
            stats.put("managers", usuarioService.listarPorRole(Usuario.Role.MANAGER).size());
            stats.put("usuarios", usuarioService.listarPorRole(Usuario.Role.USER).size());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Erro ao obter estatísticas de usuários: {}", e.getMessage(), e);
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
