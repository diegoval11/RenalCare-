package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.PacienteDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearPacienteDoctorActivity extends AppCompatActivity {

    private EditText etNombre, etDui, etContrasena, etFechaNacimiento, etPeso;
    private EditText etNivelCreatinina, etSintomas, etObservaciones;
    private EditText etTelefonoEmergencia, etContactoEmergencia;
    private Spinner spGenero, spTipoTratamiento;
    private Button btnGuardar, btnCancelar;
    private ApiServiceDoctor apiService;
    private int id_doctor;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_paciente_doctor);

        initializeViews();
        setupRetrofit();
        id_doctor = getIntent().getIntExtra("id_doctor", 0);

        btnGuardar.setOnClickListener(v -> guardarPaciente());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        // EditTexts
        etNombre = findViewById(R.id.etNombre);
        etDui = findViewById(R.id.etDui);
        etContrasena = findViewById(R.id.etContrasena);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etPeso = findViewById(R.id.etPeso);
        etNivelCreatinina = findViewById(R.id.etNivelCreatinina);
        etSintomas = findViewById(R.id.etSintomas);
        etObservaciones = findViewById(R.id.etObservaciones);
        etTelefonoEmergencia = findViewById(R.id.etTelefonoEmergencia);
        etContactoEmergencia = findViewById(R.id.etContactoEmergencia);

        // Spinners
        spGenero = findViewById(R.id.spGenero);
        spTipoTratamiento = findViewById(R.id.spTipoTratamiento);

        // Botones
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        progressBar = findViewById(R.id.progressBar);

        // Configurar adapters para spinners
        ArrayAdapter<CharSequence> adapterGenero = ArrayAdapter.createFromResource(this,
                R.array.genero_array, android.R.layout.simple_spinner_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenero.setAdapter(adapterGenero);

        ArrayAdapter<CharSequence> adapterTratamiento = ArrayAdapter.createFromResource(this,
                R.array.tipo_tratamiento_array, android.R.layout.simple_spinner_item);
        adapterTratamiento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoTratamiento.setAdapter(adapterTratamiento);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiServiceDoctor.class);
    }

    private void guardarPaciente() {
        // Validar campos
        if (etNombre.getText().toString().isEmpty() ||
                etDui.getText().toString().isEmpty() ||
                etContrasena.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Crear objeto con todos los datos incluyendo contraseña
        Map<String, Object> pacienteData = new HashMap<>();
        pacienteData.put("nombre", etNombre.getText().toString());
        pacienteData.put("dui", etDui.getText().toString());
        pacienteData.put("contrasena_hash", etContrasena.getText().toString());
        pacienteData.put("tipo", "paciente");
        pacienteData.put("fecha_nacimiento", etFechaNacimiento.getText().toString());
        pacienteData.put("genero", spGenero.getSelectedItem().toString());
        pacienteData.put("tipo_tratamiento", spTipoTratamiento.getSelectedItem().toString());
        pacienteData.put("peso", etPeso.getText().toString().isEmpty() ? 0 : Double.parseDouble(etPeso.getText().toString()));
        pacienteData.put("nivel_creatinina", etNivelCreatinina.getText().toString().isEmpty() ? 0 : Double.parseDouble(etNivelCreatinina.getText().toString()));
        pacienteData.put("sintomas", etSintomas.getText().toString());
        pacienteData.put("observaciones", etObservaciones.getText().toString());
        pacienteData.put("telefono_emergencia", etTelefonoEmergencia.getText().toString());
        pacienteData.put("contacto_emergencia", etContactoEmergencia.getText().toString());
        pacienteData.put("id_doctor", id_doctor);

        // Llamada a la API con los datos del paciente y el doctor
        apiService.crearPacienteDesdeDoctor(pacienteData).enqueue(new Callback<PacienteDoctor>() {
            @Override
            public void onResponse(Call<PacienteDoctor> call, Response<PacienteDoctor> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(CrearPacienteDoctorActivity.this, "Paciente creado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrearPacienteDoctorActivity.this, "Error al crear paciente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PacienteDoctor> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CrearPacienteDoctorActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}