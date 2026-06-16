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
    //- Interfaz para notificar al fragmento del resultado de la puja
    public interface OnPujaExitosaListener {
        void onPujaRealizada();
    }

    private static final String ARG_ARTICULO_ID = "articuloId";
    private static final String ARG_ARTICULO_NOMBRE = "articuloNombre";
    private static final String ARG_PRECIO_ACTUAL = "precioActual";

    private OnPujaExitosaListener listener;

    // factory para crea el dialogo con datos}
    public static PujaDialogFragment newInstance(Articulo articulo) {
        PujaDialogFragment fragment = new PujaDialogFragment();
        Bundle args = new Bundle();
        args.putInt("articuloId", articulo.id);
        args.putString("articuloNombre", articulo.nombre);
        args.putDouble("precioActual", articulo.precioActual);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPujaExitosaListener(OnPujaExitosaListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Recuperar datos del artículo
        int articuloId = getArguments().getInt("articuloId");
        String nombre = getArguments().getString("articuloNombre");
        double precioActual = getArguments().getDouble("precioActual");

        // infla el layout personalziado
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_puja, null);

        //referecnia a la vistas
        TextView tvNombre = view.findViewById(R.id.tvNombreArticuloDialog);
        TextView tvPrecio = view.findViewById(R.id.tvPrecioActualDialog);
        EditText etMonto = view.findViewById(R.id.etMontoPuja);
        TextView tvError = view.findViewById(R.id.tvError);

        // asigancion de los datos
        tvNombre.setText(nombre);
        tvPrecio.setText(String.format(
                Locale.getDefault(), "Precio actual: $ %.2f", precioActual));

        // Construcion de alertDialog
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

        //Sobreescribir el click del boton pujar para validar antes de cerrar
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) return;

        double precioActual = getArguments().getDouble("precioActual");
        int articuloId = getArguments().getInt("articuloId");

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            EditText etMonto = dialog.findViewById(R.id.etMontoPuja);
            TextView tvError = dialog.findViewById(R.id.tvError);

            String input = etMonto.getText().toString().trim();

            //validacion 1: campo vacio
            if (input.isEmpty()) {
                tvError.setText("Ingresa un monto");
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            double montoPuja = Double.parseDouble(input);

            //validacion 2: monto menor al precio actual
            if (montoPuja <= precioActual) {
                tvError.setText(String.format(
                        Locale.getDefault(), "El monto debe ser mayor al precio actual: $ %.2f", precioActual));
                tvError.setVisibility(View.VISIBLE);
                return;
            }
            guardarPuja(articuloId, montoPuja);
        });
    }

    private void guardarPuja(int articuloId, double montoPuja) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            SubastasDatabase db = SubastasDatabase.obtenerInstancia(getContext());

            //se inserta la nueva puja
            Puja nuevaPuja = new Puja();
            nuevaPuja.articuloId = articuloId;
            nuevaPuja.usuarioId = 1; // Suponiendo que el usuario está logeado
            nuevaPuja.monto = montoPuja;
            nuevaPuja.timestamp = System.currentTimeMillis();
            db.pujaDao().insertar(nuevaPuja);

            //Actualiza el precio actual del articulo
            Articulo articulo = db.articuloDao().buscarPorId(articuloId);
            articulo.precioActual = montoPuja;
            db.articuloDao().actualizar(articulo);

            //notfica al fragment para refrescar la lista
            handler.post(() -> {
                if (listener != null) listener.onPujaRealizada();
                dismiss();
            });
        });
    }
}
