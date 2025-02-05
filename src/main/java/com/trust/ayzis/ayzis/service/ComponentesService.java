package com.trust.ayzis.ayzis.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trust.ayzis.ayzis.model.Componentes;
import com.trust.ayzis.ayzis.model.IComponentesRepository;
import com.trust.ayzis.ayzis.model.Produto;

@Service
public class ComponentesService implements IComponentesService {
    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IComponentesRepository componentesRepository;

    @Override
    public Optional<Componentes> buscarPorId(Long id) {
        logger.info("Buscando componente por id" + id);

        Optional<Componentes> componente = componentesRepository.findById(id);
        return componente;
    }

    @Override
    public List<Componentes> buscarPorProdutoComposto(Produto produtoComposto) {
        logger.info("Buscando componentes por produto composto: " + produtoComposto.getId());

        List<Componentes> componentes = componentesRepository.findByProdutoComposto(produtoComposto);
        return componentes;
    }

    @Override
    public List<Componentes> buscarPorProdutoComponente(Produto produtoComponente) {
        logger.info("Buscando componentes por produto componente: " + produtoComponente.getId());

        List<Componentes> componentes = componentesRepository.findByProdutoComponente(produtoComponente);
        return componentes;
    }

    @Override
    public Optional<Componentes> salvarComponente(Componentes componente) {
        logger.info("Salvando componente" + componente.getId());

        Optional<Componentes> componenteSalvo = Optional.of(componentesRepository.save(componente));
        return componenteSalvo;
    }

    @Override
    public Optional<Componentes> atualizarComponente(Componentes componente) {
        logger.info("Atualizando componente" + componente.getId());

        return componentesRepository.findById(componente.getId()).map(c -> {
            c.setProdutoComposto(componente.getProdutoComposto());
            c.setProdutoComponente(componente.getProdutoComponente());
            c.setQuantidade(componente.getQuantidade());
            return componentesRepository.save(c);
        });
    }

    @Override
    public void deletarComponentePorId(Long id) {
        logger.info("Deletando componente por id");

        componentesRepository.deleteById(id);
    }
}
