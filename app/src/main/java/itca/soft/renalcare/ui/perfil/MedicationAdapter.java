package itca.soft.renalcare.ui.perfil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.MedicationItem;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<MedicationItem> medicationList;

    public MedicationAdapter(List<MedicationItem> medicationList) {
        this.medicationList = medicationList;
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
        holder.medName.setText(item.getName());
        holder.medDose.setText(item.getDose());
        holder.medTime.setText(item.getTime());
        holder.medTaken.setChecked(item.isTaken());
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        CheckBox medTaken;
        TextView medName;
        TextView medDose;
        TextView medTime;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medTaken = itemView.findViewById(R.id.cb_med_taken);
            medName = itemView.findViewById(R.id.tv_med_name);
            medDose = itemView.findViewById(R.id.tv_med_dose);
            medTime = itemView.findViewById(R.id.tv_med_time);
        }
    }
}