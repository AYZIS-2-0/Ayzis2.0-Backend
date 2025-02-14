package com.trust.ayzis.ayzis.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;


@Repository
public interface IInfoMesRepository extends JpaRepository<InfoMes, Integer> {
   
    public Optional<InfoMes> findById(int id);
    
    public List<InfoMes> findByProduto(Produto produto);

    public List<InfoMes> findByMonthYear(Date monthYear);

    public List<InfoMes> findByMonthYearBetween(Date start, Date end);

    public List<InfoMes> findByProdutoAndMonthYearBetween(Produto produto, Date start, Date end);

    public void deleteByID(int id);
}
