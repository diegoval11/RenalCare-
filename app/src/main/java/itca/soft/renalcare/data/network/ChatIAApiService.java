// ChatIAApiService.java - COMPLETO
package itca.soft.renalcare.data.network;

import itca.soft.renalcare.data.models.ConversacionesResponse;
import itca.soft.renalcare.data.models.MensajeResponse;
import itca.soft.renalcare.data.models.VoiceSessionResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ChatIAApiService {

    // ========== ENVIAR MENSAJE DE TEXTO ==========
    @Multipart
    @POST("chatIA/mensaje")
    Call<MensajeResponse> enviarMensaje(
            @Part("id_usuario") RequestBody idUsuario,
            @Part("mensaje_usuario") RequestBody mensajeUsuario,
            @Part("id_conversacion") RequestBody idConversacion
    );

    // ========== ENVIAR MENSAJE CON IMAGEN ==========
    @Multipart
    @POST("chatIA/mensaje")
    Call<MensajeResponse> enviarMensajeConImagen(
            @Part("id_usuario") RequestBody idUsuario,
            @Part("mensaje_usuario") RequestBody mensajeUsuario,
            @Part("id_conversacion") RequestBody idConversacion,
            @Part MultipartBody.Part imagen
    );

    // ========== OBTENER CONVERSACIONES DEL USUARIO ==========
    @GET("chatIA/conversaciones/{id_usuario}")
    Call<ConversacionesResponse> obtenerConversaciones(
            @Path("id_usuario") int idUsuario
    );

    // ========== CREAR SESIÓN DE VOZ ==========
    @GET("chatIA/voice-session/{id_paciente}")
    Call<VoiceSessionResponse> crearSesionVoz(
            @Path("id_paciente") int idPaciente
    );
}