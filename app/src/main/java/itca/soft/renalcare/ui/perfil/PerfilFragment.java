package itca.soft.renalcare.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.MedicationItem;

public class PerfilFragment extends Fragment {

    private RecyclerView recyclerMedications;
    private MedicationAdapter medicationAdapter;
    private List<MedicationItem> medicationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Configurar lista de medicamentos
        recyclerMedications = view.findViewById(R.id.recycler_medications);
        setupMedicationList();

        // 2. Configurar datos de metas
        setupGoalBars(view);

        // 3. Configurar datos de laboratorios
        setupLabIndicators(view);
    }

    private void setupMedicationList() {
        medicationList = new ArrayList<>();
        // Datos simulados
        medicationList.add(new MedicationItem("Sevelamer", "800 mg", "08:00 AM", true));
        medicationList.add(new MedicationItem("Eritropoyetina", "4000 UI", "08:00 AM", true));
        medicationList.add(new MedicationItem("Calcitriol", "0.25 mcg", "02:00 PM", false));
        medicationList.add(new MedicationItem("Furosemida", "40 mg", "08:00 PM", false));

        medicationAdapter = new MedicationAdapter(medicationList);
        recyclerMedications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMedications.setAdapter(medicationAdapter);
    }

    private void setupGoalBars(View view) {
        // Metas (obtenidas del <include>)
        View goalSodio = view.findViewById(R.id.goal_sodio);
        View goalPotasio = view.findViewById(R.id.goal_potasio);
        View goalFosforo = view.findViewById(R.id.goal_fosforo);
        View goalPeso = view.findViewById(R.id.goal_peso);

        // Configurar Sodio
        ((TextView) goalSodio.findViewById(R.id.tv_goal_label)).setText("Límite de Sodio");
        ((TextView) goalSodio.findViewById(R.id.tv_goal_value)).setText("Máx. 2,000 mg");
        ((ProgressBar) goalSodio.findViewById(R.id.progress_goal)).setProgress(80); // Simulado

        // Configurar Potasio
        ((TextView) goalPotasio.findViewById(R.id.tv_goal_label)).setText("Límite de Potasio");
        ((TextView) goalPotasio.findViewById(R.id.tv_goal_value)).setText("Máx. 2,500 mg");
        ((ProgressBar) goalPotasio.findViewById(R.id.progress_goal)).setProgress(60); // Simulado

        // Configurar Fósforo
        ((TextView) goalFosforo.findViewById(R.id.tv_goal_label)).setText("Límite de Fósforo");
        ((TextView) goalFosforo.findViewById(R.id.tv_goal_value)).setText("Máx. 1,000 mg");
        ((ProgressBar) goalFosforo.findViewById(R.id.progress_goal)).setProgress(75); // Simulado

        // Configurar Peso Seco
        ((TextView) goalPeso.findViewById(R.id.tv_goal_label)).setText("Peso Seco");
        ((TextView) goalPeso.findViewById(R.id.tv_goal_value)).setText("68 kg");
        ((ProgressBar) goalPeso.findViewById(R.id.progress_goal)).setProgress(90); // Simulado
    }

    private void setupLabIndicators(View view) {
        // Laboratorios (simulamos el progreso)
        ((CircularProgressIndicator) view.findViewById(R.id.progress_potasio)).setProgress(51);
        ((CircularProgressIndicator) view.findViewById(R.id.progress_fosforo)).setProgress(48);
        ((CircularProgressIndicator) view.findViewById(R.id.progress_creatinina)).setProgress(62);

    }
}