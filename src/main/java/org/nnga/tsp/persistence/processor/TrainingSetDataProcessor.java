package org.nnga.tsp.persistence.processor;

import org.nnga.tsp.persistence.entity.TrainingSet;
import java.util.List;
import java.util.Map;

public interface TrainingSetDataProcessor {
    List<TrainingSet> getTrainingSets();
    void createNewTrainingSet(String json) throws Exception;
    void deleteTrainingSet(String json) throws Exception;
    void createTrainingSetFromUpload(String uploadSetName, int numInputs, int numOutputs, String fieldsDelimiter, String rowsDelimiter, String fileData) throws Exception;
    List<Map<String, Object>> getTrainingSetIOs(int trainingSetId);
    void updateTrainingSetIOs(String json) throws Exception;
    String processAndReturnTimesSeries(String fromData, String dataDelimiter, String fileData) throws Exception;
}
