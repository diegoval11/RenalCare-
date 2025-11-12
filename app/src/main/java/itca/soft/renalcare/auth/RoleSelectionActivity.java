package itca.soft.renalcare.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import itca.soft.renalcare.R;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        Button btnGoToPatient = findViewById(R.id.btnGoToPatient);
        Button btnGoToCaregiver = findViewById(R.id.btnGoToCaregiver);

        btnGoToPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoleSelectionActivity.this, SignupPatientActivity.class));
            }
        });

        btnGoToCaregiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoleSelectionActivity.this, SignupCaregiverActivity.class));
            }
        });
    }
}