// ChatFragment.java - VERSIÓN COMPLETA Y CORREGIDA
package itca.soft.renalcare.ui.chat;

// ¡CAMBIO! Imports añadidos para SharedPreferences
import android.content.Context;
import android.content.SharedPreferences;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.LoginActivity; // ¡CAMBIO! Import añadido
import itca.soft.renalcare.data.models.ConversacionItem;

public class ChatFragment extends Fragment {

    // UI Components
    private RecyclerView recyclerChat, recyclerHistory;
    private EditText etMessage;
    private ImageButton btnSend, btnAttach, btnMenu, btnNewChat;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private CardView imagePreviewContainer;
    private ImageView imagePreview;
    private ImageButton btnRemoveImage;

    // --- 1. AÑADIR VARIABLE PARA EL BOTÓN DE VOZ ---
    private ImageButton btnVoiceChat;

    // Adapters
    private ChatAdapter chatAdapter;
    private HistoryAdapter historyAdapter;

    // ViewModel
    private ChatViewModel viewModel;

    // URIs para cámara y galería
    private Uri selectedImageUri; // Esta será la content:// URI (de cámara o galería)
    private Uri cameraImageUri;   // Variable temporal para guardar la content:// URI de la cámara

    // --- ¡CAMBIO! El ID ya no se hardcodea ---
    // private int idUsuario = 2; // <- Línea antigua
    private int idUsuario; // Se asignará en onViewCreated

