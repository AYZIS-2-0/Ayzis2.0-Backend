package com.trust.ayzis.ayzis.controller;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Resposta;
import com.trust.ayzis.ayzis.model.Venda;
import com.trust.ayzis.ayzis.service.IVendaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1")
public class APIVendaController {
    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IVendaService vendaServico;

    @CrossOrigin
    @GetMapping("/vendas")
    @Transactional
    public ResponseEntity<Object> buscarTodos() {
        logger.info("Buscando todas as vendas");

        return ResponseEntity.status(HttpStatus.OK).body(vendaServico.buscarTodasVendas());
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "id")
    @Transactional
    public ResponseEntity<Object> buscarPorId(@RequestParam String id) {
        logger.info("Buscando venda por id: " + id);

        return ResponseEntity.status(HttpStatus.OK).body(vendaServico.buscarPorId(id));
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "data")
    @Transactional
    public ResponseEntity<Object> buscarPorData(@RequestParam String data) {
        logger.info("Buscando venda por data: " + data);

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date utilDate = dateFormat.parse(data);
            Date date = new Date(utilDate.getTime());
            return ResponseEntity.status(HttpStatus.OK).body(vendaServico.buscarPorData(date));
        } catch (ParseException e) {
            logger.error("Erro ao converter data: " + data, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data inválida");
        }
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "sku")
    @Transactional
    public ResponseEntity<Object> buscarPorProduto(@RequestParam String produto) {
        logger.info("Buscando venda por produto: " + produto);

        Produto produtoObj = new Produto();
        produtoObj.setNome(produto);
        return ResponseEntity.status(HttpStatus.OK).body(vendaServico.buscarPorProduto(produtoObj));
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "status")
    @Transactional
    public ResponseEntity<Object> buscarPorStatus(@RequestParam String status) {
        logger.info("Buscando venda por status: " + status);

        return ResponseEntity.status(HttpStatus.OK).body(vendaServico.buscarPorStatus(status));
    }

    @CrossOrigin
    @PostMapping("/vendas")
    @Transactional
    public ResponseEntity<Object> salvarVenda(@RequestBody Venda venda) {
        logger.info("Salvando venda: " + venda);

        Optional<Venda> vendaSalva = vendaServico.salvarVenda(venda);
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaSalva);
    }

    @CrossOrigin
    @PatchMapping("/vendas")
    @Transactional
    public ResponseEntity<Object> atualizar(@RequestBody Venda venda) {
        logger.info("Atualizando venda: " + venda);

        return ResponseEntity.status(HttpStatus.OK).body(vendaServico.atualizarVenda(venda));
    }

    @CrossOrigin
    @DeleteMapping(value = "vendas", params = "id")
    @Transactional
    public ResponseEntity<Object> deletarPorId(@RequestParam String id, HttpServletRequest req) {
        logger.info("Deletando venda por id: " + id);

        vendaServico.deletarPorId(id);

        Resposta resposta = new Resposta();
        resposta.setMensagem("Venda deletada com sucesso");
        resposta.setCaminho(req.getRequestURI());
        resposta.setMetodo(req.getMethod());
        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }

}
