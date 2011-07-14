package org.nnga.tsp.processor.impl;

import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.entity.Neuron;
import org.nnga.tsp.persistence.entity.NeuronLayer;
import org.nnga.tsp.persistence.entity.NeuronWeight;
import org.nnga.tsp.persistence.provider.NeuralNetworkDataProvider;
import org.nnga.tsp.processor.NeuralNetworkProcessor;
import org.nnga.tsp.processor.activation.ActivationFunction;
import org.nnga.tsp.processor.activation.types.ActivationFunctionType;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetworkInputProcessor implements NeuralNetworkProcessor<Double> {

    private NeuralNetworkDataProvider neuralNetworkDataProvider;
    private ActivationFunction activationFunction;

    @Override
    public List<Double> process(int neuralNetworkId, List<Double> data, boolean saveActivations) throws Exception {
        NeuralNetwork neuralNetwork = neuralNetworkDataProvider.getById(neuralNetworkId);
        if( neuralNetwork == null ) {
            throw new IllegalArgumentException("Neural network with id \"" + neuralNetwork + "\" doesn't exist.");
        }
        return process(neuralNetwork, data, saveActivations);
    }

    @Override
    public List<Double> process(NeuralNetwork neuralNetwork, List<Double> data, boolean saveActivations) throws Exception {

        List<Double> inputs = new ArrayList<Double>();
        inputs.addAll(data);
        List<Double> outputs = new ArrayList<Double>();

        if( inputs.size() != neuralNetwork.getNumInputNeurons() ) {
            throw new IllegalStateException("Number of inputs must match number of input neurons.");
        }

        ActivationFunctionType activationFunctionType = ActivationFunctionType.valueOf(neuralNetwork.getActivationFunction());

        int currentLayer = 0;

        for( NeuronLayer neuronLayer : neuralNetwork.getNeuronLayers() ) {

            // outputs from previous layer become inputs into current layer
            if( currentLayer > 0 ) {
                inputs.clear();
                inputs.addAll(outputs);
            }

            outputs.clear();

            // calculate outputs of current layer neurons
            outputs = processLayer(neuronLayer, activationFunctionType, inputs);

            currentLayer++;

        }

        // save network with updated neuron activation values if specified
        if( saveActivations ) {
            neuralNetworkDataProvider.save(neuralNetwork);
        }

        return outputs;

    }

    private List<Double> processLayer(NeuronLayer neuronLayer, ActivationFunctionType activationFunctionType, List<Double> inputs) {

        List<Double> outputs = new ArrayList<Double>();

        for( Neuron neuron : neuronLayer.getNeurons() ) {

            double activation = 0;

            // calculate w*i for every neuron in this layer
            List<NeuronWeight> neuronWeights = neuron.getNeuronWeights();
            for( int i = 0; i < neuron.getNumInputs(); i++ ) {
                double weight = neuronWeights.get(i).getWeight();
                activation += weight * inputs.get(i);
            }

            // add the bias - it' usually -1
            activation += neuronWeights.get(neuron.getNumInputs()).getWeight() * (-1);

            // twist it through sigmoid activation function
            double neuronOutput = activationFunction.execute(activationFunctionType, activation, 1.0, false);

            // memorize neuron output ("squashed" activation)
            neuron.setActivation(neuronOutput);

            // add this neuron's neuronOutput - or output - to this layer's outputs
            outputs.add(neuronOutput);

        }

        return outputs;

    }

    @Required
    public void setNeuralNetworkDataProvider(NeuralNetworkDataProvider neuralNetworkDataProvider) {
        this.neuralNetworkDataProvider = neuralNetworkDataProvider;
    }

    @Required
    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

}
