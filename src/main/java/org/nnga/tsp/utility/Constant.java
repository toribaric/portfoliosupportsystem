package org.nnga.tsp.utility;

public enum Constant {
    BIAS (-1.0),
    ACTIVATION_RESPONSE (1.0),
    JITTER_NOISE (0.1),
    MOMENTUM (0.25),
    TRAINING_GRAPH_STEPS (5000),
    // RPROP constants
    UPDATE_VALUE_START (0.1),
    UPDATE_VALUE_MAX (50),
    UPDATE_VALUE_MIN (0.000001),
    DECREASE_FACTOR (0.5),
    INCREASE_FACTOR(1.2);

    private double value;

    Constant(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
