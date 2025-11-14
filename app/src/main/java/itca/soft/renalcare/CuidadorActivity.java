package itca.soft.renalcare;

import android.content.Context;
import android.content.Intent; // ¡NUEVO IMPORT!
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout; // ¡NUEVO IMPORT!
import android.widget.ProgressBar; // ¡NUEVO IMPORT!
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import itca.soft.renalcare.auth.LoginActivity;

public class CuidadorActivity extends AppCompatActivity {

    private static final String TAG = "CuidadorActivity";

    // URLs de la API
    // URL base (de RetrofitClient, para consistencia)
    private static final String API_BASE_URL = "http://192.168.1.12:3000/api/cuidadores/";
    private static final String URL_ASIGNAR_CUIDADOR = API_BASE_URL + "asignarPorDUI";
    // ¡NUEVA URL! Para verificar si el cuidador tiene un paciente
    private static final String URL_GET_PACIENTE = API_BASE_URL; // Se le añadirá /:id/mi-paciente

    // Componentes de la UI
    private ProgressBar pbLoading;
    private LinearLayout layoutAsignacion;
    private TextInputEditText etDuiPaciente;
    private Button btnAsignarPaciente;
    private TextView tvFeedbackAsignacion;

    private SharedPreferences sharedPreferences;
    private int idCuidadorLogueado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidador);

        // 1. Inicializar vistas
        pbLoading = findViewById(R.id.pbLoadingCuidador);
        layoutAsignacion = findViewById(R.id.layoutAsignacion);
        etDuiPaciente = findViewById(R.id.etDuiPacienteCuidador);
        btnAsignarPaciente = findViewById(R.id.btnAsignarPacienteCuidador);
        tvFeedbackAsignacion = findViewById(R.id.tvFeedbackAsignacion);

        // 2. Obtener sesión
        sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        idCuidadorLogueado = sharedPreferences.getInt(LoginActivity.KEY_ID_USUARIO, -1);

        if (idCuidadorLogueado == -1) {
            // Si no hay ID, es un error grave de sesión.
            Toast.makeText(this, "Error de sesión. ID de Cuidador no encontrado.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error de sesión: idCuidadorLogueado es -1");
            // Aquí podríamos redirigir a LoginActivity, pero por ahora solo mostramos error.
            pbLoading.setVisibility(View.GONE);
            return;
        }

        // 3. Lógica principal: Comprobar si tiene paciente
        // El ProgressBar (pbLoading) ya está visible por defecto
        comprobarPacienteAsignado();

        // 4. Configurar el listener del botón (se usará si se muestra el layout)
        btnAsignarPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarAsignarPaciente();
            }
        });
    }

    /**
     * Llama al nuevo endpoint (GET /:id/mi-paciente) para ver si este cuidador
     * ya tiene un paciente vinculado.
     */
    private void comprobarPacienteAsignado() {
        String urlCompleta = URL_GET_PACIENTE + idCuidadorLogueado + "/mi-paciente";
        Log.d(TAG, "Comprobando paciente en: " + urlCompleta);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlCompleta, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // ¡Éxito! Significa que SÍ tiene un paciente (Status 200)
                Log.d(TAG, "onSuccess (getPacienteAsignado): " + response.toString());
                try {
                    int idPaciente = response.getInt("id_paciente");
                    // Lanzamos la PacienteActivity en modo "Solo Vista"
                    lanzarModoVista(idPaciente);

                } catch (JSONException e) {
                    Log.e(TAG, "Error JSON al parsear id_paciente", e);
                    // Caso raro, mostramos UI de asignación como fallback
                    mostrarUiAsignacion();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Falla: Puede ser 404 (No tiene paciente) u otro error
                if (statusCode == 404) {
                    // ¡Caso esperado! El cuidador NO tiene paciente.
                    Log.w(TAG, "onFailure (404): No tiene paciente asignado.");
                    // Mostramos la UI para que asigne uno.
                    mostrarUiAsignacion();
                } else {
                    // Otro error (500, sin red, etc.)
                    Log.e(TAG, "onFailure (getPacienteAsignado). StatusCode: " + statusCode, throwable);
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(CuidadorActivity.this, "Error de red al verificar paciente.", Toast.LENGTH_SHORT).show();
                    // Opcionalmente, mostrar UI de asignación aquí también
                    // mostrarUiAsignacion();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // Falla si la respuesta no es JSON (ej. error de proxy)
                Log.e(TAG, "onFailure (String). StatusCode: " + statusCode, throwable);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(CuidadorActivity.this, "Error de respuesta del servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Muestra la UI para asignar un paciente (oculta el ProgressBar).
     * Se llama cuando la comprobación inicial falla con 404.
     */
    private void mostrarUiAsignacion() {
        pbLoading.setVisibility(View.GONE);
        layoutAsignacion.setVisibility(View.VISIBLE);
    }

    /**
     * Lanza la PacienteActivity, pasándole el ID del paciente a cargar
     * y la bandera de "Solo Vista".
     * @param idPaciente El ID del paciente que se debe mostrar.
     */
    private void lanzarModoVista(int idPaciente) {
        Intent intent = new Intent(CuidadorActivity.this, PacienteActivity.class);

        // Pasamos el ID del PACIENTE que queremos ver
        intent.putExtra(PacienteActivity.EXTRA_ID_PACIENTE, idPaciente);
        // Pasamos la bandera de "Solo Vista"
        intent.putExtra(PacienteActivity.EXTRA_IS_VIEW_ONLY, true);

        startActivity(intent);
        // Finalizamos esta actividad (CuidadorActivity)
        finish();
    }


    /**
     * Lógica existente para asignar un paciente.
     * Se llama solo si el cuidador está en la UI de asignación.
     */
    private void intentarAsignarPaciente() {
        String duiPaciente = etDuiPaciente.getText().toString().trim();
        // El ID del cuidador ya lo tenemos en 'idCuidadorLogueado'

        if (duiPaciente.isEmpty()) {
            Toast.makeText(this, "Debe ingresar un DUI", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ocultamos el feedback anterior
        tvFeedbackAsignacion.setVisibility(View.GONE);

        RequestParams params = new RequestParams();
        params.put("id_cuidador", idCuidadorLogueado);
        params.put("dui_paciente", duiPaciente);

        Log.d(TAG, "Intentando asignar paciente...");
        Log.d(TAG, "URL: " + URL_ASIGNAR_CUIDADOR);
        Log.d(TAG, "Params: id_cuidador=" + idCuidadorLogueado + ", dui_paciente=" + duiPaciente);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL_ASIGNAR_CUIDADOR, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess (asignar): " + response.toString());
                try {
                    boolean exito = response.getBoolean("exito");
                    if (exito) {
                        // ¡Éxito! El paciente fue asignado.
                        // Ahora, debemos recargar esta actividad o lanzar el modo vista
                        // directamente. Lo más simple es lanzar el modo vista.
                        int idPaciente = response.getInt("id_paciente"); // Asumimos que el API devuelve el ID

                        // Si el API de asignarPorDUI no devuelve el id_paciente,
                        // tendríamos que llamar a comprobarPacienteAsignado() de nuevo.
                        // Asumiendo que SÍ lo devuelve (o que podemos añadirlo):

                        if (idPaciente > 0) {
                            lanzarModoVista(idPaciente);
                        } else {
                            // Fallback si el API no devuelve el ID (poco probable)
                            String nombrePaciente = response.getString("nombre_paciente");
                            String mensaje = "Paciente '" + nombrePaciente + "' asignado. Reiniciando...";
                            mostrarFeedback(mensaje, false);
                            // Recargamos la actividad para que pase por el check de nuevo
                            recreate();
                        }

                    } else {
                        // El servidor devolvió exito: false (ej. "Paciente no encontrado")
                        String error = response.getString("error");
                        Log.w(TAG, "Error Lógico (asignar): " + error);
                        mostrarFeedback(error, true);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error de JSONException en onSuccess (asignar)", e);
                    mostrarFeedback("Error al procesar la respuesta.", true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "onFailure (asignar). StatusCode: " + statusCode, throwable);
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