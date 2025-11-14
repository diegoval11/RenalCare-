package itca.soft.renalcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import itca.soft.renalcare.auth.LoginActivity;

public class DoctorActivity extends AppCompatActivity {

    private TextInputEditText etDuiPaciente;
    private Button btnAsignarPaciente;
    private TextView tvFeedbackAsignacion;
    private SharedPreferences sharedPreferences;

    // --- ¡CAMBIO CRÍTICO! ---
    // Añadido el prefijo /api/ que vimos en tu server.js
    private static final String URL_ASIGNAR_DOCTOR = "http://192.168.1.163:3000/api/doctores/asignarPorDUI";

    // Si usas un emulador, la URL debería ser:
    // private static final String URL_ASIGNAR_DOCTOR = "http://10.0.2.2:3000/api/doctores/asignarPorDUI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        etDuiPaciente = findViewById(R.id.etDuiPaciente);
        btnAsignarPaciente = findViewById(R.id.btnAsignarPaciente);

        tvFeedbackAsignacion = findViewById(R.id.tvFeedbackAsignacionDoctor);

        sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

        btnAsignarPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarAsignarPaciente();
            }
        });
    }

    private void intentarAsignarPaciente() {
        String duiPaciente = etDuiPaciente.getText().toString().trim();
        int idDoctorLogueado = sharedPreferences.getInt(LoginActivity.KEY_ID_USUARIO, -1);

        if (duiPaciente.isEmpty()) {
            Toast.makeText(this, "Debe ingresar un DUI", Toast.LENGTH_SHORT).show();
            return;
        }
        if (idDoctorLogueado == -1) {
            Toast.makeText(this, "Error de sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        if(tvFeedbackAsignacion != null) {
            tvFeedbackAsignacion.setVisibility(View.GONE);
        }

        RequestParams params = new RequestParams();
        params.put("id_doctor", idDoctorLogueado);
        params.put("dui_paciente", duiPaciente);

        AsyncHttpClient client = new AsyncHttpClient();
        // Hacemos el POST a la URL corregida
        client.post(URL_ASIGNAR_DOCTOR, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean exito = response.getBoolean("exito");
                    if (exito) {
                        String nombrePaciente = response.optString("nombre_paciente", "Paciente");
                        String mensaje = "'" + nombrePaciente + "' asignado correctamente.";

                        mostrarFeedback(mensaje, false);
                        etDuiPaciente.setText("");

                    } else {
                        mostrarFeedback(response.getString("error"), true);
                    }
                } catch (JSONException e) {
                    mostrarFeedback("Error al procesar la respuesta.", true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = "Error de red (" + statusCode + ").";
                if (errorResponse != null) {
                    try {
                        msg = errorResponse.getString("error");
                    } catch (JSONException e) { /* no-op */ }
                }
                mostrarFeedback(msg, true);
            }
        });
    }

    private void mostrarFeedback(String mensaje, boolean esError) {
        if(tvFeedbackAsignacion == null) {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            return;
        }

        tvFeedbackAsignacion.setVisibility(View.VISIBLE);
        tvFeedbackAsignacion.setText(mensaje);

        if (esError) {
            tvFeedbackAsignacion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvFeedbackAsignacion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
    }
}