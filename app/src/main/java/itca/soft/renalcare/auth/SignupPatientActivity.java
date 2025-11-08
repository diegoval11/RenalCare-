package itca.soft.renalcare.auth;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import itca.soft.renalcare.R;

public class SignupPatientActivity extends AppCompatActivity {

    private static final String URL_REGISTRO = "http://192.168.1.12/wsrenalcare/signup_patient.php";

    private LinearLayout containerMedicamentos;
    private Button btnAddMedicamento;
    private Button btnSkipMedicamentos;
    private TextView tvMedsOmitidos;
    private ImageButton btnMedicationInfo;
    private boolean medicamentosOmitidos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_patient);

        containerMedicamentos = findViewById(R.id.containerMedicamentos);
        btnAddMedicamento = findViewById(R.id.btnAddMedicamento);
        btnSkipMedicamentos = findViewById(R.id.btnSkipMedicamentos);
        tvMedsOmitidos = findViewById(R.id.tvMedsOmitidos);
        btnMedicationInfo = findViewById(R.id.btnMedicationInfo);

        btnAddMedicamento.setOnClickListener(v -> agregarFilaMedicamento());
        btnSkipMedicamentos.setOnClickListener(v -> skipMedicamentos());
        btnMedicationInfo.setOnClickListener(v -> mostrarDialogoMedicamentos());
    }

    private void skipMedicamentos() {
        medicamentosOmitidos = true;
        containerMedicamentos.setVisibility(View.GONE);
        btnAddMedicamento.setVisibility(View.GONE);
        btnSkipMedicamentos.setVisibility(View.GONE);

        tvMedsOmitidos.setVisibility(View.VISIBLE);

        containerMedicamentos.removeAllViews();
    }

    private void agregarFilaMedicamento() {
        if(medicamentosOmitidos) return;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View filaView = inflater.inflate(R.layout.activity_signup_patient, null);

        TextInputEditText etHorario = filaView.findViewById(R.id.etHorario);
        etHorario.setInputType(0);
        etHorario.setOnClickListener(v -> mostrarTimePickerDialog(etHorario));

        ImageButton btnEliminar = filaView.findViewById(R.id.btnEliminarFila);
        btnEliminar.setOnClickListener(v -> {
            ((LinearLayout)filaView.getParent()).removeView(filaView);
        });

        containerMedicamentos.addView(filaView);
    }

    private void mostrarTimePickerDialog(TextInputEditText etHorario) {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay < 12 ? "AM" : "PM";
                    int hora12 = hourOfDay % 12;
                    if (hora12 == 0) hora12 = 12;

                    String horarioFormato = String.format("%02d:%02d %s", hora12, minute, amPm);
                    etHorario.setText(horarioFormato);
                },
                hora, minuto, false
        );
        timePicker.show();
    }

    private void intentarRegistroPaciente() {
        // --- 1. Obtener datos de campos de texto ---
        String nombreCompleto = etNombrePaciente.getText().toString().trim();
        String dui = etDuiPaciente.getText().toString().trim();
        String pin = etPinPaciente.getText().toString().trim();
        String pinConfirm = etPinConfirmPaciente.getText().toString().trim();
        String contactoNombre = etNombreContacto.getText().toString().trim();
        String contactoTelefono = etTelefonoContacto.getText().toString().trim();

        // --- 2. Validaciones básicas (Campos obligatorios) ---
        tilNombrePaciente.setError(null);
        tilDuiPaciente.setError(null);
        tilPinPaciente.setError(null);
        tilPinConfirmPaciente.setError(null);

        boolean esValido = true;
        if (TextUtils.isEmpty(nombreCompleto)) {
            tilNombrePaciente.setError("El nombre es obligatorio");
            esValido = false;
        }
        if (TextUtils.isEmpty(dui)) {
            tilDuiPaciente.setError("El DUI es obligatorio");
            esValido = false;
        }
        if (TextUtils.isEmpty(pin)) {
            tilPinPaciente.setError("El PIN es obligatorio");
            esValido = false;
        }
        if (TextUtils.isEmpty(pinConfirm)) {
            tilPinConfirmPaciente.setError("Confirma tu PIN");
            esValido = false;
        }
        if (!pin.equals(pinConfirm)) {
            tilPinPaciente.setError("Los PIN no coinciden");
            tilPinConfirmPaciente.setError("Los PIN no coinciden");
            esValido = false;
        }

        if (!esValido) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 3. Preparar datos para el Web Service ---
        String[] partesNombre = nombreCompleto.split(" ");
        String nombre = "";
        String apellido = "";

        if (partesNombre.length > 0) {
            nombre = partesNombre[0];
            if (partesNombre.length > 1) {
                apellido = TextUtils.join(" ", Arrays.copyOfRange(partesNombre, 1, partesNombre.length));
            }
        }
        if (apellido.isEmpty()) {
            tilNombrePaciente.setError("Ingresa al menos un nombre y un apellido");
            return;
        }

        // --- 4. Obtener datos de RadioButtons y CheckBoxes ---
        String tipoInsuficiencia = getTextoRadioSeleccionado(rgTipoInsuficiencia);
        String tipoDialisis = getTextoRadioSeleccionado(rgTipoDialisis);

        // --- 5. Construir los parámetros de la solicitud ---
        RequestParams params = new RequestParams();

        // Datos Personales
        params.put("nombre", nombre);
        params.put("apellido", apellido);
        params.put("dui", dui);
        params.put("pin", pin);

        // Datos Médicos
        params.put("tipo_insuficiencia", tipoInsuficiencia);
        params.put("tipo_dialisis", tipoDialisis);

        // Dieta
        if (cbBajoSodio.isChecked()) params.put("dieta_sodio", "1");
        if (cbBajoPotasio.isChecked()) params.put("dieta_potasio", "1");
        if (cbBajoFosforo.isChecked()) params.put("dieta_fosforo", "1");
        if (cbBajoProteinas.isChecked()) params.put("dieta_proteinas", "1");

        // Contacto de Emergencia
        params.put("contacto_nombre", contactoNombre);
        params.put("contacto_telefono", contactoTelefono);

        // --- ¡NUEVO! Recolectar Medicamentos ---
        JSONArray medicamentosArray = new JSONArray();
        if (!medicamentosOmitidos) {
            for (int i = 0; i < containerMedicamentos.getChildCount(); i++) {
                View filaView = containerMedicamentos.getChildAt(i);
                TextInputEditText etNombreMed = filaView.findViewById(R.id.etNombreMedicamento);
                TextInputEditText etDosisMed = filaView.findViewById(R.id.etDosis);
                TextInputEditText etHorarioMed = filaView.findViewById(R.id.etHorario);

                String nombreMed = etNombreMed.getText().toString().trim();
                String dosis = etDosisMed.getText().toString().trim();
                String horario = etHorarioMed.getText().toString().trim();

                // Solo agregar si el nombre del medicamento no está vacío
                if (!TextUtils.isEmpty(nombreMed)) {
                    JSONObject medJson = new JSONObject();
                    try {
                        medJson.put("nombre", nombreMed);
                        medJson.put("dosis", dosis);
                        medJson.put("horario", horario);
                        medicamentosArray.put(medJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Enviar el array de medicamentos como un string JSON
        // El PHP lo recibirá si no está vacío.
        if (medicamentosArray.length() > 0) {
            params.put("medicamentos_json", medicamentosArray.toString());
        }





        // --- 6. Enviar datos al Web Service ---
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL_REGISTRO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean exito = response.getBoolean("exito");
                    if (exito) {
                        Toast.makeText(SignupPatientActivity.this, "Paciente registrado exitosamente.", Toast.LENGTH_LONG).show();
                        // Volver al Login
                        Intent intent = new Intent(SignupPatientActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String error = response.getString("error");
                        Toast.makeText(SignupPatientActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(SignupPatientActivity.this, "Respuesta inválida del servidor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = "Error de red (" + statusCode + "): " + throwable.getMessage();
                Toast.makeText(SignupPatientActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra un diálogo de Alerta con información sobre medicamentos.
     * Lee los strings del nuevo archivo strings_meds.xml
     */
    private void mostrarDialogoMedicamentos() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.medication_info_title)
                .setMessage(R.string.medication_info_details) // Esto ahora viene de strings_meds.xml
                .setPositiveButton("Entendido", null)
                .show();
    }

    /**
     * Método de ayuda para obtener el texto de un RadioButton seleccionado
     * dentro de un RadioGroup.
     */
    private String getTextoRadioSeleccionado(RadioGroup radioGroup) {
        int idSeleccionado = radioGroup.getCheckedRadioButtonId();
        if (idSeleccionado != -1) {
            RadioButton rb = findViewById(idSeleccionado);
            return rb.getText().toString();
        }
        return null; // Devuelve null si no hay nada seleccionado
    }
}