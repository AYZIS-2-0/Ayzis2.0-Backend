package com.trust.ayzis.ayzis.service;

import com.trust.ayzis.ayzis.dto.BlingAccountRequestDTO;
import com.trust.ayzis.ayzis.dto.BlingTokenResponseDTO;
import com.trust.ayzis.ayzis.model.BlingAccount;
import com.trust.ayzis.ayzis.model.Usuario;
import com.trust.ayzis.ayzis.repository.IBlingAccountRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BlingService {

    private static final Logger logger = LogManager.getLogger(BlingService.class);
    private static final String BLING_API_BASE_URL = "https://www.bling.com.br/Api/v3";
    private static final String BLING_OAUTH_URL = "https://www.bling.com.br/Api/v3/oauth/token";

    @Autowired
    private IBlingAccountRepository blingRespository;

    private final WebClient webClient;

    public BlingService() {
        this.webClient = WebClient.builder()
                .baseUrl(BLING_API_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Adiciona uma nova conta Bling para o usuário
     */
    public BlingAccount adicionarContaBling(Usuario usuario, BlingAccountRequestDTO request) {
        logger.info("Adicionando nova conta Bling para usuário: {}", usuario.getEmail());

        // Verificar se já existe uma conta com esse nome
        Optional<BlingAccount> existingAccount = blingRespository
                .findByUsuarioAndAccountNameAndIsActiveTrue(usuario, request.getAccountName());

        if (existingAccount.isPresent()) {
            throw new IllegalArgumentException("Já existe uma conta Bling com esse nome");
        }

        BlingAccount blingAccount = new BlingAccount(
                usuario,
                request.getAccountName(),
                request.getClientId(),
                request.getClientSecret());

        return blingRespository.save(blingAccount);
    }

    /**
     * Gera URL de autorização OAuth2 para o Bling
     */
    public String gerarUrlAutorizacao(Long accountId, String redirectUri) {
        Optional<BlingAccount> accountOpt = blingRespository.findById(accountId);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("Conta Bling não encontrada");
        }

        BlingAccount account = accountOpt.get();

        return String.format(
                "https://www.bling.com.br/Api/v3/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
                account.getClientId(),
                redirectUri,
                accountId // Usando o ID da conta como state para identificar depois
        );
    }

    /**
     * Troca o código de autorização por um token de acesso
     */
    public BlingAccount trocarCodigoPorToken(Long accountId, String authorizationCode, String redirectUri) {
        logger.info("Trocando código de autorização por token para conta: {}", accountId);

        Optional<BlingAccount> accountOpt = blingRespository.findById(accountId);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("Conta Bling não encontrada");
        }

        BlingAccount account = accountOpt.get();

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", authorizationCode);
            formData.add("redirect_uri", redirectUri);

            String credentials = account.getClientId() + ":" + account.getClientSecret();
            String encodedCredentials = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            BlingTokenResponseDTO tokenResponse = WebClient.create()
                    .post()
                    .uri(BLING_OAUTH_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(BlingTokenResponseDTO.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (tokenResponse != null) {
                account.setAccessToken(tokenResponse.getAccessToken());
                account.setRefreshToken(tokenResponse.getRefreshToken());
                account.setTokenExpiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));

                return blingRespository.save(account);
            }

            throw new RuntimeException("Falha ao obter token do Bling");

        } catch (Exception e) {
            logger.error("Erro ao trocar código por token: {}", e.getMessage(), e);
            throw new RuntimeException("Erro na autenticação com Bling: " + e.getMessage());
        }
    }

    /**
     * Atualiza token usando refresh token
     */
    public BlingAccount atualizarToken(BlingAccount account) {
        logger.info("Atualizando token para conta: {}", account.getId());

        if (account.getRefreshToken() == null) {
            throw new IllegalArgumentException("Refresh token não disponível");
        }

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", account.getRefreshToken());

            String credentials = account.getClientId() + ":" + account.getClientSecret();
            String encodedCredentials = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            BlingTokenResponseDTO tokenResponse = WebClient.create()
                    .post()
                    .uri(BLING_OAUTH_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(BlingTokenResponseDTO.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (tokenResponse != null) {
                account.setAccessToken(tokenResponse.getAccessToken());
                if (tokenResponse.getRefreshToken() != null) {
                    account.setRefreshToken(tokenResponse.getRefreshToken());
                }
                account.setTokenExpiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));

                return blingRespository.save(account);
            }

            throw new RuntimeException("Falha ao atualizar token do Bling");

        } catch (Exception e) {
            logger.error("Erro ao atualizar token: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar token do Bling: " + e.getMessage());
        }
    }

    /**
     * Busca produtos no Bling
     */
    public Mono<String> buscarProdutos(BlingAccount account) {
        logger.info("Buscando produtos no Bling para conta: {}", account.getId());

        if (!account.isTokenValid()) {
            account = atualizarToken(account);
        }

        return webClient.get()
                .uri("/produtos")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + account.getAccessToken())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Erro ao buscar produtos: {}", error.getMessage()));
    }

    /**
     * Lista contas Bling do usuário
     */
    public List<BlingAccount> listarContasUsuario(Usuario usuario) {
        return blingRespository.findByUsuarioAndIsActiveTrue(usuario);
    }

    /**
     * Remove conta Bling
     */
    public void removerConta(Usuario usuario, Long accountId) {
        Optional<BlingAccount> accountOpt = blingRespository.findByIdAndUsuario(accountId, usuario);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("Conta não encontrada");
        }

        BlingAccount account = accountOpt.get();
        account.setIsActive(false);
        blingRespository.save(account);

        logger.info("Conta Bling removida: {}", accountId);
    }
}