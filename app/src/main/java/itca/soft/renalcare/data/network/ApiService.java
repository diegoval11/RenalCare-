package itca.soft.renalcare.data.network;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Body;

public interface ApiService {

    // ========== CONVERSACIONES ==========

    @GET("api/conversaciones/conversaciones/{id_usuario}")
    Call<JsonObject> getConversacionesPorUsuario(@Path("id_usuario") String idUsuario);
    @GET("api/conversaciones/{id_conversacion}")
    Call<JsonObject> getConversacionById(@Path("id_conversacion") String idConversacion);

    @POST("api/conversaciones")
    Call<JsonObject> crearConversacion(@Body JsonObject conversacion);

    // ========== MENSAJES ==========

    @GET("api/conversaciones/mensajes/{id_conversacion}")
    Call<JsonObject> getMensajesPorConversacion(@Path("id_conversacion") String idConversacion);


    @POST("api/conversaciones/enviarMensaje")
    Call<JsonObject> crearMensaje(@Body JsonObject mensaje);

    @PUT("api/conversaciones/editarMensaje")
    Call<JsonObject> actualizarMensaje(@Body JsonObject mensaje);

    @DELETE("api/conversaciones/{id_mensaje}")
    Call<Void> eliminarMensaje(@Path("id_mensaje") String idMensaje);

    // ========== PARTICIPANTES ==========

    @GET("api/conversaciones/{id_conversacion}/participantes")
    Call<JsonObject> getParticipantes(@Path("id_conversacion") String idConversacion);

    @GET("api/conversaciones/participantes/{id_conversacion}")
    Call<JsonObject> getParticipantesPorConversacion(@Path("id_conversacion") String idConversacion);

    @POST("api/conversaciones/{id_conversacion}/participantes/{id_usuario}")
    Call<JsonObject> agregarParticipante(@Path("id_conversacion") String idConversacion, @Path("id_usuario") String idUsuario);

    // ========== USUARIOS ==========

    @GET("api/usuarios/buscar/{dui}")
    Call<JsonObject> buscarUsuarioPorDUI(@Path("dui") String dui);
}