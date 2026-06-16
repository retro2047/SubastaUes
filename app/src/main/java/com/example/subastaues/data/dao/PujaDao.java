package com.example.subastaues.data.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.subastaues.data.entities.Puja;

import java.util.List;

@Dao
public interface PujaDao {
    @Insert
    void insertar(Puja puja);

    @Query("SELECT * FROM pujas WHERE articuloId = :articuloId ORDER BY monto DESC")
    List<Puja> obtenerPorArticulo(int articuloId);

    @Query("SELECT MAX(monto) FROM pujas WHERE articuloId = :articuloId")
    double obtenerMejorPuja(int articuloId);
}
