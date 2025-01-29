package com.trust.ayzis.ayzis.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.micrometer.common.lang.NonNull;
import java.sql.Date;

@Repository
public interface IVendaRepository extends JpaRepository<Venda, String> {
    
    @NonNull
    public Optional<Venda> findById(@NonNull String id);

    public List<Venda> findByDataVenda(Date dataVenda);

    public List<Venda> findByDataVendaBetween(Date start, Date end);

    public List<Venda> findByProduto(Produto produto);

    public List<Venda> findByStatus(String status);

    public void deleteById(String id);
}
