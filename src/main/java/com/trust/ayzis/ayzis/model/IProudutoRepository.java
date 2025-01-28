package com.trust.ayzis.ayzis.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.trust.ayzis.ayzis.model.Produto;

@Repository
public interface IProudutoRepository extends JpaRepository<Produto, String> {
    public Optional<Produto> findById(String id);

    public Optional<Produto> findByNome(String nome);

    public List<Produto> findByProdutosComposicao_Produto(Produto produto);

    public void deleteById(String id);
}
