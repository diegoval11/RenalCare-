// ConversacionItem.java
package itca.soft.renalcare.data.models;

public class ConversacionItem {
    private int id_conversacion;
    private String titulo;
    private String fecha_creacion;

    public ConversacionItem(int id, String titulo, String fecha) {
        this.id_conversacion = id;
        this.titulo = titulo;
        this.fecha_creacion = fecha;
    }

    public int getIdConversacion() { return id_conversacion; }
    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha_creacion; }
}
