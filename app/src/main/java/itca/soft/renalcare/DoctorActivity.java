// Ubicación: DoctorActivity.java (Archivo Nuevo)
package itca.soft.renalcare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DoctorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Simplemente infla el layout placeholder
        setContentView(R.layout.activity_doctor);
    }
}