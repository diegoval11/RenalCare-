package itca.soft.renalcare.data.models;
import com.google.gson.annotations.SerializedName;
public class SignoVital {
    @SerializedName("id_signo") private int idSigno;
    @SerializedName("presion_sistolica") private float presionSistolica;
    @SerializedName("presion_diastolica") private float presionDiastolica;
    @SerializedName("peso") private float peso;
}