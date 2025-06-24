package com.trust.ayzis.ayzis.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

@Service
public class EstatiscasVendaService implements IEstatiscasVendasService {

    private static final String STATUS_ENTREGUE = "Entregue";
    private static final String STATUS_VENDA_ENTREGUE = "Venda entregue";
    private static final String STATUS_MEDIACAO_FINALIZADA = "Mediação finalizada. Te demos o dinheiro.";
    private static final String STATUS_A_CAMINHO = "A caminho";

    Logger logger = LogManager.getLogger(getClass());

    @Autowired
    IProdutoService produtoService;

    @Autowired
    IVendaService vendaService;

    @Autowired
    IInfoMesService infoMesService;

    private boolean isVendaEntregue(Venda venda) {
        String status = venda.getStatus();
        return STATUS_ENTREGUE.equalsIgnoreCase(status)
                || STATUS_VENDA_ENTREGUE.equalsIgnoreCase(status)
                || STATUS_MEDIACAO_FINALIZADA.equalsIgnoreCase(status);
    }

    @Override
    public Map<String, Integer> somaVendasQTDPorMes() {
        logger.info("Calculando soma de quantidade vendida por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Integer> somaPorMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            somaPorMes.put(mes, somaPorMes.getOrDefault(mes, 0) + venda.getQuantidade());
        }
        return somaPorMes;
    }

    @Override
    public Map<String, Double> somaVendasValorPorMes() {
        logger.info("Calculando soma de valor vendido por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Double> somaPorMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            somaPorMes.put(mes, somaPorMes.getOrDefault(mes, 0.0) + venda.getValorTotal());
        }
        return somaPorMes;
    }

    @Override
    public Map<String, Map<String, Object>> somaVendasQTDPorMesProduto() {
        logger.info("Calculando soma de quantidade vendida por mês e produto, incluindo lista de vendas");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Map<String, Object>> somaPorProdutoMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            Produto produto = venda.getProduto();
            String produtoKey = produto.getId();
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());

            somaPorProdutoMes.computeIfAbsent(produtoKey, k -> new HashMap<>());

            // Soma quantidade
            Map<String, Object> dadosMes = somaPorProdutoMes.get(produtoKey);
            Integer somaQtd = (Integer) dadosMes.getOrDefault(mes + "_qtd", 0);
            dadosMes.put(mes + "_qtd", somaQtd + venda.getQuantidade());

