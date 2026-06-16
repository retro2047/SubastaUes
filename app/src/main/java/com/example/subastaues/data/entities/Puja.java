package com.example.subastaues.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pujas")
public class Puja {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int usuarioId;

    public int articuloId;

    public Double monto;
    public Long timestamp;
}
