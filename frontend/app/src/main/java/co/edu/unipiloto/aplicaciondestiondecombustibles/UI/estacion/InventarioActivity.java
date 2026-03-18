package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Estacion;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventarioActivity extends AppCompatActivity {

    private TextView tvStockGasolina, tvStockDiesel, tvAlertaGasolina, tvAlertaDiesel;
    private TextInputEditText etCantidad;
    private RadioGroup rgTipo;
    private Button btnAgregar;
    private View cardAlerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        tvStockGasolina  = findViewById(R.id.tv_stock_gasolina);
        tvStockDiesel    = findViewById(R.id.tv_stock_diesel);
        tvAlertaGasolina = findViewById(R.id.tv_alerta_gasolina);
        tvAlertaDiesel   = findViewById(R.id.tv_alerta_diesel);
        etCantidad       = findViewById(R.id.et_cantidad);
        rgTipo           = findViewById(R.id.rg_tipo_combustible);
        btnAgregar       = findViewById(R.id.btn_agregar_stock);
        cardAlerta       = findViewById(R.id.card_alerta);

        cargarInventario();

        btnAgregar.setOnClickListener(v -> agregarStock());
    }

    private void cargarInventario() {
        ApiClient.getApiService().getMiEstacion()
                .enqueue(new Callback<ApiResponse<Estacion>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Estacion>> call,
                                           Response<ApiResponse<Estacion>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            mostrarInventario(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Estacion>> call, Throwable t) {
                        Toast.makeText(InventarioActivity.this,
                                "Error cargando inventario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarInventario(Estacion estacion) {
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "CO"));

        double stockGas   = estacion.getStockGasolina() != null ? estacion.getStockGasolina() : 0;
        double stockDiesel= estacion.getStockDiesel()   != null ? estacion.getStockDiesel()   : 0;
        double capGas     = estacion.getCapacidadGasolina() != null ? estacion.getCapacidadGasolina() : 0;
        double capDiesel  = estacion.getCapacidadDiesel()   != null ? estacion.getCapacidadDiesel()   : 0;

        tvStockGasolina.setText(fmt.format(stockGas) + " gal");
        tvStockDiesel.setText(fmt.format(stockDiesel) + " gal");

        // Verificar alertas HU-008: alerta si stock < 25% de capacidad
        boolean alertaGas    = capGas > 0 && stockGas < (capGas * 0.25);
        boolean alertaDiesel = capDiesel > 0 && stockDiesel < (capDiesel * 0.25);

        if (alertaGas || alertaDiesel) {
            cardAlerta.setVisibility(View.VISIBLE);
            tvAlertaGasolina.setVisibility(alertaGas ? View.VISIBLE : View.GONE);
            tvAlertaDiesel.setVisibility(alertaDiesel ? View.VISIBLE : View.GONE);
        } else {
            cardAlerta.setVisibility(View.GONE);
        }
    }

    private void agregarStock() {
        String cantStr = etCantidad.getText().toString().trim();
        if (cantStr.isEmpty()) {
            Toast.makeText(this, "Ingresa una cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad = Double.parseDouble(cantStr);
        boolean esGasolina = rgTipo.getCheckedRadioButtonId() == R.id.rb_gasolina;

        Map<String, Double> stock = new HashMap<>();
        if (esGasolina) {
            stock.put("gasolina", cantidad);
        } else {
            stock.put("diesel", cantidad);
        }

        ApiClient.getApiService().actualizarMiStock(stock)
                .enqueue(new Callback<ApiResponse<Estacion>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Estacion>> call,
                                           Response<ApiResponse<Estacion>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            Toast.makeText(InventarioActivity.this,
                                    "Stock actualizado", Toast.LENGTH_SHORT).show();
                            etCantidad.setText("");
                            mostrarInventario(response.body().getData());
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage() : "Error";
                            Toast.makeText(InventarioActivity.this,
                                    msg, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Estacion>> call, Throwable t) {
                        Toast.makeText(InventarioActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}