package org.nnga.tsp.observer.impl;

import org.nnga.tsp.executor.neuralnetwork.context.TrainingExecutorContext;
import org.nnga.tsp.observer.NeuralNetworkTrainingObserver;

import java.util.*;

public class NeuralNetworkTrainingObserverImpl implements Observer, NeuralNetworkTrainingObserver {

    private Observable observable;
    private Map<Integer, Map<String, Object>> trainingData;

    public NeuralNetworkTrainingObserverImpl() {
        trainingData = new HashMap<Integer, Map<String, Object>>();
    }

    /*
     * TODO: there's no need for sending all error and iteration data from start to now when simple training data window is running
     */
    @Override
    public synchronized void update(Observable observable, Object o) {
        if( observable instanceof TrainingExecutorContext ) {

            TrainingExecutorContext trainingExecutorContext = (TrainingExecutorContext) observable;

            if( !trainingData.containsKey(trainingExecutorContext.getNeuralNetworkId()) ) {
                trainingData.put(trainingExecutorContext.getNeuralNetworkId(), new HashMap<String, Object>());
            }

            Map<String, Object> currentNetworkData = trainingData.get(trainingExecutorContext.getNeuralNetworkId());

            if( currentNetworkData.get("totalErrors") == null ) {
                currentNetworkData.put("totalErrors", new ArrayList<Double>());
            }
            List<Double> totalErrors = (List<Double>) currentNetworkData.get("totalErrors");
            totalErrors.add(trainingExecutorContext.getTotalError());

            if( currentNetworkData.get("trainingIterations") == null ) {
                currentNetworkData.put("trainingIterations", new ArrayList<Integer>());
            }
            List<Integer> trainingIterations = (List<Integer>) currentNetworkData.get("trainingIterations");
            trainingIterations.add(trainingExecutorContext.getTrainingIteration());

            currentNetworkData.put("trainingInProcess", trainingExecutorContext.isTrainingInProcess());

        }

    }

    @Override
    public void setObservable(Observable observable) {
        this.observable = observable;
        observable.addObserver(this);
    }

    @Override
    public Map<String, Object> getTrainingData(int neuralNetworkId) {
        return trainingData.get(neuralNetworkId);
    }

    @Override
    public void clearTrainingData(int neuralNetworkId) {
        if( trainingData.containsKey(neuralNetworkId) ) {
            trainingData.get(neuralNetworkId).clear();
        }
    }
}
