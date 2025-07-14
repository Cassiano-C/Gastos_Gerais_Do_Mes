package com.example.gastosgeraisdomes.Tabelas;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ListaDao {
    @Query("SELECT * FROM lista_itens")
    List<ListaItens> getALL();

    @Query("SELECT EXISTS(SELECT 1 FROM lista_itens)")
    boolean ExisteLista();

    @Query("SELECT total FROM lista_itens")
    float Total();

    @Query("SELECT gasto FROM lista_itens")
    float Gasto();

    @Query("SELECT restante FROM lista_itens")
    float Restante();

    @Insert
    void insertAll(ListaItens... listaItens);

    @Update
    void upgrade(ListaItens listaItens);

    @Delete
    void delete(ListaItens listaItens);
    @Query("DELETE FROM lista_itens")
    void deletarTodos();
}
