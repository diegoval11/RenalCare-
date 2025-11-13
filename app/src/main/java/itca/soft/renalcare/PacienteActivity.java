// Ubicación: PacienteActivity.java
// (Anteriormente MainActivity.java)
package itca.soft.renalcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
// NO MÁS FragmentTransaction (a menos que la uses para otra cosa)
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import itca.soft.renalcare.auth.LoginActivity;
// NO MÁS imports de ChatFragment/VoiceChatFragment aquí
import itca.soft.renalcare.ui.MainViewModel;


// ¡CAMBIO DE NOMBRE!
public class PacienteActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ¡CAMBIO DE LAYOUT!
        setContentView(R.layout.activity_paciente); // Infla el nuevo layout

        // --- INICIO DE LÓGICA DE VIEWMODEL (Sin cambios) ---
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

        // Usamos la clave hardcodeada que ya tenías
        int idUsuarioLogueado = prefs.getInt("id_usuario", -1);

        if (idUsuarioLogueado != -1) {
            mainViewModel.cargarDatosPaciente(idUsuarioLogueado);
        } else {
            Toast.makeText(this, "Error de sesión: ID no encontrado.", Toast.LENGTH_LONG).show();
        }
        // --- FIN DE LÓGICA DE VIEWMODEL ---


        // 1. Encontrar los componentes del layout
        // (Los IDs son los mismos que definiste en activity_main.xml)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation_view);

        // 2. Obtener el NavHostFragment y el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment); // ID sigue siendo el mismo
        NavController navController = navHostFragment.getNavController();

        // 3. Conectar la Toolbar
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController);

        // 4. Conectar el BottomNavigationView
        NavigationUI.setupWithNavController(bottomNavView, navController);
    }

    // (Sin cambios)
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}