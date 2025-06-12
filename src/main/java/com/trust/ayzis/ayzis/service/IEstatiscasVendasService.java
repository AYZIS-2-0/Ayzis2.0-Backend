package com.trust.ayzis.ayzis.service;

import java.util.List;
import java.util.Map;

import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

public interface IEstatiscasVendasService {

    // 1. Soma total de vendas (por produto, por mês, geral)
    public int somaTotal();

    public Map<String, Integer> somaMensal(Produto produto);

    // 2. Média de vendas
    public double media();

    // 3. Mediana de vendas
    public double mediana();

    // 4. Moda de vendas
    public int moda();

    // 5. Desvio padrão das vendas
    public double desvioPadrao();

    // 6. Crescimento percentual entre dois períodos
    public double crescimentoPercentual(int valorAnterior, int valorAtual);

    // 7. Taxa de participação de cada produto
    public Map<Produto, Double> taxaParticipacao(List<Venda> vendas);

    // 8. Análise ABC
    public Map<String, List<Produto>> analiseABC(List<Venda> vendas);

    // 9. Tendência (ex: lista de totais por mês)
    public List<Double> tendencia(List<Venda> vendas, List<String> periodos);

    // 10. Variação absoluta entre dois valores
    public int variacaoAbsoluta(int valorAnterior, int valorAtual);

    // 11. Índice de sazonalidade (por mês)
    public Map<String, Double> indiceSazonalidade(List<Venda> vendas);

    // 12. Ticket médio (valor médio por venda)
    public double ticketMedio(List<Venda> vendas);
}