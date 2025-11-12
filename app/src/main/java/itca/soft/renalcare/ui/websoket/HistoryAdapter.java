package itca.soft.renalcare.ui.websoket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.Conversacion;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ConversacionViewHolder> {
    private List<Conversacion> conversaciones;
    private OnConversacionClickListener listener;

    public interface OnConversacionClickListener {
        void onConversacionClick(Conversacion conversacion);
        void onConversacionLongClick(Conversacion conversacion);
    }

    public HistoryAdapter(List<Conversacion> conversaciones, OnConversacionClickListener listener) {
        this.conversaciones = conversaciones;
        this.listener = listener;
    }

    @Override
    public ConversacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversacion, parent, false);
        return new ConversacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversacionViewHolder holder, int position) {
        Conversacion conv = conversaciones.get(position);

        // Nombre o grupo
        String titulo = conv.getTipo().equals("grupo") ?
                conv.getNombre_grupo() :
                conv.getParticipantes();
        holder.tvTitulo.setText(titulo);

        // Último mensaje
        holder.tvUltimoMensaje.setText(conv.getUltimo_mensaje() != null ?
                conv.getUltimo_mensaje() : "Sin mensajes");

        // Hora del último mensaje
        holder.tvFecha.setText(formatearHora(conv.getTimestamp_ultimo()));

        // Badge de no leídos
        if (conv.getCantidad_no_leidos() > 0) {
            holder.tvNoLeidos.setVisibility(View.VISIBLE);
            holder.tvNoLeidos.setText(String.valueOf(conv.getCantidad_no_leidos()));
        } else {
            holder.tvNoLeidos.setVisibility(View.GONE);
        }

        // Avatar
        int avatarRes = conv.getTipo().equals("grupo") ?
                R.drawable.ic_grupo :
                R.drawable.ic_usuario;
        holder.imgAvatar.setImageResource(avatarRes);

        holder.itemView.setOnClickListener(v -> listener.onConversacionClick(conv));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onConversacionLongClick(conv);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return conversaciones.size();
    }

    private String formatearHora(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) return "";
        try {
            if (timestamp.contains("T")) {
                return timestamp.split("T")[1].substring(0, 5);
            }
            return timestamp;
        } catch (Exception e) {
            return "";
        }
    }

    public static class ConversacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvUltimoMensaje;
        TextView tvFecha;
        TextView tvNoLeidos;
        ImageView imgAvatar;

        public ConversacionViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_conversacion_titulo);
            tvUltimoMensaje = itemView.findViewById(R.id.tvUltimoMensaje);
            tvFecha = itemView.findViewById(R.id.tv_conversacion_fecha);
            tvNoLeidos = itemView.findViewById(R.id.tvNoLeidos);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}