package org.nnga.tsp.assessor;

import org.nnga.tsp.persistence.entity.NeuralNetwork;

import java.util.List;

public interface NeuralNetworkAssessor {
    void validate(NeuralNetwork neuralNetwork, List<List<Double>> validationInputs, List<List<Double>> validationOutputs) throws Exception;
    double test(NeuralNetwork neuralNetwork, List<List<Double>> testInputs, List<List<Double>> testOutputs) throws Exception;
    double getValidationError();
    double getRSquared();
}
