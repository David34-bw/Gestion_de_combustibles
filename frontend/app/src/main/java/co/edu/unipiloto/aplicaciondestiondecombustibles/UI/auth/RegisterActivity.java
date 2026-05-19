package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor.DistribuidorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion.EstacionDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador.ReguladorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario.UsuarioDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.AuthResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.RegisterRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.administrador.AdministradorDashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    // Campos base
    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private TextInputEditText etDocumentoUsuario;
    private TextInputLayout tilDocumentoUsuario;
    private RadioGroup rgRole;
    private MaterialButton btnUbicacionRegistro;
    private TextInputLayout tilDireccionUsuario;
    private TextInputEditText etDireccionUsuario;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private String ciudadUsuario;
    private String departamentoUsuario;

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
    private MaterialCardView cardAdministrador;
    private TextInputEditText etCodigoAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        setupRolListener();
        actualizarVisibilidadUsuario(rgRole.getCheckedRadioButtonId());
        findViewById(R.id.btn_register).setOnClickListener(v -> validarYRegistrar());
        btnUbicacionRegistro.setOnClickListener(v -> obtenerUbicacionUsuario());
    }

    private void initViews() {
        etEmail           = findViewById(R.id.et_email);
        etPassword        = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etDocumentoUsuario = findViewById(R.id.et_documento_usuario);
        tilDocumentoUsuario = findViewById(R.id.til_documento_usuario);
        btnUbicacionRegistro = findViewById(R.id.btn_obtener_ubicacion_registro);
        tilDireccionUsuario = findViewById(R.id.til_direccion_usuario);
        etDireccionUsuario = findViewById(R.id.et_direccion_usuario);
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

        cardAdministrador = findViewById(R.id.card_administrador);
        etCodigoAdmin     = findViewById(R.id.et_codigo_admin);
        cardAdministrador.setVisibility(View.GONE);
    }

    private void setupRolListener() {
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            // Ocultar todas
            cardEstacion.setVisibility(View.GONE);
            cardDistribuidor.setVisibility(View.GONE);
            cardRegulador.setVisibility(View.GONE);
            cardAdministrador.setVisibility(View.GONE);

            if (checkedId == R.id.rb_estacion) {
                cardEstacion.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_distribuidor) {
                cardDistribuidor.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_regulador) {
                cardRegulador.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_administrador) {
                cardAdministrador.setVisibility(View.VISIBLE);
            }

            actualizarVisibilidadUsuario(checkedId);
        });
    }

    private void actualizarVisibilidadUsuario(int checkedId) {
        boolean esUsuario = checkedId == R.id.rb_usuario;
        if (tilDocumentoUsuario != null) {
            tilDocumentoUsuario.setVisibility(esUsuario ? View.VISIBLE : View.GONE);
        }
        if (btnUbicacionRegistro != null) {
            btnUbicacionRegistro.setVisibility(esUsuario ? View.VISIBLE : View.GONE);
        }
        if (tilDireccionUsuario != null) {
            tilDireccionUsuario.setVisibility(esUsuario ? View.VISIBLE : View.GONE);
        }
    }

    private void validarYRegistrar() {
        String email   = etEmail.getText().toString().trim();
        String pass    = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();
        String documento = etDocumentoUsuario.getText().toString().trim();
        int rolId = rgRole.getCheckedRadioButtonId();

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

        if (rolId == -1) {
            Toast.makeText(this, "Selecciona un tipo de usuario", Toast.LENGTH_SHORT).show();
            return;
        }


        if (rolId == R.id.rb_usuario) {
            if (documento.isEmpty()) {
                etDocumentoUsuario.requestFocus();
                Toast.makeText(this, "Ingresa tu número de documento", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (rolId == R.id.rb_usuario) {
            RegisterRequest request = new RegisterRequest(
                    email,
                    pass,
                    "USUARIO",
                    documento);
            String direccion = etDireccionUsuario.getText().toString().trim();
            if (!direccion.isEmpty()) {
                request.setDireccion(direccion);
                if (ciudadUsuario != null) {
                    request.setCiudad(ciudadUsuario);
                }
                if (departamentoUsuario != null) {
                    request.setDepartamento(departamentoUsuario);
                }
            }
            enviarRegistro(request, "USUARIO");

        } else if (rolId == R.id.rb_estacion) {
            if (!validarEstacion()) return;
            RegisterRequest request = new RegisterRequest(
                    etNombreEstacion.getText().toString().trim(),
                    email, pass,
                    etNitEstacion.getText().toString().trim(),
                    "ESTACION");
            request.setDireccion(etDireccionEstacion.getText().toString().trim());
            request.setCiudad(etCiudadEstacion.getText().toString().trim());
            request.setDepartamento(etDepartamentoEstacion.getText().toString().trim());
            enviarRegistro(request, "ESTACION");

        } else if (rolId == R.id.rb_distribuidor) {
            if (!validarDistribuidor()) return;
            RegisterRequest request = new RegisterRequest(
                    etNombreEmpresa.getText().toString().trim(),
                    email, pass,
                    etNitDist.getText().toString().trim(),
                    "DISTRIBUIDOR");
            request.setCiudad(etCiudadDist.getText().toString().trim());
            request.setDepartamento(etDepartamentoDist.getText().toString().trim());
            enviarRegistro(request, "DISTRIBUIDOR");

        } else if (rolId == R.id.rb_regulador) {
            if (!validarRegulador()) return;
            RegisterRequest request = new RegisterRequest(
                    etCargo.getText().toString().trim(),
                    email, pass,
                    etNitReg.getText().toString().trim(),
                    "REGULADOR");
            request.setCodigoEntidad(etCodigoEntidad.getText().toString().trim());
            request.setCargo(etCargo.getText().toString().trim());
            request.setDependencia(etDependencia.getText().toString().trim());
            enviarRegistro(request, "REGULADOR");
        }else if (rolId == R.id.rb_administrador) {
            if (!validarAdministrador()) return;
            RegisterRequest request = new RegisterRequest(
                    "Administrador",
                    email, pass,
                    null,
                    "ADMINISTRADOR");
            request.setCodigoAdmin(etCodigoAdmin.getText().toString().trim());
            enviarRegistro(request, "ADMINISTRADOR");
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
                            .putInt("puntos", auth.getPuntosAcumulados() != null
                                    ? auth.getPuntosAcumulados() : 0)
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
            case "ADMINISTRADOR":                                                    // ← nuevo
                intent = new Intent(this, AdministradorDashboardActivity.class); break;
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
        if (etDepartamentoEstacion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el departamento", Toast.LENGTH_SHORT).show();
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
        if (etDepartamentoDist.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el departamento", Toast.LENGTH_SHORT).show();
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
        if (etCodigoEntidad.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el código de la entidad", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validarAdministrador() {
        String codigo = etCodigoAdmin.getText().toString().trim();
        if (codigo.isEmpty()) {
            Toast.makeText(this, "Ingresa el código de administrador", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!codigo.equals("555")) {
            Toast.makeText(this, "Código de administrador incorrecto", Toast.LENGTH_SHORT).show();
            etCodigoAdmin.setText("");
            return false;
        }
        return true;
    }

    private void obtenerUbicacionUsuario() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
            return;
        }

        fusedLocationClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(this, new Locale("es", "CO"));
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                etDireccionUsuario.setText(address.getAddressLine(0));
                                ciudadUsuario = address.getLocality();
                                departamentoUsuario = address.getAdminArea();
                            } else {
                                etDireccionUsuario.setText(location.getLatitude()
                                        + ", " + location.getLongitude());
                            }
                        } catch (IOException e) {
                            etDireccionUsuario.setText(location.getLatitude()
                                    + ", " + location.getLongitude());
                        }
                    } else {
                        etDireccionUsuario.setText("Activa el GPS en el emulador");
                    }
                })
                .addOnFailureListener(e -> etDireccionUsuario.setText(e.getMessage()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionUsuario();
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }
}
