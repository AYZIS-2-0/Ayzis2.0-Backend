package com.trust.ayzis.ayzis.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trust.ayzis.ayzis.exception.ProdutoNotFoundException;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Resposta;
import com.trust.ayzis.ayzis.service.IProdutoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1")
public class APIProdutoController {
    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IProdutoService produtoServico;

    @CrossOrigin
    @GetMapping("/produtos")
    public ResponseEntity<Object> buscarTodos() {
        logger.info("Buscando todos os produtos");

        return ResponseEntity.status(HttpStatus.OK).body(produtoServico.buscarTodosProdutos());
    }

    @CrossOrigin
    @GetMapping(value = "produtos", params = "id")
    @Transactional
    public ResponseEntity<Object> buscarPorId(@RequestParam("id") String id) {
        logger.info("Buscando produto por id: " + id);

        return ResponseEntity.status(HttpStatus.OK).body(produtoServico.buscarPorId(id));
    }

    @CrossOrigin
    @GetMapping(value = "produtos", params = "nome")
    @Transactional
    public ResponseEntity<Object> buscarPorNome(@RequestParam String nome) {
        logger.info("Buscando produto por nome: " + nome);

        return ResponseEntity.status(HttpStatus.OK).body(produtoServico.buscarPorNome(nome));
    }

    @GetMapping("/produtosComposicao")
    public List<Produto> getProdutosComposicao(@RequestParam Produto produto) {
        return produtoServico.buscarPorProdutosCompostos(produto);
    }

    @ExceptionHandler(ProdutoNotFoundException.class)
    public ResponseEntity<String> handleProdutoNotFoundException(ProdutoNotFoundException ex) {
        logger.error("Erro: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @CrossOrigin
    @PostMapping("/produtos")
    @Transactional
    public ResponseEntity<Object> salvarProduto(@RequestBody Produto produto) {
        logger.info("Salvando produto: " + produto);

        Optional<Produto> produtoSalvo = produtoServico.salvarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    @CrossOrigin
    @PatchMapping("/produtos")
    @Transactional
    public ResponseEntity<Object> atualizar(@RequestBody Produto produto) {
        logger.info("Atualizando produto: " + produto);

        return ResponseEntity.status(HttpStatus.OK).body(produtoServico.atualizarProduto(produto));
    }

    @CrossOrigin
    @DeleteMapping("/produtos/deletar")
    @Transactional
    public ResponseEntity<Object> deletarPorId(@RequestParam("id") String id, HttpServletRequest req) {
        logger.info("Deletando produto por id: " + id);

        produtoServico.deletarProdutoPorId(id);

        Resposta resposta = new Resposta();
        resposta.setMensagem("Produto deletado com sucesso");
        resposta.setStatus(HttpStatus.OK);
        resposta.setCaminho(req.getRequestURI());
        resposta.setMetodo(req.getMethod());

        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }
}
