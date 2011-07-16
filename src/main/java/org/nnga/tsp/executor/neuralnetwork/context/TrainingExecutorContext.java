package org.nnga.tsp.executor.neuralnetwork.context;

import org.apache.log4j.Logger;
import org.nnga.tsp.algorithms.neuralnetwork.SupervisedTrainingAlgorithm;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.provider.NeuralNetworkDataProvider;

import java.util.Map;
import java.util.Observable;

import static org.springframework.util.Assert.notNull;

public class TrainingExecutorContext extends Observable implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(TrainingExecutorContext.class);

    private NeuralNetwork neuralNetwork;
    private SupervisedTrainingAlgorithm supervisedTrainingAlgorithm;
    private TrainingExecutorParamsContext params;
    private NeuralNetworkDataProvider neuralNetworkDataProvider;

    private double totalError;
    private int trainingIteration;
    private boolean trainingInProcess;

    private Map<Integer, TrainingExecutorContext> executingContexts;

    public TrainingExecutorContext(NeuralNetwork neuralNetwork, SupervisedTrainingAlgorithm supervisedTrainingAlgorithm, TrainingExecutorParamsContext params, NeuralNetworkDataProvider neuralNetworkDataProvider, Map<Integer, TrainingExecutorContext> executingContexts) {
        notNull(neuralNetwork);
        notNull(params);
        notNull(supervisedTrainingAlgorithm);
        notNull(neuralNetworkDataProvider);
        notNull(executingContexts);

        this.neuralNetwork = neuralNetwork;
        this.supervisedTrainingAlgorithm = supervisedTrainingAlgorithm;
        this.params = params;
        this.neuralNetworkDataProvider = neuralNetworkDataProvider;

        this.totalError = 0.5;
        this.trainingIteration = 0;
        trainingInProcess = false;

        // add this context to executing context so it can be stopped manually
        this.executingContexts = executingContexts;
        executingContexts.put(neuralNetwork.getId(), this);
    }

    @Override
    public void run() {

        LOGGER.info("Training of neural network " + neuralNetwork.getName() + " started.");

        // notify observers of training start
        trainingInProcess = true;
        trainingDataChanged();

        /*
         * TODO: thread sync of algorithm execution and ann persistence
         */
        while( trainingInProcess ) {

            // execute training algorithm
            try {
                totalError = supervisedTrainingAlgorithm.execute(neuralNetwork, params.getSupervisedTrainingAlgorithmType(), params.getLearningRate(), params.getSetInputs(), params.getSetOutputs());
            } catch (Exception e) {
                /*
                 * TODO: user must ve notified of this situation!
                 */
                LOGGER.info("Error in training algorithm execution (neural network " + neuralNetwork.getName() + "): " + e.getMessage(), e);
                trainingInProcess = false;
                return;
            }

            // iteration finished
            trainingIteration++;

            // if maxIterations is defined and achieved, stop training
            if( params.getMaxIterations() != null )  {
                if( trainingIteration == params.getMaxIterations() ) {
                    LOGGER.info("Max iterations achieved for neural network " + neuralNetwork.getName() + ", finishing training...");
                    trainingInProcess = false;
                }
            }

            // if errorSum (SSE) is lower tan threshold, training is finished
            if( totalError <= params.getErrorThreshold() ) {
                trainingInProcess = false;
            }

            // be friendly with CPU :)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}

            // iteration and total error changed, notify observers
            trainingDataChanged();

        }

        executingContexts.remove(neuralNetwork.getId());

        // save new, trained network (weights precisely)
        try {
            neuralNetworkDataProvider.save(neuralNetwork);
        } catch (Exception e) {
            LOGGER.info("Error in training algorithm execution (neural network " + neuralNetwork.getName() + "): " + e.getMessage(), e);
            return;
        }

        LOGGER.info("Training of neural network " + neuralNetwork.getName() + " finished!");
    }

    private void trainingDataChanged() {
        setChanged();
        notifyObservers();
    }

    public int getNeuralNetworkId() {
        return neuralNetwork.getId();
    }

    public double getTotalError() {
        return totalError;
    }

    public int getTrainingIteration() {
        return trainingIteration;
    }

    public void setTrainingInProcess(boolean trainingInProcess) {
        this.trainingInProcess = trainingInProcess;
    }

    public boolean isTrainingInProcess() {
        return trainingInProcess;
    }
}
