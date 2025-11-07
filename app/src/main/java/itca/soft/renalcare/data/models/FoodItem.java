package itca.soft.renalcare.data.models;

public class FoodItem {
    private String name;
    private int potassium; // K
    private int sodium;    // Na
    private int phosphorus; // P
    private String recommendation;
    private int imageResId; // <-- 1. AÑADE LA VARIABLE AQUÍ

    public FoodItem(String name, int potassium, int sodium, int phosphorus, String recommendation, int imageResId) {
        this.name = name;
        this.potassium = potassium;
        this.sodium = sodium;
        this.phosphorus = phosphorus;
        this.recommendation = recommendation;
        this.imageResId = imageResId; // <-- 2. ASÍGNALA EN EL CONSTRUCTOR
    }

    // Getters
    public String getName() { return name; }
    public int getPotassium() { return potassium; }
    public int getSodium() { return sodium; }
    public int getPhosphorus() { return phosphorus; }
    public String getRecommendation() { return recommendation; }

    // Ahora esta línea funcionará
    public int getImageResId() { return imageResId; }
}