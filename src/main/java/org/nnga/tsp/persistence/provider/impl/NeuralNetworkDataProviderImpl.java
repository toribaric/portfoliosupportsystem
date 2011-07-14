package org.nnga.tsp.persistence.provider.impl;

import org.apache.log4j.Logger;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.provider.AbstractDataProvider;
import org.nnga.tsp.persistence.provider.NeuralNetworkDataProvider;

public class NeuralNetworkDataProviderImpl extends AbstractDataProvider<NeuralNetwork> implements NeuralNetworkDataProvider {

    private static final Logger LOGGER = Logger.getLogger(NeuralNetworkDataProviderImpl.class);

    @Override
    protected Class<NeuralNetwork> getEntityClass() {
        return NeuralNetwork.class;
    }

}
