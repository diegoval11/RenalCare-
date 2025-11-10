// VoiceChatViewModel.java
package itca.soft.renalcare.ui.voice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import itca.soft.renalcare.data.models.VoiceSessionResponse;
import itca.soft.renalcare.data.repository.VoiceRepository;

public class VoiceChatViewModel extends ViewModel {
    private final VoiceRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> sessionToken = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public VoiceChatViewModel() {
        repository = new VoiceRepository();
        isLoading.setValue(false);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getSessionToken() {
        return sessionToken;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void iniciarSesionVoz(int idPaciente) {
        isLoading.setValue(true);

        repository.crearSesionVoz(idPaciente)
                .observeForever(result -> {
                    if (result.getStatus() == VoiceRepository.Result.Status.SUCCESS) {
                        VoiceSessionResponse response = result.getData();

                        // Guardar el token de la sesión
                        if (response.getClientSecret() != null) {
                            sessionToken.setValue(response.getClientSecret().getValue());
                        }

                        isLoading.setValue(false);
                    } else if (result.getStatus() == VoiceRepository.Result.Status.ERROR) {
                        errorMessage.setValue(result.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    public void limpiarError() {
        errorMessage.setValue(null);
    }
}