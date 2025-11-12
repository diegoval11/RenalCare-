package itca.soft.renalcare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction; // Se mantiene por si acaso
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import itca.soft.renalcare.ui.chat.ChatFragment;
import itca.soft.renalcare.ui.voice.VoiceChatFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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