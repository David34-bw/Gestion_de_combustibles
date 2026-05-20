package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.distribuidor;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import co.edu.unipiloto.aplicaciondestiondecombustibles.R;

public class FuelRegistrationService extends IntentService {

    public static final String EXTRA_TIPO        = "tipo";
    public static final String EXTRA_VOLUMEN     = "volumen";
    public static final String EXTRA_ESTACION    = "estacion";
    public static final int    NOTIFICATION_ID   = 7001;
    public static final String CHANNEL_ID        = "fuel_entregas_channel";

    public FuelRegistrationService() {
        super("FuelRegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String tipo     = intent.getStringExtra(EXTRA_TIPO);
        double volumen  = intent.getDoubleExtra(EXTRA_VOLUMEN, 0);
        String estacion = intent.getStringExtra(EXTRA_ESTACION);

        synchronized (this) {
            try {
                wait(10000); // Simula procesamiento en background
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.v("FuelRegistrationService",
                "Entrega procesada: " + tipo + " | " + volumen + " gal | " + estacion);

        mostrarNotificacion(tipo, volumen, estacion);
    }

    private void mostrarNotificacion(String tipo, double volumen, String estacion) {
        crearCanal();

        // Al tocar la notificación vuelve al dashboard del distribuidor
        Intent actionIntent = new Intent(this, DistribuidorDashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String titulo   = "Entrega registrada ✓";
        String mensaje  = tipo + " • " + volumen + " gal → " + estacion;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(new long[]{0, 1000})
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager nm =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Entregas de Combustible",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Confirmaciones de entregas registradas");
            canal.enableVibration(true);

            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(canal);
        }
    }
}