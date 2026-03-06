package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.Vehiculo;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.VehiculoRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarVehiculoActivity extends AppCompatActivity {

    private TextInputEditText etPlaca, etMarca, etModelo, etRunt;
    private RadioGroup rgTipoVehiculo;
    private CheckBox cbSubsidio;
    private TextInputLayout layoutRunt; // ← TextInputLayout, no TextView
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_vehiculo);

        etPlaca        = findViewById(R.id.et_placa);
        etMarca        = findViewById(R.id.et_marca);
        etModelo       = findViewById(R.id.et_modelo);
        etRunt         = findViewById(R.id.et_runt);
        rgTipoVehiculo = findViewById(R.id.rg_tipo_vehiculo);
        cbSubsidio     = findViewById(R.id.cb_subsidio);
        layoutRunt     = findViewById(R.id.layout_runt); // ← el TextInputLayout completo
        btnRegistrar   = findViewById(R.id.btn_registrar_vehiculo);

        // Ocultar el campo RUNT al inicio
        layoutRunt.setVisibility(View.GONE);

        // Mostrar/ocultar RUNT según subsidio
        cbSubsidio.setOnCheckedChangeListener((btn, isChecked) ->
                layoutRunt.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        String placa   = etPlaca.getText().toString().trim().toUpperCase();
        String marca   = etMarca.getText().toString().trim();
        String modelo  = etModelo.getText().toString().trim();
        boolean subsidio = cbSubsidio.isChecked();
        String runt    = subsidio ? etRunt.getText().toString().trim() : null;

        if (placa.isEmpty()) {
            Toast.makeText(this, "Ingresa la placa", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!placa.matches("[A-Z]{3}\\d{3}")) {
            Toast.makeText(this, "Placa inválida. Formato: ABC123", Toast.LENGTH_SHORT).show();
            return;
        }
        if (subsidio && (runt == null || runt.isEmpty())) {
            Toast.makeText(this, "Ingresa el número RUNT", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipoVehiculo = getTipoVehiculo();
        VehiculoRequest request = new VehiculoRequest(
                placa, tipoVehiculo, subsidio, runt, marca, modelo);

        ApiClient.getApiService().registrarVehiculo(request)
                .enqueue(new Callback<ApiResponse<Vehiculo>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Vehiculo>> call,
                                           Response<ApiResponse<Vehiculo>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            Toast.makeText(RegistrarVehiculoActivity.this,
                                    "Vehículo registrado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage() : "Error al registrar";
                            Toast.makeText(RegistrarVehiculoActivity.this,
                                    msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Vehiculo>> call, Throwable t) {
                        Toast.makeText(RegistrarVehiculoActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getTipoVehiculo() {
        int id = rgTipoVehiculo.getCheckedRadioButtonId();
        if (id == R.id.rb_taxi)        return "TAXI";
        if (id == R.id.rb_motocicleta) return "MOTOCICLETA";
        if (id == R.id.rb_carga)       return "CARGA";
        return "PARTICULAR";
    }
}