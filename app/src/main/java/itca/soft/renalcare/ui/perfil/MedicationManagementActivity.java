package itca.soft.renalcare.ui.perfil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import itca.soft.renalcare.R;
import itca.soft.renalcare.data.models.MedicationItem;
import itca.soft.renalcare.data.models.ReminderItem;
import itca.soft.renalcare.data.network.RecordatorioApiService;
import itca.soft.renalcare.notifications.AlarmScheduler;
import itca.soft.renalcare.data.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicationManagementActivity extends AppCompatActivity implements ReminderAdapter.OnReminderDeleteListener {

    private static final String TAG = "MedManagementActivity";
    private RecordatorioApiService apiService;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddMedication;
    private ReminderAdapter adapter;
    private List<ReminderItem> reminderList = new ArrayList<>();
    private TextView tvNoReminders;
    private TextView btnBack;
    private List<MedicationItem> prescriptionList = new ArrayList<>();

    // Launcher de permisos (sin cambios)
    private final String[] REQUIRED_PERMISSIONS = { Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SCHEDULE_EXACT_ALARM };
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) allGranted = false;
                }
                if (allGranted) loadPendingReminders();
                else Toast.makeText(this, "Se necesitan permisos para las alarmas", Toast.LENGTH_LONG).show();
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_management);

        apiService = RetrofitClient.getClient().create(RecordatorioApiService.class);

        fabAddMedication = findViewById(R.id.btn_add_medication);
        recyclerView = findViewById(R.id.recycler_manage_meds);
        tvNoReminders = findViewById(R.id.tv_no_reminders);
        btnBack = findViewById(R.id.btn_back_reminders);

        setupRecyclerView();
        checkAndRequestPermissions();

        btnBack.setOnClickListener(v -> finish());
        fabAddMedication.setOnClickListener(v -> showAddMedicationDialog());
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean notificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
            boolean alarmPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                notificationPermission = true;
            }
            if (notificationPermission && alarmPermission) {
                loadPendingReminders();
            } else {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS);
            }
        } else {
            loadPendingReminders();
        }
    }

    private void setupRecyclerView() {
        adapter = new ReminderAdapter(reminderList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // Esta función solo se llama la PRIMERA vez que se carga la actividad
    private void loadPendingReminders() {
        int idPacienteLogueado = 2; // (¡Recuerda cambiar esto!)
        Call<List<ReminderItem>> call = apiService.getPendingReminders(idPacienteLogueado);
        call.enqueue(new Callback<List<ReminderItem>>() {
            @Override
            public void onResponse(Call<List<ReminderItem>> call, Response<List<ReminderItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reminderList.clear();
                    reminderList.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Actualización inicial
                    updateEmptyView(); // Comprobar si la lista está vacía
                    scheduleAllAlarms(reminderList);
                } else {
                    Toast.makeText(MedicationManagementActivity.this, "Error al cargar recordatorios", Toast.LENGTH_SHORT).show();
                    updateEmptyView();
                }
            }
            @Override
            public void onFailure(Call<List<ReminderItem>> call, Throwable t) {
                Toast.makeText(MedicationManagementActivity.this, "Fallo de conexión", Toast.LENGTH_LONG).show();
                updateEmptyView();
            }
        });
    }

    private void scheduleAllAlarms(List<ReminderItem> items) {
        if (items == null || items.isEmpty()) return;
        for (ReminderItem item : items) {
            AlarmScheduler.scheduleAlarm(this, item);
        }
    }

    private void showAddMedicationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_med, null);

        // --- ENLAZAR VISTAS (INCLUYENDO LOS CHIPS) ---
        final EditText etMedName = dialogView.findViewById(R.id.et_med_name);
        final EditText etMedDose = dialogView.findViewById(R.id.et_med_dose);
        final TimePicker tpMedTime = dialogView.findViewById(R.id.tp_med_time);
        final EditText etMedNotes = dialogView.findViewById(R.id.et_med_notes);
        final TextView btnLoad = dialogView.findViewById(R.id.btn_load_prescription);

        // --- Encontrar el ChipGroup y los Chips ---
        final ChipGroup chipGroup = dialogView.findViewById(R.id.chip_group_dias);
        final Chip chipL = dialogView.findViewById(R.id.chip_l);
        final Chip chipM = dialogView.findViewById(R.id.chip_m);
        final Chip chipX = dialogView.findViewById(R.id.chip_x);
        final Chip chipJ = dialogView.findViewById(R.id.chip_j);
        final Chip chipV = dialogView.findViewById(R.id.chip_v);
        final Chip chipS = dialogView.findViewById(R.id.chip_s);
        final Chip chipD = dialogView.findViewById(R.id.chip_d);
        // --- Fin Enlace Chips ---

        // Pasamos los nuevos controles (TimePicker y ChipGroup)
        btnLoad.setOnClickListener(v -> showPrescriptionPicker(etMedName, etMedDose, tpMedTime, chipGroup));

        builder.setView(dialogView)
                .setTitle("Añadir Recordatorio")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etMedName.getText().toString().trim();
                    String dosis = etMedDose.getText().toString().trim();
                    String notas = etMedNotes.getText().toString().trim();
                    int hour = tpMedTime.getHour();
                    int minute = tpMedTime.getMinute();
                    String horario = String.format(Locale.US, "%02d:%02d:00", hour, minute);

                    // --- Construir la cadena dias_semana ---
                    StringBuilder diasBuilder = new StringBuilder();
                    if (chipL.isChecked()) diasBuilder.append("L,");
                    if (chipM.isChecked()) diasBuilder.append("M,");
                    if (chipX.isChecked()) diasBuilder.append("X,");
                    if (chipJ.isChecked()) diasBuilder.append("J,");
                    if (chipV.isChecked()) diasBuilder.append("V,");
                    if (chipS.isChecked()) diasBuilder.append("S,");
                    if (chipD.isChecked()) diasBuilder.append("D,");

                    String dias_semana = diasBuilder.toString();
                    if (dias_semana.endsWith(",")) {
                        dias_semana = dias_semana.substring(0, dias_semana.length() - 1); // Quitar la última coma
                    }
                    // --- Fin Construcción Cadena ---

                    if (nombre.isEmpty()) {
                        Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // --- Validar que se seleccionó al menos un día ---
                    if (dias_semana.isEmpty()) {
                        Toast.makeText(this, "Debes seleccionar al menos un día", Toast.LENGTH_SHORT).show();
                        return; // Detener el guardado
                    }
                    // --- Fin Validación ---

                    int idPacienteLogueado = 2; // (¡Recuerda cambiar esto!)

                    // --- Usar el nuevo constructor con dias_semana ---
                    MedicationItem newMed = new MedicationItem(idPacienteLogueado, nombre, dosis, horario, notas, dias_semana);
                    createNewMedication(newMed);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // --- MODIFICADO: showPrescriptionPicker ahora también rellena los días y la hora ---
    private void showPrescriptionPicker(EditText etMedName, EditText etMedDose, TimePicker tpMedTime, ChipGroup chipGroup) {
        int idPacienteLogueado = 2; // (¡Recuerda cambiar esto!)
        apiService.getMedicamentos(idPacienteLogueado).enqueue(new Callback<List<MedicationItem>>() {
            @Override
            public void onResponse(Call<List<MedicationItem>> call, Response<List<MedicationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    prescriptionList = response.body();
                    if(prescriptionList.isEmpty()) {
                        Toast.makeText(MedicationManagementActivity.this, "No tienes prescripciones guardadas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CharSequence[] medNames = new CharSequence[prescriptionList.size()];
                    for (int i = 0; i < prescriptionList.size(); i++) {
                        // Mostramos Nombre + Dosis + Hora para diferenciar reglas
                        MedicationItem item = prescriptionList.get(i);
                        medNames[i] = item.getNombre() + " (" + item.getDosis() + " - " + item.getHorario().substring(0, 5) + ")";
                    }
                    AlertDialog.Builder pickerBuilder = new AlertDialog.Builder(MedicationManagementActivity.this);
                    pickerBuilder.setTitle("Elige una prescripción")
                            .setItems(medNames, (dialog, which) -> {
                                MedicationItem selectedMed = prescriptionList.get(which);
                                etMedName.setText(selectedMed.getNombre());
                                etMedDose.setText(selectedMed.getDosis());

                                // Rellenar Hora
                                String[] timeParts = selectedMed.getHorario().split(":");
                                tpMedTime.setHour(Integer.parseInt(timeParts[0]));
                                tpMedTime.setMinute(Integer.parseInt(timeParts[1]));

                                // Rellenar Días
                                String dias = selectedMed.getDias_semana();
                                if (dias != null) {
                                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                                        Chip chip = (Chip) chipGroup.getChildAt(i);
                                        String tag = (String) chip.getTag();
                                        chip.setChecked(dias.contains(tag));
                                    }
                                }
                            });
                    pickerBuilder.create().show();
                } else {
                    Toast.makeText(MedicationManagementActivity.this, "Error al cargar prescripciones", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<MedicationItem>> call, Throwable t) {
                Toast.makeText(MedicationManagementActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDeleteClick(ReminderItem reminder, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Recordatorio")
                .setMessage("¿Estás seguro de que quieres eliminar la alarma para '" + reminder.getTitulo() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    deleteReminderFromApi(reminder, position);
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteReminderFromApi(ReminderItem reminder, int position) {
        Call<Void> call = apiService.deleteRecordatorio(reminder.getIdRecordatorio());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MedicationManagementActivity.this, "Recordatorio eliminado", Toast.LENGTH_SHORT).show();
                    AlarmScheduler.cancelAlarm(MedicationManagementActivity.this, reminder);
                    // Actualizar UI localmente
                    if (position >= 0 && position < reminderList.size()) {
                        reminderList.remove(position);
                        adapter.notifyItemRemoved(position);
                        updateEmptyView(); // Comprobar si la lista quedó vacía
                    }
                } else {
                    Toast.makeText(MedicationManagementActivity.this, "Error al eliminar (API)", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MedicationManagementActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Función helper para mostrar/ocultar el texto "No hay recordatorios"
    private void updateEmptyView() {
        if (reminderList.isEmpty()) {
            tvNoReminders.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoReminders.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ▼▼▼ FUNCIÓN CORREGIDA (Anti-Race-Condition) ▼▼▼
    private void createNewMedication(MedicationItem newMed) {
        Log.d(TAG, "Intentando crear nuevo recordatorio para: " + newMed.getNombre());

        // ¡CAMBIO! La API ahora devuelve un ReminderItem
        // (Asegúrate de haber hecho el cambio en RecordatorioApiService.java)
        Call<ReminderItem> call = apiService.createMedicamento(newMed);

        // ¡CAMBIO! El Callback espera un ReminderItem
        call.enqueue(new Callback<ReminderItem>() {
            @Override
            public void onResponse(Call<ReminderItem> call, Response<ReminderItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "¡Éxito! Recordatorio creado.");
                    Toast.makeText(MedicationManagementActivity.this, "Recordatorio añadido", Toast.LENGTH_SHORT).show();

                    // --- INICIO DE LA SOLUCIÓN ---
                    // 1. Obtener el nuevo recordatorio de la respuesta
                    ReminderItem nuevoRecordatorio = response.body();

                    // 2. Añadirlo a la lista local
                    reminderList.add(nuevoRecordatorio);

                    // 3. Notificar al adaptador que un ítem se insertó al final
                    adapter.notifyItemInserted(reminderList.size() - 1);

                    // 4. Asegurarse de que el RecyclerView sea visible
                    updateEmptyView();

                    // 5. Programar la alarma para este *nuevo* recordatorio
                    AlarmScheduler.scheduleAlarm(MedicationManagementActivity.this, nuevoRecordatorio);

                    // 6. NO LLAMAR A loadPendingReminders()
                    // --- FIN DE LA SOLUCIÓN ---

                } else {
                    String errorMsg = "Error al crear recordatorio (API)";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += ": " + response.code() + " " + response.message();
                        } catch (Exception e) {}
                    } else if (response.code() == 400) {
                        errorMsg = "Revisa los datos (Quizás faltan los días)";
                    }
                    Log.e(TAG, "Error al crear: " + errorMsg);
                    Toast.makeText(MedicationManagementActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReminderItem> call, Throwable t) {
                Log.e(TAG, "Fallo de conexión al crear: " + t.getMessage());
                Toast.makeText(MedicationManagementActivity.this, "Fallo de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }
}