            // Lista de vendas
            @SuppressWarnings("unchecked")
            List<Venda> vendasMes = (List<Venda>) dadosMes.getOrDefault(mes + "_vendas", new ArrayList<Venda>());
            vendasMes.add(venda);
            dadosMes.put(mes + "_vendas", vendasMes);
        }
        return somaPorProdutoMes;
    }

    @Override
    public Map<String, Map<String, Object>> somaVendasValorPorMesProduto() {
        logger.info("Calculando soma de valor vendido por mês e produto, incluindo lista de vendas");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Map<String, Object>> somaPorProdutoMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            Produto produto = venda.getProduto();
            String produtoKey = produto.getId();
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());

            somaPorProdutoMes.computeIfAbsent(produtoKey, k -> new HashMap<>());

            Map<String, Object> dadosMes = somaPorProdutoMes.get(produtoKey);

            // Soma valor
            Double somaValor = (Double) dadosMes.getOrDefault(mes + "_valor", 0.0);
            dadosMes.put(mes + "_valor", somaValor + venda.getValorTotal());

            // Lista de vendas
            @SuppressWarnings("unchecked")
            List<Venda> vendasMes = (List<Venda>) dadosMes.getOrDefault(mes + "_vendas", new ArrayList<Venda>());
            vendasMes.add(venda);
            dadosMes.put(mes + "_vendas", vendasMes);
        }
        return somaPorProdutoMes;
    }

    @Override
    public Map<String, Integer> somaVendasQTDPorMesProduto(Produto produto) {
        logger.info("Calculando soma de quantidade vendida por mês para o produto: " + produto.getId());
        List<Venda> vendas = vendaService.buscarPorProduto(produto);
        Map<String, Integer> somaPorMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            somaPorMes.put(mes, somaPorMes.getOrDefault(mes, 0) + venda.getQuantidade());
        }
        return somaPorMes;
    }

    @Override
    public Map<String, Double> somaVendasValorPorMesProduto(Produto produto) {
        logger.info("Calculando soma de valor vendido por mês para o produto: " + produto.getId());
        List<Venda> vendas = vendaService.buscarPorProduto(produto);
        Map<String, Double> somaPorMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            somaPorMes.put(mes, somaPorMes.getOrDefault(mes, 0.0) + venda.getValorTotal());
        }
        return somaPorMes;
    }

    @Override
    public Map<String, Integer> mediaVendasQTDPorMes() {
        logger.info("Calculando média de quantidade vendida por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Integer> soma = new HashMap<>();
        Map<String, Integer> cont = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            soma.put(mes, soma.getOrDefault(mes, 0) + venda.getQuantidade());
            cont.put(mes, cont.getOrDefault(mes, 0) + 1);
        }
        Map<String, Integer> media = new HashMap<>();
        for (String mes : soma.keySet()) {
            media.put(mes, soma.get(mes) / cont.get(mes));
        }
        return media;
    }

    @Override
    public Map<String, Double> mediaVendasValorPorMes() {
        logger.info("Calculando média de valor vendido por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Double> soma = new HashMap<>();
        Map<String, Integer> cont = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            soma.put(mes, soma.getOrDefault(mes, 0.0) + venda.getValorTotal());
            cont.put(mes, cont.getOrDefault(mes, 0) + 1);
        }
        Map<String, Double> media = new HashMap<>();
        for (String mes : soma.keySet()) {
            media.put(mes, soma.get(mes) / cont.get(mes));
        }
        return media;
    }

    @Override
    public Map<Produto, Map<String, Integer>> mediaVendasQTDPorMesProduto() {
        logger.info("Calculando média de quantidade vendida por mês e produto");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<Produto, Map<String, Integer>> somaPorProdutoMes = new HashMap<>();
        Map<Produto, Map<String, Integer>> contPorProdutoMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            Produto produto = venda.getProduto();
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            somaPorProdutoMes
                    .computeIfAbsent(produto, k -> new HashMap<>())
                    .merge(mes, venda.getQuantidade(), Integer::sum);
            contPorProdutoMes
                    .computeIfAbsent(produto, k -> new HashMap<>())
                    .merge(mes, 1, Integer::sum);
        }
        Map<Produto, Map<String, Integer>> mediaPorProdutoMes = new HashMap<>();
        for (Produto produto : somaPorProdutoMes.keySet()) {
            Map<String, Integer> somaMes = somaPorProdutoMes.get(produto);
            Map<String, Integer> contMes = contPorProdutoMes.get(produto);
            Map<String, Integer> mediaMes = new HashMap<>();
            for (String mes : somaMes.keySet()) {
                mediaMes.put(mes, somaMes.get(mes) / contMes.get(mes));
            }
            mediaPorProdutoMes.put(produto, mediaMes);
        }
        return mediaPorProdutoMes;
    }

    @Override
    public Map<Produto, Map<String, Double>> mediaVendasValorPorMesProduto() {
        logger.info("Calculando média de valor vendido por mês e produto");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<Produto, Map<String, Double>> somaPorProdutoMes = new HashMap<>();
        Map<Produto, Map<String, Integer>> contPorProdutoMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            Produto produto = venda.getProduto();
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            somaPorProdutoMes
                    .computeIfAbsent(produto, k -> new HashMap<>())
                    .merge(mes, venda.getValorTotal(), Double::sum);
            contPorProdutoMes
                    .computeIfAbsent(produto, k -> new HashMap<>())
                    .merge(mes, 1, Integer::sum);
        }
        Map<Produto, Map<String, Double>> mediaPorProdutoMes = new HashMap<>();
        for (Produto produto : somaPorProdutoMes.keySet()) {
            Map<String, Double> somaMes = somaPorProdutoMes.get(produto);
            Map<String, Integer> contMes = contPorProdutoMes.get(produto);
            Map<String, Double> mediaMes = new HashMap<>();
            for (String mes : somaMes.keySet()) {
                mediaMes.put(mes, somaMes.get(mes) / contMes.get(mes));
            }
            mediaPorProdutoMes.put(produto, mediaMes);
        }
        return mediaPorProdutoMes;
    }

    @Override
    public Map<String, Integer> mediaVendasQTDPorAnoProduto(Produto produto) {
        logger.info("Calculando média de quantidade vendida por ano para o produto: " + produto.getId());
        List<Venda> vendas = vendaService.buscarPorProduto(produto);
        Map<String, Integer> somaPorAno = new HashMap<>();
        Map<String, Integer> contPorAno = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String ano = new java.text.SimpleDateFormat("yyyy").format(venda.getDataVenda());
            somaPorAno.put(ano, somaPorAno.getOrDefault(ano, 0) + venda.getQuantidade());
            contPorAno.put(ano, contPorAno.getOrDefault(ano, 0) + 1);
        }
        Map<String, Integer> mediaPorAno = new HashMap<>();
        for (String ano : somaPorAno.keySet()) {
            mediaPorAno.put(ano, somaPorAno.get(ano) / contPorAno.get(ano));
        }
        return mediaPorAno;
    }

    @Override
    public Map<String, Integer> medianaVendasQTDPorMes() {
        logger.info("Calculando mediana de quantidade vendida por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, List<Integer>> quantidadesPorMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            quantidadesPorMes.computeIfAbsent(mes, k -> new ArrayList<>()).add(venda.getQuantidade());
        }
        Map<String, Integer> medianaPorMes = new HashMap<>();
        for (String mes : quantidadesPorMes.keySet()) {
            List<Integer> quantidades = quantidadesPorMes.get(mes);
            Collections.sort(quantidades);
            int n = quantidades.size();
            int mediana = (n % 2 == 0) ? (quantidades.get(n / 2 - 1) + quantidades.get(n / 2)) / 2
                    : quantidades.get(n / 2);
            medianaPorMes.put(mes, mediana);
        }
        return medianaPorMes;
    }

    @Override
    public Map<String, Double> medianaVendasValorPorMes() {
        logger.info("Calculando mediana de valor vendido por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, List<Double>> valoresPorMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            valoresPorMes.computeIfAbsent(mes, k -> new ArrayList<>()).add(venda.getValorTotal());
        }
        Map<String, Double> medianaPorMes = new HashMap<>();
        for (String mes : valoresPorMes.keySet()) {
            List<Double> valores = valoresPorMes.get(mes);
            Collections.sort(valores);
            int n = valores.size();
            double mediana = (n % 2 == 0) ? (valores.get(n / 2 - 1) + valores.get(n / 2)) / 2.0 : valores.get(n / 2);
            medianaPorMes.put(mes, mediana);
        }
        return medianaPorMes;
    }

    @Override
    public Produto produtoMaisVendidoPorPeriodo(String periodo) {
        logger.info("Calculando produto mais vendido por período: " + periodo);
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<Produto, Integer> contagem = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            if (mes.equals(periodo)) {
                Produto produto = venda.getProduto();
                contagem.put(produto, contagem.getOrDefault(produto, 0) + venda.getQuantidade());
            }
        }
        return contagem.isEmpty() ? null : Collections.max(contagem.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    @Override
    public Map<String, Produto> produtoMaisVendidoPorMes() {
        logger.info("Calculando produto mais vendido por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Map<Produto, Integer>> contagem = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            Produto produto = venda.getProduto();
            contagem.computeIfAbsent(mes, k -> new HashMap<>())
                    .merge(produto, venda.getQuantidade(), Integer::sum);
        }
        Map<String, Produto> modaPorMes = new HashMap<>();
        for (String mes : contagem.keySet()) {
            Produto maisVendido = Collections.max(contagem.get(mes).entrySet(), Map.Entry.comparingByValue()).getKey();
            modaPorMes.put(mes, maisVendido);
        }
        return modaPorMes;
    }

    @Override
    public Map<String, Double> desvioPadraoVendasQTDPorMes() {
        logger.info("Calculando desvio padrão da quantidade vendida por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, List<Integer>> quantidadesPorMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            quantidadesPorMes.computeIfAbsent(mes, k -> new ArrayList<>()).add(venda.getQuantidade());
        }
        Map<String, Double> desvioPorMes = new HashMap<>();
        for (String mes : quantidadesPorMes.keySet()) {
            List<Integer> quantidades = quantidadesPorMes.get(mes);
            double media = quantidades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            double somaQuadrados = quantidades.stream().mapToDouble(q -> Math.pow(q - media, 2)).sum();
            double desvio = Math.sqrt(somaQuadrados / quantidades.size());
            desvioPorMes.put(mes, desvio);
        }
        return desvioPorMes;
    }

    @Override
    public Map<String, Double> desvioPadraoVendasValorPorMes() {
        logger.info("Calculando desvio padrão do valor vendido por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, List<Double>> valoresPorMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            valoresPorMes.computeIfAbsent(mes, k -> new ArrayList<>()).add(venda.getValorTotal());
        }
        Map<String, Double> desvioPorMes = new HashMap<>();
        for (String mes : valoresPorMes.keySet()) {
            List<Double> valores = valoresPorMes.get(mes);
            double media = valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double somaQuadrados = valores.stream().mapToDouble(v -> Math.pow(v - media, 2)).sum();
            double desvio = Math.sqrt(somaQuadrados / valores.size());
            desvioPorMes.put(mes, desvio);
        }
        return desvioPorMes;
    }

    @Override
    public Map<String, Double> crescimentoPercentualVendasPorMes() {
        logger.info("Calculando crescimento percentual de vendas por mês");
        Map<String, Double> somaPorMes = somaVendasValorPorMes();
        List<String> meses = new ArrayList<>(somaPorMes.keySet());
        Collections.sort(meses);
        Map<String, Double> crescimento = new HashMap<>();
        for (int i = 1; i < meses.size(); i++) {
            String mesAtual = meses.get(i);
            String mesAnterior = meses.get(i - 1);
            double valorAtual = somaPorMes.get(mesAtual);
            double valorAnterior = somaPorMes.get(mesAnterior);
            if (valorAnterior != 0) {
                crescimento.put(mesAtual, ((valorAtual - valorAnterior) / valorAnterior) * 100);
            } else {
                crescimento.put(mesAtual, 0.0);
            }
        }
        return crescimento;
    }

    @Override
    public Map<Produto, Double> taxaParticipacaoPorProduto() {
        logger.info("Calculando taxa de participação por produto");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        double total = vendas.stream().filter(this::isVendaEntregue).mapToDouble(Venda::getValorTotal).sum();
        Map<Produto, Double> somaPorProduto = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            Produto produto = venda.getProduto();
            somaPorProduto.put(produto, somaPorProduto.getOrDefault(produto, 0.0) + venda.getValorTotal());
        }
        Map<Produto, Double> taxa = new HashMap<>();
        for (Produto produto : somaPorProduto.keySet()) {
            taxa.put(produto, (somaPorProduto.get(produto) / total) * 100);
        }
        return taxa;
    }

    @Override
    public Map<String, Double> taxaParticipacaoPorOrigem() {
        logger.info("Calculando taxa de participação por origem");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        double total = vendas.stream().filter(this::isVendaEntregue).mapToDouble(Venda::getValorTotal).sum();
        Map<String, Double> somaPorOrigem = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String origem = venda.getOrigem(); // Supondo que existe esse campo
            somaPorOrigem.put(origem, somaPorOrigem.getOrDefault(origem, 0.0) + venda.getValorTotal());
        }
        Map<String, Double> taxa = new HashMap<>();
        for (String origem : somaPorOrigem.keySet()) {
            taxa.put(origem, (somaPorOrigem.get(origem) / total) * 100);
        }
        return taxa;
    }

    @Override
    public Map<Produto, String> classificacaoABCProdutos() {
        logger.info("Classificando produtos ABC");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<Produto, Double> somaPorProduto = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            Produto produto = venda.getProduto();
            somaPorProduto.put(produto, somaPorProduto.getOrDefault(produto, 0.0) + venda.getValorTotal());
        }
        List<Map.Entry<Produto, Double>> lista = new ArrayList<>(somaPorProduto.entrySet());
        lista.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        double total = lista.stream().mapToDouble(Map.Entry::getValue).sum();
        double acumulado = 0.0;
        Map<Produto, String> classificacao = new HashMap<>();
        for (Map.Entry<Produto, Double> entry : lista) {
            acumulado += entry.getValue();
            double percentual = (acumulado / total) * 100;
            if (percentual <= 20) {
                classificacao.put(entry.getKey(), "A");
            } else if (percentual <= 50) {
                classificacao.put(entry.getKey(), "B");
            } else {
                classificacao.put(entry.getKey(), "C");
            }
        }
        return classificacao;
    }

    @Override
    public Map<Produto, List<Double>> tendenciaVendasPorMesProduto() {
        logger.info("Calculando tendência de vendas por produto e mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<Produto, Map<String, Double>> valoresPorProdutoMes = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            Produto produto = venda.getProduto();
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            valoresPorProdutoMes
                    .computeIfAbsent(produto, k -> new HashMap<>())
                    .put(mes, valoresPorProdutoMes.get(produto).getOrDefault(mes, 0.0) + venda.getValorTotal());
        }
        Map<Produto, List<Double>> tendencia = new HashMap<>();
        for (Produto produto : valoresPorProdutoMes.keySet()) {
            Map<String, Double> valoresMes = valoresPorProdutoMes.get(produto);
            List<String> meses = new ArrayList<>(valoresMes.keySet());
            Collections.sort(meses);
            List<Double> valores = new ArrayList<>();
            for (String mes : meses) {
                valores.add(valoresMes.get(mes));
            }
            tendencia.put(produto, valores);
        }
        return tendencia;
    }

    @Override
    public Map<String, Double> ticketMedioPorMes() {
        logger.info("Calculando ticket médio por mês");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Double> soma = new HashMap<>();
        Map<String, Integer> cont = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String mes = new java.text.SimpleDateFormat("yyyy-MM").format(venda.getDataVenda());
            soma.put(mes, soma.getOrDefault(mes, 0.0) + venda.getValorTotal());
            cont.put(mes, cont.getOrDefault(mes, 0) + 1);
        }
        Map<String, Double> ticketMedio = new HashMap<>();
        for (String mes : soma.keySet()) {
            ticketMedio.put(mes, soma.get(mes) / cont.get(mes));
        }
        return ticketMedio;
    }

    @Override
    public Map<String, Double> conversaoPorOrigem() {
        logger.info("Calculando conversão por origem");
        List<Venda> vendas = vendaService.buscarTodasVendas();
        Map<String, Integer> totalPorOrigem = new HashMap<>();
        Map<String, Integer> concluidasPorOrigem = new HashMap<>();
        for (Venda venda : vendas) {
            if (!isVendaEntregue(venda)) continue;
            String origem = venda.getOrigem(); // Supondo que existe esse campo
            totalPorOrigem.put(origem, totalPorOrigem.getOrDefault(origem, 0) + 1);
            if ("CONCLUIDA".equalsIgnoreCase(venda.getStatus())) {
                concluidasPorOrigem.put(origem, concluidasPorOrigem.getOrDefault(origem, 0) + 1);
            }
        }
        Map<String, Double> conversao = new HashMap<>();
        for (String origem : totalPorOrigem.keySet()) {
            int total = totalPorOrigem.get(origem);
            int concluidas = concluidasPorOrigem.getOrDefault(origem, 0);
            conversao.put(origem, total > 0 ? (concluidas * 100.0 / total) : 0.0);
        }
        return conversao;
    }
}
