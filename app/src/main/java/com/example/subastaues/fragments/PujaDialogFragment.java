package com.example.subastaues.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.subastaues.R;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.Articulo;
import com.example.subastaues.data.entities.Puja;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PujaDialogFragment extends DialogFragment {
    
    public interface OnPujaExitosaListener {
        void onPujaRealizada();
    }

    private static final String ARG_ARTICULO_ID = "articuloId";
    private static final String ARG_ARTICULO_NOMBRE = "articuloNombre";
    private static final String ARG_PRECIO_ACTUAL = "precioActual";

    private OnPujaExitosaListener listener;

    public static PujaDialogFragment newInstance(Articulo articulo) {
        PujaDialogFragment fragment = new PujaDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICULO_ID, articulo.id);
        args.putString(ARG_ARTICULO_NOMBRE, articulo.nombre);
        args.putDouble(ARG_PRECIO_ACTUAL, articulo.precioActual);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPujaExitosaListener(OnPujaExitosaListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = requireArguments();
        String nombre = args.getString(ARG_ARTICULO_NOMBRE);
        double precioActual = args.getDouble(ARG_PRECIO_ACTUAL);

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_puja, null);

        TextView tvNombre = view.findViewById(R.id.tvNombreArticuloDialog);
        TextView tvPrecio = view.findViewById(R.id.tvPrecioActualDialog);

        tvNombre.setText(nombre);
        tvPrecio.setText(String.format(
                Locale.getDefault(), "Precio actual: $ %.2f", precioActual));

        return new AlertDialog.Builder(requireContext())
                .setTitle("Realizar Puja")
                .setView(view)
                .setPositiveButton("Pujar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dismiss())
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) return;

        double precioActual = requireArguments().getDouble(ARG_PRECIO_ACTUAL);
        int articuloId = requireArguments().getInt(ARG_ARTICULO_ID);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            EditText etMonto = dialog.findViewById(R.id.etMontoPuja);
            TextView tvError = dialog.findViewById(R.id.tvError);

            String input = etMonto.getText().toString().trim();

            if (input.isEmpty()) {
                tvError.setText("Ingresa un monto");
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            try {
                double montoPuja = Double.parseDouble(input);

                if (montoPuja <= precioActual) {
                    tvError.setText(String.format(
                            Locale.getDefault(), "El monto debe ser mayor al precio actual: $ %.2f", precioActual));
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                guardarPuja(articuloId, montoPuja);
            } catch (NumberFormatException e) {
                tvError.setText("Ingrese un monto válido");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void guardarPuja(int articuloId, double montoPuja) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            SubastasDatabase db = SubastasDatabase.obtenerInstancia(requireContext());

            //se inserta la nueva puja
            Puja nuevaPuja = new Puja();
            nuevaPuja.articuloId = articuloId;
            nuevaPuja.usuarioId = 1; 
            nuevaPuja.monto = montoPuja;
            nuevaPuja.timestamp = System.currentTimeMillis();
            db.pujaDao().insertar(nuevaPuja);

            //Actualiza el precio actual del articulo
            Articulo articulo = db.articuloDao().buscarPorIdSync(articuloId);
            if (articulo != null) {
                articulo.precioActual = montoPuja;
                db.articuloDao().actualizar(articulo);
            }

            handler.post(() -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Puja realizada con éxito", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onPujaRealizada();
                    dismiss();
                }
            });
        });
    }
}
