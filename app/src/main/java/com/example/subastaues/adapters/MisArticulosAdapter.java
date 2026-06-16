package com.example.subastaues.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.subastaues.R;
import com.example.subastaues.data.entities.Articulo;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

public class MisArticulosAdapter extends RecyclerView.Adapter<MisArticulosAdapter.MisArticulosViewHolder> {

    public interface OnAccionArticuloListener {
        void onEditar(Articulo articulo);
        void onEliminar(Articulo articulo);
    }

    private List<Articulo> listaArticulos;
    private OnAccionArticuloListener listener;

    public MisArticulosAdapter(List<Articulo> listaArticulos, OnAccionArticuloListener listener) {
        this.listaArticulos = listaArticulos;
        this.listener = listener;
    }

    public static class MisArticulosViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMiArticulo;
        TextView tvNombre, tvPrecio;
        Button btnEditar, btnEliminar;

        public MisArticulosViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMiArticulo = itemView.findViewById(R.id.imgMiArticulo);
            tvNombre = itemView.findViewById(R.id.tvMiNombreArticulo);
            tvPrecio = itemView.findViewById(R.id.tvMiPrecioActual);
            btnEditar = itemView.findViewById(R.id.btnEditarMiArticulo);
            btnEliminar = itemView.findViewById(R.id.btnEliminarMiArticulo);
        }
    }

    @NonNull
    @Override
    public MisArticulosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mi_articulo, parent, false);
        return new MisArticulosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MisArticulosViewHolder holder, int position) {
        Articulo articulo = listaArticulos.get(position);
        holder.tvNombre.setText(articulo.nombre != null ? articulo.nombre : "---");
        
        if (articulo.precioActual != null) {
            holder.tvPrecio.setText(String.format(Locale.getDefault(), "$ %.2f", articulo.precioActual));
        } else {
            holder.tvPrecio.setText("$ 0.00");
        }

        Glide.with(holder.itemView.getContext())
                .load(articulo.imagenUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(holder.imgMiArticulo);

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEditar(articulo);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onEliminar(articulo);
        });
    }

    @Override
    public int getItemCount() {
        return listaArticulos != null ? listaArticulos.size() : 0;
    }

    public void actualizarLista(List<Articulo> nuevalista) {
        this.listaArticulos = nuevalista;
        notifyDataSetChanged();
    }
}
