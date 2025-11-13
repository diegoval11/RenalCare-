package itca.soft.renalcare.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import itca.soft.renalcare.R;

public class SignupCaregiverActivity extends AppCompatActivity {

    // URL de tu Web Service
    private static final String URL_REGISTRO = "http://192.168.1.163/wsrenalcare/signup_caregiver.php";

    // Declarar todos los componentes de la UI
    private CircleImageView imgProfile;
    private Button btnUploadPhoto;
    private TextInputLayout tilNombreCuidador, tilDuiCuidador, tilParentesco, tilTelefonoCuidador, tilPinCuidador, tilPinConfirmCuidador;
    private TextInputEditText etNombreCuidador, etDuiCuidador, etParentesco, etTelefonoCuidador, etPinCuidador, etPinConfirmCuidador;
    private Button btnRegisterCaregiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_caregiver);

        // --- Encontrar todos los Vistas por su ID ---
        imgProfile = findViewById(R.id.imgProfile);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);

        // Campos de texto (Layouts)
        tilNombreCuidador = findViewById(R.id.tilNombreCuidador);
        tilDuiCuidador = findViewById(R.id.tilDuiCuidador);
        tilParentesco = findViewById(R.id.tilParentesco);
        tilTelefonoCuidador = findViewById(R.id.tilTelefonoCuidador);
        tilPinCuidador = findViewById(R.id.tilPinCuidador);
        tilPinConfirmCuidador = findViewById(R.id.tilPinConfirmCuidador);

        // Campos de texto (EditTexts)
        etNombreCuidador = findViewById(R.id.etNombreCuidador);
        etDuiCuidador = findViewById(R.id.etDuiCuidador);
        etParentesco = findViewById(R.id.etParentesco);
        etTelefonoCuidador = findViewById(R.id.etTelefonoCuidador);
        etPinCuidador = findViewById(R.id.etPinCuidador);
        etPinConfirmCuidador = findViewById(R.id.etPinConfirmCuidador);

        // Botón de registro
        btnRegisterCaregiver = findViewById(R.id.btnRegisterCaregiver);

        // Configurar el listener del botón
        btnRegisterCaregiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cuando se hace clic, llamamos a nuestro método de registro
                intentarRegistro();
            }
        });

        // (Aquí puedes añadir la lógica para btnUploadPhoto si lo deseas)
    }

    private void intentarRegistro() {
        // --- 1. Obtener los datos de los campos ---
        String nombreCompleto = etNombreCuidador.getText().toString().trim();
        String dui = etDuiCuidador.getText().toString().trim();
        String parentesco = etParentesco.getText().toString().trim();
        String telefono = etTelefonoCuidador.getText().toString().trim();
        String pin = etPinCuidador.getText().toString().trim();
        String pinConfirm = etPinConfirmCuidador.getText().toString().trim();

        // --- 2. Limpiar errores previos ---
        tilNombreCuidador.setError(null);
        tilDuiCuidador.setError(null);
        tilParentesco.setError(null);
        tilTelefonoCuidador.setError(null);
        tilPinCuidador.setError(null);
        tilPinConfirmCuidador.setError(null);

        // --- 3. Validar los campos ---
        boolean esValido = true;

        if (TextUtils.isEmpty(nombreCompleto)) {
            tilNombreCuidador.setError("El nombre completo es obligatorio");
            esValido = false;
        }

        if (TextUtils.isEmpty(dui)) {
            tilDuiCuidador.setError("El DUI es obligatorio");
            esValido = false;
        }

        if (TextUtils.isEmpty(parentesco)) {
            tilParentesco.setError("El parentesco es obligatorio");
            esValido = false;
        }

        if (TextUtils.isEmpty(telefono)) {
            tilTelefonoCuidador.setError("El teléfono es obligatorio");
            esValido = false;
        }

        if (TextUtils.isEmpty(pin)) {
            tilPinCuidador.setError("El PIN es obligatorio");
            esValido = false;
        }

        if (TextUtils.isEmpty(pinConfirm)) {
            tilPinConfirmCuidador.setError("Confirma tu PIN");
            esValido = false;
        }

        // Validación cruzada de PINs
        if (!pin.equals(pinConfirm)) {
            tilPinCuidador.setError("Los PIN no coinciden");
            tilPinConfirmCuidador.setError("Los PIN no coinciden");
            esValido = false;
        }

        // Si alguna validación falló, no continuamos
        if (!esValido) {
            return;
        }

        // --- 4. Preparar datos para el Web Service ---
        // El PHP espera "nombre" y "apellido" separados, pero tenemos "nombreCompleto".
        // Vamos a dividirlos.
        String[] partesNombre = nombreCompleto.split(" ");
        String nombre = "";
        String apellido = "";

        if (partesNombre.length > 0) {
            nombre = partesNombre[0];
            if (partesNombre.length > 1) {
                // Asume que todo después del primer espacio es apellido
                apellido = TextUtils.join(" ", Arrays.copyOfRange(partesNombre, 1, partesNombre.length));
            }
        }

        // Si el apellido sigue vacío (ej: solo escribió "Juan"), pedimos apellido
        if (apellido.isEmpty()) {
            tilNombreCuidador.setError("Ingresa al menos un nombre y un apellido");
            return;
        }

        // --- 5. Enviar los datos al backend (Web Service) ---
        RequestParams params = new RequestParams();
        params.put("nombre", nombre);
        params.put("apellido", apellido);
        params.put("dui", dui);
        params.put("pin", pin);
        params.put("telefono", telefono);
        params.put("parentesco", parentesco);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL_REGISTRO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // El servidor respondió (HTTP 200)
                try {
                    boolean exito = response.getBoolean("exito");

                    if (exito) {
                        // ¡Registro exitoso!
                        Toast.makeText(SignupCaregiverActivity.this, "Encargado registrado exitosamente.", Toast.LENGTH_LONG).show();

                        // Volver al Login
                        Intent intent = new Intent(SignupCaregiverActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // El PHP nos devolvió un error (ej: DUI ya existe)
                        String error = response.getString("error");
                        Toast.makeText(SignupCaregiverActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(SignupCaregiverActivity.this, "Respuesta inválida del servidor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // El servidor falló o no se pudo conectar (ej: HTTP 404, 500 o no hay red)

                // 1. Corregido el typo 'SignupCarefigverActivity' a 'SignupCaregiverActivity'.
                // 2. Mejorado el mensaje de error para mostrar el código de estado.
                String msg = "Error de red (" + statusCode + "): " + throwable.getMessage();
                Toast.makeText(SignupCaregiverActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}