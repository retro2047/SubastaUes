package com.example.subastaues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.subastaues.data.UsuarioRepository;
import com.example.subastaues.data.entities.Usuario;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutNombre;
    private TextInputLayout inputLayoutCorreo;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutConfirmPassword;
    private Button btnSubmit;
    private TextView textToggleMode;
    private TextView textTitle;
    private TextView textSubtitle;

    private UsuarioRepository usuarioRepository;
    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme preference before anything else
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 1. Session check: if user is already logged in, redirect immediately
        boolean sessionActive = prefs.getBoolean("session_active", false);
        if (sessionActive) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            super.onCreate(savedInstanceState);
            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize repository
        usuarioRepository = new UsuarioRepository(getApplication());

        // Bind views
        inputLayoutNombre = findViewById(R.id.inputLayoutNombre);
        inputLayoutCorreo = findViewById(R.id.inputLayoutCorreo);
        inputLayoutPassword = findViewById(R.id.inputLayoutPassword);
        inputLayoutConfirmPassword = findViewById(R.id.inputLayoutConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        textToggleMode = findViewById(R.id.textToggleMode);
        textTitle = findViewById(R.id.textTitle);
        textSubtitle = findViewById(R.id.textSubtitle);

        // Click listener to toggle modes (Login <-> Register)
        textToggleMode.setOnClickListener(v -> toggleMode());

        // Submit action
        btnSubmit.setOnClickListener(v -> handleAuth());
    }

    private void toggleMode() {
        isRegisterMode = !isRegisterMode;
        if (isRegisterMode) {
            inputLayoutNombre.setVisibility(View.VISIBLE);
            inputLayoutConfirmPassword.setVisibility(View.VISIBLE);
            btnSubmit.setText("Registrarse");
            textTitle.setText("Crear Cuenta");
            textSubtitle.setText("Únete a la comunidad de subastas");
            textToggleMode.setText("¿Ya tienes cuenta? Inicia sesión");
        } else {
            inputLayoutNombre.setVisibility(View.GONE);
            inputLayoutConfirmPassword.setVisibility(View.GONE);
            btnSubmit.setText("Iniciar Sesión");
            textTitle.setText("Subasta UES");
            textSubtitle.setText("Plataforma de Subastas Universitarias");
            textToggleMode.setText("¿No tienes cuenta? Regístrate aquí");
        }
    }

    private void handleAuth() {
        String correo = inputLayoutCorreo.getEditText().getText().toString().trim();
        String password = inputLayoutPassword.getEditText().getText().toString().trim();

        if (correo.isEmpty()) {
            inputLayoutCorreo.setError("El correo es requerido");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            inputLayoutCorreo.setError("Ingrese un correo válido");
            return;
        } else {
            inputLayoutCorreo.setError(null);
        }

        if (password.isEmpty()) {
            inputLayoutPassword.setError("La contraseña es requerida");
            return;
        } else if (password.length() < 6) {
            inputLayoutPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        } else {
            inputLayoutPassword.setError(null);
        }

        if (isRegisterMode) {
            String nombre = inputLayoutNombre.getEditText().getText().toString().trim();
            String confirmPassword = inputLayoutConfirmPassword.getEditText().getText().toString().trim();

            if (nombre.isEmpty()) {
                inputLayoutNombre.setError("El nombre es requerido");
                return;
            } else {
                inputLayoutNombre.setError(null);
            }

            if (confirmPassword.isEmpty()) {
                inputLayoutConfirmPassword.setError("Debe confirmar su contraseña");
                return;
            } else if (!password.equals(confirmPassword)) {
                inputLayoutConfirmPassword.setError("Las contraseñas no coinciden");
                return;
            } else {
                inputLayoutConfirmPassword.setError(null);
            }

            // Register logic
            btnSubmit.setEnabled(false);
            usuarioRepository.buscarPorCorreo(correo, existingUser -> {
                if (existingUser != null) {
                    btnSubmit.setEnabled(true);
                    inputLayoutCorreo.setError("Este correo ya está registrado");
                    Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                } else {
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.nombre = nombre;
                    nuevoUsuario.correo = correo;
                    nuevoUsuario.contraseña = password;

                    usuarioRepository.insertar(nuevoUsuario, aVoid -> {
                        // Retrieve the inserted user to get their auto-generated ID
                        usuarioRepository.login(correo, password, registeredUser -> {
                            btnSubmit.setEnabled(true);
                            if (registeredUser != null) {
                                saveSessionAndRedirect(registeredUser);
                            } else {
                                Toast.makeText(this, "Error al iniciar sesión tras el registro", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
            });

        } else {
            // Login logic
            btnSubmit.setEnabled(false);
            usuarioRepository.login(correo, password, user -> {
                btnSubmit.setEnabled(true);
                if (user != null) {
                    saveSessionAndRedirect(user);
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void saveSessionAndRedirect(Usuario usuario) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("session_active", true);
        editor.putInt("user_id", usuario.id);
        editor.putString("user_name", usuario.nombre);
        editor.putString("user_email", usuario.correo);
        editor.apply();

        Toast.makeText(this, "¡Bienvenido, " + usuario.nombre + "!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
