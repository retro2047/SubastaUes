package com.example.subastaues.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subastaues.R;
import com.example.subastaues.adapters.PujaAdapter;
import com.example.subastaues.data.database.SubastasDatabase;
import com.example.subastaues.data.entities.PujaConArticulo;

import java.util.List;

public class MisPujasFragment extends Fragment {
    private RecyclerView recyclerView;
    private PujaAdapter adapter;
    private TextView tvSinPujas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_pujas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerMisPujas);
        tvSinPujas = view.findViewById(R.id.tvSinPujas);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PujaAdapter(null);
        recyclerView.setAdapter(adapter);

        cargarMisPujas();
    }

    private void cargarMisPujas() {
        SubastasDatabase.obtenerInstancia(requireContext())
                .pujaDao()
                .obtenerPujasConArticulo(1)
                .observe(getViewLifecycleOwner(), pujas -> {
                    if (pujas == null || pujas.isEmpty()) {
                        tvSinPujas.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvSinPujas.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.actualizarLista(pujas);
                    }
                });
    }
}
