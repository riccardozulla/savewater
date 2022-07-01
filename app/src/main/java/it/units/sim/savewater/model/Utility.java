package it.units.sim.savewater.model;

public class Utility {
    private String name;
    private String description;
    private float waterConsumption;

    public Utility() {
    }

    public Utility(String name, String description, float waterConsumption) {
        this.name = name;
        this.description = description;
        this.waterConsumption = waterConsumption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWaterConsumption() {
        return waterConsumption;
    }

    public void setWaterConsumption(float waterConsumption) {
        this.waterConsumption = waterConsumption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}