package org.nnga.tsp.validator.impl;

import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.processor.NeuralNetworkProcessor;
import org.nnga.tsp.validator.NeuralNetworkValidator;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class NeuralNetworkValidatorImpl implements NeuralNetworkValidator {

    private NeuralNetworkProcessor<Double> neuralNetworkProcessor;
    private double validationError;
    private double rSquared;

    @Override
    public void validate(NeuralNetwork neuralNetwork, List<List<Double>> validationInputs, List<List<Double>> validationOutputs) throws Exception {
        double sumSquaredError = 0;
        rSquared = 0;
        double outputsMean = calculateMean(validationOutputs);
        // residual sum squared and total sum squared for calculating the coefficient of determination
        double rss = 0;
        double tss = 0;

        for( int i = 0; i < validationInputs.size(); i++ ) {

            // process current training set record inputs, but don't persist (last argument)
            List<Double> calculatedOutputs = neuralNetworkProcessor.process(neuralNetwork, validationInputs.get(i), false);

            if( calculatedOutputs.size() == 0 ) {
                throw new IllegalStateException("ANN validator: Error in processing neural network validation inputs!");
            }

            /*
             * TODO: implement for num. outputs > 1
             */
            // calculate sum squared error, residual sum squared and total sum squared
            List<Double> realOutputs = validationOutputs.get(i);
            double unexplainedDiff = realOutputs.get(0) - calculatedOutputs.get(0);
            double totalDiff = realOutputs.get(0) - outputsMean;
            sumSquaredError += (unexplainedDiff * unexplainedDiff);
            rss += (unexplainedDiff * unexplainedDiff);
            tss += (totalDiff * totalDiff);

        }

        // calculate validation error, which is a mean squared error
        validationError = sumSquaredError / validationOutputs.size();

        // finally calculate the coefficient of determination
        rSquared = 1 - (rss / tss);
    }

    private double calculateMean(List<List<Double>> validationOutputs) {
        double mean = 0;
        for( List<Double> output : validationOutputs ) {
            mean += output.get(0);
        }
        return mean / validationOutputs.size();
    }

    @Override
    public double getValidationError() {
        return validationError;
    }

    @Override
    public double getRSquared() {
        return rSquared;
    }

    @Required
    public void setNeuralNetworkProcessor(NeuralNetworkProcessor<Double> neuralNetworkProcessor) {
        this.neuralNetworkProcessor = neuralNetworkProcessor;
    }
}
