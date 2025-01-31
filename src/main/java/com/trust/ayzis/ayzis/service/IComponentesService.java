package com.trust.ayzis.ayzis.service;

import java.util.List;
import java.util.Optional;

import com.trust.ayzis.ayzis.model.Componentes;
import com.trust.ayzis.ayzis.model.Produto;

public interface IComponentesService {
    public Optional<Componentes> buscarPorId(Long id);

    public List<Componentes> buscarPorProdutoComposto(Produto produtoComposto);

    public List<Componentes> buscarPorProdutoComponente(Produto produtoComponente);

    public Optional<Componentes> salvarComponente(Componentes componente);

    public Optional<Componentes> atualizarComponente(Componentes componente);

    public void deletarComponentePorId(Long id);
}
