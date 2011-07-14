package org.nnga.tsp.processor.activation.impl;

import org.nnga.tsp.processor.activation.AbstractActivationFunction;
import org.nnga.tsp.processor.activation.ActivationFunction;
import org.nnga.tsp.processor.activation.types.ActivationFunctionType;

public class LogisticFunction extends AbstractActivationFunction {

    protected LogisticFunction() {
        super(null);
    }

    protected LogisticFunction(ActivationFunction next) {
        super(next);
    }

    @Override
    protected boolean isForMe(ActivationFunctionType activationFunctionType) {
        return activationFunctionType.equals(ActivationFunctionType.LOG);
    }

    @Override
    protected Double doExecute(double activation, double response, boolean returnDerivative) {
        if( returnDerivative ) {
            return getDerivative(activation, response);
        }
        else {
            return getOutput(activation, response);
        }
    }

    /**
     * Logistic Sigmoid activation function: s-shaped curve in 0,1 interval which converges at 0.5
     * Formula: 1 / (1 + e^(-a/p))
     * a - activation sum of neuron
     * p - response, or slope, controls the shape of the S curve; value of 1 is common
     */
    private double getOutput(double activation, double response) {
        return (1 / ( 1 + Math.exp( -response * activation ) ));
    }

    private double getDerivative(double squashedActivation, double response) {
        double derivative = response * squashedActivation * (1 - squashedActivation);
        return derivative;
    }

}
