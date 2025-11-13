package itca.soft.renalcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.PacienteDoctor;
import java.util.ArrayList;
import java.util.List;

public class PacienteDoctorAdapter extends RecyclerView.Adapter<PacienteDoctorAdapter.ViewHolder> {

    private List<PacienteDoctor> pacientes;
    private OnPacienteClickListener listener;
    private OnPacienteDeleteListener deleteListener;

    public interface OnPacienteClickListener {
        void onPacienteClick(PacienteDoctor paciente);
    }

    public interface OnPacienteDeleteListener {
        void onPacienteDelete(PacienteDoctor paciente);
    }

    public PacienteDoctorAdapter(List<PacienteDoctor> pacientes, OnPacienteClickListener listener, OnPacienteDeleteListener deleteListener) {
        this.pacientes = pacientes;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paciente_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PacienteDoctor paciente = pacientes.get(position);
        holder.tvNombre.setText(paciente.getNombre());
        holder.tvDui.setText("DUI: " + paciente.getDui());
        holder.tvTratamiento.setText("Tratamiento: " + paciente.getTipo_tratamiento());
        holder.tvGenero.setText("Género: " + paciente.getGenero());
        holder.tvPeso.setText("Peso: " + paciente.getPeso() + " kg");
        holder.tvCreatinina.setText("Creatinina: " + paciente.getNivel_creatinina());

        holder.itemView.setOnClickListener(v -> listener.onPacienteClick(paciente));
        holder.btnEliminar.setOnClickListener(v -> deleteListener.onPacienteDelete(paciente));
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public void setData(List<PacienteDoctor> data) {
        this.pacientes = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombre, tvDui, tvTratamiento, tvGenero, tvPeso, tvCreatinina;
        public Button btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDui = itemView.findViewById(R.id.tvCorreo);
            tvTratamiento = itemView.findViewById(R.id.tvTratamiento);
            tvGenero = itemView.findViewById(R.id.tvGenero);
            tvPeso = itemView.findViewById(R.id.tvPeso);
            tvCreatinina = itemView.findViewById(R.id.tvCreatinina);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}