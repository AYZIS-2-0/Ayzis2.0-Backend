// filepath: c:\Users\Raposo\Documents\projects\ayzis2\Ayzis2.0-Backend\src\main\java\com\trust\ayzis\ayzis\dto\bling\BlingAccountRequestDTO.java
package com.trust.ayzis.ayzis.dto;

import jakarta.validation.constraints.NotBlank;

public class BlingAccountRequestDTO {
    @NotBlank(message = "Nome da conta é obrigatório")
    private String accountName;
    
    @NotBlank(message = "Client ID é obrigatório")
    private String clientId;
    
    @NotBlank(message = "Client Secret é obrigatório")
    private String clientSecret;

    // Getters e Setters
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}