package itca.soft.renalcare.ui.perfil;

import android.content.Context; // <-- 1. IMPORTAR
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu; // <-- 2. IMPORTAR
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.ReminderItem;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<ReminderItem> reminderList;
    private OnReminderDeleteListener deleteListener;

    // La interfaz no necesita cambios
    public interface OnReminderDeleteListener {
        void onDeleteClick(ReminderItem reminder, int position);
    }

    public ReminderAdapter(List<ReminderItem> reminderList, OnReminderDeleteListener deleteListener) {
        this.reminderList = reminderList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        return new ReminderViewHolder(view);
    }

    // ▼▼▼ BLOQUE onBindViewHolder MODIFICADO ▼▼▼
    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderItem item = reminderList.get(position);

        holder.medName.setText(item.getTitulo());
        holder.medDose.setText(item.getDescripcion());

        String fullDateTime = item.getFechaHora();
        if (fullDateTime != null && fullDateTime.length() > 10) {
            holder.medTime.setText(fullDateTime.substring(11, 16));
        } else {
            holder.medTime.setText(item.getFechaHora());
        }

        if (holder.medTaken != null) {
            holder.medTaken.setVisibility(View.GONE);
        }

        // --- 3. Lógica del Menú Emergente ---
        holder.iconMenu.setOnClickListener(view -> {
            // Crear el PopupMenu
            PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.iconMenu);
            // Inflar nuestro archivo res/menu/reminder_item_menu.xml
            popup.getMenuInflater().inflate(R.menu.reminder_item_menu, popup.getMenu());

            // Asignar el listener de clic a los items del menú
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.action_delete_reminder) {
                    // Si se toca "Eliminar", llamamos a nuestra interfaz
                    deleteListener.onDeleteClick(item, holder.getAdapterPosition());
                    return true;
                }
                return false;
            });

            // Mostrar el menú
            popup.show();
        });
    }
    // ▲▲▲ FIN DEL BLOQUE ▲▲▲

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // ▼▼▼ BLOQUE ViewHolder MODIFICADO ▼▼▼
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        View medTaken;
        TextView medName;
        TextView medDose;
        TextView medTime;
        ImageView iconMenu; // <-- 4. Renombrado (antes iconDelete)

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            medTaken = itemView.findViewById(R.id.cb_med_taken);
            medName = itemView.findViewById(R.id.tv_med_name);
            medDose = itemView.findViewById(R.id.tv_med_dose);
            medTime = itemView.findViewById(R.id.tv_med_time);
            // 5. Apuntar al nuevo ID del layout
            iconMenu = itemView.findViewById(R.id.icon_reminder_menu);
        }
    }
    // ▲▲▲ FIN DEL BLOQUE ▲▲▲
}