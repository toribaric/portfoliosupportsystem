package org.nnga.tsp.algorithms.neuralnetwork.impl;

import org.nnga.tsp.algorithms.neuralnetwork.AbstractSupervisedTrainingAlgorithm;
import org.nnga.tsp.algorithms.neuralnetwork.SupervisedTrainingAlgorithm;
import org.nnga.tsp.algorithms.neuralnetwork.types.SupervisedTrainingAlgorithmType;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.entity.Neuron;
import org.nnga.tsp.persistence.entity.NeuronLayer;
import org.nnga.tsp.persistence.entity.NeuronWeight;
import org.nnga.tsp.processor.activation.types.ActivationFunctionType;
import org.nnga.tsp.utility.Constant;

import java.util.ArrayList;
import java.util.List;

public class BackpropWithMomentum extends AbstractSupervisedTrainingAlgorithm {

    protected BackpropWithMomentum() {
        super(null);
    }

    protected BackpropWithMomentum(SupervisedTrainingAlgorithm next) {
        super(next);
    }

    @Override
    protected boolean isForMe(SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType) {
        return supervisedTrainingAlgorithmType.equals(SupervisedTrainingAlgorithmType.BACKPROPMOMENTUM);
    }

    @Override
    protected Double doExecute(NeuralNetwork neuralNetwork, double learningRate, List<List<Double>> setInputs, List<List<Double>> setOutputs) throws Exception {

        double errorSum = 0;

        ActivationFunctionType activationFunctionType = ActivationFunctionType.valueOf(neuralNetwork.getActivationFunction());

        List<NeuronLayer> neuronLayers = neuralNetwork.getNeuronLayers();

        for( int i = 0; i < setInputs.size(); i++ ) {

            // process current training set record inputs, but don't persist (last argument)
            List<Double> calculatedOutputs = neuralNetworkProcessor.process(neuralNetwork, setInputs.get(i), false);

            if( calculatedOutputs.size() == 0 ) {
                throw new IllegalStateException("Backpropagation with momentum algorithm: Error in processing neural network inputs!");
            }

            // process output layer
            List<Double> targetOutputs = setOutputs.get(i);
            errorSum = processOutputLayer(neuronLayers, activationFunctionType, calculatedOutputs, targetOutputs, learningRate, errorSum);

            // process hidden layers
            List<Double> networkInputs = setInputs.get(i);
            processHiddenLayers(neuronLayers, activationFunctionType, networkInputs, learningRate);

        }

        return errorSum;

    }

    private double processOutputLayer(List<NeuronLayer> neuronLayers, ActivationFunctionType activationFunctionType, List<Double> calculatedOutputs, List<Double> targetOutputs, double learningRate, double errorSum) {

        NeuronLayer outputLayer = neuronLayers.get(neuronLayers.size() - 1);
        NeuronLayer hiddenLayer = neuronLayers.get(neuronLayers.size() - 2);
        List<Neuron> outputNeurons = outputLayer.getNeurons();
        List<Neuron> hiddenNeurons = hiddenLayer.getNeurons();

        // get inputs into output neurons - activations of all neurons of last hidden layer
        List<Double> neuronInputs = new ArrayList<Double>();
        for( Neuron hiddenNeuron : hiddenNeurons ) {
            neuronInputs.add(hiddenNeuron.getActivation());
        }

        // calculate error and adjust weights for output layer neurons
        for( int i = 0; i < outputLayer.getNumNeurons(); i++ ) {

            Neuron outputNeuron = outputNeurons.get(i);

            // calculate error
            double targetOutput = targetOutputs.get(i);
            double calculatedOutput = calculatedOutputs.get(i);
            double derivative = activationFunction.execute(activationFunctionType, calculatedOutput, Constant.ACTIVATION_RESPONSE.getValue(), true);
            double error = ( targetOutput - calculatedOutput ) * derivative;

            // save neuron's error value for hidden layers calculations
            outputNeuron.setError(error);

            // update SSE - sum squared errors - when SSE becomes lower than error threshold, training is finished
            // also know as squared sum of residuals - or deviation of a sample (calculated output) from it's "theoretical value" (target output)
            errorSum += (( targetOutput - calculatedOutput ) * ( targetOutput - calculatedOutput )) * 0.5;

            // update output neurons weights
            updateNeuronWeights(outputNeuron, neuronInputs, error, learningRate);

        }

        return errorSum;

    }

