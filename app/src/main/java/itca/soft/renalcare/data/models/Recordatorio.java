package itca.soft.renalcare.data.models;
import com.google.gson.annotations.SerializedName;
public class Recordatorio {
    @SerializedName("id_recordatorio") private int idRecordatorio;
    @SerializedName("titulo") private String titulo;
    @SerializedName("estado") private String estado;
    // ... Getters
}