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
// --- Import añadido ---
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
// Importa el ViewModel desde la ubicación correcta
import itca.soft.renalcare.ui.MainViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment implements OnMedicationTakeListener {

    private static final String TAG = "PerfilFragment";

    // --- Variables para ViewModel ---
    private MainViewModel mainViewModel;

    // --- Variables de Medicamentos (Lógica existente) ---
    private ViewPager2 viewPagerMedications;
    private TabLayout tabLayoutIndicator;
    private MedicationPagerAdapter pagerAdapter;
    private ImageView iconMedNotification;
    private RecordatorioApiService apiService;
    private List<MedicationItem> medicationList = new ArrayList<>();
    private Map<Integer, TodayReminderStatus> statusMap = new HashMap<>();
    private int idPacienteLogueado; // Se obtiene de SharedPreferences

    // --- Variables para Metas (Goals) ---
    private View goalSodio, goalPotasio, goalFosforo, goalPeso;
    private TextView tvUserName, tvUserAge, tvDiagnosisStage, tvDiagnosisTreatment;

    // --- Variable para el botón de settings ---
    private ImageView iconSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Obtener ID del Paciente desde SharedPreferences ---
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        // NOTA: 'id_usuario' aquí es el ID del paciente, ya sea el logueado
        // o el que el cuidador está viendo. MainViewModel se encarga de eso.
        idPacienteLogueado = prefs.getInt("id_usuario", -1);

        if (idPacienteLogueado == -1) {
            Toast.makeText(getContext(), "Error de sesión: ID no encontrado.", Toast.LENGTH_SHORT).show();
            // Aquí podrías cerrar el fragmento o redirigir al Login
            return;
            // --- ¡ERROR CORREGIDO! ---
            // El bloque de código de "Solo Vista" que estaba pegado aquí
            // ha sido eliminado de este bloque 'if'.
        }

        // --- 2. Lógica existente de Medicamentos (se mantiene) ---
        // (Esta lógica alimenta el carrusel de medicamentos)
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
        loadMedicationData(); // Se mantiene para el carrusel de meds

        // --- 3. Inicializar vistas de Perfil y Metas ---
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserAge = view.findViewById(R.id.tv_user_age);
        tvDiagnosisStage = view.findViewById(R.id.tv_diagnosis_stage);
        tvDiagnosisTreatment = view.findViewById(R.id.tv_diagnosis_treatment);

        goalSodio = view.findViewById(R.id.goal_sodio);
        goalPotasio = view.findViewById(R.id.goal_potasio);
        goalFosforo = view.findViewById(R.id.goal_fosforo);
        goalPeso = view.findViewById(R.id.goal_peso);

        // --- 4. Inicializar ViewModel y Observadores ---
        // Obtenemos el ViewModel con el scope de la Actividad (requireActivity())
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Observador para los datos del paciente (actualiza el perfil)
        mainViewModel.getPacienteInfo().observe(getViewLifecycleOwner(), new Observer<PacienteInfoResponse>() {
            @Override
            public void onChanged(PacienteInfoResponse pacienteInfo) {
                // Se llamará automáticamente cuando los datos estén listos
                if (pacienteInfo != null) {
                    // ¡Datos recibidos! Actualizamos la UI
                    Log.d(TAG, "Datos de PacienteInfoResponse recibidos. Actualizando UI.");
                    actualizarPerfilHeader(pacienteInfo);
                    actualizarMetas(pacienteInfo);
                } else {
                    // Error de red o API
                    Log.e(TAG, "Error al obtener PacienteInfoResponse (null)");
                    Toast.makeText(getContext(), "No se pudieron cargar datos del perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- ¡LÓGICA CORREGIDA Y REUBICADA! ---
        // Observador para el modo "Solo Vista" (oculta botones)
        mainViewModel.isViewOnly().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean esSoloVista) {
                // Oculta o muestra los botones de acción/edición
                if (esSoloVista) {
                    // Estamos en modo "Solo Vista" (Cuidador)
                    // Ocultamos el botón de gestionar medicamentos y el de settings
                    if (iconMedNotification != null) {
                        iconMedNotification.setVisibility(View.GONE);
                    }
                    if (iconSettings != null) {
                        iconSettings.setVisibility(View.GONE);
                    }
                } else {
                    // Estamos en modo normal (Paciente)
                    // Mostramos los botones
                    if (iconMedNotification != null) {
                        iconMedNotification.setVisibility(View.VISIBLE);
                    }
                    if (iconSettings != null) {
                        iconSettings.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // --- 5. INICIO DE LÓGICA PARA CERRAR SESIÓN ---
        iconSettings = view.findViewById(R.id.icon_settings);
        if (iconSettings != null) {
            iconSettings.setOnClickListener(v -> {
                mostrarMenuCerrarSesion(v);
            });
        }
    }

    /**
     * NUEVO MÉTODO: Actualiza el header del perfil y el diagnóstico.
     */
    private void actualizarPerfilHeader(PacienteInfoResponse info) {
        if (tvUserName != null) {
            tvUserName.setText(info.getNombrePaciente());
        }
        // Nota: La edad requiere cálculo. Por ahora, dejamos el campo del XML
        // if (tvUserAge != null) {
        //    tvUserAge.setText(calcularEdad(info.getFechaNacimiento()) + " años");
        // }
        if (tvDiagnosisStage != null) {
            tvDiagnosisStage.setText(info.getCondicionRenal()); // Ej. "Etapa 4"
        }
        if (tvDiagnosisTreatment != null) {
            tvDiagnosisTreatment.setText(info.getTipoTratamiento()); // Ej. "Hemodiálisis"
        }
    }

    /**
     * MÉTODO MODIFICADO: Reemplaza a setupGoalBars(). Ahora usa datos del ViewModel.
     */
    private void actualizarMetas(PacienteInfoResponse info) {

        // --- 💡 NOTA IMPORTANTE ---
        // Tu API (getAllInfoByID) solo provee "peso".
        // Los límites de Sodio, Potasio y Fósforo NO están en ese JSON.
        // Para que sean dinámicos, debes añadirlos a tu query en el backend.
        // Por ahora, solo "Peso" será dinámico.

        // Datos de ejemplo (Mock) para metas no provistas por la API
        String limiteSodio = "Máx. 2,000 mg";
        int progresoSodio = 80;
        String limitePotasio = "Máx. 2,500 mg";
        int progresoPotasio = 60;
        String limiteFosforo = "Máx. 1,000 mg";
        int progresoFosforo = 75;

        // Dato REAL desde la API
        String metaPeso = String.format(Locale.US, "%.1f kg", info.getPeso()); // Formateado
        int progresoPeso = 90; // (Progreso sigue siendo mock, necesitas una meta)

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
            ((TextView) goalPeso.findViewById(R.id.tv_goal_value)).setText(metaPeso); // ¡DATO REAL!
            ((ProgressBar) goalPeso.findViewById(R.id.progress_goal)).setProgress(progresoPeso);
        }
    }


    // ==================================================================
    // --- LÓGICA DE MEDICAMENTOS EXISTENTE (SIN CAMBIOS) ---
    // ==================================================================

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

        // Usa el idPacienteLogueado obtenido de SharedPreferences
        // NOTA: Este ID debe ser el ID del paciente que se está viendo
        // MainViewModel es quien decide qué ID cargar (el del cuidador o el del paciente)
        // PERO... esta lógica de meds es local del fragmento y usa 'idPacienteLogueado'
        // Deberíamos ALINEAR esto para que use el ID del MainViewModel.

        // Por ahora, asumimos que 'idPacienteLogueado' es correcto
        // (ya que se obtiene ANTES de que el ViewModel cargue)
        // Si esto falla, necesitaremos refactorizar para que esta
        // carga de meds se dispare DESPUÉS de que el ViewModel confirme el ID.

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
        // Usa el idPacienteLogueado
        apiService.getTodayReminderStatus(idPacienteLogueado).enqueue(new Callback<List<TodayReminderStatus>>() {
            @Override
            public void onResponse(Call<List<TodayReminderStatus>> call, Response<List<TodayReminderStatus>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statusMap.clear(); // Limpiar antes de llenar
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

        // Usa el idPacienteLogueado
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

    // --- MÉTODO PARA MOSTRAR MENÚ (Sin cambios) ---
    private void mostrarMenuCerrarSesion(View v) {
        // Asegúrate de usar androidx.appcompat.widget.PopupMenu
        PopupMenu popup = new PopupMenu(requireContext(), v);

        // Añade la opción de "Cerrar Sesión" al menú
        popup.getMenu().add("Cerrar Sesión");

        // Configura el listener para el clic
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Cerrar Sesión")) {
                // Borrar todos los datos guardados en SharedPreferences
                SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().clear().apply();

                // Crear un Intent para volver a LoginActivity
                Intent intent = new Intent(requireActivity(), LoginActivity.class);

                // Limpiar la pila de actividades:
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // Finalizar la MainActivity (y este fragmento)
                requireActivity().finish();
                return true;
            }
            return false;
        });

        // Mostrar el menú
        popup.show();
    }
}