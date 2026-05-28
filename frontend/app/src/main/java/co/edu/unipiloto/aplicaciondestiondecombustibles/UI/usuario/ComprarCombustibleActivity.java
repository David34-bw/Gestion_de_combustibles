package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.Ventas.VentaResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.VentaRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Estacion;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Vehiculo;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComprarCombustibleActivity extends AppCompatActivity {

    private Spinner spinnerEstacion;
    private Spinner spinnerVehiculo;
    private RadioGroup rgCombustible;
    private TextInputEditText etCantidad;
    private TextInputEditText etObservaciones;
    private Button btnConfirmar;

    private List<Estacion> estaciones = new ArrayList<>();
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_combustible);

        spinnerEstacion = findViewById(R.id.spinner_estacion);
        spinnerVehiculo = findViewById(R.id.spinner_vehiculo);
        rgCombustible = findViewById(R.id.rg_combustible_compra);
        etCantidad = findViewById(R.id.et_cantidad_compra);
        etObservaciones = findViewById(R.id.et_observaciones_compra);
        btnConfirmar = findViewById(R.id.btn_confirmar_compra);

        cargarEstaciones();
        cargarVehiculos();

        btnConfirmar.setOnClickListener(v -> confirmarCompra());
    }

    private void cargarEstaciones() {
        ApiClient.getApiService().getEstacionesPublicas()
                .enqueue(new Callback<ApiResponse<List<Estacion>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Estacion>>> call,
                                           Response<ApiResponse<List<Estacion>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            estaciones = response.body().getData();
                            List<String> nombres = new ArrayList<>();
                            if (estaciones != null) {
                                for (Estacion e : estaciones) {
                                    nombres.add(e.getNombre() + " - " + e.getCiudad());
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    ComprarCombustibleActivity.this,
                                    R.layout.spinner_item,
                                    nombres);
                            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                            spinnerEstacion.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Estacion>>> call, Throwable t) {
                        Toast.makeText(ComprarCombustibleActivity.this,
                                "No se pudieron cargar estaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarVehiculos() {
        ApiClient.getApiService().getMisVehiculos()
                .enqueue(new Callback<ApiResponse<List<Vehiculo>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Vehiculo>>> call,
                                           Response<ApiResponse<List<Vehiculo>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            vehiculos = response.body().getData();
                            List<String> placas = new ArrayList<>();
                            if (vehiculos != null) {
                                for (Vehiculo v : vehiculos) {
                                    String tipo = v.getTipoVehiculo() != null ? v.getTipoVehiculo() : "";
                                    placas.add(v.getPlaca() + " - " + tipo);
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    ComprarCombustibleActivity.this,
                                    R.layout.spinner_item,
                                    placas);
                            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                            spinnerVehiculo.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Vehiculo>>> call, Throwable t) {
                        Toast.makeText(ComprarCombustibleActivity.this,
                                "No se pudieron cargar vehículos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confirmarCompra() {
        if (estaciones == null || estaciones.isEmpty()) {
            Toast.makeText(this, "No hay estaciones disponibles", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vehiculos == null || vehiculos.isEmpty()) {
            Toast.makeText(this, "Debes registrar un vehículo", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantStr = etCantidad.getText() != null
                ? etCantidad.getText().toString().trim()
                : "";
        if (cantStr.isEmpty()) {
            Toast.makeText(this, "Ingresa la cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad = Double.parseDouble(cantStr);
        String tipo = rgCombustible.getCheckedRadioButtonId() == R.id.rb_gasolina_compra
                ? "GASOLINA" : "DIESEL";
        String obs = etObservaciones.getText() != null
                ? etObservaciones.getText().toString().trim()
                : "";

        Vehiculo vehiculo = vehiculos.get(spinnerVehiculo.getSelectedItemPosition());
        String placa = vehiculo.getPlaca();

        Estacion estacion = estaciones.get(spinnerEstacion.getSelectedItemPosition());
        Long estacionId = estacion != null ? estacion.getId() : null;
        VentaRequest request = new VentaRequest(tipo, cantidad, obs, placa, estacionId);

        ApiClient.getApiService().registrarCompra(request)
                .enqueue(new Callback<ApiResponse<VentaResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<VentaResponse>> call,
                                           Response<ApiResponse<VentaResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            Toast.makeText(ComprarCombustibleActivity.this,
                                    "Compra registrada", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : "Error al registrar";
                            Toast.makeText(ComprarCombustibleActivity.this,
                                    msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<VentaResponse>> call, Throwable t) {
                        Toast.makeText(ComprarCombustibleActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
