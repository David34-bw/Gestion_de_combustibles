package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor.DistribuidorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion.EstacionDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador.ReguladorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario.UsuarioDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.AuthResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.RegisterRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // ── Campos base ────────────────────────────────
    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private RadioGroup rgRole;

    // ── Cards dinámicas ────────────────────────────
    private MaterialCardView cardUsuario, cardDistribuidor, cardRegulador;

    // ── Campos USUARIO ─────────────────────────────
    private TextInputEditText etCedula, etPlaca, etRunt;
    private RadioGroup rgTipoVehiculo, rgSubsidio;
    private TextInputLayout layoutRunt;
    private TextView tvRuntInfo;

    // ── Campos ESTACIÓN ────────────────────────────
    private MaterialCardView cardEstacion;
    private TextInputEditText etNitEstacion, etNombreEstacion, etCodigoSicom;
    private TextInputEditText etLicenciaEstacion, etDireccionEstacion;
    private TextInputEditText etCiudadEstacion, etDepartamentoEstacion;

    // ── Campos DISTRIBUIDOR ────────────────────────
    private TextInputEditText etNitDist, etNombreEmpresa, etRegistroMercantil;
    private TextInputEditText etCiudadDist, etDepartamentoDist;

    // ── Campos REGULADOR ───────────────────────────
    private TextInputEditText etNitReg, etCodigoEntidad, etCargo, etDependencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupRolListener();
        setupSubsidioListener();

        findViewById(R.id.btn_register).setOnClickListener(v -> validarYRegistrar());
    }

    private void initViews() {
        etEmail           = findViewById(R.id.et_email);
        etPassword        = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgRole            = findViewById(R.id.rg_role);

        cardUsuario      = findViewById(R.id.card_usuario);
        cardDistribuidor = findViewById(R.id.card_distribuidor);
        cardRegulador    = findViewById(R.id.card_regulador);

        etCedula        = findViewById(R.id.et_cedula);
        etPlaca         = findViewById(R.id.et_placa);
        etRunt          = findViewById(R.id.et_runt);
        rgTipoVehiculo  = findViewById(R.id.rg_tipo_vehiculo);
        rgSubsidio      = findViewById(R.id.rg_subsidio);
        layoutRunt      = findViewById(R.id.layout_runt);
        tvRuntInfo      = findViewById(R.id.tv_runt_info);

        cardEstacion            = findViewById(R.id.card_estacion);
        etNitEstacion           = findViewById(R.id.et_nit_estacion);
        etNombreEstacion        = findViewById(R.id.et_nombre_estacion);
        etCodigoSicom           = findViewById(R.id.et_codigo_sicom);
        etLicenciaEstacion      = findViewById(R.id.et_licencia_estacion);
        etDireccionEstacion     = findViewById(R.id.et_direccion_estacion);
        etCiudadEstacion        = findViewById(R.id.et_ciudad_estacion);
        etDepartamentoEstacion  = findViewById(R.id.et_departamento_estacion);

        etNitDist            = findViewById(R.id.et_nit_dist);
        etNombreEmpresa      = findViewById(R.id.et_nombre_empresa);
        etRegistroMercantil  = findViewById(R.id.et_registro_mercantil);
        etCiudadDist         = findViewById(R.id.et_ciudad_dist);
        etDepartamentoDist   = findViewById(R.id.et_departamento_dist);

        etNitReg        = findViewById(R.id.et_nit_reg);
        etCodigoEntidad = findViewById(R.id.et_codigo_entidad);
        etCargo         = findViewById(R.id.et_cargo);
        etDependencia   = findViewById(R.id.et_dependencia);
    }

    private void setupRolListener() {
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            cardUsuario.setVisibility(View.GONE);
            cardDistribuidor.setVisibility(View.GONE);
            cardRegulador.setVisibility(View.GONE);

            if (checkedId == R.id.rb_usuario) {
                cardUsuario.setVisibility(View.VISIBLE);
                cardEstacion.setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_estacion) {
                cardEstacion.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_distribuidor) {
                cardDistribuidor.setVisibility(View.VISIBLE);
                cardEstacion.setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_regulador) {
                cardRegulador.setVisibility(View.VISIBLE);
                cardEstacion.setVisibility(View.GONE);
            }
        });
    }

    private void setupSubsidioListener() {
        rgSubsidio.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_subsidio_si) {
                layoutRunt.setVisibility(View.VISIBLE);
                tvRuntInfo.setVisibility(View.VISIBLE);
            } else {
                layoutRunt.setVisibility(View.GONE);
                tvRuntInfo.setVisibility(View.GONE);
            }
        });
    }

    private void validarYRegistrar() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm  = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Completa los datos de acceso", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener mínimo 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        int rolId = rgRole.getCheckedRadioButtonId();

        if (rolId == R.id.rb_usuario) {
            if (!validarUsuario()) return;
            enviarRegistro(email, password, "USUARIO",
                    etCedula.getText().toString().trim(), null, null, null);

        } else if (rolId == R.id.rb_estacion) {
            if (!validarEstacion()) return;
            enviarRegistro(email, password, "ESTACION",
                    null,
                    etNombreEstacion.getText().toString().trim(),
                    etNitEstacion.getText().toString().trim(),
                    etCiudadEstacion.getText().toString().trim());

        } else if (rolId == R.id.rb_distribuidor) {
            if (!validarDistribuidor()) return;
            enviarRegistro(email, password, "DISTRIBUIDOR",
                    null,
                    etNombreEmpresa.getText().toString().trim(),
                    etNitDist.getText().toString().trim(),
                    etCiudadDist.getText().toString().trim());

        } else if (rolId == R.id.rb_regulador) {
            if (!validarRegulador()) return;
            enviarRegistro(email, password, "REGULADOR",
                    null,
                    etCargo.getText().toString().trim(),
                    etNitReg.getText().toString().trim(),
                    etDependencia.getText().toString().trim());
        } else {
            Toast.makeText(this, "Selecciona un rol", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarRegistro(String email, String password, String rol,
                                String numeroDocumento, String nombre,
                                String nit, String ciudad) {
        // nombre y apellido: usamos email como nombre si no hay campo específico
        String nombreFinal   = nombre != null ? nombre : email.split("@")[0];
        String apellidoFinal = ciudad != null ? ciudad : "";

        RegisterRequest request = new RegisterRequest(nombreFinal, apellidoFinal, email, password, numeroDocumento, rol);

        ApiClient.getApiService().register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                   Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {

                    AuthResponse auth = response.body().getData();
                    ApiClient.setToken(auth.getToken());

                    SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
                    prefs.edit()
                            .putString("token", auth.getToken())
                            .putLong("userId", auth.getId())
                            .putString("nombre", auth.getNombre())
                            .putString("rol", auth.getRol().toString())
                            .apply();

                    Toast.makeText(RegisterActivity.this,
                            "¡Registro exitoso!", Toast.LENGTH_SHORT).show();

                    switch (auth.getRol().toString()) {
                        case "USUARIO":
                            startActivity(new Intent(RegisterActivity.this, UsuarioDashboardActivity.class));
                            break;
                        case "DISTRIBUIDOR":
                            startActivity(new Intent(RegisterActivity.this, DistribuidorDashboardActivity.class));
                            break;
                        case "ESTACION":
                            startActivity(new Intent(RegisterActivity.this, EstacionDashboardActivity.class));
                            break;
                        case "REGULADOR":
                            startActivity(new Intent(RegisterActivity.this, ReguladorDashboardActivity.class));
                            break;
                    }
                    finish();

                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Error al registrar";
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

    private boolean validarUsuario() {
        String cedula = etCedula.getText().toString().trim();
        String placa  = etPlaca.getText().toString().trim();

        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingresa tu número de cédula", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (placa.isEmpty() || !placa.matches("[A-Za-z]{3}\\d{3}")) {
            Toast.makeText(this, "Formato de placa inválido. Usa 3 letras y 3 números", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (rgSubsidio.getCheckedRadioButtonId() == R.id.rb_subsidio_si) {
            if (etRunt.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Ingresa el número RUNT", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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
            Toast.makeText(this, "Ingresa ciudad y departamento", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Ingresa ciudad y departamento", Toast.LENGTH_SHORT).show();
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