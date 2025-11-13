package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.EstadisticasPaciente;
import itca.soft.renalcare.data.models.SignosVitalesDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.Toast;

public class EstadisticasPacienteActivity extends AppCompatActivity {

    private TextView tvNombrePaciente, tvTipoTratamiento, tvPeso, tvCreatinina;
    private TextView tvTotalDialisis, tvTotalMedicamentos;
    private TextView tvPresionSistolica, tvPresionDiastolica, tvFrecuenciaCardiaca;
    private TextView tvPesoPromedio;
    private RecyclerView rvSignosVitales;
    private ProgressBar progressBar;
    private ApiServiceDoctor apiService;
    private int id_paciente;
    private SignosVitalesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_paciente);

        initializeViews();
        setupRetrofit();
        id_paciente = getIntent().getIntExtra("id_paciente", 2);

        if (id_paciente > 0) {
            cargarEstadisticas();
        }
    }

    private void initializeViews() {
        tvNombrePaciente = findViewById(R.id.tvNombrePaciente);
        tvTipoTratamiento = findViewById(R.id.tvTipoTratamiento);
        tvPeso = findViewById(R.id.tvPeso);
        tvCreatinina = findViewById(R.id.tvCreatinina);
        tvTotalDialisis = findViewById(R.id.tvTotalDialisis);
        tvTotalMedicamentos = findViewById(R.id.tvTotalMedicamentos);
        tvPresionSistolica = findViewById(R.id.tvPresionSistolica);
        tvPresionDiastolica = findViewById(R.id.tvPresionDiastolica);
        tvFrecuenciaCardiaca = findViewById(R.id.tvFrecuenciaCardiaca);
        tvPesoPromedio = findViewById(R.id.tvPesoPromedio);
        rvSignosVitales = findViewById(R.id.rvSignosVitales);
        progressBar = findViewById(R.id.progressBar);

        rvSignosVitales.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SignosVitalesAdapter();
        rvSignosVitales.setAdapter(adapter);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiServiceDoctor.class);
    }

    private void cargarEstadisticas() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getEstadisticas(id_paciente).enqueue(new Callback<EstadisticasPaciente>() {
            @Override
            public void onResponse(Call<EstadisticasPaciente> call, Response<EstadisticasPaciente> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    mostrarEstadisticas(response.body());
                }
            }

            @Override
            public void onFailure(Call<EstadisticasPaciente> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EstadisticasPacienteActivity.this, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarEstadisticas(EstadisticasPaciente estadisticas) {
        // Información del paciente
        tvNombrePaciente.setText(estadisticas.getPaciente().getNombre());
        tvTipoTratamiento.setText("Tratamiento: " + estadisticas.getPaciente().getTipo_tratamiento());
        tvPeso.setText("Peso actual: " + estadisticas.getPaciente().getPeso() + " kg");
        tvCreatinina.setText("Creatinina: " + estadisticas.getPaciente().getNivel_creatinina());

        // Totales
        tvTotalDialisis.setText("Total sesiones diálisis: " + estadisticas.getTotal_sesiones_dialisis());
        tvTotalMedicamentos.setText("Medicamentos activos: " + estadisticas.getTotal_medicamentos());

        // Promedios
        if (estadisticas.getPresion_promedio() != null) {
            tvPresionSistolica.setText("Presión sistólica promedio: " +
                    String.format("%.1f", estadisticas.getPresion_promedio().presion_sistolica_promedio));
            tvPresionDiastolica.setText("Presión diastólica promedio: " +
                    String.format("%.1f", estadisticas.getPresion_promedio().presion_diastolica_promedio));
        }

        if (estadisticas.getPeso_promedio() != null) {
            tvPesoPromedio.setText("Peso promedio (30 días): " +
                    String.format("%.1f", estadisticas.getPeso_promedio().peso_promedio) + " kg");
        }

        // Lista de signos vitales
        if (estadisticas.getSignos_vitales() != null && !estadisticas.getSignos_vitales().isEmpty()) {
            adapter.setSignosVitales(estadisticas.getSignos_vitales());
        }
    }

    // Adapter para RecyclerView de signos vitales
    private class SignosVitalesAdapter extends RecyclerView.Adapter<SignosVitalesAdapter.ViewHolder> {
        private java.util.List<SignosVitalesDoctor> signosVitales = new java.util.ArrayList<>();

        void setSignosVitales(java.util.List<SignosVitalesDoctor> lista) {
            this.signosVitales = lista;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_signo_vital, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SignosVitalesDoctor signo = signosVitales.get(position);
            holder.tvFecha.setText("Fecha: " + signo.getFecha_registro());
            holder.tvPresion.setText("P: " + signo.getPresion_sistolica() + "/" + signo.getPresion_diastolica());
            holder.tvFC.setText("FC: " + signo.getFrecuencia_cardiaca() + " bpm");
            holder.tvPeso.setText("Peso: " + signo.getPeso() + " kg");
        }

        @Override
        public int getItemCount() {
            return signosVitales.size();
        }

        class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            TextView tvFecha, tvPresion, tvFC, tvPeso;

            ViewHolder(android.view.View itemView) {
                super(itemView);
                tvFecha = itemView.findViewById(R.id.tvFecha);
                tvPresion = itemView.findViewById(R.id.tvPresion);
                tvFC = itemView.findViewById(R.id.tvFC);
                tvPeso = itemView.findViewById(R.id.tvPeso);
            }
        }
    }
}