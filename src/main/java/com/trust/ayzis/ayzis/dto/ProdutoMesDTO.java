package com.trust.ayzis.ayzis.dto;

import java.sql.Date;

public class ProdutoMesDTO {
    private String produtoId;
    private Date data;
    private int vendaIndividual;
    private int vendaComposta;
    private int vendaDireta;
    private int vendaTotal;
    private int cancelamentoIndividual;
    private int cancelamentoComposta;
    private int cancelamentoDireta;
    private int cancelamentoTotal;
    private int pendenteIndividual;
    private int pendenteComposta;
    private int pendenteDireta;
    private int pendenteTotal;


    public int getVendaIndividual() {
        return this.vendaIndividual;
    }

    public void setVendaIndividual(int vendaIndividual) {
        this.vendaIndividual = vendaIndividual;
    }

    public int getVendaComposta() {
        return this.vendaComposta;
    }

    public void setVendaComposta(int vendaComposta) {
        this.vendaComposta = vendaComposta;
    }

    public int getVendaDireta() {
        return this.vendaDireta;
    }

    public void setVendaDireta(int vendaDireta) {
        this.vendaDireta = vendaDireta;
    }

    public int getVendaTotal() {
        return this.vendaTotal;
    }

    public void setVendaTotal(int vendaTotal) {
        this.vendaTotal = vendaTotal;
    }

    public int getCancelamentoIndividual() {
        return this.cancelamentoIndividual;
    }

    public void setCancelamentoIndividual(int cancelamentoIndividual) {
        this.cancelamentoIndividual = cancelamentoIndividual;
    }

    public int getCancelamentoComposta() {
        return this.cancelamentoComposta;
    }

    public void setCancelamentoComposta(int cancelamentoComposta) {
        this.cancelamentoComposta = cancelamentoComposta;
    }

    public int getCancelamentoDireta() {
        return this.cancelamentoDireta;
    }

    public void setCancelamentoDireta(int cancelamentoDireta) {
        this.cancelamentoDireta = cancelamentoDireta;
    }

    public int getCancelamentoTotal() {
        return this.cancelamentoTotal;
    }

    public void setCancelamentoTotal(int cancelamentoTotal) {
        this.cancelamentoTotal = cancelamentoTotal;
    }

    public int getPendenteIndividual() {
        return this.pendenteIndividual;
    }

    public void setPendenteIndividual(int pendenteIndividual) {
        this.pendenteIndividual = pendenteIndividual;
    }

    public int getPendenteComposta() {
        return this.pendenteComposta;
    }

    public void setPendenteComposta(int pendenteComposta) {
        this.pendenteComposta = pendenteComposta;
    }

    public int getPendenteDireta() {
        return this.pendenteDireta;
    }

    public void setPendenteDireta(int pendenteDireta) {
        this.pendenteDireta = pendenteDireta;
    }

    public int getPendenteTotal() {
        return this.pendenteTotal;
    }

    public void setPendenteTotal(int pendenteTotal) {
        this.pendenteTotal = pendenteTotal;
    }

    public String getProdutoId() {
        return this.produtoId;
    }

    public void setProdutoId(String produtoId) {
        this.produtoId = produtoId;
    }

    public Date getData() {
        return this.data;
    }

    public void setData(Date data) {
        this.data = data;
    }

}
