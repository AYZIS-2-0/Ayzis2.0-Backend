package com.trust.ayzis.ayzis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trust.ayzis.ayzis.model.Usuario;
import com.trust.ayzis.ayzis.model.Usuario.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email informado
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários ativos
     */
    List<Usuario> findByAtivoTrue();

    /**
     * Busca usuários inativos
     */
    List<Usuario> findByAtivoFalse();

    /**
     * Busca usuários por role
     */
    List<Usuario> findByRole(Usuario.Role role);

    /**
     * Busca usuários ativos por role
     */
    List<Usuario> findByRoleAndAtivoTrue(Usuario.Role role);

    /**
     * Busca usuários que fizeram login após uma data específica
     */
    List<Usuario> findByUltimoAcessoAfter(LocalDateTime dataLimite);

    /**
     * Busca usuários por nome (case insensitive)
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Usuario> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    /**
     * Busca usuários criados entre duas datas
     */
    List<Usuario> findByDataCriacaoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Conta o número de usuários ativos
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.ativo = true")
    long countUsuariosAtivos();

    /**
     * Conta o número de usuários por role
     */
    long countByRole(Usuario.Role role);

    /**
     * Busca usuários administradores ativos
     */
    @Query("SELECT u FROM Usuario u WHERE u.role = 'ADMIN' AND u.ativo = true")
    List<Usuario> findAdministradoresAtivos();

    /**
     * Busca o usuário mais recente criado
     */
    Optional<Usuario> findTopByOrderByDataCriacaoDesc();
}
