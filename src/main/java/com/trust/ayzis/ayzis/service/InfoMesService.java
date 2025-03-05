package com.trust.ayzis.ayzis.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trust.ayzis.ayzis.model.Componentes;
import com.trust.ayzis.ayzis.model.IInfoMesRepository;
import com.trust.ayzis.ayzis.model.IProdutoRepository;
import com.trust.ayzis.ayzis.model.IVendaRepository;
import com.trust.ayzis.ayzis.model.InfoMes;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

@Service
public class InfoMesService implements IInfoMesService {

    Logger logger = LogManager.getLogger(InfoMesService.class);

    private static final String STATUS_ENTREGUE = "Entregue";
    private static final String STATUS_VENDA_ENTREGUE = "Venda entregue";
    private static final String STATUS_MEDIACAO_FINALIZADA = "Mediação finalizada. Te demos o dinheiro.";
    private static final String STATUS_A_CAMINHO = "A caminho";

    @Autowired
    private IInfoMesRepository infoMesRepository;

    @Autowired
    private IProdutoRepository produtoRepository;

    @Autowired
    private IVendaRepository vendaRepository;

    @Override
    public Optional<InfoMes> buscarPorId(Long id) {
        logger.info("Buscando infoMes por id: {}", id);
        return infoMesRepository.findById(id);
    }

    @Override
    public List<InfoMes> buscarTodos() {
        logger.info("Buscando todos os infoMes");
        return infoMesRepository.findAll();
    }

    @Override
    public List<InfoMes> buscarPorProduto(Produto produto) {
        logger.info("Buscando infoMes por produto: {}", produto.getId());
        return infoMesRepository.findByProduto(produto);
    }

    @Override
    public List<InfoMes> buscarPorMesAno(Date mesAno) {
        logger.info("Buscando infoMes por mesAno: {}", mesAno);
        return infoMesRepository.findByMonthYear(mesAno);
    }

    @Override
    public List<InfoMes> buscarPorMesAnoEntre(Date inicio, Date fim) {
        logger.info("Buscando infoMes por mesAno entre: {} e {}", inicio, fim);
        return infoMesRepository.findByMonthYearBetween(inicio, fim);
    }

    @Override
    public List<InfoMes> buscarPorProdutoMesAnoEntre(Produto produto, Date inicio, Date fim) {
        logger.info("Buscando infoMes por produto e mesAno entre: {} e {} e {}", produto.getId(), inicio, fim);
        return infoMesRepository.findByProdutoAndMonthYearBetween(produto, inicio, fim);
    }

    @Override
    public void calcAllInfoMes() {
        logger.info("Calculando infoMes");
        List<Produto> produtos = produtoRepository.findAll();
        logger.info("Obtendo {} Produtos", produtos.size());

        logger.info("Iterando sobre produtos");
        for (Produto produto : produtos) {
            logger.info("Produto: {}", produto.getId());

            List<Venda> vendas = vendaRepository.findByProduto(produto);

            if (vendas.isEmpty()) {
                continue;
            }

            logger.info("Iterando sobre Vendas");
            for (Venda venda : vendas) {
                logger.info("Venda: {}", venda.getId());
                if (produto.getProdutosComposicao() == null
                        || produto.getProdutosComposicao().isEmpty()) {
                    processarProdutoSemComponentes(produto, venda);
                } else {
                    processarProdutoComComponentes(produto, venda);
                }
            }
        }
    }

    @Override
    public void recalcInfoMes(Venda venda) {
        logger.info("Venda: {} foi adicionada", venda.getId());
        if (venda.getProduto().getProdutosComposicao() == null
                || venda.getProduto().getProdutosComposicao().isEmpty()) {
            processarProdutoSemComponentes(venda.getProduto(), venda);
        } else {
            processarProdutoComComponentes(venda.getProduto(), venda);
        }
    }

