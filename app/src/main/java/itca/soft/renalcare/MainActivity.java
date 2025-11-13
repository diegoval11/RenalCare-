package itca.soft.renalcare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import itca.soft.renalcare.ui.chat.ChatFragment;
import itca.soft.renalcare.ui.voice.VoiceChatFragment;
import itca.soft.renalcare.ui.doctores.GestionPacientesDoctorActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OPCIÓN 1: Para testear - Abre directamente GestionPacientesDoctorActivity
        // Descomenta esto si quieres ver la gestión de pacientes primero

        Intent intent = new Intent(this, GestionPacientesDoctorActivity.class);
        intent.putExtra("id_doctor", 1); // Cambia 1 por el ID del doctor logueado
        startActivity(intent);
        finish();
        return;

/*
        // OPCIÓN 2: Con Navigation Graph (recomendado para producción)
        // 1. Encontrar los componentes del layout
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
        NavigationUI.setupWithNavController(bottomNavView, navController);

 */
    }

    // Necesario para que el botón "atrás" de la toolbar funcione
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment)).getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}