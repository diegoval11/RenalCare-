// 📁 data/models/Conversacion.java
package itca.soft.renalcare.data.models;

import java.util.List;

public class Conversacion {
    private String id_conversacion;
    private String tipo;
    private String nombre_grupo;
    private String fecha_creacion;
    private String participantes;
    private String ultimo_mensaje;
    private String timestamp_ultimo;
    private int cantidad_no_leidos;

    public Conversacion(String id_conversacion, String tipo, String nombre_grupo,
                        String fecha_creacion, String participantes) {
        this.id_conversacion = id_conversacion;
        this.tipo = tipo;
        this.nombre_grupo = nombre_grupo;
        this.fecha_creacion = fecha_creacion;
        this.participantes = participantes;
        this.cantidad_no_leidos = 0;
    }

    // Getters y Setters
    public String getId_conversacion() { return id_conversacion; }
    public String getTipo() { return tipo; }
    public String getNombre_grupo() { return nombre_grupo; }
    public String getFecha_creacion() { return fecha_creacion; }
    public String getParticipantes() { return participantes; }
    public String getUltimo_mensaje() { return ultimo_mensaje; }
    public void setUltimo_mensaje(String ultimo_mensaje) { this.ultimo_mensaje = ultimo_mensaje; }
    public String getTimestamp_ultimo() { return timestamp_ultimo; }
    public void setTimestamp_ultimo(String timestamp_ultimo) { this.timestamp_ultimo = timestamp_ultimo; }
    public int getCantidad_no_leidos() { return cantidad_no_leidos; }
    public void setCantidad_no_leidos(int cantidad_no_leidos) { this.cantidad_no_leidos = cantidad_no_leidos; }
}