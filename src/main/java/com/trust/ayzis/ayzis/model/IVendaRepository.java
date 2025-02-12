package com.trust.ayzis.ayzis.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.micrometer.common.lang.NonNull;
import java.sql.Date;

@Repository
public interface IVendaRepository extends JpaRepository<Venda, String> {

    @NonNull
    public Optional<Venda> findById(@NonNull String id);

    public List<Venda> findByDataVenda(Date dataVenda);

    public List<Venda> findByDataVendaBetween(Date start, Date end);

    public List<Venda> findByProdutoAndDataVendaBetween(Produto produto, Date start, Date end);

    public List<Venda> findByProduto(Produto produto);

    public List<Venda> findByStatus(String status);

    public void deleteById(String id);

    @Query("SELECT v FROM Venda v WHERE v.produto = :produto AND MONTH(v.dataVenda) = :month AND YEAR(v.dataVenda) = :year")
    public List<Venda> findByProdutoAndMonthAndYear(@Param("produto") Produto produto, @Param("month") int month, @Param("year") int year);
}
