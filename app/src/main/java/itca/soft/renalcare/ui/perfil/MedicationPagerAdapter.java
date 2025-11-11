package itca.soft.renalcare.ui.perfil;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import itca.soft.renalcare.data.models.MedicationItem;
import itca.soft.renalcare.data.models.TodayReminderStatus;

public class MedicationPagerAdapter extends FragmentStateAdapter {

    private final List<List<MedicationItem>> medicationPages = new ArrayList<>();
    private final Map<Integer, TodayReminderStatus> statusMap;
    private final int PAGE_SIZE = 3; // <-- Muestra 3 medicamentos por página

    public MedicationPagerAdapter(@NonNull Fragment fragment, Map<Integer, TodayReminderStatus> statusMap) {
        super(fragment);
        this.statusMap = statusMap;
    }

    public void setMedicationList(List<MedicationItem> medicationList) {
        medicationPages.clear();
        for (int i = 0; i < medicationList.size(); i += PAGE_SIZE) {
            int end = Math.min(i + PAGE_SIZE, medicationList.size());
            // medicationList.subList(i, end) podría no ser una ArrayList.
            // Al hacer 'new ArrayList<>(...)', nos aseguramos de que SÍ lo sea.
            medicationPages.add(new ArrayList<>(medicationList.subList(i, end))); // <-- Esto ya es correcto
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MedicationPageFragment.newInstance(medicationPages.get(position), statusMap);
    }

    @Override
    public int getItemCount() {
        return medicationPages.size();
    }
}