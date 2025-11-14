// ========== MedicamentosFragment.java ==========
package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.MedicamentoDoctor;
import itca.soft.renalcare.data.repository.ApiServiceDoctor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MedicamentosFragment extends Fragment {
    private static final String ARG_ID = "id_paciente";
    private int id_paciente;
    private RecyclerView rv;
    private List<MedicamentoDoctor> medicamentos;
    private ApiServiceDoctor apiService;
    private ProgressBar pb;

    public static MedicamentosFragment newInstance(int id) {
        MedicamentosFragment f = new MedicamentosFragment();
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
        return i.inflate(R.layout.fragment_medicamentos, c, false);
    }

    @Override
    public void onViewCreated(View v, Bundle s) {
        super.onViewCreated(v, s);
        rv = v.findViewById(R.id.rv);
        pb = v.findViewById(R.id.pb);
        Button btn = v.findViewById(R.id.btnAgregar);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        medicamentos = new ArrayList<>();
        rv.setAdapter(new MedicadosAdapter(medicamentos));

        Retrofit r = new Retrofit.Builder().baseUrl(ApiServiceDoctor.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiService = r.create(ApiServiceDoctor.class);

        btn.setOnClickListener(x -> mostrarDialog());
        cargar();
    }

    private void cargar() {
        pb.setVisibility(View.VISIBLE);
        apiService.getMedicamentosPaciente(id_paciente).enqueue(new Callback<List<MedicamentoDoctor>>() {
            @Override
            public void onResponse(retrofit2.Call<List<MedicamentoDoctor>> c, Response<List<MedicamentoDoctor>> r) {
                pb.setVisibility(View.GONE);
                if (r.isSuccessful()) {
                    medicamentos.clear();
                    medicamentos.addAll(r.body());
                    rv.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<MedicamentoDoctor>> c, Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void mostrarDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_medicamento, null);
        EditText nombre = v.findViewById(R.id.etNombre);
        EditText dosis = v.findViewById(R.id.etDosis);
        EditText hora = v.findViewById(R.id.etHora);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setView(v).setPositiveButton("Guardar", (d, w) -> {
            apiService.crearMedicamento(new MedicamentoDoctor(id_paciente, nombre.getText().toString(),
                    dosis.getText().toString(), hora.getText().toString(), "", "")).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(retrofit2.Call<Map<String, Object>> c, Response<Map<String, Object>> r) {
                    if (r.isSuccessful()) cargar();
                }

                @Override
                public void onFailure(retrofit2.Call<Map<String, Object>> c, Throwable t) {}
            });
        }).setNegativeButton("Cancelar", null).show();
    }

    private static class MedicadosAdapter extends RecyclerView.Adapter<MedicadosAdapter.VH> {
        List<MedicamentoDoctor> m;
        MedicadosAdapter(List<MedicamentoDoctor> l) { m = l; }

        @Override
        public VH onCreateViewHolder(ViewGroup p, int v) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_medicamento, p, false));
        }

        @Override
        public void onBindViewHolder(VH h, int p) {
            h.tv1.setText(m.get(p).getNombre());
            h.tv2.setText(m.get(p).getDosis());
            h.tv3.setText(m.get(p).getHorario());
        }

        @Override
        public int getItemCount() { return m.size(); }

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