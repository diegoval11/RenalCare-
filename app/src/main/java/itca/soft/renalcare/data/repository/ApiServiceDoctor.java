package itca.soft.renalcare.data.repository;

import itca.soft.renalcare.data.models.*;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiServiceDoctor {
    String BASE_URL = "http://192.168.1.163:3000/api/";

    // ========== PACIENTES ==========
    @POST("doctores/pacientes/crear")
    Call<PacienteDoctor> crearPacienteDesdeDoctor(@Body Map<String, Object> pacienteData);

    @GET("doctores/pacientes/doctor/{id_doctor}")
    Call<List<PacienteDoctor>> getPacientesByDoctor(@Path("id_doctor") int id_doctor);

    @GET("doctores/pacientes/detalle/{id_paciente}")
    Call<PacienteDoctor> getPacienteById(@Path("id_paciente") int id_paciente);

    @PUT("doctores/pacientes/actualizar/{id_paciente}")
    Call<PacienteDoctor> actualizarPaciente(@Path("id_paciente") int id_paciente, @Body PacienteDoctor paciente);

    @DELETE("doctores/pacientes/eliminar/{id_paciente}")
    Call<Void> eliminarPaciente(@Path("id_paciente") int id_paciente);

    // ========== DIETAS ==========
    @POST("doctores/dietas/crear")
    Call<Map<String, Object>> crearDieta(@Body DietaDoctor dieta);

    @GET("doctores/dietas/paciente/{id_paciente}")
    Call<List<DietaDoctor>> getDietasPaciente(@Path("id_paciente") int id_paciente);

    @PUT("doctores/dietas/actualizar/{id_dieta}")
    Call<DietaDoctor> actualizarDieta(@Path("id_dieta") int id_dieta, @Body DietaDoctor dieta);

    @DELETE("doctores/dietas/eliminar/{id_dieta}")
    Call<Void> eliminarDieta(@Path("id_dieta") int id_dieta);

    // ========== MEDICAMENTOS ==========
    @POST("doctores/medicamentos/crear")
    Call<Map<String, Object>> crearMedicamento(@Body MedicamentoDoctor medicamento);

    @GET("doctores/medicamentos/paciente/{id_paciente}")
    Call<List<MedicamentoDoctor>> getMedicamentosPaciente(@Path("id_paciente") int id_paciente);

    @PUT("doctores/medicamentos/actualizar/{id_medicamento}")
    Call<MedicamentoDoctor> actualizarMedicamento(@Path("id_medicamento") int id_medicamento, @Body MedicamentoDoctor medicamento);

    @DELETE("doctores/medicamentos/eliminar/{id_medicamento}")
    Call<Void> eliminarMedicamento(@Path("id_medicamento") int id_medicamento);

    // ========== DIÁLISIS ==========
    @POST("doctores/dialisis/crear")
    Call<Map<String, Object>> crearDialisis(@Body DialisisDoctor dialisis);

    @GET("doctores/dialisis/paciente/{id_paciente}")
    Call<List<DialisisDoctor>> getDialisisPaciente(@Path("id_paciente") int id_paciente);

    @PUT("doctores/dialisis/actualizar/{id_dialisis}")
    Call<DialisisDoctor> actualizarDialisis(@Path("id_dialisis") int id_dialisis, @Body DialisisDoctor dialisis);

    @DELETE("doctores/dialisis/eliminar/{id_dialisis}")
    Call<Void> eliminarDialisis(@Path("id_dialisis") int id_dialisis);

    // ========== SIGNOS VITALES ==========
    @POST("doctores/signos-vitales/crear")
    Call<Map<String, Object>> crearSignosVitales(@Body SignosVitalesDoctor signosVitales);

    @GET("doctores/signos-vitales/paciente/{id_paciente}")
    Call<List<SignosVitalesDoctor>> getSignosVitalesPaciente(@Path("id_paciente") int id_paciente);

    @DELETE("doctores/signos-vitales/eliminar/{id_signo}")
    Call<Void> eliminarSignosVitales(@Path("id_signo") int id_signo);

    // ========== RECORDATORIOS ==========
    @POST("doctores/recordatorios/crear")
    Call<Map<String, Object>> crearRecordatorio(@Body RecordatorioDoctor recordatorio);

    @GET("doctores/recordatorios/paciente/{id_paciente}")
    Call<List<RecordatorioDoctor>> getRecordatoriosPaciente(@Path("id_paciente") int id_paciente);

    @PUT("doctores/recordatorios/actualizar/{id_recordatorio}")
    Call<RecordatorioDoctor> actualizarRecordatorio(@Path("id_recordatorio") int id_recordatorio, @Body RecordatorioDoctor recordatorio);

    @DELETE("doctores/recordatorios/eliminar/{id_recordatorio}")
    Call<Void> eliminarRecordatorio(@Path("id_recordatorio") int id_recordatorio);

    // ========== ESTADÍSTICAS ==========
    @GET("doctores/estadisticas/{id_paciente}")
    Call<EstadisticasPaciente> getEstadisticas(@Path("id_paciente") int id_paciente);
}