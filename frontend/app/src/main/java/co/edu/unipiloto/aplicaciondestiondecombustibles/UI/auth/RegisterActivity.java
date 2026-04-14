package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor.DistribuidorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion.EstacionDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador.ReguladorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario.UsuarioDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.AuthResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.RegisterRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // Campos base
    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private RadioGroup rgRole;

    // Cards dinámicas
    private MaterialCardView cardEstacion, cardDistribuidor, cardRegulador;

    // Campos ESTACIÓN
    private TextInputEditText etNitEstacion, etNombreEstacion, etCodigoSicom;
    private TextInputEditText etLicenciaEstacion, etDireccionEstacion;
    private TextInputEditText etCiudadEstacion, etDepartamentoEstacion;

    // Campos DISTRIBUIDOR
    private TextInputEditText etNitDist, etNombreEmpresa, etRegistroMercantil;
    private TextInputEditText etCiudadDist, etDepartamentoDist;

    // Campos REGULADOR
    private TextInputEditText etNitReg, etCodigoEntidad, etCargo, etDependencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupRolListener();
        findViewById(R.id.btn_register).setOnClickListener(v -> validarYRegistrar());
    }

    private void initViews() {
        etEmail           = findViewById(R.id.et_email);
        etPassword        = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgRole            = findViewById(R.id.rg_role);

        cardEstacion     = findViewById(R.id.card_estacion);
        cardDistribuidor = findViewById(R.id.card_distribuidor);
        cardRegulador    = findViewById(R.id.card_regulador);

        // Campos estación
        etNitEstacion          = findViewById(R.id.et_nit_estacion);
        etNombreEstacion       = findViewById(R.id.et_nombre_estacion);
        etCodigoSicom          = findViewById(R.id.et_codigo_sicom);
        etLicenciaEstacion     = findViewById(R.id.et_licencia_estacion);
        etDireccionEstacion    = findViewById(R.id.et_direccion_estacion);
        etCiudadEstacion       = findViewById(R.id.et_ciudad_estacion);
        etDepartamentoEstacion = findViewById(R.id.et_departamento_estacion);

        // Campos distribuidor
        etNitDist           = findViewById(R.id.et_nit_dist);
        etNombreEmpresa     = findViewById(R.id.et_nombre_empresa);
        etRegistroMercantil = findViewById(R.id.et_registro_mercantil);
        etCiudadDist        = findViewById(R.id.et_ciudad_dist);
        etDepartamentoDist  = findViewById(R.id.et_departamento_dist);

        // Campos regulador
        etNitReg        = findViewById(R.id.et_nit_reg);
        etCodigoEntidad = findViewById(R.id.et_codigo_entidad);
        etCargo         = findViewById(R.id.et_cargo);
        etDependencia   = findViewById(R.id.et_dependencia);

        // Ocultar todas al inicio
        cardEstacion.setVisibility(View.GONE);
        cardDistribuidor.setVisibility(View.GONE);
        cardRegulador.setVisibility(View.GONE);
    }

    private void setupRolListener() {
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            // Ocultar todas
            cardEstacion.setVisibility(View.GONE);
            cardDistribuidor.setVisibility(View.GONE);
            cardRegulador.setVisibility(View.GONE);

            // Mostrar la que corresponde
            if (checkedId == R.id.rb_estacion) {
                cardEstacion.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_distribuidor) {
                cardDistribuidor.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_regulador) {
                cardRegulador.setVisibility(View.VISIBLE);
            }
            // rb_usuario no muestra card aquí porque sus campos están en el Dashboard
        });
    }

    private void validarYRegistrar() {
        String email   = etEmail.getText().toString().trim();
        String pass    = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.requestFocus();
            Toast.makeText(this, "El correo electrónico es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.requestFocus();
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty()) {
            etPassword.requestFocus();
            Toast.makeText(this, "La contraseña es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 8) {
            etPassword.requestFocus();
            Toast.makeText(this, "La contraseña debe tener mínimo 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirm.isEmpty()) {
            etConfirmPassword.requestFocus();
            Toast.makeText(this, "Confirma tu contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(confirm)) {
            etConfirmPassword.requestFocus();
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        int rolId = rgRole.getCheckedRadioButtonId();
        if (rolId == -1) {
            Toast.makeText(this, "Selecciona un tipo de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rolId == R.id.rb_usuario) {
            RegisterRequest request = new RegisterRequest(email, pass, "USUARIO");
            enviarRegistro(request, "USUARIO");

        } else if (rolId == R.id.rb_estacion) {
            if (!validarEstacion()) return;
            RegisterRequest request = new RegisterRequest(
                    etNombreEstacion.getText().toString().trim(),
                    email, pass,
                    etNitEstacion.getText().toString().trim(),
                    "ESTACION");
            enviarRegistro(request, "ESTACION");

        } else if (rolId == R.id.rb_distribuidor) {
            if (!validarDistribuidor()) return;
            RegisterRequest request = new RegisterRequest(
                    etNombreEmpresa.getText().toString().trim(),
                    email, pass,
                    etNitDist.getText().toString().trim(),
                    "DISTRIBUIDOR");
            enviarRegistro(request, "DISTRIBUIDOR");

        } else if (rolId == R.id.rb_regulador) {
            if (!validarRegulador()) return;
            RegisterRequest request = new RegisterRequest(
                    etCargo.getText().toString().trim(),
                    email, pass,
                    etNitReg.getText().toString().trim(),
                    "REGULADOR");
            enviarRegistro(request, "REGULADOR");
        }
    }

    private void enviarRegistro(RegisterRequest request, String rol) {
        ApiClient.getApiService().register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                   Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {

                    AuthResponse auth = response.body().getData();
                    ApiClient.setToken(auth.getToken());

                    getSharedPreferences("fuelcontrol", MODE_PRIVATE)
                            .edit()
                            .putString("token", auth.getToken())
                            .putLong("userId", auth.getId())
                            .putString("nombre", auth.getNombre())
                            .putString("rol", rol)
                            .apply();

                    Toast.makeText(RegisterActivity.this,
                            "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                    navegarSegunRol(rol);
                    finish();

                } else {
                    String msg = response.body() != null
                            ? response.body().getMessage() : "Error al registrar";
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navegarSegunRol(String rol) {
        Intent intent;
        switch (rol) {
            case "USUARIO":
                intent = new Intent(this, UsuarioDashboardActivity.class); break;
            case "DISTRIBUIDOR":
                intent = new Intent(this, DistribuidorDashboardActivity.class); break;
            case "ESTACION":
                intent = new Intent(this, EstacionDashboardActivity.class); break;
            default:
                intent = new Intent(this, ReguladorDashboardActivity.class); break;
        }
        startActivity(intent);
    }

    private boolean validarEstacion() {
        if (etNitEstacion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el NIT de la estación", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etNombreEstacion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre de la estación", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCodigoSicom.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el código SICOM", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDireccionEstacion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa la dirección", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCiudadEstacion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa la ciudad", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validarDistribuidor() {
        if (etNitDist.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el NIT del distribuidor", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etNombreEmpresa.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre de la empresa", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCiudadDist.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa la ciudad", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validarRegulador() {
        if (etNitReg.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el NIT de la entidad", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCargo.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el cargo", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDependencia.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa la dependencia", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}