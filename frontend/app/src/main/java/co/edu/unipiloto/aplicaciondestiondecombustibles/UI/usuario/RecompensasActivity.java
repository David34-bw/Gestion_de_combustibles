package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.puntos.PuntosResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.recompensas.RecompensaResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.CanjeRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.recompensas.CanjeResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecompensasActivity extends AppCompatActivity {

    private TextView tvPuntos;
    private LinearLayout llCatalogo;
    private TextView tvSinRecompensas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recompensas);

        tvPuntos = findViewById(R.id.tv_puntos);
        llCatalogo = findViewById(R.id.ll_catalogo);
        tvSinRecompensas = findViewById(R.id.tv_sin_recompensas);

        cargarPuntos();
        cargarCatalogo();
    }

    private void cargarPuntos() {
        ApiClient.getApiService().getSaldoPuntos()
                .enqueue(new Callback<ApiResponse<PuntosResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PuntosResponse>> call,
                                           Response<ApiResponse<PuntosResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            PuntosResponse puntos = response.body().getData();
                            int saldo = puntos != null && puntos.getPuntosAcumulados() != null
                                    ? puntos.getPuntosAcumulados() : 0;
                            tvPuntos.setText("Puntos: " + saldo);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PuntosResponse>> call, Throwable t) {
                        Toast.makeText(RecompensasActivity.this,
                                "Error cargando puntos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarCatalogo() {
        ApiClient.getApiService().getRecompensas()
                .enqueue(new Callback<ApiResponse<List<RecompensaResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<RecompensaResponse>>> call,
                                           Response<ApiResponse<List<RecompensaResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<RecompensaResponse> lista = response.body().getData();
                            llCatalogo.removeAllViews();
                            if (lista == null || lista.isEmpty()) {
                                tvSinRecompensas.setVisibility(View.VISIBLE);
                            } else {
                                tvSinRecompensas.setVisibility(View.GONE);
                                for (RecompensaResponse r : lista) {
                                    agregarRecompensa(r);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<RecompensaResponse>>> call, Throwable t) {
                        Toast.makeText(RecompensasActivity.this,
                                "Error cargando catálogo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void agregarRecompensa(RecompensaResponse r) {
        View item = getLayoutInflater().inflate(R.layout.item_recompensa, llCatalogo, false);
        ((TextView) item.findViewById(R.id.tv_recompensa_nombre)).setText(r.getNombre());
        ((TextView) item.findViewById(R.id.tv_recompensa_desc)).setText(r.getDescripcion());
        ((TextView) item.findViewById(R.id.tv_recompensa_costo))
                .setText(r.getCostoPuntos() + " pts");
        ((TextView) item.findViewById(R.id.tv_recompensa_beneficio))
                .setText("Descuento " + r.getPorcentajeDescuento() + "%");

        item.findViewById(R.id.btn_canjear).setOnClickListener(v -> canjear(r));
        llCatalogo.addView(item);
    }

    private void canjear(RecompensaResponse r) {
        ApiClient.getApiService().canjearRecompensa(new CanjeRequest(r.getId()))
                .enqueue(new Callback<ApiResponse<CanjeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CanjeResponse>> call,
                                           Response<ApiResponse<CanjeResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            Toast.makeText(RecompensasActivity.this,
                                    "Canje exitoso", Toast.LENGTH_SHORT).show();
                            cargarPuntos();
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage() : "No se pudo canjear";
                            Toast.makeText(RecompensasActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CanjeResponse>> call,
                                          Throwable t) {
                        Toast.makeText(RecompensasActivity.this,
                                "Error al canjear", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
