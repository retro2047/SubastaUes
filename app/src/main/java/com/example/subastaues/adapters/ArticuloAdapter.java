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

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ArticuloViewHolder> {

    public interface OnArticuloClickListener {
        void onPujarClick(Articulo articulo);
        void onItemClick(Articulo articulo);
    }

    private List<Articulo> listaArticulos;
    private OnArticuloClickListener listener;

    public ArticuloAdapter(List<Articulo> listaArticulos, OnArticuloClickListener listener) {
        this.listaArticulos = listaArticulos;
        this.listener = listener;
    }

    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {
        ImageView imgArticulo;
        TextView tvNombre, tvDescripcion, tvPrecioActual;
        Button btnPujar;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArticulo = itemView.findViewById(R.id.imgArticulo);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecioActual = itemView.findViewById(R.id.tvPrecioActual);
            btnPujar = itemView.findViewById(R.id.btnPujar);
        }
    }
    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_articulo, parent, false);
        return new ArticuloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int position) {
        Articulo articulo = listaArticulos.get(position);
        holder.tvNombre.setText(articulo.nombre);
        holder.tvDescripcion.setText(articulo.descripcion);
        holder.tvPrecioActual.setText(String.format(Locale.getDefault(), "$ %.2f", articulo.precioActual));

        holder.imgArticulo.setImageResource(R.drawable.ic_launcher_background);

        holder.btnPujar.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(articulo);
        });
    }

    @Override
    public int getItemCount() {
        return listaArticulos != null ? listaArticulos.size() : 0;
    }

    public void actualizarLista(List<Articulo> nuevaLista) {
        this.listaArticulos = nuevaLista;
        notifyDataSetChanged();
    }
}
