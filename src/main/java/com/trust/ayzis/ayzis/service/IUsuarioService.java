package com.trust.ayzis.ayzis.service;

import com.trust.ayzis.ayzis.dto.AuthResponseDTO;
import com.trust.ayzis.ayzis.dto.LoginRequestDTO;
import com.trust.ayzis.ayzis.dto.RegisterRequestDTO;
import com.trust.ayzis.ayzis.dto.UsuarioResponseDTO;
import com.trust.ayzis.ayzis.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {

    /**
     * Registra um novo usuário
     */
    AuthResponseDTO registrarUsuario(RegisterRequestDTO registerRequest);

    /**
     * Autentica um usuário
     */
    AuthResponseDTO autenticarUsuario(LoginRequestDTO loginRequest);

    /**
     * Busca um usuário por ID
     */
    Optional<UsuarioResponseDTO> buscarPorId(Long id);

    /**
     * Busca um usuário por email
     */
    Optional<Usuario> buscarPorEmail(String email);

    /**
     * Lista todos os usuários
     */
    List<UsuarioResponseDTO> listarTodos();

    /**
     * Lista usuários ativos
     */
    List<UsuarioResponseDTO> listarAtivos();

    /**
     * Lista usuários por role
     */
    List<UsuarioResponseDTO> listarPorRole(Usuario.Role role);

    /**
     * Atualiza os dados de um usuário
     */
    UsuarioResponseDTO atualizarUsuario(Long id, RegisterRequestDTO updateRequest);

    /**
     * Ativa/desativa um usuário
     */
    UsuarioResponseDTO alterarStatusUsuario(Long id, boolean ativo);

    /**
     * Altera a role de um usuário
     */
    UsuarioResponseDTO alterarRoleUsuario(Long id, Usuario.Role novaRole);

    /**
     * Deleta um usuário
     */
    void deletarUsuario(Long id);

    /**
     * Verifica se existe usuário com o email
     */
    boolean existeUsuarioComEmail(String email);

    /**
     * Atualiza o último acesso do usuário
     */
    void atualizarUltimoAcesso(String email);

    /**
     * Conta usuários ativos
     */
    long contarUsuariosAtivos();

    /**
     * Busca usuários por nome (case insensitive)
     */
    List<UsuarioResponseDTO> buscarPorNome(String nome);

    /**
     * Altera senha do usuário
     */
    void alterarSenha(Long id, String senhaAtual, String novaSenha);
}
