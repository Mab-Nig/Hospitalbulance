package project.cs426.hospitalbulance.backend.database;

public class BodyMeasurements {
    private double weight = 0D;
    private double height = 0D;

    public double getWeight() {
        return this.weight;
    }

    public BodyMeasurements setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public double getHeight() {
        return this.height;
    }

    public BodyMeasurements setHeight(double height) {
        this.height = height;
        return this;
    }
}
