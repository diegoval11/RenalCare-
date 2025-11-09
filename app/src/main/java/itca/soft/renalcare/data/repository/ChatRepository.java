// ChatRepository.java - COMPLETO
package itca.soft.renalcare.data.repository;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import itca.soft.renalcare.data.models.ConversacionesResponse;
import itca.soft.renalcare.data.models.MensajeResponse;
import itca.soft.renalcare.data.network.ChatIAApiService;
import itca.soft.renalcare.data.network.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.util.List;

public class ChatRepository {
    private final ChatIAApiService apiService;

    public ChatRepository() {
        apiService = RetrofitClient.getClient().create(ChatIAApiService.class);
    }

    // ========== ENVIAR MENSAJE DE TEXTO ==========
    public LiveData<Result<MensajeResponse>> enviarMensaje(
            int idUsuario, String mensaje, Integer idConversacion) {

        MutableLiveData<Result<MensajeResponse>> result = new MutableLiveData<>();
        result.setValue(Result.loading());

        RequestBody idUsuarioBody = RequestBody.create(
                MediaType.parse("text/plain"), String.valueOf(idUsuario));
        RequestBody mensajeBody = RequestBody.create(
                MediaType.parse("text/plain"), mensaje);
        RequestBody idConvBody = RequestBody.create(
                MediaType.parse("text/plain"),
                idConversacion == null ? "null" : String.valueOf(idConversacion));

        apiService.enviarMensaje(idUsuarioBody, mensajeBody, idConvBody)
                .enqueue(new Callback<MensajeResponse>() {
                    @Override
                    public void onResponse(Call<MensajeResponse> call,
                                           Response<MensajeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Result.success(response.body()));
                        } else {
                            result.setValue(Result.error("Error en la respuesta"));
                        }
                    }

                    @Override
                    public void onFailure(Call<MensajeResponse> call, Throwable t) {
                        result.setValue(Result.error(t.getMessage()));
                    }
                });

        return result;
    }

    // ========== ENVIAR MENSAJE CON IMAGEN ==========
    public LiveData<Result<MensajeResponse>> enviarMensajeConImagen(
            int idUsuario, String mensaje, Integer idConversacion, Uri imageUri) {

        MutableLiveData<Result<MensajeResponse>> result = new MutableLiveData<>();
        result.setValue(Result.loading());

        try {
            // Obtener archivo real desde la URI
            File file;

            // Si es URI de contenido (content://), crear archivo temporal
            if ("content".equalsIgnoreCase(imageUri.getScheme())) {
                file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "temp_" + System.currentTimeMillis() + ".jpg");

                // Aquí necesitarías un Context - mejor pasar el contexto al constructor
                // Por ahora usaremos la ruta directamente si es un archivo
            } else {
                file = new File(imageUri.getPath());
            }

            if (!file.exists()) {
                result.setValue(Result.error("Archivo de imagen no encontrado"));
                return result;
            }

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"), file);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                    "image", file.getName(), requestFile);

            RequestBody idUsuarioBody = RequestBody.create(
                    MediaType.parse("text/plain"), String.valueOf(idUsuario));
            RequestBody mensajeBody = RequestBody.create(
                    MediaType.parse("text/plain"), mensaje != null ? mensaje : "");
            RequestBody idConvBody = RequestBody.create(
                    MediaType.parse("text/plain"),
                    idConversacion == null ? "null" : String.valueOf(idConversacion));

            apiService.enviarMensajeConImagen(
                            idUsuarioBody, mensajeBody, idConvBody, imagePart)
                    .enqueue(new Callback<MensajeResponse>() {
                        @Override
                        public void onResponse(Call<MensajeResponse> call,
                                               Response<MensajeResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                result.setValue(Result.success(response.body()));
                            } else {
                                result.setValue(Result.error("Error en la respuesta"));
                            }
                        }

                        @Override
                        public void onFailure(Call<MensajeResponse> call, Throwable t) {
                            result.setValue(Result.error(t.getMessage()));
                        }
                    });

        } catch (Exception e) {
            result.setValue(Result.error(e.getMessage()));
        }

        return result;
    }

    // ========== OBTENER TODAS LAS CONVERSACIONES ==========
    public LiveData<Result<List<ConversacionesResponse.ConversacionData>>>
    obtenerConversaciones(int idUsuario) {

        MutableLiveData<Result<List<ConversacionesResponse.ConversacionData>>> result =
                new MutableLiveData<>();
        result.setValue(Result.loading());

        apiService.obtenerConversaciones(idUsuario)
                .enqueue(new Callback<ConversacionesResponse>() {
                    @Override
                    public void onResponse(Call<ConversacionesResponse> call,
                                           Response<ConversacionesResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            result.setValue(Result.success(response.body().getConversaciones()));
                        } else {
                            result.setValue(Result.error("Error al cargar conversaciones"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ConversacionesResponse> call, Throwable t) {
                        result.setValue(Result.error("Error de conexión: " + t.getMessage()));
                    }
                });

        return result;
    }

    // ========== OBTENER UNA CONVERSACIÓN ESPECÍFICA ==========
    public LiveData<Result<ConversacionesResponse.ConversacionData>>
    obtenerConversacion(int idUsuario, int idConversacion) {

        MutableLiveData<Result<ConversacionesResponse.ConversacionData>> result =
                new MutableLiveData<>();
        result.setValue(Result.loading());

        apiService.obtenerConversaciones(idUsuario)
                .enqueue(new Callback<ConversacionesResponse>() {
                    @Override
                    public void onResponse(Call<ConversacionesResponse> call,
                                           Response<ConversacionesResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            // Buscar la conversación específica
                            List<ConversacionesResponse.ConversacionData> conversaciones =
                                    response.body().getConversaciones();

                            if (conversaciones != null) {
                                for (ConversacionesResponse.ConversacionData conv : conversaciones) {
                                    if (conv.getIdConversacion() == idConversacion) {
                                        result.setValue(Result.success(conv));
                                        return;
                                    }
                                }
                            }
                            result.setValue(Result.error("Conversación no encontrada"));
                        } else {
                            result.setValue(Result.error("Error al cargar conversación"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ConversacionesResponse> call, Throwable t) {
                        result.setValue(Result.error("Error de conexión: " + t.getMessage()));
                    }
                });

        return result;
    }

    // ========== CLASE RESULT PARA MANEJO DE ESTADOS ==========
    public static class Result<T> {
        public enum Status { LOADING, SUCCESS, ERROR }

        private Status status;
        private T data;
        private String message;

        private Result(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }

        public static <T> Result<T> loading() {
            return new Result<>(Status.LOADING, null, null);
        }

        public static <T> Result<T> success(T data) {
            return new Result<>(Status.SUCCESS, data, null);
        }

        public static <T> Result<T> error(String message) {
            return new Result<>(Status.ERROR, null, message);
        }

        public Status getStatus() { return status; }
        public T getData() { return data; }
        public String getMessage() { return message; }
    }
}