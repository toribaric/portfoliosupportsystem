package org.nnga.tsp.executor.neuralnetwork.context;

import org.apache.log4j.Logger;
import org.nnga.tsp.algorithms.neuralnetwork.SupervisedTrainingAlgorithm;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.provider.NeuralNetworkDataProvider;
import org.nnga.tsp.validator.NeuralNetworkValidator;

import java.util.Map;
import java.util.Observable;

import static org.springframework.util.Assert.notNull;

public class TrainingExecutorContext extends Observable implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(TrainingExecutorContext.class);

    private NeuralNetwork neuralNetwork;
    private SupervisedTrainingAlgorithm supervisedTrainingAlgorithm;
    private NeuralNetworkValidator neuralNetworkValidator;
    private TrainingExecutorParamsContext params;
    private NeuralNetworkDataProvider neuralNetworkDataProvider;

    private double totalError;
    private int trainingIteration;
    private boolean trainingInProcess;

    // additional data given when validation set is used
    private double validationError;
    private double rSquared;

    private Map<Integer, TrainingExecutorContext> executingContexts;

    public TrainingExecutorContext(NeuralNetwork neuralNetwork, SupervisedTrainingAlgorithm supervisedTrainingAlgorithm, NeuralNetworkValidator neuralNetworkValidator, TrainingExecutorParamsContext params, NeuralNetworkDataProvider neuralNetworkDataProvider, Map<Integer, TrainingExecutorContext> executingContexts) {
        notNull(neuralNetwork);
        notNull(params);
        notNull(supervisedTrainingAlgorithm);
        notNull(neuralNetworkDataProvider);
        notNull(executingContexts);

        this.neuralNetwork = neuralNetwork;
        this.supervisedTrainingAlgorithm = supervisedTrainingAlgorithm;
        this.neuralNetworkValidator = neuralNetworkValidator;
        this.params = params;
        this.neuralNetworkDataProvider = neuralNetworkDataProvider;

        this.totalError = 0.5;
        this.trainingIteration = 0;
        trainingInProcess = false;

        this.validationError = 0.0;
        this.rSquared = 0.0;

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
        for( int i = 0; trainingInProcess; i++ ) {

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

            // perform validation of so-far-trained network (if configured)
            if( params.getValidationFrequency() != null && params.getValidationInputs() != null && params.getValidationOutputs() != null ) {
                if( i >= params.getValidationFrequency() ) {
                i = 0;
                    try {
                        neuralNetworkValidator.validate(neuralNetwork, params.getValidationInputs(), params.getValidationOutputs());
                        validationError = neuralNetworkValidator.getValidationError();
                        rSquared = neuralNetworkValidator.getRSquared();
                    } catch (Exception e) {
                        /*
                         * TODO: user must ve notified of this situation!
                         */
                        LOGGER.info("Error in network validation (neural network " + neuralNetwork.getName() + "): " + e.getMessage(), e);
                    }
                }
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
            neuralNetwork.setR2(rSquared);
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

    public double getValidationError() {
        return validationError;
    }

    public double getRSquared() {
        return rSquared;
    }
}
