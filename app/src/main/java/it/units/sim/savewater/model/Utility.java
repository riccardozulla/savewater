package it.units.sim.savewater.model;

public class Utility {
    private String name;
    private float waterConsumption;

    public Utility() {
    }

    public Utility(String name, float waterConsumption) {
        this.name = name;
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
}