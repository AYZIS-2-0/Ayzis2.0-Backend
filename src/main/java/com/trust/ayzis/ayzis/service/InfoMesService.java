package com.trust.ayzis.ayzis.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trust.ayzis.ayzis.model.IInfoMesRepository;
import com.trust.ayzis.ayzis.model.InfoMes;
import com.trust.ayzis.ayzis.model.Produto;

@Service
public class InfoMesService implements IInfoMesService {

    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IInfoMesRepository infoMesRepository;

    @Override
    public Optional<InfoMes> buscarPorId(int id) {
        logger.info("Buscando infoMes por id" + id);

        Optional<InfoMes> infoMes = infoMesRepository.findById(id);

        return infoMes;
    }

    @Override
    public List<InfoMes> buscarPorProduto(Produto produto) {
        logger.info("Buscando infoMes por produto: " + produto.getId());

        List<InfoMes> infoMes = infoMesRepository.findByProduto(produto);

        return infoMes;
    }

    @Override
    public List<InfoMes> buscarPorMesAno(Date mesAno) {
        logger.info("Buscando infoMes por mesAno: " + mesAno);

        List<InfoMes> infoMes = infoMesRepository.findByMonthYear(mesAno);

        return infoMes;
    }

    @Override
    public List<InfoMes> buscarPorMesAnoEntre(Date inicio, Date fim) {
        logger.info("Buscando infoMes por mesAno entre: " + inicio + " e " + fim);

        List<InfoMes> infoMes = infoMesRepository.findByMonthYearBetween(inicio, fim);

        return infoMes;
    }

    @Override
    public List<InfoMes> buscarPorProdutoMesAnoEntre(Produto produto, Date inicio, Date fim) {
        logger.info("Buscando infoMes por produto e mesAno entre: " + produto.getId() + " e " + inicio + " e " + fim);

        List<InfoMes> infoMes = infoMesRepository.findByProdutoAndMonthYearBetween(produto, inicio, fim);

        return infoMes;
    }

    @Override
    public void calcInfoMes(InfoMes infoMes) {
        logger.info("Calculando infoMes: " + infoMes.getMonthYear() + " - " + infoMes.getProduto().getId());
        
        
    }

    @Override
    public void deletarPorId(int id) {
        logger.info("Deletando infoMes por id: " + id);

        infoMesRepository.deleteByID(id);
    }
    
}
