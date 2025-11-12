package itca.soft.renalcare.data.models;

public class Mensaje {
    private String id_mensaje;
    private String id_conversacion;
    private String id_emisor;
    private String contenido;
    private String tipo;
    private String timestamp;
    private String nombre_emisor;
    private boolean leido;

    public Mensaje(String id_mensaje, String id_conversacion, String id_emisor,
                   String contenido, String tipo, String timestamp) {
        this.id_mensaje = id_mensaje;
        this.id_conversacion = id_conversacion;
        this.id_emisor = id_emisor;
        this.contenido = contenido;
        this.tipo = tipo;
        this.timestamp = timestamp;
        this.leido = false;
    }

    // Getters y Setters
    public String getId_mensaje() { return id_mensaje; }
    public void setId_mensaje(String id_mensaje) { this.id_mensaje = id_mensaje; }

    public String getId_conversacion() { return id_conversacion; }
    public void setId_conversacion(String id_conversacion) { this.id_conversacion = id_conversacion; }

    public String getId_emisor() { return id_emisor; }
    public void setId_emisor(String id_emisor) { this.id_emisor = id_emisor; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getNombre_emisor() { return nombre_emisor; }
    public void setNombre_emisor(String nombre_emisor) { this.nombre_emisor = nombre_emisor; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
}