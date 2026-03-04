// ─────────────────────────────────────────────────────────────────
// EJEMPLO: Cómo usar el ApiClient en LoginActivity.java (Android)
// ─────────────────────────────────────────────────────────────────

// 1. Agrega en app/build.gradle (Module: app):
//
// dependencies {
//     implementation 'com.squareup.retrofit2:retrofit:2.9.0'
//     implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
//     implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
// }

// 2. Agrega en AndroidManifest.xml (dentro de <manifest>):
//
// <uses-permission android:name="android.permission.INTERNET" />
// <application
//     android:usesCleartextTraffic="true"   ← Solo para desarrollo local HTTP
//     ...>

// ─────────────────────────────────────────────────────────────────
// LoginActivity.java (ejemplo de llamada)
// ─────────────────────────────────────────────────────────────────

import co.edu.unipiloto.aplicaciongestiondecombustibles.model.*;
import co.edu.unipiloto.aplicaciongestiondecombustibles.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Dentro de tu método de login (ejemplo onClick):
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

                // Guardar datos del usuario en SharedPreferences
                SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
                prefs.edit()
                     .putString("token", auth.getToken())
                     .putLong("userId", auth.getId())
                     .putString("nombre", auth.getNombre())
                     .putString("rol", auth.getRol())
                     .apply();

                // Redirigir según rol
                switch (auth.getRol()) {
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

// ─────────────────────────────────────────────────────────────────
// En todas las demás Activities, restaura el token al iniciar:
// ─────────────────────────────────────────────────────────────────

// @Override
// protected void onCreate(Bundle savedInstanceState) {
//     super.onCreate(savedInstanceState);
//
//     SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
//     String token = prefs.getString("token", null);
//     if (token != null) {
//         ApiClient.setToken(token);
//     } else {
//         // No hay sesión, ir a login
//         startActivity(new Intent(this, LoginActivity.class));
//         finish();
//         return;
//     }
// }
