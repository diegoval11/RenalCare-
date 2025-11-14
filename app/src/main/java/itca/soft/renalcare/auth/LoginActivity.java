package itca.soft.renalcare.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import itca.soft.renalcare.CuidadorActivity; // NUEVA IMPORTACIÓN
import itca.soft.renalcare.DoctorActivity; // NUEVA IMPORTACIÓN
import itca.soft.renalcare.PacienteActivity; // IMPORTACIÓN ACTUALIZADA
import itca.soft.renalcare.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etDui;
    private TextInputEditText etPin;

    private static final String URL_LOGIN = "http://192.168.1.163/wsrenalcare/login.php";

    public static final String PREFS_NAME = "RenalCarePrefs";

    // Claves de SharedPreferences (buena práctica tenerlas como constantes)
    public static final String KEY_ESTA_LOGUEADO = "estaLogueado";
    public static final String KEY_ID_USUARIO = "id_usuario";
    public static final String KEY_ROL_USUARIO = "rol_usuario";
    public static final String KEY_NOMBRE_USUARIO = "nombre_usuario";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- VERIFICACIÓN DE SESIÓN (MODIFICADO) ---
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (prefs.getBoolean(KEY_ESTA_LOGUEADO, false)) {
            // Si ya está logueado, obtenemos su rol guardado
            String rol = prefs.getString(KEY_ROL_USUARIO, null);

            // Usamos el nuevo router para ir a la actividad correcta
            redirigirSegunRol(rol);
            return; // Evita que se ejecute el resto de onCreate
        }
        // --- FIN DE VERIFICACIÓN DE SESIÓN ---

        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etDui = findViewById(R.id.etDui);
        etPin = findViewById(R.id.etPin);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToSignup = findViewById(R.id.tvGoToSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarLogin();
            }
        });

        tvGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RoleSelectionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void intentarLogin() {
        String dui = etDui.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (dui.isEmpty() || pin.isEmpty()) {
            Toast.makeText(LoginActivity.this, "DUI y PIN son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("dui", dui);
        params.put("pin", pin);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL_LOGIN, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean exito = response.getBoolean("exito");
                    if (exito) {
                        // --- Login Exitoso ---
                        JSONObject usuario = response.getJSONObject("usuario");
                        String nombre = usuario.getString("nombre");
                        String rol = usuario.getString("rol");
                        int idUsuario = usuario.getInt("id_usuario");

                        // Guardamos la sesión
                        guardarSesion(idUsuario, nombre, rol);

                        Toast.makeText(LoginActivity.this, "¡Bienvenido, " + nombre + "!", Toast.LENGTH_LONG).show();

                        // --- ¡CAMBIO! ---
                        // Redirigimos usando el rol que acabamos de obtener
                        redirigirSegunRol(rol);

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = "Error de red (" + statusCode + "): " + throwable.getMessage();
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Guarda los datos básicos del usuario en SharedPreferences.
     */
    private void guardarSesion(int id, String nombre, String rol) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Usamos las constantes para consistencia
        editor.putInt(KEY_ID_USUARIO, id);
        editor.putString(KEY_NOMBRE_USUARIO, nombre);
        editor.putString(KEY_ROL_USUARIO, rol);
        editor.putBoolean(KEY_ESTA_LOGUEADO, true);

        editor.apply();
    }

    // --- ¡CAMBIO! NUEVO MÉTODO DE ENRUTAMIENTO ---
    /**
     * Navega a la Actividad correspondiente según el rol del usuario.
     * Limpia la pila de actividades para que el usuario no pueda "volver" al login.
     * @param rol El rol del usuario (ej. "paciente", "doctor", "cuidador")
     */
    private void redirigirSegunRol(String rol) {

        Intent intent;

        if (rol == null) {
            // Caso de error: rol nulo.
            Log.e("LoginActivity", "El rol es nulo. Limpiando sesión.");
            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
            // No hacemos nada, el usuario se queda en LoginActivity
            return;
        }

        // Determinamos a qué actividad dirigir al usuario
        switch (rol) {
            case "paciente":
                intent = new Intent(LoginActivity.this, PacienteActivity.class);
                break;
            case "doctor":
                intent = new Intent(LoginActivity.this, DoctorActivity.class);
                break;
            case "cuidador":
                intent = new Intent(LoginActivity.this, CuidadorActivity.class);
                break;
            default:
                // Caso de seguridad: rol no reconocido
                Log.w("LoginActivity", "Rol no reconocido: " + rol + ". Limpiando sesión.");
                Toast.makeText(this, "Rol no reconocido: " + rol, Toast.LENGTH_LONG).show();
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
                return; // No redirigimos
        }

        // Banderas para limpiar la pila de actividades
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Cerramos LoginActivity
    }
}