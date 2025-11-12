package itca.soft.renalcare.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import itca.soft.renalcare.R;

public class SignupPatientActivity extends AppCompatActivity {



    private static final String URL_REGISTRO_WIZARD = "http://192.168.1.12/wsrenalcare/signup_patient_wizard.php"; // ¡NUEVO ENDPOINT!
    private static final int NUM_STEPS = 5;
    private static final String TAG = "SignupActivity";
    private ViewPager2 viewPager;
    private Button btnBack, btnNext;
    private LinearProgressIndicator progressIndicator;

    private SignupPatientViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_patient);

        // --- 1. Inicializar el ViewModel ---
        viewModel = new ViewModelProvider(this).get(SignupPatientViewModel.class);

        // --- 2. Encontrar Vistas de la Activity ---
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        progressIndicator = findViewById(R.id.progressIndicator);

        // --- 3. Configurar el ViewPager ---
        SignupWizardAdapter adapter = new SignupWizardAdapter(this);
        viewPager.setAdapter(adapter);

        // ¡Deshabilitar el deslizamiento manual! Forzamos a usar los botones.
        viewPager.setUserInputEnabled(false);

        // ¡Aplicar la animación elegante!
        viewPager.setPageTransformer(new ZoomFadePageTransformer());

        // --- 4. Configurar Listeners de Navegación ---
        btnNext.setOnClickListener(v -> handleNextClick());
        btnBack.setOnClickListener(v -> handleBackClick());

        // Actualizar UI (botones/progreso) cuando la página cambie
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateNavigationUI(position);
            }
        });

        // Iniciar la UI
        updateNavigationUI(0);
    }

    private void handleBackClick() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem > 0) {
            viewPager.setCurrentItem(currentItem - 1);
        }
    }

    private void handleNextClick() {
        int currentItem = viewPager.getCurrentItem();

        // Validar el paso actual ANTES de avanzar
        if (!validateStep(currentItem)) {
            return; // Detener si la validación falla
        }

        if (currentItem < NUM_STEPS - 1) {
            // Avanzar al siguiente paso
            viewPager.setCurrentItem(currentItem + 1);
        } else {
            // Estamos en el último paso (Revisión), el botón es "Registrar"
            intentarRegistroPaciente();
        }
    }

    /**
     * Valida los datos del ViewModel para el paso actual.
     */
    private boolean validateStep(int position) {
        switch (position) {
            case 0: // Paso 1: Cuenta
                String nombre = viewModel.nombre.getValue();
                String dui = viewModel.dui.getValue();
                String pass = viewModel.password.getValue();
                // (Necesitarás un campo 'confirmPassword' en el Fragment 1, aquí asumimos que ya está validado)
                if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(dui) || TextUtils.isEmpty(pass)) {
                    Toast.makeText(this, "Nombre, DUI y Contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (pass.length() < 5) {
                    Toast.makeText(this, "La contraseña debe tener al menos 5 caracteres", Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Aquí también deberías validar que "pass == confirmPass" (lógica en el Fragment 1)
                return true;

            case 1: // Paso 2: Personal
                // Todos los campos son opcionales o se validan con picker (fecha/género)
                return true;

            case 2: // Paso 3: Médico
                // Todos los campos tienen opción "No sé"
                return true;

            case 3: // Paso 4: Tratamiento
                // Todos los campos son opcionales
                return true;

            case 4: // Paso 5: Revisión
                return true;
        }
        return false;
    }


    /**
     * Actualiza la visibilidad de los botones y el texto del botón "Next".
     */
    private void updateNavigationUI(int position) {
        // Progreso (20% por paso, 100 / 5 = 20)
        progressIndicator.setProgress((position + 1) * (100 / NUM_STEPS), true);

        // Botón "Atrás"
        btnBack.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

        // Botón "Siguiente"
        if (position == NUM_STEPS - 1) {
            btnNext.setText(R.string.action_register_final); // Ej: "Confirmar y Registrar"
        } else {
            btnNext.setText(R.string.action_next); // Ej: "Siguiente"
        }
    }

    /**
     * Construye el JSON y lo envía al servidor.
     */
    private void intentarRegistroPaciente() {
        // --- 1. Construir el objeto JSON principal ---
        JSONObject data = new JSONObject();
        try {
            // --- Objeto Usuario (Paso 1) ---
            JSONObject usuario = new JSONObject();
            usuario.put("nombre", viewModel.nombre.getValue());
            usuario.put("dui", viewModel.dui.getValue());
            usuario.put("password", viewModel.password.getValue()); // El backend hará el hash
            usuario.put("telefono", viewModel.telefono.getValue());
            data.put("usuario", usuario);

            // --- Objeto Paciente (Paso 2 y 3) ---
            JSONObject paciente = new JSONObject();
            paciente.put("fecha_nacimiento", viewModel.fechaNacimiento.getValue());
            paciente.put("genero", viewModel.genero.getValue());
            paciente.put("contacto_emergencia", viewModel.contactoNombre.getValue());
            paciente.put("telefono_emergencia", viewModel.contactoTelefono.getValue());

            paciente.put("condicion_renal", viewModel.condicionRenal.getValue());
            paciente.put("tipo_tratamiento", viewModel.tipoTratamiento.getValue());

            if (Boolean.FALSE.equals(viewModel.pesoOmitido.getValue())) {
                paciente.put("peso", viewModel.peso.getValue());
            }
            if (Boolean.FALSE.equals(viewModel.creatininaOmitida.getValue())) {
                paciente.put("nivel_creatinina", viewModel.creatinina.getValue());
            }
            data.put("paciente", paciente);

            // --- Array de Medicamentos (Paso 4) ---
            JSONArray medicamentosArray = new JSONArray();
            List<Medicamento> medList = viewModel.medicamentos.getValue();
            if (Boolean.FALSE.equals(viewModel.medicamentosOmitidos.getValue()) && medList != null) {
                for (Medicamento med : medList) {
                    JSONObject medJson = new JSONObject();
                    medJson.put("nombre", med.nombre);
                    medJson.put("dosis", med.dosis);
                    medJson.put("horario", med.horario); // Ej: "08:30 AM"
                    medicamentosArray.put(medJson);
                }
            }
            data.put("medicamentos", medicamentosArray);

            // --- Array de Dieta (Paso 4) (La "Traducción") ---
            JSONArray dietaArray = new JSONArray();
            Set<String> dietaSet = viewModel.dieta.getValue();
            if (dietaSet != null) {
                for (String restriccion : dietaSet) {
                    dietaArray.put(restriccion); // Ej: "Bajo sodio", "Bajo potasio"
                }
            }
            data.put("dieta", dietaArray);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al construir los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 2. Configurar Parámetros para loopj ---
        RequestParams params = new RequestParams();
        // Enviamos completo el JSON como un solo parámetro de texto
        params.put("signup_data", data.toString());

        // (Mostrar un diálogo de "Cargando...")

        // --- 3. Enviar al Web Service ---
        AsyncHttpClient client = new AsyncHttpClient();

        Log.d(TAG, "Enviando datos a " + URL_REGISTRO_WIZARD);
        Log.d(TAG, "Parámetros: " + params.toString());

        client.post(URL_REGISTRO_WIZARD, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // (Ocultar "Cargando...")
                try {
                    boolean exito = response.getBoolean("exito");
                    if (exito) {
                        Toast.makeText(SignupPatientActivity.this, "Paciente registrado exitosamente.", Toast.LENGTH_LONG).show();
                        // Volver al Login
                        Intent intent = new Intent(SignupPatientActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String error = response.getString("error");
                        Toast.makeText(SignupPatientActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(SignupPatientActivity.this, "Respuesta inválida del servidor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // (Ocultar "Cargando...")
                String msg = "Error de red (" + statusCode + "): " + throwable.getMessage();
                Toast.makeText(SignupPatientActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            // ... (otros métodos onFailure)
        });
    }

    // Prevenir que el botón "Atrás" del sistema cierre la app (lo hace retroceder en el wizard)
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // Si está en el primer paso, dejar que el sistema maneje el "Atrás" (cerrar)
            super.onBackPressed();
        } else {
            // Si no, retroceder un paso
            handleBackClick();
        }
    }
}