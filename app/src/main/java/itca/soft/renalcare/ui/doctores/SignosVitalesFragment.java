// ========== SignosVitalesFragment.java ==========
package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.SignosVitalesDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignosVitalesFragment extends Fragment {
    private static final String ARG_ID = "id_paciente";
    private int id_paciente;
    private RecyclerView rv;
    private List<SignosVitalesDoctor> signos;
    private ApiServiceDoctor apiService;

    public static SignosVitalesFragment newInstance(int id) {
        SignosVitalesFragment f = new SignosVitalesFragment();
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
        return i.inflate(R.layout.fragment_signos_vitales, c, false);
    }

    @Override
    public void onViewCreated(View v, Bundle s) {
        super.onViewCreated(v, s);
        rv = v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        signos = new ArrayList<>();
        rv.setAdapter(new SignosAdapter(signos));

        Retrofit r = new Retrofit.Builder().baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiService = r.create(ApiServiceDoctor.class);

        v.findViewById(R.id.btnAgregar).setOnClickListener(x -> agregarSigno());
        cargar();
    }

    private void cargar() {
        apiService.getSignosVitalesPaciente(id_paciente).enqueue(new Callback<List<SignosVitalesDoctor>>() {
            @Override
            public void onResponse(retrofit2.Call<List<SignosVitalesDoctor>> c, Response<List<SignosVitalesDoctor>> r) {
                if (r.isSuccessful()) {
                    signos.clear();
                    signos.addAll(r.body());
                    rv.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<SignosVitalesDoctor>> c, Throwable t) {}
        });
    }

    private void agregarSigno() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_signos_vitales, null);
        EditText sis = v.findViewById(R.id.etSistolica);
        EditText dia = v.findViewById(R.id.etDiastolica);
        EditText fc = v.findViewById(R.id.etFC);
        EditText peso = v.findViewById(R.id.etPeso);

        new android.app.AlertDialog.Builder(getContext()).setView(v).setPositiveButton("Guardar", (d, w) -> {
            SignosVitalesDoctor signo = new SignosVitalesDoctor(id_paciente,
                    Integer.parseInt(sis.getText().toString()),
                    Integer.parseInt(dia.getText().toString()),
                    Integer.parseInt(fc.getText().toString()),
                    Double.parseDouble(peso.getText().toString()));
            apiService.crearSignosVitales(signo).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(retrofit2.Call<Map<String, Object>> c, Response<Map<String, Object>> r) {
                    if (r.isSuccessful()) cargar();
                }

                @Override
                public void onFailure(retrofit2.Call<Map<String, Object>> c, Throwable t) {}
            });
        }).setNegativeButton("Cancelar", null).show();
    }

    private static class SignosAdapter extends RecyclerView.Adapter<SignosAdapter.VH> {
        List<SignosVitalesDoctor> s;
        SignosAdapter(List<SignosVitalesDoctor> l) { s = l; }

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