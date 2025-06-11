package com.trust.ayzis.ayzis.service;

import java.sql.Date;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.trust.ayzis.ayzis.model.InfoMes;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

public interface IVendaService {
    public Optional<Venda> buscarPorId(String id);

    public List<Venda> buscarPorData(Date data);

    public List<Venda> buscarVendasPorMes(YearMonth yearMonth);

    public List<Venda> buscarPorPeriodo(Date dataInicio, Date dataFim);

    public List<Venda> buscarPorProduto(Produto produto);

    public List<Venda> buscarPorProdutoMes(Produto produto, YearMonth yearMonth);

    public List<Venda> buscarPorStatus(String status);

    public List<Venda> buscarTodasVendas(Pageable pageable);

    public List<Venda> buscarVendasPorInfoMes(InfoMes InfoMes);

    public List<Venda> buscarTodasVendas();

    public List<Venda> salvarVendasInMass(List<Venda> vendas);

    public Optional<Venda> salvarVenda(Venda venda);

    public Optional<Venda> atualizarVenda(Venda venda);

    public void deletarPorId(String id);
}
