package itca.soft.renalcare.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import itca.soft.renalcare.data.repository.PacienteRepository;
import itca.soft.renalcare.data.models.PacienteInfoResponse;

public class MainViewModel extends AndroidViewModel {

    private PacienteRepository mRepository;
    private LiveData<PacienteInfoResponse> pacienteInfo;

    // --- ¡NUEVO! LiveData para el modo "Solo Vista" ---
    /**
     * LiveData que almacena el estado de "Solo Vista".
     * Los fragmentos (como PerfilFragment) observarán esto
     * para ocultar/mostrar botones de edición.
     */
    private MutableLiveData<Boolean> _isViewOnly = new MutableLiveData<>(false);
    public LiveData<Boolean> isViewOnly() {
        return _isViewOnly;
    }
    // ---------------------------------------------------

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PacienteRepository();
        // Inicializamos vacío, se cargará desde la Activity
        pacienteInfo = new MutableLiveData<>();
    }

    // El LiveData público que observarán los fragmentos
    public LiveData<PacienteInfoResponse> getPacienteInfo() {
        return pacienteInfo;
    }

    // --- ¡MÉTODO ACTUALIZADO! ---
    /**
     * Método para iniciar la carga de datos.
     * Ahora también acepta y almacena el estado de "Solo Vista".
     *
     * @param idPaciente El ID del paciente a cargar.
     * @param viewOnly   True si la UI debe ser de solo lectura.
     */
    public void cargarDatosPaciente(int idPaciente, boolean viewOnly) {
        // Almacenamos el estado de "Solo Vista" en el LiveData
        _isViewOnly.setValue(viewOnly);

        // Evita recargar si ya se cargaron los datos (ej. en rotación de pantalla)
        // O si el ID es diferente (cambio de paciente, aunque raro en este flujo)
        if (pacienteInfo.getValue() == null || (pacienteInfo.getValue() != null && pacienteInfo.getValue().getIdUsuario() != idPaciente)) {
            pacienteInfo = mRepository.getPacienteInfo(idPaciente);
        }
    }
}