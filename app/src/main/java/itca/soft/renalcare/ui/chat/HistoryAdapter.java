// HistoryAdapter.java
package itca.soft.renalcare.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.ConversacionItem;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ConversacionItem> conversaciones = new ArrayList<>();
    private OnConversacionClickListener listener;

    public interface OnConversacionClickListener {
        void onConversacionClick(ConversacionItem conversacion);
    }

    public HistoryAdapter(OnConversacionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversacion, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ConversacionItem conversacion = conversaciones.get(position);
        holder.bind(conversacion, listener);
    }

    @Override
    public int getItemCount() {
        return conversaciones.size();
    }

    public void updateConversaciones(List<ConversacionItem> nuevasConversaciones) {
        this.conversaciones = nuevasConversaciones;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitulo;
        private TextView tvFecha;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_conversacion_titulo);
            tvFecha = itemView.findViewById(R.id.tv_conversacion_fecha);
        }

        public void bind(ConversacionItem conversacion, OnConversacionClickListener listener) {
            tvTitulo.setText(conversacion.getTitulo());
            tvFecha.setText(formatearFecha(conversacion.getFecha()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConversacionClick(conversacion);
                }
            });
        }

        private String formatearFecha(String fecha) {
            // Formato simple, puedes mejorarlo
            if (fecha == null || fecha.isEmpty()) return "";

            try {
                // Si la fecha viene como "2024-01-15 10:30:00"
                String[] partes = fecha.split(" ");
                if (partes.length > 0) {
                    String[] fechaParts = partes[0].split("-");
                    if (fechaParts.length == 3) {
                        return fechaParts[2] + "/" + fechaParts[1] + "/" + fechaParts[0];
                    }
                }
            } catch (Exception e) {
                return fecha;
            }
            return fecha;
        }
    }
}