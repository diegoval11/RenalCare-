package itca.soft.renalcare.ui.doctores;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import itca.soft.renalcare.DoctorActivity;
import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.LoginActivity;

public class PerfilDoctorFragment extends Fragment {

    private TextView tvNombreDoctor;
    private TextView tvIdDoctor;
    private Button btnCerrarSesion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_doctor, container, false);
        
        initializeViews(view);
        setupClickListeners();
        loadDoctorInfo();
        
        return view;
    }

    private void initializeViews(View view) {
        tvNombreDoctor = view.findViewById(R.id.tvNombreDoctor);
        tvIdDoctor = view.findViewById(R.id.tvIdDoctor);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
    }

    private void setupClickListeners() {
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void loadDoctorInfo() {
        if (getActivity() instanceof DoctorActivity) {
            DoctorActivity doctorActivity = (DoctorActivity) getActivity();
            String nombreDoctor = doctorActivity.getDoctorName();
            int idDoctor = doctorActivity.getDoctorId();
            
            if (tvNombreDoctor != null) {
                tvNombreDoctor.setText("Dr. " + nombreDoctor);
            }
            if (tvIdDoctor != null) {
                tvIdDoctor.setText("ID: " + idDoctor);
            }
        }
    }

    private void cerrarSesion() {
        if (getActivity() instanceof DoctorActivity) {
            DoctorActivity doctorActivity = (DoctorActivity) getActivity();
            
            // Limpiar SharedPreferences
            doctorActivity.getDoctorPreferences().edit().clear().apply();
            
            // Volver al login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