    @Override
    public void recalcByDelete(Venda venda) {
        logger.info("Venda: {} foi deletada", venda.getId());

        InfoMes infoMes = infoMesRepository.findByProdutoAndMonthYear(venda.getProduto(),
                venda.getDataVenda().toLocalDate().getMonthValue(),
                venda.getDataVenda().toLocalDate().getYear()).get();

        if (venda.getProduto().getProdutosComposicao() == null || venda.getProduto().getProdutosComposicao().isEmpty()) {
            if (venda.getStatus().equals(STATUS_ENTREGUE) || venda.getStatus().equals(STATUS_VENDA_ENTREGUE)
                    || venda.getStatus().equals(STATUS_MEDIACAO_FINALIZADA)) {
                infoMes.getVendasConcluidas().remove(venda);
                recalcVendas(infoMes, infoMes.getVendasConcluidas(), "concluídas");
            } else if (venda.getStatus().equals(STATUS_A_CAMINHO)) {
                infoMes.getVendasPendentes().remove(venda);
                recalcVendas(infoMes, infoMes.getVendasPendentes(), "pendentes");
            } else {
                infoMes.getVendasCanceladas().remove(venda);
                recalcVendas(infoMes, infoMes.getVendasCanceladas(), "canceladas");
            }
        } else {
            for (Componentes componente : venda.getProduto().getProdutosComposicao()) {
                if (componente.getProdutoComponente().getId() == infoMes.getProduto().getId()) {
                    int quantidade = venda.getQuantidade() * componente.getQuantidade();
                    if (venda.getStatus().equals(STATUS_ENTREGUE) || venda.getStatus().equals(STATUS_VENDA_ENTREGUE)
                            || venda.getStatus().equals(STATUS_MEDIACAO_FINALIZADA)) {
                        infoMes.getVendasConcluidas().remove(venda);
                        recalcVendas(infoMes, infoMes.getVendasConcluidas(), "concluídas");
                    } else if (venda.getStatus().equals(STATUS_A_CAMINHO)) {
                        infoMes.getVendasPendentes().remove(venda);
                        recalcVendas(infoMes, infoMes.getVendasPendentes(), "pendentes");
                    } else {
                        infoMes.getVendasCanceladas().remove(venda);
                        recalcVendas(infoMes, infoMes.getVendasCanceladas(), "canceladas");
                    }
                }
            }
        }
    }

    private void processarProdutoComComponentes(Produto produto, Venda venda) {
        logger.info("Iterando sobre Produto com Componentes: {}", produto.getId());

        for (Componentes componente : produto.getProdutosComposicao()) {
            logger.info("Componente: {}", componente.getId());

            InfoMes infoMes = createInfoMes(componente.getProdutoComponente(), venda);
            logger.info("Criado InfoMes {} para o produto {} na data {}", infoMes.getId(), infoMes.getProduto().getId(),
                    infoMes.getMonthYear());

            attVendaListInfoMes(infoMes, venda);
            infoMesRepository.save(infoMes);
            logger.info("InfoMes salvo: {}", infoMes.getId());
        }
    }

    private void processarProdutoSemComponentes(Produto produto, Venda venda) {
        logger.info("Processando Produto sem Componentes: {}", produto.getId());
        InfoMes infoMes = createInfoMes(produto, venda);
        logger.info("Criado InfoMes {} para o produto {} na data {}", infoMes.getId(), infoMes.getProduto().getId(),
                infoMes.getMonthYear());

        attVendaListInfoMes(infoMes, venda);
        infoMesRepository.save(infoMes);
        logger.info("InfoMes salvo: {}", infoMes.getId());
    }

    private boolean isVendaEntregue(Venda venda) {
        return venda.getStatus().equals(STATUS_ENTREGUE) || venda.getStatus().equals(STATUS_VENDA_ENTREGUE)
                || venda.getStatus().equals(STATUS_MEDIACAO_FINALIZADA);
    }

    public InfoMes createInfoMes(Produto produto, Venda venda) {
        logger.info("Criando InfoMes para o Produto: {}", produto.getId());

        return infoMesRepository
                .findByProdutoAndMonthYear(produto,
                        venda.getDataVenda().toLocalDate().getMonthValue(),
                        venda.getDataVenda().toLocalDate().getYear())
                .orElseGet(() -> {
                    InfoMes infoMesNovo = new InfoMes();
                    infoMesNovo.setMonthYear(Date.valueOf(venda.getDataVenda().toLocalDate().withDayOfMonth(1)));
                    infoMesNovo.setProduto(produto);
                    infoMesNovo.setVendasConcluidas(new ArrayList<>());
                    infoMesNovo.setVendasPendentes(new ArrayList<>());
                    infoMesNovo.setVendasCanceladas(new ArrayList<>());
                    infoMesNovo.setVendasDiretas(new ArrayList<>());
                    logger.info("InfoMesVenda: {}", infoMesNovo.getId());
                    logger.info("Data: {}-{}", venda.getDataVenda().toLocalDate().getMonthValue(),
                            venda.getDataVenda().toLocalDate().getYear());
                    return infoMesNovo;
                });
    }

