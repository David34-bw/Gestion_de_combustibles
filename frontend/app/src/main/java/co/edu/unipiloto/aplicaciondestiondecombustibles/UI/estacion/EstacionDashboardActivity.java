package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.estacion;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.LinearLayout;
import java.text.NumberFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.Ventas.VentaResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.auth.LoginActivity;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network.ApiClient;


public class EstacionDashboardActivity extends AppCompatActivity {

    private View cardPrecios;
    private View cardInventario;
    private View cardRegistrarVenta;
    private View cardHistorial;
    private View headerAcciones;
    private View headerHistorial;
    private LinearLayout llVentas;
    private TextView tvSinVentas;
    private List<VentaResponse> ventasEstacion = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estacion_dashboard);
        llVentas  = findViewById(R.id.ll_ventas_estacion);
        tvSinVentas   = findViewById(R.id.tv_sin_ventas_estacion);


        SharedPreferences prefs = getSharedPreferences("fuelcontrol", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Estación");

        TextView tvWelcome  = findViewById(R.id.tv_welcome);
        Button btnLogout    = findViewById(R.id.btn_logout);
        Button btnPrecios   = findViewById(R.id.btn_consultar_precios);
        headerAcciones = findViewById(R.id.tv_acciones_header);
        headerHistorial = findViewById(R.id.tv_historial_header);
        cardPrecios = findViewById(R.id.card_precios);
        cardInventario = findViewById(R.id.card_inventario);
        cardRegistrarVenta = findViewById(R.id.card_registrar_venta);
        cardHistorial = findViewById(R.id.card_historial_ventas);
        View btnTabPrecios = findViewById(R.id.btn_tab_precios);
        View btnTabInventario = findViewById(R.id.btn_tab_inventario);
        View btnTabRegistrar = findViewById(R.id.btn_tab_registrar);
        View btnTabHistorial = findViewById(R.id.btn_tab_historial);

        tvWelcome.setText("Bienvenido, " + nombre + "\nGestiona inventario, precios y reportes.");
        btnTabPrecios.setOnClickListener(v -> mostrarSeccion("PRECIOS"));
        btnTabInventario.setOnClickListener(v -> mostrarSeccion("INVENTARIO"));
        btnTabRegistrar.setOnClickListener(v -> mostrarSeccion("REGISTRAR"));
        btnTabHistorial.setOnClickListener(v -> mostrarSeccion("HISTORIAL"));

        mostrarSeccion("PRECIOS");

        btnPrecios.setOnClickListener(v ->
                startActivity(new Intent(this, ConsultarPrecioActivity.class)));

        btnLogout.setOnClickListener(v -> {
            ApiClient.clearToken();
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Button btnInventario = findViewById(R.id.btn_inventario);
        btnInventario.setOnClickListener(v ->
                startActivity(new Intent(this, InventarioActivity.class)));

        Button btnVentas = findViewById(R.id.btn_registrar_venta);
        btnVentas.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrarVentaActivity.class)));
        Button btnGenerarEstadistica = findViewById(R.id.btn_generar_estadistica);
        btnGenerarEstadistica.setOnClickListener(v -> generarPdfEstadisticas());
        cargarVentas();

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarVentas();
    }

    private void cargarVentas() {
        ApiClient.getApiService().getMisVentas()
                .enqueue(new Callback<ApiResponse<List<VentaResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<VentaResponse>>> call,
                                           Response<ApiResponse<List<VentaResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<VentaResponse> ventas = response.body().getData();
                            ventasEstacion = ventas != null ? ventas : new ArrayList<>();
                            llVentas.removeAllViews();
                            if (ventasEstacion.isEmpty()) {
                                tvSinVentas.setVisibility(View.VISIBLE);
                            } else {
                                tvSinVentas.setVisibility(View.GONE);
                                for (VentaResponse v : ventasEstacion) {
                                    View item = getLayoutInflater().inflate(
                                            R.layout.item_venta_estacion, llVentas, false);
                                    ((TextView) item.findViewById(R.id.tv_placa_venta))
                                            .setText(v.getPlacaVehiculo() != null ? "🚗 " + v.getPlacaVehiculo() : "");
                                    ((TextView) item.findViewById(R.id.tv_comprador))
                                            .setText(v.getUsuarioNombre() != null
                                                    ? v.getUsuarioNombre() : "Anónimo");
                                    ((TextView) item.findViewById(R.id.tv_tipo_venta))
                                            .setText(v.getTipoCombustible());
                                    ((TextView) item.findViewById(R.id.tv_cantidad_venta))
                                            .setText(v.getCantidad() + " gal");
                                    TextView tvPrecioVenta = item.findViewById(R.id.tv_precio_venta);
                                    if (v.getPrecioGalon() != null && v.getTotalVenta() != null) {
                                        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "CO"));
                                        String precioTexto = "$" + fmt.format(v.getPrecioGalon()) + "/gal" +
                                                " • Total $" + fmt.format(v.getTotalVenta());
                                        tvPrecioVenta.setText(precioTexto);
                                        tvPrecioVenta.setVisibility(View.VISIBLE);
                                    } else {
                                        tvPrecioVenta.setVisibility(View.GONE);
                                    }
                                    ((TextView) item.findViewById(R.id.tv_fecha_venta))
                                            .setText(v.getFechaVenta() != null
                                                    ? v.getFechaVenta().substring(0, 10) : "");
                                    llVentas.addView(item);
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<VentaResponse>>> call, Throwable t) {}
                });
    }

    private void mostrarSeccion(String seccion) {
        headerAcciones.setVisibility(View.GONE);
        headerHistorial.setVisibility(View.GONE);
        cardPrecios.setVisibility(View.GONE);
        cardInventario.setVisibility(View.GONE);
        cardRegistrarVenta.setVisibility(View.GONE);
        cardHistorial.setVisibility(View.GONE);

        switch (seccion) {
            case "PRECIOS":
                headerAcciones.setVisibility(View.VISIBLE);
                cardPrecios.setVisibility(View.VISIBLE);
                break;
            case "INVENTARIO":
                headerAcciones.setVisibility(View.VISIBLE);
                cardInventario.setVisibility(View.VISIBLE);
                break;
            case "REGISTRAR":
                headerAcciones.setVisibility(View.VISIBLE);
                cardRegistrarVenta.setVisibility(View.VISIBLE);
                break;
            case "HISTORIAL":
                headerHistorial.setVisibility(View.VISIBLE);
                cardHistorial.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void generarPdfEstadisticas() {
        if (ventasEstacion == null || ventasEstacion.isEmpty()) {
            Toast.makeText(this, "No hay ventas para generar estadisticas.", Toast.LENGTH_SHORT).show();
            return;
        }

        int pageWidth = 595;
        int pageHeight = 842;
        int margin = 40;
        int y = margin;

        PdfDocument document = new PdfDocument();
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(18f);
        titlePaint.setFakeBoldText(true);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(12f);

        Paint headerPaint = new Paint();
        headerPaint.setColor(Color.BLACK);
        headerPaint.setTextSize(12f);
        headerPaint.setFakeBoldText(true);

        PdfDocument.Page page = document.startPage(new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create());
        Canvas canvas = page.getCanvas();

        String estacionNombre = getSharedPreferences("fuelcontrol", MODE_PRIVATE)
                .getString("nombre", "Estacion");
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        canvas.drawText("Reporte de ventas por estacion", margin, y, titlePaint);
        y += 24;
        canvas.drawText("Estacion: " + estacionNombre, margin, y, textPaint);
        y += 18;
        canvas.drawText("Generado: " + fecha, margin, y, textPaint);
        y += 24;

        double totalCantidad = 0.0;
        double totalMonto = 0.0;
        int totalVentas = ventasEstacion.size();
        for (VentaResponse venta : ventasEstacion) {
            if (venta.getCantidad() != null) {
                totalCantidad += venta.getCantidad();
            }
            if (venta.getTotalVenta() != null) {
                totalMonto += venta.getTotalVenta();
            }
        }

        canvas.drawText("Total de ventas: " + totalVentas, margin, y, textPaint);
        y += 18;
        canvas.drawText("Total galones: " + String.format(Locale.getDefault(), "%.2f", totalCantidad), margin, y, textPaint);
        y += 24;

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        canvas.drawText("Total ventas: $" + fmt.format(totalMonto), margin, y, textPaint);
        y += 24;

        canvas.drawText("Fecha", margin, y, headerPaint);
        canvas.drawText("Tipo", margin + 160, y, headerPaint);
        canvas.drawText("Cantidad", margin + 290, y, headerPaint);
        canvas.drawText("Precio", margin + 390, y, headerPaint);
        canvas.drawText("Total", margin + 490, y, headerPaint);
        y += 16;

        int pageNumber = 1;
        for (VentaResponse venta : ventasEstacion) {
            if (y > pageHeight - margin) {
                document.finishPage(page);
                pageNumber += 1;
                page = document.startPage(new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create());
                canvas = page.getCanvas();
                y = margin;
                canvas.drawText("Reporte de ventas por estacion", margin, y, titlePaint);
                y += 24;
                canvas.drawText("Fecha", margin, y, headerPaint);
                canvas.drawText("Tipo", margin + 160, y, headerPaint);
                canvas.drawText("Cantidad", margin + 290, y, headerPaint);
                canvas.drawText("Precio", margin + 390, y, headerPaint);
                canvas.drawText("Total", margin + 490, y, headerPaint);
                y += 16;
            }

            String fechaVenta = venta.getFechaVenta() != null && venta.getFechaVenta().length() >= 10
                    ? venta.getFechaVenta().substring(0, 10)
                    : "";
            String tipo = venta.getTipoCombustible() != null ? venta.getTipoCombustible() : "";
            String cantidad = venta.getCantidad() != null
                    ? String.format(Locale.getDefault(), "%.2f", venta.getCantidad()) + " gal"
                    : "0 gal";
            String precio = venta.getPrecioGalon() != null
                    ? "$" + fmt.format(venta.getPrecioGalon())
                    : "";
            String total = venta.getTotalVenta() != null
                    ? "$" + fmt.format(venta.getTotalVenta())
                    : "";

            canvas.drawText(fechaVenta, margin, y, textPaint);
            canvas.drawText(tipo, margin + 160, y, textPaint);
            canvas.drawText(cantidad, margin + 290, y, textPaint);
            canvas.drawText(precio, margin + 390, y, textPaint);
            canvas.drawText(total, margin + 490, y, textPaint);
            y += 16;
        }

        document.finishPage(page);

        String fileName = "estadisticas_ventas_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";
        boolean guardado = guardarPdfEnDescargas(document, fileName);
        if (guardado) {
            Toast.makeText(this, "PDF generado en Descargas: " + fileName, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error al generar PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean guardarPdfEnDescargas(PdfDocument document, String fileName) {
        OutputStream out = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) {
                    return false;
                }
                out = getContentResolver().openOutputStream(uri);
            } else {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                    return false;
                }
                File file = new File(downloadsDir, fileName);
                out = new FileOutputStream(file);
            }

            if (out == null) {
                return false;
            }
            document.writeTo(out);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ignored) {
            }
            document.close();
        }
    }
}
