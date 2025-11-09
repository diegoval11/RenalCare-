// ChatFragment.java - VERSIÓN COMPLETA Y ACTUALIZADA
package itca.soft.renalcare.ui.chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.ConversacionItem;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    // Adapters
    private ChatAdapter chatAdapter;
    private HistoryAdapter historyAdapter;

    // ViewModel
    private ChatViewModel viewModel;

    // Imagen seleccionada
    private Uri selectedImageUri;
    private String currentPhotoPath;

    // ID Usuario (cambiar por tu sistema de auth)
    private int idUsuario = 2;

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

        initViews(view);
        setupViewModel();
        setupRecyclerViews();
        setupClickListeners();

        // Cargar historial al inicio
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

        // Launcher para tomar foto
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && currentPhotoPath != null) {
                        selectedImageUri = Uri.fromFile(new File(currentPhotoPath));
                        mostrarPreviewImagen(selectedImageUri);
                    }
                }
        );

        // Launcher para seleccionar imagen de galería
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
    }

    private void enviarMensaje() {
        String mensaje = etMessage.getText().toString().trim();

        if (selectedImageUri != null) {
            // Convertir URI a archivo temporal
            String filePath = getRealPathFromURI(selectedImageUri);
            if (filePath != null) {
                Uri fileUri = Uri.fromFile(new File(filePath));
                viewModel.enviarMensajeConImagen(idUsuario, mensaje, fileUri);
                quitarImagen();
            } else {
                Toast.makeText(requireContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            }
        } else if (!mensaje.isEmpty()) {
            // Enviar solo texto
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
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        try {
            File photoFile = crearArchivoImagen();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().getPackageName() + ".fileprovider",
                        photoFile
                );
                takePictureLauncher.launch(photoUri);
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error al crear archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        pickImageLauncher.launch("image/*");
    }

    // Método helper para obtener la ruta real del archivo
    private String getRealPathFromURI(Uri uri) {
        String result = null;
        try {
            // Para URIs de contenido
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Copiar el archivo a un directorio temporal
                File tempFile = new File(requireContext().getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");

                try (java.io.InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                     java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile)) {

                    if (inputStream != null) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        result = tempFile.getAbsolutePath();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                result = uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
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
        currentPhotoPath = null;
        imagePreviewContainer.setVisibility(View.GONE);
    }

    private void cargarConversacion(ConversacionItem conversacion) {
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