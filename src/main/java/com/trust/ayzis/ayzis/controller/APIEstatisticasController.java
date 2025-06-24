package com.trust.ayzis.ayzis.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.service.IEstatiscasVendasService;
import com.trust.ayzis.ayzis.service.IProdutoService;
import com.trust.ayzis.ayzis.service.IVendaService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/estatisticas")
public class APIEstatisticasController {
    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IEstatiscasVendasService statsService;

    @Autowired
    IProdutoService produtoService;

    @Autowired
    IVendaService vendaService;

    @CrossOrigin
    @GetMapping("/soma-qtd")
    @Transactional
    public ResponseEntity<Object> getSomaQuantidadeVendas() {
        logger.info("Buscando soma da quantidade de vendas");
        try {
            Object somaQuantidade = statsService.somaVendasQTDPorMes();
            return ResponseEntity.ok(somaQuantidade);
        } catch (Exception e) {
            logger.error("Erro ao buscar soma da quantidade de vendas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar soma da quantidade de vendas");
        }
    }

    @CrossOrigin
    @GetMapping("/soma-valores")
    @Transactional
    public ResponseEntity<Object> getSomaValoresVendas() {
        logger.info("Buscando soma dos valores das vendas");
        try {
            Object somaValores = statsService.somaVendasValorPorMes();
            return ResponseEntity.ok(somaValores);
        } catch (Exception e) {
            logger.error("Erro ao buscar soma dos valores das vendas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar soma dos valores das vendas");
        }
    }

    @CrossOrigin
    @GetMapping("/soma-qtd-produtos")
    @Transactional
    public ResponseEntity<Object> getSomaProdutosVendidos() {
        logger.info("Buscando soma dos produtos vendidos");
        try {
            Object somaProdutos = statsService.somaVendasQTDPorMesProduto();
            return ResponseEntity.ok(somaProdutos);
        } catch (Exception e) {
            logger.error("Erro ao buscar soma dos produtos vendidos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar soma dos produtos vendidos");
        }
    }

    @CrossOrigin
    @GetMapping(value = "soma-qtd-produtos", params = "produtoId")
    @Transactional
    public ResponseEntity<Object> getSomaProdutosVendidosPorMes(@RequestParam String produtoId) {
        logger.info("Buscando soma dos produtos vendidos por mês");

        Produto produto = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + produtoId));
        try {
            Object somaProdutos = statsService.somaVendasQTDPorMesProduto(produto);
            return ResponseEntity.ok(somaProdutos);
        } catch (Exception e) {
            logger.error("Erro ao buscar soma dos produtos vendidos por mês: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar soma dos produtos vendidos por mês");
        }
    }

    @CrossOrigin
    @GetMapping("/soma-valores-produtos")
    @Transactional
    public ResponseEntity<Object> getSomaVendas() {
        logger.info("Buscando soma das vendas");
        try {
            Object somaVendas = statsService.somaVendasValorPorMesProduto();
            return ResponseEntity.ok(somaVendas);
        } catch (Exception e) {
            logger.error("Erro ao buscar soma das vendas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar soma das vendas");
        }
    }
}
