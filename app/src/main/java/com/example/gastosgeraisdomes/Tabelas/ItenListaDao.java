package com.example.gastosgeraisdomes.Tabelas;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gastosgeraisdomes.Telas.Lista;

import java.util.List;

@Dao
public interface ItenListaDao {
    @Query("SELECT * FROM iten")
    List<ItenLista> getALL();

    @Query("SELECT estabelecimento FROM iten")
    List<String> getALLEsta();

    @Query("SELECT funcao FROM iten")
    List<String> getALLFunc();

    @Query("SELECT valor FROM iten")
    List<Float> getALLVAalor();

    @Query("SELECT * FROM iten WHERE estabelecimento = :estab")
    List<ItenLista> BuscasEstab(String estab);
    @Query("SELECT EXISTS(SELECT 1 FROM iten WHERE estabelecimento = :estab)")
    boolean BuscaIten(String estab);

    @Query("SELECT COUNT(*) FROM iten")
    int Quantos();

    @Query("SELECT * FROM iten WHERE idIten = :id")
    ItenLista Busca(int id);

    @Insert
    void insertAll(ItenLista... itenListas);

    @Update
    void upgrade(ItenLista itenListas);

    @Delete
    void delete(ItenLista itenListas);

    @Query("DELETE FROM iten")
    void deletarTodos();
}
