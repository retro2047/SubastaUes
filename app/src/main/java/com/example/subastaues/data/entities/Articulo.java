package com.example.subastaues.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "articulos", foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id", childColumns = "vendedorId", onDelete = ForeignKey.CASCADE))
public class Articulo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;

    public String descripcion;

    public Double precioBase;

    public Double precioActual;

    public String estado;

    public int vendedorId;
    public String imagenUrl;


}
