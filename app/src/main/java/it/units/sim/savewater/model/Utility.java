package it.units.sim.savewater.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class Utility {
    private String name;
    private String description;
    private int waterConsumption;
    @ServerTimestamp
    private Timestamp timestamp;

    public Utility() {
    }

    public Utility(String name, String description, int waterConsumption) {
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

    public int getWaterConsumption() {
        return waterConsumption;
    }

    public void setWaterConsumption(int waterConsumption) {
        this.waterConsumption = waterConsumption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "Utility{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", waterConsumption=" + waterConsumption +
                ", timestamp=" + timestamp +
                '}';
    }
}