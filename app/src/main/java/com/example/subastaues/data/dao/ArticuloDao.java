package com.example.subastaues.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.subastaues.data.entities.Articulo;

import java.util.List;

@Dao
public interface ArticuloDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(Articulo articulo);

    @Query("SELECT * FROM articulos WHERE estado = 'activo'")
    LiveData<List<Articulo>> obtenerActivos();

    @Query("SELECT * FROM articulos")
    LiveData<List<Articulo>> obtenerTodos();

    @Query("SELECT * FROM articulos WHERE id = :id")
    LiveData<Articulo> buscarPorId(int id);

    @Query("SELECT * FROM articulos WHERE id = :id")
    Articulo buscarPorIdSync(int id);

    @Update
    void actualizar(Articulo articulo);

    @Delete
    void eliminar(Articulo articulo);
}
