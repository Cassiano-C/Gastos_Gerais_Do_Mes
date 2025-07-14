package com.example.gastosgeraisdomes.Tabelas;

import java.util.List;

public class BackupMensal {
    private String titulo;
    private String dia;
    private float valorTotal;
    private float valorGasto;
    private float valorRestante;
    private List<String> estabelecimento;
    private List<String> funcao;
    private List<Float> valor;

    public BackupMensal(String titulo, String dia, float valorTotal, float valorGasto, float valorRestante, List<String> estabelecimento, List<String> funcao, List<Float> valor) {
        this.titulo = titulo;
        this.dia = dia;
        this.valorTotal = valorTotal;
        this.valorGasto = valorGasto;
        this.valorRestante = valorRestante;
        this.estabelecimento = estabelecimento;
        this.funcao = funcao;
        this.valor = valor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public float getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public float getValorGasto() {
        return valorGasto;
    }

    public void setValorGasto(float valorGasto) {
        this.valorGasto = valorGasto;
    }

    public float getValorRestante() {
        return valorRestante;
    }

    public void setValorRestante(float valorRestante) {
        this.valorRestante = valorRestante;
    }

    public List<String> getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(List<String> estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public List<String> getFuncao() {
        return funcao;
    }

    public void setFuncao(List<String> funcao) {
        this.funcao = funcao;
    }

    public List<Float> getValor() {
        return valor;
    }

    public void setValor(List<Float> valor) {
        this.valor = valor;
    }
}
