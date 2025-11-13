// Ubicación: data/models/PacienteInfoResponse.java
package itca.soft.renalcare.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Clase principal que recibe toda la respuesta
public class PacienteInfoResponse {

    @SerializedName("id_usuario")
    private int idUsuario;
    @SerializedName("nombre_paciente")
    private String nombrePaciente;
    @SerializedName("dui")
    private String dui;
    @SerializedName("tipo")
    private int tipo;
    @SerializedName("id_paciente")
    private int idPaciente;
    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento;
    @SerializedName("genero")
    private String genero;
    @SerializedName("tipo_tratamiento")
    private String tipoTratamiento;
    @SerializedName("peso")
    private double peso; // Usar double
    @SerializedName("nivel_creatinina")
    private double nivelCreatinina; // Usar double
    @SerializedName("sintomas")
    private String sintomas;
    @SerializedName("observaciones")
    private String observaciones;
    @SerializedName("telefono_emergencia")
    private String telefonoEmergencia;
    @SerializedName("contacto_emergencia")
    private String contactoEmergencia;
    @SerializedName("condicion_renal")
    private String condicionRenal;

    // --- Objetos Anidados ---
    @SerializedName("doctor")
    private Doctor doctor;
    @SerializedName("cuidador")
    private Cuidador cuidador;

    // --- Listas Anidadas ---
    @SerializedName("dietas")
    private List<Dieta> dietas;
    @SerializedName("medicamentos")
    private List<Medicamento> medicamentos; // Modelo Medicamento (diferente de MedicationItem)
    @SerializedName("dialisis")
    private List<Dialisis> dialisis;
    @SerializedName("signos_vitales")
    private List<SignoVital> signosVitales;
    @SerializedName("recordatorios")
    private List<Recordatorio> recordatorios;

    // --- Getters ---
    // (Añadí todos los getters que tu PerfilFragment necesita)

    public int getIdUsuario() { return idUsuario; }
    public String getNombrePaciente() { return nombrePaciente; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public double getPeso() { return peso; }
    public double getNivelCreatinina() { return nivelCreatinina; }

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    // Estos dos métodos faltaban:
    public String getTipoTratamiento() {
        return tipoTratamiento;
    }

    public String getCondicionRenal() {
        return condicionRenal;
    }
    // --- Fin de la corrección ---

    public Doctor getDoctor() { return doctor; }
    public Cuidador getCuidador() { return cuidador; }
    public List<Medicamento> getMedicamentos() { return medicamentos; }
    public List<Recordatorio> getRecordatorios() { return recordatorios; }
}