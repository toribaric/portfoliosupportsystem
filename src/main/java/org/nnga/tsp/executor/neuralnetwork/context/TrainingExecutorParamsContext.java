package org.nnga.tsp.executor.neuralnetwork.context;

import org.nnga.tsp.algorithms.neuralnetwork.types.SupervisedTrainingAlgorithmType;

import java.util.List;

public class TrainingExecutorParamsContext {
    List<List<Double>> setInputs;
    List<List<Double>> setOutputs;
    SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType;
    double learningRate;
    double errorThreshold;
    Integer maxIterations;

    public TrainingExecutorParamsContext() {

    }

    public TrainingExecutorParamsContext(List<List<Double>> setInputs, List<List<Double>> setOutputs, SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType, double learningRate, double errorThreshold, Integer maxIterations) {
        this.setInputs = setInputs;
        this.setOutputs = setOutputs;
        this.supervisedTrainingAlgorithmType = supervisedTrainingAlgorithmType;
        this.learningRate = learningRate;
        this.errorThreshold = errorThreshold;
        this.maxIterations = maxIterations;
    }

    public List<List<Double>> getSetInputs() {
        return setInputs;
    }

    public void setSetInputs(List<List<Double>> setInputs) {
        this.setInputs = setInputs;
    }

    public List<List<Double>> getSetOutputs() {
        return setOutputs;
    }

    public void setSetOutputs(List<List<Double>> setOutputs) {
        this.setOutputs = setOutputs;
    }

    public SupervisedTrainingAlgorithmType getSupervisedTrainingAlgorithmType() {
        return supervisedTrainingAlgorithmType;
    }

    public void setSupervisedTrainingAlgorithmType(SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType) {
        this.supervisedTrainingAlgorithmType = supervisedTrainingAlgorithmType;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getErrorThreshold() {
        return errorThreshold;
    }

    public void setErrorThreshold(double errorThreshold) {
        this.errorThreshold = errorThreshold;
    }

    public Integer getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(Integer maxIterations) {
        this.maxIterations = maxIterations;
    }
}
