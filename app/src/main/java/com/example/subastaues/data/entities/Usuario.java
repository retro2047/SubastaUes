package com.example.subastaues.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "usuario")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "correo")
    public String correo;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "latitud")
    public double latitud;

    @ColumnInfo(name = "longitud")
    public double longitud;

    @ColumnInfo(name = "ubicacion")
    public String ubicacion;
}
