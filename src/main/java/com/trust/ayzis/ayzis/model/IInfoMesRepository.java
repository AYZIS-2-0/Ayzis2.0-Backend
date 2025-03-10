package com.trust.ayzis.ayzis.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface IInfoMesRepository extends JpaRepository<InfoMes, Long> {

    public Optional<InfoMes> findById(Long id);

    public List<InfoMes> findByProduto(Produto produto);

    public List<InfoMes> findByMonthYear(Date monthYear);

    public List<InfoMes> findByMonthYearBetween(Date start, Date end);

    List<InfoMes> findByProdutoAndMonthYearBetween(Produto produto, Date inicio, Date fim);

    @Query("SELECT i FROM InfoMes i WHERE i.produto = :produto AND FUNCTION('MONTH', i.monthYear) = :month AND FUNCTION('YEAR', i.monthYear) = :year")
    public Optional<InfoMes> findByProdutoAndMonthYear(Produto produto, @Param("month") int month, @Param("year") int year);

    void deleteById(Long id);
}
