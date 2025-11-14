package itca.soft.renalcare.data.models;

public class MedicamentoDoctor {
    private int id_medicamento;
    private int id_paciente;
    private String nombre;
    private String dosis;
    private String horario;
    private String notas;
    private String dias_semana;

    public MedicamentoDoctor() {}

    public MedicamentoDoctor(int id_paciente, String nombre, String dosis, String horario, String notas, String dias_semana) {
        this.id_paciente = id_paciente;
        this.nombre = nombre;
        this.dosis = dosis;
        this.horario = horario;
        this.notas = notas;
        this.dias_semana = dias_semana;
    }

    public int getId_medicamento() { return id_medicamento; }
    public void setId_medicamento(int id_medicamento) { this.id_medicamento = id_medicamento; }
    public int getId_paciente() { return id_paciente; }
    public void setId_paciente(int id_paciente) { this.id_paciente = id_paciente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public String getDias_semana() { return dias_semana; }
    public void setDias_semana(String dias_semana) { this.dias_semana = dias_semana; }
}
