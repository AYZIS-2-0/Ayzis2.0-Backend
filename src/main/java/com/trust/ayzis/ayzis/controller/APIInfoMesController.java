package com.trust.ayzis.ayzis.controller;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trust.ayzis.ayzis.model.InfoMes;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.service.IInfoMesService;

@RestController
@RequestMapping("/api/v1")
public class APIInfoMesController {
    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IInfoMesService infoMesService;

    @CrossOrigin
    @GetMapping("/infoMes/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando infoMes por id: " + id);

        Optional<InfoMes> infoMes = infoMesService.buscarPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(infoMes);
    }

    @CrossOrigin
    @GetMapping("/infoMes")
    public ResponseEntity<Object> buscarTodos() {
        logger.info("Buscando todos os infoMes");

        List<InfoMes> infoMes = infoMesService.buscarTodos();
        return ResponseEntity.status(HttpStatus.OK).body(infoMes);
    }

    @CrossOrigin
    @GetMapping(value = "/infoMes", params = "produto")
    public ResponseEntity<Object> buscarPorProduto(@RequestParam Produto produto) {
        logger.info("Buscando infoMes por produto: " + produto);

        List<InfoMes> infoMes = infoMesService.buscarPorProduto(produto);
        return ResponseEntity.status(HttpStatus.OK).body(infoMes);
    }

    @CrossOrigin
    @GetMapping(value = "/infoMes", params = "mesAno")
    public ResponseEntity<Object> buscarPorMesAno(@RequestParam String mesAno) {
        logger.info("Buscando infoMes por mesAno: " + mesAno);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date mesAnoDate;
        try {
            mesAnoDate = new java.sql.Date(dateFormat.parse(mesAno).getTime());
        } catch (ParseException e) {
            logger.error("Erro ao converter mesAno para Date: " + mesAno, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de data inválido. Use 'yyyy-MM'.");
        }

        List<InfoMes> infoMes = infoMesService.buscarPorMesAno(mesAnoDate);
        return ResponseEntity.status(HttpStatus.OK).body(infoMes);
    }

    @CrossOrigin
    @GetMapping(value = "/infoMes", params = { "inicio", "fim" })
    public ResponseEntity<Object> buscarPorMesAnoEntre(@RequestParam String inicio, @RequestParam String fim) {
        logger.info("Buscando infoMes por mesAno entre: " + inicio + " e " + fim);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date inicioDate;
        Date fimDate;
        try {
            inicioDate = new java.sql.Date(dateFormat.parse(inicio).getTime());
            fimDate = new java.sql.Date(dateFormat.parse(fim).getTime());
        } catch (ParseException e) {
            logger.error("Erro ao converter mesAno para Date: " + inicio + " e " + fim, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de data inválido. Use 'yyyy-MM'.");
        }

        List<InfoMes> infoMes = infoMesService.buscarPorMesAnoEntre(inicioDate, fimDate);
        return ResponseEntity.status(HttpStatus.OK).body(infoMes);
    }

    @CrossOrigin
    @GetMapping(value = "/infoMes", params = { "produto", "inicio", "fim" })
    public ResponseEntity<Object> buscarPorProdutoMesAnoEntre(@RequestParam Produto produto, @RequestParam String inicio,
            @RequestParam String fim) {
        logger.info("Buscando infoMes por produto e mesAno entre: " + produto + " e " + inicio + " e " + fim);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date inicioDate;
        Date fimDate;
        try {
            inicioDate = new java.sql.Date(dateFormat.parse(inicio).getTime());
            fimDate = new java.sql.Date(dateFormat.parse(fim).getTime());
        } catch (ParseException e) {
            logger.error("Erro ao converter mesAno para Date: " + inicio + " e " + fim, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de data inválido. Use 'yyyy-MM'.");
        }

        List<InfoMes> infoMes = infoMesService.buscarPorProdutoMesAnoEntre(produto, inicioDate, fimDate);
        return ResponseEntity.status(HttpStatus.OK).body(infoMes);
    }

    @CrossOrigin
    @GetMapping("/infoMes/calc")
    public ResponseEntity<Object> calcInfoMes() {
        logger.info("Calculando infoMes");

        infoMesService.calcAllInfoMes();
        return ResponseEntity.status(HttpStatus.OK).body("InfoMes calculado com sucesso.");
    }

    @CrossOrigin
    @GetMapping("/infoMes/{id}/delete")
    public ResponseEntity<Object> deletarPorId(@PathVariable Long id) {
        logger.info("Deletando infoMes por id: " + id);

        infoMesService.deletarPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body("InfoMes deletado com sucesso.");
    }
}
