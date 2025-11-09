package itca.soft.renalcare.auth.steps;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.SignupPatientViewModel;

public class Step2PersonalFragment extends Fragment {

    private SignupPatientViewModel viewModel;
    private TextInputEditText etFechaNacimiento, etNombreContacto, etTelefonoContacto;
    private RadioGroup rgGenero;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Obtener el ViewModel compartido
        viewModel = new ViewModelProvider(requireActivity()).get(SignupPatientViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 2. Inflar el layout
        return inflater.inflate(R.layout.fragment_signup_step2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. Encontrar las vistas
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        rgGenero = view.findViewById(R.id.rgGenero);
        etNombreContacto = view.findViewById(R.id.etNombreContacto);
        etTelefonoContacto = view.findViewById(R.id.etTelefonoContacto);

        // 4. Cargar datos DESDE el ViewModel (para restaurar el estado)
        loadDataFromViewModel();

        // 5. Configurar Listeners para guardar DATOS HACIA el ViewModel
        setupListeners();
    }

    private void loadDataFromViewModel() {
        // Cargar Fecha
        etFechaNacimiento.setText(viewModel.fechaNacimiento.getValue());

        // Cargar Contacto Opcional
        etNombreContacto.setText(viewModel.contactoNombre.getValue());
        etTelefonoContacto.setText(viewModel.contactoTelefono.getValue());

        // Cargar Género (esto es un poco más complejo)
        String generoGuardado = viewModel.genero.getValue();
        if (generoGuardado != null) {
            if (generoGuardado.equals("masculino")) {
                rgGenero.check(R.id.rbMasculino);
            } else if (generoGuardado.equals("femenino")) {
                rgGenero.check(R.id.rbFemenino);
            } else if (generoGuardado.equals("otro")) {
                rgGenero.check(R.id.rbOtro);
            }
        }
    }

    private void setupListeners() {
        // --- Listener para el DatePicker ---
        etFechaNacimiento.setOnClickListener(v -> showDatePickerDialog());

        // --- Listener para el Género ---
        rgGenero.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMasculino) {
                viewModel.genero.setValue("masculino");
            } else if (checkedId == R.id.rbFemenino) {
                viewModel.genero.setValue("femenino");
            } else if (checkedId == R.id.rbOtro) {
                viewModel.genero.setValue("otro");
            }
        });

        // --- Listeners para Contacto Opcional ---
        etNombreContacto.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.contactoNombre.setValue(s.toString());
            }
        });

        etTelefonoContacto.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.contactoTelefono.setValue(s.toString());
            }
        });
    }

    private void showDatePickerDialog() {
        // Obtener la fecha actual para el picker
        Calendar cal = Calendar.getInstance();

        // Si ya hay una fecha en el ViewModel, usarla
        String fechaGuardada = viewModel.fechaNacimiento.getValue();
        if (fechaGuardada != null && !fechaGuardada.isEmpty()) {
            try {
                String[] partes = fechaGuardada.split("-");
                cal.set(Calendar.YEAR, Integer.parseInt(partes[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(partes[1]) - 1); // Mes es 0-indexado
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(partes[2]));
            } catch (Exception e) {
                // Fallback a la fecha actual si el formato es inválido
            }
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, yearSet, monthSet, daySet) -> {
                    // Formato para la BD (YYYY-MM-DD)
                    String fechaFormateada = String.format(Locale.getDefault(), "%04d-%02d-%02d", yearSet, monthSet + 1, daySet);

                    // Guardar en ambos lugares
                    etFechaNacimiento.setText(fechaFormateada);
                    viewModel.fechaNacimiento.setValue(fechaFormateada);
                },
                year, month, day
        );

        // Opcional: Poner un límite para que no se registren fechas futuras
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    // Helper class para no implementar todos los métodos de TextWatcher
    abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}