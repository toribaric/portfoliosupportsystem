package org.nnga.tsp.persistence.processor.impl;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.entity.Neuron;
import org.nnga.tsp.persistence.entity.NeuronLayer;
import org.nnga.tsp.persistence.entity.NeuronWeight;
import org.nnga.tsp.persistence.processor.NeuralNetworkDataProcessor;
import org.nnga.tsp.persistence.provider.NeuralNetworkDataProvider;
import org.nnga.tsp.utility.RandomDataProvider;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetworkDataProcessorImpl implements NeuralNetworkDataProcessor {

    private static final Logger LOGGER = Logger.getLogger(NeuralNetworkDataProcessorImpl.class);

    private NeuralNetworkDataProvider neuralNetworkDataProvider;

    @Override
    public List<NeuralNetwork> getAll() {
        return neuralNetworkDataProvider.getAll();
    }

    @Override
    public void updateConfigurationData(int id, String activationFunction, Boolean trained) throws Exception {
        NeuralNetwork neuralNetwork = neuralNetworkDataProvider.getById(id);
        if( neuralNetwork == null ) {
            throw new IllegalArgumentException("Neural network with provided ID was not found");
        }
        if( activationFunction != null ) {
            neuralNetwork.setActivationFunction(activationFunction);
        }
        if( trained != null ) {
            neuralNetwork.setTrained(trained);
        }
        neuralNetworkDataProvider.save(neuralNetwork);
        LOGGER.info("Configuration data updated");
    }

    @Override
    public void randomizeWeights(int id) throws Exception {
        NeuralNetwork neuralNetwork = neuralNetworkDataProvider.getById(id);
        if( neuralNetwork == null ) {
            throw new IllegalArgumentException("Neural network with provided ID was not found");
        }
        // randomizing of weights implies reset of training state
        neuralNetwork.setTrained(false);
        for( NeuronLayer neuronLayer : neuralNetwork.getNeuronLayers() ) {
            for( Neuron neuron : neuronLayer.getNeurons() ) {
                for( NeuronWeight neuronWeight : neuron.getNeuronWeights() ) {
                    neuronWeight.setWeight(RandomDataProvider.randomDoubleFromRange(-1, 1));
                }
            }
        }
        neuralNetworkDataProvider.save(neuralNetwork);
        LOGGER.info("Network weights randomized");
    }

    @Override
    public void processNewStructuralData(String name, String description, String structure, String activationFunction) throws Exception {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.setName(name);
        neuralNetwork.setDescription(description);
        neuralNetwork.setActivationFunction(activationFunction);
        createStructureAndPersistModel(neuralNetwork, structure);
        LOGGER.info("New neural network structural data assembled and saved");
    }

    @Override
    public void processUpdatedStructuralData(int id, String structure) throws Exception {
        NeuralNetwork neuralNetwork = neuralNetworkDataProvider.getById(id);
        if( neuralNetwork == null ) {
            throw new IllegalArgumentException("Neural network with provided ID was not found");
        }
        // structure update randomizes all weights, which implies reset of training state
        neuralNetwork.setTrained(false);
        neuralNetwork.getNeuronLayers().clear();
        createStructureAndPersistModel(neuralNetwork, structure);
        LOGGER.info("Neural network structural model updated");
    }

    @Override
    public void deleteNetwork(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(json);
        int id = jsonNode.get("id").getIntValue();

        NeuralNetwork neuralNetwork = neuralNetworkDataProvider.getById(id);

        if( neuralNetwork == null ) {
            throw new IllegalArgumentException("Neural network with provided ID was not found");
        }

        neuralNetworkDataProvider.delete(neuralNetwork);
        LOGGER.info("Neural network deleted");
    }

    private void createStructureAndPersistModel(NeuralNetwork neuralNetwork, String structure) throws Exception {

        if( structure.matches("(\\d+\\s)+\\d+") ) {

            String[] neuronGroups = structure.split("\\s");

            for( int i = 1; i < neuronGroups.length; i++ ) {

                int inputs = Integer.parseInt(neuronGroups[i - 1]);
                int numNeurons = Integer.parseInt(neuronGroups[i]);

                List<Neuron> neurons = createNeurons(inputs, numNeurons);

                neuralNetwork.addNeuronLayer(new NeuronLayer(numNeurons, neurons));

            }

            int numInputNeurons = Integer.parseInt(neuronGroups[0]);
            int numOutputNeurons = Integer.parseInt(neuronGroups[neuronGroups.length - 1]);
            int numHiddenLayers = neuronGroups.length - 2;

            neuralNetwork.setNumInputNeurons(numInputNeurons);
            neuralNetwork.setNumOutputNeurons(numOutputNeurons);
            neuralNetwork.setNumHiddenLayers(numHiddenLayers);

            neuralNetworkDataProvider.save(neuralNetwork);

        }
        else {
            throw new IllegalArgumentException("Invalid structure input!");
        }

    }

    private List<Neuron> createNeurons(int inputs, int numNeurons) {

        List<Neuron> neurons = new ArrayList<Neuron>();

        for( int i = 0; i < numNeurons; i++ ) {
            List<NeuronWeight> neuronWeights = new ArrayList<NeuronWeight>();

            // initialize neurons' weights at random doubles between -1 and 1 - +1 is the BIAS
            for( int j = 0; j < inputs + 1; j++ ) {
                neuronWeights.add(new NeuronWeight(RandomDataProvider.randomDoubleFromRange(-1, 1)));
            }

            neurons.add(new Neuron(inputs, neuronWeights));
        }

        return neurons;

    }

    @Required
    public void setNeuralNetworkDataProvider(NeuralNetworkDataProvider neuralNetworkDataProvider) {
        this.neuralNetworkDataProvider = neuralNetworkDataProvider;
    }

}
