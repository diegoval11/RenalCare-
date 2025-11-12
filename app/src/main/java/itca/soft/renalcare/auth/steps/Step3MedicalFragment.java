package itca.soft.renalcare.auth.steps;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.SignupPatientViewModel;

public class Step3MedicalFragment extends Fragment {

    private SignupPatientViewModel viewModel;
    private RadioGroup rgCondicion, rgTratamiento;
    private TextInputLayout tilPeso, tilCreatinina;
    private TextInputEditText etPeso, etCreatinina;
    private CheckBox cbPesoOmitido, cbCreatininaOmitida;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SignupPatientViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup_step3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Encontrar Vistas
        rgCondicion = view.findViewById(R.id.rgCondicion);
        rgTratamiento = view.findViewById(R.id.rgTratamiento);
        tilPeso = view.findViewById(R.id.tilPeso);
        etPeso = view.findViewById(R.id.etPeso);
        cbPesoOmitido = view.findViewById(R.id.cbPesoOmitido);
        tilCreatinina = view.findViewById(R.id.tilCreatinina);
        etCreatinina = view.findViewById(R.id.etCreatinina);
        cbCreatininaOmitida = view.findViewById(R.id.cbCreatininaOmitida);

        loadDataFromViewModel();
        setupListeners();
    }

    private void loadDataFromViewModel() {
        // Cargar Condición Renal
        String condicion = viewModel.condicionRenal.getValue();
        if (condicion != null) {
            if (condicion.equals("cronica")) rgCondicion.check(R.id.rbCondicionCronica);
            else if (condicion.equals("aguda")) rgCondicion.check(R.id.rbCondicionAguda);
            else if (condicion.equals("no_se")) rgCondicion.check(R.id.rbCondicionNoSe);
        }

        // Cargar Tipo de Tratamiento
        String tratamiento = viewModel.tipoTratamiento.getValue();
        if (tratamiento != null) {
            if (tratamiento.equals("hemodialisis")) rgTratamiento.check(R.id.rbTratamientoHemo);
            else if (tratamiento.equals("dialisis")) rgTratamiento.check(R.id.rbTratamientoPeritoneal);
            else if (tratamiento.equals("trasplante")) rgTratamiento.check(R.id.rbTratamientoTrasplante);
            else if (tratamiento.equals("otro")) rgTratamiento.check(R.id.rbTratamientoOtro);
            else if (tratamiento.equals("no_se")) rgTratamiento.check(R.id.rbTratamientoNoSe);
        }

        // Cargar Peso
        etPeso.setText(viewModel.peso.getValue());
        boolean pesoOmitido = Boolean.TRUE.equals(viewModel.pesoOmitido.getValue());
        cbPesoOmitido.setChecked(pesoOmitido);
        etPeso.setEnabled(!pesoOmitido);
        tilPeso.setEnabled(!pesoOmitido);

        // Cargar Creatinina
        etCreatinina.setText(viewModel.creatinina.getValue());
        boolean creatininaOmitida = Boolean.TRUE.equals(viewModel.creatininaOmitida.getValue());
        cbCreatininaOmitida.setChecked(creatininaOmitida);
        etCreatinina.setEnabled(!creatininaOmitida);
        tilCreatinina.setEnabled(!creatininaOmitida);
    }

    private void setupListeners() {
        // Listener Condición
        rgCondicion.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCondicionCronica) viewModel.condicionRenal.setValue("cronica");
            else if (checkedId == R.id.rbCondicionAguda) viewModel.condicionRenal.setValue("aguda");
            else if (checkedId == R.id.rbCondicionNoSe) viewModel.condicionRenal.setValue("no_se");
        });

        // Listener Tratamiento
        rgTratamiento.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTratamientoHemo) viewModel.tipoTratamiento.setValue("hemodialisis");
            else if (checkedId == R.id.rbTratamientoPeritoneal) viewModel.tipoTratamiento.setValue("dialisis"); // Mapeo clave
            else if (checkedId == R.id.rbTratamientoTrasplante) viewModel.tipoTratamiento.setValue("trasplante");
            else if (checkedId == R.id.rbTratamientoOtro) viewModel.tipoTratamiento.setValue("otro");
            else if (checkedId == R.id.rbTratamientoNoSe) viewModel.tipoTratamiento.setValue("no_se");
        });

        // Listener Omitir Peso
        cbPesoOmitido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.pesoOmitido.setValue(isChecked);
            etPeso.setEnabled(!isChecked);
            tilPeso.setEnabled(!isChecked);
            if (isChecked) {
                etPeso.setText(""); // Borra el texto si se omite
                viewModel.peso.setValue("");
            }
        });

        // Listener Omitir Creatinina
        cbCreatininaOmitida.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.creatininaOmitida.setValue(isChecked);
            etCreatinina.setEnabled(!isChecked);
            tilCreatinina.setEnabled(!isChecked);
            if (isChecked) {
                etCreatinina.setText(""); // Borra el texto si se omite
                viewModel.creatinina.setValue("");
            }
        });

        // Listeners de texto
        etPeso.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.peso.setValue(s.toString());
            }
        });

        etCreatinina.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.creatinina.setValue(s.toString());
            }
        });
    }

    // Helper class
    abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}