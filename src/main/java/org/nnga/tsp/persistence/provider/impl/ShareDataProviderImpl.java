package org.nnga.tsp.persistence.provider.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.entity.Share;
import org.nnga.tsp.persistence.entity.SharePrice;
import org.nnga.tsp.persistence.provider.AbstractDataProvider;
import org.nnga.tsp.persistence.provider.ShareDataProvider;

import java.util.List;

public class ShareDataProviderImpl extends AbstractDataProvider<Share> implements ShareDataProvider {

    @Override
    protected Class<Share> getEntityClass() {
        return Share.class;
    }

    @Override
    public List<NeuralNetwork> getNeuralNetworks(int shareId) {
        Session session = getSession();
        String sqlQuery = "from " + NeuralNetwork.class.getName() + " where shareId = ?";
        Query query = session.createQuery(sqlQuery);
        query.setInteger(0, shareId);
        List<NeuralNetwork> neuralNetworks = listData(session, query);
        return neuralNetworks.size() > 0 ? neuralNetworks : null;
    }

    @Override
    public List<SharePrice> getSharePrices(int shareId) {
        Session session = getSession();
        String sqlQuery = "from " + SharePrice.class.getName() + " where shareId = ? order by date asc";
        Query query = session.createQuery(sqlQuery);
        query.setInteger(0, shareId);
        List<SharePrice> sharePrices = listData(session, query);
        return sharePrices.size() > 0 ? sharePrices : null;
    }

}
