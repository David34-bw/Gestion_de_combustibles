package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.EntregaRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.entregas.EntregaResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Estacion;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.button.MaterialButton;

public class RegistrarEntregaActivity extends AppCompatActivity {

    private RadioGroup rgCombustible;
    private TextInputEditText etVolumen, etObservaciones;
    private Spinner spinnerEstacion;
    private MaterialButton btnRegistrar;

    private List<Estacion> estaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_entrega);

        rgCombustible  = findViewById(R.id.rg_combustible);
        etVolumen      = findViewById(R.id.et_volumen);
        etObservaciones= findViewById(R.id.et_observaciones);
        spinnerEstacion= findViewById(R.id.spinner_estacion);
        btnRegistrar   = findViewById(R.id.btn_registrar_entrega);

        cargarEstaciones();

        btnRegistrar.setOnClickListener(v -> registrar());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100);
            }
        }
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
                            for (Estacion e : estaciones) nombres.add(e.getNombre());
                            
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    RegistrarEntregaActivity.this,
                                    R.layout.spinner_item, nombres);
                            
                            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                            spinnerEstacion.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Estacion>>> call, Throwable t) {
                        Toast.makeText(RegistrarEntregaActivity.this,
                                "Error cargando estaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrar() {
        String volumenStr = etVolumen.getText().toString().trim();

        if (volumenStr.isEmpty()) {
            Toast.makeText(this, "Ingresa el volumen", Toast.LENGTH_SHORT).show();
            return;
        }
        if (estaciones.isEmpty()) {
            Toast.makeText(this, "No hay estaciones disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        double volumen = Double.parseDouble(volumenStr);
        String tipo = rgCombustible.getCheckedRadioButtonId() == R.id.rb_gasolina
                ? "GASOLINA" : "DIESEL";
        String obs  = etObservaciones.getText().toString().trim();
        Long estacionId = estaciones.get(spinnerEstacion.getSelectedItemPosition()).getId();

        EntregaRequest request = new EntregaRequest(tipo, volumen, estacionId, obs);

        ApiClient.getApiService().registrarEntrega(request)
                .enqueue(new Callback<ApiResponse<EntregaResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<EntregaResponse>> call,
                                           Response<ApiResponse<EntregaResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            // Obtener datos para la notificación
                            String estacionNombre = estaciones
                                    .get(spinnerEstacion.getSelectedItemPosition())
                                    .getNombre();

                            // Iniciar el servicio en background
                            Intent serviceIntent = new Intent(
                                    RegistrarEntregaActivity.this,
                                    FuelRegistrationService.class
                            );
                            serviceIntent.putExtra(FuelRegistrationService.EXTRA_TIPO, tipo);
                            serviceIntent.putExtra(FuelRegistrationService.EXTRA_VOLUMEN, volumen);
                            serviceIntent.putExtra(FuelRegistrationService.EXTRA_ESTACION, estacionNombre);
                            startService(serviceIntent);

                            Toast.makeText(RegistrarEntregaActivity.this,
                                    "Entrega registrada", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage() : "Error al registrar";
                            Toast.makeText(RegistrarEntregaActivity.this,
                                    msg, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<EntregaResponse>> call, Throwable t) {
                        Toast.makeText(RegistrarEntregaActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}