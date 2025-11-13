package itca.soft.renalcare.adapters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import itca.soft.renalcare.ui.doctores.DialisisFragment;
import itca.soft.renalcare.ui.doctores.DietasFragment;
import itca.soft.renalcare.ui.doctores.EstadisticasFragment;
import itca.soft.renalcare.ui.doctores.InformacionPacienteFragment;
import itca.soft.renalcare.ui.doctores.MedicamentosFragment;
import itca.soft.renalcare.ui.doctores.RecordatoriosFragment;
import itca.soft.renalcare.ui.doctores.SignosVitalesFragment;

public class DetallesPacienteAdapter extends FragmentStateAdapter {

    private int id_paciente;
    private int id_doctor;

    public DetallesPacienteAdapter(AppCompatActivity activity, int id_paciente, int id_doctor) {
        super(activity);
        this.id_paciente = id_paciente;
        this.id_doctor = id_doctor;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return InformacionPacienteFragment.newInstance(id_paciente);
            case 1:
                return DietasFragment.newInstance(id_paciente);
            case 2:
                return MedicamentosFragment.newInstance(id_paciente);
            case 3:
                return DialisisFragment.newInstance(id_paciente);
            case 4:
                return SignosVitalesFragment.newInstance(id_paciente);
            case 5:
                return RecordatoriosFragment.newInstance(id_paciente);
            case 6:
                return EstadisticasFragment.newInstance(id_paciente);
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 7; // 7 tabs
    }
}