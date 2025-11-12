package itca.soft.renalcare.data.network;

import java.util.List;
import itca.soft.renalcare.data.models.MedicationItem;
import itca.soft.renalcare.data.models.ReminderItem;
import itca.soft.renalcare.data.models.TodayReminderStatus; // <-- 1. AÑADE ESTE IMPORT (Lo crearemos)
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RecordatorioApiService {

    // --- MEDICAMENTOS ---
    @GET("api/medicamentos/{id_paciente}")
    Call<List<MedicationItem>> getMedicamentos(@Path("id_paciente") int pacienteId);

    @POST("api/medicamentos")
    Call<ReminderItem> createMedicamento(@Body MedicationItem med);
    @PUT("api/medicamentos/{id_medicamento}")
    Call<Void> updateMedicamento(@Path("id_medicamento") int medId, @Body MedicationItem med);

    // --- RECORDATORIOS ---

    // (Ruta para la pantalla de la campana)
    @GET("api/recordatorios/{id_paciente}")
    Call<List<ReminderItem>> getPendingReminders(@Path("id_paciente") int pacienteId);

    // (Ruta para el checkbox del PerfilFragment)
    // ▼▼▼ 2. AÑADE ESTE MÉTODO ▼▼▼
    @GET("api/recordatorios/hoy/{id_paciente}")
    Call<List<TodayReminderStatus>> getTodayReminderStatus(@Path("id_paciente") int pacienteId);
    // ▲▲▲ FIN ▲▲▲

    @PUT("api/recordatorios/{id_recordatorio}")
    Call<Void> updateReminderStatus(@Path("id_recordatorio") int reminderId, @Body UpdateStatusBody body);

    @DELETE("api/recordatorios/{id_recordatorio}")
    Call<Void> deleteRecordatorio(@Path("id_recordatorio") int recordatorioId);

}