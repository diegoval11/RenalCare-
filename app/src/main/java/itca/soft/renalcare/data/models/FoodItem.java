package itca.soft.renalcare.data.models;

import com.google.gson.annotations.SerializedName;

public class FoodItem {

    // SerializedName mapea el nombre de la columna de la BD/JSON al campo Java
    @SerializedName("id_alimento")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    // Cambiamos de int (ID de drawable) a String (URL)
    @SerializedName("foto_url")
    private String fotoUrl;

    @SerializedName("sodio")
    private int sodio;

    @SerializedName("potasio")
    private int potasio;

    @SerializedName("fosforo")
    private int fosforo;

    @SerializedName("etiqueta")
    private String etiqueta;

    // Campos nuevos
    @SerializedName("ingredientes")
    private String ingredientes;

    @SerializedName("receta")
    private String receta;

    // Constructor (puedes dejarlo vacío si GSON lo maneja)

    // --- Getters ---
    // (Estos son los que usará el Adapter)

    public String getNombre() {
        return nombre;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public int getSodio() {
        return sodio;
    }

    public int getPotasio() {
        return potasio;
    }

    public int getFosforo() {
        return fosforo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    // Getters para los nuevos campos (por si los usas en una vista de detalle)
    public String getIngredientes() {
        return ingredientes;
    }

    public String getReceta() {
        return receta;
    }
}