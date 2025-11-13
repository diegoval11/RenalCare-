// Ubicación: data/PacienteRepository.java (o donde prefieras, ej. data/repos)
package itca.soft.renalcare.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import itca.soft.renalcare.data.models.PacienteInfoResponse;
import itca.soft.renalcare.data.network.ChatIAApiService;
import itca.soft.renalcare.data.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteRepository {

    final private ChatIAApiService apiService;

    public PacienteRepository() {
        // Usamos el ChatIAApiService, al que le añadiremos la nueva llamada
        apiService = RetrofitClient.getClient().create(ChatIAApiService.class);
    }

    public LiveData<PacienteInfoResponse> getPacienteInfo(int idPaciente) {
        final MutableLiveData<PacienteInfoResponse> data = new MutableLiveData<>();

        apiService.getInfoPaciente(idPaciente).enqueue(new Callback<PacienteInfoResponse>() {
            @Override
            public void onResponse(Call<PacienteInfoResponse> call, Response<PacienteInfoResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null); // Error de API
                }
            }

            @Override
            public void onFailure(Call<PacienteInfoResponse> call, Throwable t) {
                data.setValue(null); // Error de Red
            }
        });

        return data;
    }
}