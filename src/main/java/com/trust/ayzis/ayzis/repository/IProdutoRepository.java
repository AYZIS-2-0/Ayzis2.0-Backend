package com.trust.ayzis.ayzis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.trust.ayzis.ayzis.model.Produto;

@Repository
public interface IProdutoRepository extends JpaRepository<Produto, String> {

    @NonNull
    public Optional<Produto> findById(@NonNull String id);

    public List<Produto> findByNomeContainingIgnoreCase(String nome);

    public List<Produto> findByProdutosComposicao_ProdutoComposto(Produto produtoComposto);

    public void deleteById(@NonNull String id);
}