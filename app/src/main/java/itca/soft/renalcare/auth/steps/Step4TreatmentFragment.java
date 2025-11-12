package itca.soft.renalcare.auth.steps;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.Medicamento;
import itca.soft.renalcare.auth.SignupPatientViewModel;

public class Step4TreatmentFragment extends Fragment {

    private SignupPatientViewModel viewModel;

    // Vistas de Medicamentos
    private LinearLayout containerMedicamentos;
    private Button btnAddMedicamento;
    private Button btnSkipMedicamentos;
    private TextView tvMedsOmitidos;
    private ImageButton btnMedicationInfo;

    // Vistas de Dieta
    private CheckBox cbBajoSodio, cbBajoPotasio, cbBajoFosforo, cbBajoProteinas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SignupPatientViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup_step4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Encontrar vistas de Medicamentos ---
        containerMedicamentos = view.findViewById(R.id.containerMedicamentos);
        btnAddMedicamento = view.findViewById(R.id.btnAddMedicamento);
        btnSkipMedicamentos = view.findViewById(R.id.btnSkipMedicamentos);
        tvMedsOmitidos = view.findViewById(R.id.tvMedsOmitidos);
        btnMedicationInfo = view.findViewById(R.id.btnMedicationInfo);

        // --- Encontrar vistas de Dieta ---
        cbBajoSodio = view.findViewById(R.id.cbBajoSodio);
        cbBajoPotasio = view.findViewById(R.id.cbBajoPotasio);
        cbBajoFosforo = view.findViewById(R.id.cbBajoFosforo);
        cbBajoProteinas = view.findViewById(R.id.cbBajoProteinas);

