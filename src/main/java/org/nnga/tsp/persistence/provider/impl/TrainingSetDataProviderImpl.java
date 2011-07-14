package org.nnga.tsp.persistence.provider.impl;

import org.nnga.tsp.persistence.entity.TrainingSet;
import org.nnga.tsp.persistence.provider.AbstractDataProvider;
import org.nnga.tsp.persistence.provider.TrainingSetDataProvider;

public class TrainingSetDataProviderImpl extends AbstractDataProvider<TrainingSet> implements TrainingSetDataProvider {

    @Override
    protected Class<TrainingSet> getEntityClass() {
        return TrainingSet.class;
    }

}
