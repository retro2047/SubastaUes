package com.example.subastaues.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subastaues.ArticuloViewModel;
import com.example.subastaues.R;
import com.example.subastaues.adapters.ArticuloAdapter;
import com.example.subastaues.data.entities.Articulo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CatalogoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArticuloAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalogo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerArticulos);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ArticuloAdapter(new ArrayList<>(), new ArticuloAdapter.OnArticuloClickListener() {
            @Override
            public void onPujarClick(Articulo articulo) {
                PujaDialogFragment dialog = PujaDialogFragment.newInstance(articulo);
                dialog.show(getParentFragmentManager(), "PujaDialog");
            }

            @Override
            public void onItemClick(Articulo articulo) {
                Bundle bundle = new Bundle();
                bundle.putInt("articuloId", articulo.id);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_catalogo_to_detalle, bundle);
            }
        });
        recyclerView.setAdapter(adapter);

        // Uso de LiveData para observar cambios automáticamente
        ArticuloViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(ArticuloViewModel.class);
        viewModel.obtenerActivos().observe(getViewLifecycleOwner(), articulos -> {
            if (articulos != null) {
                adapter.actualizarLista(articulos);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fabAgregarArticulo);
        fab.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_catalogo_to_agregar));
    }
}
