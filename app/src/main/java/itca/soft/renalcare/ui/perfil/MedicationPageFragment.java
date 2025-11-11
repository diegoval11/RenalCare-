package itca.soft.renalcare.ui.perfil;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.MedicationItem;
import itca.soft.renalcare.data.models.TodayReminderStatus;

public class MedicationPageFragment extends Fragment {

    private static final String ARG_MEDICATIONS = "arg_medications";
    private List<MedicationItem> medicationPageList;
    private Map<Integer, TodayReminderStatus> statusMap;
    private OnMedicationTakeListener listener;

    // --- ▼▼▼ CAMBIO 1: El adaptador ahora es una variable de clase ▼▼▼ ---
    private MedicationAdapter adapter;
    // --- ▲▲▲ FIN CAMBIO 1 ▲▲▲ ---


    public static MedicationPageFragment newInstance(List<MedicationItem> medicationPage, Map<Integer, TodayReminderStatus> statusMap) {
        MedicationPageFragment fragment = new MedicationPageFragment();
        Bundle args = new Bundle();

        // (Esto es de una corrección anterior, usando Parcelable)
        args.putParcelableArrayList(ARG_MEDICATIONS, (ArrayList<MedicationItem>) medicationPage);

        fragment.setArguments(args);
        fragment.statusMap = statusMap;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnMedicationTakeListener) {
            listener = (OnMedicationTakeListener) getParentFragment();
        } else {
            throw new RuntimeException("El Fragment padre debe implementar OnMedicationTakeListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            medicationPageList = getArguments().getParcelableArrayList(ARG_MEDICATIONS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medication_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_medication_page);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- ▼▼▼ CAMBIO 2: Inicializamos la variable de clase ▼▼▼ ---
        adapter = new MedicationAdapter(medicationPageList, statusMap, listener);
        // --- ▲▲▲ FIN CAMBIO 2 ▲▲▲ ---
        recyclerView.setAdapter(adapter);
    }

    public void notifyDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    // --- ▼▼▼ CAMBIO 3: AÑADIR ESTE MÉTODO COMPLETO ▼▼▼ ---
    @Override
    public void onResume() {
        super.onResume();

        // CADA VEZ que el fragmento/página vuelve a estar visible
        // (esto incluye cuando el PerfilFragment llama a notifyDataSetChanged() en el Pager),
        // forzamos al adaptador INTERNO a redibujarse.
        // El adaptador leerá el 'statusMap' actualizado (que se actualizó en PerfilFragment)
        // y aplicará el tachado y el gris.
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    // --- ▲▲▲ FIN CAMBIO 3 ▲▲▲ ---
}