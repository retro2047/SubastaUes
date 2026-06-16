package com.example.subastaues.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.subastaues.R;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.Articulo;

import java.util.Locale;

public class DetalleArticuloFragment extends Fragment {

    private static final String ARG_ARTICULO_ID = "articuloId";

    public static DetalleArticuloFragment newInstance(int articuloId) {
        DetalleArticuloFragment fragment = new DetalleArticuloFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICULO_ID, articuloId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalle_articulo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int articuloId = requireArguments().getInt(ARG_ARTICULO_ID);

        ImageView imgArticulo = view.findViewById(R.id.imgDetalleArticulo);
        TextView tvNombre = view.findViewById(R.id.tvDetalleNombre);
        TextView tvDescripcion = view.findViewById(R.id.tvDetalleDescripcion);
        TextView tvPrecioBase = view.findViewById(R.id.tvDetallePrecioBase);
        TextView tvPrecioActual = view.findViewById(R.id.tvDetallePrecioActual);
        Button btnPujar = view.findViewById(R.id.btnDetallePujar);

        // Observar el artículo con LiveData para actualizaciones en tiempo real
        SubastasDatabase.obtenerInstancia(requireContext())
                .articuloDao()
                .buscarPorId(articuloId)
                .observe(getViewLifecycleOwner(), articulo -> {
                    if (articulo == null) {
                        Toast.makeText(getContext(), "Artículo no encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tvNombre.setText(articulo.nombre);
                    tvDescripcion.setText(articulo.descripcion);
                    tvPrecioBase.setText(String.format(Locale.getDefault(), "$ %.2f", articulo.precioBase));
                    tvPrecioActual.setText(String.format(Locale.getDefault(), "$ %.2f", articulo.precioActual));

                    Glide.with(requireContext())
                            .load(articulo.imagenUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .centerCrop()
                            .into(imgArticulo);

                    btnPujar.setOnClickListener(v -> {
                        PujaDialogFragment dialog = PujaDialogFragment.newInstance(articulo);
                        dialog.show(getParentFragmentManager(), "PujaDialog");
                    });
                });
    }
}
