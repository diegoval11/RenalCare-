// ========== EstadisticasFragment.java ==========
package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.EstadisticasPaciente;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EstadisticasFragment extends Fragment {
    private static final String ARG_ID = "id_paciente";
    private int id_paciente;
    private ApiServiceDoctor apiService;

    public static EstadisticasFragment newInstance(int id) {
        EstadisticasFragment f = new EstadisticasFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_ID, id);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        id_paciente = getArguments().getInt(ARG_ID);
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle s) {
        return i.inflate(R.layout.fragment_estadisticas, c, false);
    }

    @Override
    public void onViewCreated(View v, Bundle s) {
        super.onViewCreated(v, s);
        Retrofit r = new Retrofit.Builder().baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiService = r.create(ApiServiceDoctor.class);
        cargarEstadisticas(v);
    }

    private void cargarEstadisticas(View v) {
        apiService.getEstadisticas(id_paciente).enqueue(new Callback<EstadisticasPaciente>() {
            @Override
            public void onResponse(retrofit2.Call<EstadisticasPaciente> c, Response<EstadisticasPaciente> r) {
                if (r.isSuccessful() && r.body() != null) {
                    mostrar(v, r.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<EstadisticasPaciente> c, Throwable t) {}
        });
    }

    private void mostrar(View v, EstadisticasPaciente e) {
        ((TextView) v.findViewById(R.id.tv1)).setText("Paciente: " + e.getPaciente().getNombre());
        ((TextView) v.findViewById(R.id.tv2)).setText("Sesiones diálisis: " + e.getTotal_sesiones_dialisis());
        ((TextView) v.findViewById(R.id.tv3)).setText("Medicamentos: " + e.getTotal_medicamentos());
        if (e.getPresion_promedio() != null) {
            ((TextView) v.findViewById(R.id.tv4)).setText(String.format("Presión: %.0f/%.0f",
                    e.getPresion_promedio().presion_sistolica_promedio,
                    e.getPresion_promedio().presion_diastolica_promedio));
        }
        if (e.getPeso_promedio() != null) {
            ((TextView) v.findViewById(R.id.tv5)).setText(String.format("Peso promedio: %.1f kg",
                    e.getPeso_promedio().peso_promedio));
        }

        RecyclerView rv = v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new SignosVitalesAdapter(e.getSignos_vitales()));
    }

    private static class SignosVitalesAdapter extends RecyclerView.Adapter<SignosVitalesAdapter.VH> {
        java.util.List<itca.soft.renalcare.data.models.SignosVitalesDoctor> s;
        SignosVitalesAdapter(java.util.List<itca.soft.renalcare.data.models.SignosVitalesDoctor> l) { s = l; }

        @Override
        public VH onCreateViewHolder(ViewGroup p, int v) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_signo, p, false));
        }

        @Override
        public void onBindViewHolder(VH h, int p) {
            h.tv1.setText(s.get(p).getFecha_registro());
            h.tv2.setText("P: " + s.get(p).getPresion_sistolica() + "/" + s.get(p).getPresion_diastolica());
            h.tv3.setText("FC: " + s.get(p).getFrecuencia_cardiaca());
            h.tv4.setText("Peso: " + s.get(p).getPeso());
        }

        @Override
        public int getItemCount() { return s.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tv1, tv2, tv3, tv4;
            VH(View v) {
                super(v);
                tv1 = v.findViewById(R.id.tv1);
                tv2 = v.findViewById(R.id.tv2);
                tv3 = v.findViewById(R.id.tv3);
                tv4 = v.findViewById(R.id.tv4);
            }
        }
    }
}