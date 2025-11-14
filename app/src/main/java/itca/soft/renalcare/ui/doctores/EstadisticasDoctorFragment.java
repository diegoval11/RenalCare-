package itca.soft.renalcare.ui.doctores;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import itca.soft.renalcare.DoctorActivity;
import itca.soft.renalcare.R;

public class EstadisticasDoctorFragment extends Fragment {

    private CardView cardEstadisticas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas_doctor, container, false);
        
        initializeViews(view);
        setupClickListeners();
        
        return view;
    }

    private void initializeViews(View view) {
        cardEstadisticas = view.findViewById(R.id.cardEstadisticas);
    }

    private void setupClickListeners() {
        cardEstadisticas.setOnClickListener(v -> abrirEstadisticas());
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
}
