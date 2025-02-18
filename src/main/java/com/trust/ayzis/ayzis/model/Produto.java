package com.trust.ayzis.ayzis.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Produto {
    @Id
    private String id; // Código SKU

    private String nome;
    private String descricao;
    private String marca;
    private String tipo;
    private String condicao;
    private double preco;
    private double largura;
    private double altura;
    private double profundidade;
    private double peso;
    private String unidade;
    private boolean composto;

    @OneToMany(mappedBy = "produtoComposto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Componentes> produtosComposicao;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Venda> vendas;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private InfoMes infoMes;

    public Produto() {
    }

    public Produto(String id, String nome, String descricao, String marca, String tipo, String condicao, double preco,
            double largura, double altura, double profundidade, double peso, String unidade, boolean composto,
            List<Componentes> produtosComposicao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.marca = marca;
        this.tipo = tipo;
        this.condicao = condicao;
        this.preco = preco;
        this.largura = largura;
        this.altura = altura;
        this.profundidade = profundidade;
        this.peso = peso;
        this.unidade = unidade;
        this.composto = composto;
        this.produtosComposicao = produtosComposicao;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getLargura() {
        return largura;
    }

    public void setLargura(double largura) {
        this.largura = largura;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getProfundidade() {
        return profundidade;
    }

    public void setProfundidade(double profundidade) {
        this.profundidade = profundidade;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public boolean isComposto() {
        return composto;
    }

    public void setComposto(boolean composto) {
        this.composto = composto;
    }

    public List<Componentes> getProdutosComposicao() {
        return produtosComposicao;
    }

    public void setProdutosComposicao(List<Componentes> produtosComposicao) {
        this.produtosComposicao = produtosComposicao;
        this.composto = (produtosComposicao != null && !produtosComposicao.isEmpty());
    }

    public List<Venda> getVendas() {
        return vendas;
    }

    public void setVendas(List<Venda> vendas) {
        this.vendas = vendas;
    }
}