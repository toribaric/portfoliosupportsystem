package org.nnga.tsp.processor.activation.impl;

import org.nnga.tsp.processor.activation.AbstractActivationFunction;
import org.nnga.tsp.processor.activation.ActivationFunction;
import org.nnga.tsp.processor.activation.types.ActivationFunctionType;

public class HyperbolicTangentFunction extends AbstractActivationFunction {

    protected HyperbolicTangentFunction() {
        super(null);
    }

    protected HyperbolicTangentFunction(ActivationFunction next) {
        super(next);
    }

    @Override
    protected boolean isForMe(ActivationFunctionType activationFunctionType) {
        return activationFunctionType.equals(ActivationFunctionType.TANH);
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
     * Hyperbolic tangent function
     */
    private double getOutput(double activation, double response) {
        double eX = Math.exp( -response * activation );
	    return (1 - eX) / (1 + eX);
    }

    private double getDerivative(double squashedActivation, double response) {
        double derivative = response * (1 - squashedActivation * squashedActivation);
        return derivative;
    }

}
