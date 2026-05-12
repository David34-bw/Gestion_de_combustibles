package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth.LoginActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;

public class ReguladorDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulador_dashboard);

        SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Regulador");

        TextView tvWelcome       = findViewById(R.id.tv_welcome);
        Button btnLogout         = findViewById(R.id.btn_logout);
        Button btnPrecios        = findViewById(R.id.btn_actualizar_precios);

        tvWelcome.setText("Bienvenido, " + nombre + "\nPanel de administración.");

        btnPrecios.setOnClickListener(v ->
                startActivity(new Intent(this, ActualizarPrecioActivity.class)));

        btnLogout.setOnClickListener(v -> {
            ApiClient.clearToken();
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}