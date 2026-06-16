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
                    Toast.makeText(requireContext(), "Permisos de ubicación denegados.", Toast.LENGTH_SHORT).show();
                    textCoordinates.setText("Coordenadas: Permiso denegado");
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
            // Optionally keep email/name, or clear completely:
            editor.remove("user_id");
            editor.remove("user_name");
            editor.remove("user_email");
            editor.apply();

            Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void fetchLocation() {
        textCoordinates.setText("Coordenadas: Obteniendo ubicación...");
        textAddress.setText("Dirección: Obteniendo dirección...");

        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationFound(Location location) {
                if (isAdded()) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    textCoordinates.setText(String.format(Locale.getDefault(), "Coordenadas: Lat: %.6f, Lon: %.6f", lat, lon));
                    
                    // Fetch address on a background task or using Geocoder directly
                    new Thread(() -> {
                        String address = locationHelper.getAddressFromLocation(lat, lon);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> textAddress.setText("Dirección: " + address));
                        }
                    }).start();
                }
            }

            @Override
            public void onLocationError(String error) {
                if (isAdded()) {
                    textCoordinates.setText("Coordenadas: Error al obtener");
                    textAddress.setText("Dirección: " + error);
                    Toast.makeText(requireContext(), "Error de ubicación: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
