package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultarPrecioActivity extends AppCompatActivity {

    private Spinner spinnerZona, spinnerCombustible, spinnerVehiculo;
    private Button btnConsultar;
    private TextView tvPrecioBase, tvDescuento, tvPrecioFinal;
    private View cardResultado;

    private final String[] ZONAS = {
            "CENTRO", "ANTIOQUIA", "PACIFICA", "CARIBE",
            "EJE_CAFETERO", "ORINOQUIA", "SANTANDERES", "SUR_ANDINA", "FRONTERA"
    };
    private final String[] ZONAS_DISPLAY = {
            "Centro (Bogotá)", "Antioquia (Medellín)", "Pacífica (Cali)",
            "Caribe (Barranquilla)", "Eje Cafetero (Pereira)",
            "Orinoquía (Villavicencio)", "Santanderes (Bucaramanga)",
            "Sur Andina (Pasto)", "Frontera (Cúcuta)"
    };
    private final String[] COMBUSTIBLES = {"GASOLINA", "ACPM"};
    private final String[] COMBUSTIBLES_DISPLAY = {"Gasolina Corriente", "ACPM / Diésel"};
    private final String[] VEHICULOS = {"PARTICULAR", "TAXI", "MOTOCICLETA", "CARGA"};
    private final String[] VEHICULOS_DISPLAY = {"Particular", "Taxi", "Motocicleta", "Vehículo de carga"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_precio);

        spinnerZona        = findViewById(R.id.spinner_zona);
        spinnerCombustible = findViewById(R.id.spinner_combustible);
        spinnerVehiculo    = findViewById(R.id.spinner_vehiculo);
        btnConsultar       = findViewById(R.id.btn_consultar);
        tvPrecioBase       = findViewById(R.id.tv_precio_base);
        tvDescuento        = findViewById(R.id.tv_descuento);
        tvPrecioFinal      = findViewById(R.id.tv_precio_final);
        cardResultado      = findViewById(R.id.tv_resultado);

        setupSpinner(spinnerZona, ZONAS_DISPLAY);
        setupSpinner(spinnerCombustible, COMBUSTIBLES_DISPLAY);
        setupSpinner(spinnerVehiculo, VEHICULOS_DISPLAY);

        btnConsultar.setOnClickListener(v -> consultar());
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void consultar() {
        String zona        = ZONAS[spinnerZona.getSelectedItemPosition()];
        String combustible = COMBUSTIBLES[spinnerCombustible.getSelectedItemPosition()];
        String vehiculo    = VEHICULOS[spinnerVehiculo.getSelectedItemPosition()];

        ApiClient.getApiService()
                .consultarPrecio(zona, combustible, vehiculo)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                           Response<ApiResponse<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            Map<String, Object> data = response.body().getData();
                            NumberFormat fmt = NumberFormat.getNumberInstance(
                                    new Locale("es", "CO"));

                            double base   = ((Number) data.get("precioBase")).doubleValue();
                            double desc   = ((Number) data.get("descuentoPct")).doubleValue();
                            double final_ = ((Number) data.get("precioFinal")).doubleValue();

                            tvPrecioBase.setText("Precio base: $" + fmt.format(base) + " / galón");
                            tvDescuento.setText("Descuento subsidio: " + (int) desc + "%");
                            tvPrecioFinal.setText("$" + fmt.format(final_) + " / galón");
                            cardResultado.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(ConsultarPrecioActivity.this,
                                    "Error al consultar precio", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                        Toast.makeText(ConsultarPrecioActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
