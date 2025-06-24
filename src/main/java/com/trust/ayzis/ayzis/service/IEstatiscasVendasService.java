package com.trust.ayzis.ayzis.service;

import java.util.List;
import java.util.Map;

import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

public interface IEstatiscasVendasService {

    // Soma
    public Map<String, Integer> somaVendasQTDPorMes();
    public Map<String, Double> somaVendasValorPorMes();

    public Map<String, Map<String, Object>> somaVendasQTDPorMesProduto();
    public Map<String, Map<String, Object>> somaVendasValorPorMesProduto();

    public Map<String, Integer> somaVendasQTDPorMesProduto(Produto produto);
    public Map<String, Double> somaVendasValorPorMesProduto(Produto produto);

    // Média
    public Map<String, Integer> mediaVendasQTDPorMes();
    public Map<String, Double> mediaVendasValorPorMes();

    public Map<Produto, Map<String, Integer>> mediaVendasQTDPorMesProduto();
    public Map<Produto, Map<String, Double>> mediaVendasValorPorMesProduto();

    public Map<String, Integer> mediaVendasQTDPorAnoProduto(Produto produto);

    // Mediana
    public Map<String, Integer> medianaVendasQTDPorMes();
    public Map<String, Double> medianaVendasValorPorMes();

    // Moda
    public Produto produtoMaisVendidoPorPeriodo(String periodo);
    public Map<String, Produto> produtoMaisVendidoPorMes();

    // Desvio Padrão
    public Map<String, Double> desvioPadraoVendasQTDPorMes();
    public Map<String, Double> desvioPadraoVendasValorPorMes();

    // Crescimento Percentual
    public Map<String, Double> crescimentoPercentualVendasPorMes();

    // Taxa de Participação
    public Map<Produto, Double> taxaParticipacaoPorProduto();
    public Map<String, Double> taxaParticipacaoPorOrigem();

    // Análise ABC
    public Map<Produto, String> classificacaoABCProdutos();

    // Tendência
    public Map<Produto, List<Double>> tendenciaVendasPorMesProduto();

    // Ticket Médio
    public Map<String, Double> ticketMedioPorMes();

    // Conversão por Origem
    public Map<String, Double> conversaoPorOrigem();
}