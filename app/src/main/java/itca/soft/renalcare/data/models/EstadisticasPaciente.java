package itca.soft.renalcare.data.models;

import java.util.List;

public class EstadisticasPaciente {
    private PacienteDoctor paciente;
    private List<SignosVitalesDoctor> signos_vitales;
    private int total_sesiones_dialisis;
    private int total_medicamentos;
    private PresionPromedio presion_promedio;
    private PesoPromedio peso_promedio;
    private String mensaje;

    public EstadisticasPaciente() {}

    public PacienteDoctor getPaciente() { return paciente; }
    public void setPaciente(PacienteDoctor paciente) { this.paciente = paciente; }
    public List<SignosVitalesDoctor> getSignos_vitales() { return signos_vitales; }
    public void setSignos_vitales(List<SignosVitalesDoctor> signos_vitales) { this.signos_vitales = signos_vitales; }
    public int getTotal_sesiones_dialisis() { return total_sesiones_dialisis; }
    public void setTotal_sesiones_dialisis(int total_sesiones_dialisis) { this.total_sesiones_dialisis = total_sesiones_dialisis; }
    public int getTotal_medicamentos() { return total_medicamentos; }
    public void setTotal_medicamentos(int total_medicamentos) { this.total_medicamentos = total_medicamentos; }
    public PresionPromedio getPresion_promedio() { return presion_promedio; }
    public void setPresion_promedio(PresionPromedio presion_promedio) { this.presion_promedio = presion_promedio; }
    public PesoPromedio getPeso_promedio() { return peso_promedio; }
    public void setPeso_promedio(PesoPromedio peso_promedio) { this.peso_promedio = peso_promedio; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public static class PresionPromedio {
        public Double presion_sistolica_promedio;
        public Double presion_diastolica_promedio;
    }

    public static class PesoPromedio {
        public Double peso_promedio;
    }
}