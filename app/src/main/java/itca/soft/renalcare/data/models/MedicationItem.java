package itca.soft.renalcare.data.models;

public class MedicationItem {
    private String name;
    private String dose;
    private String time;
    private boolean taken;

    public MedicationItem(String name, String dose, String time, boolean taken) {
        this.name = name;
        this.dose = dose;
        this.time = time;
        this.taken = taken;
    }

    // Getters
    public String getName() { return name; }
    public String getDose() { return dose; }
    public String getTime() { return time; }
    public boolean isTaken() { return taken; }
}