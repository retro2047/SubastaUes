package com.example.subastaues.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subastaues.R;
import com.example.subastaues.adapters.ArticuloAdapter;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.Articulo;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatalogoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArticuloAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalogo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerArticulos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ArticuloAdapter(new ArrayList<>(), new ArticuloAdapter.OnArticuloClickListener() {
            @Override
            public void onPujarClick(Articulo articulo) {
                PujaDialogFragment dialog = PujaDialogFragment.newInstance(articulo);

                dialog.setOnPujaExitosaListener(() -> cargarArticulos());
                dialog.show(getParentFragmentManager(), "PujaDialog");

            }

            @Override
            public void onItemClick(Articulo articulo) {

            }
        });
        recyclerView.setAdapter(adapter);
        cargarArticulos();
    }

    private void cargarArticulos() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Articulo> datos = SubastasDatabase
                    .obtenerInstancia(getContext())
                    .articuloDao()
                    .obtenerActivos();

            handler.post(() -> adapter.actualizarLista(datos));
        });
    }
}
