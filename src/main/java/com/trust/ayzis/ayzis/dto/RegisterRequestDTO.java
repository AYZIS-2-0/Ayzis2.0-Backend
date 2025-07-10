package com.trust.ayzis.ayzis.dto;

import com.trust.ayzis.ayzis.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String senha;
    
    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmarSenha;
    
    private Usuario.Role role = Usuario.Role.USER; // Default role
    
    // Constructors
    public RegisterRequestDTO() {}
    
    public RegisterRequestDTO(String nome, String email, String senha, String confirmarSenha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confirmarSenha = confirmarSenha;
    }
    
    // Custom validation method
    public boolean senhasConferem() {
        return senha != null && senha.equals(confirmarSenha);
    }
    
    // Getters and Setters
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
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getConfirmarSenha() {
        return confirmarSenha;
    }
    
    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
    
    public Usuario.Role getRole() {
        return role;
    }
    
    public void setRole(Usuario.Role role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "RegisterRequestDTO{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='[PROTECTED]'" +
                ", confirmarSenha='[PROTECTED]'" +
                ", role=" + role +
                '}';
    }
}
