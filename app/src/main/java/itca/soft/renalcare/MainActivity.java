// Ubicación: MainActivity.java
package itca.soft.renalcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import itca.soft.renalcare.auth.LoginActivity;
import itca.soft.renalcare.ui.chat.ChatFragment;
import itca.soft.renalcare.ui.MainViewModel;
import itca.soft.renalcare.ui.voice.VoiceChatFragment;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel; // ¡NUEVA VARIABLE!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- INICIO DE LÓGICA DE VIEWMODEL (AÑADIDO) ---

        // 1. Inicializar el ViewModel compartido
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 2. Obtener el ID del usuario desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        // Usamos -1 como valor por defecto si no se encuentra el ID
        int idUsuarioLogueado = prefs.getInt("id_usuario", -1);

        // 3. Llamar al ViewModel para cargar los datos
        if (idUsuarioLogueado != -1) {
            mainViewModel.cargarDatosPaciente(idUsuarioLogueado);
        } else {
            // Manejar error: No se encontró ID.
            Toast.makeText(this, "Error de sesión: ID no encontrado.", Toast.LENGTH_LONG).show();
            // Aquí podrías redirigir al LoginActivity
        }

        // --- FIN DE LÓGICA DE VIEWMODEL ---


        // 1. Encontrar los componentes del layout
        // (Asegúrate de que tus IDs en activity_main.xml coincidan)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation_view);

        // 2. Obtener el NavHostFragment y el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // 3. Conectar la Toolbar
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController);

        // 4. Conectar el BottomNavigationView
        // Esto maneja los clics de la barra inferior
        NavigationUI.setupWithNavController(bottomNavView, navController);
    }



    // Necesario para que el botón "atrás" de la toolbar funcione
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}