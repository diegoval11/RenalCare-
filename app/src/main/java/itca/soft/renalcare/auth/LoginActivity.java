package itca.soft.renalcare.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import itca.soft.renalcare.MainActivity;
import itca.soft.renalcare.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etDui;
    private TextInputEditText etPin;

    // URL del Web Service de Login
    private static final String URL_LOGIN = "http://192.168.1.163/wsrenalcare/login.php";

    public static final String PREFS_NAME = "RenalCarePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ocultar la barra de acción
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
                // Reemplazamos la lógica de ejemplo por la llamada real
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

        // Validación simple de campos vacíos
        if (dui.isEmpty() || pin.isEmpty()) {
            Toast.makeText(LoginActivity.this, "DUI y PIN son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Preparar los parámetros que pide el login.php
        RequestParams params = new RequestParams();
        params.put("dui", dui);
        params.put("pin", pin);

        // Cliente AsyncHttpClient para la petición POST
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL_LOGIN, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // El servidor respondió, revisamos si 'exito' es true
                    boolean exito = response.getBoolean("exito");
                    if (exito) {
                        // --- ¡Login Exitoso! ---
                        JSONObject usuario = response.getJSONObject("usuario");
                        String nombre = usuario.getString("nombre");
                        String rol = usuario.getString("rol");
                        int idUsuario = usuario.getInt("id_usuario");

                        // Guardamos la sesión del usuario
                        guardarSesion(idUsuario, nombre, rol);

                        // Mostramos bienvenida y redirigimos a MainActivity
                        Toast.makeText(LoginActivity.this, "¡Bienvenido, " + nombre + "!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        // Limpiamos la pila para que el usuario no pueda "volver" al login
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Cerramos LoginActivity

                    } else {
                        // --- Login Fallido (Usuario o PIN incorrecto) ---
                        String error = response.getString("error");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // El servidor no respondió o hubo un error de red
                String msg = "Error de red (" + statusCode + "): " + throwable.getMessage();
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Guarda los datos básicos del usuario en SharedPreferences para mantener la sesión.
     */
    private void guardarSesion(int id, String nombre, String rol) {
        // Usamos MODE_PRIVATE para que solo esta app pueda leer las preferencias
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Guardamos los datos clave
        editor.putInt("id_usuario", id);
        editor.putString("nombre_usuario", nombre);
        editor.putString("rol_usuario", rol);
        editor.putBoolean("estaLogueado", true);

        // Aplicamos los cambios
        editor.apply();
    }
}