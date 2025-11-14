package itca.soft.renalcare.ui.websoket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.Mensaje;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private List<Mensaje> mensajes;
    private String idUsuarioActual;
    private static final int TIPO_ENVIADO = 1;
    private static final int TIPO_RECIBIDO = 2;

    public MensajeAdapter(List<Mensaje> mensajes, String idUsuarioActual) {
        this.mensajes = mensajes;
        this.idUsuarioActual = idUsuarioActual;
        android.util.Log.d("MensajeAdapter", "🔑 ID Usuario Actual: " + idUsuarioActual);
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = mensajes.get(position);

        // Comparar strings correctamente
        boolean esMio = mensaje.getId_emisor() != null &&
                mensaje.getId_emisor().equals(idUsuarioActual);

        android.util.Log.d("MensajeAdapter",
                "Posición: " + position +
                        " | id_emisor: " + mensaje.getId_emisor() +
                        " | idUsuario: " + idUsuarioActual +
                        " | ¿Es mío?: " + esMio);

        return esMio ? TIPO_ENVIADO : TIPO_RECIBIDO;
    }

    @Override
    public MensajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        int viewType = getItemViewType(position);

        holder.tvContenido.setText(mensaje.getContenido());
        holder.tvHora.setText(formatearHora(mensaje.getFecha_envio()));
        View spacer = holder.itemView.findViewById(R.id.spacer);

        if (viewType == TIPO_ENVIADO) {
            // 🟦 MENSAJE ENVIADO (DERECHA - AZUL)
            // Mostrar spacer para empujar el mensaje a la derecha
            spacer.setVisibility(View.VISIBLE);
            holder.tvContenido.setBackgroundResource(R.drawable.bg_mensaje_enviado);
            holder.tvContenido.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
            holder.tvHora.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
        } else {
            // 🟩 MENSAJE RECIBIDO (IZQUIERDA - GRIS)
            // Ocultar spacer para que el mensaje quede a la izquierda
            spacer.setVisibility(View.GONE);
            holder.tvContenido.setBackgroundResource(R.drawable.bg_mensaje_recibido);
            holder.tvContenido.setTextColor(holder.itemView.getContext().getColor(android.R.color.black));
            holder.tvHora.setTextColor(holder.itemView.getContext().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
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

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView tvContenido;
        TextView tvHora;

        public MensajeViewHolder(View itemView) {
            super(itemView);
            tvContenido = itemView.findViewById(R.id.tvContenido);
            tvHora = itemView.findViewById(R.id.tvHora);
        }
    }
}