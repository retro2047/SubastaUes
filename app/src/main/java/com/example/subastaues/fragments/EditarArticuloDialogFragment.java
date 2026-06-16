package com.example.subastaues.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.subastaues.R;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.Articulo;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditarArticuloDialogFragment extends DialogFragment {

    public interface OnArticulosActualizadoListener {
        void onActualizado();
    }

    private static final String ARG_ARTICULO_ID = "articuloId";
    private OnArticulosActualizadoListener listener;
    private EditText etNombre, etDescripcion, etPrecio;

    public static EditarArticuloDialogFragment newInstance(int articuloId) {
        EditarArticuloDialogFragment fragment = new EditarArticuloDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICULO_ID, articuloId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnArticulosActualizadoListener(OnArticulosActualizadoListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int articuloId = requireArguments().getInt(ARG_ARTICULO_ID);
        Context context = requireContext();

        View view = getLayoutInflater().inflate(R.layout.dialog_editar_articulo, null);
        etNombre = view.findViewById(R.id.etEditarNombre);
        etDescripcion = view.findViewById(R.id.etEditarDescripcion);
        etPrecio = view.findViewById(R.id.etEditarPrecio);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Articulo articulo = SubastasDatabase.obtenerInstancia(context).articuloDao().buscarPorIdSync(articuloId);

            handler.post(() -> {
                if (articulo != null && getContext() != null) {
                    etNombre.setText(articulo.nombre != null ? articulo.nombre : "");
                    etDescripcion.setText(articulo.descripcion != null ? articulo.descripcion : "");
                    
                    if (articulo.precioBase != null) {
                        etPrecio.setText(String.format(Locale.getDefault(), "$ %.2f", articulo.precioBase));
                    } else {
                        etPrecio.setText("$ 0.00");
                    }
                }
            });
            executor.shutdown();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Guardar Cambios", null)
                .setNegativeButton("Cancelar", null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        int articuloId = requireArguments().getInt(ARG_ARTICULO_ID);
        Context context = requireContext();

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                String nombre = etNombre.getText().toString().trim();
                String descripcion = etDescripcion.getText().toString().trim();

                if (nombre.isEmpty() || descripcion.isEmpty()) {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Deshabilitar el botón para evitar múltiples clics
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    Articulo articulo = SubastasDatabase
                            .obtenerInstancia(context)
                            .articuloDao()
                            .buscarPorIdSync(articuloId);

                    if (articulo != null) {
                        articulo.nombre = nombre;
                        articulo.descripcion = descripcion;

                        SubastasDatabase.obtenerInstancia(context)
                                .articuloDao()
                                .actualizar(articulo);

                        handler.post(() -> {
                            if (isAdded()) {
                                Toast.makeText(context, "Artículo actualizado con éxito", Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onActualizado();
                                dismiss();
                            }
                        });
                    } else {
                        handler.post(() -> {
                            if (isAdded()) {
                                Toast.makeText(context, "Error: El artículo ya no existe", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        });
                    }
                    executor.shutdown();
                });
            });
        }
    }
}
