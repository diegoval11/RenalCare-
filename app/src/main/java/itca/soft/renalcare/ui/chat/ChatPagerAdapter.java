package itca.soft.renalcare.ui.chat;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import itca.soft.renalcare.ui.websoket.HumanChatListFragment; // ¡Importante!

public class ChatPagerAdapter extends FragmentStateAdapter {

    private final int idUsuario;
    private final String nombreUsuario;

    public ChatPagerAdapter(@NonNull Fragment fragment, int idUsuario, String nombreUsuario) {
        super(fragment);
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            // Pestaña 1: Chat de IA
            return new AiChatFragment(); // Usará SharedPreferences para el ID
        } else {
            // Pestaña 2: Chat Humano
            // Pasamos los datos del usuario al fragmento
            Bundle args = new Bundle();
            args.putString("id_usuario", String.valueOf(idUsuario));
            args.putString("nombre_usuario", nombreUsuario);

            HumanChatListFragment fragment = new HumanChatListFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Dos pestañas: IA y Humano
    }
}