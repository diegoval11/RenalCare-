package itca.soft.renalcare.ui.doctores;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import itca.soft.renalcare.DoctorActivity;
import itca.soft.renalcare.R;

public class DashboardDoctorFragment extends Fragment {

    private TextView tvBienvenidaDoctor;
    private CardView cardGestionPacientes;
    private CardView cardEstadisticas;
    private CardView cardAsignarPaciente;
    private CardView cardReportes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_doctor, container, false);
        
        initializeViews(view);
        setupClickListeners();
        loadDoctorInfo();
        
        return view;
    }

    private void initializeViews(View view) {
        tvBienvenidaDoctor = view.findViewById(R.id.tvBienvenidaDoctor);
        cardGestionPacientes = view.findViewById(R.id.cardGestionPacientes);
        cardEstadisticas = view.findViewById(R.id.cardEstadisticas);
        cardAsignarPaciente = view.findViewById(R.id.cardAsignarPaciente);
        cardReportes = view.findViewById(R.id.cardReportes);
    }

    private void setupClickListeners() {
        cardGestionPacientes.setOnClickListener(v -> abrirGestionPacientes());
        cardEstadisticas.setOnClickListener(v -> abrirEstadisticas());
        cardAsignarPaciente.setOnClickListener(v -> abrirAsignarPaciente());
        cardReportes.setOnClickListener(v -> abrirReportes());
    }

    private void loadDoctorInfo() {
        if (getActivity() instanceof DoctorActivity) {
            DoctorActivity doctorActivity = (DoctorActivity) getActivity();
            String nombreDoctor = doctorActivity.getDoctorName();
            if (tvBienvenidaDoctor != null) {
                tvBienvenidaDoctor.setText("¡Bienvenido, Dr. " + nombreDoctor + "!");
            }
        }
    }

    private void abrirGestionPacientes() {
        if (getActivity() instanceof DoctorActivity) {
            DoctorActivity doctorActivity = (DoctorActivity) getActivity();
            int idDoctor = doctorActivity.getDoctorId();
            if (idDoctor != -1) {
                Intent intent = new Intent(getActivity(), GestionPacientesDoctorActivity.class);
                intent.putExtra("id_doctor", idDoctor);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Error de sesión", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirEstadisticas() {
        if (getActivity() instanceof DoctorActivity) {
            DoctorActivity doctorActivity = (DoctorActivity) getActivity();
            int idDoctor = doctorActivity.getDoctorId();
            if (idDoctor != -1) {
                Intent intent = new Intent(getActivity(), EstadisticasPacienteActivity.class);
                intent.putExtra("id_doctor", idDoctor);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Error de sesión", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirAsignarPaciente() {
        Toast.makeText(getActivity(), "Funcionalidad de asignar paciente próximamente", Toast.LENGTH_SHORT).show();
    }

    private void abrirReportes() {
        Toast.makeText(getActivity(), "Funcionalidad de reportes próximamente", Toast.LENGTH_SHORT).show();
    }
}
