package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor;

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
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.entregas.EntregaResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistribuidorDashboardActivity extends AppCompatActivity {

    private LinearLayout llEntregas;
    private TextView tvSinEntregas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribuidor_dashboard);

        SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Distribuidor");

        TextView tvWelcome    = findViewById(R.id.tv_welcome);
        Button btnLogout      = findViewById(R.id.btn_logout);
        Button btnEntrega     = findViewById(R.id.btn_registrar_entrega);
        llEntregas            = findViewById(R.id.ll_entregas);
        tvSinEntregas         = findViewById(R.id.tv_sin_entregas);

        tvWelcome.setText("Bienvenido, " + nombre + "\nGestiona tus entregas de combustible.");

        btnEntrega.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrarEntregaActivity.class)));

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
        cargarEntregas();
    }

    private void cargarEntregas() {
        ApiClient.getApiService().getMisEntregas()
                .enqueue(new Callback<ApiResponse<List<EntregaResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<EntregaResponse>>> call,
                                           Response<ApiResponse<List<EntregaResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<EntregaResponse> lista = response.body().getData();
                            llEntregas.removeAllViews();

                            if (lista == null || lista.isEmpty()) {
                                tvSinEntregas.setVisibility(View.VISIBLE);
                            } else {
                                tvSinEntregas.setVisibility(View.GONE);
                                for (EntregaResponse e : lista) {
                                    agregarTarjeta(e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<EntregaResponse>>> call, Throwable t) {
                        Toast.makeText(DistribuidorDashboardActivity.this,
                                "Error cargando entregas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void agregarTarjeta(EntregaResponse e) {
        View tarjeta = getLayoutInflater().inflate(R.layout.item_entrega, llEntregas, false);

        ((TextView) tarjeta.findViewById(R.id.tv_estacion)).setText(e.getEstacionNombre());
        ((TextView) tarjeta.findViewById(R.id.tv_tipo)).setText(e.getTipoCombustible());
        ((TextView) tarjeta.findViewById(R.id.tv_volumen)).setText(e.getVolumen() + " galones");
        ((TextView) tarjeta.findViewById(R.id.tv_fecha)).setText(
                e.getFechaEntrega() != null ? e.getFechaEntrega().substring(0, 10) : "");

        llEntregas.addView(tarjeta);
    }
}