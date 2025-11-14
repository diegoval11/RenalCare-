package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.PacienteDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarPacienteDoctorActivity extends AppCompatActivity {

    private EditText etNombre, etDui, etFechaNacimiento, etPeso;
    private EditText etNivelCreatinina, etSintomas, etObservaciones;
    private EditText etTelefonoEmergencia, etContactoEmergencia;
    private Spinner spGenero, spTipoTratamiento;
    private Button btnGuardar, btnEliminar, btnCancelar;
    private ApiServiceDoctor apiService;
    private int id_paciente, id_doctor;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_paciente_doctor);

        initializeViews();
        setupRetrofit();
        id_paciente = getIntent().getIntExtra("id_paciente", 0);
        id_doctor = getIntent().getIntExtra("id_doctor", 0);

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnEliminar.setOnClickListener(v -> mostrarConfirmacionEliminacion());
        btnCancelar.setOnClickListener(v -> finish());

        cargarPaciente();
    }

    private void initializeViews() {
        etNombre = findViewById(R.id.etNombre);
        etDui = findViewById(R.id.etDui);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etPeso = findViewById(R.id.etPeso);
        etNivelCreatinina = findViewById(R.id.etNivelCreatinina);
        etSintomas = findViewById(R.id.etSintomas);
        etObservaciones = findViewById(R.id.etObservaciones);
        etTelefonoEmergencia = findViewById(R.id.etTelefonoEmergencia);
        etContactoEmergencia = findViewById(R.id.etContactoEmergencia);

        spGenero = findViewById(R.id.spGenero);
        spTipoTratamiento = findViewById(R.id.spTipoTratamiento);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
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

    private void cargarPaciente() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getPacienteById(id_paciente).enqueue(new Callback<PacienteDoctor>() {
            @Override
            public void onResponse(Call<PacienteDoctor> call, Response<PacienteDoctor> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    mostrarDatos(response.body());
                }
            }

            @Override
            public void onFailure(Call<PacienteDoctor> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditarPacienteDoctorActivity.this, "Error al cargar paciente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatos(PacienteDoctor paciente) {
        etNombre.setText(paciente.getNombre());
        etDui.setText(paciente.getDui());
        etFechaNacimiento.setText(paciente.getFecha_nacimiento());
        etPeso.setText(String.valueOf(paciente.getPeso()));
        etNivelCreatinina.setText(String.valueOf(paciente.getNivel_creatinina()));
        etSintomas.setText(paciente.getSintomas());
        etObservaciones.setText(paciente.getObservaciones());
        etTelefonoEmergencia.setText(paciente.getTelefono_emergencia());
        etContactoEmergencia.setText(paciente.getContacto_emergencia());

        // Seleccionar valores en spinners
        seleccionarSpinner(spGenero, paciente.getGenero());
        seleccionarSpinner(spTipoTratamiento, paciente.getTipo_tratamiento());
    }

    private void seleccionarSpinner(Spinner spinner, String valor) {
        if (valor != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            int position = adapter.getPosition(valor);
            spinner.setSelection(position);
        }
    }

    private void guardarCambios() {
        progressBar.setVisibility(View.VISIBLE);

        PacienteDoctor paciente = new PacienteDoctor();
        paciente.setId_paciente(id_paciente);
        paciente.setNombre(etNombre.getText().toString());
        paciente.setDui(etDui.getText().toString());
        paciente.setFecha_nacimiento(etFechaNacimiento.getText().toString());
        paciente.setGenero(spGenero.getSelectedItem().toString());
        paciente.setTipo_tratamiento(spTipoTratamiento.getSelectedItem().toString());
        paciente.setPeso(Double.parseDouble(etPeso.getText().toString().isEmpty() ? "0" : etPeso.getText().toString()));
        paciente.setNivel_creatinina(Double.parseDouble(etNivelCreatinina.getText().toString().isEmpty() ? "0" : etNivelCreatinina.getText().toString()));
        paciente.setSintomas(etSintomas.getText().toString());
        paciente.setObservaciones(etObservaciones.getText().toString());
        paciente.setTelefono_emergencia(etTelefonoEmergencia.getText().toString());
        paciente.setContacto_emergencia(etContactoEmergencia.getText().toString());

        apiService.actualizarPaciente(id_paciente, paciente).enqueue(new Callback<PacienteDoctor>() {
            @Override
            public void onResponse(Call<PacienteDoctor> call, Response<PacienteDoctor> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(EditarPacienteDoctorActivity.this, "Paciente actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PacienteDoctor> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditarPacienteDoctorActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarConfirmacionEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar paciente")
                .setMessage("¿Está seguro de que desea eliminar este paciente?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarPaciente())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarPaciente() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.eliminarPaciente(id_paciente).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(EditarPacienteDoctorActivity.this, "Paciente eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditarPacienteDoctorActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}