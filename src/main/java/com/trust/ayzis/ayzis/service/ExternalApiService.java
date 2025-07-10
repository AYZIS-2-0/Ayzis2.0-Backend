package com.trust.ayzis.ayzis.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

import reactor.core.publisher.Mono;

@Service
public class ExternalApiService {

    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private WebClient webClient;

    @Autowired
    private WebClient localWebClient;

    /**
     * Busca produto em API externa (ex: Mercado Livre)
     */
    public Optional<Produto> buscarProdutoExterno(String produtoId) {
        logger.info("Buscando produto externo com ID: {}", produtoId);
        
        try {
            Produto produto = webClient.get()
                    .uri("/items/{id}", produtoId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, 
                             response -> {
                                 logger.warn("Produto não encontrado na API externa: {}", produtoId);
                                 return Mono.error(new RuntimeException("Produto não encontrado: " + produtoId));
                             })
                    .onStatus(HttpStatusCode::is5xxServerError,
                             response -> {
                                 logger.error("Erro no servidor da API externa para produto: {}", produtoId);
                                 return Mono.error(new RuntimeException("Erro no servidor externo"));
                             })
                    .bodyToMono(Produto.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
                    
            logger.info("Produto encontrado na API externa: {}", produto.getId());
            return Optional.ofNullable(produto);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar produto externo {}: {}", produtoId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Sincroniza produto com API externa
     */
    public boolean sincronizarProduto(Produto produto) {
        logger.info("Sincronizando produto com API externa: {}", produto.getId());
        
        try {
            String response = webClient.post()
                    .uri("/produtos/sincronizar")
                    .bodyValue(produto)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                             clientResponse -> {
                                 logger.error("Erro ao sincronizar produto: {}", produto.getId());
                                 return Mono.error(new RuntimeException("Erro na sincronização"));
                             })
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();
                    
            logger.info("Produto sincronizado com sucesso: {}", produto.getId());
            return true;
            
        } catch (Exception e) {
            logger.error("Erro ao sincronizar produto {}: {}", produto.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza preço do produto em API externa
     */
    public boolean atualizarPrecoExterno(String produtoId, Double novoPreco) {
        logger.info("Atualizando preço do produto {} para {}", produtoId, novoPreco);
        
        try {
            String response = webClient.put()
                    .uri("/items/{id}/price", produtoId)
                    .bodyValue("{\"price\": " + novoPreco + "}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
                    
            logger.info("Preço atualizado com sucesso para produto: {}", produtoId);
            return true;
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar preço do produto {}: {}", produtoId, e.getMessage());
            return false;
        }
    }

    /**
     * Busca produtos similares em API externa
     */
    public List<Produto> buscarProdutosSimilares(String categoria, String termo) {
        logger.info("Buscando produtos similares: categoria={}, termo={}", categoria, termo);
        
        try {
            List<Produto> produtos = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/sites/MLB/search")
                            .queryParam("category", categoria)
                            .queryParam("q", termo)
                            .queryParam("limit", 10)
                            .build())
                    .retrieve()
                    .bodyToFlux(Produto.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(15))
                    .block();
                    
            logger.info("Encontrados {} produtos similares", produtos != null ? produtos.size() : 0);
            return produtos != null ? produtos : List.of();
            
        } catch (Exception e) {
            logger.error("Erro ao buscar produtos similares: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Envia venda para sistema externo de analytics
     */
    public boolean enviarVendaParaAnalytics(Venda venda) {
        logger.info("Enviando venda para analytics: {}", venda.getId());
        
        try {
            String response = webClient.post()
                    .uri("/analytics/vendas")
                    .bodyValue(venda)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
                    
            logger.info("Venda enviada para analytics com sucesso: {}", venda.getId());
            return true;
            
        } catch (Exception e) {
            logger.error("Erro ao enviar venda para analytics {}: {}", venda.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Busca dados internos usando WebClient local
     */
    public Optional<Produto> buscarProdutoLocal(String produtoId) {
        logger.info("Buscando produto local via WebClient: {}", produtoId);
        
        try {
            Produto produto = localWebClient.get()
                    .uri("/produtos?id={id}", produtoId)
                    .retrieve()
                    .bodyToMono(Produto.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
                    
            return Optional.ofNullable(produto);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar produto local {}: {}", produtoId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Verificação de saúde de API externa
     */
    public boolean verificarSaudeApiExterna() {
        logger.info("Verificando saúde da API externa");
        
        try {
            String response = webClient.get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
                    
            logger.info("API externa está funcionando");
            return true;
            
        } catch (Exception e) {
            logger.warn("API externa não está respondendo: {}", e.getMessage());
            return false;
        }
    }
}
