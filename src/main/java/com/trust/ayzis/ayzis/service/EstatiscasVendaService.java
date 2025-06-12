package com.trust.ayzis.ayzis.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.trust.ayzis.ayzis.model.IProdutoRepository;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

public class EstatiscasVendaService implements IEstatiscasVendasService {

    Logger logger = LogManager.getLogger(getClass());

    @Autowired
    IProdutoService produtoService;

    @Autowired
    IVendaService vendaService;

    @Override
    public int somaTotal() {
        logger.info("Calculando a soma total de vendas");

        List<Venda> vendas = vendaService.buscarTodasVendas();
        return vendas.stream()
                .mapToInt(v -> v.getQuantidade() != null ? v.getQuantidade() : 0)
                .sum();
    }

    // sobrecarga somaTotal: calcula a soma total de vendas por produto
    public int somaTotal(Produto produto) {
        logger.info("Calculando a soma total de vendas para o produto: " + produto.getId());

        List<Venda> vendas = vendaService.buscarPorProduto(produto);
        return vendas.stream()
                .mapToInt(v -> v.getQuantidade() != null ? v.getQuantidade() : 0)
                .sum();
    }

    // sobrecarga somaTotal: calcula a soma total de vendas por período
    public int somaTotal(Date dataInicio, Date dataFim) {
        logger.info("Calculando a soma total de vendas entre: " + dataInicio + " e " + dataFim);

        List<Venda> vendas = vendaService.buscarPorPeriodo(
                new java.sql.Date(dataInicio.getTime()),
                new java.sql.Date(dataFim.getTime()));
        return vendas.stream()
                .mapToInt(v -> v.getQuantidade() != null ? v.getQuantidade() : 0)
                .sum();
    }

    @Override
    public Map<String, Integer> somaMensal(Produto produto) {
        logger.info("Calculando a soma mensal de vendas para o produto: " + produto.getId());

        List<Venda> vendas = vendaService.buscarPorProduto(produto);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MM/yyyy");

        return vendas.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        v -> v.getDataVenda().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                                .format(formatter),
                        java.util.stream.Collectors.summingInt(Venda::getQuantidade)));
    }

    @Override
    public double media() {
        logger.info("Calculando a média de vendas");

        List<Venda> vendas = vendaService.buscarTodasVendas();
        if (vendas.isEmpty()) {
            return 0.0;
        }

        double total = vendas.stream()
                .mapToInt(v -> v.getQuantidade() != null ? v.getQuantidade() : 0)
                .sum();
        return total / vendas.size();
    }

    public double media(Produto produto) {
        logger.info("Calculando a média de vendas para o produto: " + produto.getId());

        List<Venda> vendas = vendaService.buscarPorProduto(produto);
        if (vendas.isEmpty()) {
            return 0.0;
        }

        double total = vendas.stream()
                .mapToInt(v -> v.getQuantidade() != null ? v.getQuantidade() : 0)
                .sum();
        return total / vendas.size();
    }

    public double media(Date dataInicio, Date dataFim) {
        logger.info("Calculando a média de vendas entre: " + dataInicio + " e " + dataFim);

        List<Venda> vendas = vendaService.buscarPorPeriodo(
                new java.sql.Date(dataInicio.getTime()),
                new java.sql.Date(dataFim.getTime()));
        if (vendas.isEmpty()) {
            return 0.0;
        }

        double total = vendas.stream()
                .mapToInt(v -> v.getQuantidade() != null ? v.getQuantidade() : 0)
                .sum();
        return total / vendas.size();
    }
    @Override
    public double mediana() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mediana'");
    }

    @Override
    public int moda() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'moda'");
    }

    @Override
    public double desvioPadrao() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'desvioPadrao'");
    }

    @Override
    public double crescimentoPercentual(int valorAnterior, int valorAtual) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'crescimentoPercentual'");
    }

    @Override
    public Map<Produto, Double> taxaParticipacao(List<Venda> vendas) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'taxaParticipacao'");
    }

    @Override
    public Map<String, List<Produto>> analiseABC(List<Venda> vendas) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'analiseABC'");
    }

    @Override
    public List<Double> tendencia(List<Venda> vendas, List<String> periodos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tendencia'");
    }

    @Override
    public int variacaoAbsoluta(int valorAnterior, int valorAtual) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'variacaoAbsoluta'");
    }

    @Override
    public Map<String, Double> indiceSazonalidade(List<Venda> vendas) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'indiceSazonalidade'");
    }

    @Override
    public double ticketMedio(List<Venda> vendas) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ticketMedio'");
    }

}
