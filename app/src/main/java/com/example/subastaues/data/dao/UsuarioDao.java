package com.example.subastaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.subastaues.data.entities.Usuario;

@Dao
public interface UsuarioDao {
    @Insert
    void insert(Usuario usuario);

    @Query("SELECT * FROM usuario WHERE correo = :correo AND contraseña = :contraseña LIMIT 1")
    Usuario login(String correo, String contraseña);

    @Query("SELECT * FROM usuario WHERE id = :id")
    Usuario getUsuarioById(int id);

    @Query("SELECT * FROM usuario WHERE correo = :correo LIMIT 1")
    Usuario getUsuarioByCorreo(String correo);

    @Update
    void actualizar(Usuario usuario);

    @Delete
    void eliminar(Usuario usuario);
}
