package itca.soft.renalcare.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ReminderItem implements Parcelable {

    private int id_recordatorio;
    private int id_medicamento;
    private int id_paciente;
    private String titulo;
    private String descripcion;
    private String fecha_hora;
    private String tipo;
    private String estado;

    // --- ▼▼▼ AÑADIR ESTE CONSTRUCTOR VACÍO ▼▼▼ ---
    // Constructor vacío (necesario para Retrofit/Gson y para la herencia)
    public ReminderItem() {}
    // --- ▲▲▲ FIN ▲▲▲ ---

    // Getters
    public int getIdRecordatorio() { return id_recordatorio; }
    public int getIdMedicamento() { return id_medicamento; }
    public int getIdPaciente() { return id_paciente; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getFechaHora() { return fecha_hora; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }


    // --- Bloque Parcelable (Sin cambios) ---
    protected ReminderItem(Parcel in) {
        id_recordatorio = in.readInt();
        id_medicamento = in.readInt();
        id_paciente = in.readInt();
        titulo = in.readString();
        descripcion = in.readString();
        fecha_hora = in.readString();
        tipo = in.readString();
        estado = in.readString();
    }

    public static final Creator<ReminderItem> CREATOR = new Creator<ReminderItem>() {
        @Override
        public ReminderItem createFromParcel(Parcel in) {
            return new ReminderItem(in);
        }

        @Override
        public ReminderItem[] newArray(int size) {
            return new ReminderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_recordatorio);
        dest.writeInt(id_medicamento);
        dest.writeInt(id_paciente);
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeString(fecha_hora);
        dest.writeString(tipo);
        dest.writeString(estado);
    }
}