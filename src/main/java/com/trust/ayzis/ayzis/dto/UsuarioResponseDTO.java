package com.trust.ayzis.ayzis.dto;

import com.trust.ayzis.ayzis.model.Usuario;

import java.time.LocalDateTime;

public class UsuarioResponseDTO {
    
    private Long id;
    private String nome;
    private String email;
    private Usuario.Role role;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime ultimoAcesso;
    
    // Constructors
    public UsuarioResponseDTO() {}
    
    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
        this.ativo = usuario.getAtivo();
        this.dataCriacao = usuario.getDataCriacao();
        this.ultimoAcesso = usuario.getUltimoAcesso();
    }
    
    // Static factory method
    public static UsuarioResponseDTO fromUsuario(Usuario usuario) {
        return new UsuarioResponseDTO(usuario);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Usuario.Role getRole() {
        return role;
    }
    
    public void setRole(Usuario.Role role) {
        this.role = role;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getUltimoAcesso() {
        return ultimoAcesso;
    }
    
    public void setUltimoAcesso(LocalDateTime ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }
    
    @Override
    public String toString() {
        return "UsuarioResponseDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", ativo=" + ativo +
                ", dataCriacao=" + dataCriacao +
                ", ultimoAcesso=" + ultimoAcesso +
                '}';
    }
}
