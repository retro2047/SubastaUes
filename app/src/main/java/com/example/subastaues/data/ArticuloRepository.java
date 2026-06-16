package com.example.subastaues.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.subastaues.data.dao.ArticuloDao;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.Articulo;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArticuloRepository {

    private final ArticuloDao articuloDao;
    private final LiveData<List<Articulo>> articulosActivos;
    private final ExecutorService executor;

    public ArticuloRepository(Application application) {
        SubastasDatabase db = SubastasDatabase.obtenerInstancia(application);
        articuloDao = db.articuloDao();
        articulosActivos = articuloDao.obtenerActivos();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Articulo>> obtenerActivos() {
        return articulosActivos;
    }

    public LiveData<Articulo> buscarPorId(int id) {
        return articuloDao.buscarPorId(id);
    }

    public void insertar(Articulo articulo) {
        executor.execute(() -> articuloDao.insertar(articulo));
    }

    public void actualizar(Articulo articulo) {
        executor.execute(() -> articuloDao.actualizar(articulo));
    }

    public void eliminar(Articulo articulo) {
        executor.execute(() -> articuloDao.eliminar(articulo));
    }
}