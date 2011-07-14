package org.nnga.tsp.processor;

import org.nnga.tsp.persistence.entity.NeuralNetwork;

import java.util.List;

public interface NeuralNetworkProcessor<T> {
    List<T> process(int neuralNetworkId, List<T> data, boolean saveActivations) throws Exception;
    List<T> process(NeuralNetwork neuralNetwork, List<T> data, boolean saveActivations) throws Exception;
}
