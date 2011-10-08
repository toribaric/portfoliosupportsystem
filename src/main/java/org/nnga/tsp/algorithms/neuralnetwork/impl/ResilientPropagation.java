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

public class ResilientPropagation extends AbstractSupervisedTrainingAlgorithm {

    protected ResilientPropagation() {
        super(null);
    }

    protected ResilientPropagation(SupervisedTrainingAlgorithm next) {
        super(next);
    }

    @Override
    protected boolean isForMe(SupervisedTrainingAlgorithmType supervisedTrainingAlgorithmType) {
        return supervisedTrainingAlgorithmType.equals(SupervisedTrainingAlgorithmType.RPROP);
    }

    @Override
    protected Double doExecute(NeuralNetwork neuralNetwork, double learningRate, List<List<Double>> setInputs, List<List<Double>> setOutputs) throws Exception {

        double sumSquaredError = 0;

        // reset sum of partial derivatives Ee/wij (for all weights)
        resetSumDerivatives(neuralNetwork);

        ActivationFunctionType activationFunctionType = ActivationFunctionType.valueOf(neuralNetwork.getActivationFunction());
        List<NeuronLayer> neuronLayers = neuralNetwork.getNeuronLayers();

        // go through all training set input/output pairs (patterns) and calculate sum of SSE and sum of partial derivatives
        // of total SSE with respect to every weight (dE/dwij) by summing partial derivatives of SSE per training pattern
        // with respect to every weight (dEp/dwij) -> dE/dwij = 1/2 * SUM(dEp/dwij) -> we'll than use it to adjust neurons'
        // weights with the RPROP algorithm
        for( int i = 0; i < setInputs.size(); i++ ) {
            // process current training set record inputs, but don't persist (last argument)
            List<Double> calculatedOutputs = neuralNetworkProcessor.process(neuralNetwork, setInputs.get(i), false);

            if( calculatedOutputs.size() == 0 ) {
                throw new IllegalStateException("Resilient propagation algorithm: Error in processing neural network inputs!");
            }

            // process output layer
            List<Double> targetOutputs = setOutputs.get(i);
            sumSquaredError = processOutputLayer(neuronLayers, activationFunctionType, calculatedOutputs, targetOutputs, sumSquaredError);

            // process hidden layers
            List<Double> networkInputs = setInputs.get(i);
            processHiddenLayers(neuronLayers, activationFunctionType, networkInputs);
        }

        // finally update all weights using the RPROP (resilient propagation) algorithm
        for( NeuronLayer neuronLayer : neuralNetwork.getNeuronLayers() ) {
            for( Neuron neuron : neuronLayer.getNeurons() ) {
                updateNeuronWeights(neuron);
            }
        }

        // return MSE - mean squared error
        return sumSquaredError / setOutputs.size();

    }

    private double processOutputLayer(List<NeuronLayer> neuronLayers, ActivationFunctionType activationFunctionType, List<Double> calculatedOutputs, List<Double> targetOutputs, double sumSquaredError) {

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
            double errorSignal = -( targetOutput - calculatedOutput ) * derivative;

            // save neuron's error value for hidden layers calculations
            outputNeuron.setError(errorSignal);

            // update SSE - sum squared error - when SSE becomes lower than error threshold, training is finished
            // also know as squared sum of residuals - or deviation of a sample (calculated output) from it's "theoretical value" (target output)
            sumSquaredError += (( targetOutput - calculatedOutput ) * ( targetOutput - calculatedOutput ));

            // calculate partial derivatives of Ep (error produced by current training pattern) with respect to each of current neuron's
            // weight and add those to the existing sum of derivatives for this training epoch (all training set patterns)
            calculateSumDerivative(outputNeuron, neuronInputs);

        }

