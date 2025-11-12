package itca.soft.renalcare;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

import itca.soft.renalcare.notifications.MedicationAlarmReceiver;

public class RenalCareApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = MedicationAlarmReceiver.CHANNEL_ID;
            CharSequence name = "Recordatorios de Medicamentos";
            String description = "Canal para las alarmas de medicamentos de RenalCare";

            // IMPORTANCIA ALTA (Habilitado por defecto para aparecer en la barra)
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Le decimos al canal que SÍ debe permitir vibración y luces
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            // El patrón de vibración para el canal (la notificación lo anulará, pero es bueno tenerlo)
            channel.setVibrationPattern(new long[]{0, 500, 1000});

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}