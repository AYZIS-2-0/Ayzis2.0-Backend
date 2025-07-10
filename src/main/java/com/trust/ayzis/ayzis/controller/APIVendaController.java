package com.trust.ayzis.ayzis.controller;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trust.ayzis.ayzis.exception.ExceptionLogger;
import com.trust.ayzis.ayzis.model.IVendaRepository;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Resposta;
import com.trust.ayzis.ayzis.model.Venda;
import com.trust.ayzis.ayzis.service.IProdutoService;
import com.trust.ayzis.ayzis.service.IVendaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1")
public class APIVendaController {
    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IVendaService vendaService;

    @Autowired
    IProdutoService produtoService;

    @Autowired
    IVendaRepository vendaRepository;

    @CrossOrigin
    @GetMapping("/vendas")
    @Transactional
    public ResponseEntity<Object> buscarTodos(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Buscando todas as vendas");

        Pageable pageable = PageRequest.of(page, limit);

        List<Venda> vendas = vendaService.buscarTodasVendas(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(vendas);
    }

    @CrossOrigin
    @GetMapping("/vendas/all")
    @Transactional
    public ResponseEntity<Object> buscarTodos() {
        logger.info("Buscando todas as vendas");

        List<Venda> vendas = vendaService.buscarTodasVendas();

        return ResponseEntity.status(HttpStatus.OK).body(vendas);
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "id")
    @Transactional
    public ResponseEntity<Object> buscarPorId(@RequestParam String id) {
        logger.info("Buscando venda por id: " + id);

        return ResponseEntity.status(HttpStatus.OK).body(vendaService.buscarPorId(id));
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "data")
    @Transactional
    public ResponseEntity<Object> buscarPorData(@RequestParam String data) {
        logger.info("Buscando venda por data: " + data);

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = dateFormat.parse(data);
            Date date = new Date(utilDate.getTime());
            return ResponseEntity.status(HttpStatus.OK).body(vendaService.buscarPorData(date));
        } catch (ParseException e) {
            logger.error("Erro ao converter data: " + data, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data inválida");
        }
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "mes")
    @Transactional
    public ResponseEntity<Object> buscarPorMes(@RequestParam String mes) {
        logger.info("Buscando venda por mês: " + mes);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(mes, formatter);

        List<Venda> vendas = vendaService.buscarVendasPorMes(yearMonth);

        return ResponseEntity.status(HttpStatus.OK).body(vendas);
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "sku")
    public ResponseEntity<Object> buscarVendasPorProduto(@RequestParam("sku") String produtoId) {
        logger.info("Buscando vendas por produto id: " + produtoId);

        Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
        if (produtoOpt.isPresent()) {
            List<Venda> vendas = vendaService.buscarPorProduto(produtoOpt.get());
            return ResponseEntity.status(HttpStatus.OK).body(vendas);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado com o id: " + produtoId);
        }
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = { "sku", "mes" })
    @Transactional
    public ResponseEntity<Object> buscarVendasPorProdutoMes(@RequestParam("sku") String produtoId,
            @RequestParam("mes") String mes) {
        logger.info("Buscando vendas por produto id: " + produtoId + " e mês: " + mes);

        Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
        if (produtoOpt.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(mes, formatter);

            List<Venda> vendas = vendaService.buscarPorProdutoMes(produtoOpt.get(), yearMonth);
            return ResponseEntity.status(HttpStatus.OK).body(vendas);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado com o id: " + produtoId);
        }
    }

    @CrossOrigin
    @GetMapping(value = "vendas", params = "status")
    @Transactional
    public ResponseEntity<Object> buscarPorStatus(@RequestParam String status) {
        logger.info("Buscando venda por status: " + status);

        return ResponseEntity.status(HttpStatus.OK).body(vendaService.buscarPorStatus(status));
    }

    @CrossOrigin
    @PostMapping("/vendas/mass")
    @Transactional
    public ResponseEntity<Object> salvarVendasInMass(@RequestBody List<Venda> vendas) {
        logger.info(">>> Salvando pack de vendas");

        class VendaErro {
            private Venda venda;
            private String mensagem;

            public VendaErro(Venda venda, String mensagem) {
                this.venda = venda;
                this.mensagem = mensagem;
            }

            public Venda getVenda() {
                return venda;
            }

            public String getMensagem() {
                return mensagem;
            }
        }

        try {
            List<Venda> vendasSalvas = new ArrayList<>();
            List<VendaErro> vendasErro = new ArrayList<>();
            for (Venda venda : vendas) {
                if (venda.getProduto() == null || venda.getProduto().getId() == null
                        || venda.getProduto().getId().isEmpty()) {
                    logger.warn("Produto não pode ser nulo ou vazio");
                    vendasErro.add(new VendaErro(venda, "Produto não pode ser nulo ou vazio"));
                    continue;
                }
                if (venda.getProduto().getId() != null) {
                    Optional<Produto> produtoOpt = produtoService.buscarPorId(venda.getProduto().getId());
                    if (!produtoOpt.isPresent()) {
                        logger.warn("Produto não encontrado com o id: " + venda.getProduto().getId());
                        vendasErro.add(
                                new VendaErro(venda, "Produto não encontrado com o id: " + venda.getProduto().getId()));
                        continue;
                    }
                    venda.setProduto(produtoOpt.get());
                }
                vendasSalvas.add(venda);
            }
            vendaService.salvarVendasInMass(vendasSalvas);
            return ResponseEntity.status(HttpStatus.CREATED).body(vendasErro.isEmpty() ? vendasSalvas : vendasErro);
        } catch (Exception e) {
            logger.error("Erro ao salvar vendas em massa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar vendas em massa");
        }
    }

    @CrossOrigin
    @PostMapping("/vendas")
    @Transactional
    public ResponseEntity<Object> salvarVenda(@RequestBody Venda venda) {
        logger.info(">>> Salvando venda: " + venda.getId());

        // Verifica se o ID da venda está presente
        if (venda.getId() == null || venda.getId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID da venda não pode ser nulo ou vazio");
        }

        // Busca o produto pelo ID
        Optional<Produto> produtoOpt = produtoService.buscarPorId(venda.getProduto().getId());
        if (!produtoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Produto não encontrado com o id: " + venda.getProduto().getId());
        }

        // Vincula o produto existente à venda
        venda.setProduto(produtoOpt.get());

        // Salva a venda
        Optional<Venda> vendaSalva = vendaService.salvarVenda(venda);
        logger.info("Venda salva com sucesso: " + vendaSalva.get().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(vendaSalva);
    }

    @CrossOrigin
    @PatchMapping("/vendas")
    @Transactional
    public ResponseEntity<Object> atualizar(@RequestBody Venda venda) {
        logger.info("Atualizando venda" + venda.getId());

        return ResponseEntity.status(HttpStatus.OK).body(vendaService.atualizarVenda(venda));
    }

    @CrossOrigin
    @DeleteMapping(value = "vendas", params = "id")
    @Transactional
    public ResponseEntity<Object> deletarPorId(@RequestParam String id, HttpServletRequest req) {

        Optional<Venda> vendaOpt = vendaService.buscarPorId(id);
        if (!vendaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Venda não encontrada com o id: " + id);
        }

        logger.info("Deletando venda por id" + id);

        vendaService.deletarPorId(id);

        Resposta resposta = new Resposta();
        resposta.setMensagem("Venda deletada com sucesso");
        resposta.setCaminho(req.getRequestURI());
        resposta.setMetodo(req.getMethod());
        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }

    @ExceptionHandler(ExceptionLogger.class)
    public ResponseEntity<String> handleProdutoNotFoundException(ExceptionLogger ex) {
        logger.error("Erro: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
