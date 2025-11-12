package itca.soft.renalcare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import itca.soft.renalcare.ui.websoket.ChatListFragment;
import itca.soft.renalcare.data.network.WebSocketService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Conectar WebSocket
            WebSocketService wsService = WebSocketService.getInstance();
            if (!wsService.isConnected()) {
                wsService.connect();
                wsService.registerUser("2"); // Usuario logueado
            }

            // Abrir ChatListFragment (lista de conversaciones)
            ChatListFragment listFragment = new ChatListFragment();
            Bundle args = new Bundle();
            args.putString("id_usuario", "2");
            args.putString("nombre_usuario", "Dr. Juan Pérez");
            listFragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, listFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}