package com.example.subastaues.data.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.subastaues.data.entities.Puja;
import com.example.subastaues.data.entities.PujaConArticulo;

import java.util.List;

@Dao
public interface PujaDao {
    @Insert
    void insertar(Puja puja);

    @Query("SELECT * FROM pujas WHERE articuloId = :articuloId ORDER BY monto DESC")
    List<Puja> obtenerPorArticulo(int articuloId);

    @Query("SELECT MAX(monto) FROM pujas WHERE articuloId = :articuloId")
    Double obtenerMejorPuja(int articuloId);

    @Query("SELECT p.monto, p.timestamp, a.nombre AS nombreArticulo " +
           "FROM pujas p " +
           "INNER JOIN articulos a ON p.articuloId = a.id " +
           "WHERE p.usuarioId = :usuarioId " +
           "ORDER BY p.timestamp DESC")
    LiveData<List<PujaConArticulo>> obtenerPujasConArticulo(int usuarioId);
}