        loadDataFromViewModel();
        setupListeners();
    }

    private void loadDataFromViewModel() {
        // --- Cargar Medicamentos ---
        boolean medsOmitidos = Boolean.TRUE.equals(viewModel.medicamentosOmitidos.getValue());
        if (medsOmitidos) {
            skipMedicamentosUI(false); // Cargar estado "omitido" sin guardar de nuevo
        } else {
            // Restaurar la lista de medicamentos
            containerMedicamentos.removeAllViews(); // Limpiar por si acaso
            List<Medicamento> medList = viewModel.medicamentos.getValue();
            if (medList != null) {
                for (Medicamento med : medList) {
                    agregarFilaMedicamento(med); // Cargar fila con datos
                }
            }
        }

        // --- Cargar Dieta ---
        Set<String> dieta = viewModel.dieta.getValue();
        if (dieta != null) {
            cbBajoSodio.setChecked(dieta.contains(getString(R.string.patient_diet_opt1)));
            cbBajoPotasio.setChecked(dieta.contains(getString(R.string.patient_diet_opt2)));
            cbBajoFosforo.setChecked(dieta.contains(getString(R.string.patient_diet_opt3)));
            cbBajoProteinas.setChecked(dieta.contains(getString(R.string.patient_diet_opt4)));
        }
    }

    private void setupListeners() {
        // --- Listeners de Medicamentos ---
        btnAddMedicamento.setOnClickListener(v -> {
            // Crear un nuevo medicamento vacío y añadirlo al ViewModel
            Medicamento newMed = new Medicamento("", "", "");
            viewModel.medicamentos.getValue().add(newMed);
            // Añadir la fila de UI, pasando el objeto para enlazarlo
            agregarFilaMedicamento(newMed);
        });

        btnSkipMedicamentos.setOnClickListener(v -> skipMedicamentosUI(true)); // Omitir y guardar
        btnMedicationInfo.setOnClickListener(v -> mostrarDialogoMedicamentos());

        // --- Listeners de Dieta ---
        cbBajoSodio.setOnCheckedChangeListener((btn, isChecked) -> onDietaChanged(isChecked, getString(R.string.patient_diet_opt1)));
        cbBajoPotasio.setOnCheckedChangeListener((btn, isChecked) -> onDietaChanged(isChecked, getString(R.string.patient_diet_opt2)));
        cbBajoFosforo.setOnCheckedChangeListener((btn, isChecked) -> onDietaChanged(isChecked, getString(R.string.patient_diet_opt3)));
        cbBajoProteinas.setOnCheckedChangeListener((btn, isChecked) -> onDietaChanged(isChecked, getString(R.string.patient_diet_opt4)));
    }

    /**
     * Actualiza el Set de Dieta en el ViewModel
     */
    private void onDietaChanged(boolean isChecked, String dietaValor) {
        Set<String> dietaSet = viewModel.dieta.getValue();
        if (dietaSet == null) {
            dietaSet = new HashSet<>();
        }
        if (isChecked) {
            dietaSet.add(dietaValor);
        } else {
            dietaSet.remove(dietaValor);
        }
        viewModel.dieta.setValue(dietaSet); // Disparar la actualización del LiveData
    }

    /**
     * Oculta la UI de medicamentos y actualiza el ViewModel
     * @param guardarActualizacion Si debe o no actualizar el ViewModel (false si solo carga)
     */
    private void skipMedicamentosUI(boolean guardarActualizacion) {
        if (guardarActualizacion) {
            viewModel.medicamentosOmitidos.setValue(true);
            viewModel.medicamentos.getValue().clear(); // Limpiar lista
            viewModel.medicamentos.setValue(viewModel.medicamentos.getValue()); // Notificar
        }
        containerMedicamentos.setVisibility(View.GONE);
        btnAddMedicamento.setVisibility(View.GONE);
        btnSkipMedicamentos.setVisibility(View.GONE);
        tvMedsOmitidos.setVisibility(View.VISIBLE);
        containerMedicamentos.removeAllViews();
    }

    /**
     * Infla una fila de medicamento y la enlaza a un objeto Medicamento del ViewModel
     * @param medicamento El objeto Medicamento para enlazar (nuevo o existente)
     */
    private void agregarFilaMedicamento(@Nullable Medicamento medicamento) {
        if (Boolean.TRUE.equals(viewModel.medicamentosOmitidos.getValue())) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View filaView = inflater.inflate(R.layout.item_medicamento_input, containerMedicamentos, false);

        TextInputEditText etNombreMed = filaView.findViewById(R.id.etNombreMedicamento);
        TextInputEditText etDosisMed = filaView.findViewById(R.id.etDosis);
        TextInputEditText etHorarioMed = filaView.findViewById(R.id.etHorario);
        ImageButton btnEliminar = filaView.findViewById(R.id.btnEliminarFila);

        // Si se pasa un medicamento (cargando datos), poblar los campos
        if (medicamento != null) {
            etNombreMed.setText(medicamento.nombre);
            etDosisMed.setText(medicamento.dosis);
            etHorarioMed.setText(medicamento.horario);
        }

        // --- ENLACE CON VIEWMODEL ---
        // Guardar cambios del usuario de vuelta al objeto Medicamento
        etNombreMed.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (medicamento != null) medicamento.nombre = s.toString();
            }
        });
        etDosisMed.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (medicamento != null) medicamento.dosis = s.toString();
            }
        });
        // Horario se guarda desde el TimePicker

        // --- Listeners de UI (TimePicker y Eliminar) ---
        etHorarioMed.setFocusable(false);
        etHorarioMed.setClickable(true);
        etHorarioMed.setOnClickListener(v -> mostrarTimePickerDialog(etHorarioMed, medicamento));

        btnEliminar.setOnClickListener(v -> {
            ((LinearLayout) filaView.getParent()).removeView(filaView);
            // Eliminar también del ViewModel
            if (medicamento != null) {
                viewModel.medicamentos.getValue().remove(medicamento);
            }
        });

        containerMedicamentos.addView(filaView);
    }

    private void mostrarTimePickerDialog(TextInputEditText etHorario, Medicamento medicamento) {
        Calendar cal = Calendar.getInstance();

        // Intentar parsear la hora guardada, si existe
        if (medicamento != null && !TextUtils.isEmpty(medicamento.horario)) {
            try {
                String[] partes = medicamento.horario.split("[: ]"); // "08:30 AM"
                int hora = Integer.parseInt(partes[0]);
                int minuto = Integer.parseInt(partes[1]);
                if (partes[2].equalsIgnoreCase("PM") && hora != 12) hora += 12;
                if (partes[2].equalsIgnoreCase("AM") && hora == 12) hora = 0; // 12 AM es 00:00
                cal.set(Calendar.HOUR_OF_DAY, hora);
                cal.set(Calendar.MINUTE, minuto);
            } catch (Exception e) {
                // Fallback a la hora actual
            }
        }

        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay < 12 ? "AM" : "PM";
                    int hora12 = hourOfDay % 12;
                    if (hora12 == 0) hora12 = 12;

                    String horarioFormato = String.format(Locale.getDefault(), "%02d:%02d %s", hora12, minute, amPm);

                    // Guardar en ambos lugares
                    etHorario.setText(horarioFormato);
                    if (medicamento != null) {
                        medicamento.horario = horarioFormato;
                    }
                },
                hora, minuto, false // false = formato 12 horas
        );
        timePicker.show();
    }

    private void mostrarDialogoMedicamentos() {
        new AlertDialog.Builder(getContext())
                .setTitle("Info de Medicamentos")
                .setMessage("Añade los medicamentos que usas. Si no estás seguro, puedes omitir este paso y tu doctor los añadirá por ti.")
                .setPositiveButton("Entendido", null)
                .show();
    }

    // Helper class
    abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}