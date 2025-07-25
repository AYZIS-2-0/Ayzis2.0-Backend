package com.trust.ayzis.ayzis.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.trust.ayzis.ayzis.exception.ExceptionLogger;
import com.trust.ayzis.ayzis.model.Componentes;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Resposta;
import com.trust.ayzis.ayzis.repository.IProdutoRepository;
import com.trust.ayzis.ayzis.service.IComponentesService;
import com.trust.ayzis.ayzis.service.IProdutoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1")
public class APIProdutoController {

    private final APIComponentesController APIComponentesController;
    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IProdutoService produtoServico;

    @Autowired
    IProdutoRepository produtoRepository;

    @Autowired
    IComponentesService componentesService;

    APIProdutoController(APIComponentesController APIComponentesController) {
        this.APIComponentesController = APIComponentesController;
    }

    @CrossOrigin
    @GetMapping("/produtos")
    @Transactional
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Object> buscarTodos(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Buscando todos os produtos");

        Pageable pageable = PageRequest.of(page, limit);
        List<Produto> produtos = produtoServico.buscarTodosProdutos(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
    }

    @CrossOrigin
    @GetMapping("/produtos/all")
    @Transactional
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Object> buscarTodos() {
        logger.info("Buscando todos os produtos");

        List<Produto> produtos = produtoServico.buscarTodosProdutos();
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
    }

    @CrossOrigin
    @GetMapping(value = "produtos", params = "id")
    @Transactional
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Object> buscarPorId(@RequestParam("id") String id) {
        logger.info("Buscando produto por id" + id);

        return ResponseEntity.status(HttpStatus.OK).body(produtoServico.buscarPorId(id));
    }

    @CrossOrigin
    @GetMapping(value = "produtos", params = "nome")
    @Transactional
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Object> buscarPorNome(@RequestParam String nome) {
        logger.info("Buscando produto por nome" + nome);

        return ResponseEntity.status(HttpStatus.OK).body(produtoServico.buscarPorNome(nome));
    }

    @GetMapping("/produtosComposicao")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public List<Produto> getProdutosComposicao(@RequestParam Produto produto) {
        return produtoServico.buscarPorProdutosCompostos(produto);
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

    @CrossOrigin
    @PostMapping("/produtos/mass")
    @Transactional
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Object> salvarProdutosInMass(@RequestBody List<Produto> produtos) {
        logger.info(">>> Salvando pack de produtos");

        try {
            List<Produto> produtosSalvos = new ArrayList<>();
            for (Produto produto : produtos) {
                if (produtoRepository.existsById(produto.getId())) {
                    logger.warn("Produto já existe com o id: " + produto.getId());
                    continue; // Skip this product
                }
                produtosSalvos.add(produto);
            }
            if (!produtosSalvos.isEmpty()) {
                produtoServico.salvarProdutosInMass(produtosSalvos);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(produtosSalvos);
        } catch (ExceptionLogger ex) {
            logger.error("Erro: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            logger.error("Erro de integridade de dados: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro de integridade de dados: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Erro interno: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getMessage());
        }
    }

    @CrossOrigin
    @PostMapping("/produtos")
    @Transactional
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Object> salvarProduto(@RequestBody Produto produto) {
        logger.info(">>> Salvando produto: " + produto.getId());

        try {
            if (produtoRepository.existsById(produto.getId())) {
                throw new ExceptionLogger("Produto já existe com o id: " + produto.getId());
            }

            Optional<Produto> produtoSalvo = produtoServico.salvarProduto(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
        } catch (ExceptionLogger ex) {
            logger.error("Erro: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            logger.error("Erro de integridade de dados: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro de integridade de dados: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Erro interno: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getMessage());
        }
    }

    @CrossOrigin
    @PatchMapping("/produtos")
    @Transactional
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Object> atualizar(@RequestBody Produto produto) {
        logger.info("Atualizando produto" + produto.getId());

        return ResponseEntity.status(HttpStatus.OK).body(produtoServico.atualizarProduto(produto));
    }

    @CrossOrigin
    @DeleteMapping("/produtos/deletar")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deletarPorId(@RequestParam("id") String id, HttpServletRequest req) {
        logger.info("Deletando produto por id" + id);

        Optional<Produto> optionalProduto = produtoServico.buscarPorId(id);
        if (!optionalProduto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        Produto produto = optionalProduto.get();

        // Verificar se o produto é um produto componente em algum componente
        List<Componentes> componentes = componentesService.buscarPorProdutoComponente(produto);
        for (Componentes componente : componentes) {
            componentesService.deletarComponentePorId(componente.getId());
        }

        // Deletar o produto
        produtoServico.deletarProdutoPorId(id);

        Resposta resposta = new Resposta();
        resposta.setMensagem("Produto deletado com sucesso");
        resposta.setStatus(HttpStatus.OK);
        resposta.setCaminho(req.getRequestURI());
        resposta.setMetodo(req.getMethod());

        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }
}