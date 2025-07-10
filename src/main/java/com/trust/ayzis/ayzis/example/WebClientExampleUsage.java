package com.trust.ayzis.ayzis.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Exemplo de como usar os WebClients configurados
 * Este arquivo é apenas para demonstração - NÃO É PARA USO EM PRODUÇÃO
 */
@Component
public class WebClientExampleUsage {

    @Autowired
    private WebClient webClient; // WebClient padrão com base URL

    @Autowired
    @Qualifier("webClientGeneric")
    private WebClient webClientGeneric; // WebClient genérico sem base URL

    @Autowired
    @Qualifier("webClientWithAuth")
    private WebClient webClientWithAuth; // WebClient com autenticação

    @Autowired
    @Qualifier("localWebClient")
    private WebClient localWebClient; // WebClient para APIs locais

    /**
     * Exemplo de requisição GET usando o WebClient padrão
     */
    public String exemploGetRequest() {
        return webClient.get()
                .uri("/endpoint")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // block() converte para síncrono
    }

    /**
     * Exemplo de requisição GET assíncrona
     */
    public Mono<String> exemploGetRequestAsync() {
        return webClient.get()
                .uri("/endpoint")
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Exemplo de requisição POST
     */
    public String exemploPostRequest(Object requestBody) {
        return webClient.post()
                .uri("/endpoint")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Exemplo usando WebClient genérico com URL completa
     */
    public String exemploWebClientGenerico() {
        return webClientGeneric.get()
                .uri("https://api.exemplo.com/dados")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Exemplo usando WebClient com autenticação
     */
    public String exemploWebClientComAuth() {
        return webClientWithAuth.get()
                .uri("/dados-protegidos")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Exemplo de comunicação com API local
     */
    public String exemploApiLocal() {
        return localWebClient.get()
                .uri("/produtos")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Exemplo com tratamento de erro
     */
    public String exemploComTratamentoErro() {
        return webClient.get()
                .uri("/endpoint")
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError(),
                    response -> Mono.error(new RuntimeException("Erro 4xx"))
                )
                .onStatus(
                    status -> status.is5xxServerError(),
                    response -> Mono.error(new RuntimeException("Erro 5xx"))
                )
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println("Erro na requisição: " + error.getMessage()))
                .onErrorReturn("Valor padrão em caso de erro")
                .block();
    }
}
