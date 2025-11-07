package itca.soft.renalcare.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    // 1. Este método le dice al RecyclerView qué tipo de vista usar en qué posición
    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getViewType();
    }

    // 2. Este método crea el ViewHolder correcto basado en el viewType
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ChatMessage.VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else { // VIEW_TYPE_RECEIVED
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    // 3. Este método bindea los datos al ViewHolder correcto
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (holder.getItemViewType() == ChatMessage.VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // --- Dos ViewHolders, uno para cada tipo de burbuja ---

    // ViewHolder para mensajes ENVIADOS (Usuario)
    private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_chat_message);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
        }
    }

    // ViewHolder para mensajes RECIBIDOS (Karito)
    private static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        // También podrías tener el ImageView del avatar aquí si quisieras cambiarlo

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_chat_message);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
        }
    }
}