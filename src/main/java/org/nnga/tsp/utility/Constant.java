package org.nnga.tsp.utility;

public enum Constant {
    BIAS (-1.0),
    ACTIVATION_RESPONSE (1.0),
    JITTER_NOISE (0.1),
    MOMENTUM (0.25);

    private double value;

    Constant(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
