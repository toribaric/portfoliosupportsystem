package org.nnga.tsp.algorithms.neuralnetwork;

import org.nnga.tsp.algorithms.neuralnetwork.types.SupervisedTrainingAlgorithmType;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.provider.NeuralNetworkDataProvider;
import org.nnga.tsp.processor.NeuralNetworkProcessor;
import org.nnga.tsp.processor.activation.ActivationFunction;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public abstract class AbstractSupervisedTrainingAlgorithm implements SupervisedTrainingAlgorithm {

    protected SupervisedTrainingAlgorithm next;
    protected NeuralNetworkProcessor<Double> neuralNetworkProcessor;
    protected ActivationFunction activationFunction;

    protected AbstractSupervisedTrainingAlgorithm(SupervisedTrainingAlgorithm next) {
        this.next = next;
    }

    protected abstract boolean isForMe(SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType);

    protected abstract Double doExecute(NeuralNetwork neuralNetwork, double learningRate, List<List<Double>> setInputs, List<List<Double>> setOutputs) throws Exception;

    @Override
    public Double execute(NeuralNetwork neuralNetwork, SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType, double learningRate, List<List<Double>> setInputs, List<List<Double>> setOutputs) throws Exception {
        if( neuralNetwork == null ) {
            throw new IllegalArgumentException("NeuralNetwork must be defined!");
        }

        if( isForMe(supervisedTrainingAlgorithmType) ) {
            return doExecute(neuralNetwork, learningRate, setInputs, setOutputs);
        }

        if( next != null ) {
            return next.execute(neuralNetwork, supervisedTrainingAlgorithmType, learningRate, setInputs, setOutputs);
        }

        throw new IllegalStateException("Defined supervised training algorithm not found.");
    }

    @Required
    public void setNeuralNetworkProcessor(NeuralNetworkProcessor<Double> neuralNetworkProcessor) {
        this.neuralNetworkProcessor = neuralNetworkProcessor;
    }

    @Required
    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }
}
