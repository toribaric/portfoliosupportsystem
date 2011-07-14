package org.nnga.tsp.executor.neuralnetwork;

import org.nnga.tsp.algorithms.neuralnetwork.types.SupervisedTrainingAlgorithmType;
import org.nnga.tsp.executor.neuralnetwork.context.TrainingExecutorContext;

public interface TrainingExecutor {
    void train(int neuralNetworkId, int trainingSetId, SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType, double learningRate, double errorThreshold, Integer maxIterations);
    void train(TrainingExecutorContext trainingExecutorContext);
    void stopTraining(int neuralNetworkId);
}
