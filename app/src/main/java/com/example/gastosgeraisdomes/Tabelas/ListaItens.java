package com.example.gastosgeraisdomes.Tabelas;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lista_itens")
public class ListaItens {
    @PrimaryKey(autoGenerate = true)
    private int idLista;
    @ColumnInfo(name = "Titulo")
    private String titulo;
    @ColumnInfo(name = "dia")
    private String dia;
    @ColumnInfo(name = "total")
    private float valorTotal;
    @ColumnInfo(name = "gasto")
    private float valorGasto;
    @ColumnInfo(name = "restante")
    private float valorRestante;

    public ListaItens(String titulo, String dia, float valorTotal) {
        this.titulo = titulo;
        this.dia = dia;
        this.valorTotal = valorTotal;
    }

    public int getIdLista() {
        return idLista;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
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

    @Override
    public String toString() {
        return "Lista{" +
                "idLista=" + idLista +
                ", titulo='" + titulo + '\'' +
                ", dia='" + dia + '\'' +
                ", valorTotal=" + valorTotal +
                ", valorGasto=" + valorGasto +
                ", valorRestante=" + valorRestante +
                '}';
    }
}
