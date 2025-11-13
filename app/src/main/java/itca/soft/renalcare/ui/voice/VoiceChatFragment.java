package itca.soft.renalcare.ui.voice;

import android.content.Context;
import android.content.SharedPreferences;
import itca.soft.renalcare.auth.LoginActivity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import itca.soft.renalcare.R;
import itca.soft.renalcare.utils.WebRTCManager;

public class VoiceChatFragment extends Fragment {

    private VoiceChatViewModel viewModel;
    private WebRTCManager webRTCManager;
    private ImageView ivVoiceIndicator;
    private AnimatorSet voiceAnimation;

    // UI Components
    private ImageButton btnMicrophone;
    private TextView tvStatus;
    private ProgressBar progressBar;

    // --- ¡CAMBIO 2! ID de Usuario ahora es dinámico ---
    // private int idUsuario = 2; // <- Línea antigua eliminada
    private int idUsuario; // Se cargará desde SharedPreferences
    // --- Fin del Cambio 2 ---

    // Estado de conexión
    private boolean isConnected = false;

    // Launcher para permisos
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPermissionLauncher();
        webRTCManager = new WebRTCManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voice_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- ¡CAMBIO 3! Cargar ID de usuario dinámicamente ---
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        // 'idUsuario' ahora se refiere a la variable de la clase
        idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Error de sesión: ID no encontrado.", Toast.LENGTH_SHORT).show();
            // Opcional: navegar de vuelta
            // requireActivity().onBackPressed(); 
            return; // No continuar si no hay ID
        }
        // --- Fin del Cambio 3 ---

        initViews(view);
        setupViewModel();

        // 🔹 Iniciar sesión automáticamente al entrar (¡Ahora usa el ID dinámico!)
        verificarPermisoMicrofono();
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        iniciarSesionVoz();
                    } else {
                        Toast.makeText(requireContext(),
                                "Permiso de micrófono necesario", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initViews(View view) {
        // btnMicrophone = view.findViewById(R.id.btn_microphone);
        tvStatus = view.findViewById(R.id.tv_status);
        progressBar = view.findViewById(R.id.progress_bar);
        ivVoiceIndicator = view.findViewById(R.id.iv_voice_indicator);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(VoiceChatViewModel.class);

        // Observar loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observar token de sesión
        viewModel.getSessionToken().observe(getViewLifecycleOwner(), token -> {
            if (token != null && !isConnected) {
                tvStatus.setText("Conectando a WebRTC...");
                connectToWebRTC(token);
            }
        });

        // Observar errores
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                tvStatus.setText("Error al conectar");
                resetUI();
                viewModel.limpiarError();
            }
        });
    }

    private void verificarPermisoMicrofono() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            iniciarSesionVoz();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void iniciarSesionVoz() {
        tvStatus.setText("Solicitando sesión...");
        // ¡Esta línea ahora usa automáticamente el 'idUsuario' dinámico de la clase!
        viewModel.iniciarSesionVoz(idUsuario);
    }

    private void connectToWebRTC(String token) {
        webRTCManager.startVoiceSession(token, new WebRTCManager.VoiceConnectionCallback() {
            @Override
            public void onConnected() {
                requireActivity().runOnUiThread(() -> {
                    isConnected = true;
                    tvStatus.setText("🎤 Conectado - Puedes hablar");
                    //btnMicrophone.setImageResource(R.drawable.ic_mic_recording);
                    iniciarAnimacionVoz();
                    Toast.makeText(requireContext(),
                            "¡Conexión establecida!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onDisconnected() {
                requireActivity().runOnUiThread(() -> {
                    detenerAnimacionVoz();
                    isConnected = false;
                    tvStatus.setText("Desconectado");
                    resetUI();
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    detenerAnimacionVoz();
                    Toast.makeText(requireContext(),
                            "Error: " + error, Toast.LENGTH_LONG).show();
                    tvStatus.setText("Error de conexión");
                    resetUI();
                });
            }
        });
    }

    private void detenerSesion() {
        webRTCManager.stopVoiceSession();
        isConnected = false;
        tvStatus.setText("Sesión finalizada");
        resetUI();
    }

    private void resetUI() {
        isConnected = false;

    }

    @Override
    public void onStop() {
        super.onStop();
        if (isConnected) {
            detenerSesion();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webRTCManager != null) {
            webRTCManager.dispose();
        }
        detenerAnimacionVoz();
    }


    private void iniciarAnimacionVoz() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivVoiceIndicator, "scaleX", 1f, 1.3f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivVoiceIndicator, "scaleY", 1f, 1.3f);
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(ivVoiceIndicator, "colorFilter",
                Color.parseColor("#888888"), Color.parseColor("#00BCD4"));
        colorAnim.setEvaluator(new ArgbEvaluator());

        scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);
        colorAnim.setRepeatMode(ObjectAnimator.REVERSE);

        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        colorAnim.setRepeatCount(ObjectAnimator.INFINITE);

        voiceAnimation = new AnimatorSet();
        voiceAnimation.setInterpolator(new LinearInterpolator());
        voiceAnimation.setDuration(600);
        voiceAnimation.playTogether(scaleX, scaleY, colorAnim);
        voiceAnimation.start();
    }

    private void detenerAnimacionVoz() {
        if (voiceAnimation != null && voiceAnimation.isRunning()) {
            voiceAnimation.cancel();
            ivVoiceIndicator.clearAnimation();
        }
    }

}