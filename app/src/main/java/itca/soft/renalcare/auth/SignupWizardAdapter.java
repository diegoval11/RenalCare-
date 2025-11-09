package itca.soft.renalcare.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import itca.soft.renalcare.auth.steps.Step1AccountFragment;
import itca.soft.renalcare.auth.steps.Step2PersonalFragment;


import itca.soft.renalcare.auth.steps.Step3MedicalFragment;
import itca.soft.renalcare.auth.steps.Step4TreatmentFragment;
import itca.soft.renalcare.auth.steps.Step5ReviewFragment;

public class SignupWizardAdapter extends FragmentStateAdapter {

    // El número total de pasos
    private static final int NUM_STEPS = 5;

    public SignupWizardAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Step1AccountFragment();
            case 1:
                return new Step2PersonalFragment();
            case 2:
                return new Step3MedicalFragment();
            case 3:
                return new Step4TreatmentFragment();
            case 4:
                return new Step5ReviewFragment();
            default:
                return new Step1AccountFragment(); // Fallback
        }
    }

    @Override
    public int getItemCount() {
        return NUM_STEPS;
    }
}