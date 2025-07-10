package com.trust.ayzis.ayzis.service;

import com.trust.ayzis.ayzis.dto.AuthResponseDTO;
import com.trust.ayzis.ayzis.dto.LoginRequestDTO;
import com.trust.ayzis.ayzis.dto.RegisterRequestDTO;
import com.trust.ayzis.ayzis.dto.UsuarioResponseDTO;
import com.trust.ayzis.ayzis.model.IUsuarioRepository;
import com.trust.ayzis.ayzis.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService implements IUsuarioService, UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    
    @Autowired
    private IUsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        
        logger.debug("Usuário encontrado: {}", usuario.getEmail());
        return usuario;
    }
    
    @Override
    public AuthResponseDTO registrarUsuario(RegisterRequestDTO registerRequest) {
        logger.info("Registrando novo usuário: {}", registerRequest.getEmail());
        
        // Validar se as senhas conferem
        if (!registerRequest.senhasConferem()) {
            throw new IllegalArgumentException("As senhas não conferem");
        }
        
        // Verificar se já existe usuário com este email
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }
        
        // Criar novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(registerRequest.getNome());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setSenha(passwordEncoder.encode(registerRequest.getSenha()));
        usuario.setRole(registerRequest.getRole());
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        
        // Salvar usuário
        usuario = usuarioRepository.save(usuario);
        
        // Gerar token
        String token = jwtService.generateToken(usuario);
        LocalDateTime expiresAt = jwtService.getExpirationAsLocalDateTime(token);
        
        logger.info("Usuário registrado com sucesso: {}", usuario.getEmail());
        return new AuthResponseDTO(token, usuario, expiresAt);
    }
    
    @Override
    public AuthResponseDTO autenticarUsuario(LoginRequestDTO loginRequest) {
        logger.info("Autenticando usuário: {}", loginRequest.getEmail());
        
        try {
            // Autenticar credenciais
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getSenha()
                    )
            );
            
            // Buscar usuário
            Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
            
            // Verificar se o usuário está ativo
            if (!usuario.getAtivo()) {
                throw new IllegalArgumentException("Usuário inativo");
            }
            
            // Atualizar último acesso
            usuario.atualizarUltimoAcesso();
            usuarioRepository.save(usuario);
            
            // Gerar token
            String token = jwtService.generateToken(usuario);
            LocalDateTime expiresAt = jwtService.getExpirationAsLocalDateTime(token);
            
            logger.info("Usuário autenticado com sucesso: {}", usuario.getEmail());
            return new AuthResponseDTO(token, usuario, expiresAt);
            
        } catch (AuthenticationException e) {
            logger.warn("Falha na autenticação para: {}", loginRequest.getEmail());
            throw new IllegalArgumentException("Credenciais inválidas");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponseDTO::fromUsuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromUsuario)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarAtivos() {
        return usuarioRepository.findByAtivoTrue().stream()
                .map(UsuarioResponseDTO::fromUsuario)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorRole(Usuario.Role role) {
        return usuarioRepository.findByRole(role).stream()
                .map(UsuarioResponseDTO::fromUsuario)
                .collect(Collectors.toList());
    }
    
    @Override
    public UsuarioResponseDTO atualizarUsuario(Long id, RegisterRequestDTO updateRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Verificar se o email já está em uso por outro usuário
        if (!usuario.getEmail().equals(updateRequest.getEmail()) &&
            usuarioRepository.existsByEmail(updateRequest.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }
        
        // Atualizar dados
        usuario.setNome(updateRequest.getNome());
        usuario.setEmail(updateRequest.getEmail());
        
        // Atualizar senha se fornecida
        if (updateRequest.getSenha() != null && !updateRequest.getSenha().isEmpty()) {
            if (!updateRequest.senhasConferem()) {
                throw new IllegalArgumentException("As senhas não conferem");
            }
            usuario.setSenha(passwordEncoder.encode(updateRequest.getSenha()));
        }
        
        usuario = usuarioRepository.save(usuario);
        logger.info("Usuário atualizado: {}", usuario.getEmail());
        
        return UsuarioResponseDTO.fromUsuario(usuario);
    }
    
    @Override
    public UsuarioResponseDTO alterarStatusUsuario(Long id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        usuario.setAtivo(ativo);
        usuario = usuarioRepository.save(usuario);
        
        logger.info("Status do usuário {} alterado para: {}", usuario.getEmail(), ativo);
        return UsuarioResponseDTO.fromUsuario(usuario);
    }
    
    @Override
    public UsuarioResponseDTO alterarRoleUsuario(Long id, Usuario.Role novaRole) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        usuario.setRole(novaRole);
        usuario = usuarioRepository.save(usuario);
        
        logger.info("Role do usuário {} alterada para: {}", usuario.getEmail(), novaRole);
        return UsuarioResponseDTO.fromUsuario(usuario);
    }
    
    @Override
    public void deletarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        usuarioRepository.delete(usuario);
        logger.info("Usuário deletado: {}", usuario.getEmail());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeUsuarioComEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    @Override
    public void atualizarUltimoAcesso(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            usuario.atualizarUltimoAcesso();
            usuarioRepository.save(usuario);
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarUsuariosAtivos() {
        return usuarioRepository.countUsuariosAtivos();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(UsuarioResponseDTO::fromUsuario)
                .collect(Collectors.toList());
    }
    
    @Override
    public void alterarSenha(Long id, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Verificar senha atual
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        
        // Atualizar senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        
        logger.info("Senha alterada para usuário: {}", usuario.getEmail());
    }
}
