// Ubicación: PacienteActivity.java
package itca.soft.renalcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import itca.soft.renalcare.auth.LoginActivity;
import itca.soft.renalcare.data.network.WebSocketService;
import itca.soft.renalcare.ui.MainViewModel;

public class PacienteActivity extends AppCompatActivity {

    // --- ¡NUEVO! Claves públicas para el Intent ---
    /**
     * Clave para el Intent que indica el ID del paciente a cargar.
     * Usado por CuidadorActivity.
     */
    public static final String EXTRA_ID_PACIENTE = "itca.soft.renalcare.EXTRA_ID_PACIENTE";
    /**
     * Clave para el Intent (boolean) que indica si la actividad debe
     * iniciarse en modo de solo vista.
     */
    public static final String EXTRA_IS_VIEW_ONLY = "itca.soft.renalcare.EXTRA_IS_VIEW_ONLY";
    // ------------------------------------------------

    private MainViewModel mainViewModel;
    private BottomNavigationView bottomNavView; // Hacemos referencia global
    private boolean isViewOnly = false; // Flag local

    private WebSocketService wsService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        // --- INICIO DE LÓGICA DE VIEWMODEL (Modificada) ---
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

        // 1. Determinar si estamos en modo "Solo Vista"
        Intent intent = getIntent();
        isViewOnly = intent.getBooleanExtra(EXTRA_IS_VIEW_ONLY, false);

        // 2. Determinar qué ID de paciente cargar
        int idPacienteACargar;
        String nombrePacienteLogueado = ""; // ¡NUEVO!

        if (isViewOnly) {
            // Si es "Solo Vista", el ID VIENE DEL INTENT
            idPacienteACargar = intent.getIntExtra(EXTRA_ID_PACIENTE, -1);
        } else {
            // Si NO es "Solo Vista", es un Paciente logueado.
            // El ID VIENE DE SHAREDPREFERENCES
            idPacienteACargar = prefs.getInt(LoginActivity.KEY_ID_USUARIO, -1);
            nombrePacienteLogueado = prefs.getString(LoginActivity.KEY_NOMBRE_USUARIO, "Usuario");

        }

        // 3. Cargar datos en el ViewModel
        if (idPacienteACargar != -1) {
            // Pasamos AMBOS datos al ViewModel
            mainViewModel.cargarDatosPaciente(idPacienteACargar, isViewOnly);
        } else {
            Toast.makeText(this, "Error de sesión: ID no encontrado.", Toast.LENGTH_LONG).show();
        }

        if (!isViewOnly && idPacienteACargar != -1 && !TextUtils.isEmpty(nombrePacienteLogueado)) {
            // Solo conectar si es el paciente real (no un cuidador viendo)
            Log.d("PacienteActivity", "Iniciando WebSocketService...");
            wsService = WebSocketService.getInstance();
            if (!wsService.isConnected()) {
                wsService.connect();
                // Registramos al usuario en el socket
                wsService.registerUser(String.valueOf(idPacienteACargar));
            }
        }
        // --- FIN DE LÓGICA DE VIEWMODEL ---

        // 1. Encontrar los componentes del layout
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        bottomNavView = findViewById(R.id.bottom_navigation_view); // Usamos la referencia global

        // 2. Obtener el NavHostFragment y el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // 3. Conectar la Toolbar
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController);

        // 4. Conectar el BottomNavigationView
        NavigationUI.setupWithNavController(bottomNavView, navController);

        mainViewModel.getNavigateTo().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer destinationId) {
                if (destinationId != null) {
                    // Cuando el ViewModel pide navegar,
                    // le decimos al BottomNavigationView que cambie su ítem seleccionado.
                    bottomNavView.setSelectedItemId(destinationId);
                }
            }
        });


        // 5. ¡NUEVO! Ajustar UI para modo "Solo Vista"
        if (isViewOnly) {
            // Si es solo vista, deshabilitamos la navegación inferior
            // para que el cuidador no pueda cambiar de fragmento
            // (Opcional: podrías querer que sí navegue)

            // Ejemplo 1: Deshabilitar clics en el BottomNav
            // bottomNavView.setEnabled(false);

            // Ejemplo 2: Ocultar el BottomNav
            // bottomNavView.setVisibility(View.GONE);

            // Por ahora, lo dejamos visible pero los fragmentos
            // (ej. Perfil) reaccionarán al VM para ocultar botones.
            // Lo que sí haremos es cambiar el título:
            getSupportActionBar().setTitle("Viendo Perfil de Paciente");
        }
    }

    // (Sin cambios)


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wsService != null && !isViewOnly) {
            Log.d("PacienteActivity", "Desconectando WebSocketService...");
            wsService.disconnect();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    /**
     * ¡NUEVO!
     * Infla el menú de la Toolbar (si tienes uno).
     * Aquí es donde ocultamos el botón de "Cerrar Sesión"
     * si estamos en modo "Solo Vista".
     */






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Asumimos que tienes un menú (ej. res/menu/main_menu.xml)
        // getMenuInflater().inflate(R.menu.main_menu, menu);

        // (Si no tienes menú, esta función puede quedar vacía,
        // pero la lógica de abajo es como lo harías)

        return true; // Devuelve true para mostrar el menú
    }

    /**
     * ¡NUEVO!
     * Se llama justo antes de mostrar el menú.
     * Perfecto para ocultar/mostrar items dinámicamente.
     */



    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        // Asumimos que tu botón de logout tiene el id "action_logout"
        // MenuItem itemLogout = menu.findItem(R.id.action_logout);

        // if (itemLogout != null) {
        //     // Si es "Solo Vista", oculta el botón de logout
        //     itemLogout.setVisible(!isViewOnly);
        // }

        // (Descomenta lo anterior cuando tengas tu menú)

        return super.onPrepareOptionsMenu(menu);
    }
}


