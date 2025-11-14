package itca.soft.renalcare.ui.websoket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.Mensaje;
import itca.soft.renalcare.data.network.WebSocketService;
import itca.soft.renalcare.data.repository.ConversacionRepository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.util.ArrayList;

/**
 * Fragmento que muestra los mensajes de una conversación específica.
 */
public class HumanChatRoomFragment extends Fragment implements WebSocketService.ChatWebSocketListener {

    private static final String TAG = "HumanChatRoomFragment";

    // UI
    private RecyclerView rvMensajes;
    private EditText etMensajeInput;
    private Button btnEnviarMensaje;
    private Button btnAgregarUsuario;
    private TextView tvParticipantes;
    private Toolbar toolbar;
    private HumanChatMessagesAdapter messagesAdapter;

    // Datos
    private String idConversacion;
    private String idUsuario;
    private String nombreUsuario;
    private String titulo;
    private ArrayList<Mensaje> listaMensajes = new ArrayList<>();

    // Servicios
    private WebSocketService wsService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener datos del bundle
        if (getArguments() != null) {
            idConversacion = getArguments().getString("id_conversacion");
            idUsuario = getArguments().getString("id_usuario");
            nombreUsuario = getArguments().getString("nombre_usuario");
            titulo = getArguments().getString("titulo");
        }

        // Obtener instancia del WebSocket y registrarse como listener
        wsService = WebSocketService.getInstance();
        wsService.setListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_human_chat_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar Toolbar
        toolbar = view.findViewById(R.id.chat_room_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(titulo);

        toolbar.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Configurar Vistas
        rvMensajes = view.findViewById(R.id.rvMensajes);
        etMensajeInput = view.findViewById(R.id.etMensajeInput);
        btnEnviarMensaje = view.findViewById(R.id.btnEnviarMensaje);
        btnAgregarUsuario = view.findViewById(R.id.btnAgregarUsuario);
        tvParticipantes = view.findViewById(R.id.tvParticipantes);

        // Configurar RecyclerView
        rvMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesAdapter = new HumanChatMessagesAdapter(listaMensajes, idUsuario);
        rvMensajes.setAdapter(messagesAdapter);

        // Conectar al WebSocket si no está conectado
        if (!wsService.isConnected()) {
            wsService.connect();
        }
        
        // Registrar usuario y unirse a la sala
        wsService.registerUser(idUsuario);
        wsService.joinRoom(idConversacion, idUsuario);

        btnEnviarMensaje.setOnClickListener(v -> enviarMensaje());
        btnAgregarUsuario.setOnClickListener(v -> mostrarDialogoAgregarUsuario());

        // Cargar historial y participantes
        cargarHistorialMensajes();
        cargarParticipantes();
    }

    private void enviarMensaje() {
        String contenido = etMensajeInput.getText().toString().trim();
        if (!contenido.isEmpty()) {
            if (wsService.isConnected()) {
                wsService.sendMessage(idConversacion, idUsuario, contenido, nombreUsuario);
                etMensajeInput.setText("");
                Toast.makeText(getContext(), "Enviando mensaje...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No conectado al servidor", Toast.LENGTH_SHORT).show();
                // Intentar reconectar
                wsService.connect();
            }
        }
    }

    private void cargarHistorialMensajes() {
        android.util.Log.d(TAG, " Cargando historial para conversación: " + idConversacion + " | Usuario logueado: " + idUsuario);
        
        ConversacionRepository repo = new ConversacionRepository();
        repo.getMensajesPorConversacion(idConversacion, new ConversacionRepository.OnMensajesCallback() {
            @Override
            public void onSuccess(java.util.List<Object> mensajes) {
                listaMensajes.clear();
                Gson gson = new Gson();
                int totalMensajes = mensajes.size();
                
                for (Object obj : mensajes) {
                    try {
                        JsonElement element = (JsonElement) obj;
                        JsonObject jsonObj = element.getAsJsonObject();
                        
                        // Parsear id_emisor
                        String idEmisor = "";
                        if (jsonObj.has("id_emisor")) {
                            if (jsonObj.get("id_emisor").isJsonPrimitive()) {
                                idEmisor = String.valueOf(jsonObj.get("id_emisor").getAsInt());
                            } else {
                                idEmisor = jsonObj.get("id_emisor").getAsString();
                            }
                        }
                        
                        // Crear Mensaje
                        Mensaje mensaje = new Mensaje(
                                String.valueOf(jsonObj.get("id_mensaje").getAsInt()),
                                String.valueOf(jsonObj.get("id_conversacion").getAsInt()),
                                idEmisor,
                                jsonObj.get("contenido").getAsString(),
                                jsonObj.get("tipo").getAsString(),
                                jsonObj.get("fecha_envio").getAsString()
                        );
                        
                        // Log dinámico: mostrar quién envió el mensaje
                        boolean esMio = idEmisor.equals(idUsuario);
                        android.util.Log.d(TAG, (esMio ? "" : "") + " Mensaje ID: " + mensaje.getId_mensaje() + 
                                " | Emisor: " + idEmisor + " | ¿Es mío?: " + esMio + " | Contenido: " + mensaje.getContenido());
                        
                        listaMensajes.add(mensaje);
                    } catch (Exception e) {
                        android.util.Log.e(TAG, " Error parseando mensaje: " + e.getMessage(), e);
                    }
                }
                
                android.util.Log.d(TAG, " Total mensajes cargados: " + listaMensajes.size() + " de " + totalMensajes);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        messagesAdapter.notifyDataSetChanged();
                        if (listaMensajes.size() > 0) {
                            rvMensajes.scrollToPosition(listaMensajes.size() - 1);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error cargando historial: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean loading) {
                // Aquí podrías mostrar/ocultar un ProgressBar si lo agregas al layout.
            }
        });
    }

    private void cargarParticipantes() {
        ConversacionRepository repo = new ConversacionRepository();
        repo.getParticipantesPorConversacion(idConversacion, new ConversacionRepository.OnParticipantesCallback() {
            @Override
            public void onSuccess(java.util.List<Object> participantes) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < participantes.size(); i++) {
                            try {
                                JsonElement element = (JsonElement) participantes.get(i);
                                JsonObject jsonObj = element.getAsJsonObject();
                                String nombre = jsonObj.has("nombre") ? jsonObj.get("nombre").getAsString() : "Usuario";
                                sb.append(nombre);
                                if (i < participantes.size() - 1) {
                                    sb.append(", ");
                                }
                            } catch (Exception e) {
                                android.util.Log.e(TAG, "Error parseando participante", e);
                            }
                        }
                        tvParticipantes.setText(sb.toString().isEmpty() ? "Sin participantes" : sb.toString());
                        
                        // Deshabilitar botón si ya hay 2 participantes
                        btnAgregarUsuario.setEnabled(participantes.size() < 2);
                        if (participantes.size() >= 2) {
                            btnAgregarUsuario.setAlpha(0.5f);
                        } else {
                            btnAgregarUsuario.setAlpha(1.0f);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e(TAG, "Error cargando participantes: " + error);
            }

            @Override
            public void onLoading(boolean loading) {}
        });
    }

