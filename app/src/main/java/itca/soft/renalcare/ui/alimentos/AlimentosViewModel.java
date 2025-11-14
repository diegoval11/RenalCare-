package itca.soft.renalcare.ui.alimentos;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import itca.soft.renalcare.data.models.FoodItem;
import itca.soft.renalcare.data.network.AlimentosApiService;
import itca.soft.renalcare.data.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlimentosViewModel extends ViewModel {

    private static final String TAG = "AlimentosViewModel";

    // LiveData para la lista de alimentos
    private MutableLiveData<List<FoodItem>> _alimentos = new MutableLiveData<>();
    public LiveData<List<FoodItem>> getAlimentos() {
        return _alimentos;
    }

    // LiveData para el estado de carga (ProgressBar)
    private MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    // LiveData para errores
    private MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() {
        return _error;
    }

    // Servicio de API
    private AlimentosApiService apiService;

    public AlimentosViewModel() {
        // Inicializa el servicio de Retrofit
        apiService = RetrofitClient.getClient().create(AlimentosApiService.class);
        // Carga los alimentos al crear el ViewModel
        cargarAlimentos();
    }

    public void cargarAlimentos() {
        _isLoading.setValue(true); // Mostrar ProgressBar
        _error.setValue(null); // Limpiar errores previos

        apiService.getAlimentos().enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _alimentos.setValue(response.body()); // Éxito
                } else {
                    _error.setValue("Error al cargar alimentos (Respuesta no exitosa)");
                    Log.e(TAG, "Error en onResponse: " + response.message());
                }
                _isLoading.setValue(false); // Ocultar ProgressBar
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                _error.setValue("Fallo de red: " + t.getMessage());
                Log.e(TAG, "Error en onFailure: ", t);
                _isLoading.setValue(false); // Ocultar ProgressBar
            }
        });
    }
}