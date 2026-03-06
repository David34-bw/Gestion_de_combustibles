package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth.LoginActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;

public class EstacionDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estacion_dashboard);

        SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Estación");

        TextView tvWelcome  = findViewById(R.id.tv_welcome);
        Button btnLogout    = findViewById(R.id.btn_logout);
        Button btnPrecios   = findViewById(R.id.btn_consultar_precios);

        tvWelcome.setText("Bienvenido, " + nombre + "\nGestiona inventario, precios y reportes.");

        btnPrecios.setOnClickListener(v ->
                startActivity(new Intent(this, ConsultarPrecioActivity.class)));

        btnLogout.setOnClickListener(v -> {
            ApiClient.clearToken();
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}