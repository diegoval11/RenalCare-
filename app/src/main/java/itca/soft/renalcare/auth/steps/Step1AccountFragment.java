package itca.soft.renalcare.auth.steps;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.text.TextUtils; 

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.SignupPatientViewModel;

public class Step1AccountFragment extends Fragment {

    private SignupPatientViewModel viewModel;
    private TextInputEditText etNombre, etDui, etPassword, etPasswordConfirm, etTelefono;
    private TextInputLayout tilPassword, tilPasswordConfirm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener el ViewModel compartido de la Activity
        viewModel = new ViewModelProvider(requireActivity()).get(SignupPatientViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup_step1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNombre = view.findViewById(R.id.etNombrePaciente);
        etDui = view.findViewById(R.id.etDuiPaciente);
        etPassword = view.findViewById(R.id.etPassword);
        etPasswordConfirm = view.findViewById(R.id.etPasswordConfirm);
        etTelefono = view.findViewById(R.id.etTelefonoUsuario);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilPasswordConfirm = view.findViewById(R.id.tilPasswordConfirm);

        // --- Cargar datos del ViewModel (si el usuario retrocede) ---
        etNombre.setText(viewModel.nombre.getValue());
        etDui.setText(viewModel.dui.getValue());
        etPassword.setText(viewModel.password.getValue());
        etTelefono.setText(viewModel.telefono.getValue());

        // --- Guardar datos en el ViewModel (mientras el usuario escribe) ---
        etTelefono.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.telefono.setValue(s.toString());
            }
        });


        etNombre.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.nombre.setValue(s.toString());
            }
        });
        etDui.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.dui.setValue(s.toString());
            }
        });
        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.password.setValue(s.toString());
                validatePasswords(); // Validar al instante
            }
        });
        etPasswordConfirm.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswords(); // Validar al instante
            }
        });
    }

    private void validatePasswords() {
        String pass = etPassword.getText().toString();
        String confirmPass = etPasswordConfirm.getText().toString();

        if (pass.length() < 5 && !TextUtils.isEmpty(pass)) {
            tilPassword.setError("Mínimo 5 caracteres");
        } else {
            tilPassword.setError(null);
        }

        if (!pass.equals(confirmPass) && !TextUtils.isEmpty(confirmPass)) {
            tilPasswordConfirm.setError("Las contraseñas no coinciden");
        } else {
            tilPasswordConfirm.setError(null);
        }
    }

    // Helper class para no implementar todos los métodos de TextWatcher
    abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}