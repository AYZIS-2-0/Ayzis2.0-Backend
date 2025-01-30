package com.trust.ayzis.ayzis.service;

import java.sql.Date;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trust.ayzis.ayzis.model.IVendaRepository;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

@Service
public class VendaService implements IVendaService {

    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IVendaRepository vendaRepository;

    @Override
    public Optional<Venda> buscarPorId(String id) {
        logger.info("Buscando venda por id: " + id);

        Optional<Venda> venda = vendaRepository.findById(id);
        return venda;
    }

    @Override
    public List<Venda> buscarPorData(Date data) {
        logger.info("Buscando venda por data: " + data);

        List<Venda> vendas = vendaRepository.findByDataVenda(data);
        return vendas;
    }

    @Override
    public List<Venda> buscarVendasPorMes(YearMonth yearMonth) {
        logger.info("Buscando vendas por mês");

        List<Venda> vendas = vendaRepository.findByDataVendaBetween(
                Date.valueOf(yearMonth.atDay(1)),
                Date.valueOf(yearMonth.atEndOfMonth()));

        return vendas;
    }

    @Override
    public List<Venda> buscarPorProduto(Produto produto) {
        logger.info("Buscando venda por produto: " + produto);

        List<Venda> vendas = vendaRepository.findByProduto(produto);
        return vendas;
    }

    @Override
    public List<Venda> buscarPorProdutoMes(Produto produto, YearMonth yearMonth) {
        logger.info("Buscando vendas por produto e mês");

        List<Venda> vendas = vendaRepository.findByProdutoAndDataVendaBetween(
                produto,
                Date.valueOf(yearMonth.atDay(1)),
                Date.valueOf(yearMonth.atEndOfMonth()));

        return vendas;
    }

    @Override
    public List<Venda> buscarPorStatus(String status) {
        logger.info("Buscando venda por status: " + status);

        List<Venda> vendas = vendaRepository.findByStatus(status);
        return vendas;
    }

    @Override
    public List<Venda> buscarTodasVendas() {
        logger.info("Buscando todas as vendas");

        List<Venda> vendas = vendaRepository.findAll();
        return vendas;
    }

    @Override
    public Optional<Venda> salvarVenda(Venda venda) {
        logger.info("Salvando venda: " + venda);

        Optional<Venda> vendaSalva = Optional.of(vendaRepository.save(venda));
        return vendaSalva;
    }

    @Override
    public Optional<Venda> atualizarVenda(Venda newVenda) {
        logger.info("Atualizando venda: " + newVenda);

        return vendaRepository.findById(newVenda.getId()).map(venda -> {
            venda.setDataVenda(newVenda.getDataVenda());
            venda.setStatus(newVenda.getStatus());
            venda.setDescStatus(newVenda.getDescStatus());
            venda.setQuantidade(newVenda.getQuantidade());
            venda.setValorTotal(newVenda.getValorTotal());
            venda.setProduto(newVenda.getProduto());

            return vendaRepository.save(venda);
        });
    }

    @Override
    public void deletarPorId(String id) {
        logger.info("Deletando venda por id: " + id);

        vendaRepository.deleteById(id);
    }
}