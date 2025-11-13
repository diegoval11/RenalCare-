package itca.soft.renalcare.data.models;

public class DietaDoctor {
    private int id_dieta;
    private int id_paciente;
    private String descripcion;
    private String fecha_inicio;
    private String fecha_fin;

    public DietaDoctor() {}

    public DietaDoctor(int id_paciente, String descripcion, String fecha_inicio, String fecha_fin) {
        this.id_paciente = id_paciente;
        this.descripcion = descripcion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
    }

    public int getId_dieta() { return id_dieta; }
    public void setId_dieta(int id_dieta) { this.id_dieta = id_dieta; }
    public int getId_paciente() { return id_paciente; }
    public void setId_paciente(int id_paciente) { this.id_paciente = id_paciente; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFecha_inicio() { return fecha_inicio; }
    public void setFecha_inicio(String fecha_inicio) { this.fecha_inicio = fecha_inicio; }
    public String getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(String fecha_fin) { this.fecha_fin = fecha_fin; }
}