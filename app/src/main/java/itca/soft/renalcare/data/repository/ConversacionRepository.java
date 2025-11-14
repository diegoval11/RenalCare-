package itca.soft.renalcare.data.repository;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import itca.soft.renalcare.data.models.Conversacion;
import itca.soft.renalcare.data.models.ConversacionResponse;
import itca.soft.renalcare.data.network.ApiService;
import itca.soft.renalcare.data.network.RetrofitClient;

public class ConversacionRepository {
    private static final String TAG = "ConversacionRepository";
    private ApiService apiService;
    private Gson gson;

    public ConversacionRepository() {
        apiService = RetrofitClient.getApiService();
        gson = new Gson();
    }

    // ========== CALLBACKS ==========

    public interface OnConversacionesCallback {
        void onSuccess(List<Conversacion> conversaciones);
        void onError(String error);
        void onLoading(boolean loading);
    }

    public interface OnMensajesCallback {
        void onSuccess(List<Object> mensajes);
        void onError(String error);
        void onLoading(boolean loading);
    }

    // ========== OBTENER CONVERSACIONES POR USUARIO ==========

    public void getConversacionesPorUsuario(String idUsuario, OnConversacionesCallback callback) {
        callback.onLoading(true);

        apiService.getConversacionesPorUsuario(idUsuario).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                callback.onLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonObject = response.body();

                        // Verificar si success es true
                        if (jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {

                            // Extraer el array de conversaciones
                            if (jsonObject.has("conversaciones")) {
                                List<ConversacionResponse> responsesTemp =
                                        gson.fromJson(
                                                jsonObject.get("conversaciones"),
                                                com.google.gson.reflect.TypeToken.getParameterized(
                                                        List.class,
                                                        ConversacionResponse.class
                                                ).getType()
                                        );

                                // Convertir a Conversacion
                                List<Conversacion> conversaciones = new java.util.ArrayList<>();
                                if (responsesTemp != null) {
                                    for (ConversacionResponse resp : responsesTemp) {
                                        conversaciones.add(resp.toConversacion());
                                    }
                                }

                                Log.d(TAG, "✅ Conversaciones cargadas: " + conversaciones.size());
                                callback.onSuccess(conversaciones);
                            } else {
                                callback.onSuccess(new java.util.ArrayList<>());
                            }
                        } else {
                            callback.onError("Error: success = false");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error parseando respuesta", e);
                        callback.onError("Error parseando: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ Error: " + response.code());
                    callback.onError("Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onLoading(false);
                Log.e(TAG, "❌ Fallo en la red: " + t.getMessage());
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    // ========== OBTENER MENSAJES DE UNA CONVERSACIÓN ==========

    public void getMensajesPorConversacion(String idConversacion, OnMensajesCallback callback) {
        callback.onLoading(true);

        apiService.getMensajesPorConversacion(idConversacion).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                callback.onLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonObject = response.body();

                        if (jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {
                            if (jsonObject.has("mensajes")) {
                                List<Object> mensajes =
                                        gson.fromJson(
                                                jsonObject.get("mensajes"),
                                                com.google.gson.reflect.TypeToken.getParameterized(
                                                        List.class,
                                                        Object.class
                                                ).getType()
                                        );

                                Log.d(TAG, "✅ Mensajes cargados: " + (mensajes != null ? mensajes.size() : 0));
                                callback.onSuccess(mensajes != null ? mensajes : new java.util.ArrayList<>());
                            } else {
                                callback.onSuccess(new java.util.ArrayList<>());
                            }
                        } else {
                            callback.onError("Error: success = false");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error parseando respuesta", e);
                        callback.onError("Error parseando: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ Error: " + response.code());
                    callback.onError("Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onLoading(false);
                Log.e(TAG, "❌ Fallo en la red: " + t.getMessage());
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }
}