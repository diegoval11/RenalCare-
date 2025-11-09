    package itca.soft.renalcare;

    import android.os.Bundle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.navigation.NavController;
    import androidx.navigation.fragment.NavHostFragment;
    import androidx.navigation.ui.NavigationUI;
    import com.google.android.material.appbar.MaterialToolbar;
    import com.google.android.material.bottomnavigation.BottomNavigationView;

    import itca.soft.renalcare.ui.chat.ChatFragment;

    public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            if (savedInstanceState == null) {
                ChatFragment chatFragment = new ChatFragment();
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(android.R.id.content, chatFragment);
                transaction.commit();
            }
            /*
            // 1. Encontrar los componentes del layout
            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation_view);

            // 2. Obtener el NavHostFragment y el NavController
            // Esto es el "cerebro" que maneja el cambio de fragmentos
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHostFragment.getNavController();

            // 3. Conectar la Toolbar
            setSupportActionBar(toolbar); // Establece la toolbar como la barra de acción
            NavigationUI.setupActionBarWithNavController(this, navController);

            // 4. Conectar el BottomNavigationView
            // ¡Esta línea hace toda la magia!
            // Automáticamente maneja los clics y cambia al fragmento correcto
            NavigationUI.setupWithNavController(bottomNavView, navController);*/
        }


/*
        // Necesario para que el botón "atrás" de la toolbar funcione
        @Override
        public boolean onSupportNavigateUp() {
            NavController navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
            return navController.navigateUp() || super.onSupportNavigateUp();
        }*/
    }