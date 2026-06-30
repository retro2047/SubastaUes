package com.example.subastaues.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subastaues.R;
import com.example.subastaues.adapters.MisArticulosAdapter;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.Articulo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MisArticulosFragment extends Fragment {

    private RecyclerView recyclerView;
    private MisArticulosAdapter adapter;
    private TextView tvSinArticulos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_articulos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerMisArticulos);
        tvSinArticulos = view.findViewById(R.id.tvSinArticulos);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MisArticulosAdapter(null, new MisArticulosAdapter.OnAccionArticuloListener() {
            @Override
            public void onEditar(Articulo articulo) {
                EditarArticuloDialogFragment dialog = EditarArticuloDialogFragment.newInstance(articulo.id);

                dialog.setOnArticulosActualizadoListener(() -> cargarMisArticulos());
                dialog.show(getParentFragmentManager(), "EditarDialog");
            }

            @Override
            public void onEliminar(Articulo articulo) {
                confirmarEliminar(articulo);
            }
        });

        recyclerView.setAdapter(adapter);
        cargarMisArticulos();
    }

    private void cargarMisArticulos() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int usuarioId = prefs.getInt("user_id", 0);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        if (usuarioId == 0) {
            return;
        }

        executor.execute(() -> {
            List<Articulo> articulos = SubastasDatabase
                    .obtenerInstancia(getContext())
                    .articuloDao()
                    .obtenerPorVendedor(usuarioId);

            handler.post(() -> {
                if (articulos.isEmpty()) {
                    tvSinArticulos.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvSinArticulos.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.actualizarLista(articulos);
                }
            });
        });
        executor.shutdown();
    }

    private void confirmarEliminar(Articulo articulo) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_eliminar_articulo)
                .setMessage(getString(R.string.msg_confirmar_eliminacion, articulo.nombre))
                .setPositiveButton(R.string.btn_eliminar, (dialog, which) -> {
                    eliminarArticulo(articulo);
                })
                .setNegativeButton(R.string.btn_cancelar, null)
                .show();
    }

    private void eliminarArticulo(Articulo articulo) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            int cantidadPujas = SubastasDatabase.obtenerInstancia(requireContext())
                    .pujaDao()
                    .contarPujasPorArticulo(articulo.id);

            if (cantidadPujas > 0) {
                handler.post(() -> {
                    Toast.makeText(getContext(), "No se puede eliminar que ya esta en subasta.", Toast.LENGTH_SHORT).show();
                });
            } else {
                SubastasDatabase.obtenerInstancia(getContext())
                        .articuloDao()
                        .eliminar(articulo);

                handler.post(() -> {
                    Toast.makeText(getContext(), R.string.msg_articulo_eliminado, Toast.LENGTH_SHORT).show();
                    cargarMisArticulos();
                });
            }
        });
        executor.shutdown();
    }
}
