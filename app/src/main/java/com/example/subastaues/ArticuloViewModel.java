package com.example.subastaues;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.subastaues.data.ArticuloRepository;
import com.example.subastaues.data.entities.Articulo;
import java.util.List;

public class ArticuloViewModel extends AndroidViewModel {

    private final ArticuloRepository repository;
    private final LiveData<List<Articulo>> articulosActivos;

    public ArticuloViewModel(Application application) {
        super(application);
        repository = new ArticuloRepository(application);
        articulosActivos = repository.obtenerActivos();
    }

    public LiveData<List<Articulo>> obtenerActivos() {
        return articulosActivos;
    }

    public LiveData<Articulo> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public void insertar(Articulo articulo) {
        repository.insertar(articulo);
    }

    public void actualizar(Articulo articulo) {
        repository.actualizar(articulo);
    }

    public void eliminar(Articulo articulo) {
        repository.eliminar(articulo);
    }
}