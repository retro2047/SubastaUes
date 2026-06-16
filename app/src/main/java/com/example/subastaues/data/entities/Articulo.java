package com.example.subastaues.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "articulos")
public class Articulo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;

    public String descripcion;

    public double precioBase;

    public double precioActual;

    public int estado;

    public int VendedorId;


}
