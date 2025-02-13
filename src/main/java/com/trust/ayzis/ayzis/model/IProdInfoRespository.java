package com.trust.ayzis.ayzis.model;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.micrometer.common.lang.NonNull;

public interface IProdInfoRespository extends JpaRepository<ProdInfo, Integer> {
    
    public Optional<ProdInfo> findById(String id);

    public List<ProdInfo> findByProduto(Produto produto);

    public List<ProdInfo> findByMesAno(Date mesAno);

    public Optional<ProdInfo> findByProdutoAndMesAno(Produto produto, Date mesAno);

    public void deleteById(@NonNull String id);
}
