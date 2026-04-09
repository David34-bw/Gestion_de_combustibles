package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Usuario;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionUsuariosActivity extends AppCompatActivity {

    private Spinner spinnerFiltro;
    private LinearLayout llUsuarios;
    private TextView tvSinUsuarios;
    private List<Usuario> todosLosUsuarios = new ArrayList<>();

    private final String[] FILTROS     = {"TODOS", "USUARIO", "ESTACION", "DISTRIBUIDOR", "REGULADOR"};
    private final String[] FILTROS_DISPLAY = {"Todos", "Usuario", "Estación", "Distribuidor", "Regulador"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        spinnerFiltro = findViewById(R.id.spinner_filtro_rol);
        llUsuarios    = findViewById(R.id.ll_usuarios);
        tvSinUsuarios = findViewById(R.id.tv_sin_usuarios);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, FILTROS_DISPLAY);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filtrar(FILTROS[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        ApiClient.getApiService().getTodosUsuarios()
                .enqueue(new Callback<ApiResponse<List<Usuario>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Usuario>>> call,
                                           Response<ApiResponse<List<Usuario>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            todosLosUsuarios = response.body().getData();
                            filtrar(FILTROS[spinnerFiltro.getSelectedItemPosition()]);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Usuario>>> call, Throwable t) {
                        Toast.makeText(GestionUsuariosActivity.this,
                                "Error cargando usuarios", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filtrar(String filtro) {
        llUsuarios.removeAllViews();
        List<Usuario> filtrados = new ArrayList<>();
        for (Usuario u : todosLosUsuarios) {
            if (filtro.equals("TODOS") || filtro.equals(u.getRol())) {
                filtrados.add(u);
            }
        }
        if (filtrados.isEmpty()) {
            tvSinUsuarios.setVisibility(View.VISIBLE);
        } else {
            tvSinUsuarios.setVisibility(View.GONE);
            for (Usuario u : filtrados) agregarTarjeta(u);
        }
    }

    private void agregarTarjeta(Usuario u) {
        View tarjeta = getLayoutInflater().inflate(R.layout.item_usuario_admin, llUsuarios, false);

        TextView tvNombre  = tarjeta.findViewById(R.id.tv_nombre);
        TextView tvEmail   = tarjeta.findViewById(R.id.tv_email);
        TextView tvRol     = tarjeta.findViewById(R.id.tv_rol);
        TextView tvEstado  = tarjeta.findViewById(R.id.tv_estado);
        MaterialButton btnActivar   = tarjeta.findViewById(R.id.btn_activar);
        MaterialButton btnRol       = tarjeta.findViewById(R.id.btn_cambiar_rol);
        MaterialButton btnEliminar  = tarjeta.findViewById(R.id.btn_eliminar);

        tvNombre.setText(u.getNombre());
        tvEmail.setText(u.getEmail());
        tvRol.setText(u.getRol());

        boolean activo = Boolean.TRUE.equals(u.getActivo());
        tvEstado.setText(activo ? "✓ Activo" : "✗ Inactivo");
        tvEstado.setTextColor(getResources().getColor(
                activo ? R.color.fuel_success : R.color.fuel_error, null));
        btnActivar.setText(activo ? "Desactivar" : "Activar");

        btnActivar.setOnClickListener(v -> toggleActivar(u, activo));
        btnRol.setOnClickListener(v -> mostrarDialogRol(u));
        btnEliminar.setOnClickListener(v -> confirmarEliminar(u));

        llUsuarios.addView(tarjeta);
    }

    private void toggleActivar(Usuario u, boolean activo) {
        Call<ApiResponse<Void>> call = activo
                ? ApiClient.getApiService().desactivarUsuario(u.getId())
                : ApiClient.getApiService().desactivarUsuario(u.getId());

        ApiClient.getApiService().desactivarUsuario(u.getId())
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        Toast.makeText(GestionUsuariosActivity.this,
                                activo ? "Usuario desactivado" : "Usuario activado",
                                Toast.LENGTH_SHORT).show();
                        cargarUsuarios();
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Toast.makeText(GestionUsuariosActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogRol(Usuario u) {
        String[] roles = {"USUARIO", "ESTACION", "DISTRIBUIDOR", "REGULADOR"};
        new AlertDialog.Builder(this)
                .setTitle("Cambiar rol de " + u.getNombre())
                .setItems(roles, (dialog, which) -> {
                    Map<String, String> body = new HashMap<>();
                    body.put("rol", roles[which]);
                    ApiClient.getApiService().cambiarRol(u.getId(), body)
                            .enqueue(new Callback<ApiResponse<Usuario>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Usuario>> call,
                                                       Response<ApiResponse<Usuario>> response) {
                                    Toast.makeText(GestionUsuariosActivity.this,
                                            "Rol actualizado", Toast.LENGTH_SHORT).show();
                                    cargarUsuarios();
                                }
                                @Override
                                public void onFailure(Call<ApiResponse<Usuario>> call, Throwable t) {
                                    Toast.makeText(GestionUsuariosActivity.this,
                                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    private void confirmarEliminar(Usuario u) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de eliminar a " + u.getNombre() + "? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    ApiClient.getApiService().eliminarUsuario(u.getId())
                            .enqueue(new Callback<ApiResponse<Void>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Void>> call,
                                                       Response<ApiResponse<Void>> response) {
                                    Toast.makeText(GestionUsuariosActivity.this,
                                            "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                    cargarUsuarios();
                                }
                                @Override
                                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                                    Toast.makeText(GestionUsuariosActivity.this,
                                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}