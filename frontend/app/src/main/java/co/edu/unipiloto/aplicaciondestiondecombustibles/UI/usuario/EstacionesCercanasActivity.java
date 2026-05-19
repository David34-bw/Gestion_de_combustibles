package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Estacion;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstacionesCercanasActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 102;
    private static final int MAX_ESTACIONES = 5;

    private FusedLocationProviderClient fusedLocationClient;
    private LinearLayout llEstaciones;
    private TextView tvSinEstaciones;
    private Button btnPlanear;

    private Location ubicacionActual;
    private Estacion estacionSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estaciones_cercanas);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        llEstaciones = findViewById(R.id.ll_estaciones);
        tvSinEstaciones = findViewById(R.id.tv_sin_estaciones);
        btnPlanear = findViewById(R.id.btn_planear_recorrido);

        btnPlanear.setOnClickListener(v -> abrirRecorrido());

        cargarUbicacion();
    }

    private void cargarUbicacion() {
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
                        ubicacionActual = location;
                        cargarEstacionesCercanas();
                    } else {
                        Toast.makeText(this, "Activa el GPS para buscar estaciones", Toast.LENGTH_SHORT).show();
                        mostrarSinEstaciones();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    mostrarSinEstaciones();
                });
    }

    private void cargarEstacionesCercanas() {
        ApiClient.getApiService().getEstacionesPublicas()
                .enqueue(new Callback<ApiResponse<List<Estacion>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Estacion>>> call,
                                           Response<ApiResponse<List<Estacion>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<Estacion> estaciones = response.body().getData();
                            if (estaciones == null || estaciones.isEmpty()) {
                                mostrarSinEstaciones();
                                return;
                            }
                            mostrarEstacionesOrdenadas(estaciones);
                        } else {
                            mostrarSinEstaciones();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Estacion>>> call, Throwable t) {
                        Toast.makeText(EstacionesCercanasActivity.this,
                                "Error cargando estaciones", Toast.LENGTH_SHORT).show();
                        mostrarSinEstaciones();
                    }
                });
    }

    private void mostrarEstacionesOrdenadas(List<Estacion> estaciones) {
        llEstaciones.removeAllViews();
        List<Estacion> estacionesConDireccion = new ArrayList<>(estaciones);

        Collections.sort(estacionesConDireccion, new Comparator<Estacion>() {
            @Override
            public int compare(Estacion a, Estacion b) {
                double distA = distanciaEstimada(a);
                double distB = distanciaEstimada(b);
                return Double.compare(distA, distB);
            }
        });

        int mostradas = 0;
        for (Estacion estacion : estacionesConDireccion) {
            if (mostradas >= MAX_ESTACIONES) break;
            View item = getLayoutInflater().inflate(R.layout.item_estacion_cercana, llEstaciones, false);
            TextView tvNombre = item.findViewById(R.id.tv_estacion_nombre);
            TextView tvDireccion = item.findViewById(R.id.tv_estacion_direccion);
            TextView tvDistancia = item.findViewById(R.id.tv_estacion_distancia);

            tvNombre.setText(estacion.getNombre());
            tvDireccion.setText(formatearDireccion(estacion));
            double distancia = distanciaEstimada(estacion);
            if (distancia > 0) {
                tvDistancia.setText(String.format(Locale.getDefault(), "%.1f km", distancia));
            } else {
                tvDistancia.setText("Distancia no disponible");
            }

            item.setOnClickListener(v -> {
                estacionSeleccionada = estacion;
                btnPlanear.setEnabled(true);
                Toast.makeText(this, "Seleccionaste " + estacion.getNombre(), Toast.LENGTH_SHORT).show();
            });

            llEstaciones.addView(item);
            mostradas++;
        }

        tvSinEstaciones.setVisibility(mostradas == 0 ? View.VISIBLE : View.GONE);
    }

    private String formatearDireccion(Estacion estacion) {
        StringBuilder sb = new StringBuilder();
        if (estacion.getDireccion() != null) {
            sb.append(estacion.getDireccion());
        }
        if (estacion.getCiudad() != null) {
            if (sb.length() > 0) sb.append(" • ");
            sb.append(estacion.getCiudad());
        }
        if (estacion.getDepartamento() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(estacion.getDepartamento());
        }
        if (sb.length() == 0) {
            return "Dirección no disponible";
        }
        return sb.toString();
    }

    private double distanciaEstimada(Estacion estacion) {
        if (ubicacionActual == null) return -1;
        String destino = formatearDireccion(estacion);
        if ("Dirección no disponible".equals(destino)) return -1;
        try {
            Geocoder geocoder = new Geocoder(this, new Locale("es", "CO"));
            List<Address> addresses = geocoder.getFromLocationName(destino, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                float[] result = new float[1];
                Location.distanceBetween(
                        ubicacionActual.getLatitude(), ubicacionActual.getLongitude(),
                        address.getLatitude(), address.getLongitude(),
                        result);
                return result[0] / 1000.0;
            }
        } catch (IOException ignored) {
        }
        return -1;
    }

    private void mostrarSinEstaciones() {
        llEstaciones.removeAllViews();
        tvSinEstaciones.setVisibility(View.VISIBLE);
        btnPlanear.setEnabled(false);
    }

    private void abrirRecorrido() {
        if (ubicacionActual == null || estacionSeleccionada == null) {
            Toast.makeText(this, "Selecciona una estación", Toast.LENGTH_SHORT).show();
            return;
        }
        String destino = formatearDireccion(estacionSeleccionada);
        Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                + Uri.encode(destino) + "&travelmode=driving");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cargarUbicacion();
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            mostrarSinEstaciones();
        }
    }
}
