package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion.EstacionDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario.UsuarioDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor.DistribuidorDashboardActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.regulador.ReguladorDashboardActivity;

public class TwoFactorActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnVerify;
    private TextView tvEmail;
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor);

        etCode    = findViewById(R.id.et_code);
        btnVerify = findViewById(R.id.btn_verify);
        tvEmail   = findViewById(R.id.tv_email);

        String email = getIntent().getStringExtra("EMAIL");
        rol          = getIntent().getStringExtra("ROL");

        tvEmail.setText("Código enviado a: " + email);

        btnVerify.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (code.length() != 6) {
                Toast.makeText(this, "El código debe tener 6 dígitos", Toast.LENGTH_SHORT).show();
                return;
            }
            // Verificación simulada → redirigir según rol
            redirigirSegunRol();
        });
    }

    private void redirigirSegunRol() {
        Intent intent;

        switch (rol != null ? rol : "USUARIO") {
            case "ESTACION":
                intent = new Intent(this, EstacionDashboardActivity.class);
                break;
            case "DISTRIBUIDOR":
                intent = new Intent(this, DistribuidorDashboardActivity.class);
                break;
            case "REGULADOR":
                intent = new Intent(this, ReguladorDashboardActivity.class);
                break;
            default: // USUARIO
                intent = new Intent(this, UsuarioDashboardActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }
}
