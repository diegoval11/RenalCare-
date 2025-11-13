package itca.soft.renalcare.data.models;
import com.google.gson.annotations.SerializedName;
public class Dialisis {
    @SerializedName("id_dialisis") private int idDialisis;
    @SerializedName("tipo") private String tipo;
    @SerializedName("fecha") private String fecha;
    // ... Getters
}