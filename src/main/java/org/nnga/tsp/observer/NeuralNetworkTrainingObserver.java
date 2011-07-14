package org.nnga.tsp.observer;

import java.util.List;
import java.util.Map;
import java.util.Observable;

public interface NeuralNetworkTrainingObserver {
    void setObservable(Observable observable);
    Map<String, Object> getTrainingData(int neuralNetworkId);
    void clearTrainingData(int neuralNetworkId);
}
