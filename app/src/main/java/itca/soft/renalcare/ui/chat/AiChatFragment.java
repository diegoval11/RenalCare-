package itca.soft.renalcare.ui.chat;

// (Todos los imports de tu ChatFragment.java original)
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
import itca.soft.renalcare.auth.LoginActivity;
import itca.soft.renalcare.data.models.ConversacionItem;

/**
 * Este fragmento contiene la lógica del Chat de IA.
 * Es el código de tu 'ChatFragment' original, movido aquí.
 */
public class AiChatFragment extends Fragment {

    // (Todas las variables de tu ChatFragment original van aquí)
    private RecyclerView recyclerChat, recyclerHistory;
    private EditText etMessage;
    private ImageButton btnSend, btnAttach, btnMenu, btnNewChat;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private CardView imagePreviewContainer;
    private ImageView imagePreview;
    private ImageButton btnRemoveImage;
    private ImageButton btnVoiceChat;
    private ChatAdapter chatAdapter;
    private HistoryAdapter historyAdapter;
    private ChatViewModel viewModel;
    private Uri selectedImageUri;
    private Uri cameraImageUri;
    private int idUsuario; // Se asignará en onViewCreated
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
        // Inflar el layout del chat de IA
        return inflater.inflate(R.layout.fragment_ai_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cargar ID de usuario (igual que antes)
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        idUsuario = prefs.getInt(LoginActivity.KEY_ID_USUARIO, -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Error de sesión: ID no encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        initViews(view);
        setupViewModel();
        setupRecyclerViews();
        setupClickListeners();

        viewModel.cargarConversaciones(idUsuario);
    }

    // (Aquí van TODOS los métodos privados de tu ChatFragment original)
    // setupLaunchers, initViews, setupViewModel, setupRecyclerViews,
    // setupClickListeners, enviarMensaje, mostrarOpcionesImagen,
    // verificarPermisosCamara, abrirCamara, abrirGaleria,
    // crearArchivoImagen, mostrarPreviewImagen, quitarImagen,
    // cargarConversacion, nuevaConversacion

    private void setupLaunchers() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) abrirCamara();
                    else Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
        );
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        selectedImageUri = cameraImageUri;
                        mostrarPreviewImagen(selectedImageUri);
                    } else {
                        cameraImageUri = null;
                    }
                }
        );
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
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
        btnVoiceChat = view.findViewById(R.id.btn_voice_chat);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.updateMessages(messages);
            if (!messages.isEmpty()) {
                recyclerChat.scrollToPosition(messages.size() - 1);
            }
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSend.setEnabled(!isLoading);
            btnAttach.setEnabled(!isLoading);
        });
        viewModel.getConversaciones().observe(getViewLifecycleOwner(), conversaciones -> {
            historyAdapter.updateConversaciones(conversaciones);
        });
    }

    private void setupRecyclerViews() {
        chatAdapter = new ChatAdapter();
        LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getContext());
        chatLayoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(chatLayoutManager);
        recyclerChat.setAdapter(chatAdapter);

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
        if (btnVoiceChat != null) {
            btnVoiceChat.setOnClickListener(v -> {
                try {
                    NavController navController = Navigation.findNavController(v);
                    // ¡OJO! La acción de navegación ahora debe salir de AiChatFragment
                    navController.navigate(R.id.action_aiChatFragment_to_voiceChatFragment);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al navegar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void enviarMensaje() {
        String mensaje = etMessage.getText().toString().trim();
        if (selectedImageUri != null) {
            viewModel.enviarMensajeConImagen(idUsuario, mensaje, selectedImageUri);
            quitarImagen();
        } else if (!mensaje.isEmpty()) {
            viewModel.enviarMensaje(idUsuario, mensaje);
        } else {
            return;
        }
        etMessage.setText("");
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar foto", "Elegir de galería"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Seleccionar imagen")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) verificarPermisosCamara();
                    else abrirGaleria();
                })
                .show();
    }

    private void verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        try {
            File photoFile = crearArchivoImagen();
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().getPackageName() + ".provider",
                        photoFile
                );
                takePictureLauncher.launch(cameraImageUri);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al preparar la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        pickImageLauncher.launch("image/*");
    }

    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void mostrarPreviewImagen(Uri uri) {
        imagePreviewContainer.setVisibility(View.VISIBLE);
        Glide.with(this).load(uri).centerCrop().into(imagePreview);
    }

    private void quitarImagen() {
        selectedImageUri = null;
        cameraImageUri = null;
        imagePreviewContainer.setVisibility(View.GONE);
    }

    private void cargarConversacion(ConversacionItem conversacion) {
        viewModel.cargarMensajesDeConversacion(idUsuario, conversacion.getIdConversacion());
        drawerLayout.closeDrawers();
        Toast.makeText(requireContext(), "Cargando: " + conversacion.getTitulo(), Toast.LENGTH_SHORT).show();
    }

    private void nuevaConversacion() {
        viewModel.nuevaConversacion();
        drawerLayout.closeDrawers();
        Toast.makeText(requireContext(), "Nueva conversación", Toast.LENGTH_SHORT).show();
    }
}