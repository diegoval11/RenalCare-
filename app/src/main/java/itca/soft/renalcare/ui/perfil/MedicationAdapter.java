package itca.soft.renalcare.ui.perfil;

import android.graphics.Paint; // <-- Importar
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map; // <-- Importar

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.MedicationItem;
import itca.soft.renalcare.data.models.TodayReminderStatus;

// --- Interfaz para el clic del Checkbox ---
interface OnMedicationTakeListener {
    void onMedicationToggled(MedicationItem medication, TodayReminderStatus status, boolean isChecked);
}

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<MedicationItem> medicationList;
    // Un "mapa" que guarda el estado de hoy (id_medicamento -> estado)
    private Map<Integer, TodayReminderStatus> statusMap;
    private OnMedicationTakeListener listener;

    public MedicationAdapter(List<MedicationItem> medicationList, Map<Integer, TodayReminderStatus> statusMap, OnMedicationTakeListener listener) {
        this.medicationList = medicationList;
        this.statusMap = statusMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        MedicationItem item = medicationList.get(position);
        holder.bind(item, statusMap.get(item.getId_medicamento()), listener);
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    // --- ViewHolder ahora maneja la lógica ---
    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        CheckBox medTaken;
        TextView medName;
        TextView medDose;
        TextView medTime;
        // (El icon_reminder_menu no está en este layout, está en el de ReminderAdapter)

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medTaken = itemView.findViewById(R.id.cb_med_taken);
            medName = itemView.findViewById(R.id.tv_med_name);
            medDose = itemView.findViewById(R.id.tv_med_dose);
            medTime = itemView.findViewById(R.id.tv_med_time);
        }

        public void bind(MedicationItem medication, TodayReminderStatus status, OnMedicationTakeListener listener) {
            medName.setText(medication.getNombre());
            medDose.setText(medication.getDosis());
            medTime.setText(medication.getHorario());

            // Lógica del Checkbox
            if (status != null) {
                // Si existe un recordatorio para hoy
                medTaken.setVisibility(View.VISIBLE);

                if (status.getEstado().equals("completado")) {
                    medTaken.setChecked(true);
                    medName.setPaintFlags(medName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Tachar
                    medTaken.setEnabled(false); // No se puede des-marcar
                } else if (status.getEstado().equals("pendiente")) {
                    medTaken.setChecked(false);
                    medName.setPaintFlags(medName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Quitar tachado
                    medTaken.setEnabled(true);
                }

                medTaken.setOnClickListener(v -> {
                    listener.onMedicationToggled(medication, status, medTaken.isChecked());
                });

            } else {
                // Si no hay recordatorio para hoy (ej. es Sábado y solo toma Lunes)
                medTaken.setVisibility(View.INVISIBLE); // Ocultar
                medName.setPaintFlags(medName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }
}