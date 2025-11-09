// DatosNutricionales.java
package itca.soft.renalcare.data.models;

public class DatosNutricionales {
    private int sodio;
    private int potasio;
    private int fosforo;
    private int calorias;
    private boolean recomendado;

    // Getters y Setters
    public int getSodio() { return sodio; }
    public void setSodio(int sodio) { this.sodio = sodio; }
    public int getPotasio() { return potasio; }
    public void setPotasio(int potasio) { this.potasio = potasio; }
    public int getFosforo() { return fosforo; }
    public void setFosforo(int fosforo) { this.fosforo = fosforo; }
    public int getCalorias() { return calorias; }
    public void setCalorias(int calorias) { this.calorias = calorias; }
    public boolean isRecomendado() { return recomendado; }
    public void setRecomendado(boolean recomendado) { this.recomendado = recomendado; }
}