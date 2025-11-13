package itca.soft.renalcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log; // ¡NUEVO IMPORT!
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

public class CuidadorActivity extends AppCompatActivity {

    // ¡NUEVO! Tag para el Log
    private static final String TAG = "CuidadorActivity";

    private TextInputEditText etDuiPaciente;
    private Button btnAsignarPaciente;
    private TextView tvFeedbackAsignacion;
    private SharedPreferences sharedPreferences;

    // URL de la API (Asegúrate que sea la correcta)
    private static final String URL_ASIGNAR_CUIDADOR = "http://192.168.1.12:3000/api/cuidadores/asignarPorDUI";
    // Si usas emulador:
    // private static final String URL_ASIGNAR_CUIDADOR = "http://10.0.2.2:3000/api/cuidadores/asignarPorDUI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidador);

        etDuiPaciente = findViewById(R.id.etDuiPacienteCuidador);
        btnAsignarPaciente = findViewById(R.id.btnAsignarPacienteCuidador);
        tvFeedbackAsignacion = findViewById(R.id.tvFeedbackAsignacion);

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
        // Leemos el ID del cuidador logueado
        int idCuidadorLogueado = sharedPreferences.getInt(LoginActivity.KEY_ID_USUARIO, -1);

        if (duiPaciente.isEmpty()) {
            Toast.makeText(this, "Debe ingresar un DUI", Toast.LENGTH_SHORT).show();
            return;
        }
        if (idCuidadorLogueado == -1) {
            Toast.makeText(this, "Error de sesión. ID de Cuidador no encontrado.", Toast.LENGTH_LONG).show();
            // ¡NUEVO! Log para ver si este es el problema
            Log.e(TAG, "Error de sesión: idCuidadorLogueado es -1");
            return;
        }

        // Ocultamos el feedback anterior
        tvFeedbackAsignacion.setVisibility(View.GONE);

        RequestParams params = new RequestParams();
        params.put("id_cuidador", idCuidadorLogueado);
        params.put("dui_paciente", duiPaciente);

        // --- ¡NUEVO LOG DE DEBUG! ---
        // Esto imprimirá en el Logcat exactamente lo que estás enviando.
        Log.d(TAG, "Intentando asignar paciente...");
        Log.d(TAG, "URL: " + URL_ASIGNAR_CUIDADOR);
        Log.d(TAG, "Params: id_cuidador=" + idCuidadorLogueado + ", dui_paciente=" + duiPaciente);
        // -----------------------------

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL_ASIGNAR_CUIDADOR, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // ¡NUEVO LOG DE DEBUG!
                Log.d(TAG, "onSuccess. Respuesta: " + response.toString());

                try {
                    boolean exito = response.getBoolean("exito");
                    if (exito) {
                        String nombrePaciente = response.getString("nombre_paciente");
                        String mensaje = "Paciente '" + nombrePaciente + "' asignado correctamente.";

                        mostrarFeedback(mensaje, false);
                        etDuiPaciente.setText("");

                    } else {
                        // El servidor devolvió exito: false (ej. "Paciente no encontrado")
                        String error = response.getString("error");
                        Log.w(TAG, "Error Lógico: " + error);
                        mostrarFeedback(error, true);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error de JSONException en onSuccess", e);
                    mostrarFeedback("Error al procesar la respuesta.", true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // ¡NUEVO LOG DE DEBUG!
                Log.e(TAG, "onFailure. StatusCode: " + statusCode, throwable);

                String msg = "Error de red (" + statusCode + ").";
                if (errorResponse != null) {
                    Log.e(TAG, "Respuesta de Error (JSON): " + errorResponse.toString());
                    try {
                        msg = errorResponse.getString("error"); // Leemos el error del JSON
                    } catch (JSONException e) { /* no-op */ }
                }
                mostrarFeedback(msg, true);
            }
        });
    }

    // Esta función no tiene cambios
    private void mostrarFeedback(String mensaje, boolean esError) {
        tvFeedbackAsignacion.setVisibility(View.VISIBLE);
        tvFeedbackAsignacion.setText(mensaje);

        if (esError) {
            tvFeedbackAsignacion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvFeedbackAsignacion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
    }
}