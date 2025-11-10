// VoiceRepository.java
package itca.soft.renalcare.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import itca.soft.renalcare.data.models.VoiceSessionResponse;
import itca.soft.renalcare.data.network.ChatIAApiService;
import itca.soft.renalcare.data.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoiceRepository {
    private final ChatIAApiService apiService;

    public VoiceRepository() {
        apiService = RetrofitClient.getClient().create(ChatIAApiService.class);
    }

    public LiveData<Result<VoiceSessionResponse>> crearSesionVoz(int idPaciente) {
        MutableLiveData<Result<VoiceSessionResponse>> result = new MutableLiveData<>();
        result.setValue(Result.loading());

        apiService.crearSesionVoz(idPaciente)
                .enqueue(new Callback<VoiceSessionResponse>() {
                    @Override
                    public void onResponse(Call<VoiceSessionResponse> call,
                                           Response<VoiceSessionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Result.success(response.body()));
                        } else {
                            result.setValue(Result.error("Error al crear sesión de voz"));
                        }
                    }

                    @Override
                    public void onFailure(Call<VoiceSessionResponse> call, Throwable t) {
                        result.setValue(Result.error(t.getMessage()));
                    }
                });

        return result;
    }

    // Clase Result (reutiliza la misma de ChatRepository o crea una aquí)
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