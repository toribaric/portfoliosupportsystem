package org.nnga.tsp.processor.activation;

import org.nnga.tsp.processor.activation.types.ActivationFunctionType;

public interface ActivationFunction {
    Double execute(ActivationFunctionType activationFunctionType, double activation, double response, boolean returnDerivative);
}
