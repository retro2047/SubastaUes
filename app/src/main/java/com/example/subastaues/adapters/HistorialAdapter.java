package com.example.subastaues.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.subastaues.R;
import com.example.subastaues.data.entities.PujaConUsuario;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {
    private List<PujaConUsuario> listaPujas;

    public HistorialAdapter(List<PujaConUsuario> listaPujas) {
        this.listaPujas = listaPujas;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        PujaConUsuario pujaActual = listaPujas.get(position);

        if (pujaActual.usuario != null) {
            holder.tvNombreUsuario.setText(pujaActual.usuario.nombre);
        } else {
            holder.tvNombreUsuario.setText("Usuario Anonimo");
        }

        holder.tvMonto.setText("$ " + pujaActual.puja.monto);
        long timestamp = pujaActual.puja.timestamp;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        String fechaFormateada = sdf.format(new java.util.Date(timestamp));
        holder.tvFechaPuja.setText(fechaFormateada);
    }

    @Override
    public int getItemCount() {
        return listaPujas != null ? listaPujas.size() : 0;
    }

    public void actualizarLista(List<PujaConUsuario> nuevaLista) {
        this.listaPujas = nuevaLista;
        notifyDataSetChanged();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuario, tvMonto, tvFechaPuja;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
            tvMonto = itemView.findViewById(R.id.tvMontoPuja);
                tvFechaPuja = itemView.findViewById(R.id.tvFechaPuja);
        }
    }


}
