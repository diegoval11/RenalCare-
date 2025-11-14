package itca.soft.renalcare.ui.doctores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import itca.soft.renalcare.R;
import itca.soft.renalcare.adapters.PacienteDoctorAdapter;
import itca.soft.renalcare.data.models.PacienteDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class GestionPacientesDoctorActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPacientes;
    private PacienteDoctorAdapter adapter;
    private List<PacienteDoctor> pacientes;
    private ApiServiceDoctor apiService;
    private int id_doctor;
    private FloatingActionButton btnCrearPaciente;
    private ProgressBar progressBar;
    private EditText etBuscar;
    private TextView tvTotalPacientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_pacientes_doctor);

        initializeViews();
        setupRetrofit();
        id_doctor = getIntent().getIntExtra("id_doctor", 1); // Default: 1

        if (id_doctor > 0) {
            cargarPacientes();
        }
    }

    private void initializeViews() {
        recyclerViewPacientes = findViewById(R.id.recyclerViewPacientes);
        btnCrearPaciente = findViewById(R.id.btnCrearPaciente);
        progressBar = findViewById(R.id.progressBar);
        etBuscar = findViewById(R.id.etBuscar);
        tvTotalPacientes = findViewById(R.id.tvTotalPacientes);

        recyclerViewPacientes.setLayoutManager(new LinearLayoutManager(this));
        pacientes = new ArrayList<>();
        adapter = new PacienteDoctorAdapter(pacientes, this::onPacienteClick, this::onPacienteEliminar);
        recyclerViewPacientes.setAdapter(adapter);

        btnCrearPaciente.setOnClickListener(v -> abrirCrearPaciente());

        etBuscar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPacientes(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiServiceDoctor.class);
    }

    private void cargarPacientes() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getPacientesByDoctor(id_doctor).enqueue(new Callback<List<PacienteDoctor>>() {
            @Override
            public void onResponse(Call<List<PacienteDoctor>> call, Response<List<PacienteDoctor>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    pacientes.clear();
                    pacientes.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    
                    // Actualizar contador de pacientes
                    if (tvTotalPacientes != null) {
                        tvTotalPacientes.setText(String.valueOf(pacientes.size()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PacienteDoctor>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GestionPacientesDoctorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarPacientes(String texto) {
        List<PacienteDoctor> listafiltrada = new ArrayList<>();
        for (PacienteDoctor p : pacientes) {
            if (p.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                    p.getDui().contains(texto)) {
                listafiltrada.add(p);
            }
        }
        adapter.setData(listafiltrada);
    }

    private void abrirCrearPaciente() {
        startActivity(new Intent(this, CrearPacienteDoctorActivity.class)
                .putExtra("id_doctor", id_doctor)
        );
    }

    private void onPacienteClick(PacienteDoctor paciente) {
        startActivity(new Intent(this, DetallesPacienteDoctorActivity.class)
                .putExtra("id_paciente", paciente.getId_paciente())
                .putExtra("id_doctor", id_doctor)
        );
    }

    private void onPacienteEliminar(PacienteDoctor paciente) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar paciente")
                .setMessage("¿Eliminar a " + paciente.getNombre() + "?")
                .setPositiveButton("Sí", (d, w) -> eliminarPaciente(paciente.getId_paciente()))
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarPaciente(int id_paciente) {
        apiService.eliminarPaciente(id_paciente).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GestionPacientesDoctorActivity.this, "Paciente eliminado", Toast.LENGTH_SHORT).show();
                    cargarPacientes();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(GestionPacientesDoctorActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPacientes();
    }
}
