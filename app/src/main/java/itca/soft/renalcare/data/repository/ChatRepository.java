// ChatRepository.java - COMPLETO Y CORREGIDO
package itca.soft.renalcare.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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

public class ChatRepository {
    private final ChatIAApiService apiService;
    private final Context context; // <-- 1. Contexto agregado

    // 2. Constructor modificado para recibir Context
    public ChatRepository(Context context) {
        apiService = RetrofitClient.getClient().create(ChatIAApiService.class);
        this.context = context.getApplicationContext(); // Usar ApplicationContext
    }

    // ========== ENVIAR MENSAJE DE TEXTO (Sin cambios) ==========
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

    // ========== ENVIAR MENSAJE CON IMAGEN (Corregido) ==========
    public LiveData<Result<MensajeResponse>> enviarMensajeConImagen(
            int idUsuario, String mensaje, Integer idConversacion, Uri imageUri) {

        MutableLiveData<Result<MensajeResponse>> result = new MutableLiveData<>();
        result.setValue(Result.loading());

        try {
            // 3. Usar el método helper para obtener el archivo desde la URI
            File file = getFileFromUri(imageUri);

            if (file == null || !file.exists()) {
                result.setValue(Result.error("Archivo de imagen no encontrado o no se pudo leer"));
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
                            file.delete(); // 4. Borrar archivo temporal
                            if (response.isSuccessful() && response.body() != null) {
                                result.setValue(Result.success(response.body()));
                            } else {
                                result.setValue(Result.error("Error en la respuesta"));
                            }
                        }

                        @Override
                        public void onFailure(Call<MensajeResponse> call, Throwable t) {
                            file.delete(); // 4. Borrar archivo temporal
                            result.setValue(Result.error(t.getMessage()));
                        }
                    });

        } catch (Exception e) {
            result.setValue(Result.error(e.getMessage()));
        }

        return result;
    }

    // ========== OBTENER TODAS LAS CONVERSACIONES (Sin cambios) ==========
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

    // ========== OBTENER UNA CONVERSACIÓN ESPECÍFICA (Sin cambios) ==========
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

    // ========== 5. MÉTODOS HELPER PARA MANEJAR LA URI ==========

    /**
     * Crea un archivo temporal en la caché de la app
     * a partir de los datos de una content:// URI.
     */
    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            String fileName = getFileName(uri);
            File tempFile = new File(context.getCacheDir(), fileName);

            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return tempFile;

        } catch (Exception e) {
            Log.e("ChatRepository", "Error al crear archivo desde URI", e);
            return null;
        }
    }

    /**
     * Método helper para obtener el nombre del archivo desde la URI
     */
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "temp_image_" + System.currentTimeMillis();
    }


    // ========== CLASE RESULT PARA MANEJO DE ESTADOS (Sin cambios) ==========
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