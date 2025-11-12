// 📁 ui/chat/ChatListFragment.java
package itca.soft.renalcare.ui.websoket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.Conversacion;
import itca.soft.renalcare.data.network.WebSocketService;
import itca.soft.renalcare.data.repository.ConversacionRepository;

public class ChatListFragment extends Fragment implements ConversacionAdapter.OnConversacionClickListener {

    private RecyclerView rvConversaciones;
    private ConversacionAdapter adapter;
    private List<Conversacion> conversaciones;
    private FloatingActionButton fabNuevoChat;
    private String idUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        rvConversaciones = view.findViewById(R.id.rvConversaciones);
        fabNuevoChat = view.findViewById(R.id.fabNuevoChat);

        // Obtener ID de usuario de argumentos
        if (getArguments() != null) {
            idUsuario = getArguments().getString("id_usuario", "");
        }

        // Configurar RecyclerView
        conversaciones = new ArrayList<>();
        adapter = new ConversacionAdapter(conversaciones, this);
        rvConversaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConversaciones.setAdapter(adapter);

        // Botón nuevo chat
        fabNuevoChat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Crear nuevo chat", Toast.LENGTH_SHORT).show();
        });

        // Cargar conversaciones desde API
        cargarConversaciones();

        // Conectar WebSocket
        WebSocketService wsService = WebSocketService.getInstance();
        if (!wsService.isConnected()) {
            wsService.connect();
        }
        wsService.registerUser(idUsuario);
    }

    /**
     * Cargar conversaciones desde API
     */
    private void cargarConversaciones() {
        ConversacionRepository repo = new ConversacionRepository();

        repo.getConversacionesPorUsuario(idUsuario, new ConversacionRepository.OnConversacionesCallback() {
            @Override
            public void onSuccess(List<Conversacion> listaConversaciones) {
                if (listaConversaciones != null && !listaConversaciones.isEmpty()) {
                    conversaciones.clear();
                    conversaciones.addAll(listaConversaciones);
                    adapter.notifyDataSetChanged();
                } else {
                    // Si no hay conversaciones, mostrar datos de prueba
                    cargarDatosDeJuego();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                // Cargar datos de prueba en caso de error
                cargarDatosDeJuego();
            }

            @Override
            public void onLoading(boolean loading) {
                // Aquí puedes mostrar un ProgressBar si quieres
            }
        });
    }

    /**
     * Datos de prueba para testing
     */
    private void cargarDatosDeJuego() {
        // Conversación 1: Chat individual con doctor
        Conversacion conv1 = new Conversacion("1", "individual", null, "2025-11-11", "Dr. Juan Pérez");
        conv1.setUltimo_mensaje("Hola María, ¿cómo estás? ¿Cómo fue tu sesión?");
        conv1.setTimestamp_ultimo("2025-11-11T14:30:00");
        conv1.setCantidad_no_leidos(3);

        // Conversación 2: Chat individual con otro paciente
        Conversacion conv2 = new Conversacion("2", "individual", null, "2025-11-10", "Carlos Ramírez");
        conv2.setUltimo_mensaje("Gracias por la información sobre la dieta");
        conv2.setTimestamp_ultimo("2025-11-10T11:15:00");
        conv2.setCantidad_no_leidos(0);

        // Conversación 3: Grupo de cuidadores
        Conversacion conv3 = new Conversacion("3", "grupo", "Cuidado de María", "2025-11-09", "Dr. Juan, Ana Torres");
        conv3.setUltimo_mensaje("Recordemos que mañana es la sesión de diálisis");
        conv3.setTimestamp_ultimo("2025-11-09T16:45:00");
        conv3.setCantidad_no_leidos(1);

        // Conversación 4: Otro chat
        Conversacion conv4 = new Conversacion("4", "individual", null, "2025-11-08", "Enfermera Rosa");
        conv4.setUltimo_mensaje("Los análisis están listos");
        conv4.setTimestamp_ultimo("2025-11-08T09:20:00");
        conv4.setCantidad_no_leidos(0);

        // Agregar a la lista
        conversaciones.clear();
        conversaciones.add(conv1);
        conversaciones.add(conv2);
        conversaciones.add(conv3);
        conversaciones.add(conv4);
        adapter.notifyDataSetChanged();
    }

    /**
     * Cuando hace click en una conversación
     */
    @Override
    public void onConversacionClick(Conversacion conversacion) {
        // Navegar a ChatFragment
        Bundle bundle = new Bundle();
        bundle.putString("id_conversacion", conversacion.getId_conversacion());
        bundle.putString("id_usuario", idUsuario);
        bundle.putString("titulo", conversacion.getTipo().equals("grupo") ?
                conversacion.getNombre_grupo() :
                conversacion.getParticipantes());

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Cuando hace click largo en una conversación
     */
    @Override
    public void onConversacionLongClick(Conversacion conversacion) {
        Toast.makeText(getContext(), "Conversación: " + conversacion.getId_conversacion(), Toast.LENGTH_SHORT).show();
    }
}