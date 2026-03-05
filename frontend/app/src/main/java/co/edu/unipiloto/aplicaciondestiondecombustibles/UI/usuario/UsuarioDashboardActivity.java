package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth.LoginActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.Vehiculo;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioDashboardActivity extends AppCompatActivity {

    private LinearLayout llVehiculos;
    private TextView tvSinVehiculos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_dashboard);

        SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Usuario");

        TextView tvWelcome   = findViewById(R.id.tv_welcome);
        Button btnLogout     = findViewById(R.id.btn_logout);
        Button btnVehiculo   = findViewById(R.id.btn_registrar_vehiculo);
        llVehiculos          = findViewById(R.id.ll_vehiculos);
        tvSinVehiculos       = findViewById(R.id.tv_sin_vehiculos);

        tvWelcome.setText("Bienvenido, " + nombre + "\nConsulta precios, historial y alertas.");

        btnVehiculo.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrarVehiculoActivity.class)));

        btnLogout.setOnClickListener(v -> {
            ApiClient.clearToken();
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarVehiculos(); // Se recarga cada vez que vuelves al dashboard
    }

    private void cargarVehiculos() {
        ApiClient.getApiService().getMisVehiculos()
                .enqueue(new Callback<ApiResponse<List<Vehiculo>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Vehiculo>>> call,
                                           Response<ApiResponse<List<Vehiculo>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            List<Vehiculo> lista = response.body().getData();
                            llVehiculos.removeAllViews();

                            if (lista == null || lista.isEmpty()) {
                                tvSinVehiculos.setVisibility(View.VISIBLE);
                            } else {
                                tvSinVehiculos.setVisibility(View.GONE);
                                for (Vehiculo v : lista) {
                                    agregarTarjetaVehiculo(v);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Vehiculo>>> call, Throwable t) {
                        Toast.makeText(UsuarioDashboardActivity.this,
                                "Error cargando vehículos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void agregarTarjetaVehiculo(Vehiculo v) {
        View tarjeta = getLayoutInflater().inflate(R.layout.item_vehiculo, llVehiculos, false);

        TextView tvIcono   = tarjeta.findViewById(R.id.tv_icono_vehiculo);
        TextView tvPlaca   = tarjeta.findViewById(R.id.tv_placa);
        TextView tvTipo    = tarjeta.findViewById(R.id.tv_tipo);
        TextView tvMarca   = tarjeta.findViewById(R.id.tv_marca);
        TextView tvSubsidio= tarjeta.findViewById(R.id.tv_subsidio);

        String tipo = v.getTipoVehiculo() != null ? v.getTipoVehiculo().toUpperCase() : "";
        String emoji = "🚗"; // Por defecto

        if (tipo.contains("MOTO")) {
            emoji = "🏍️";
        } else if (tipo.contains("TAXI")) {
            emoji = "🚕";
        } else if (tipo.contains("CARGA") || tipo.contains("CAMION")) {
            emoji = "🚛";
        } else if (tipo.contains("PARTICULAR")) {
            emoji = "🚗";
        }

        tvIcono.setText(emoji);
        tvPlaca.setText(v.getPlaca());
        tvTipo.setText(v.getTipoVehiculo());
        tvMarca.setText((v.getMarca() != null ? v.getMarca() : "") +
                (v.getModelo() != null ? " " + v.getModelo() : ""));
        tvSubsidio.setText(Boolean.TRUE.equals(v.getAplicaSubsidio())
                ? "✓ Con subsidio" : "Sin subsidio");
        tvSubsidio.setTextColor(getResources().getColor(
                Boolean.TRUE.equals(v.getAplicaSubsidio())
                        ? R.color.fuel_success : R.color.fuel_gray, null));

        llVehiculos.addView(tarjeta);
    }
}
