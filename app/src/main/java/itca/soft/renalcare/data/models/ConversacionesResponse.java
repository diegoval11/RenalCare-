// ConversacionesResponse.java
package itca.soft.renalcare.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ConversacionesResponse {
    private boolean success;
    private List<ConversacionData> conversaciones;

    public boolean isSuccess() {
        return success;
    }

    public List<ConversacionData> getConversaciones() {
        return conversaciones;
    }

    public static class ConversacionData {
        @SerializedName("id_conversacion")
        private int idConversacion;

        private String titulo;

        @SerializedName("fecha_creacion")
        private String fechaCreacion;

        private List<MensajeData> mensajes;

        public int getIdConversacion() {
            return idConversacion;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getFechaCreacion() {
            return fechaCreacion;
        }

        public List<MensajeData> getMensajes() {
            return mensajes;
        }
    }

    public static class MensajeData {
        @SerializedName("id_mensaje")
        private int idMensaje;

        @SerializedName("id_conversacion")
        private int idConversacion;

        private String remitente;
        private String contenido;
        private String tipo;

        @SerializedName("fecha_envio")
        private String fechaEnvio;

        @SerializedName("url_imagen")
        private String urlImagen;

        @SerializedName("datos_nutricionales")
        private Object datosNutricionales;

        public int getIdMensaje() {
            return idMensaje;
        }

        public int getIdConversacion() {
            return idConversacion;
        }

        public String getRemitente() {
            return remitente;
        }

        public String getContenido() {
            return contenido;
        }

        public String getTipo() {
            return tipo;
        }

        public String getFechaEnvio() {
            return fechaEnvio;
        }

        public String getUrlImagen() {
            return urlImagen;
        }

        public Object getDatosNutricionales() {
            return datosNutricionales;
        }
    }
}