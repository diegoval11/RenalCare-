package itca.soft.renalcare.ui.alimentos;

// --- ¡NUEVO! Imports para la cámara, permisos, SharedPreferences, etc. ---
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton; // ¡NUEVO!

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.LoginActivity; // ¡NUEVO!
import itca.soft.renalcare.data.models.FoodItem;
import itca.soft.renalcare.ui.chat.ChatViewModel; // ¡NUEVO!

public class AlimentosFragment extends Fragment {

    // Vistas de Alimentos
    private RecyclerView recyclerViewAlimentos;
    private FoodListAdapter foodListAdapter;
    private SearchView searchViewAlimentos;
    private ProgressBar progressBarAlimentos;

    // ViewModel de Alimentos
    private AlimentosViewModel alimentosViewModel;

    // --- ¡NUEVO! Vistas y variables para la cámara y el chat ---
    private MaterialButton btnScanFood;
    private ChatViewModel chatViewModel; // ViewModel compartido para enviar el msg
    private int idUsuario; // ID del usuario logueado
    private Uri cameraImageUri; // URI temporal para la foto
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    // -----------------------------------------------------------

    // ¡NUEVO! onCreate se usa para registrar los launchers ANTES de que se cree la vista
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLaunchers();
    }

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
        btnScanFood = view.findViewById(R.id.btn_scan_food); // ¡NUEVO!

        // Reseteamos el SearchView (lógica existente)
        searchViewAlimentos.setQuery("", false);

        // 2. Configurar el RecyclerView (lógica existente)
        setupRecyclerView();

        // 3. Configurar el SearchView (lógica existente)
        setupSearchView();

        // --- ¡NUEVO! Lógica de sesión y ViewModels ---
        // 4. Obtener ID de usuario
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Error de sesión: ID no encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 5. Inicializar ViewModels (usando requireActivity() para compartirlos)
        alimentosViewModel = new ViewModelProvider(requireActivity()).get(AlimentosViewModel.class);
        chatViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        // 6. Configurar los observadores (lógica existente)
        setupObservers();

        // 7. ¡NUEVO! Configurar listener para el botón de escanear
        btnScanFood.setOnClickListener(v -> verificarPermisosCamara());
    }

    // --- MÉTODOS DE LA LISTA DE ALIMENTOS (Sin cambios) ---

    private void setupRecyclerView() {
        foodListAdapter = new FoodListAdapter(requireContext(), new ArrayList<>());
        recyclerViewAlimentos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAlimentos.setAdapter(foodListAdapter);
    }

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

    private void setupObservers() {
        // Observador para la lista de alimentos
        alimentosViewModel.getAlimentos().observe(getViewLifecycleOwner(), foodItems -> {
            if (foodItems != null) {
                foodListAdapter.updateData(foodItems);
                String currentQuery = searchViewAlimentos.getQuery().toString();
                if (!currentQuery.isEmpty()) {
                    foodListAdapter.getFilter().filter(currentQuery);
                }
            }
        });

        // Observador para el estado de carga
        alimentosViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBarAlimentos.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observador para errores
        alimentosViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- ¡NUEVOS MÉTODOS! (Copiados de ChatFragment.java) ---

    /**
     * Prepara los launchers de ActivityResult para permisos y cámara.
     * Esta lógica es idéntica a la de ChatFragment,
     * EXCEPTO por el callback de takePictureLauncher.
     */
    private void setupLaunchers() {
        // Launcher para pedir permisos
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamara(); // Permiso concedido, abrir cámara
                    } else {
                        Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Launcher para tomar la foto
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        // ¡Éxito! La foto se guardó en 'cameraImageUri'
                        if (cameraImageUri != null && idUsuario != -1) {
                            Toast.makeText(getContext(), "Foto capturada, analizando...", Toast.LENGTH_SHORT).show();

                            // 1. Enviar la imagen a la IA usando el ChatViewModel
                            chatViewModel.enviarMensajeConImagen(
                                    idUsuario,
                                    "Analiza esta imagen de mi comida", // Mensaje automático
                                    cameraImageUri
                            );

                            // 2. Navegar al ChatFragment para ver la respuesta
                            try {
                                NavController navController = Navigation.findNavController(requireView());
                                // Usamos el ID del ítem del menú de navegación
                                navController.navigate(R.id.nav_chat);
                            } catch (Exception e) {
                                Log.e("AlimentosFragment", "Error al navegar al chat", e);
                                Toast.makeText(getContext(), "Error al abrir el chat", Toast.LENGTH_SHORT).show();
                            }
                        }
                        cameraImageUri = null; // Limpiar la URI
                    } else {
                        // Si falla o se cancela, reseteamos la URI
                        cameraImageUri = null;
                        Toast.makeText(getContext(), "Captura cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Comprueba si el permiso de cámara ya está concedido.
     * Si sí, abre la cámara. Si no, pide el permiso.
     */
    private void verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            // Pedir permiso
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Prepara un archivo temporal y la URI, y lanza la app de cámara.
     */
    private void abrirCamara() {
        try {
            File photoFile = crearArchivoImagen();
            if (photoFile != null) {
                // Generar la content:// URI usando el FileProvider
                cameraImageUri = FileProvider.getUriForFile(
                        requireContext(),
                        // AVISO: Asegúrate que esta autoridad coincide con tu AndroidManifest.xml
                        requireContext().getPackageName() + ".provider",
                        photoFile
                );
                // Lanzar la cámara, pidiéndole que guarde la foto en nuestra URI
                takePictureLauncher.launch(cameraImageUri);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al preparar la cámara", Toast.LENGTH_SHORT).show();
            Log.e("AlimentosFragment", "Error en abrirCamara", e);
        }
    }

    /**
     * Crea un archivo temporal en el almacenamiento externo.
     */
    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Crear el archivo temporal
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    // --- Limpieza (sin cambios) ---
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpiamos referencias
        recyclerViewAlimentos = null;
        searchViewAlimentos = null;
        progressBarAlimentos = null;
        foodListAdapter = null;
        btnScanFood = null; // ¡NUEVO!
    }
}