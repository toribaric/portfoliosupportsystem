package org.nnga.tsp.executor.neuralnetwork.impl;

import org.nnga.tsp.algorithms.neuralnetwork.SupervisedTrainingAlgorithm;
import org.nnga.tsp.algorithms.neuralnetwork.types.SupervisedTrainingAlgorithmType;
import org.nnga.tsp.executor.neuralnetwork.ThreadPoolExecutorProvider;
import org.nnga.tsp.executor.neuralnetwork.TrainingExecutor;
import org.nnga.tsp.executor.neuralnetwork.context.TrainingExecutorContext;
import org.nnga.tsp.executor.neuralnetwork.context.TrainingExecutorParamsContext;
import org.nnga.tsp.observer.NeuralNetworkTrainingObserver;
import org.nnga.tsp.parser.TrainingSetParser;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.entity.TrainingSet;
import org.nnga.tsp.persistence.provider.NeuralNetworkDataProvider;
import org.nnga.tsp.persistence.provider.TrainingSetDataProvider;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import static org.springframework.util.Assert.notNull;

public class TrainingExecutorImpl implements TrainingExecutor {

    private ThreadPoolExecutorProvider threadPoolExecutorProvider;
    private NeuralNetworkDataProvider neuralNetworkDataProvider;
    private TrainingSetDataProvider trainingSetDataProvider;
    private TrainingSetParser trainingSetParser;
    private SupervisedTrainingAlgorithm supervisedTrainingAlgorithm;
    private NeuralNetworkTrainingObserver neuralNetworkTrainingObserver;
    private Map<Integer, TrainingExecutorContext> executingContexts = new ConcurrentHashMap<Integer, TrainingExecutorContext>();

    @Override
    public void train(int neuralNetworkId, int trainingSetId, SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType, double learningRate, double errorThreshold, Integer maxIterations) {
        NeuralNetwork neuralNetwork = neuralNetworkDataProvider.getById(neuralNetworkId);
        TrainingSet trainingSet = trainingSetDataProvider.getById(trainingSetId);

        Map<String, List<List<Double>>> trainingData = trainingSetParser.getTrainingData(trainingSet);
        List<List<Double>> setInputs = trainingData.get("inputs");
        List<List<Double>> setOutputs = trainingData.get("outputs");

        TrainingExecutorParamsContext params = new TrainingExecutorParamsContext(setInputs, setOutputs, supervisedTrainingAlgorithmType, learningRate, errorThreshold, maxIterations);

        train(new TrainingExecutorContext(neuralNetwork, supervisedTrainingAlgorithm, params, neuralNetworkDataProvider, executingContexts));
    }

    @Override
    public void train(TrainingExecutorContext trainingExecutorContext) {
        notNull(trainingExecutorContext);

        neuralNetworkTrainingObserver.setObservable(trainingExecutorContext);

        ThreadPoolExecutor executor = threadPoolExecutorProvider.getExecutor();
        executor.execute(trainingExecutorContext);
    }

    @Override
    public void stopTraining(int neuralNetworkId) {
        TrainingExecutorContext trainingExecutorContext = executingContexts.get(neuralNetworkId);
        if( trainingExecutorContext == null ) {
            throw new IllegalStateException("This network isn't being trained!");
        }
        trainingExecutorContext.setTrainingInProcess(false);
    }

    @Required
    public void setThreadPoolExecutorProvider(ThreadPoolExecutorProvider threadPoolExecutorProvider) {
        this.threadPoolExecutorProvider = threadPoolExecutorProvider;
    }

    @Required
    public void setNeuralNetworkDataProvider(NeuralNetworkDataProvider neuralNetworkDataProvider) {
        this.neuralNetworkDataProvider = neuralNetworkDataProvider;
    }

    @Required
    public void setTrainingSetDataProvider(TrainingSetDataProvider trainingSetDataProvider) {
        this.trainingSetDataProvider = trainingSetDataProvider;
    }

    @Required
    public void setTrainingSetParser(TrainingSetParser trainingSetParser) {
        this.trainingSetParser = trainingSetParser;
    }

    @Required
    public void setSupervisedTrainingAlgorithm(SupervisedTrainingAlgorithm supervisedTrainingAlgorithm) {
        this.supervisedTrainingAlgorithm = supervisedTrainingAlgorithm;
    }

    @Required
    public void setNeuralNetworkTrainingObserver(NeuralNetworkTrainingObserver neuralNetworkTrainingObserver) {
        this.neuralNetworkTrainingObserver = neuralNetworkTrainingObserver;
    }
}
