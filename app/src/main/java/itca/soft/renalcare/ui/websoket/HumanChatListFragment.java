package itca.soft.renalcare.ui.websoket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment; // ¡NUEVO!
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.Conversacion;
// ¡OJO! Asegúrate de que ConversacionAdapter esté en este paquete o importa el correcto
// import itca.soft.renalcare.ui.websoket.ConversacionAdapter;
import itca.soft.renalcare.data.network.WebSocketService;
import itca.soft.renalcare.data.repository.ConversacionRepository;

// Renombrado de ChatListFragment a HumanChatListFragment
public class HumanChatListFragment extends Fragment implements ConversacionAdapter.OnConversacionClickListener {

    private static final String TAG = "HumanChatListFragment";
    private RecyclerView rvConversaciones;
    private ConversacionAdapter adapter;
    private List<Conversacion> conversaciones;
    private FloatingActionButton fabNuevoChat;

    private String idUsuario;
    private String nombreUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Renombrado el layout
        return inflater.inflate(R.layout.fragment_human_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvConversaciones = view.findViewById(R.id.rvConversaciones);
        fabNuevoChat = view.findViewById(R.id.fabNuevoChat);

        // Obtener ID y Nombre de usuario de los argumentos (pasados por el PagerAdapter)
        if (getArguments() != null) {
            idUsuario = getArguments().getString("id_usuario", "");
            nombreUsuario = getArguments().getString("nombre_usuario", "Usuario");
            Log.d(TAG, "Usuario cargado: " + idUsuario + " (" + nombreUsuario + ")");
        }

        conversaciones = new ArrayList<>();
        adapter = new ConversacionAdapter(conversaciones, this);
        rvConversaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConversaciones.setAdapter(adapter);

        fabNuevoChat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Crear nuevo chat", Toast.LENGTH_SHORT).show();
        });

        cargarConversaciones();

        // --- LÓGICA DE WEBSOCKET ELIMINADA DE AQUÍ ---
        // WebSocketService wsService = WebSocketService.getInstance();
        // ... (todo esto ahora vive en PacienteActivity) ...
    }

    private void cargarConversaciones() {
        if (idUsuario == null || idUsuario.isEmpty()) {
            Toast.makeText(getContext(), "Error: No se pudo cargar el ID de usuario.", Toast.LENGTH_SHORT).show();
            return;
        }

        ConversacionRepository repo = new ConversacionRepository();
        repo.getConversacionesPorUsuario(idUsuario, new ConversacionRepository.OnConversacionesCallback() {
            @Override
            public void onSuccess(List<Conversacion> listaConversaciones) {
                if (listaConversaciones != null && !listaConversaciones.isEmpty()) {
                    conversaciones.clear();
                    conversaciones.addAll(listaConversaciones);
                    adapter.notifyDataSetChanged();
                } else {
                    cargarDatosDeJuego();
                }
            }
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                cargarDatosDeJuego();
            }
            @Override
            public void onLoading(boolean loading) {
                // Manejar ProgressBar
            }
        });
    }

    private void cargarDatosDeJuego() {
        // (Tu método de datos de prueba no tiene cambios)
        Conversacion conv1 = new Conversacion("1", "individual", null, "2025-11-11", "Dr. Juan Pérez");
        conv1.setUltimo_mensaje("Hola María, ¿cómo estás? ¿Cómo fue tu sesión?");
        conv1.setTimestamp_ultimo("2025-11-11T14:30:00");
        conv1.setCantidad_no_leidos(3);
        Conversacion conv2 = new Conversacion("2", "individual", null, "2025-11-10", "Carlos Ramírez");
        conv2.setUltimo_mensaje("Gracias por la información sobre la dieta");
        conv2.setTimestamp_ultimo("2025-11-10T11:15:00");
        conv2.setCantidad_no_leidos(0);
        conversaciones.clear();
        conversaciones.add(conv1);
        conversaciones.add(conv2);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConversacionClick(Conversacion conversacion) {
        Log.d(TAG, "Navegando a la sala de chat: " + conversacion.getId_conversacion());

        // Navegar a la sala de chat
        Bundle bundle = new Bundle();
        bundle.putString("id_conversacion", conversacion.getId_conversacion());
        bundle.putString("id_usuario", idUsuario);
        bundle.putString("nombre_usuario", nombreUsuario);
        bundle.putString("titulo", conversacion.getTipo().equals("grupo") ?
                conversacion.getNombre_grupo() :
                conversacion.getParticipantes());

        // ¡OJO! Debes crear esta acción en tu nav_graph.xml
        // que vaya desde ChatFragment (el contenedor) hacia HumanChatRoomFragment
        try {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_chatFragment_to_humanChatRoomFragment, bundle);
        } catch (Exception e) {
            Log.e(TAG, "Error al navegar. Asegúrate de que la acción de navegación exista.", e);
            Toast.makeText(getContext(), "Error al abrir chat: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConversacionLongClick(Conversacion conversacion) {
        Toast.makeText(getContext(), "Conversación: " + conversacion.getId_conversacion(), Toast.LENGTH_SHORT).show();
    }
}