package itca.soft.renalcare.data.models;

import com.google.gson.annotations.SerializedName;

// Modelo para la respuesta de la API de consejos
public class ConsejoResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("consejo")
    private Consejo consejo;

    // Getter
    public Consejo getConsejo() {
        return consejo;
    }

    // Clase interna para el objeto "consejo"
    public static class Consejo {
        @SerializedName("consejo")
        private String consejo;

        @SerializedName("categoria")
        private String categoria;

        // Getters
        public String getConsejo() {
            return consejo;
        }

        public String getCategoria() {
            return categoria;
        }
    }
}