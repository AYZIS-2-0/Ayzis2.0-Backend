package com.trust.ayzis.ayzis.controller;

import com.trust.ayzis.ayzis.dto.BlingAccountRequestDTO;
import com.trust.ayzis.ayzis.model.BlingAccount;
import com.trust.ayzis.ayzis.model.Usuario;
import com.trust.ayzis.ayzis.service.BlingService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@RestController
@RequestMapping("/api/bling")
@CrossOrigin(origins = "*")
public class BlingController {

    private static final Logger logger = LogManager.getLogger(BlingController.class);

    @Autowired
    private BlingService blingService;

    /**
     * Adiciona uma nova conta Bling
     */
    @PostMapping("/accounts")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> adicionarConta(@Valid @RequestBody BlingAccountRequestDTO request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = (Usuario) authentication.getPrincipal();

            BlingAccount account = blingService.adicionarContaBling(usuario, request);

            Map<String, Object> response = new HashMap<>();
            response.put("id", account.getId());
            response.put("accountName", account.getAccountName());
            response.put("message", "Conta adicionada com sucesso");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao adicionar conta Bling: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Gera URL de autorização OAuth2
     */
    @GetMapping("/accounts/{accountId}/authorize")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> gerarUrlAutorizacao(@PathVariable Long accountId,
            @RequestParam String redirectUri) {
        try {
            String authUrl = blingService.gerarUrlAutorizacao(accountId, redirectUri);

            Map<String, String> response = new HashMap<>();
            response.put("authorizationUrl", authUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro ao gerar URL de autorização: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Processa callback do OAuth2 e troca código por token
     */
    @PostMapping("/accounts/{accountId}/callback")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> processarCallback(@PathVariable Long accountId,
            @RequestParam String code,
            @RequestParam String redirectUri) {
        try {
            BlingAccount account = blingService.trocarCodigoPorToken(accountId, code, redirectUri);

            Map<String, Object> response = new HashMap<>();
            response.put("id", account.getId());
            response.put("accountName", account.getAccountName());
            response.put("authenticated", account.isTokenValid());
            response.put("message", "Autenticação realizada com sucesso");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro no callback OAuth2: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Lista contas Bling do usuário
     */
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> listarContas() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = (Usuario) authentication.getPrincipal();

            List<BlingAccount> accounts = blingService.listarContasUsuario(usuario);

            // Não expor informações sensíveis
            List<Map<String, Object>> response = accounts.stream()
                    .map(account -> {
                        Map<String, Object> accountData = new HashMap<>();
                        accountData.put("id", account.getId());
                        accountData.put("accountName", account.getAccountName());
                        accountData.put("authenticated", account.isTokenValid());
                        accountData.put("createdAt", account.getCreatedAt());
                        return accountData;
                    })
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro ao listar contas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Remove conta Bling
     */
    @DeleteMapping("/accounts/{accountId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> removerConta(@PathVariable Long accountId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = (Usuario) authentication.getPrincipal();

            blingService.removerConta(usuario, accountId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Conta removida com sucesso");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao remover conta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Busca produtos no Bling
     */
    @GetMapping("/accounts/{accountId}/produtos")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> buscarProdutos(@PathVariable Long accountId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = (Usuario) authentication.getPrincipal();

            // Verificar se a conta pertence ao usuário
            List<BlingAccount> userAccounts = blingService.listarContasUsuario(usuario);
            BlingAccount account = userAccounts.stream()
                    .filter(acc -> acc.getId().equals(accountId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

            if (!account.isTokenValid()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Conta não está autenticada. Realize a autorização novamente."));
            }

            return blingService.buscarProdutos(account)
                    .map(produtos -> ResponseEntity.ok().body(produtos))
                    .doOnError(error -> logger.error("Erro ao buscar produtos: {}", error.getMessage()))
                    .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\":true,\"message\":\"Erro ao buscar produtos no Bling\"}"))
                    .block();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao buscar produtos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        return error;
    }
}