    private void processHiddenLayers(List<NeuronLayer> neuronLayers, ActivationFunctionType activationFunctionType, List<Double> networkInputs, double learningRate) {

        for( int i = neuronLayers.size() - 2; i >= 0; i-- ) {

            List<Neuron> frontLayerNeurons = neuronLayers.get(i + 1).getNeurons();
            List<Neuron> hiddenNeurons = neuronLayers.get(i).getNeurons();

            // prepare inputs for every hidden neuron for calculating new weights; if there are more hidden layers before this
            // one, calculate using previous layer neurons outputs; if previous layer is input layer, than
            // use input training set (inputs in network)
            List<Double> neuronInputs = new ArrayList<Double>();
            if( i - 1 >= 0 ) {
                List<Neuron> previousLayerNeurons = neuronLayers.get(i - 1).getNeurons();
                for( Neuron previousLayerNeuron : previousLayerNeurons ) {
                    neuronInputs.add(previousLayerNeuron.getActivation());
                }
            }
            else {
                neuronInputs.addAll(networkInputs);
            }

            // process current hidden layer
            processHiddenLayer(hiddenNeurons, frontLayerNeurons, activationFunctionType, neuronInputs, learningRate);

        }

    }

    private void processHiddenLayer(List<Neuron> hiddenNeurons, List<Neuron> frontLayerNeurons, ActivationFunctionType activationFunctionType, List<Double> neuronInputs, double learningRate) {

        for( int i = 0; i < hiddenNeurons.size(); i++ ) {

            Neuron hiddenNeuron = hiddenNeurons.get(i);

            double error = 0;

            // first part of equation is to sum products of errors of all front layer neurons
            // and weights from this neuron to all front layer neurons - SUM(Ek * Wjk), where
            // k is front layer's neuron index and j this neuron index - Wjk is the weight
            // between this neuron and k-th front layer neuron
            for( Neuron frontLayerNeuron : frontLayerNeurons ) {
                NeuronWeight frontLayerNeuronWeight = frontLayerNeuron.getNeuronWeights().get(i);
                error += frontLayerNeuron.getError() * frontLayerNeuronWeight.getWeight();
            }

            // now we calculate the error - full equation: Ej = Oj * (1 - Oj) * SUM(Ek * Wjk), where
            // Oj is activation - or calculated output - of this neuron
            double derivative = activationFunction.execute(activationFunctionType, hiddenNeuron.getActivation(), Constant.ACTIVATION_RESPONSE.getValue(), true);
            error *= derivative;

            // save error (if this is the first hidden layer it doesn't make any difference)
            hiddenNeuron.setError(error);

            // update hidden neurons weights
            updateNeuronWeights(hiddenNeuron, neuronInputs, error, learningRate);

        }

    }

    private void updateNeuronWeights(Neuron neuron, List<Double> neuronInputs, double error, double learningRate) {

        List<NeuronWeight> neuronWeights = neuron.getNeuronWeights();

        double momentumValue = 0;

        // update neuron weights
        // formula: Wij += (L * Ej * Oi) + ((Wij - Wijt-1) * MOMENTUM), where Oi is output (activation) from previous layer neuron (hidden or input layer),
        // Wijt-1 neuron's weight from previous training set iteration for j-th weight and MOMENTUM is a constant value of 0.9
        for( int i = 0; i < neuronWeights.size() - 1; i++ ) {
            double currentWeight = neuronWeights.get(i).getWeight();
            double previousWeight = neuronWeights.get(i).getPreviousWeight();

            // calculate current weight delta
            double deltaWeight = learningRate * error * neuronInputs.get(i);

            // calculate momentum
            momentumValue = (currentWeight - previousWeight) * Constant.MOMENTUM.getValue();

            // save previousWeight of this weight for use in next training iteration
            neuronWeights.get(i).setPreviousWeight(currentWeight);

            // calculate updated weight and refresh current weight
            double newWeight = currentWeight + deltaWeight + momentumValue;
            neuronWeights.get(i).setWeight(newWeight);
        }

        // update bias weight
        double biasWeight = neuronWeights.get(neuronWeights.size() - 1).getWeight();
        double previousBiasWeight = neuronWeights.get(neuronWeights.size() - 1).getPreviousWeight();

        double deltaWeight = learningRate * error * Constant.BIAS.getValue();

        momentumValue = (biasWeight - previousBiasWeight) * Constant.MOMENTUM.getValue();

        neuronWeights.get(neuronWeights.size() - 1).setPreviousWeight(biasWeight);

        double newBiasWeight = biasWeight + deltaWeight + momentumValue;
        neuronWeights.get(neuronWeights.size() - 1).setWeight(newBiasWeight);

    }

}
