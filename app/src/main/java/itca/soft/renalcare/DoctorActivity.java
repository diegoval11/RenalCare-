package itca.soft.renalcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import itca.soft.renalcare.auth.LoginActivity;

public class DoctorActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

        // Verificar datos del doctor
        int idUsuarioLogueado = sharedPreferences.getInt(LoginActivity.KEY_ID_USUARIO, -1);
        if (idUsuarioLogueado == -1) {
            Toast.makeText(this, "Error de sesión: ID no encontrado.", Toast.LENGTH_LONG).show();
            cerrarSesion();
            return;
        }

        // Configurar Navigation Component
        setupNavigation();
    }

    private void setupNavigation() {
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment)).getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    // Método público para obtener datos del doctor desde los fragmentos
    public SharedPreferences getDoctorPreferences() {
        return sharedPreferences;
    }

    public int getDoctorId() {
        return sharedPreferences.getInt(LoginActivity.KEY_ID_USUARIO, -1);
    }

    public String getDoctorName() {
        return sharedPreferences.getString(LoginActivity.KEY_NOMBRE_USUARIO, "Doctor");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            cerrarSesion();
            return true;
        } else if (id == R.id.action_profile) {
            // Abrir perfil del doctor
            Toast.makeText(this, "Perfil próximamente", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {
        // Limpiar SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Volver al login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}