        return sumSquaredError;

    }

    private void processHiddenLayers(List<NeuronLayer> neuronLayers, ActivationFunctionType activationFunctionType, List<Double> networkInputs) {

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
            processHiddenLayer(hiddenNeurons, frontLayerNeurons, activationFunctionType, neuronInputs);

        }

    }

    private void processHiddenLayer(List<Neuron> hiddenNeurons, List<Neuron> frontLayerNeurons, ActivationFunctionType activationFunctionType, List<Double> neuronInputs) {

        for( int i = 0; i < hiddenNeurons.size(); i++ ) {

            Neuron hiddenNeuron = hiddenNeurons.get(i);

            double errorSignal = 0;

            // first part of equation is to sum products of errors of all front layer neurons
            // and weights from this neuron to all front layer neurons - SUM(Ek * Wjk), where
            // k is front layer's neuron index and j this neuron index - Wjk is the weight
            // between this neuron and k-th front layer neuron
            for( Neuron frontLayerNeuron : frontLayerNeurons ) {
                NeuronWeight frontLayerNeuronWeight = frontLayerNeuron.getNeuronWeights().get(i);
                errorSignal += frontLayerNeuron.getError() * frontLayerNeuronWeight.getWeight();
            }

            // now we calculate the error signal - full equation: Ej = Oj * (1 - Oj) * SUM(Ek * Wjk), where
            // Oj is activation - or calculated output - of this neuron
            double derivative = activationFunction.execute(activationFunctionType, hiddenNeuron.getActivation(), Constant.ACTIVATION_RESPONSE.getValue(), true);
            errorSignal *= derivative;

            // save error (if this is the first hidden layer it doesn't make any difference)
            hiddenNeuron.setError(errorSignal);

            // calculate partial derivatives of Ep (error produced by current training pattern) with respect to each of current neuron's
            // weight and add those to the existing sum of derivatives for this training epoch (all training set patterns)
            calculateSumDerivative(hiddenNeuron, neuronInputs);

        }

    }

    private void calculateSumDerivative(Neuron neuron, List<Double> neuronInputs) {
        List<NeuronWeight> neuronWeights = neuron.getNeuronWeights();
        for( int i = 0; i < neuronWeights.size(); i++ ) {
            NeuronWeight currentWeight = neuronWeights.get(i);
            // calculate sum of partial derivatives of E with respect to current weight for each weight of current
            // neuron, and repeat this for every training pattern
            double neuronInput = (i == neuronWeights.size() - 1 ? Constant.BIAS.getValue() : neuronInputs.get(i));
            double derivative = (neuron.getError() * neuronInput);
            double newSumDerivative = currentWeight.getSumDerivative() + derivative;
            // add currently calculated derivative to the existing sum of derivatives for this weight
            currentWeight.setSumDerivative(newSumDerivative);
        }
    }

    private void updateNeuronWeights(Neuron neuron) {

        for( NeuronWeight neuronWeight : neuron.getNeuronWeights() ) {

            // sum derivatives are summed partial derivatives of error function (SSE) with respect to current
            // weight for all training set patterns (or records, or IOs); dE/dwij(t) = 1/2 * SUM(dEp/dwij(t)), where
            // p stands for training pattern in training set, and it goes from 1 to n - training set size
            double sumDerivative = neuronWeight.getSumDerivative() * 0.5;
            double previousSumDerivative = neuronWeight.getPreviousSumDerivative();
            double updateValue = neuronWeight.getUpdateValue();
            double deltaWeight = neuronWeight.getDeltaWeight();
            double newWeight = 0.0;

            // if partial derivative dE/dwij of error function (or gradient) didn't changed the sign form last epoch's derivative
            // (that means that we haven't skipped any local/potential global minimum - so we're continuing in the same direction down
            // the error function hill as we were going in the last epoch)
            if( previousSumDerivative * sumDerivative > 0 ) {
                updateValue = Math.min(updateValue * Constant.INCREASE_FACTOR.getValue(), Constant.UPDATE_VALUE_MAX.getValue());
                deltaWeight = -Math.signum(sumDerivative) * updateValue;
                newWeight = neuronWeight.getWeight() + deltaWeight;
                neuronWeight.setPreviousSumDerivative(sumDerivative);
            }
            // if gradient changed the sign from last epoch; that means that we've jumped on the "other hill" of the error function, and
            // because of that, here we're going back on previous hill be subtracting the weight delta from current weight value - because
            // we added that value in previous epoch, in this way we're reverting that "too big step"
            // -> here we also set weight's "previousSumDerivative" to 0 to prevent double neutralization of the weight, because in the next step
            //    the derivative will change it's sign again (because we reverted the sign back and this else-if would trigger again without
            //    that value set to 0, which will cause the last else-if to trigger)
            else if( previousSumDerivative * sumDerivative < 0 ) {
                updateValue = Math.max(updateValue * Constant.DECREASE_FACTOR.getValue(), Constant.UPDATE_VALUE_MIN.getValue());
                newWeight = neuronWeight.getWeight() - deltaWeight;
                neuronWeight.setPreviousSumDerivative(0);
            }
            // change in the gradient was very small; we continue to apply the update, but do not change it
            else if( previousSumDerivative * sumDerivative == 0 ) {
                // here we don't change the update value, but calculate delta with last one calculated
                deltaWeight = -Math.signum(sumDerivative) * updateValue;
                newWeight = neuronWeight.getWeight() + deltaWeight;
                neuronWeight.setPreviousSumDerivative(sumDerivative);
            }

            // update weight and deltaWeight and updateValue for use in next training epochs
            neuronWeight.setWeight(newWeight);
            neuronWeight.setDeltaWeight(deltaWeight);
            neuronWeight.setUpdateValue(updateValue);

        }

    }

    private void resetSumDerivatives(NeuralNetwork neuralNetwork) {
        for( NeuronLayer neuronLayer : neuralNetwork.getNeuronLayers() ) {
            for( Neuron neuron : neuronLayer.getNeurons() ) {
                for( NeuronWeight weight : neuron.getNeuronWeights() ) {
                    weight.setSumDerivative(0);
                }
            }
        }
    }

}
