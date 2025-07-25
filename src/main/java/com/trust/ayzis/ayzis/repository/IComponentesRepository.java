package com.trust.ayzis.ayzis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.trust.ayzis.ayzis.model.Componentes;
import com.trust.ayzis.ayzis.model.Produto;

@Repository
public interface IComponentesRepository extends JpaRepository<Componentes, Long> {
    @NonNull
    public Optional<Componentes> findById(@NonNull Long id);

    public List<Componentes> findByProdutoComposto(Produto produtoComposto);

    public List<Componentes> findByProdutoComponente(Produto produtoComponente);

    public void deleteById(@NonNull Long id);
}
