package itca.soft.renalcare.data.models;
import com.google.gson.annotations.SerializedName;
public class Medicamento {
    @SerializedName("id_medicamento") private int idMedicamento;
    @SerializedName("nombre") private String nombre;
    @SerializedName("dosis") private String dosis;
    @SerializedName("horario") private String horario;
    @SerializedName("notas") private String notas;
    // ... Getters
    public String getNombre() { return nombre; }
    public String getDosis() { return dosis; }
    public String getHorario() { return horario; }
    public String getNotas() { return notas; }
}