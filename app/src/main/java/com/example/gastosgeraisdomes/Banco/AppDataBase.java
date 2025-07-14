package com.example.gastosgeraisdomes.Banco;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.gastosgeraisdomes.Tabelas.ItenLista;
import com.example.gastosgeraisdomes.Tabelas.ItenListaDao;
import com.example.gastosgeraisdomes.Tabelas.ListaDao;
import com.example.gastosgeraisdomes.Tabelas.ListaItens;
import com.example.gastosgeraisdomes.Telas.Lista;


@Database(entities = {ListaItens.class, ItenLista.class},version = 1)//Almentar as vesoes se colocar mais classes
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase INSTANCE;

    public static AppDataBase getDataBase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "GerenciaGastos")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }


    public abstract ListaDao listaDao();
    public abstract ItenListaDao itenListaDao();
}