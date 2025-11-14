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
    private Toolbar toolbar;
    // private ChatMessagesAdapter messagesAdapter; // (Necesitarás un adaptador para esto)

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

        // Configurar RecyclerView
        rvMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        // messagesAdapter = new ChatMessagesAdapter(listaMensajes, idUsuario);
        // rvMensajes.setAdapter(messagesAdapter);

        // Notificar al socket que nos unimos a esta sala
        wsService.joinRoom(idConversacion, idUsuario);

        btnEnviarMensaje.setOnClickListener(v -> enviarMensaje());

        // (Aquí deberías llamar a tu ConversacionRepository para cargar el historial de mensajes)
        Toast.makeText(getContext(), "Cargando historial...", Toast.LENGTH_SHORT).show();
    }

    private void enviarMensaje() {
        String contenido = etMensajeInput.getText().toString().trim();
        if (!contenido.isEmpty()) {
            wsService.sendMessage(idConversacion, idUsuario, contenido);
            etMensajeInput.setText("");
            // (El mensaje se añadirá al RecyclerView cuando llegue por el listener 'onNewMessage')
        }
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
                // listaMensajes.add(mensaje);
                // messagesAdapter.notifyItemInserted(listaMensajes.size() - 1);
                // rvMensajes.scrollToPosition(listaMensajes.size() - 1);
                Toast.makeText(getContext(), "Nuevo mensaje: " + mensaje.getContenido(), Toast.LENGTH_SHORT).show();
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
    @Override public void onError(String error) {}
}