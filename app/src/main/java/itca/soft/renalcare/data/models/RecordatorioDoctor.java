package itca.soft.renalcare.data.models;


public class RecordatorioDoctor {
    private int id_recordatorio;
    private int id_paciente;
    private int id_medicamento;
    private String titulo;
    private String descripcion;
    private String fecha_hora;
    private String tipo;
    private String estado;

    public RecordatorioDoctor() {}

    public RecordatorioDoctor(int id_paciente, String titulo, String descripcion, String fecha_hora, String tipo) {
        this.id_paciente = id_paciente;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha_hora = fecha_hora;
        this.tipo = tipo;
    }

    public int getId_recordatorio() { return id_recordatorio; }
    public void setId_recordatorio(int id_recordatorio) { this.id_recordatorio = id_recordatorio; }
    public int getId_paciente() { return id_paciente; }
    public void setId_paciente(int id_paciente) { this.id_paciente = id_paciente; }
    public int getId_medicamento() { return id_medicamento; }
    public void setId_medicamento(int id_medicamento) { this.id_medicamento = id_medicamento; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFecha_hora() { return fecha_hora; }
    public void setFecha_hora(String fecha_hora) { this.fecha_hora = fecha_hora; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}