package org.nnga.tsp.parser.impl;

import org.nnga.tsp.parser.TrainingSetParser;
import org.nnga.tsp.persistence.entity.TrainingSet;
import org.nnga.tsp.persistence.entity.TrainingSetIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingSetParserImpl implements TrainingSetParser {

    @Override
    public Map<String, List<List<Double>>> getTrainingData(TrainingSet trainingSet) {
        List<TrainingSetIO> trainingSetIOs = trainingSet.getTrainingSetIOs();

        List<List<Double>> inputsList = new ArrayList<List<Double>>();
        List<List<Double>> outputsList = new ArrayList<List<Double>>();

        for( TrainingSetIO trainingSetIO : trainingSetIOs ) {
            List<Double> inputs = new ArrayList<Double>();
            List<Double> outputs = new ArrayList<Double>();

            String[] inputsRecord = trainingSetIO.getInputs().split("\\s");
            String[] outputsRecord = trainingSetIO.getOutputs().split("\\s");

            for( int i = 0; i < inputsRecord.length; i++ ) {
                inputs.add(Double.parseDouble(inputsRecord[i]));
            }

            for( int i = 0; i < outputsRecord.length; i++ ) {
                outputs.add(Double.parseDouble(outputsRecord[i]));
            }

            inputsList.add(inputs);
            outputsList.add(outputs);
        }

        Map<String, List<List<Double>>> trainingData = new HashMap<String, List<List<Double>>>();
        trainingData.put("inputs", inputsList);
        trainingData.put("outputs", outputsList);

        return trainingData;
    }

}
