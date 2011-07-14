package org.nnga.tsp.persistence.processor;

import org.nnga.tsp.persistence.entity.NeuralNetwork;

import java.util.List;

public interface NeuralNetworkDataProcessor {
    List<NeuralNetwork> getAll();
    void updateConfigurationData(int id, String activationFunction, Boolean trained) throws Exception;
    void randomizeWeights(int id) throws Exception;
    void processNewStructuralData(String name, String description, String structure, String activationFunction) throws Exception;
    void processUpdatedStructuralData(int id, String structure) throws Exception;
    void deleteNetwork(String json) throws Exception;
}
