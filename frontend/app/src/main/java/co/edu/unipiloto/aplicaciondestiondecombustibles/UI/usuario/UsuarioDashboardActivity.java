package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import com.google.android.material.card.MaterialCardView;
import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth.LoginActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion.ConsultarPrecioActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario.RecompensasActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.puntos.PuntosResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Vehiculo;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.Ventas.VentaResponse;

public class UsuarioDashboardActivity extends AppCompatActivity {

    private LinearLayout llVehiculos;
    private TextView tvSinVehiculos;
    private Spinner spinnerFiltro;
    private View cardPrecios;
    private View cardRecompensas;
    private View cardVehiculos;
    private View cardHistorial;
    private View headerAcciones;
    private View headerVehiculos;
    private View headerHistorial;

    private List<Vehiculo> todosLosVehiculos = new ArrayList<>();

    private final String[] FILTROS = {"TODOS", "PARTICULAR", "TAXI", "MOTOCICLETA", "CARGA"};
    private final String[] FILTROS_DISPLAY = {"Todos", "Particular", "Taxi", "Motocicleta", "Carga"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_dashboard);
        LinearLayout llCompras   = findViewById(R.id.ll_compras);
        TextView tvSinCompras    = findViewById(R.id.tv_sin_compras);

        SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Usuario");

        TextView tvWelcome = findViewById(R.id.tv_welcome);
        TextView tvPuntosResumen = findViewById(R.id.tv_puntos_resumen);
        MaterialCardView btn_ir_perfil = findViewById(R.id.btn_ir_perfil);
        cardPrecios = findViewById(R.id.card_precios);
        cardRecompensas = findViewById(R.id.card_recompensas);
        cardVehiculos = findViewById(R.id.card_vehiculos);
        cardHistorial = findViewById(R.id.card_historial_compras);
        headerAcciones = findViewById(R.id.tv_acciones_header);
        headerVehiculos = findViewById(R.id.tv_vehiculos_header);
        headerHistorial = findViewById(R.id.tv_historial_header);
        View btnTabPrecios = findViewById(R.id.btn_tab_precios);
        View btnTabRecompensas = findViewById(R.id.btn_tab_recompensas);
        View btnTabVehiculos = findViewById(R.id.btn_tab_vehiculos);
        View btnTabHistorial = findViewById(R.id.btn_tab_historial);
        Button btnLogout   = findViewById(R.id.btn_logout);
        Button btnVehiculo = findViewById(R.id.btn_registrar_vehiculo);
        Button btnPrecios  = findViewById(R.id.btn_ver_precios);
        Button btnRecompensas = findViewById(R.id.btn_recompensas);
        llVehiculos        = findViewById(R.id.ll_vehiculos);
        tvSinVehiculos     = findViewById(R.id.tv_sin_vehiculos);
        spinnerFiltro      = findViewById(R.id.spinner_filtro);

        tvWelcome.setText("Bienvenido, " + nombre);
        int puntos = prefs.getInt("puntos", 0);
        tvPuntosResumen.setText("Puntos: " + puntos);

        btnTabPrecios.setOnClickListener(v -> mostrarSeccion("PRECIOS"));
        btnTabRecompensas.setOnClickListener(v -> mostrarSeccion("RECOMPENSAS"));
        btnTabVehiculos.setOnClickListener(v -> mostrarSeccion("VEHICULOS"));
        btnTabHistorial.setOnClickListener(v -> mostrarSeccion("HISTORIAL"));

        mostrarSeccion("PRECIOS");

