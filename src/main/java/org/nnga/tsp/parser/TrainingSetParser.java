package org.nnga.tsp.parser;

import org.nnga.tsp.persistence.entity.TrainingSet;

import java.util.List;
import java.util.Map;

public interface TrainingSetParser {
    Map<String, List<List<Double>>> getTrainingData(TrainingSet trainingSet);
}
