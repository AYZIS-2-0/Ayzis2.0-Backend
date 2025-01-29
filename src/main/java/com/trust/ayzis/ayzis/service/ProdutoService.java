package com.trust.ayzis.ayzis.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trust.ayzis.ayzis.exception.ProdutoNotFoundException;
import com.trust.ayzis.ayzis.model.IProudutoRepository;
import com.trust.ayzis.ayzis.model.Produto;

@Service
public class ProdutoService implements IProdutoService {

    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    IProudutoRepository produtoRepository;

    @Override
    public Optional<Produto> buscarPorId(String id) {
        logger.info("Buscando produto por id: " + id);

        Optional<Produto> produto = produtoRepository.findById(id);
        return produto;
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        logger.info("Buscando produto por nome: " + nome);

        List<Produto> produto = produtoRepository.findByNomeContainingIgnoreCase(nome);

        if (produto.isEmpty()) {
            throw new ProdutoNotFoundException("Produto não encontrado com o nome: " + nome);
        }

        return produto;
    }

    @Override
    public List<Produto> buscarPorProdutosCompostos(Produto produtosCompostos) {
        logger.info("Buscando produtos por produtos compostos: " + produtosCompostos);

        List<Produto> produtos = produtoRepository.findByProdutosComposicao_Produto(produtosCompostos);
        return produtos;
    }

    @Override
    public List<Produto> buscarTodos() {
        logger.info("Buscando todos os produtos");

        List<Produto> produtos = produtoRepository.findAll();
        return produtos;
    }

    @Override
    public Optional<Produto> salvarProduto(Produto produto) {
        logger.info("Salvando produto: " + produto);

        Optional<Produto> produtoSalvo = Optional.of(produtoRepository.save(produto));
        return produtoSalvo;
    }

    @Override
    public Optional<Produto> atualizar(Produto newProduto) {
        logger.info("Atualizando produto: " + newProduto);

        return produtoRepository.findById(newProduto.getId()).map(produto -> {
            produto.setNome(newProduto.getNome());
            produto.setDescricao(newProduto.getDescricao());
            produto.setMarca(newProduto.getMarca());
            produto.setTipo(newProduto.getTipo());
            produto.setCondicao(newProduto.getCondicao());
            produto.setPreco(newProduto.getPreco());
            produto.setLargura(newProduto.getLargura());
            produto.setAltura(newProduto.getAltura());
            produto.setProfundidade(newProduto.getProfundidade());
            produto.setPeso(newProduto.getPeso());
            produto.setUnidade(newProduto.getUnidade());
            produto.setProdutosComposicao(newProduto.getProdutosComposicao());
            return produtoRepository.save(produto);
        });
    }

    @Override
    public void deletarPorId(String id) {
        logger.info("Deletando produto por id: " + id);

        produtoRepository.deleteById(id);
    }

}
