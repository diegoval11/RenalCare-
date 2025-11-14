package itca.soft.renalcare.ui.websoket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.Mensaje;

public class HumanChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Mensaje> mensajes;
    private final String idUsuarioLogueado;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public HumanChatMessagesAdapter(List<Mensaje> mensajes, String idUsuarioLogueado) {
        this.mensajes = mensajes;
        this.idUsuarioLogueado = idUsuarioLogueado;
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = mensajes.get(position);
        if (mensaje.getId_emisor().equals(idUsuarioLogueado)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            // ¡OJO! Usamos el layout 'item_chat_message_received.xml'
            // pero tu XML se ve diseñado para la IA (con el avatar de Karito).
            // Funcionará, pero puede que quieras un layout diferente para chat humano.
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageHolder) holder).bind(mensaje);
        } else {
            ((ReceivedMessageHolder) holder).bind(mensaje);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    // --- HOLDER CORREGIDO para item_chat_message_sent.xml ---
    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        // No hay 'timeText' en tu XML, así que lo quitamos.

        SentMessageHolder(View itemView) {
            super(itemView);
            // ID Corregido (aunque este ya estaba bien)
            messageText = itemView.findViewById(R.id.tv_message_sent);

            // (Ocultamos el contenedor de imagen por ahora, ya que solo manejamos texto)
            View imageContainer = itemView.findViewById(R.id.cv_image_container_sent);
            if (imageContainer != null) {
                imageContainer.setVisibility(View.GONE);
            }
        }

        void bind(Mensaje mensaje) {
            messageText.setText(mensaje.getContenido());
            // No hay lógica para 'timeText'
        }
    }

    // --- HOLDER CORREGIDO para item_chat_message_received.xml ---
    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        // No hay 'timeText' ni 'nameText' en tu XML, así que los quitamos.

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            // ¡ID CORREGIDO!
            messageText = itemView.findViewById(R.id.tv_chat_message);
        }

        void bind(Mensaje mensaje) {
            messageText.setText(mensaje.getContenido());
            // No hay lógica para 'timeText' ni 'nameText'
        }
    }
}