package org.nnga.tsp.validator;

import org.nnga.tsp.persistence.entity.NeuralNetwork;

import java.util.List;

public interface NeuralNetworkValidator {
    void validate(NeuralNetwork neuralNetwork, List<List<Double>> validationInputs, List<List<Double>> validationOutputs) throws Exception;
    double getValidationError();
    double getRSquared();
}
