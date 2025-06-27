package com.trust.ayzis.ayzis.model;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Venda {
    @Id
    private String id;

    private Date dataVenda;
    private String status;
    private String descStatus;
    private Integer quantidade;
    private Double valorTotal;
    private String origem;
    private String vendedor;

    @ManyToOne
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "info_mes_id")
    @JsonBackReference
    private InfoMes infoMes;

    // Getters and Setters

    public Venda() {
    }

    public Venda(Date dataVenda, String status, String descStatus, Integer quantidade, Double valorTotal,
            String origem,
            Produto produto, String vendedor) {
        this.dataVenda = dataVenda;
        this.status = status;
        this.descStatus = descStatus;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.origem = origem;
        this.produto = produto;
        this.vendedor = vendedor;
    }

    public Venda(String id, Date dataVenda, String status, String descStatus, Integer quantidade, Double valorTotal,
            String origem,
            Produto produto) {
        this.id = id;
        this.dataVenda = dataVenda;
        this.status = status;
        this.descStatus = descStatus;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.origem = origem;
        this.produto = produto;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDataVenda() {
        return this.dataVenda;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescStatus() {
        return this.descStatus;
    }

    public void setDescStatus(String descStatus) {
        this.descStatus = descStatus;
    }

    public Integer getQuantidade() {
        return this.quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorTotal() {
        return this.valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getOrigem() {
        return this.origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Produto getProduto() {
        return this.produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public InfoMes getInfoMes() {
        return infoMes;
    }

    public void setInfoMes(InfoMes infoMes) {
        this.infoMes = infoMes;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }
}
