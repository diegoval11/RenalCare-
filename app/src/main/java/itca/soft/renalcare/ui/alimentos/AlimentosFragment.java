package itca.soft.renalcare.ui.alimentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.FoodItem;

public class AlimentosFragment extends Fragment {

    private RecyclerView recyclerViewAlimentos;
    private FoodListAdapter foodListAdapter;
    private SearchView searchViewAlimentos;
    private ProgressBar progressBarAlimentos;

    private AlimentosViewModel alimentosViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alimentos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Encontrar las vistas
        recyclerViewAlimentos = view.findViewById(R.id.recyclerViewAlimentos);
        searchViewAlimentos = view.findViewById(R.id.searchViewAlimentos);
        progressBarAlimentos = view.findViewById(R.id.progressBarAlimentos);

        // --- ¡ESTA ES LA CORRECCIÓN! ---
        // Reseteamos el SearchView cada vez que se crea la vista.
        // Esto previene que un filtro anterior se quede "pegado"
        // cuando vuelves a este fragmento.
        searchViewAlimentos.setQuery("", false);
        // ---------------------------------

        // 2. Configurar el RecyclerView con un adaptador vacío al inicio
        setupRecyclerView();

        // 3. Configurar el SearchView
        setupSearchView();

        // 4. Inicializar el ViewModel (¡esto ya está correcto!)
        alimentosViewModel = new ViewModelProvider(requireActivity()).get(AlimentosViewModel.class);

        // 5. Configurar los observadores
        setupObservers();
    }

    /**
     * Prepara el RecyclerView con un adaptador vacío.
     * Los datos se añadirán cuando el ViewModel responda.
     */
    private void setupRecyclerView() {
        // Iniciamos el adaptador con una lista vacía
        foodListAdapter = new FoodListAdapter(requireContext(), new ArrayList<>());
        recyclerViewAlimentos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAlimentos.setAdapter(foodListAdapter);
    }

    /**
     * Configura el listener del SearchView (sin cambios)
     */
    private void setupSearchView() {
        searchViewAlimentos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (foodListAdapter != null) {
                    foodListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    /**
     * Conecta el Fragment al ViewModel usando LiveData.
     */
    private void setupObservers() {
        // Observador para la lista de alimentos
        alimentosViewModel.getAlimentos().observe(getViewLifecycleOwner(), new Observer<List<FoodItem>>() {
            @Override
            public void onChanged(List<FoodItem> foodItems) {
                // Cuando los datos lleguen, actualiza el adaptador
                if (foodItems != null) {
                    foodListAdapter.updateData(foodItems);

                    // --- REFUERZO DE LA SOLUCIÓN ---
                    // Forzamos al filtro a correr de nuevo por si acaso,
                    // ahora que SÍ hay datos.
                    String currentQuery = searchViewAlimentos.getQuery().toString();
                    if (!currentQuery.isEmpty()) {
                        foodListAdapter.getFilter().filter(currentQuery);
                    }
                }
            }
        });

        // Observador para el estado de carga (ProgressBar)
        alimentosViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    progressBarAlimentos.setVisibility(View.VISIBLE);
                } else {
                    progressBarAlimentos.setVisibility(View.GONE);
                }
            }
        });

        // Observador para errores
        alimentosViewModel.getError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    // Evitamos mostrar el mismo error varias veces
                    // (Opcional, pero buena práctica)
                    // alimentosViewModel.clearError(); // Necesitarías crear este método en el VM
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpiamos referencias
        recyclerViewAlimentos = null;
        searchViewAlimentos = null;
        progressBarAlimentos = null;
        foodListAdapter = null;
    }
}