    // Launchers para permisos e imágenes
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLaunchers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- ¡CAMBIO! Cargar ID de usuario dinámicamente ---
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        // 'idUsuario' ahora se refiere a la variable de la clase
        idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Error de sesión: ID no encontrado.", Toast.LENGTH_SHORT).show();
            // Opcional: navegar de vuelta al login
            // NavController navController = Navigation.findNavController(view);
            // navController.navigate(R.id.action_global_to_login); // (Si existe tal acción)
            return; // No continuar si no hay ID
        }
        // --- Fin del cambio ---


        initViews(view);
        setupViewModel();
        setupRecyclerViews();
        setupClickListeners();

        // Cargar historial al inicio (¡Ahora usa el ID dinámico!)
        viewModel.cargarConversaciones(idUsuario);
    }

    private void setupLaunchers() {
        // Launcher para pedir permisos
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamara();
                    } else {
                        Toast.makeText(requireContext(),
                                "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // --- CORREGIDO ---
        // Launcher para tomar foto
        // El callback 'success' indica si la escritura en la URI de entrada (cameraImageUri) fue exitosa
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        // La foto se guardó en 'cameraImageUri'. Esa es nuestra URI seleccionada.
                        selectedImageUri = cameraImageUri;
                        mostrarPreviewImagen(selectedImageUri);
                    } else {
                        // Si falla o se cancela, reseteamos la URI
                        cameraImageUri = null;
                    }
                }
        );

        // Launcher para seleccionar imagen de galería
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // La 'uri' de la galería ya es una content:// URI
                        selectedImageUri = uri;
                        mostrarPreviewImagen(uri);
                    }
                }
        );
    }

    private void initViews(View view) {
        drawerLayout = view.findViewById(R.id.drawer_layout);
        recyclerChat = view.findViewById(R.id.recycler_chat);
        recyclerHistory = view.findViewById(R.id.recycler_history);
        etMessage = view.findViewById(R.id.et_chat_message);
        btnSend = view.findViewById(R.id.btn_send_chat);
        btnAttach = view.findViewById(R.id.btn_attach);
        btnMenu = view.findViewById(R.id.btn_menu);
        btnNewChat = view.findViewById(R.id.btn_new_chat);
        progressBar = view.findViewById(R.id.progress_bar);
        imagePreviewContainer = view.findViewById(R.id.image_preview_container);
        imagePreview = view.findViewById(R.id.image_preview);
        btnRemoveImage = view.findViewById(R.id.btn_remove_image);

        // --- 2. ENCONTRAR EL BOTÓN DE VOZ ---
        btnVoiceChat = view.findViewById(R.id.btn_voice_chat);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Observar mensajes
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.updateMessages(messages);
            if (!messages.isEmpty()) {
                recyclerChat.scrollToPosition(messages.size() - 1);
            }
        });

        // Observar loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSend.setEnabled(!isLoading);
            btnAttach.setEnabled(!isLoading);
        });

        // Observar historial de conversaciones
        viewModel.getConversaciones().observe(getViewLifecycleOwner(), conversaciones -> {
            historyAdapter.updateConversaciones(conversaciones);
        });
    }

    private void setupRecyclerViews() {
        // RecyclerView del chat
        chatAdapter = new ChatAdapter();
        LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getContext());
        chatLayoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(chatLayoutManager);
        recyclerChat.setAdapter(chatAdapter);

        // RecyclerView del historial
        historyAdapter = new HistoryAdapter(this::cargarConversacion);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerHistory.setAdapter(historyAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> enviarMensaje());
        btnAttach.setOnClickListener(v -> mostrarOpcionesImagen());
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(androidx.core.view.GravityCompat.START));
        btnNewChat.setOnClickListener(v -> nuevaConversacion());
        btnRemoveImage.setOnClickListener(v -> quitarImagen());

        // --- 3. CONFIGURAR EL LISTENER DEL BOTÓN DE VOZ ---
        if (btnVoiceChat != null) {
            btnVoiceChat.setOnClickListener(v -> {
                try {
                    // Obtiene el NavController desde el Fragment
                    NavController navController = Navigation.findNavController(v);

                    // Ejecuta la acción de navegación que definimos en mobile_navigation.xml
                    // (Asegúrate de que el ID 'action_chatFragment_to_voiceChatFragment' existe en tu XML)
                    navController.navigate(R.id.action_chatFragment_to_voiceChatFragment);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al navegar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // --- CORREGIDO ---
    private void enviarMensaje() {
        String mensaje = etMessage.getText().toString().trim();

        // Ya no se usa getRealPathFromURI ni se crea una file:// URI
        if (selectedImageUri != null) {
            // ¡Ahora usa el ID dinámico!
            viewModel.enviarMensajeConImagen(idUsuario, mensaje, selectedImageUri);
            quitarImagen(); // Limpiar la preview
        } else if (!mensaje.isEmpty()) {
            // ¡Ahora usa el ID dinámico!
            viewModel.enviarMensaje(idUsuario, mensaje);
        } else {
            // No hacer nada si está vacío
            return;
        }

        etMessage.setText(""); // Limpiar el input
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar foto", "Elegir de galería"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Seleccionar imagen")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        verificarPermisosCamara();
                    } else {
                        abrirGaleria();
                    }
                })
                .show();
    }

    private void verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            // Pedir permiso
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // --- CORREGIDO ---
    private void abrirCamara() {
        try {
            File photoFile = crearArchivoImagen();
            if (photoFile != null) {
                // Generar la content:// URI usando el FileProvider
                // Esta 'cameraImageUri' se guarda como variable de instancia
                cameraImageUri = FileProvider.getUriForFile(
                        requireContext(),
                        // AVISO: Asegúrate que esta autoridad coincide con tu AndroidManifest.xml
                        // (basado en tu manifest, debe ser ".provider")
                        requireContext().getPackageName() + ".provider",
                        photoFile
                );
                // Lanzar la cámara, pidiéndole que guarde la foto en nuestra URI
                takePictureLauncher.launch(cameraImageUri);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al preparar la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        pickImageLauncher.launch("image/*");
    }

    // --- ELIMINADO ---
    // El método getRealPathFromURI() se eliminó por completo.
    // Ya no es necesario y era la fuente del error.

    // --- MODIFICADO ---
    // Este método ya no necesita guardar 'currentPhotoPath'
    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Crear el archivo temporal
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void mostrarPreviewImagen(Uri uri) {
        imagePreviewContainer.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(imagePreview);
    }

    private void quitarImagen() {
        selectedImageUri = null;
        cameraImageUri = null;
        imagePreviewContainer.setVisibility(View.GONE);
    }

    private void cargarConversacion(ConversacionItem conversacion) {
        // ¡Ahora usa el ID dinámico!
        viewModel.cargarMensajesDeConversacion(idUsuario, conversacion.getIdConversacion());
        drawerLayout.closeDrawers();
        Toast.makeText(requireContext(),
                "Cargando: " + conversacion.getTitulo(), Toast.LENGTH_SHORT).show();
    }

    private void nuevaConversacion() {
        viewModel.nuevaConversacion();
        drawerLayout.closeDrawers();
        Toast.makeText(requireContext(), "Nueva conversación", Toast.LENGTH_SHORT).show();
    }
}