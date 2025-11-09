// MensajeResponse.java
package itca.soft.renalcare.data.models;

import com.google.gson.annotations.SerializedName;

public class MensajeResponse {
    private boolean success;

    @SerializedName("id_conversacion")
    private int idConversacion;

    private String titulo;

    @SerializedName("id_mensaje")
    private int idMensaje;

    @SerializedName("mensaje_ia")
    private String mensajeIa;

    @SerializedName("datos_nutricionales")
    private DatosNutricionales datosNutricionales;

    @SerializedName("url_imagen")
    private String urlImagen;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public int getIdConversacion() {
        return idConversacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getIdMensaje() {
        return idMensaje;
    }

    public String getMensajeIa() {
        return mensajeIa;
    }

    public DatosNutricionales getDatosNutricionales() {
        return datosNutricionales;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    // Clase interna para datos nutricionales
    public static class DatosNutricionales {
        private int sodio;
        private int potasio;
        private int fosforo;
        private int calorias;
        private boolean recomendado;

        public int getSodio() { return sodio; }
        public int getPotasio() { return potasio; }
        public int getFosforo() { return fosforo; }
        public int getCalorias() { return calorias; }
        public boolean isRecomendado() { return recomendado; }
    }
}