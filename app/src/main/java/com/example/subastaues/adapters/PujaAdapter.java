package com.example.subastaues.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subastaues.R;
import com.example.subastaues.data.entities.PujaConArticulo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PujaAdapter extends RecyclerView.Adapter<PujaAdapter.PujaViewHolder> {

    private List<PujaConArticulo> listaPujas;

    public PujaAdapter(List<PujaConArticulo> listaPujas) {
        this.listaPujas = listaPujas;
    }

    public static class PujaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreArticulo, tvMonto, tvFecha;

        public PujaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreArticulo = itemView.findViewById(R.id.tvNombreArticuloPuja);
            tvMonto = itemView.findViewById(R.id.tvMontoPuja);
            tvFecha = itemView.findViewById(R.id.tvFechaPuja);
        }
    }

    @NonNull
    @Override
    public PujaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_puja, parent, false);
        return new PujaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PujaViewHolder holder, int position) {
        PujaConArticulo puja = listaPujas.get(position);

        holder.tvNombreArticulo.setText(puja.nombreArticulo != null ? puja.nombreArticulo : "---");
        
        if (puja.monto != null) {
            holder.tvMonto.setText(String.format(Locale.getDefault(), "$ %.2f", puja.monto));
        } else {
            holder.tvMonto.setText("$ 0.00");
        }

        if (puja.timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvFecha.setText(sdf.format(new Date(puja.timestamp)));
        } else {
            holder.tvFecha.setText("---");
        }
    }

    @Override
    public int getItemCount() {
        return listaPujas != null ? listaPujas.size() : 0;
    }

    public void actualizarLista(List<PujaConArticulo> nuevaLista) {
        this.listaPujas = nuevaLista;
        notifyDataSetChanged();
    }

}
