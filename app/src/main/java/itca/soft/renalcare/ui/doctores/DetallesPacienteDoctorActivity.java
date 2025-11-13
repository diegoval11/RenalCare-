package itca.soft.renalcare.ui.doctores;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import itca.soft.renalcare.R;
import itca.soft.renalcare.adapters.DetallesPacienteAdapter;

public class DetallesPacienteDoctorActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private int id_paciente;
    private int id_doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_paciente_doctor);

        id_paciente = getIntent().getIntExtra("id_paciente", 2);
        id_doctor = getIntent().getIntExtra("id_doctor", 1);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        DetallesPacienteAdapter adapter = new DetallesPacienteAdapter(this, id_paciente, id_doctor);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Información");
                    break;
                case 1:
                    tab.setText("Dietas");
                    break;
                case 2:
                    tab.setText("Medicamentos");
                    break;
                case 3:
                    tab.setText("Diálisis");
                    break;
                case 4:
                    tab.setText("Signos Vitales");
                    break;
                case 5:
                    tab.setText("Recordatorios");
                    break;
                case 6:
                    tab.setText("Estadísticas");
                    break;
            }
        }).attach();
    }
}