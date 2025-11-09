// ChatAdapter.java
package itca.soft.renalcare.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    // ViewHolder para mensajes enviados
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private ImageView ivMessageImage;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message_sent);
            ivMessageImage = itemView.findViewById(R.id.iv_message_image_sent);
        }

        public void bind(ChatMessage message) {
            // Mostrar texto si existe
            if (message.getContent() != null && !message.getContent().isEmpty()) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message.getContent());
            } else {
                tvMessage.setVisibility(View.GONE);
            }

            // Mostrar imagen si existe
            if (message.hasImage() && ivMessageImage != null) {
                ivMessageImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(message.getImageUrl())
                        .centerCrop()
                        .into(ivMessageImage);
            } else if (ivMessageImage != null) {
                ivMessageImage.setVisibility(View.GONE);
            }
        }
    }

    // ViewHolder para mensajes recibidos (solo texto)
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_chat_message);
        }

        public void bind(ChatMessage message) {
            // Solo mostrar texto
            tvMessage.setText(message.getContent());
        }
    }
}