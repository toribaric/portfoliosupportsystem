package org.nnga.tsp.persistence.processor.impl;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.nnga.tsp.persistence.entity.TrainingSet;
import org.nnga.tsp.persistence.entity.TrainingSetIO;
import org.nnga.tsp.persistence.processor.TrainingSetDataProcessor;
import org.nnga.tsp.persistence.provider.TrainingSetDataProvider;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TrainingSetDataProcessorImpl implements TrainingSetDataProcessor {

    private static final Logger LOGGER = Logger.getLogger(TrainingSetDataProcessorImpl.class);

    private TrainingSetDataProvider trainingSetDataProvider;

    @Override
    public List<TrainingSet> getTrainingSets() {
        return trainingSetDataProvider.getAll();
    }

    @Override
    public void createNewTrainingSet(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        TrainingSet trainingSet = objectMapper.readValue(json, new TypeReference<TrainingSet>(){});
        trainingSetDataProvider.save(trainingSet);
    }

    @Override
    public void deleteTrainingSet(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(json);
        int id = jsonNode.get("id").getIntValue();

        TrainingSet trainingSet = trainingSetDataProvider.getById(id);

        if( trainingSet == null ) {
            throw new IllegalArgumentException("TrainingSet with id \"" + id + "\" was not found");
        }

        trainingSetDataProvider.delete(trainingSet);
    }

    @Override
    public void createTrainingSetFromUpload(String uploadSetName, int numInputs, int numOutputs, String fieldsDelimiter, String rowsDelimiter, String fileData) throws Exception {

        // create new entities
        TrainingSet trainingSet = new TrainingSet();
        List<TrainingSetIO> trainingSetIOs = new ArrayList<TrainingSetIO>();

        /*
         * TODO: implement file format validation logic
         */
        // compose TrainingSetIo entity data from uploaded file
        String[] rows = fileData.split(rowsDelimiter);
        for( String row : rows ) {

            String[] fields = row.split(fieldsDelimiter);
            int totalFields = numInputs + numOutputs;

            if( totalFields != fields.length ) {
                throw new IOException("Invalid file format: number of fields in each row must be equal to entered amount of inputs and outputs.");
            }

            String entityInputs = "", entityOutputs = "";
            for( int i = 0; i < numInputs; i++ ) {
                entityInputs += fields[i] + " ";
            }
            for( int i = numInputs; i < totalFields; i++ ) {
                entityOutputs += fields[i] + " ";
            }

            TrainingSetIO trainingSetIO = new TrainingSetIO();
            trainingSetIO.setInputs(entityInputs.trim());
            trainingSetIO.setOutputs(entityOutputs.trim());

            trainingSetIOs.add(trainingSetIO);

        }

        trainingSet.setName(uploadSetName);
        trainingSet.setNumInputs(numInputs);
        trainingSet.setNumOutputs(numOutputs);
        trainingSet.setTrainingSetIOs(trainingSetIOs);

        trainingSetDataProvider.save(trainingSet);

    }

    @Override
    public List<Map<String, Object>> getTrainingSetIOs(int trainingSetId) {

        TrainingSet trainingSet = trainingSetDataProvider.getById(trainingSetId);

        if( trainingSet == null ) {
            throw new IllegalArgumentException("TrainingSet with id \"" + trainingSetId + "\" was not found");
        }

        List<TrainingSetIO> trainingSetIOEntities = trainingSet.getTrainingSetIOs();

        // assemble list of maps - or "models" - which will be sent to extJS to create
        // store, model and grid panel of trainingSetIos - or training set elements; every
        // map has a number of input and output fields with values from TrainingSetIO jpa
        // model contained in one string - in fields "inputs" and "outputs"
        List<Map<String, Object>> trainingSetIOs = new ArrayList<Map<String, Object>>();
        for( TrainingSetIO trainingSetIO : trainingSetIOEntities ) {
            Map<String, Object> trainingSetIoMap = new HashMap<String, Object>();
            trainingSetIoMap.put("trainingSetId", trainingSetId);

            String[] inputs = trainingSetIO.getInputs().split("\\s");
            String[] outputs = trainingSetIO.getOutputs().split("\\s");

            for( int i = 0; i < inputs.length; i++ ) {
                trainingSetIoMap.put("input" + (i + 1), inputs[i]);
            }
            for( int i = 0; i < outputs.length; i++ ) {
                trainingSetIoMap.put("output" + (i + 1), outputs[i]);
            }

            trainingSetIOs.add(trainingSetIoMap);
        }

        return trainingSetIOs;
    }

    @Override
    public void updateTrainingSetIOs(String json) throws Exception {

        // we deserialize trainingSetIo fields - a nubmer of input and output fields - to a list of maps
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> trainingSetIOs = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>(){});

        // id of TrainingSet to which these IOs belong - al IO records have it same, so we can take it from the first one
        int trainingSetId = Integer.parseInt(trainingSetIOs.get(0).get("trainingSetId").toString());

        TrainingSet trainingSet = trainingSetDataProvider.getById(trainingSetId);

        if( trainingSet == null ) {
            throw new IllegalArgumentException("TrainingSet with id \"" + trainingSetId + "\" was not found");
        }

        trainingSet.getTrainingSetIOs().clear();

        /*
         * TODO: check for valid input and output data (doubles, integers)
         */
        // here we're doing the opposite operation compared to previous method; input and output values from client stored
        // in a number of input and outputs fields must be join in two strings - "inputs" and "outputs" (separated by space)
        // so they can be added to TrainingSetIO model which has "input" and "output" fields
        String inputs = "", outputs = "";
        for( Map<String, Object> trainingSetIO : trainingSetIOs ) {

            for (Map.Entry<String, Object> field : trainingSetIO.entrySet()) {
                if( field.getKey().contains("input") ) {
                    inputs += field.getValue() + " ";
                }
                else if( field.getKey().contains("output") ) {
                    outputs += field.getValue() + " ";
                }
            }

            TrainingSetIO trainingSetIOEntity = new TrainingSetIO();
            trainingSetIOEntity.setInputs(inputs.trim());
            trainingSetIOEntity.setOutputs(outputs.trim());

            trainingSet.addTrainingSetIO(trainingSetIOEntity);

            inputs = outputs = "";
        }

        trainingSetDataProvider.save(trainingSet);

    }

    @Override
    public String processAndReturnTimesSeries(String fromDate, String dataDelimiter, String fileData) throws Exception {

        if( !fileData.contains(dataDelimiter) ) {
            throw new IllegalArgumentException("Invalid data delimiter.");
        }

        String[] data = fileData.split(dataDelimiter);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Calendar current = Calendar.getInstance();
        current.setTime(dateFormat.parse(fromDate));

        List<Date> dates = new ArrayList<Date>();
        for( String value : data ) {
            dates.add(current.getTime());
            current.add(Calendar.DATE, 1);
        }

        // assemble time series data JSON to send to extJS chart
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for( int i = 0; i < data.length; i++ ) {
            String currentDate = dateFormat.format(dates.get(i));
            stringBuilder.append("{");
            stringBuilder.append("date: \"").append(currentDate).append("\",");
            stringBuilder.append("data: ").append(data[i]);
            stringBuilder.append("}");
            if( i < data.length - 1 ) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    @Override
    public void generateTrainingSetFromTimeSeries(int trainingSetId, String[] timeSeries, int numRecords, int frequency) throws Exception {

        TrainingSet trainingSet = trainingSetDataProvider.getById(trainingSetId);
        if( trainingSet == null ) {
            throw new IllegalArgumentException("TrainingSet with id \"" + trainingSetId + "\" was not found");
        }
        trainingSet.getTrainingSetIOs().clear();

        // number of inputs and outputs; length of training set record
        int numInputsOutputs = trainingSet.getNumInputs() + trainingSet.getNumOutputs();

        // check if time series is long enough to fit into one training set record at selected training set's record length (inputs + outputs),
        // at selected sampling frequency; ts record length (numInputsOutputs) is diminished by 1 in this formula because first field value
        // is the first value from time series, so we don't have to skip "frequency" places to get to it - because of that we must take out
        // one "frequency slice", and that's exactly what that -1 is doing here
        if( frequency * (numInputsOutputs - 1) >= timeSeries.length ) {
            throw new IllegalStateException("Time series is too small for filling all inputs/outputs of selected training set at current sampling frequency. Try entering lower sampling frequency.");
        }

        // iterate through time series data and generate "numRecords" training set records
        int start = 0;
        for( int i = 0; i < numRecords; i++ ) {

            String inputs = "", outputs = "";

            // this loop forms new record from time series data, from "start" to "end" points of time series data,
            // with selected sampling frequency
            int end = start + (numInputsOutputs * frequency) - (frequency - 1); // frequency - 1; because first field is at start of current sampling part, which means we have one frequency slice less
            for( int j = start, inputsCount = 0; j < end; j += frequency, inputsCount++ ) {
                if( inputsCount < trainingSet.getNumInputs() ) {
                    inputs += timeSeries[j] + " ";
                }
                else {
                    outputs += timeSeries[j] + " ";
                }
            }

            TrainingSetIO trainingSetIO = new TrainingSetIO();
            trainingSetIO.setInputs(inputs.trim());
            trainingSetIO.setOutputs(outputs.trim());
            trainingSet.addTrainingSetIO(trainingSetIO);

            inputs = outputs = "";

            // when we come to the end of time series, we go back to the start of it until "numRecords" is achieved
            if( end >= timeSeries.length ) {
                start = 0;
            }
            else {
                start++;
            }

        }

        // persist newly created records
        trainingSetDataProvider.save(trainingSet);

    }

    @Required
    public void setTrainingSetDataProvider(TrainingSetDataProvider trainingSetDataProvider) {
        this.trainingSetDataProvider = trainingSetDataProvider;
    }
}
