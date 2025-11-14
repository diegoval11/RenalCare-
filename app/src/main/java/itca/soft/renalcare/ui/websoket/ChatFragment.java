package itca.soft.renalcare.ui.websoket;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.Mensaje;
import itca.soft.renalcare.data.network.WebSocketService;
import itca.soft.renalcare.data.repository.ConversacionRepository;

public class ChatFragment extends Fragment implements WebSocketService.ChatWebSocketListener {

    private WebSocketService webSocketService;
    private EditText etMensaje;
    private Button btnEnviar;
    private RecyclerView rvMensajes;
    private MensajeAdapter adapter;
    private List<Mensaje> mensajes;
    private TextView tvEstado;
    private TextView tvTitulo;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private Handler typingHandler;
    private Gson gson;

    private String idConversacion;
    private String idUsuario;
    private String titulo;
    private boolean isTyping = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_websoket, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        etMensaje = view.findViewById(R.id.etMensaje);
        btnEnviar = view.findViewById(R.id.btnEnviar);
        rvMensajes = view.findViewById(R.id.rvMensajes);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvTitulo = view.findViewById(R.id.tvTitulo);
        btnBack = view.findViewById(R.id.btnBack);
        progressBar = view.findViewById(R.id.progressBar);

        gson = new Gson();

        // Obtener argumentos
        if (getArguments() != null) {
            idConversacion = getArguments().getString("id_conversacion", "");
            idUsuario = getArguments().getString("id_usuario", "");
            titulo = getArguments().getString("titulo", "Chat");
        }

        tvTitulo.setText(titulo);
        tvEstado.setText("⏳ Cargando mensajes...");

        // Configurar RecyclerView
        mensajes = new ArrayList<>();
        adapter = new MensajeAdapter(mensajes, idUsuario);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvMensajes.setLayoutManager(layoutManager);
        rvMensajes.setAdapter(adapter);

        // Handler para el typing
        typingHandler = new Handler(getContext().getMainLooper());

        // Configurar WebSocket
        webSocketService = WebSocketService.getInstance();
        webSocketService.setListener(this);

        if (!webSocketService.isConnected()) {
            webSocketService.connect();
        }
        webSocketService.joinRoom(idConversacion, idUsuario);

        // ⭐ CARGAR MENSAJES DESDE API
        cargarMensajes();

        // Listeners
        btnEnviar.setOnClickListener(v -> enviarMensaje());

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        etMensaje.setOnEditorActionListener((v, actionId, event) -> {
            enviarMensaje();
            return true;
        });

