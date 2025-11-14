package itca.soft.renalcare.ui.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import itca.soft.renalcare.R;
import itca.soft.renalcare.auth.LoginActivity;

public class ChatFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el NUEVO layout con pestañas
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.chat_view_pager);
        tabLayout = view.findViewById(R.id.chat_tab_layout);

        // Obtener los datos del usuario logueado
        SharedPreferences prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt(LoginActivity.KEY_ID_USUARIO, -1);
        String nombreUsuario = prefs.getString(LoginActivity.KEY_NOMBRE_USUARIO, "Usuario");

        // Configurar el adaptador para el ViewPager
        ChatPagerAdapter adapter = new ChatPagerAdapter(this, idUsuario, nombreUsuario);
        viewPager.setAdapter(adapter);

        // Conectar el TabLayout con el ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Chat IA");
                // (Opcional: puedes añadir un ícono)
                // tab.setIcon(R.drawable.ic_karito); 
            } else {
                tab.setText("Chat Humano");
                // (Opcional: puedes añadir un ícono)
                // tab.setIcon(R.drawable.ic_chat);
            }
        }).attach();
    }
}