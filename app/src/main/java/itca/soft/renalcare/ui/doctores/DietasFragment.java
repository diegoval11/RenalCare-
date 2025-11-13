package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.DietaDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DietasFragment extends Fragment {

    private static final String ARG_ID_PACIENTE = "id_paciente";
    private int id_paciente;
    private RecyclerView rvDietas;
    private Button btnAgregar;
    private EditText etDescripcion, etFechaInicio, etFechaFin;
    private Button btnGuardarDieta, btnCancelar;
    private LinearLayout formularioDieta;
    private DietasAdapter adapter;
    private List<DietaDoctor> dietas;
    private ApiServiceDoctor apiService;
    private ProgressBar progressBar;

    public static DietasFragment newInstance(int id_paciente) {
        DietasFragment fragment = new DietasFragment();
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
        return inflater.inflate(R.layout.fragment_dietas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRetrofit();
        cargarDietas();
    }

    private void initializeViews(View view) {
        rvDietas = view.findViewById(R.id.rvDietas);
        btnAgregar = view.findViewById(R.id.btnAgregar);
        formularioDieta = view.findViewById(R.id.formulardioDieta);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etFechaInicio = view.findViewById(R.id.etFechaInicio);
        etFechaFin = view.findViewById(R.id.etFechaFin);
        btnGuardarDieta = view.findViewById(R.id.btnGuardarDieta);
        btnCancelar = view.findViewById(R.id.btnCancelar);
        progressBar = view.findViewById(R.id.progressBar);

        rvDietas.setLayoutManager(new LinearLayoutManager(getContext()));
        dietas = new ArrayList<>();
        adapter = new DietasAdapter(dietas, this::editarDieta, this::eliminarDieta);
        rvDietas.setAdapter(adapter);

        btnAgregar.setOnClickListener(v -> mostrarFormulario());
        btnGuardarDieta.setOnClickListener(v -> guardarDieta());
        btnCancelar.setOnClickListener(v -> ocultarFormulario());
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiServiceDoctor.class);
    }

    private void cargarDietas() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getDietasPaciente(id_paciente).enqueue(new Callback<List<DietaDoctor>>() {
            @Override
            public void onResponse(Call<List<DietaDoctor>> call, Response<List<DietaDoctor>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    dietas.clear();
                    dietas.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<DietaDoctor>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar dietas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarFormulario() {
        formularioDieta.setVisibility(View.VISIBLE);
        btnAgregar.setVisibility(View.GONE);
    }

    private void ocultarFormulario() {
        formularioDieta.setVisibility(View.GONE);
        btnAgregar.setVisibility(View.VISIBLE);
        etDescripcion.setText("");
        etFechaInicio.setText("");
        etFechaFin.setText("");
    }

    private void guardarDieta() {
        if (etDescripcion.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        DietaDoctor dieta = new DietaDoctor(
                id_paciente,
                etDescripcion.getText().toString(),
                etFechaInicio.getText().toString(),
                etFechaFin.getText().toString()
        );

        apiService.crearDieta(dieta).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Dieta creada", Toast.LENGTH_SHORT).show();
                    ocultarFormulario();
                    cargarDietas();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editarDieta(DietaDoctor dieta) {
        etDescripcion.setText(dieta.getDescripcion());
        etFechaInicio.setText(dieta.getFecha_inicio());
        etFechaFin.setText(dieta.getFecha_fin());
        mostrarFormulario();
    }

    private void eliminarDieta(DietaDoctor dieta) {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Eliminar dieta")
                .setMessage("¿Eliminar esta dieta?")
                .setPositiveButton("Sí", (d, w) -> {
                    apiService.eliminarDieta(dieta.getId_dieta()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                cargarDietas();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Adapter Simple
    private static class DietasAdapter extends RecyclerView.Adapter<DietasAdapter.VH> {
        private List<DietaDoctor> dietas;
        private OnEditListener onEdit;
        private OnDeleteListener onDelete;

        interface OnEditListener { void onEdit(DietaDoctor d); }
        interface OnDeleteListener { void onDelete(DietaDoctor d); }

        DietasAdapter(List<DietaDoctor> d, OnEditListener e, OnDeleteListener del) {
            dietas = d; onEdit = e; onDelete = del;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup p, int v) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_dieta, p, false));
        }

        @Override
        public void onBindViewHolder(VH h, int p) {
            DietaDoctor d = dietas.get(p);
            h.tvDescripcion.setText(d.getDescripcion());
            h.tvFechas.setText(d.getFecha_inicio() + " a " + d.getFecha_fin());
            h.btnEditar.setOnClickListener(v -> onEdit.onEdit(d));
            h.btnEliminar.setOnClickListener(v -> onDelete.onDelete(d));
        }

        @Override
        public int getItemCount() { return dietas.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDescripcion, tvFechas;
            Button btnEditar, btnEliminar;

            VH(View v) {
                super(v);
                tvDescripcion = v.findViewById(R.id.tvDescripcion);
                tvFechas = v.findViewById(R.id.tvFechas);
                btnEditar = v.findViewById(R.id.btnEditar);
                btnEliminar = v.findViewById(R.id.btnEliminar);
            }
        }
    }
}