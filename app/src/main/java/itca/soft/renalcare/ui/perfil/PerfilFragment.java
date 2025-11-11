package itca.soft.renalcare.ui.perfil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

// (El resto de tus imports)
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.MedicationItem;
import itca.soft.renalcare.data.models.ReminderItem;
import itca.soft.renalcare.data.models.TodayReminderStatus;
import itca.soft.renalcare.data.network.RecordatorioApiService;
import itca.soft.renalcare.data.network.UpdateStatusBody;
import itca.soft.renalcare.notifications.AlarmScheduler;
import itca.soft.renalcare.data.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment implements OnMedicationTakeListener {

    private static final String TAG = "PerfilFragment";

    private ViewPager2 viewPagerMedications;
    private TabLayout tabLayoutIndicator;
    private MedicationPagerAdapter pagerAdapter;

    private ImageView iconMedNotification;
    private RecordatorioApiService apiService;
    private List<MedicationItem> medicationList = new ArrayList<>();

    private Map<Integer, TodayReminderStatus> statusMap = new HashMap<>();

    private int idPacienteLogueado = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();

        iconMedNotification = view.findViewById(R.id.icon_med_notification);
        if (iconMedNotification != null) {
            iconMedNotification.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MedicationManagementActivity.class);
                startActivity(intent);
            });
        }

        viewPagerMedications = view.findViewById(R.id.viewpager_medications);
        tabLayoutIndicator = view.findViewById(R.id.tablayout_med_indicator);
        setupViewPager();
        loadMedicationData();
        setupGoalBars(view);
    }

    private void setupViewPager() {
        pagerAdapter = new MedicationPagerAdapter(this, statusMap);
        viewPagerMedications.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayoutIndicator, viewPagerMedications,
                (tab, position) -> { }
        ).attach();
    }

    private void loadMedicationData() {
        medicationList.clear();
        statusMap.clear();

        apiService.getMedicamentos(idPacienteLogueado).enqueue(new Callback<List<MedicationItem>>() {
            @Override
            public void onResponse(Call<List<MedicationItem>> call, Response<List<MedicationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    medicationList.addAll(response.body());
                    loadTodayStatus();
                } else {
                    Log.e(TAG, "Error cargando medicamentos (API)");
                }
            }
            @Override
            public void onFailure(Call<List<MedicationItem>> call, Throwable t) {
                Log.e(TAG, "Fallo de conexión (medicamentos): " + t.getMessage());
            }
        });
    }

    private void loadTodayStatus() {
        apiService.getTodayReminderStatus(idPacienteLogueado).enqueue(new Callback<List<TodayReminderStatus>>() {
            @Override
            public void onResponse(Call<List<TodayReminderStatus>> call, Response<List<TodayReminderStatus>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (TodayReminderStatus status : response.body()) {
                        statusMap.put(status.getId_medicamento(), status);
                    }
                } else {
                    Log.e(TAG, "Error cargando status de hoy (API)");
                }

                pagerAdapter.setMedicationList(medicationList);
                if (pagerAdapter.getItemCount() <= 1) {
                    tabLayoutIndicator.setVisibility(View.GONE);
                } else {
                    tabLayoutIndicator.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<TodayReminderStatus>> call, Throwable t) {
                Log.e(TAG, "Fallo de conexión (status hoy): " + t.getMessage());
            }
        });
    }

    @Override
    public void onMedicationToggled(MedicationItem medication, TodayReminderStatus status, boolean isChecked) {
        if (!isChecked) return;

        Log.d(TAG, "Marcando como 'completado' el recordatorio: " + status.getId_recordatorio());

        UpdateStatusBody body = new UpdateStatusBody("completado", idPacienteLogueado);
        apiService.updateReminderStatus(status.getId_recordatorio(), body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), medication.getNombre() + " marcado como tomado.", Toast.LENGTH_LONG).show();

                    if (getContext() != null) {
                        ReminderItem itemToCancel = new ReminderItem(status.getId_recordatorio());
                        AlarmScheduler.cancelAlarm(getContext(), itemToCancel);
                    }


                    TodayReminderStatus localStatus = statusMap.get(medication.getId_medicamento());
                    if (localStatus != null) {
                        localStatus.setEstado("completado");
                    }


                    int currentPageIndex = viewPagerMedications.getCurrentItem();


                    Fragment visibleFragment = getChildFragmentManager().findFragmentByTag("f" + currentPageIndex);

                    if (visibleFragment instanceof MedicationPageFragment) {
                        Log.d(TAG, "Refrescando el fragmento hijo visible (Página " + currentPageIndex + ")");
                        ((MedicationPageFragment) visibleFragment).notifyDataChanged();
                    } else {

                        Log.w(TAG, "No se pudo encontrar el fragmento hijo por tag, usando notifyDataSetChanged() como fallback.");
                        pagerAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(getContext(), "Error al marcar (API)", Toast.LENGTH_SHORT).show();
                    loadMedicationData();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                loadMedicationData();
            }
        });
    }

    class ReminderItem extends itca.soft.renalcare.data.models.ReminderItem {
        private int fakeId;
        public ReminderItem(int id) { this.fakeId = id; }
        @Override
        public int getIdRecordatorio() { return fakeId; }
    }

    private void setupGoalBars(View view) {
        View goalSodio = view.findViewById(R.id.goal_sodio);
        View goalPotasio = view.findViewById(R.id.goal_potasio);
        View goalFosforo = view.findViewById(R.id.goal_fosforo);
        View goalPeso = view.findViewById(R.id.goal_peso);

        if (goalSodio != null) {
            ((TextView) goalSodio.findViewById(R.id.tv_goal_label)).setText("Límite de Sodio");
            ((TextView) goalSodio.findViewById(R.id.tv_goal_value)).setText("Máx. 2,000 mg");
            ((ProgressBar) goalSodio.findViewById(R.id.progress_goal)).setProgress(80);
        }
        if (goalPotasio != null) {
            ((TextView) goalPotasio.findViewById(R.id.tv_goal_label)).setText("Límite de Potasio");
            ((TextView) goalPotasio.findViewById(R.id.tv_goal_value)).setText("Máx. 2,500 mg");
            ((ProgressBar) goalPotasio.findViewById(R.id.progress_goal)).setProgress(60);
        }
        if (goalFosforo != null) {
            ((TextView) goalFosforo.findViewById(R.id.tv_goal_label)).setText("Límite de Fósforo");
            ((TextView) goalFosforo.findViewById(R.id.tv_goal_value)).setText("Máx. 1,000 mg");
            ((ProgressBar) goalFosforo.findViewById(R.id.progress_goal)).setProgress(75);
        }
        if (goalPeso != null) {
            ((TextView) goalPeso.findViewById(R.id.tv_goal_label)).setText("Peso Seco");
            ((TextView) goalPeso.findViewById(R.id.tv_goal_value)).setText("68 kg");
            ((ProgressBar) goalPeso.findViewById(R.id.progress_goal)).setProgress(90);
        }
    }
}