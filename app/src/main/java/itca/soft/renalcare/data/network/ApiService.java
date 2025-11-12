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

    @GET("conversaciones/usuario/{id_usuario}")
    Call<JsonObject> getConversacionesPorUsuario(@Path("id_usuario") String idUsuario);

    @GET("conversaciones/{id_conversacion}")
    Call<JsonObject> getConversacionById(@Path("id_conversacion") String idConversacion);

    @POST("conversaciones")
    Call<JsonObject> crearConversacion(@Body JsonObject conversacion);

    // ========== MENSAJES ==========

    @GET("conversaciones/{id_conversacion}/mensajes")
    Call<JsonObject> getMensajesPorConversacion(@Path("id_conversacion") String idConversacion);

    @POST("mensajes")
    Call<JsonObject> crearMensaje(@Body JsonObject mensaje);

    @PUT("mensajes/{id_mensaje}")
    Call<JsonObject> actualizarMensaje(@Path("id_mensaje") String idMensaje, @Body JsonObject mensaje);

    @DELETE("mensajes/{id_mensaje}")
    Call<Void> eliminarMensaje(@Path("id_mensaje") String idMensaje);

    // ========== PARTICIPANTES ==========

    @GET("conversaciones/{id_conversacion}/participantes")
    Call<JsonObject> getParticipantes(@Path("id_conversacion") String idConversacion);
}