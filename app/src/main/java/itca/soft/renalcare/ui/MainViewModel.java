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

    // Método para iniciar la carga de datos
    public void cargarDatosPaciente(int idPaciente) {
        // Evita recargar si ya se cargaron los datos (ej. en rotación de pantalla)
        if (pacienteInfo.getValue() == null) {
            pacienteInfo = mRepository.getPacienteInfo(idPaciente);
        }
    }
}