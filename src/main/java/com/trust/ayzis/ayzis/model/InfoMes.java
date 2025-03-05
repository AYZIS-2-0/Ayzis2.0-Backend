package com.trust.ayzis.ayzis.model;

import java.sql.Date;
import java.util.ArrayList;
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

    private int individual = 0;
    private int componente = 0;
    private int direta = 0;
    private int total = 0;

    private int pendenteIndividual = 0;
    private int pendenteComponente = 0;
    private int pendenteTotal = 0;

    private int canceladoIndividual = 0;
    private int canceladoComponente = 0;
    private int canceladoTotal = 0;

    @ManyToOne
    private Produto produto;

    @OneToMany(mappedBy = "infoMes", orphanRemoval = true)
    @JsonManagedReference
    private List<Venda> vendaDireta = new ArrayList<>();

    @OneToMany(mappedBy = "infoMes", orphanRemoval = true)
    @JsonManagedReference
    private List<Venda> vendaConluida = new ArrayList<>();

    @OneToMany(mappedBy = "infoMes", orphanRemoval = true)
    @JsonManagedReference
    private List<Venda> vendaPendente = new ArrayList<>();

    @OneToMany(mappedBy = "infoMes", orphanRemoval = true)
    @JsonManagedReference
    private List<Venda> vendaCancelada = new ArrayList<>();

    public InfoMes() {
    }

    public InfoMes(Date monthYear, int individual, int componente, int direta, int total, int pendenteIndividual,
            int pendenteComponente, int pendenteTotal, int canceladoIndividual, int canceladoComponente,
            int canceladoTotal, Produto produto, List<Venda> venda, List<Venda> vendaPendente,
            List<Venda> vendaCancelada, List<Venda> vendaDireta) {
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
        this.vendaConluida = new ArrayList<>(venda);
        this.vendaPendente = new ArrayList<>(venda);
        this.vendaCancelada = new ArrayList<>(venda);
        this.vendaDireta = new ArrayList<>(venda);
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

    public List<Venda> getVendasConcluidas() {
        return this.vendaConluida;
    }

    public void setVendasConcluidas(List<Venda> venda) {
        this.vendaConluida = venda;
    }

    public List<Venda> getVendasPendentes() {
        return this.vendaPendente;
    }

    public void setVendasPendentes(List<Venda> venda) {
        this.vendaPendente = venda;
    }

    public List<Venda> getVendasCanceladas() {
        return this.vendaCancelada;
    }

    public void setVendasCanceladas(List<Venda> venda) {
        this.vendaCancelada = venda;
    }

    public List<Venda> getVendasDiretas() {
        return this.vendaDireta;
    }

    public void setVendasDiretas(List<Venda> venda) {
        this.vendaDireta = venda;
    }
}
