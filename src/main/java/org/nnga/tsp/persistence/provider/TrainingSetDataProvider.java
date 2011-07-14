package org.nnga.tsp.persistence.provider;

import org.nnga.tsp.persistence.entity.TrainingSet;

import java.util.List;

public interface TrainingSetDataProvider {
    List<TrainingSet> getAll();
    TrainingSet getById(int id);
    void save(TrainingSet trainingSet) throws Exception;
    void delete(TrainingSet trainingSet) throws Exception;
}
