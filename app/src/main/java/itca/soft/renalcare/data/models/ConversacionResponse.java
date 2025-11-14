package itca.soft.renalcare.data.models;

import com.google.gson.annotations.SerializedName;

public class ConversacionResponse {

    @SerializedName("id_conversacion")
    private int id_conversacion;

    @SerializedName("tipo_conversacion")
    private String tipo_conversacion;

    @SerializedName("nombre_grupo")
    private String nombre_grupo;

    @SerializedName("fecha_creacion")
    private String fecha_creacion;

    @SerializedName("participantes")
    private String participantes;

    @SerializedName("ultima_fecha")
    private String ultima_fecha;

    @SerializedName("ultimo_mensaje")
    private String ultimo_mensaje;

    // Constructor
    public ConversacionResponse() {}

    // Getters y Setters
    public int getId_conversacion() { return id_conversacion; }
    public void setId_conversacion(int id_conversacion) { this.id_conversacion = id_conversacion; }

    public String getTipo_conversacion() { return tipo_conversacion; }
    public void setTipo_conversacion(String tipo_conversacion) { this.tipo_conversacion = tipo_conversacion; }

    public String getNombre_grupo() { return nombre_grupo; }
    public void setNombre_grupo(String nombre_grupo) { this.nombre_grupo = nombre_grupo; }

    public String getFecha_creacion() { return fecha_creacion; }
    public void setFecha_creacion(String fecha_creacion) { this.fecha_creacion = fecha_creacion; }

    public String getParticipantes() { return participantes; }
    public void setParticipantes(String participantes) { this.participantes = participantes; }

    public String getUltima_fecha() { return ultima_fecha; }
    public void setUltima_fecha(String ultima_fecha) { this.ultima_fecha = ultima_fecha; }

    public String getUltimo_mensaje() { return ultimo_mensaje; }
    public void setUltimo_mensaje(String ultimo_mensaje) { this.ultimo_mensaje = ultimo_mensaje; }

    // Convertir a Conversacion para compatibilidad
    public Conversacion toConversacion() {
        Conversacion conv = new Conversacion(
                String.valueOf(id_conversacion),
                tipo_conversacion,
                nombre_grupo,
                fecha_creacion,
                participantes
        );
        conv.setUltimo_mensaje(ultimo_mensaje);
        conv.setTimestamp_ultimo(ultima_fecha);
        return conv;
    }
}