package org.nnga.tsp.persistence.provider;

import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.entity.Neuron;
import org.nnga.tsp.persistence.entity.NeuronLayer;
import org.nnga.tsp.persistence.entity.NeuronWeight;

import java.util.List;

public interface NeuralNetworkDataProvider {
    List<NeuralNetwork> getAll();
    NeuralNetwork getById(int id);
    void save(NeuralNetwork neuralNetwork) throws Exception;
    void delete(NeuralNetwork neuralNetwork) throws Exception;
}
