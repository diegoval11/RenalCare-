package itca.soft.renalcare.notifications;

import android.app.Notification; // <-- ¡ASEGÚRATE DE IMPORTAR ESTE!
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import itca.soft.renalcare.MainActivity;
import itca.soft.renalcare.R;

public class MedicationAlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "MEDICATION_CHANNEL";
    public static final String EXTRA_MED_NAME = "EXTRA_MED_NAME";
    public static final String EXTRA_MED_DOSE = "EXTRA_MED_DOSE";
    public static final String EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID";

    @Override
    public void onReceive(Context context, Intent intent) {

        String medName = intent.getStringExtra(EXTRA_MED_NAME);
        String medDose = intent.getStringExtra(EXTRA_MED_DOSE);
        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                notificationId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        long[] vibrationPattern = {0, 1000, 1000}; // Vibra 1s, pausa 1s, repite

        // 1. Construir el Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("¡No olvides tu medicina: " + medName + "!")
                .setContentText(medDose)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setLights(Color.RED, 500, 500)
                // --- TU CORRECCIÓN (ES CORRECTA) ---
                .setVibrate(vibrationPattern); // 2. Usar patrón de vibración

        // 2. Obtener el NotificationManager
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // --- LA CORRECCIÓN DE 'setFlags' ---

        // 3. Construir la notificación (aún sin la bandera)
        Notification notification = builder.build();

        // 4. ¡LA CLAVE! Añadir la bandera INSISTENT al objeto final
        // Esto hace que suene y vibre repetidamente
        notification.flags |= Notification.FLAG_INSISTENT;

        // 5. Mostrar la notificación modificada
        if (manager != null) {
            manager.notify(notificationId, notification);
        }
    }
}