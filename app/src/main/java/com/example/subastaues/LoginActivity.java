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
        super.onCreate(savedInstanceState);

        // Apply saved theme preference before anything else
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        if (isDarkMode) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 1. Session check: if user is already logged in, redirect immediately
        boolean sessionActive = prefs.getBoolean("session_active", false);
        if (sessionActive) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

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
            btnSubmit.setText(R.string.btn_registrarse);
            textTitle.setText(R.string.title_crear_cuenta);
            textSubtitle.setText(R.string.subtitle_unete);
            textToggleMode.setText(R.string.text_ya_tienes_cuenta);
        } else {
            inputLayoutNombre.setVisibility(View.GONE);
            inputLayoutConfirmPassword.setVisibility(View.GONE);
            btnSubmit.setText(R.string.btn_iniciar_sesion);
            textTitle.setText(R.string.title_subasta_ues);
            textSubtitle.setText(R.string.subtitle_plataforma);
            textToggleMode.setText(R.string.text_no_tienes_cuenta);
        }
    }

    private String getInputValue(TextInputLayout layout) {
        if (layout != null && layout.getEditText() != null) {
            return layout.getEditText().getText().toString().trim();
        }
        return "";
    }

    private void handleAuth() {
        String correo = getInputValue(inputLayoutCorreo);
        String password = getInputValue(inputLayoutPassword);

        if (correo.isEmpty()) {
            inputLayoutCorreo.setError(getString(R.string.error_correo_requerido));
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            inputLayoutCorreo.setError(getString(R.string.error_correo_valido));
            return;
        } else {
            inputLayoutCorreo.setError(null);
        }

        if (password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.error_password_requerida));
            return;
        } else if (password.length() < 6) {
            inputLayoutPassword.setError(getString(R.string.error_password_min_length));
            return;
        } else {
            inputLayoutPassword.setError(null);
        }

        if (isRegisterMode) {
            String nombre = getInputValue(inputLayoutNombre);
            String confirmPassword = getInputValue(inputLayoutConfirmPassword);

            if (nombre.isEmpty()) {
                inputLayoutNombre.setError(getString(R.string.error_nombre_requerido));
                return;
            } else {
                inputLayoutNombre.setError(null);
            }

            if (confirmPassword.isEmpty()) {
                inputLayoutConfirmPassword.setError(getString(R.string.error_confirm_password_requerida));
                return;
            } else if (!password.equals(confirmPassword)) {
                inputLayoutConfirmPassword.setError(getString(R.string.error_passwords_no_coinciden));
                return;
            } else {
                inputLayoutConfirmPassword.setError(null);
            }

            // Register logic
            btnSubmit.setEnabled(false);
            usuarioRepository.buscarPorCorreo(correo, existingUser -> {
                if (existingUser != null) {
                    btnSubmit.setEnabled(true);
                    inputLayoutCorreo.setError(getString(R.string.error_correo_registrado));
                    Toast.makeText(this, R.string.msg_usuario_ya_existe, Toast.LENGTH_SHORT).show();
                } else {
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.nombre = nombre;
                    nuevoUsuario.correo = correo;
                    nuevoUsuario.password = password;

                    usuarioRepository.insertar(nuevoUsuario, aVoid -> {
                        // Retrieve the inserted user to get their auto-generated ID
                        usuarioRepository.login(correo, password, registeredUser -> {
                            btnSubmit.setEnabled(true);
                            if (registeredUser != null) {
                                saveSessionAndRedirect(registeredUser);
                            } else {
                                Toast.makeText(this, R.string.error_login_post_registro, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, R.string.error_credenciales_incorrectas, Toast.LENGTH_LONG).show();
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
        android.util.Log.d("DEPURACION_SESION", "Iniciando sesion - Usuario: " + usuario.nombre + "| ID a guardar" + usuario.id);
        editor.apply();

        String welcome = getString(R.string.welcome_message, usuario.nombre);
        Toast.makeText(this, welcome, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
