package org.nnga.tsp.persistence.provider;

import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.entity.Share;
import org.nnga.tsp.persistence.entity.SharePrice;

import java.util.List;

public interface ShareDataProvider {
    List<Share> getAll();
    Share getById(int id);
    List<NeuralNetwork> getNeuralNetworks( int shareId );
    List<SharePrice> getSharePrices(int shareId);
}
