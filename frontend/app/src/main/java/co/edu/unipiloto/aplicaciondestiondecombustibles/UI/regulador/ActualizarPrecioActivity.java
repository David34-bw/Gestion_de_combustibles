package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;

import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.precios.HistorialPrecio;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.PrecioUpdateRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarPrecioActivity extends AppCompatActivity {

    private Spinner spinnerZona, spinnerCombustible;
    private TextInputEditText etNuevoPrecio;
    private Button btnActualizar;
    private LinearLayout llHistorial;
    private TextView tvSinHistorial;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_precio);

        spinnerZona       = findViewById(R.id.spinner_zona);
        spinnerCombustible= findViewById(R.id.spinner_combustible);
        etNuevoPrecio     = findViewById(R.id.et_nuevo_precio);
        btnActualizar     = findViewById(R.id.btn_actualizar_precio);
        llHistorial       = findViewById(R.id.ll_historial);
        tvSinHistorial    = findViewById(R.id.tv_sin_historial);

        setupSpinner(spinnerZona, ZONAS_DISPLAY);
        setupSpinner(spinnerCombustible, COMBUSTIBLES_DISPLAY);

        btnActualizar.setOnClickListener(v -> actualizar());
        cargarHistorial();
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void actualizar() {
        String precioStr = etNuevoPrecio.getText().toString().trim();
        if (precioStr.isEmpty()) {
            Toast.makeText(this, "Ingresa el nuevo precio", Toast.LENGTH_SHORT).show();
            return;
        }

        String zona       = ZONAS[spinnerZona.getSelectedItemPosition()];
        String combustible= COMBUSTIBLES[spinnerCombustible.getSelectedItemPosition()];
        double precio     = Double.parseDouble(precioStr);

        PrecioUpdateRequest request = new PrecioUpdateRequest(zona, combustible, precio);

        ApiClient.getApiService().actualizarPrecio(request)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                           Response<ApiResponse<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            Toast.makeText(ActualizarPrecioActivity.this,
                                    "Precio actualizado correctamente", Toast.LENGTH_SHORT).show();
                            etNuevoPrecio.setText("");
                            cargarHistorial();
                        } else {
                            Toast.makeText(ActualizarPrecioActivity.this,
                                    "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                        Toast.makeText(ActualizarPrecioActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void cargarHistorial() {
        ApiClient.getApiService().getHistorialPrecios()
                .enqueue(new Callback<ApiResponse<List<HistorialPrecio>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<HistorialPrecio>>> call,
                                           Response<ApiResponse<List<HistorialPrecio>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<HistorialPrecio> lista = response.body().getData();
                            llHistorial.removeAllViews();
                            if (lista == null || lista.isEmpty()) {
                                tvSinHistorial.setVisibility(View.VISIBLE);
                            } else {
                                tvSinHistorial.setVisibility(View.GONE);
                                for (HistorialPrecio h : lista) {
                                    agregarItemHistorial(h);
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<HistorialPrecio>>> call, Throwable t) {}
                });
    }

    private void agregarItemHistorial(HistorialPrecio h) {
        View item = getLayoutInflater().inflate(R.layout.item_historial_precio, llHistorial, false);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "CO"));

        ((TextView) item.findViewById(R.id.tv_zona_combustible))
                .setText(h.getZona() + " — " + h.getTipoCombustible());
        ((TextView) item.findViewById(R.id.tv_precio_anterior))
                .setText("Anterior: $" + fmt.format(h.getPrecioAnterior()));
        ((TextView) item.findViewById(R.id.tv_precio_nuevo))
                .setText("Nuevo: $" + fmt.format(h.getPrecioNuevo()));
        ((TextView) item.findViewById(R.id.tv_fecha_cambio))
                .setText(h.getFechaCambio() != null
                        ? h.getFechaCambio().substring(0, 10) : "");

        llHistorial.addView(item);
    }
}