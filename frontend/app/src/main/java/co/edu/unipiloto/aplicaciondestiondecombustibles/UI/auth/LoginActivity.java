package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor.DistribuidorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion.EstacionDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador.ReguladorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario.UsuarioDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.AuthResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.LoginRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private RadioGroup rgRole;
    private Button btnLogin;
    private TextView tvGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail      = findViewById(R.id.et_email);
        etPassword   = findViewById(R.id.et_password);
        rgRole       = findViewById(R.id.rg_role);
        btnLogin     = findViewById(R.id.btn_login);
        tvGoRegister = findViewById(R.id.tv_go_register);

        btnLogin.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            doLogin(email, password);
        });

        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void doLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        ApiClient.getApiService().login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                   Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {

                    AuthResponse auth = response.body().getData();

                    // Guardar token para futuras llamadas
                    ApiClient.setToken(auth.getToken());

                    // Guardar datos en SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
                    prefs.edit()
                            .putString("token", auth.getToken())
                            .putLong("userId", auth.getId())
                            .putString("nombre", auth.getNombre())
                            .putString("rol", auth.getRol().toString())
                            .apply();

                    // Redirigir según rol
                    switch (auth.getRol().toString()) {
                        case "USUARIO":
                            startActivity(new Intent(LoginActivity.this, UsuarioDashboardActivity.class));
                            break;
                        case "DISTRIBUIDOR":
                            startActivity(new Intent(LoginActivity.this, DistribuidorDashboardActivity.class));
                            break;
                        case "ESTACION":
                            startActivity(new Intent(LoginActivity.this, EstacionDashboardActivity.class));
                            break;
                        case "REGULADOR":
                            startActivity(new Intent(LoginActivity.this, ReguladorDashboardActivity.class));
                            break;
                    }
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this,
                            "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRolSeleccionado() {
        int id = rgRole.getCheckedRadioButtonId();
        if (id == R.id.rb_estacion)     return "ESTACION";
        if (id == R.id.rb_distribuidor) return "DISTRIBUIDOR";
        if (id == R.id.rb_regulador)    return "REGULADOR";
        return "USUARIO";
    }
}