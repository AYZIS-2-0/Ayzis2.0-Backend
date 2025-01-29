package com.trust.ayzis.ayzis.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.trust.ayzis.ayzis.model.Produto;

import io.micrometer.common.lang.NonNull;

@Repository
public interface IProudutoRepository extends JpaRepository<Produto, String> {

    @NonNull
    public Optional<Produto> findById(@NonNull String id);

    public List<Produto> findByNomeContainingIgnoreCase(String nome);

    public List<Produto> findByProdutosComposicao_Produto(Produto produto);

    public void deleteById(@NonNull String id);
}
