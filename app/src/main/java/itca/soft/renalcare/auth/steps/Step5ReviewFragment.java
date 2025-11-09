package itca.soft.renalcare.auth.steps;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.Medicamento;
import itca.soft.renalcare.auth.SignupPatientViewModel;

public class Step5ReviewFragment extends Fragment {

    private SignupPatientViewModel viewModel;

    private TextView tvNombre, tvDui, tvFechaNac, tvGenero, tvContacto,
            tvCondicion, tvTratamiento, tvPeso, tvCreatinina,
            tvMedicamentos, tvDieta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(SignupPatientViewModel.class);
        return inflater.inflate(R.layout.fragment_signup_step5, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Encontrar todas las vistas
        tvNombre = view.findViewById(R.id.tvReviewNombre);
        tvDui = view.findViewById(R.id.tvReviewDui);
        tvFechaNac = view.findViewById(R.id.tvReviewFechaNac);
        tvGenero = view.findViewById(R.id.tvReviewGenero);
        tvContacto = view.findViewById(R.id.tvReviewContacto);
        tvCondicion = view.findViewById(R.id.tvReviewCondicion);
        tvTratamiento = view.findViewById(R.id.tvReviewTratamiento);
        tvPeso = view.findViewById(R.id.tvReviewPeso);
        tvCreatinina = view.findViewById(R.id.tvReviewCreatinina);
        tvMedicamentos = view.findViewById(R.id.tvReviewMedicamentos);
        tvDieta = view.findViewById(R.id.tvReviewDieta);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cargar datos CADA VEZ que el fragmento se muestre
        // Esto asegura que si el usuario retrocede, cambia algo y vuelve,
        // los datos de revisión estarán actualizados.
        loadDataFromViewModel();
    }

    private void loadDataFromViewModel() {
        String noEsp = "No especificado";

        // 1. Cuenta
        tvNombre.setText("Nombre: " + viewModel.nombre.getValue());
        tvDui.setText("DUI: " + viewModel.dui.getValue());

        // 2. Personal
        tvFechaNac.setText("Nacimiento: " + formatText(viewModel.fechaNacimiento.getValue(), noEsp));
        tvGenero.setText("Género: " + formatText(viewModel.genero.getValue(), noEsp, true));

        String cNombre = viewModel.contactoNombre.getValue();
        String cTel = viewModel.contactoTelefono.getValue();
        if (!TextUtils.isEmpty(cNombre) && !TextUtils.isEmpty(cTel)) {
            tvContacto.setText("Contacto: " + cNombre + " (" + cTel + ")");
        } else {
            tvContacto.setText("Contacto: " + noEsp);
        }

        // 3. Condición
        tvCondicion.setText("Condición: " + formatText(viewModel.condicionRenal.getValue(), noEsp, true));
        tvTratamiento.setText("Tratamiento: " + formatText(viewModel.tipoTratamiento.getValue(), noEsp, true));

        if (Boolean.TRUE.equals(viewModel.pesoOmitido.getValue()) || TextUtils.isEmpty(viewModel.peso.getValue())) {
            tvPeso.setText("Peso: " + noEsp);
        } else {
            tvPeso.setText("Peso: " + viewModel.peso.getValue() + " lbs");
        }

        if (Boolean.TRUE.equals(viewModel.creatininaOmitida.getValue()) || TextUtils.isEmpty(viewModel.creatinina.getValue())) {
            tvCreatinina.setText("Creatinina: " + noEsp);
        } else {
            tvCreatinina.setText("Creatinina: " + viewModel.creatinina.getValue() + " mg/dL");
        }

        // 4. Tratamiento
        // Medicamentos
        if (Boolean.TRUE.equals(viewModel.medicamentosOmitidos.getValue())) {
            tvMedicamentos.setText("Medicamentos omitidos (serán añadidos por tu doctor).");
        } else {
            List<Medicamento> medList = viewModel.medicamentos.getValue();
            if (medList == null || medList.isEmpty()) {
                tvMedicamentos.setText("No se agregaron medicamentos.");
            } else {
                StringBuilder medBuilder = new StringBuilder();
                for (Medicamento med : medList) {
                    medBuilder.append("• ")
                            .append(formatText(med.nombre, "Medicamento", false))
                            .append(" (")
                            .append(formatText(med.dosis, "N/A", false))
                            .append(" mg) - ")
                            .append(formatText(med.horario, "N/A", false))
                            .append("\n");
                }
                tvMedicamentos.setText(medBuilder.toString().trim());
            }
        }

        // Dieta
        Set<String> dietaSet = viewModel.dieta.getValue();
        if (dietaSet == null || dietaSet.isEmpty()) {
            tvDieta.setText("Sin restricciones de dieta especificadas.");
        } else {
            // Unir el Set con comas
            tvDieta.setText("• " + String.join(", ", dietaSet));
        }
    }

    /** Helper para mostrar "No especificado" y capitalizar */
    private String formatText(String text, String defaultText, boolean capitalize) {
        if (text == null || text.isEmpty() || text.equals("no_se")) {
            return defaultText;
        }
        if (capitalize) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    /** Helper para mostrar "No especificado" (sin capitalizar) */
    private String formatText(String text, String defaultText) {
        return formatText(text, defaultText, false);
    }
}