package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.administrador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth.LoginActivity;

public class AdministradorDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_dashboard);

        // Mostrar nombre guardado
        SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Administrador");
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        tvWelcome.setText("Bienvenido, " + nombre);

        // Gestión de usuarios (función movida del regulador)
        MaterialButton btnUsuarios = findViewById(R.id.btn_gestionar_usuarios);
        btnUsuarios.setOnClickListener(v ->
                startActivity(new Intent(this, GestionUsuariosActivity.class))
        );

        // Cerrar sesión
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }
}