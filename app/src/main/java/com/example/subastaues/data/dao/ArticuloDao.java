package com.example.subastaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.subastaues.data.entities.Articulo;

import java.util.List;

@Dao
public interface ArticuloDao {
    @Insert
    void insertar(Articulo articulo);

    @Query("SELECT * FROM articulos WHERE estado = 'activo'")
    List<Articulo> obtenerActivos();

    @Query("SELECT * FROM articulos WHERE id = :id")
    Articulo buscarPorId(int id);

    @Update
    void actualizar(Articulo articulo);

    @Delete
    void eliminar(Articulo articulo);
}
