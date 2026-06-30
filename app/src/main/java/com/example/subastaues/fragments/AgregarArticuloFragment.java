package com.example.subastaues.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.subastaues.R;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.Articulo;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgregarArticuloFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agregar_articulo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etNombre = view.findViewById(R.id.etNombreArticulo);
        EditText etDescripcion = view.findViewById(R.id.etDescripcionArticulo);
        EditText etPrecioBase = view.findViewById(R.id.etPrecioBase);
        EditText etImagenUrl = view.findViewById(R.id.etImagenUrl);
        Button btnGuardar = view.findViewById(R.id.btnGuardarArticulo);
        TextView tvError = view.findViewById(R.id.tvErrorArticulo);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String precioStr = etPrecioBase.getText().toString().trim();
            String imagenUrl = etImagenUrl.getText().toString().trim();

            if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
                tvError.setText("Completa todos los campos obligatorios");
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            double precioBase;
            try {
                precioBase = Double.parseDouble(precioStr);
            } catch (NumberFormatException e) {
                tvError.setText("Ingrese un precio válido");
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            tvError.setVisibility(View.GONE);

            //Recuperamos la sesion
            SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            int usuarioId = prefs.getInt("user_id", 0);

            if (usuarioId == 0) {
                Toast.makeText(getContext(), "Error: no hay session activa", Toast.LENGTH_SHORT).show();
                return;
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                Articulo nuevo = new Articulo();
                nuevo.nombre = nombre;
                nuevo.descripcion = descripcion;
                nuevo.precioBase = precioBase;
                nuevo.precioActual = precioBase;
                nuevo.estado = "activo";
                nuevo.vendedorId = usuarioId;
                nuevo.imagenUrl = imagenUrl.isEmpty() ? "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400"
                        : imagenUrl;

                SubastasDatabase.obtenerInstancia(requireContext())
                        .articuloDao()
                        .insertar(nuevo);

                handler.post(() -> {
                    Toast.makeText(getContext(), "Artículo agregado con éxito", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                });
            });
            executor.shutdown();
        });
    }
}
