package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion.EstacionDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario.UsuarioDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor.DistribuidorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador.ReguladorDashboardActivity;

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

            // Simular 2FA antes de redirigir
            Intent intent = new Intent(this, TwoFactorActivity.class);
            intent.putExtra("EMAIL", email);
            intent.putExtra("ROL", getRolSeleccionado());
            startActivity(intent);
        });

        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private String getRolSeleccionado() {
        int id = rgRole.getCheckedRadioButtonId();
        if (id == R.id.rb_estacion)     return "ESTACION";
        if (id == R.id.rb_distribuidor) return "DISTRIBUIDOR";
        if (id == R.id.rb_regulador)    return "REGULADOR";
        return "USUARIO"; // default
    }
}