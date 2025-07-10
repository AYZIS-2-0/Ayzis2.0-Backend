package com.trust.ayzis.ayzis.dto;

import com.trust.ayzis.ayzis.model.Usuario;

import java.time.LocalDateTime;

public class AuthResponseDTO {
    
    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String nome;
    private String email;
    private Usuario.Role role;
    private LocalDateTime expiresAt;
    
    // Constructors
    public AuthResponseDTO() {}
    
    public AuthResponseDTO(String token, Usuario usuario, LocalDateTime expiresAt) {
        this.token = token;
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
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
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    @Override
    public String toString() {
        return "AuthResponseDTO{" +
                "token='[PROTECTED]'" +
                ", tipo='" + tipo + '\'' +
                ", id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
