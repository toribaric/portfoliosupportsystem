package org.nnga.tsp.executor.neuralnetwork.context;

import org.nnga.tsp.algorithms.neuralnetwork.types.SupervisedTrainingAlgorithmType;

import java.util.List;

public class TrainingExecutorParamsContext {
    private List<List<Double>> setInputs;
    private List<List<Double>> setOutputs;
    private List<List<Double>> validationInputs;
    private List<List<Double>> validationOutputs;
    private Integer validationFrequency;
    private SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType;
    private double learningRate;
    private double errorThreshold;
    private Integer maxIterations;

    public TrainingExecutorParamsContext() {

    }

    public TrainingExecutorParamsContext(List<List<Double>> setInputs, List<List<Double>> setOutputs, List<List<Double>> validationInputs, List<List<Double>> validationOutputs, Integer validationFrequency, SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType, double learningRate, double errorThreshold, Integer maxIterations) {
        this.setInputs = setInputs;
        this.setOutputs = setOutputs;
        this.validationInputs = validationInputs;
        this.validationOutputs = validationOutputs;
        this.validationFrequency = validationFrequency;
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

    public List<List<Double>> getValidationInputs() {
        return validationInputs;
    }

    public void setValidationInputs(List<List<Double>> validationInputs) {
        this.validationInputs = validationInputs;
    }

    public List<List<Double>> getValidationOutputs() {
        return validationOutputs;
    }

    public void setValidationOutputs(List<List<Double>> validationOutputs) {
        this.validationOutputs = validationOutputs;
    }

    public Integer getValidationFrequency() {
        return validationFrequency;
    }

    public void setValidationFrequency(Integer validationFrequency) {
        this.validationFrequency = validationFrequency;
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
