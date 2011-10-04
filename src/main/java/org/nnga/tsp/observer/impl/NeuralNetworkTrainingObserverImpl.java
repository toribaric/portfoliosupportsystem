package org.nnga.tsp.observer.impl;

import org.nnga.tsp.executor.neuralnetwork.context.TrainingExecutorContext;
import org.nnga.tsp.observer.NeuralNetworkTrainingObserver;
import org.nnga.tsp.utility.Constant;

import java.util.*;

public class NeuralNetworkTrainingObserverImpl implements Observer, NeuralNetworkTrainingObserver {

    private Observable observable;
    private Map<Integer, Map<String, Object>> trainingData;
    private int updateCounter = 0;

    public NeuralNetworkTrainingObserverImpl() {
        trainingData = new HashMap<Integer, Map<String, Object>>();
    }

    @Override
    public synchronized void update(Observable observable, Object o) {
        if( observable instanceof TrainingExecutorContext ) {

            TrainingExecutorContext trainingExecutorContext = (TrainingExecutorContext) observable;

            // clear training data after TRAINING_GRAPH_STEPS constant is reached, which defines maximum number of training
            // iterations that can be displayed at once on training graph in order for browsers to work acceptable (execute
            // javascript at acceptable speeds
            if( updateCounter > Constant.TRAINING_GRAPH_STEPS.getValue() ) {
                clearTrainingData(trainingExecutorContext.getNeuralNetworkId());
                updateCounter = 0;
            }

            if( !trainingData.containsKey(trainingExecutorContext.getNeuralNetworkId()) ) {
                trainingData.put(trainingExecutorContext.getNeuralNetworkId(), new HashMap<String, Object>());
            }

            Map<String, Object> currentNetworkData = trainingData.get(trainingExecutorContext.getNeuralNetworkId());

            // store sum squared errors
            if( currentNetworkData.get("totalErrors") == null ) {
                currentNetworkData.put("totalErrors", new ArrayList<Double>());
            }
            List<Double> totalErrors = (List<Double>) currentNetworkData.get("totalErrors");
            totalErrors.add(trainingExecutorContext.getTotalError());

            // store training iterations
            if( currentNetworkData.get("trainingIterations") == null ) {
                currentNetworkData.put("trainingIterations", new ArrayList<Integer>());
            }
            List<Integer> trainingIterations = (List<Integer>) currentNetworkData.get("trainingIterations");
            trainingIterations.add(trainingExecutorContext.getTrainingIteration());

            // store validation sum squared error
            currentNetworkData.put("validationError", trainingExecutorContext.getValidationError());

            // store the coefficient of determination
            currentNetworkData.put("rSquared", trainingExecutorContext.getRSquared());

            // store training in process indicator
            currentNetworkData.put("trainingInProcess", trainingExecutorContext.isTrainingInProcess());

            updateCounter++;

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
        updateCounter = 0;
        if( trainingData.containsKey(neuralNetworkId) ) {
            trainingData.get(neuralNetworkId).clear();
        }
    }
}
