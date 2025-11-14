// ========== RecordatoriosFragment.java ==========
package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.RecordatorioDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordatoriosFragment extends Fragment {
    private static final String ARG_ID = "id_paciente";
    private int id_paciente;
    private RecyclerView rv;
    private List<RecordatorioDoctor> recordatorios;
    private ApiServiceDoctor apiService;

    public static RecordatoriosFragment newInstance(int id) {
        RecordatoriosFragment f = new RecordatoriosFragment();
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
        return i.inflate(R.layout.fragment_recordatorios, c, false);
    }

    @Override
    public void onViewCreated(View v, Bundle s) {
        super.onViewCreated(v, s);
        rv = v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        recordatorios = new ArrayList<>();
        rv.setAdapter(new RecordatoriosAdapter(recordatorios));

        Retrofit r = new Retrofit.Builder().baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiService = r.create(ApiServiceDoctor.class);

        v.findViewById(R.id.btnAgregar).setOnClickListener(x -> agregarRecordatorio());
        cargar();
    }

    private void cargar() {
        apiService.getRecordatoriosPaciente(id_paciente).enqueue(new Callback<List<RecordatorioDoctor>>() {
            @Override
            public void onResponse(retrofit2.Call<List<RecordatorioDoctor>> c, Response<List<RecordatorioDoctor>> r) {
                if (r.isSuccessful()) {
                    recordatorios.clear();
                    recordatorios.addAll(r.body());
                    rv.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<RecordatorioDoctor>> c, Throwable t) {}
        });
    }

    private void agregarRecordatorio() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_recordatorio, null);
        EditText titulo = v.findViewById(R.id.etTitulo);
        EditText desc = v.findViewById(R.id.etDesc);
        EditText fecha = v.findViewById(R.id.etFecha);
        Spinner tipo = v.findViewById(R.id.spTipo);

        new android.app.AlertDialog.Builder(getContext()).setView(v).setPositiveButton("Guardar", (d, w) -> {
            RecordatorioDoctor rec = new RecordatorioDoctor(id_paciente, titulo.getText().toString(),
                    desc.getText().toString(), fecha.getText().toString(), tipo.getSelectedItem().toString());
            apiService.crearRecordatorio(rec).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(retrofit2.Call<Map<String, Object>> c, Response<Map<String, Object>> r) {
                    if (r.isSuccessful()) cargar();
                }

                @Override
                public void onFailure(retrofit2.Call<Map<String, Object>> c, Throwable t) {}
            });
        }).setNegativeButton("Cancelar", null).show();
    }

    private static class RecordatoriosAdapter extends RecyclerView.Adapter<RecordatoriosAdapter.VH> {
        List<RecordatorioDoctor> r;
        RecordatoriosAdapter(List<RecordatorioDoctor> l) { r = l; }

        @Override
        public VH onCreateViewHolder(ViewGroup p, int v) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_recordatorio, p, false));
        }

        @Override
        public void onBindViewHolder(VH h, int p) {
            h.tv1.setText(r.get(p).getTitulo());
            h.tv2.setText(r.get(p).getDescripcion());
            h.tv3.setText(r.get(p).getFecha_hora());
        }

        @Override
        public int getItemCount() { return r.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tv1, tv2, tv3;
            VH(View v) {
                super(v);
                tv1 = v.findViewById(R.id.tv1);
                tv2 = v.findViewById(R.id.tv2);
                tv3 = v.findViewById(R.id.tv3);
            }
        }
    }
}