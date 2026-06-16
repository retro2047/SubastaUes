package com.example.subastaues.data.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.subastaues.data.dao.ArticuloDao;
import com.example.subastaues.data.dao.PujaDao;
import com.example.subastaues.data.dao.UsuarioDao;
import com.example.subastaues.data.entities.Articulo;
import com.example.subastaues.data.entities.Puja;
import com.example.subastaues.data.entities.Usuario;

@Database(
        entities = {Usuario.class, Articulo.class, Puja.class},
        version = 5,
        exportSchema = false
)
public abstract class SubastasDatabase extends RoomDatabase {

    private static volatile SubastasDatabase instancia;

    public abstract UsuarioDao usuarioDao();
    public abstract ArticuloDao articuloDao();
    public abstract PujaDao pujaDao();

    public static synchronized SubastasDatabase obtenerInstancia(Context contexto) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(
                    contexto.getApplicationContext(),
                    SubastasDatabase.class,
                    "subastas_db"
            ).fallbackToDestructiveMigration().build();
        }
        return instancia;
    }
}
