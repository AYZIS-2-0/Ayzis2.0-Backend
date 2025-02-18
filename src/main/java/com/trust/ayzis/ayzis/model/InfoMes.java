package com.trust.ayzis.ayzis.model;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class InfoMes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date monthYear;

    private int individual;
    private int componente;
    private int direta;
    private int total;

    private int pendenteIndividual;
    private int pendenteComponente;
    private int pendenteTotal;

    private int canceladoIndividual;
    private int canceladoComponente;
    private int canceladoTotal;

    @ManyToOne
    @JsonManagedReference
    private Produto produto;

    @OneToMany(mappedBy = "infoMes", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Venda> venda;

    public InfoMes() {
    }

    public InfoMes(Date monthYear, int individual, int componente, int direta, int total, int pendenteIndividual,
            int pendenteComponente, int pendenteTotal, int canceladoIndividual, int canceladoComponente,
            int canceladoTotal, Produto produto, List<Venda> venda) {
        this.monthYear = monthYear;
        this.individual = individual;
        this.componente = componente;
        this.direta = direta;
        this.total = total;
        this.pendenteIndividual = pendenteIndividual;
        this.pendenteComponente = pendenteComponente;
        this.pendenteTotal = pendenteTotal;
        this.canceladoIndividual = canceladoIndividual;
        this.canceladoComponente = canceladoComponente;
        this.canceladoTotal = canceladoTotal;
        this.produto = produto;
        this.venda = venda;
    }

    // Getters and Setters


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getMonthYear() {
        return this.monthYear;
    }

    public void setMonthYear(Date monthYear) {
        this.monthYear = monthYear;
    }

    public int getIndividual() {
        return this.individual;
    }

    public void setIndividual(int individual) {
        this.individual = individual;
    }

    public int getComponente() {
        return this.componente;
    }

    public void setComponente(int componente) {
        this.componente = componente;
    }

    public int getDireta() {
        return this.direta;
    }

    public void setDireta(int direta) {
        this.direta = direta;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPendenteIndividual() {
        return this.pendenteIndividual;
    }

    public void setPendenteIndividual(int pendenteIndividual) {
        this.pendenteIndividual = pendenteIndividual;
    }

    public int getPendenteComponente() {
        return this.pendenteComponente;
    }

    public void setPendenteComponente(int pendenteComponente) {
        this.pendenteComponente = pendenteComponente;
    }

    public int getPendenteTotal() {
        return this.pendenteTotal;
    }

    public void setPendenteTotal(int pendenteTotal) {
        this.pendenteTotal = pendenteTotal;
    }

    public int getCanceladoIndividual() {
        return this.canceladoIndividual;
    }

    public void setCanceladoIndividual(int canceladoIndividual) {
        this.canceladoIndividual = canceladoIndividual;
    }

    public int getCanceladoComponente() {
        return this.canceladoComponente;
    }

    public void setCanceladoComponente(int canceladoComponente) {
        this.canceladoComponente = canceladoComponente;
    }

    public int getCanceladoTotal() {
        return this.canceladoTotal;
    }

    public void setCanceladoTotal(int canceladoTotal) {
        this.canceladoTotal = canceladoTotal;
    }

    public Produto getProduto() {
        return this.produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<Venda> getVenda() {
        return this.venda;
    }

    public void setVenda(List<Venda> venda) {
        this.venda = venda;
    }
}
