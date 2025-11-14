package itca.soft.renalcare.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData; // ¡Importante!
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations; // ¡Importante!

import itca.soft.renalcare.data.repository.PacienteRepository;
import itca.soft.renalcare.data.models.PacienteInfoResponse;
import itca.soft.renalcare.utils.SingleLiveEvent;

public class MainViewModel extends AndroidViewModel {

    private PacienteRepository mRepository;

    // --- ¡CAMBIOS CRÍTICOS! ---

    // 1. Trigger para el ID. Solo cambia si el *paciente* cambia.
    private final MutableLiveData<Integer> idPacienteTrigger = new MutableLiveData<>();

    // 2. Trigger para "refrescar". Usamos SingleLiveEvent para que solo se dispare una vez.
    private final SingleLiveEvent<Boolean> refreshTrigger = new SingleLiveEvent<>();

    // 3. El LiveData PÚBLICO. Sigue siendo 'final'.
    private final LiveData<PacienteInfoResponse> pacienteInfo;

    // --- FIN CAMBIOS ---


    private MutableLiveData<Boolean> _isViewOnly = new MutableLiveData<>(false);
    public LiveData<Boolean> isViewOnly() {
        return _isViewOnly;
    }

    private SingleLiveEvent<Integer> _navigateTo = new SingleLiveEvent<>();
    public LiveData<Integer> getNavigateTo() {
        return _navigateTo;
    }

    private int idPacienteActual = -1;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PacienteRepository();

        // --- ¡CAMBIO CRÍTICO EN EL CONSTRUCTOR! ---

        // 4. Creamos un "Mediador". Este observará los dos triggers.
        // Usaremos un objeto genérico 'Object' como disparador
        MediatorLiveData<Object> liveDataToObserve = new MediatorLiveData<>();

        // 5. Si el ID cambia, el mediador se actualiza.
        liveDataToObserve.addSource(idPacienteTrigger, id -> {
            if (id != null) {
                liveDataToObserve.setValue(id); // Dispara el switchMap
            }
        });

        // 6. Si "refrescar" se dispara, el mediador también se actualiza.
        liveDataToObserve.addSource(refreshTrigger, shouldRefresh -> {
            // No importa el valor (true/false), el simple hecho de que se disparó
            // nos basta. Volvemos a setear el ID actual para re-disparar el switchMap.
            if (idPacienteTrigger.getValue() != null) {
                // Usamos un new Object() para GARANTIZAR que sea una nueva instancia
                // y el switchMap se dispare SÍ O SÍ.
                liveDataToObserve.setValue(new Object());
            }
        });

        // 7. 'pacienteInfo' es el *resultado* de observar al "Mediador".
        // CADA VEZ que el Mediador emita un valor
        // (sea porque cambió el ID o porque se forzó el refresco),
        // este switchMap se ejecutará de nuevo.
        pacienteInfo = Transformations.switchMap(liveDataToObserve, trigger -> {
            // El 'trigger' puede ser el ID (Integer) o el new Object().
            // No nos importa, solo usamos el ID guardado en la clase.

            if (idPacienteActual == -1) {
                MutableLiveData<PacienteInfoResponse> emptyResult = new MutableLiveData<>();
                emptyResult.setValue(null);
                return emptyResult;
            }

            // Esto llamará al repositorio CADA VEZ que el mediador se dispare.
            return mRepository.getPacienteInfo(idPacienteActual);
        });
    }

    public LiveData<PacienteInfoResponse> getPacienteInfo() {
        return pacienteInfo;
    }

    // --- ¡MÉTODO MODIFICADO! ---
    public void cargarDatosPaciente(int idPaciente, boolean viewOnly) {
        this.idPacienteActual = idPaciente;
        _isViewOnly.setValue(viewOnly);

        // Esta condición previene recargas innecesarias (ej. rotación de pantalla)
        if (idPacienteTrigger.getValue() == null || !idPacienteTrigger.getValue().equals(idPaciente)) {
            // 8. Solo actualizamos el trigger de ID aquí.
            idPacienteTrigger.setValue(idPaciente);
        }
    }

    // --- ¡MÉTODO MODIFICADO! ---
    public void forceRefreshPacienteInfo() {
        if (idPacienteActual != -1) {
            // 9. ¡Solo actualizamos el trigger de REFRESH!
            // Esto disparará el addSource(refreshTrigger, ...)
            // que a su vez disparará al Mediador
            // que a su vez disparará el switchMap.
            refreshTrigger.setValue(true);
        }
    }
    // ------------------------

    public void requestNavigation(int destinationId) {
        _navigateTo.setValue(destinationId);
    }
}