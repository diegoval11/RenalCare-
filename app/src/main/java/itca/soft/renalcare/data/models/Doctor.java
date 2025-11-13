package itca.soft.renalcare.data.models;
import com.google.gson.annotations.SerializedName;
public class Doctor {
    @SerializedName("id_doctor") private int idDoctor;
    @SerializedName("nombre") private String nombre;
    @SerializedName("especialidad") private String especialidad;
    // ... Getters
}