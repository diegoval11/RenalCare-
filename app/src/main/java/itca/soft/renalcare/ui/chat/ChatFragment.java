package itca.soft.renalcare.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.ChatMessage;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerChat = view.findViewById(R.id.recycler_chat);
        setupChat();
    }

    private void setupChat() {
        chatMessages = new ArrayList<>();

        // Datos simulados
        chatMessages.add(new ChatMessage("¡Hola! Soy Karito, tu asistente virtual de RenalCare+. Estoy aquí para ayudarte con información sobre salud renal y brindarte apoyo. ¿En qué puedo ayudarte hoy?", ChatMessage.VIEW_TYPE_RECEIVED));
        chatMessages.add(new ChatMessage("Hola, me gustaría saber más sobre la dieta recomendada para pacientes renales.", ChatMessage.VIEW_TYPE_SENT));
        chatMessages.add(new ChatMessage("Claro, con gusto te ayudo. Una dieta renal saludable generalmente incluye controlar la ingesta de sodio, potasio y fósforo. Es importante consumir proteínas de calidad en cantidades moderadas y mantener una hidratación adecuada según las indicaciones de tu médico. ¿Tienes alguna pregunta específica sobre algún alimento en particular?", ChatMessage.VIEW_TYPE_RECEIVED));

        chatAdapter = new ChatAdapter(chatMessages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // Esta línea es clave: hace que el RecyclerView se llene desde abajo, como un chat
        layoutManager.setStackFromEnd(true);

        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);
    }
}