package itca.soft.renalcare.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MedicationItem implements Parcelable {

    private int id_medicamento;
    private int id_paciente;
    private String nombre;
    private String dosis;
    private String horario;
    private String notas;

    // --- ▼▼▼ CAMBIO 1: AÑADIR ESTE CAMPO ▼▼▼ ---
    private String dias_semana;
    // --- ▲▲▲ FIN CAMBIO 1 ▲▲▲ ---

    // Constructor vacío (necesario para Retrofit/Gson)
    public MedicationItem() {}

    // --- ▼▼▼ CAMBIO 2: ACTUALIZAR EL CONSTRUCTOR ▼▼▼ ---
    public MedicationItem(int id_paciente, String nombre, String dosis, String horario, String notas, String dias_semana) {
        this.id_paciente = id_paciente;
        this.nombre = nombre;
        this.dosis = dosis;
        this.horario = horario;
        this.notas = notas;
        this.dias_semana = dias_semana; // <-- Añadido
    }
    // --- ▲▲▲ FIN CAMBIO 2 ▲▲▲ ---

    // Getters
    public int getId_medicamento() { return id_medicamento; }
    public int getId_paciente() { return id_paciente; }
    public String getNombre() { return nombre; }
    public String getDosis() { return dosis; }
    public String getHorario() { return horario; }
    public String getNotas() { return notas; }
    public String getDias_semana() { return dias_semana; } // <-- Añadido


    // --- ▼▼▼ CAMBIO 3: ACTUALIZAR PARCELABLE ▼▼▼ ---
    protected MedicationItem(Parcel in) {
        id_medicamento = in.readInt();
        id_paciente = in.readInt();
        nombre = in.readString();
        dosis = in.readString();
        horario = in.readString();
        notas = in.readString();
        dias_semana = in.readString(); // <-- Añadido
    }

    public static final Creator<MedicationItem> CREATOR = new Creator<MedicationItem>() {
        @Override
        public MedicationItem createFromParcel(Parcel in) {
            return new MedicationItem(in);
        }

        @Override
        public MedicationItem[] newArray(int size) {
            return new MedicationItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_medicamento);
        dest.writeInt(id_paciente);
        dest.writeString(nombre);
        dest.writeString(dosis);
        dest.writeString(horario);
        dest.writeString(notas);
        dest.writeString(dias_semana); // <-- Añadido
    }
    // --- ▲▲▲ FIN CAMBIO 3 ▲▲▲ ---
}