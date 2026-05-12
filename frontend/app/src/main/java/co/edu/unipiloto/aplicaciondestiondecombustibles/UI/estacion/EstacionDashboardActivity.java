package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.LinearLayout;
import java.util.List;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.Ventas.VentaResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth.LoginActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;


public class EstacionDashboardActivity extends AppCompatActivity {

    private View cardPrecios;
    private View cardInventario;
    private View cardRegistrarVenta;
    private View cardHistorial;
    private View headerAcciones;
    private View headerHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estacion_dashboard);
        LinearLayout llVentas  = findViewById(R.id.ll_ventas_estacion);
        TextView tvSinVentas   = findViewById(R.id.tv_sin_ventas_estacion);


        SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Estación");

        TextView tvWelcome  = findViewById(R.id.tv_welcome);
        Button btnLogout    = findViewById(R.id.btn_logout);
        Button btnPrecios   = findViewById(R.id.btn_consultar_precios);
        headerAcciones = findViewById(R.id.tv_acciones_header);
        headerHistorial = findViewById(R.id.tv_historial_header);
        cardPrecios = findViewById(R.id.card_precios);
        cardInventario = findViewById(R.id.card_inventario);
        cardRegistrarVenta = findViewById(R.id.card_registrar_venta);
        cardHistorial = findViewById(R.id.card_historial_ventas);
        View btnTabPrecios = findViewById(R.id.btn_tab_precios);
        View btnTabInventario = findViewById(R.id.btn_tab_inventario);
        View btnTabRegistrar = findViewById(R.id.btn_tab_registrar);
        View btnTabHistorial = findViewById(R.id.btn_tab_historial);

        tvWelcome.setText("Bienvenido, " + nombre + "\nGestiona inventario, precios y reportes.");
        btnTabPrecios.setOnClickListener(v -> mostrarSeccion("PRECIOS"));
        btnTabInventario.setOnClickListener(v -> mostrarSeccion("INVENTARIO"));
        btnTabRegistrar.setOnClickListener(v -> mostrarSeccion("REGISTRAR"));
        btnTabHistorial.setOnClickListener(v -> mostrarSeccion("HISTORIAL"));

        mostrarSeccion("PRECIOS");

        btnPrecios.setOnClickListener(v ->
                startActivity(new Intent(this, ConsultarPrecioActivity.class)));

        btnLogout.setOnClickListener(v -> {
            ApiClient.clearToken();
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Button btnInventario = findViewById(R.id.btn_inventario);
        btnInventario.setOnClickListener(v ->
                startActivity(new Intent(this, InventarioActivity.class)));

        Button btnVentas = findViewById(R.id.btn_registrar_venta);
        btnVentas.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrarVentaActivity.class)));
        ApiClient.getApiService().getMisVentas()
                .enqueue(new Callback<ApiResponse<List<VentaResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<VentaResponse>>> call,
                                           Response<ApiResponse<List<VentaResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<VentaResponse> ventas = response.body().getData();
                            if (ventas == null || ventas.isEmpty()) {
                                tvSinVentas.setVisibility(View.VISIBLE);
                            } else {
                                tvSinVentas.setVisibility(View.GONE);
                                for (VentaResponse v : ventas) {
                                    View item = getLayoutInflater().inflate(
                                            R.layout.item_venta_estacion, llVentas, false);
                                    ((TextView) item.findViewById(R.id.tv_placa_venta))
                                            .setText(v.getPlacaVehiculo() != null ? "🚗 " + v.getPlacaVehiculo() : "");
                                    ((TextView) item.findViewById(R.id.tv_comprador))
                                            .setText(v.getUsuarioNombre() != null
                                                    ? v.getUsuarioNombre() : "Anónimo");
                                    ((TextView) item.findViewById(R.id.tv_tipo_venta))
                                            .setText(v.getTipoCombustible());
                                    ((TextView) item.findViewById(R.id.tv_cantidad_venta))
                                            .setText(v.getCantidad() + " gal");
                                    ((TextView) item.findViewById(R.id.tv_fecha_venta))
                                            .setText(v.getFechaVenta() != null
                                                    ? v.getFechaVenta().substring(0, 10) : "");
                                    llVentas.addView(item);
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<VentaResponse>>> call, Throwable t) {}
                });

    }

    private void mostrarSeccion(String seccion) {
        headerAcciones.setVisibility(View.GONE);
        headerHistorial.setVisibility(View.GONE);
        cardPrecios.setVisibility(View.GONE);
        cardInventario.setVisibility(View.GONE);
        cardRegistrarVenta.setVisibility(View.GONE);
        cardHistorial.setVisibility(View.GONE);

        switch (seccion) {
            case "PRECIOS":
                headerAcciones.setVisibility(View.VISIBLE);
                cardPrecios.setVisibility(View.VISIBLE);
                break;
            case "INVENTARIO":
                headerAcciones.setVisibility(View.VISIBLE);
                cardInventario.setVisibility(View.VISIBLE);
                break;
            case "REGISTRAR":
                headerAcciones.setVisibility(View.VISIBLE);
                cardRegistrarVenta.setVisibility(View.VISIBLE);
                break;
            case "HISTORIAL":
                headerHistorial.setVisibility(View.VISIBLE);
                cardHistorial.setVisibility(View.VISIBLE);
                break;
        }
    }
}
