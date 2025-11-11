package itca.soft.renalcare.data.models;

// NOTA: Esta clase NO necesita ser Parcelable,
// porque nunca se pasa en un 'Bundle'
public class TodayReminderStatus {
    private int id_recordatorio;
    private int id_medicamento;
    private String estado;

    // Constructor vacío (buena práctica para GSON/Retrofit)
    public TodayReminderStatus() {}

    // Getters
    public int getId_recordatorio() {
        return id_recordatorio;
    }

    public int getId_medicamento() {
        return id_medicamento;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}