    private void mostrarDialogoAgregarUsuario() {
        // Verificar que no haya más de 2 participantes
        ConversacionRepository repo = new ConversacionRepository();
        repo.getParticipantesPorConversacion(idConversacion, new ConversacionRepository.OnParticipantesCallback() {
            @Override
            public void onSuccess(java.util.List<Object> participantes) {
                if (participantes.size() >= 2) {
                    Toast.makeText(getContext(), "Este chat solo permite 2 usuarios", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear diálogo para ingresar DUI
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                builder.setTitle("Agregar Usuario");
                builder.setMessage("Ingresa el DUI del usuario a agregar:");

                EditText input = new EditText(requireContext());
                input.setHint("Ej: 12345678-9");
                input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Agregar", (dialog, which) -> {
                    String dui = input.getText().toString().trim();
                    if (!dui.isEmpty()) {
                        agregarUsuarioPorDUI(dui);
                    } else {
                        Toast.makeText(getContext(), "Por favor ingresa un DUI", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
                builder.show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean loading) {}
        });
    }

    private void agregarUsuarioPorDUI(String dui) {
        // Buscar usuario por DUI y agregarlo a la conversación
        ConversacionRepository repo = new ConversacionRepository();
        repo.buscarUsuarioPorDUI(dui, new ConversacionRepository.OnUsuarioCallback() {
            @Override
            public void onSuccess(Object usuario) {
                try {
                    JsonElement element = (JsonElement) usuario;
                    JsonObject jsonObj = element.getAsJsonObject();
                    String idUsuarioNuevo = String.valueOf(jsonObj.get("id_usuario").getAsInt());
                    String nombreUsuarioNuevo = jsonObj.get("nombre").getAsString();

                    // Agregar participante a la conversación
                    repo.agregarParticipante(idConversacion, idUsuarioNuevo, new ConversacionRepository.OnSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Usuario " + nombreUsuarioNuevo + " agregado", Toast.LENGTH_SHORT).show();
                            cargarParticipantes();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error procesando usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Usuario no encontrado: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Quitar el listener para evitar memory leaks
        wsService.removeListener();
    }

    // --- MÉTODOS DEL LISTENER DE WEBSOCKET ---

    @Override
    public void onConnect() {
        // Reconectado, volver a unirse a la sala
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Conectado al servidor", Toast.LENGTH_SHORT).show()
            );
        }
        wsService.registerUser(idUsuario);
        wsService.joinRoom(idConversacion, idUsuario);
    }

    @Override
    public void onDisconnect() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Desconectado...", Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public void onNewMessage(Mensaje mensaje) {
        if (getActivity() != null && mensaje.getId_conversacion().equals(idConversacion)) {
            getActivity().runOnUiThread(() -> {
                listaMensajes.add(mensaje);
                messagesAdapter.notifyItemInserted(listaMensajes.size() - 1);
                rvMensajes.scrollToPosition(listaMensajes.size() - 1);
            });
        }
    }

    // (Implementar el resto de métodos del listener: onMessageEdited, onMessageDeleted, etc.)
    @Override public void onMessageEdited(Mensaje mensaje) {}
    @Override public void onMessageDeleted(String idMensaje) {}
    @Override public void onUserJoined(String idUsuario) {}
    @Override public void onUserTyping(String idUsuario, String nombreUsuario) {}
    @Override public void onUserStoppedTyping(String idUsuario) {}
    @Override public void onMessageRead(String idMensaje, String idUsuario) {}
    @Override public void onError(String error) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show()
            );
        }
    }
}