        // Agregar listener de texto para detectar cuando escribe
        etMensaje.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !isTyping) {
                    isTyping = true;
                    webSocketService.notifyTyping(idConversacion, idUsuario, titulo);

                    typingHandler.removeCallbacksAndMessages(null);
                    typingHandler.postDelayed(() -> {
                        if (isTyping) {
                            isTyping = false;
                            webSocketService.notifyStopTyping(idConversacion, idUsuario);
                        }
                    }, 3000);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    /**
     * ⭐ Cargar mensajes desde API
     */
    private void cargarMensajes() {
        ConversacionRepository repo = new ConversacionRepository();

        repo.getMensajesPorConversacion(idConversacion, new ConversacionRepository.OnMensajesCallback() {
            @Override
            public void onSuccess(List<Object> listaMensajes) {
                if (listaMensajes != null && !listaMensajes.isEmpty()) {
                    mensajes.clear();

                    // Convertir JsonObject a Mensaje
                    for (Object obj : listaMensajes) {
                        try {
                            JsonObject jsonObj = gson.toJsonTree(obj).getAsJsonObject();

                            // Obtener id_emisor y convertirlo a string
                            String idEmisor = "";
                            if (jsonObj.has("id_emisor")) {
                                if (jsonObj.get("id_emisor").isJsonPrimitive()) {
                                    idEmisor = String.valueOf(jsonObj.get("id_emisor").getAsInt());
                                } else {
                                    idEmisor = jsonObj.get("id_emisor").getAsString();
                                }
                            }

                            Mensaje mensaje = new Mensaje(
                                    String.valueOf(jsonObj.get("id_mensaje").getAsInt()),
                                    String.valueOf(jsonObj.get("id_conversacion").getAsInt()),
                                    idEmisor,
                                    jsonObj.get("contenido").getAsString(),
                                    jsonObj.get("tipo").getAsString(),
                                    jsonObj.get("fecha_envio").getAsString()
                            );

                            android.util.Log.d("ChatFragment", "Mensaje: id_emisor=" + idEmisor + ", idUsuario=" + idUsuario + ", contenido=" + mensaje.getContenido());
                            mensajes.add(mensaje);
                        } catch (Exception e) {
                            android.util.Log.e("ChatFragment", "Error parseando mensaje", e);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    rvMensajes.scrollToPosition(mensajes.size() - 1);
                    tvEstado.setText("✅ Conectado");
                }
            }

            @Override
            public void onError(String error) {
                tvEstado.setText("❌ Error: " + error);
                android.util.Log.e("ChatFragment", "Error cargando mensajes: " + error);
            }

            @Override
            public void onLoading(boolean loading) {
                if (loading) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void enviarMensaje() {
        String contenido = etMensaje.getText().toString().trim();

        if (contenido.isEmpty()) {
            Toast.makeText(getContext(), "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        if (webSocketService != null && webSocketService.isConnected()) {
            webSocketService.sendMessage(idConversacion, idUsuario, contenido);
            etMensaje.setText("");
            webSocketService.notifyStopTyping(idConversacion, idUsuario);
            isTyping = false;
        } else {
            Toast.makeText(getContext(), "Sin conexión", Toast.LENGTH_SHORT).show();
        }
    }

    // ===== IMPLEMENTAR LISTENERS WEBSOCKET =====

    @Override
    public void onConnect() {
        if (getContext() != null) {
            tvEstado.setText("✅ Conectado");
        }
        if (webSocketService != null) {
            webSocketService.joinRoom(idConversacion, idUsuario);
        }
    }

    @Override
    public void onDisconnect() {
        if (getContext() != null) {
            tvEstado.setText("❌ Desconectado");
        }
    }

    @Override
    public void onNewMessage(Mensaje mensaje) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mensajes.add(mensaje);
                adapter.notifyItemInserted(mensajes.size() - 1);
                rvMensajes.scrollToPosition(mensajes.size() - 1);
            });
        }
    }

    @Override
    public void onMessageEdited(Mensaje mensaje) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                for (int i = 0; i < mensajes.size(); i++) {
                    if (mensajes.get(i).getId_mensaje().equals(mensaje.getId_mensaje())) {
                        mensajes.set(i, mensaje);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public void onMessageDeleted(String idMensaje) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                for (int i = 0; i < mensajes.size(); i++) {
                    if (mensajes.get(i).getId_mensaje().equals(idMensaje)) {
                        mensajes.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public void onUserJoined(String idUsuario) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    tvEstado.setText("✅ Usuario se unió")
            );
        }
    }

    @Override
    public void onUserTyping(String idUsuario, String nombreUsuario) {
        if (!idUsuario.equals(this.idUsuario)) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        tvEstado.setText("✍️ " + nombreUsuario + " está escribiendo...")
                );
            }
        }
    }

    @Override
    public void onUserStoppedTyping(String idUsuario) {
        if (!idUsuario.equals(this.idUsuario)) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        tvEstado.setText("✅ Conectado")
                );
            }
        }
    }

    @Override
    public void onMessageRead(String idMensaje, String idUsuario) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                for (Mensaje msg : mensajes) {
                    if (msg.getId_mensaje().equals(idMensaje)) {
                        msg.setLeido(true);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public void onError(String errorMessage) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocketService != null) {
            webSocketService.removeListener();
        }
        if (typingHandler != null) {
            typingHandler.removeCallbacksAndMessages(null);
        }
    }
}