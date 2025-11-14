// ========== DialisisFragment.java ==========
package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.DialisisDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DialisisFragment extends Fragment {
    private static final String ARG_ID = "id_paciente";
    private int id_paciente;
    private RecyclerView rv;
    private List<DialisisDoctor> dialisis;
    private ApiServiceDoctor apiService;

    public static DialisisFragment newInstance(int id) {
        DialisisFragment f = new DialisisFragment();
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
        return i.inflate(R.layout.fragment_dialisis, c, false);
    }

    @Override
    public void onViewCreated(View v, Bundle s) {
        super.onViewCreated(v, s);
        rv = v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        dialisis = new ArrayList<>();
        rv.setAdapter(new DialisisAdapter(dialisis));

        Retrofit r = new Retrofit.Builder().baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiService = r.create(ApiServiceDoctor.class);

        v.findViewById(R.id.btnAgregar).setOnClickListener(x -> agregarDialisis());
        cargar();
    }

    private void cargar() {
        apiService.getDialisisPaciente(id_paciente).enqueue(new Callback<List<DialisisDoctor>>() {
            @Override
            public void onResponse(retrofit2.Call<List<DialisisDoctor>> c, Response<List<DialisisDoctor>> r) {
                if (r.isSuccessful()) {
                    dialisis.clear();
                    dialisis.addAll(r.body());
                    rv.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<DialisisDoctor>> c, Throwable t) {}
        });
    }

    private void agregarDialisis() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_dialisis, null);
        EditText tipo = v.findViewById(R.id.etTipo);
        EditText fecha = v.findViewById(R.id.etFecha);
        EditText hora = v.findViewById(R.id.etHora);

        new android.app.AlertDialog.Builder(getContext()).setView(v).setPositiveButton("Guardar", (d, w) -> {
            apiService.crearDialisis(new DialisisDoctor(id_paciente, tipo.getText().toString(),
                    fecha.getText().toString(), hora.getText().toString(), "")).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(retrofit2.Call<Map<String, Object>> c, Response<Map<String, Object>> r) {
                    if (r.isSuccessful()) cargar();
                }

                @Override
                public void onFailure(retrofit2.Call<Map<String, Object>> c, Throwable t) {}
            });
        }).setNegativeButton("Cancelar", null).show();
    }

    private static class DialisisAdapter extends RecyclerView.Adapter<DialisisAdapter.VH> {
        List<DialisisDoctor> d;
        DialisisAdapter(List<DialisisDoctor> l) { d = l; }

        @Override
        public VH onCreateViewHolder(ViewGroup p, int v) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_dialisis, p, false));
        }

        @Override
        public void onBindViewHolder(VH h, int p) {
            h.tv1.setText(d.get(p).getTipo());
            h.tv2.setText(d.get(p).getFecha());
            h.tv3.setText(d.get(p).getHora());
        }

        @Override
        public int getItemCount() { return d.size(); }

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