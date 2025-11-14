package itca.soft.renalcare.ui.perfil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.LoginActivity;
import itca.soft.renalcare.data.models.MedicationItem;
import itca.soft.renalcare.data.models.PacienteInfoResponse;
import itca.soft.renalcare.data.models.ReminderItem;
import itca.soft.renalcare.data.models.TodayReminderStatus;
import itca.soft.renalcare.data.network.RecordatorioApiService;
import itca.soft.renalcare.data.network.RetrofitClient;
import itca.soft.renalcare.data.network.UpdateStatusBody;
import itca.soft.renalcare.notifications.AlarmScheduler;
import itca.soft.renalcare.ui.MainViewModel;
import itca.soft.renalcare.ui.perfil.MedicationManagementActivity;
import itca.soft.renalcare.ui.perfil.MedicationPagerAdapter;
import itca.soft.renalcare.ui.perfil.OnMedicationTakeListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment implements OnMedicationTakeListener {

    private static final String TAG = "PerfilFragment";
    private MainViewModel mainViewModel;

    // (Variables de UI y de lógica sin cambios)
    private ViewPager2 viewPagerMedications;
    private TabLayout tabLayoutIndicator;
    private MedicationPagerAdapter pagerAdapter;
    private ImageView iconMedNotification;
    private RecordatorioApiService apiService;
    private List<MedicationItem> medicationList = new ArrayList<>();
    private Map<Integer, TodayReminderStatus> statusMap = new HashMap<>();
    private int idPacienteLogueado;
    private View goalSodio, goalPotasio, goalFosforo, goalPeso;
    private TextView tvUserName, tvUserAge, tvDiagnosisStage, tvDiagnosisTreatment;
    private ImageView iconSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        idPacienteLogueado = prefs.getInt("id_usuario", -1);

        if (idPacienteLogueado == -1) {
            Toast.makeText(getContext(), "Error de sesión: ID no encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lógica de Medicamentos (sin cambios)
        apiService = RetrofitClient.getClient().create(RecordatorioApiService.class);
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
        // loadMedicationData(); // Se quita de aquí, ahora lo llama el observer

        // Vistas de Perfil y Metas (sin cambios)
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserAge = view.findViewById(R.id.tv_user_age);
        tvDiagnosisStage = view.findViewById(R.id.tv_diagnosis_stage);
        tvDiagnosisTreatment = view.findViewById(R.id.tv_diagnosis_treatment);
        goalSodio = view.findViewById(R.id.goal_sodio);
        goalPotasio = view.findViewById(R.id.goal_potasio);
        goalFosforo = view.findViewById(R.id.goal_fosforo);
        goalPeso = view.findViewById(R.id.goal_peso);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

// Observador de PacienteInfo (¡MODIFICADO!)
        mainViewModel.getPacienteInfo().observe(getViewLifecycleOwner(), new Observer<PacienteInfoResponse>() {
            @Override
            public void onChanged(PacienteInfoResponse pacienteInfo) {
                if (pacienteInfo != null) {
                    Log.d(TAG, "PacienteInfo actualizado, refrescando UI (Header/Goals).");
                    actualizarPerfilHeader(pacienteInfo);
                    actualizarMetas(pacienteInfo);

                    // ¡¡¡CAMBIO CLAVE!!!
                    // ¡NO LLAMAR a loadMedicationData() desde aquí!
                    // Esto acopla la carga de medicamentos a la carga del perfil
                    // y causa los problemas de refresco.
                    // loadMedicationData(); // <-- LÍNEA ELIMINADA

                } else {
                    Log.e(TAG, "Error al obtener PacienteInfoResponse (null)");
                }
            }
        });

        // Observador de Modo Solo Vista (sin cambios)
        mainViewModel.isViewOnly().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean esSoloVista) {
                if (iconMedNotification != null) {
                    iconMedNotification.setVisibility(esSoloVista ? View.GONE : View.VISIBLE);
                }
                if (iconSettings != null) {
                    iconSettings.setVisibility(esSoloVista ? View.GONE : View.VISIBLE);
                }
            }
        });

        // Lógica de Cerrar Sesión (sin cambios)
        iconSettings = view.findViewById(R.id.icon_settings);
        if (iconSettings != null) {
            iconSettings.setOnClickListener(v -> mostrarMenuCerrarSesion(v));
        }
    }

    // --- ¡AQUÍ ESTÁ LA SOLUCIÓN AL BUG 1! ---
    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: Forzando refresco de AMBOS datos.");

        // 1. Forzar refresco de los datos del ViewModel (Header, Metas)
        //    (Esto usa la lógica de MediatorLiveData que te di)
        if (mainViewModel != null) {
            mainViewModel.forceRefreshPacienteInfo();
        }

        // 2. Forzar refresco de la lista de Medicamentos (Carrusel)
        //    Esta es la llamada que soluciona tu bug de medicamentos,
        //    porque ahora se llama *directamente* cada vez que vuelves.
        loadMedicationData();
    }
    // ----------------------------------------

    // (El resto de métodos: actualizarPerfilHeader, actualizarMetas,
    //  setupViewPager, loadMedicationData, loadTodayStatus,
    //  onMedicationToggled, ReminderItem class, mostrarMenuCerrarSesion...
    //  NO TIENEN CAMBIOS)

    private void actualizarPerfilHeader(PacienteInfoResponse info) {
        if (tvUserName != null) tvUserName.setText(info.getNombrePaciente());
        if (tvDiagnosisStage != null) tvDiagnosisStage.setText(info.getCondicionRenal());
        if (tvDiagnosisTreatment != null) tvDiagnosisTreatment.setText(info.getTipoTratamiento());
    }

    private void actualizarMetas(PacienteInfoResponse info) {
        // (Tu lógica de metas con datos mock y peso real)
        String limiteSodio = "Máx. 2,000 mg";
        int progresoSodio = 80;
        String limitePotasio = "Máx. 2,500 mg";
        int progresoPotasio = 60;
        String limiteFosforo = "Máx. 1,000 mg";
        int progresoFosforo = 75;
        String metaPeso = String.format(Locale.US, "%.1f kg", info.getPeso());
        int progresoPeso = 90;

        if (goalSodio != null) {
            ((TextView) goalSodio.findViewById(R.id.tv_goal_label)).setText("Límite de Sodio");
            ((TextView) goalSodio.findViewById(R.id.tv_goal_value)).setText(limiteSodio);
            ((ProgressBar) goalSodio.findViewById(R.id.progress_goal)).setProgress(progresoSodio);
        }
        if (goalPotasio != null) {
            ((TextView) goalPotasio.findViewById(R.id.tv_goal_label)).setText("Límite de Potasio");
            ((TextView) goalPotasio.findViewById(R.id.tv_goal_value)).setText(limitePotasio);
            ((ProgressBar) goalPotasio.findViewById(R.id.progress_goal)).setProgress(progresoPotasio);
        }
        if (goalFosforo != null) {
            ((TextView) goalFosforo.findViewById(R.id.tv_goal_label)).setText("Límite de Fósforo");
            ((TextView) goalFosforo.findViewById(R.id.tv_goal_value)).setText(limiteFosforo);
            ((ProgressBar) goalFosforo.findViewById(R.id.progress_goal)).setProgress(progresoFosforo);
        }
        if (goalPeso != null) {
            ((TextView) goalPeso.findViewById(R.id.tv_goal_label)).setText("Peso Seco");
            ((TextView) goalPeso.findViewById(R.id.tv_goal_value)).setText(metaPeso);
            ((ProgressBar) goalPeso.findViewById(R.id.progress_goal)).setProgress(progresoPeso);
        }
    }

    private void setupViewPager() {
        pagerAdapter = new MedicationPagerAdapter(this, statusMap);
        viewPagerMedications.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayoutIndicator, viewPagerMedications, (tab, position) -> {}).attach();
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
                    statusMap.clear();
                    for (TodayReminderStatus status : response.body()) {
                        statusMap.put(status.getId_medicamento(), status);
                    }
                } else {
                    Log.e(TAG, "Error cargando status de hoy (API)");
                }
                pagerAdapter.setMedicationList(new ArrayList<>(medicationList));
                tabLayoutIndicator.setVisibility(pagerAdapter.getItemCount() <= 1 ? View.GONE : View.VISIBLE);
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
        UpdateStatusBody body = new UpdateStatusBody("completado", idPacienteLogueado);
        apiService.updateReminderStatus(status.getId_recordatorio(), body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), medication.getNombre() + " marcado como tomado.", Toast.LENGTH_LONG).show();
                    if (getContext() != null) {
                        AlarmScheduler.cancelAlarm(getContext(), new ReminderItem(status.getId_recordatorio()));
                    }
                    TodayReminderStatus localStatus = statusMap.get(medication.getId_medicamento());
                    if (localStatus != null) localStatus.setEstado("completado");

                    int currentPageIndex = viewPagerMedications.getCurrentItem();
                    Fragment visibleFragment = getChildFragmentManager().findFragmentByTag("f" + currentPageIndex);
                    if (visibleFragment instanceof MedicationPageFragment) {
                        ((MedicationPageFragment) visibleFragment).notifyDataChanged();
                    } else {
                        pagerAdapter.notifyDataSetChanged();
                    }
                } else {
                    loadMedicationData();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loadMedicationData();
            }
        });
    }

    // Clase interna para cancelar alarma
    class ReminderItem extends itca.soft.renalcare.data.models.ReminderItem {
        private int fakeId;
        public ReminderItem(int id) { this.fakeId = id; }
        @Override
        public int getIdRecordatorio() { return fakeId; }
    }

    private void mostrarMenuCerrarSesion(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);
        popup.getMenu().add("Cerrar Sesión");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Cerrar Sesión")) {
                SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                return true;
            }
            return false;
        });
        popup.show();
    }
}