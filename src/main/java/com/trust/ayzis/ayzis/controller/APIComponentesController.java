package com.trust.ayzis.ayzis.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trust.ayzis.ayzis.exception.ExceptionLogger;
import com.trust.ayzis.ayzis.model.Componentes;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Resposta;
import com.trust.ayzis.ayzis.service.IComponentesService;
import com.trust.ayzis.ayzis.service.IProdutoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1")
public class APIComponentesController {
    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IComponentesService componentesService;

    @Autowired
    IProdutoService produtoService;

    @CrossOrigin
    @GetMapping(value = "componentes", params = "id")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> buscarPorId(@RequestParam("id") Long id) {
        logger.info("Buscando componente por id" + id);

        return ResponseEntity.status(HttpStatus.OK).body(componentesService.buscarPorId(id));
    }

    @CrossOrigin
    @GetMapping(value = "componentes", params = "produtoComposto")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> buscarPorProdutoComposto(@RequestParam("produtoComposto") String produtoComposto) {
        logger.info("Buscando componentes por produto composto" + produtoComposto);

        Optional<Produto> optionalProduto = produtoService.buscarPorId(produtoComposto);
        if (!optionalProduto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        Produto produto = optionalProduto.get();

        return ResponseEntity.status(HttpStatus.OK).body(componentesService.buscarPorProdutoComposto(produto));
    }

    @CrossOrigin
    @GetMapping(value = "componentes", params = "produtoComponente")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> buscarPorProdutoComponente(
            @RequestParam("produtoComponente") String produtoComponente) {
        logger.info("Buscando componentes por produto componente" + produtoComponente);

        Optional<Produto> optionalProduto = produtoService.buscarPorId(produtoComponente);
        if (!optionalProduto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        Produto produto = optionalProduto.get();

        return ResponseEntity.status(HttpStatus.OK).body(componentesService.buscarPorProdutoComponente(produto));
    }

    @CrossOrigin
    @PostMapping(value = "componentes", params = "produtoComposto")
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Object> salvarComponente(@RequestParam("produtoComposto") String produtoComposto,
            @RequestParam("produtoComponente") String produtoComponente,
            @RequestParam("quantidade") Integer quantidade) {
        logger.info("Salvando componente: " + produtoComposto +':'+ produtoComponente + ':' + quantidade);

        Optional<Produto> optionalProdutoComposto = produtoService.buscarPorId(produtoComposto);
        if (!optionalProdutoComposto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto composto não encontrado");
        }
        Produto produtoCompostoEntity = optionalProdutoComposto.get();

        Optional<Produto> optionalProdutoComponente = produtoService.buscarPorId(produtoComponente);
        if (!optionalProdutoComponente.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto componente não encontrado");
        }
        Produto produtoComponenteEntity = optionalProdutoComponente.get();

        // Verificar se o componente já existe
        List<Componentes> existingComponentes = componentesService.buscarPorProdutoComponente(produtoComponenteEntity);
        if (!existingComponentes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Componente já existe");
        }

        Componentes componentes = new Componentes(produtoCompostoEntity, produtoComponenteEntity, quantidade);

        return ResponseEntity.status(HttpStatus.OK)
                .body(componentesService.salvarComponente(componentes));
    }

    @CrossOrigin
    @PatchMapping(value = "componentes", params = "id")
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Object> atualizarComponente(@RequestParam("id") Long id,
            @RequestParam("produtoComposto") String produtoComposto,
            @RequestParam("produtoComponente") String produtoComponente,
            @RequestParam("quantidade") Integer quantidade) {
        logger.info("Atualizando componente" + id);

        Optional<Componentes> optionalComponente = componentesService.buscarPorId(id);
        if (!optionalComponente.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Componente não encontrado" + id);
        }
        Componentes componente = optionalComponente.get();

        Optional<Produto> optionalProdutoComposto = produtoService.buscarPorId(produtoComposto);
        if (!optionalProdutoComposto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto composto não encontrado" + produtoComposto);
        }
        Produto produtoCompostoEntity = optionalProdutoComposto.get();

        Optional<Produto> optionalProdutoComponente = produtoService.buscarPorId(produtoComponente);
        if (!optionalProdutoComponente.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto componente não encontrado" + produtoComponente);
        }
        Produto produtoComponenteEntity = optionalProdutoComponente.get();

        componente.setProdutoComposto(produtoCompostoEntity);
        componente.setProdutoComponente(produtoComponenteEntity);
        componente.setQuantidade(quantidade);

        return ResponseEntity.status(HttpStatus.OK)
                .body(componentesService.atualizarComponente(componente));
    }

    @CrossOrigin
    @DeleteMapping(value = "componentes", params = "id")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deletarComponentePorId(@RequestParam("id") Long id, HttpServletRequest req) {
        logger.info("Deletando componente por id" + id);

        Resposta resposta = new Resposta();
        resposta.setMensagem("Componente deletado com sucesso" + id);
        resposta.setStatus(HttpStatus.OK);
        resposta.setCaminho(req.getRequestURI());
        resposta.setMetodo(req.getMethod());

        componentesService.deletarComponentePorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }

    @ExceptionHandler(ExceptionLogger.class)
    public ResponseEntity<String> handleProdutoNotFoundException(ExceptionLogger ex) {
        logger.error("Erro: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("Erro de integridade de dados: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro de integridade de dados: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        logger.error("Erro interno: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getMessage());
    }
}