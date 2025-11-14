package itca.soft.renalcare.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para un solo mensaje en el chat humano.
 * Coincide con la tabla 'mensajes' y la respuesta de 'getMensajes'.
 */
public class Mensaje {

    @SerializedName("id_mensaje")
    private String id_mensaje;

    @SerializedName("id_conversacion")
    private String id_conversacion;

    @SerializedName("id_emisor")
    private String id_emisor;

    @SerializedName("contenido")
    private String contenido;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("fecha_envio")
    private String fecha_envio;

    @SerializedName("nombre_emisor")
    private String nombre_emisor;

    // --- ¡NUEVO CAMPO! ---
    // Este campo es "transient" (no se serializa por GSON)
    // y solo se usa en la UI para rastrear el estado de "leído".
    private boolean leido = false;
    // --- FIN DEL NUEVO CAMPO ---


    // Constructor para WebSocket
    public Mensaje(String id_mensaje, String id_conversacion, String id_emisor, String contenido, String tipo, String fecha_envio) {
        this.id_mensaje = id_mensaje;
        this.id_conversacion = id_conversacion;
        this.id_emisor = id_emisor;
        this.contenido = contenido;
        this.tipo = tipo;
        this.fecha_envio = fecha_envio;
    }

    // Getters
    public String getId_mensaje() { return id_mensaje; }
    public String getId_conversacion() { return id_conversacion; }
    public String getId_emisor() { return id_emisor; }
    public String getContenido() { return contenido; }
    public String getTipo() { return tipo; }
    public String getFecha_envio() { return fecha_envio; }
    public String getNombre_emisor() { return nombre_emisor; }

    // Setters (para WebSocket)
    public void setNombre_emisor(String nombre_emisor) { this.nombre_emisor = nombre_emisor; }

    // --- ¡NUEVOS MÉTODOS GETTER Y SETTER! ---
    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }
    // --- FIN DE LOS NUEVOS MÉTODOS ---
}