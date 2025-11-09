package itca.soft.renalcare.auth;

// Un objeto simple para guardar los datos de un medicamento
public class Medicamento {
    public String nombre;
    public String dosis;
    public String horario;

    public Medicamento(String nombre, String dosis, String horario) {
        this.nombre = nombre;
        this.dosis = dosis;
        this.horario = horario;
    }
}