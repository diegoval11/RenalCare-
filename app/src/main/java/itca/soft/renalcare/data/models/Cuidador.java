package itca.soft.renalcare.data.models;
import com.google.gson.annotations.SerializedName;
public class Cuidador {
    @SerializedName("id_cuidador") private int idCuidador;
    @SerializedName("nombre") private String nombre;
    @SerializedName("relacion") private String relacion;
    // ... Getters
}