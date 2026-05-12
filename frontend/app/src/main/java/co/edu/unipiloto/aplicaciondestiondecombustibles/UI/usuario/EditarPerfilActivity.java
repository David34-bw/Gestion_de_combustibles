package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Usuario;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {
    private TextInputEditText etNombre, etEmail, etDocumento;
    private Long usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil); // 1. Primero el layout

        etNombre = findViewById(R.id.et_nombre);
        etEmail = findViewById(R.id.et_email);
        etDocumento = findViewById(R.id.et_documento);
        usuarioId = getIntent().getLongExtra("id", -1L);
        Button btnGuardar = findViewById(R.id.btn_guardar);
        btnGuardar.setOnClickListener(v -> guardarCambios());

        // 3. CARGAR DATOS (Ahora sí, porque etNombre ya existe)
        if (usuarioId != -1L) {
            cargarDatos();
        }
    }

    private void cargarDatos() {
        ApiClient.getApiService().getUsuario(usuarioId).enqueue(new Callback<ApiResponse<Usuario>>() {
            @Override
            public void onResponse(Call<ApiResponse<Usuario>> call, Response<ApiResponse<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario u = response.body().getData();
                    etNombre.setText(u.getNombre());
                    etEmail.setText(u.getEmail());
                    etDocumento.setText(u.getNumeroDocumento());
                }
            }
            @Override public void onFailure(Call<ApiResponse<Usuario>> call, Throwable t) {}
        });
    }

    private void guardarCambios() {

        Usuario u = new Usuario();
        u.setNombre(etNombre.getText().toString().trim());
        u.setEmail(etEmail.getText().toString().trim());
        u.setNumeroDocumento(etDocumento.getText().toString().trim());

        u.setId(usuarioId);

        ApiClient.getApiService().actualizarUsuario(usuarioId, u).enqueue(new Callback<ApiResponse<Usuario>>() {
            @Override
            public void onResponse(Call<ApiResponse<Usuario>> call, Response<ApiResponse<Usuario>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditarPerfilActivity.this, "¡Datos actualizados!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    Log.e("API_ERROR", "Error: " + response.code());
                    Toast.makeText(EditarPerfilActivity.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Usuario>> call, Throwable t) {
                Log.e("API_FAILURE", t.getMessage());
                Toast.makeText(EditarPerfilActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}