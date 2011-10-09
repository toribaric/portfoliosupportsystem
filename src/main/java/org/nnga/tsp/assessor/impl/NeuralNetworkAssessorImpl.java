package org.nnga.tsp.assessor.impl;

import org.nnga.tsp.assessor.NeuralNetworkAssessor;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.processor.NeuralNetworkProcessor;
import org.nnga.tsp.utility.MathFunctions;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NeuralNetworkAssessorImpl implements NeuralNetworkAssessor {

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
                throw new IllegalStateException("ANN assessor: Error in processing neural network validation inputs!");
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

    @Override
    public double test(NeuralNetwork neuralNetwork, List<List<Double>> testInputs, List<List<Double>> testOutputs) throws Exception {
        List<Double> targetOutputs = new ArrayList<Double>();
        List<Double> approximatedOutputs = new ArrayList<Double>();

        for( int i = 0; i < testInputs.size(); i++ ) {
            List<Double> outputs = neuralNetworkProcessor.process(neuralNetwork, testInputs.get(i), false);
            if( outputs.size() == 0 ) {
                throw new IllegalStateException("ANN assessor: Error in processing neural network test inputs!");
            }
            /*
             * TODO: implement for num. outputs > 1
             */
            targetOutputs.add(testOutputs.get(i).get(0));
            approximatedOutputs.add(outputs.get(0));
        }

        // return calculated error energy of this network
        return calculateErrorEnergy(targetOutputs, approximatedOutputs);
    }

    private double calculateMean(List<List<Double>> data) {
        double mean = 0;
        for( List<Double> output : data ) {
            mean += output.get(0);
        }
        return mean / data.size();
    }

    private double calculateErrorEnergy(List<Double> targetOutputs, List<Double> approximatedOutputs) {
        Map<String, List<Double>> targetComplexFrequencies = MathFunctions.discreteFourierTransform(targetOutputs);
        Map<String, List<Double>> approximatedComplexFrequencies = MathFunctions.discreteFourierTransform(approximatedOutputs);
        List<Double> targetMagnitudes = calculateMagnitudes(targetComplexFrequencies);
        List<Double> approximatedMagnitudes = calculateMagnitudes(approximatedComplexFrequencies);
        // the energy of a time series is calculated as: EE = SUM(xi^2), where (x0, x1,...,x(n-1)) is the time series
        // -> here error is the difference of frequencies magnitudes of real and approximated time series
        //    transformed from time domain to frequency domain with discrete fourier transform
        double errorEnergy = 0;
        for( int i = 0; i < targetMagnitudes.size(); i++ ) {
            double error = targetMagnitudes.get(i) - approximatedMagnitudes.get(i);
            errorEnergy += (error * error);
        }
        return errorEnergy;
    }

    private List<Double> calculateMagnitudes(Map<String, List<Double>> complexFrequencyData) {
        List<Double> magnitudes = new ArrayList<Double>();
        List<Double> realParts = complexFrequencyData.get("realData");
        List<Double> imgParts = complexFrequencyData.get("imgData");
        for( int i = 0; i < realParts.size() / 2; i++ ) {
            double magnitude = (realParts.get(i) * realParts.get(i)) + (imgParts.get(i) * imgParts.get(i));
            magnitudes.add(magnitude);
        }
        return magnitudes;
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
