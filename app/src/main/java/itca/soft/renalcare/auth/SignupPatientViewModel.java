package itca.soft.renalcare.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SignupPatientViewModel extends ViewModel {

    // --- Paso 1: Cuenta ---
    public final MutableLiveData<String> nombre = new MutableLiveData<>("");
    public final MutableLiveData<String> dui = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");
    public final MutableLiveData<String> telefono = new MutableLiveData<>("");


    // --- Paso 2: Personal ---
    public final MutableLiveData<String> fechaNacimiento = new MutableLiveData<>(""); // Guardar como "YYYY-MM-DD"
    public final MutableLiveData<String> genero = new MutableLiveData<>("");
    public final MutableLiveData<String> contactoNombre = new MutableLiveData<>("");
    public final MutableLiveData<String> contactoTelefono = new MutableLiveData<>("");

    // --- Paso 3: Médico ---
    public final MutableLiveData<String> condicionRenal = new MutableLiveData<>(); // "cronica", "aguda", "no_se"
    public final MutableLiveData<String> tipoTratamiento = new MutableLiveData<>(); // "hemodialisis", "dialisis", "trasplante", "otro", "no_se"
    public final MutableLiveData<String> peso = new MutableLiveData<>("");
    public final MutableLiveData<Boolean> pesoOmitido = new MutableLiveData<>(false);
    public final MutableLiveData<String> creatinina = new MutableLiveData<>("");
    public final MutableLiveData<Boolean> creatininaOmitida = new MutableLiveData<>(false);

    // --- Paso 4: Tratamiento ---
    public final MutableLiveData<List<Medicamento>> medicamentos = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<Boolean> medicamentosOmitidos = new MutableLiveData<>(false);

    // Usamos un Set para manejar fácilmente las selecciones de dieta
    public final MutableLiveData<Set<String>> dieta = new MutableLiveData<>(new HashSet<>());

}