package itca.soft.renalcare.data.network;

public class UpdateStatusBody {
    String estado;
    int id_paciente;

    public UpdateStatusBody(String estado, int id_paciente) {
        this.estado = estado;
        this.id_paciente = id_paciente;
    }
}