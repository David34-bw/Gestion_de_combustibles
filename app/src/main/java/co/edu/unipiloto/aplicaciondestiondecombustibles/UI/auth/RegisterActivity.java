package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth;

import android.content.Intent;
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
        // Base
        etEmail           = findViewById(R.id.et_email);
        etPassword        = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgRole            = findViewById(R.id.rg_role);

        // Cards
        cardUsuario      = findViewById(R.id.card_usuario);
        cardDistribuidor = findViewById(R.id.card_distribuidor);
        cardRegulador    = findViewById(R.id.card_regulador);

        // Usuario
        etCedula        = findViewById(R.id.et_cedula);
        etPlaca         = findViewById(R.id.et_placa);
        etRunt          = findViewById(R.id.et_runt);
        rgTipoVehiculo  = findViewById(R.id.rg_tipo_vehiculo);
        rgSubsidio      = findViewById(R.id.rg_subsidio);
        layoutRunt      = findViewById(R.id.layout_runt);
        tvRuntInfo      = findViewById(R.id.tv_runt_info);

        // Estación
        cardEstacion            = findViewById(R.id.card_estacion);
        etNitEstacion           = findViewById(R.id.et_nit_estacion);
        etNombreEstacion        = findViewById(R.id.et_nombre_estacion);
        etCodigoSicom           = findViewById(R.id.et_codigo_sicom);
        etLicenciaEstacion      = findViewById(R.id.et_licencia_estacion);
        etDireccionEstacion     = findViewById(R.id.et_direccion_estacion);
        etCiudadEstacion        = findViewById(R.id.et_ciudad_estacion);
        etDepartamentoEstacion  = findViewById(R.id.et_departamento_estacion);

        // Distribuidor
        etNitDist            = findViewById(R.id.et_nit_dist);
        etNombreEmpresa      = findViewById(R.id.et_nombre_empresa);
        etRegistroMercantil  = findViewById(R.id.et_registro_mercantil);
        etCiudadDist         = findViewById(R.id.et_ciudad_dist);
        etDepartamentoDist   = findViewById(R.id.et_departamento_dist);

        // Regulador
        etNitReg        = findViewById(R.id.et_nit_reg);
        etCodigoEntidad = findViewById(R.id.et_codigo_entidad);
        etCargo         = findViewById(R.id.et_cargo);
        etDependencia   = findViewById(R.id.et_dependencia);
    }

    // ── Mostrar/ocultar card según rol seleccionado ─
    private void setupRolListener() {
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            // Ocultar todas las cards primero
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
            // rb_estacion: no necesita campos extra en registro
        });
    }

    // ── Mostrar campo RUNT solo si tiene subsidio ───
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

    // ── Validaciones y registro ─────────────────────
    private void validarYRegistrar() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm  = etConfirmPassword.getText().toString().trim();

        // Validaciones base
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

        // Validaciones por rol
        int rolId = rgRole.getCheckedRadioButtonId();

        if (rolId == R.id.rb_usuario) {
            if (!validarUsuario()) return;
        } else if (rolId == R.id.rb_estacion) {
            if (!validarEstacion()) return;   // ← nuevo
        } else if (rolId == R.id.rb_distribuidor) {
            if (!validarDistribuidor()) return;
        } else if (rolId == R.id.rb_regulador) {
            if (!validarRegulador()) return;
        }

        // Todo OK → ir al login
        Toast.makeText(this, "✓ Registro enviado. Será verificado antes de activarse.",
                Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private boolean validarUsuario() {
        String cedula = etCedula.getText().toString().trim();
        String placa  = etPlaca.getText().toString().trim();

        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingresa tu número de cédula", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (placa.isEmpty() || placa.length() < 5) {
            Toast.makeText(this, "Ingresa una placa válida (ej: ABC123)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!placa.matches("[A-Za-z]{3}\\d{3}")) {
            Toast.makeText(this, "Formato de placa inválido. Usa 3 letras y 3 números", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Si tiene subsidio, verificar RUNT
        if (rgSubsidio.getCheckedRadioButtonId() == R.id.rb_subsidio_si) {
            String runt = etRunt.getText().toString().trim();
            if (runt.isEmpty()) {
                Toast.makeText(this, "Ingresa el número RUNT para verificar el subsidio", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean validarEstacion() {
        String nit       = etNitEstacion.getText().toString().trim();
        String nombre    = etNombreEstacion.getText().toString().trim();
        String sicom     = etCodigoSicom.getText().toString().trim();
        String licencia  = etLicenciaEstacion.getText().toString().trim();
        String direccion = etDireccionEstacion.getText().toString().trim();
        String ciudad    = etCiudadEstacion.getText().toString().trim();
        String depto     = etDepartamentoEstacion.getText().toString().trim();

        if (nit.isEmpty() || !nit.contains("-")) {
            Toast.makeText(this, "Ingresa el NIT con dígito de verificación (ej: 900123456-1)",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre comercial de la estación",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sicom.isEmpty()) {
            Toast.makeText(this, "Ingresa el código SICOM asignado por MinEnergía",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (licencia.isEmpty()) {
            Toast.makeText(this, "Ingresa el número de licencia de operación",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (direccion.isEmpty()) {
            Toast.makeText(this, "Ingresa la dirección de la estación",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ciudad.isEmpty() || depto.isEmpty()) {
            Toast.makeText(this, "Ingresa ciudad y departamento",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validarDistribuidor() {
        String nit      = etNitDist.getText().toString().trim();
        String nombre   = etNombreEmpresa.getText().toString().trim();
        String registro = etRegistroMercantil.getText().toString().trim();
        String ciudad   = etCiudadDist.getText().toString().trim();
        String depto    = etDepartamentoDist.getText().toString().trim();

        if (nit.isEmpty() || !nit.contains("-")) {
            Toast.makeText(this, "Ingresa el NIT con dígito de verificación (ej: 900123456-1)",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre legal de la empresa", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (registro.isEmpty()) {
            Toast.makeText(this, "Ingresa el número de registro mercantil", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ciudad.isEmpty() || depto.isEmpty()) {
            Toast.makeText(this, "Ingresa ciudad y departamento", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validarRegulador() {
        String nit      = etNitReg.getText().toString().trim();
        String codigo   = etCodigoEntidad.getText().toString().trim();
        String cargo    = etCargo.getText().toString().trim();
        String depencia = etDependencia.getText().toString().trim();

        if (nit.isEmpty()) {
            Toast.makeText(this, "Ingresa el NIT de la entidad", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (codigo.isEmpty()) {
            Toast.makeText(this, "Ingresa el código CHIP de la entidad", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cargo.isEmpty()) {
            Toast.makeText(this, "Ingresa el cargo del funcionario", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (depencia.isEmpty()) {
            Toast.makeText(this, "Ingresa la dependencia o ministerio", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}