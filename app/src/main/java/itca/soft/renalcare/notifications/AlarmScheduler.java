package itca.soft.renalcare.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent; // <-- Asegúrate de que este import existe
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import itca.soft.renalcare.data.models.ReminderItem;

public class AlarmScheduler {

    private static final SimpleDateFormat dateFormat;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

    public static void scheduleAlarm(Context context, ReminderItem item) {

        Log.d("AlarmScheduler", "Intentando parsear fecha LOCAL: " + item.getFechaHora());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, MedicationAlarmReceiver.class);
        intent.putExtra(MedicationAlarmReceiver.EXTRA_MED_NAME, item.getTitulo());
        intent.putExtra(MedicationAlarmReceiver.EXTRA_MED_DOSE, item.getDescripcion());
        intent.putExtra(MedicationAlarmReceiver.EXTRA_NOTIFICATION_ID, item.getIdRecordatorio());

        // ▼▼▼ LÍNEA CORREGIDA ▼▼▼
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                item.getIdRecordatorio(),
                intent,
                // Era 'PendingTIntent', ahora es 'PendingIntent'
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        // ▲▲▲ FIN DE LA CORRECCIÓN ▲▲▲

        try {
            Date date = dateFormat.parse(item.getFechaHora());
            if (date == null) return;

            long alarmTimeInMillis = date.getTime();
            Log.d("AlarmScheduler", "Fecha parseada. Hora de alarma (milis): " + alarmTimeInMillis);

            if (alarmTimeInMillis <= System.currentTimeMillis()) {
                Log.w("AlarmScheduler", "Alarma omitida: La hora ya pasó.");
                return;
            }

            // Programar la alarma
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
                    Log.d("AlarmScheduler", "¡Alarma programada exitosamente!");
                } else {
                    Toast.makeText(context, "Se necesita permiso para alarmas exactas", Toast.LENGTH_LONG).show();
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
                Log.d("AlarmScheduler", "¡Alarma programada exitosamente!");
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("AlarmScheduler", "¡¡¡PARSE EXCEPTION!!! El formato de fecha no coincide.", e);
            Toast.makeText(context, "Error al programar alarma (Formato de Fecha Inválido)", Toast.LENGTH_LONG).show();
        }



    }

    public static void cancelAlarm(Context context, ReminderItem item) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Debemos recrear el *mismo* PendingIntent que se usó para crear la alarma.
        Intent intent = new Intent(context, MedicationAlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                item.getIdRecordatorio(), // Usamos el mismo ID único
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Cancelamos la alarma asociada con ese PendingIntent
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("AlarmScheduler", "Alarma cancelada para ID: " + item.getIdRecordatorio());
        }
    }
}