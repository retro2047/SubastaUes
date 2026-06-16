package com.example.subastaues.data;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.subastaues.data.dao.UsuarioDao;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsuarioRepository {

    private final UsuarioDao usuarioDao;
    private final ExecutorService executor;
    private final Handler mainThreadHandler;

    public interface Callback<T> {
        void onResult(T result);
    }

    public UsuarioRepository(Application application) {
        SubastasDatabase db = SubastasDatabase.obtenerInstancia(application);
        usuarioDao = db.usuarioDao();
        executor = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void insertar(Usuario usuario, Callback<Void> callback) {
        executor.execute(() -> {
            usuarioDao.insert(usuario);
            mainThreadHandler.post(() -> {
                if (callback != null) {
                    callback.onResult(null);
                }
            });
        });
    }

    public void login(String correo, String password, Callback<Usuario> callback) {
        executor.execute(() -> {
            Usuario usuario = usuarioDao.login(correo, password);
            mainThreadHandler.post(() -> {
                if (callback != null) {
                    callback.onResult(usuario);
                }
            });
        });
    }

    public void buscarPorCorreo(String correo, Callback<Usuario> callback) {
        executor.execute(() -> {
            Usuario usuario = usuarioDao.getUsuarioByCorreo(correo);
            mainThreadHandler.post(() -> {
                if (callback != null) {
                    callback.onResult(usuario);
                }
            });
        });
    }
}
