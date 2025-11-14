package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.PacienteDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InformacionPacienteFragment extends Fragment {

    private static final String ARG_ID_PACIENTE = "id_paciente";
    private int id_paciente;
    private EditText etNombre, etDui, etFechaNac, etGenero, etTratamiento, etPeso, etCreatinina;
    private EditText etSintomas, etObservaciones, etTelefono, etContacto;
    private Button btnGuardar, btnEditar;
    private ProgressBar progressBar;
    private ApiServiceDoctor apiService;
    private boolean editando = false;

    public static InformacionPacienteFragment newInstance(int id_paciente) {
        InformacionPacienteFragment fragment = new InformacionPacienteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_PACIENTE, id_paciente);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_paciente = getArguments().getInt(ARG_ID_PACIENTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_informacion_paciente, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRetrofit();
        cargarPaciente();
    }

    private void initializeViews(View view) {
        etNombre = view.findViewById(R.id.etNombre);
        etDui = view.findViewById(R.id.etDui);
        etFechaNac = view.findViewById(R.id.etFechaNacimiento);
        etGenero = view.findViewById(R.id.etGenero);
        etTratamiento = view.findViewById(R.id.etTratamiento);
        etPeso = view.findViewById(R.id.etPeso);
        etCreatinina = view.findViewById(R.id.etCreatinina);
        etSintomas = view.findViewById(R.id.etSintomas);
        etObservaciones = view.findViewById(R.id.etObservaciones);
        etTelefono = view.findViewById(R.id.etTelefono);
        etContacto = view.findViewById(R.id.etContacto);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnEditar = view.findViewById(R.id.btnEditar);
        progressBar = view.findViewById(R.id.progressBar);

        setEditingMode(false);

        btnEditar.setOnClickListener(v -> {
            editando = !editando;
            setEditingMode(editando);
        });

        btnGuardar.setOnClickListener(v -> guardarCambios());
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
                Toast.makeText(getContext(), "Error al cargar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatos(PacienteDoctor p) {
        etNombre.setText(p.getNombre());
        etDui.setText(p.getDui());
        etFechaNac.setText(p.getFecha_nacimiento());
        etGenero.setText(p.getGenero());
        etTratamiento.setText(p.getTipo_tratamiento());
        etPeso.setText(String.valueOf(p.getPeso()));
        etCreatinina.setText(String.valueOf(p.getNivel_creatinina()));
        etSintomas.setText(p.getSintomas());
        etObservaciones.setText(p.getObservaciones());
        etTelefono.setText(p.getTelefono_emergencia());
        etContacto.setText(p.getContacto_emergencia());
    }

    private void setEditingMode(boolean editing) {
        etNombre.setEnabled(editing);
        etDui.setEnabled(editing);
        etFechaNac.setEnabled(editing);
        etGenero.setEnabled(editing);
        etTratamiento.setEnabled(editing);
        etPeso.setEnabled(editing);
        etCreatinina.setEnabled(editing);
        etSintomas.setEnabled(editing);
        etObservaciones.setEnabled(editing);
        etTelefono.setEnabled(editing);
        etContacto.setEnabled(editing);

        btnGuardar.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnEditar.setText(editing ? "Cancelar" : "Editar");
    }

    private void guardarCambios() {
        progressBar.setVisibility(View.VISIBLE);

        PacienteDoctor paciente = new PacienteDoctor();
        paciente.setNombre(etNombre.getText().toString());
        paciente.setDui(etDui.getText().toString());
        paciente.setFecha_nacimiento(etFechaNac.getText().toString());
        paciente.setGenero(etGenero.getText().toString());
        paciente.setTipo_tratamiento(etTratamiento.getText().toString());
        paciente.setPeso(Double.parseDouble(etPeso.getText().toString()));
        paciente.setNivel_creatinina(Double.parseDouble(etCreatinina.getText().toString()));
        paciente.setSintomas(etSintomas.getText().toString());
        paciente.setObservaciones(etObservaciones.getText().toString());
        paciente.setTelefono_emergencia(etTelefono.getText().toString());
        paciente.setContacto_emergencia(etContacto.getText().toString());

        apiService.actualizarPaciente(id_paciente, paciente).enqueue(new Callback<PacienteDoctor>() {
            @Override
            public void onResponse(Call<PacienteDoctor> call, Response<PacienteDoctor> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Paciente actualizado", Toast.LENGTH_SHORT).show();
                    editando = false;
                    setEditingMode(false);
                }
            }

            @Override
            public void onFailure(Call<PacienteDoctor> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}