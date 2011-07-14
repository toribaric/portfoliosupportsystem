package org.nnga.tsp.processor.activation;

import org.nnga.tsp.processor.activation.types.ActivationFunctionType;

public abstract class AbstractActivationFunction implements ActivationFunction {

    protected ActivationFunction next;

    protected AbstractActivationFunction(ActivationFunction next) {
        this.next = next;
    }

    protected abstract boolean isForMe(ActivationFunctionType activationFunctionType);

    protected abstract Double doExecute(double activation, double response, boolean returnDerivative);

    @Override
    public Double execute(ActivationFunctionType activationFunctionType, double activation, double response, boolean returnDerivative) {
        if( isForMe(activationFunctionType) ) {
            return doExecute(activation, response, returnDerivative);
        }
        if( next != null ) {
            return next.execute(activationFunctionType, activation, response, returnDerivative);
        }
        throw new IllegalStateException("Defined neurons activation function wasn't found!");
    }


}