    private void attVendaListInfoMes(InfoMes infoMes, Venda venda) {
        logger.info("Atualizando InfoMes: {}", infoMes.getId());
        logger.info("Venda: {}", venda.getId());

        if (venda.getOrigem().equals("ML")) {
            if (isVendaEntregue(venda)) {
                if (infoMes.getVendasPendentes().contains(venda)) {
                    infoMes.getVendasPendentes().remove(venda);
                } else if (infoMes.getVendasCanceladas().contains(venda)) {
                    infoMes.getVendasCanceladas().remove(venda);
                }
                infoMes.getVendasConcluidas().add(venda);
                recalcVendas(infoMes, infoMes.getVendasConcluidas(), "concluídas");
            } else if (venda.getStatus().equals(STATUS_A_CAMINHO)) {
                if (infoMes.getVendasConcluidas().contains(venda)) {
                    infoMes.getVendasConcluidas().remove(venda);
                } else if (infoMes.getVendasCanceladas().contains(venda)) {
                    infoMes.getVendasCanceladas().remove(venda);
                }
                infoMes.getVendasPendentes().add(venda);
                recalcVendas(infoMes, infoMes.getVendasPendentes(), "pendentes");
            } else {
                if (infoMes.getVendasConcluidas().contains(venda)) {
                    infoMes.getVendasConcluidas().remove(venda);
                } else if (infoMes.getVendasPendentes().contains(venda)) {
                    infoMes.getVendasPendentes().remove(venda);
                }
                infoMes.getVendasCanceladas().add(venda);
                recalcVendas(infoMes, infoMes.getVendasCanceladas(), "canceladas");
            }
        } else {
            if (isVendaEntregue(venda)) {
                infoMes.getVendasDiretas().add(venda);
                recalcVendas(infoMes, infoMes.getVendasDiretas(), "diretas");
            }
        }

        logger.info("InfoMes atualizado: Total: {}, Pendente Total: {}, Cancelado Total: {}",
                infoMes.getTotal(), infoMes.getPendenteTotal(), infoMes.getCanceladoTotal());
    }

    private void recalcVendas(InfoMes infoMes, List<Venda> vendas, String tipo) {
        logger.info("Recalculando Vendas {}: {}", tipo, infoMes.getId());

        int individual = 0;
        int componente = 0;
        int pendenteIndividual = 0;
        int pendenteComponente = 0;
        int canceladoIndividual = 0;
        int canceladoComponente = 0;

        logger.info("Iterando sobre Vendas {}", tipo);
        for (Venda venda : vendas) {
            if (venda.getProduto().getProdutosComposicao().isEmpty()
                    || venda.getProduto().getProdutosComposicao() == null) {
                logger.info("Produto sem Componentes: {}", venda.getProduto().getId());
                if (tipo.equals("concluídas")) {
                    individual += venda.getQuantidade();
                } else if (tipo.equals("pendentes")) {
                    pendenteIndividual += venda.getQuantidade();
                } else if (tipo.equals("canceladas")) {
                    canceladoIndividual += venda.getQuantidade();
                }
            } else {
                logger.info("Produto com Componentes: {}", venda.getProduto().getId());
                logger.info("Iterando sobre Componentes, e obtendo quantidade");
                for (Componentes comp : venda.getProduto().getProdutosComposicao()) {
                    if (comp.getProdutoComponente().getId() == infoMes.getProduto().getId()) {
                        int quantidade = venda.getQuantidade() * comp.getQuantidade();
                        if (tipo.equals("concluídas")) {
                            componente += quantidade;
                        } else if (tipo.equals("pendentes")) {
                            pendenteComponente += quantidade;
                        } else if (tipo.equals("canceladas")) {
                            canceladoComponente += quantidade;
                        }
                        logger.info("Componente: {}, Quantidade: {}", comp.getProdutoComponente().getId(),
                                comp.getQuantidade());
                    }
                }
            }
        }

        if (tipo.equals("concluídas")) {
            infoMes.setIndividual(individual);
            infoMes.setComponente(componente);
            infoMes.setTotal(infoMes.getIndividual() + infoMes.getComponente() + infoMes.getDireta());
        } else if (tipo.equals("pendentes")) {
            infoMes.setPendenteIndividual(pendenteIndividual);
            infoMes.setPendenteComponente(pendenteComponente);
            infoMes.setPendenteTotal(infoMes.getPendenteIndividual() + infoMes.getPendenteComponente());
        } else if (tipo.equals("canceladas")) {
            infoMes.setCanceladoIndividual(canceladoIndividual);
            infoMes.setCanceladoComponente(canceladoComponente);
            infoMes.setCanceladoTotal(infoMes.getCanceladoIndividual() + infoMes.getCanceladoComponente());
        }

        logger.info(
                "InfoMes atualizado: Individual: {}, Componente: {}, Total: {}, Pendente Individual: {}, Pendente Componente: {}, Pendente Total: {}, Cancelado Individual: {}, Cancelado Componente: {}, Cancelado Total: {}",
                infoMes.getIndividual(), infoMes.getComponente(), infoMes.getTotal(),
                infoMes.getPendenteIndividual(), infoMes.getPendenteComponente(), infoMes.getPendenteTotal(),
                infoMes.getCanceladoIndividual(), infoMes.getCanceladoComponente(), infoMes.getCanceladoTotal());
    }

    @Override
    public void deletarPorId(Long id) {
        logger.info("Deletando infoMes por id: {}", id);
        infoMesRepository.deleteById(id);
    }
}