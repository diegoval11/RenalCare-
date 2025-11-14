package itca.soft.renalcare.data.models;
public class SignosVitalesDoctor {
    private int id_signo;
    private int id_paciente;
    private int presion_sistolica;
    private int presion_diastolica;
    private int frecuencia_cardiaca;
    private double peso;
    private String fecha_registro;

    public SignosVitalesDoctor() {}

    public SignosVitalesDoctor(int id_paciente, int presion_sistolica, int presion_diastolica, int frecuencia_cardiaca, double peso) {
        this.id_paciente = id_paciente;
        this.presion_sistolica = presion_sistolica;
        this.presion_diastolica = presion_diastolica;
        this.frecuencia_cardiaca = frecuencia_cardiaca;
        this.peso = peso;
    }

    public int getId_signo() { return id_signo; }
    public void setId_signo(int id_signo) { this.id_signo = id_signo; }
    public int getId_paciente() { return id_paciente; }
    public void setId_paciente(int id_paciente) { this.id_paciente = id_paciente; }
    public int getPresion_sistolica() { return presion_sistolica; }
    public void setPresion_sistolica(int presion_sistolica) { this.presion_sistolica = presion_sistolica; }
    public int getPresion_diastolica() { return presion_diastolica; }
    public void setPresion_diastolica(int presion_diastolica) { this.presion_diastolica = presion_diastolica; }
    public int getFrecuencia_cardiaca() { return frecuencia_cardiaca; }
    public void setFrecuencia_cardiaca(int frecuencia_cardiaca) { this.frecuencia_cardiaca = frecuencia_cardiaca; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public String getFecha_registro() { return fecha_registro; }
    public void setFecha_registro(String fecha_registro) { this.fecha_registro = fecha_registro; }
}
