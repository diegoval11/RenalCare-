// Ubicación: CuidadorActivity.java (Archivo Nuevo)
package itca.soft.renalcare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CuidadorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Infla el layout placeholder para el cuidador
        setContentView(R.layout.activity_cuidador);
    }
}