        // Spinner de filtro
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, FILTROS_DISPLAY);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filtrarVehiculos(FILTROS[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btn_ir_perfil.setOnClickListener(v -> {
            Long miId = prefs.getLong("userId", -1L);

            Intent intent = new Intent(this, EditarPerfilActivity.class);
            intent.putExtra("id", miId);
            startActivity(intent);
        });

        btnVehiculo.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrarVehiculoActivity.class)));

        btnPrecios.setOnClickListener(v ->
                startActivity(new Intent(this, ConsultarPrecioActivity.class)));

        btnRecompensas.setOnClickListener(v ->
                startActivity(new Intent(this, RecompensasActivity.class)));

        btnLogout.setOnClickListener(v -> {
            ApiClient.clearToken();
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        ApiClient.getApiService().getMisCompras()
                .enqueue(new Callback<ApiResponse<List<VentaResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<VentaResponse>>> call,
                                           Response<ApiResponse<List<VentaResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<VentaResponse> compras = response.body().getData();
                            if (compras == null || compras.isEmpty()) {
                                tvSinCompras.setVisibility(View.VISIBLE);
                            } else {
                                tvSinCompras.setVisibility(View.GONE);
                                for (VentaResponse compra : compras) {
                                    View item = getLayoutInflater().inflate(
                                            R.layout.item_compra, llCompras, false);
                                    ((TextView) item.findViewById(R.id.tv_estacion_compra))
                                            .setText(compra.getEstacionNombre());
                                    ((TextView) item.findViewById(R.id.tv_tipo_compra))
                                            .setText(compra.getTipoCombustible());
                                    ((TextView) item.findViewById(R.id.tv_cantidad_compra))
                                            .setText(compra.getCantidad() + " gal");
                                    ((TextView) item.findViewById(R.id.tv_fecha_compra))
                                            .setText(compra.getFechaVenta() != null
                                                    ? compra.getFechaVenta().substring(0, 10) : "");
                                    ((TextView) item.findViewById(R.id.tv_placa_compra))
                                            .setText(compra.getPlacaVehiculo() != null
                                                    ? "🚗 " + compra.getPlacaVehiculo() : "");
                                    llCompras.addView(item);
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<VentaResponse>>> call, Throwable t) {}
                });

        ApiClient.getApiService().getSaldoPuntos()
                .enqueue(new Callback<ApiResponse<PuntosResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PuntosResponse>> call,
                                           Response<ApiResponse<PuntosResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            PuntosResponse puntosResponse = response.body().getData();
                            int saldo = puntosResponse != null && puntosResponse.getPuntosAcumulados() != null
                                    ? puntosResponse.getPuntosAcumulados() : 0;
                            tvPuntosResumen.setText("Puntos: " + saldo);
                            prefs.edit().putInt("puntos", saldo).apply();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PuntosResponse>> call,
                                          Throwable t) {}
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarVehiculos();
    }

    private void cargarVehiculos() {
        ApiClient.getApiService().getMisVehiculos()
                .enqueue(new Callback<ApiResponse<List<Vehiculo>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Vehiculo>>> call,
                                           Response<ApiResponse<List<Vehiculo>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            todosLosVehiculos = response.body().getData();
                            filtrarVehiculos(FILTROS[spinnerFiltro.getSelectedItemPosition()]);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Vehiculo>>> call, Throwable t) {
                        Toast.makeText(UsuarioDashboardActivity.this,
                                "Error cargando vehículos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filtrarVehiculos(String filtro) {
        llVehiculos.removeAllViews();
        List<Vehiculo> filtrados = new ArrayList<>();

        for (Vehiculo v : todosLosVehiculos) {
            if (filtro.equals("TODOS") || filtro.equals(v.getTipoVehiculo())) {
                filtrados.add(v);
            }
        }

        if (filtrados.isEmpty()) {
            tvSinVehiculos.setVisibility(View.VISIBLE);
        } else {
            tvSinVehiculos.setVisibility(View.GONE);
            for (Vehiculo v : filtrados) {
                agregarTarjetaVehiculo(v);
            }
        }
    }

    private void mostrarSeccion(String seccion) {
        headerAcciones.setVisibility(View.GONE);
        headerVehiculos.setVisibility(View.GONE);
        headerHistorial.setVisibility(View.GONE);
        cardPrecios.setVisibility(View.GONE);
        cardRecompensas.setVisibility(View.GONE);
        cardVehiculos.setVisibility(View.GONE);
        cardHistorial.setVisibility(View.GONE);

        switch (seccion) {
            case "PRECIOS":
                headerAcciones.setVisibility(View.VISIBLE);
                cardPrecios.setVisibility(View.VISIBLE);
                break;
            case "RECOMPENSAS":
                headerAcciones.setVisibility(View.VISIBLE);
                cardRecompensas.setVisibility(View.VISIBLE);
                break;
            case "VEHICULOS":
                headerVehiculos.setVisibility(View.VISIBLE);
                cardVehiculos.setVisibility(View.VISIBLE);
                break;
            case "HISTORIAL":
                headerHistorial.setVisibility(View.VISIBLE);
                cardHistorial.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void agregarTarjetaVehiculo(Vehiculo v) {
        View tarjeta = getLayoutInflater().inflate(R.layout.item_vehiculo, llVehiculos, false);

        TextView tvIcono   = tarjeta.findViewById(R.id.tv_icono_vehiculo);
        TextView tvPlaca   = tarjeta.findViewById(R.id.tv_placa);
        TextView tvTipo    = tarjeta.findViewById(R.id.tv_tipo);
        TextView tvMarca   = tarjeta.findViewById(R.id.tv_marca);
        TextView tvSubsidio= tarjeta.findViewById(R.id.tv_subsidio);
        Button btnEliminar  = tarjeta.findViewById(R.id.btn_eliminar);

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

        btnEliminar.setOnClickListener(x -> eliminarVehiculo(v.getId()));
        llVehiculos.addView(tarjeta);
    }

    private void eliminarVehiculo(Long id) {
        ApiClient.getApiService().eliminarVehiculo(id)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        Toast.makeText(UsuarioDashboardActivity.this,
                                "Vehículo eliminado", Toast.LENGTH_SHORT).show();
                        cargarVehiculos();
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Toast.makeText(UsuarioDashboardActivity.this,
                                "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
