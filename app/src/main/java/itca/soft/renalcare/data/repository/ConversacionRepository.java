package itca.soft.renalcare.data.repository;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
        apiService = RetrofitClient.getClient().create(ApiService.class);
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

    public interface OnParticipantesCallback {
        void onSuccess(List<Object> participantes);
        void onError(String error);
        void onLoading(boolean loading);
    }

    public interface OnUsuarioCallback {
        void onSuccess(Object usuario);
        void onError(String error);
    }

    public interface OnSuccessCallback {
        void onSuccess();
        void onError(String error);
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
                                JsonArray mensajesArray = jsonObject.getAsJsonArray("mensajes");
                                List<Object> mensajes = new java.util.ArrayList<>();

                                for (JsonElement element : mensajesArray) {
                                    mensajes.add(element);
                                }

                                Log.d(TAG, "✅ Mensajes cargados: " + mensajes.size());
                                callback.onSuccess(mensajes);
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

    // ========== OBTENER PARTICIPANTES DE UNA CONVERSACIÓN ==========

    public void getParticipantesPorConversacion(String idConversacion, OnParticipantesCallback callback) {
        callback.onLoading(true);

        apiService.getParticipantesPorConversacion(idConversacion).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                callback.onLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonObject = response.body();

                        if (jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {
                            if (jsonObject.has("participantes")) {
                                JsonArray participantesArray = jsonObject.getAsJsonArray("participantes");
                                List<Object> participantes = new java.util.ArrayList<>();

                                for (JsonElement element : participantesArray) {
                                    participantes.add(element);
                                }

                                Log.d(TAG, "✅ Participantes cargados: " + participantes.size());
                                callback.onSuccess(participantes);
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

    // ========== BUSCAR USUARIO POR DUI ==========

    public void buscarUsuarioPorDUI(String dui, OnUsuarioCallback callback) {
        apiService.buscarUsuarioPorDUI(dui).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonObject = response.body();

                        if (jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {
                            if (jsonObject.has("usuario")) {
                                JsonElement usuarioElement = jsonObject.get("usuario");
                                Log.d(TAG, "✅ Usuario encontrado por DUI: " + dui);
                                callback.onSuccess(usuarioElement);
                            } else {
                                callback.onError("Usuario no encontrado");
                            }
                        } else {
                            callback.onError("Error en la respuesta del servidor");
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
                Log.e(TAG, "❌ Fallo en la red: " + t.getMessage());
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    // ========== AGREGAR PARTICIPANTE A CONVERSACIÓN ==========

    public void agregarParticipante(String idConversacion, String idUsuario, OnSuccessCallback callback) {
        apiService.agregarParticipante(idConversacion, idUsuario).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonObject = response.body();

                        if (jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {
                            Log.d(TAG, "✅ Participante agregado a conversación: " + idConversacion);
                            callback.onSuccess();
                        } else {
                            callback.onError("Error en la respuesta del servidor");
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
                Log.e(TAG, "❌ Fallo en la red: " + t.getMessage());
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }
}