package com.example.subastaues.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.subastaues.LoginActivity;
import com.example.subastaues.R;
import com.example.subastaues.utils.LocationHelper;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Locale;

public class PerfilFragment extends Fragment {

    private ImageView profileAvatar;
    private TextView profileName;
    private TextView profileEmail;
    private TextView textCoordinates;
    private TextView textAddress;
    private Button btnFetchLocation;
    private MaterialSwitch switchDarkMode;
    private Button btnLogout;

    private LocationHelper locationHelper;
    private SharedPreferences userPrefs;

    // Handle runtime permissions request
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocation = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseLocation = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                if ((fineLocation != null && fineLocation) || (coarseLocation != null && coarseLocation)) {
                    fetchLocation();
                } else {
                    Toast.makeText(requireContext(), R.string.msg_permisos_denegados, Toast.LENGTH_SHORT).show();
                    textCoordinates.setText("Coordenadas: Permiso Denegado");
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        profileAvatar = view.findViewById(R.id.profileAvatar);
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        textCoordinates = view.findViewById(R.id.textCoordinates);
        textAddress = view.findViewById(R.id.textAddress);
        btnFetchLocation = view.findViewById(R.id.btnFetchLocation);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Init helpers
        locationHelper = new LocationHelper(requireContext());
        userPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Load Session parameters
        String name = userPrefs.getString("user_name", "Usuario");
        String email = userPrefs.getString("user_email", "usuario@ues.edu.sv");
        profileName.setText(name);
        profileEmail.setText(email);

        // cargar ubicacion
        cargarUbicacionGuardada();

        // Determine current theme to set the initial switch state
        int nightModeFlags = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
        switchDarkMode.setChecked(isNightMode);

        // Set Dark Mode toggle listener
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Set Location button listener
        btnFetchLocation.setOnClickListener(v -> {
            if (locationHelper.hasLocationPermission()) {
                fetchLocation();
            } else {
                requestPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        });

        // Set Logout listener
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putBoolean("session_active", false);
            editor.remove("user_id");
            editor.remove("user_name");
            editor.remove("user_email");
            editor.apply();

            Toast.makeText(requireContext(), R.string.msg_sesion_cerrada, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void cargarUbicacionGuardada() {
        String ubicacion = userPrefs.getString("user_location", null);
        String address = userPrefs.getString("user_address", null);

        if (ubicacion != null && !ubicacion.isEmpty()) {
            textCoordinates.setText("Coordenadas: " + ubicacion);
        } else {
            textCoordinates.setText("Coordenadas: No configurada");
        }

        if (address != null && !address.isEmpty()) {
            textAddress.setText("Dirección: " + address);
        } else {
            textAddress.setText("Dirección: No disponible");
        }
    }

    private void fetchLocation() {
        textCoordinates.setText("Coordenadas: Obteniendo ubicación...");
        textAddress.setText("Dirección: Obteniendo dirección...");

        Toast.makeText(requireContext(), "Solicitando ubicación...", Toast.LENGTH_SHORT).show();

        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationFound(Location location) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "✓ Ubicación encontrada", Toast.LENGTH_SHORT).show();

                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String ubicacion = String.format(Locale.getDefault(), "Lat: %.6f, Lon: %.6f", lat, lon);

                    // Guarda en SharedPreferences
                    SharedPreferences.Editor editor = userPrefs.edit();
                    editor.putString("user_location", ubicacion);
                    editor.apply();

                    textCoordinates.setText("Coordenadas: " + ubicacion);

                    new Thread(() -> {
                        String address = locationHelper.getAddressFromLocation(lat, lon);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                SharedPreferences.Editor editorDir = userPrefs.edit();
                                editorDir.putString("user_address", address);
                                editorDir.apply();

                                textAddress.setText("Dirección: " + address);
                            });
                        }
                    }).start();
                }
            }

            @Override
            public void onLocationError(String error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "✗ Error: " + error, Toast.LENGTH_LONG).show();
                    textCoordinates.setText("Coordenadas: Error - " + error);
                    textAddress.setText("Dirección: No disponible");
                }
            }
        });
    }
}
