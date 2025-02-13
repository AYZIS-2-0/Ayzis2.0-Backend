package com.trust.ayzis.ayzis.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.trust.ayzis.ayzis.model.Produto;

public interface IProdutoService {
    public Optional<Produto> buscarPorId(String id);

    public List<Produto> buscarPorNome(String nome);

    public List<Produto> buscarPorProdutosCompostos(Produto produtosCompostos);

    public List<Produto> buscarTodosProdutos(Pageable pageable);

    public List<Produto> buscarTodosProdutos();

    public Optional<Produto> salvarProduto(Produto produto);

    public Optional<Produto> atualizarProduto(Produto produto);

    public void deletarProdutoPorId(String id);
}
