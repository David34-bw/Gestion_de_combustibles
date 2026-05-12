package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.AuthResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.LoginRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.administrador.AdministradorDashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private RadioGroup rgRole;
    private Button btnLogin, btnUbicacion;
    private TextView tvGoRegister;
    private com.google.android.material.textfield.TextInputEditText etDireccion;  // ← tvUbicacion, no etDireccion
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etEmail      = findViewById(R.id.et_email);
        etPassword   = findViewById(R.id.et_password);
        rgRole       = findViewById(R.id.rg_role);
        btnLogin     = findViewById(R.id.btn_login);
        btnUbicacion = findViewById(R.id.btn_obtener_ubicacion); // ← inicializar ANTES de usar
        etDireccion = findViewById(R.id.et_direccion);
        tvGoRegister = findViewById(R.id.tv_go_register);

        btnUbicacion.setOnClickListener(v -> obtenerUbicacion());

        btnLogin.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            doLogin(email, password);
        });
        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void doLogin(String email, String password) {
        // Rol que el usuario seleccionó en la pantalla de login
        String rolSeleccionado = getRolSeleccionado();

        LoginRequest request = new LoginRequest(email, password);

        ApiClient.getApiService().login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                   Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {

                    AuthResponse auth = response.body().getData();
                    String rolReal = auth.getRol().toString();

                    // ── Validar que el rol coincida ──────────────
                    if (!rolReal.equals(rolSeleccionado)) {
                        String esperado = rolDisplay(rolSeleccionado);
                        String real     = rolDisplay(rolReal);
                        Toast.makeText(LoginActivity.this,
                                "Tu cuenta es de tipo \"" + real + "\".\n" +
                                        "Selecciona el tipo correcto para ingresar.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // ── Guardar sesión ───────────────────────────
                    ApiClient.setToken(auth.getToken());
                    SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
                    prefs.edit()
                            .putString("token", auth.getToken())
                            .putLong("userId", auth.getId())
                            .putString("nombre", auth.getNombre())
                            .putString("rol", rolReal)
                            .putInt("puntos", auth.getPuntosAcumulados() != null
                                    ? auth.getPuntosAcumulados() : 0)
                            .apply();

                    // ── Redirigir según rol ──────────────────────
                    switch (rolReal) {
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
                        case "ADMINISTRADOR":
                            startActivity(new Intent(LoginActivity.this, AdministradorDashboardActivity.class));
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
        if (id == R.id.rb_administrador) return "ADMINISTRADOR";
        return "USUARIO";
    }

    private String rolDisplay(String rol) {
        switch (rol) {
            case "ESTACION":     return "Estación de servicio";
            case "DISTRIBUIDOR": return "Distribuidor mayorista";
            case "REGULADOR":    return "Autoridad reguladora";
            case "ADMINISTRADOR": return "Administrador";
            default:             return "Usuario particular";
        }
    }
    private void obtenerUbicacion() {
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
                                etDireccion.setText(addresses.get(0).getAddressLine(0));
                            } else {
                                etDireccion.setText(location.getLatitude()
                                        + ", " + location.getLongitude());
                            }
                        } catch (IOException e) {
                            etDireccion.setText( location.getLatitude()
                                    + ", " + location.getLongitude());
                        }
                        etDireccion.setVisibility(View.VISIBLE);
                    } else {
                        etDireccion.setText("Activa el GPS en el emulador");
                        etDireccion.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    etDireccion.setText(e.getMessage());
                    etDireccion.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }

}
