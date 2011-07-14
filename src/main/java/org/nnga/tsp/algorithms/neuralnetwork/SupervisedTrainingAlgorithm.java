package org.nnga.tsp.algorithms.neuralnetwork;

import org.nnga.tsp.algorithms.neuralnetwork.types.SupervisedTrainingAlgorithmType;
import org.nnga.tsp.persistence.entity.NeuralNetwork;

import java.math.BigDecimal;
import java.util.List;

public interface SupervisedTrainingAlgorithm {
    Double execute(NeuralNetwork neuralNetwork, SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType, double learningRate, List<List<Double>> setInputs, List<List<Double>> setOutputs) throws Exception;
}
