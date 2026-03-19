package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.Ventas.VentaResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.VentaRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarVentaActivity extends AppCompatActivity {

    private RadioGroup rgCombustible;
    private TextInputEditText etCantidad, etObservaciones;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_venta);

        rgCombustible   = findViewById(R.id.rg_combustible);
        etCantidad      = findViewById(R.id.et_cantidad);
        etObservaciones = findViewById(R.id.et_observaciones);
        btnRegistrar    = findViewById(R.id.btn_registrar_venta);

        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        String cantStr = etCantidad.getText().toString().trim();
        if (cantStr.isEmpty()) {
            Toast.makeText(this, "Ingresa la cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad = Double.parseDouble(cantStr);
        String tipo     = rgCombustible.getCheckedRadioButtonId() == R.id.rb_gasolina
                ? "GASOLINA" : "DIESEL";
        String obs      = etObservaciones.getText().toString().trim();

        VentaRequest request = new VentaRequest(tipo, cantidad, obs);

        ApiClient.getApiService().registrarVenta(request)
                .enqueue(new Callback<ApiResponse<VentaResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<VentaResponse>> call,
                                           Response<ApiResponse<VentaResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            VentaResponse venta = response.body().getData();
                            String msg = "Venta registrada correctamente";

                            // HU-008: alerta si stock bajo
                            if (Boolean.TRUE.equals(venta.getAlertaStockBajo())) {
                                msg += "\n⚠️ ALERTA: Stock por debajo del 25%";
                            }

                            Toast.makeText(RegistrarVentaActivity.this,
                                    msg, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage() : "Error al registrar";
                            Toast.makeText(RegistrarVentaActivity.this,
                                    msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<VentaResponse>> call, Throwable t) {
                        Toast.makeText(RegistrarVentaActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}