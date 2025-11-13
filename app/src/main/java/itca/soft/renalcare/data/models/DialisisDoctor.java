package itca.soft.renalcare.data.models;
public class DialisisDoctor {
    private int id_dialisis;
    private int id_paciente;
    private String tipo;
    private String fecha;
    private String hora;
    private String observaciones;

    public DialisisDoctor() {}

    public DialisisDoctor(int id_paciente, String tipo, String fecha, String hora, String observaciones) {
        this.id_paciente = id_paciente;
        this.tipo = tipo;
        this.fecha = fecha;
        this.hora = hora;
        this.observaciones = observaciones;
    }

    public int getId_dialisis() { return id_dialisis; }
    public void setId_dialisis(int id_dialisis) { this.id_dialisis = id_dialisis; }
    public int getId_paciente() { return id_paciente; }
    public void setId_paciente(int id_paciente) { this.id_paciente = id_paciente; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}