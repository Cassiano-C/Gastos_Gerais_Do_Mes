package com.example.gastosgeraisdomes.Tabelas;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "iten")
public class ItenLista {

    @PrimaryKey(autoGenerate = true)
    private int idIten;
    @ColumnInfo(name = "estabelecimento")
    private String estabelecimento;
    @ColumnInfo(name = "funcao")
    private String funcao;
    @ColumnInfo(name = "valor")
    private float valor;


    public ItenLista(String estabelecimento, String funcao, float valor) {
        this.estabelecimento = estabelecimento;
        this.funcao = funcao;
        this.valor = valor;
    }

    public int getIdIten() {
        return idIten;
    }

    public void setIdIten(int idIten) {
        this.idIten = idIten;
    }

    public String getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return  estabelecimento + " : " + valor +'$';
    }
}
